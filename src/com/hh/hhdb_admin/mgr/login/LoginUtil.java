package com.hh.hhdb_admin.mgr.login;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.base.OraJdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.create_dbobj.treeMr.TreeMrUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.hhdb_admin.common.util.StartUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LoginUtil {

    public static JsonObject currConnInfo;
    public static SshLoginBen sshLoginBen;

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

    static JdbcBean json2JdbcBean(JsonObject json) {
        String dbType = json.getString("db_type");
        boolean isOracle = dbType.equals(DBTypeEnum.oracle.name());
        JdbcBean jdbcBean = isOracle ? new OraJdbcBean() : new JdbcBean();
        jdbcBean.setClazz(json.getString("db_clazz"));
        jdbcBean.setDbUrl(json.getString("db_url"));
        jdbcBean.setSchema(json.getString("db_schema"));
        jdbcBean.setUser(json.getString("db_user"));
        jdbcBean.setPassword(json.getString("db_pass"));
        String isAdmin = json.getString("is_admin");
        setSchema(dbType, jdbcBean, isAdmin);
        return jdbcBean;
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
            // TODO Auto-generated catch block
        }
    }

    /**
     * 利用按位异或进行加密和解密
     *
     * @param str str
     * @return java.lang.String
     **/
    protected static String encryptLoginData(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    protected static String decryptLoginData(String str) {
        return new String(Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        String data = encryptLoginData("{test:近近景近景}");
        System.out.println(decryptLoginData(data));
    }

    /**
     * @return 读取连接信息文件
     */
    protected static JsonObject readConnFile() {
        try {
            File jsonFile = new File(StartUtil.workspace, "login.dat");
            if (!jsonFile.exists()) {
                jsonFile.createNewFile();
                FileUtils.writeStringToFile(jsonFile, LoginUtil.encryptLoginData("{}"), StandardCharsets.UTF_8);
                // todo 因登录加解密方式修改，删除原有登录信息文件
                try {
                    FileUtils.forceDelete(new File(StartUtil.workspace, "login_data.dat"));
                } catch (Exception ignored) {

                }
            }
            return Json.parse(LoginUtil.decryptLoginData(FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8)))
                    .asObject();
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonObject();
        }
    }

    /**
     * 覆写连接信息文件
     *
     * @param loginData loginData
     */
    protected static void saveConnFile(JsonObject loginData) {
        File jsonFile = new File(StartUtil.workspace, "login.dat");
        try {
            FileUtils.writeStringToFile(jsonFile, LoginUtil.encryptLoginData(loginData.toPrettyString()),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

}
