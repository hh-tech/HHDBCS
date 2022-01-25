package com.hh.hhdb_admin.mgr.login;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.base.OraJdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.JdbcUrlIpUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.common.util.db.SshSessionTool;
import com.hh.frame.create_dbobj.treeMr.TreeMrUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.connMgr.base.ConnMgrUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.base.LoginConnMsg;
import com.hh.hhdb_admin.mgr.login.comp.CommonComp;
import com.hh.hhdb_admin.mgr.login.comp.LoginTabComp;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LoginUtil {

    public static JsonObject currConnInfo = new JsonObject();

    public static String PATH;

    public static final Pattern pattern = Pattern.compile("[0-9]*");

    // 以前的login.dat转换为etc/login/login.dat
    public static void transformData() {
        try {
            File loginFile = new File(StartUtil.getEtcFile(), "login");
            File file = new File(StartUtil.getEtcFile(), ConnMgrUtil.FILE_NAME);
            File workSpaceFile = new File(StartUtil.workspace, ConnMgrUtil.FILE_NAME);
            handleData(loginFile, file);
            handleData(loginFile, workSpaceFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleData(File ligDir, File file) throws Exception {
        File newFile = new File(ligDir, ConnMgrUtil.FILE_NAME);
        JsonObject json = ConnMgrUtil.file2json(newFile);
        if (file.exists()) {
            JsonObject datas = Json.parse(ConnMgrUtil.decryptData(FileUtils.readFileToString(file, StandardCharsets.UTF_8))).asObject();
            for (String name : datas.names()) {
                JsonObject data = datas.get(name).asObject();
                LoginConnMsg connMsg = new LoginConnMsg();
                connMsg.setConnName(data.getString("conn_name"));
                connMsg.setView(StringUtils.isEmpty(data.getString("db_view")) ? "USER" : data.getString("db_view"));
                connMsg.setType(data.getString("db_type"));
                connMsg.setConnect(data.getString("db_url"));
                connMsg.setUsername(data.getString("db_user"));
                connMsg.setPassword(data.getString("db_pass"));
                connMsg.setEncrypted(false);
                connMsg.setSchema(data.getString("db_schema"));
                connMsg.setUserType(Boolean.parseBoolean(datas.getString("is_admin")));
                connMsg.setEnabled(false);
                if (data.names().contains("ssh")) {
                    JsonObject sshJson = data.get("ssh").asObject();
                    connMsg.setEnabled(true);
                    connMsg.setHost(isNull("ssh_host", sshJson));
                    connMsg.setPort(sshJson.getInt("ssh_port") + "");
                    connMsg.setSshType(StringUtils.isEmpty(isNull("ssh_type", sshJson)) ? "PASSWORD" : isNull("ssh_type", sshJson));
                    connMsg.setSshUsername(isNull("ssh_user", sshJson));
                    connMsg.setSshEncrypted(false);
                    connMsg.setSshPassword(isNull("ssh_password", sshJson));
                    connMsg.setPrivateKey(isNull("ssh_private_key", sshJson));
                    connMsg.setPrivatePassword(isNull("ssh_private_key_pwd", sshJson));
                }
                if (!json.names().contains(connMsg.getConnName())) {
                    json.add(connMsg.getConnName(), connMsg.toJson());
                }
            }
            ConnMgrUtil.writeStringToFile(newFile, json.toPrettyString());
        }
    }

    private static String isNull(String key, JsonObject data) {
        try {
            return StringUtils.isEmpty(data.getString(key)) ? "" : data.getString(key);
        } catch (Exception e) {
            return "";
        }
    }


    public static String getRealName(String userName, String dbType) {
        boolean isWithQuota = userName.startsWith("\"") && userName.endsWith("\"");
        String realUserName = isWithQuota ? userName.substring(1, userName.length() - 1) : userName;
        if (isWithQuota) {
            return realUserName;
        }
        switch (DBTypeEnum.valueOf(dbType)) {
            case hhdb:
            case pgsql:
            case mysql:
            case sqlserver:
                return realUserName.toLowerCase();
            case oracle:
            case db2:
                return realUserName.toUpperCase();
            default:
                return realUserName;
        }
    }

    public static JdbcBean json2JdbcBean(LoginConnMsg connMsg) {
        String dbType = connMsg.getType();
        boolean isOracle = dbType.equals(DBTypeEnum.oracle.name());
        JdbcBean jdbcBean = isOracle ? new OraJdbcBean() : new JdbcBean();
        jdbcBean.setClazz(DriverUtil.getDriverClass(dbType));
        jdbcBean.setDbUrl(connMsg.getConnect());
        jdbcBean.setSchema(connMsg.getSchema());
        jdbcBean.setUser(connMsg.getUsername());
        jdbcBean.setPassword(connMsg.isEncrypted() ? connMsg.getDecryptPassword() : connMsg.getPassword());
        String isAdmin = String.valueOf(connMsg.isUserType());
        setSchema(dbType, jdbcBean, isAdmin);
        return jdbcBean;
    }

    public static JdbcBean buildJdbcBean(JdbcBean jdbcBean, LoginConnMsg connMsg, SshSessionTool sessionTool) throws Exception {
        if (connMsg.isEnabled()) {
            String dbUrl = jdbcBean.getDbUrl();
            sessionTool.setRemoteDbHost(JdbcUrlIpUtil.getHostFromUrl(dbUrl));
            sessionTool.setRemoteDbPort(JdbcUrlIpUtil.getPortFromUrl(dbUrl));
            boolean isPassword = LoginTabComp.SshTypeEnum.PASSWORD.name().equals(connMsg.getSshType());
            try {
                String host = connMsg.getHost();
                String portStr = connMsg.getPort();
                if (!pattern.matcher(String.valueOf(portStr)).matches() || Integer.parseInt(portStr) > 65535) {
                    throw new NumberFormatException(portStr + " " + CommonComp.getLang("portIncorrect"));
                }
                int port = Integer.parseInt(portStr);
                if (isPassword) {
                    String password = connMsg.isSshEncrypted() ? connMsg.getDecryptSshPassword() : connMsg.getSshPassword();
                    sessionTool.connect(host, port, connMsg.getSshUsername(), password);
                } else {
                    File keyFile = new File(connMsg.getPrivateKey());
                    if (!keyFile.exists()) {
                        throw new FileNotFoundException(connMsg.getPrivateKey() + " PrivateKey File Not Found");
                    }
                    String password = connMsg.isSshEncrypted() ? connMsg.getDecryptPrivatePassword() : connMsg.getPrivatePassword();
                    sessionTool.connect(host, port, connMsg.getSshUsername(), keyFile, password);
                }
            } catch (Exception e) {
                LoginComp.loginBean.setSshAuth(false);
                throw e;
            }
            jdbcBean = sessionTool.getSshJdbcBean(jdbcBean);
            LoginComp.loginBean.setSshAuth(true);
        }
        return jdbcBean;
    }

    public static void switchSchema(JdbcBean jdbcBean, String newSchema) {
        DriverUtil.getDbTypeOp(jdbcBean).ifPresent(type -> {
            if (type == DBTypeEnum.mysql) {
                String url = jdbcBean.getDbUrl();
                int lastSprit = url.indexOf("/");
                String newUrl;
                if (lastSprit > 0 && url.charAt(lastSprit - 1) == '/') {
                    newUrl = url.replace("?", "/" + newSchema + "?");
                } else {
                    newUrl = url.replaceAll("(?<=.\\d/).*\\?", newSchema + "?");
                }
                jdbcBean.setDbUrl(newUrl);
                String name = newSchema;
                if (!name.startsWith("`") && !name.endsWith("`")) {
                    name = String.format("%s%s%s", "`", name, "`");
                }
                jdbcBean.setCurSessionSchema(name);
            }
            jdbcBean.setSchema(newSchema);
        });
    }

    public static void setLockTimeOut(Connection conn) {
        try {
            DBTypeEnum dbTypeEnum = DriverUtil.getDbType(conn);
            switch (dbTypeEnum) {
                case hhdb:
                case pgsql:
                    SqlExeUtil.executeUpdate(conn, "set statement_timeout=0");
                    SqlExeUtil.executeUpdate(conn, "set lock_timeout=5000");
                    break;
                case sqlserver:
                    SqlExeUtil.executeUpdate(conn, "set lock_timeout 5000");
                    break;
                default:
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否有DBA权限
     *
     * @param userName username
     * @return 是否是DBA
     */
    public static boolean isDba(Connection conn, String userName) throws SQLException {
        DBTypeEnum dbType = DriverUtil.getDbType(conn);
        switch (dbType) {
            case oracle:
                String sql = "select role from session_roles";
                List<Map<String, String>> res = SqlQueryUtil.selectStrMapList(conn, sql);
                if (res.size() == 0) {
                    try {
                        res = SqlQueryUtil.selectStrMapList(conn, "select role from dba_roles");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return res.stream().map(item -> item.get("role").toUpperCase()).collect(Collectors.toList())
                        .contains("DBA");
            case hhdb:
            case pgsql:
                HHdbPgsqlPrefixEnum prefix = dbType == DBTypeEnum.hhdb ? HHdbPgsqlPrefixEnum.hh : HHdbPgsqlPrefixEnum.pg;
                sql = "select usesuper as dba from " + prefix + "_user where usename = '%s'";
                Map<String, String> hhRes = SqlQueryUtil.selectOneStrMap(conn, String.format(sql, userName));
                return StringUtils.isNotBlank(hhRes.get("dba")) && "t".equals(hhRes.get("dba"));
            default:
                return true;
        }
    }

    private static void setSchema(String dbType, JdbcBean jdbcBean, String isAdmin) {
        switch (DBTypeEnum.valueOf(dbType)) {
            case hhdb:
            case pgsql:
                if (StringUtils.isEmpty(jdbcBean.getSchema())) {
                    jdbcBean.setSchema("public");
                }
                break;
            case oracle:
                ((OraJdbcBean) jdbcBean).setSys(Boolean.parseBoolean(isAdmin));
                if (StringUtils.isEmpty(jdbcBean.getSchema())) {
                    jdbcBean.setSchema(jdbcBean.getUser());
                }
                break;
            case mysql:
                dealMysqlConn(jdbcBean);
                break;
            case db2:
                if (StringUtils.isEmpty(jdbcBean.getSchema())) {
                    jdbcBean.setSchema("");
                }
                break;
            case sqlserver:
                if (StringUtils.isEmpty(jdbcBean.getSchema())) {
                    jdbcBean.setSchema(TreeMrUtil.getSqlServerDbName(jdbcBean));
                }
                break;
            case dm:
                if (StringUtils.isEmpty(jdbcBean.getSchema())) {
                    jdbcBean.setSchema(jdbcBean.getUser().toUpperCase());
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + dbType);
        }
    }

    private static void dealMysqlConn(JdbcBean jdbc) {
        String code = "`";
        String dbUrl = jdbc.getDbUrl();
        String schema = jdbc.getSchema();
        String urlSchema = getSchema(jdbc);
        if (StringUtils.isEmpty(urlSchema) && StringUtils.isEmpty(schema)) {
            jdbc.setSchema("mysql");
        } else {
            //url数据库不为空 且 用户没填
            if (StringUtils.isNotEmpty(urlSchema) && StringUtils.isEmpty(schema)) {
                boolean isSchemaWithQuart = urlSchema.startsWith(code) && urlSchema.endsWith(code);
                jdbc.setSchema(isSchemaWithQuart ? urlSchema.replaceAll("`", "") : urlSchema);
                jdbc.setCurSessionSchema(isSchemaWithQuart ? urlSchema : "`" + urlSchema + "`");
            }
            //用户填了数据
            if (StringUtils.isNotEmpty(schema)) {
                boolean isSchemaWithQuart = schema.startsWith(code) && schema.endsWith(code);
                jdbc.setSchema(isSchemaWithQuart ? schema.replaceAll("`", "") : schema);
                jdbc.setCurSessionSchema(isSchemaWithQuart ? schema : "`" + schema + "`");
            }
        }
        jdbc.setDbUrl(dbUrl);
    }

    private static String getSchema(JdbcBean jdbcBean) {
        String str = jdbcBean.getDbUrl().split("//")[1];
        String[] fullDbNames = str.split(":")[1].split("/");
        String urlSchema = "";
        if (fullDbNames.length > 1) {
            String fullDbName = fullDbNames[1];
            int index = fullDbName.indexOf("?");
            if (index == -1) {
                if (str.split(":")[1].indexOf("/") < str.split(":")[1].indexOf("?")) {
                    urlSchema = fullDbName;
                }
            } else {
                urlSchema = fullDbName.substring(0, index);
            }
        }
        return urlSchema;
    }

}
