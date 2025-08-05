package com.hh.hhdb_admin.mgr.usr2.form;

import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.UserSqlUtil;
import com.hh.frame.db_sql.sqlserver.SqlServerDatabaseUtil;
import com.hh.frame.db_sql.sqlserver.SqlserverUserSqlUtil;
import com.hh.frame.db_sql.sqlserver.info.SqlServerUserBean;
import com.hh.frame.db_sql.sqlserver.info.SqlServerUserInfoBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.mgr.usr2.UsrBean;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SqlServerUsrForm extends UsrBaseForm {
    private CheckBoxInput loginSet;
    private SelectBox defaultDBBox;
    private SelectBox verifyBox;

    
    public SqlServerUsrForm(UsrBean usrBean, Connection conn) throws Exception {
        super(usrBean,conn);
        if (usrBean.isUser()) {
            defaultDBBox = new SelectBox();
            defaultDBBox.addOption("","");
            verifyBox = new SelectBox();
            verifyBox.addOption(LangMgr.sLang(CsL.SQLSERVER_AUTH), "sqlserver");
            verifyBox.addOption(LangMgr.sLang(CsL.WINDOWS_AUTH), "windows");
            verifyBox.addListener(e ->  pwInput.setEnabled(!verifyBox.getValue().equals("windows")));
            loginSet = new CheckBoxInput("login");
            loginSet.setText(LangMgr.lang(Word4.LOGIN_FORBIDDEN));
    
            //获取默认数据库
            List<String> list = SqlQueryUtil.selectOneColumn(conn, SqlServerDatabaseUtil.getDbNamesSql(usrBean.getVersion()));
            for (String str : list) {
                defaultDBBox.addOption(str, str);
            }
    
            //显示所有可选角色
            roleGranteComp.refreshTable(getUsrRoles());
        } else if (usrBean.getOprType() == UsrBean.OprType.PERM) {
            nameInput.setValue(usrBean.getName());
            roleGranteComp.refreshTable(getUsrPrivs());
        }
    }
    
    @Override
    public LastPanel getLastPanel() {
        LastPanel lastPanel = new LastPanel();
        if (usrBean.isUser()) {
            HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
            layout.setRightWidth(100);
            HPanel panel = new HPanel(layout);
            panel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
            panel.add(new HeightComp(5));
            panel.add(getLabelInput(LangMgr.lang(Word2.AUTHENTICATION)+" ：", verifyBox));
            panel.add(getLabelInput(LangMgr.sLang(CsL.DEFAULT_DB)+" ：", defaultDBBox));
            HPanel loginSetPanel = new HPanel(getLayout());
            loginSetPanel.add(new HeightComp(10));
            loginSetPanel.add(loginSet);
            panel.add(loginSetPanel);
            panel.add(new HeightComp(5));
    
            lastPanel.setHead(panel.getComp());
        }
        LastPanel areaLast = new LastPanel();
        areaLast.set(area.getComp());
        hTabPanel = new HTabPanel();
        hTabPanel.setPanel(LangMgr.lang(Word2.CODE),areaLast, newHeaderConfig(LangMgr.lang(Word2.CODE)));
        hTabPanel.setPanel(LangMgr.lang(Word2.AUTHORIZATION),roleGranteComp, newHeaderConfig(LangMgr.lang(usrBean.getOprType() == UsrBean.OprType.PERM ? Word2.PRIVILEGE : Word2.ROLE)));
        hTabPanel.selectPanel(LangMgr.lang(Word2.AUTHORIZATION));
        
        lastPanel.set(hTabPanel.getComp());
        return lastPanel;
    }
    
    @Override
    public void upInitForm(String usrName) throws SQLException {
        nameInput.setValue(usrName);
        nameInput.setEnabled(false);
        
        String sql = SqlserverUserSqlUtil.getSelUserInfoSql(usrBean.getVersion(),new SqlServerUserInfoBean(usrName, SqlServerUserInfoBean.SqlServerUserInfo.class));
        Map<SqlServerUserInfoBean.SqlServerUserInfo, String> data = SqlQueryUtil.selectOneStrMap( conn, sql, SqlServerUserInfoBean.SqlServerUserInfo.class);
        defaultDBBox.setValue(data.get(SqlServerUserInfoBean.SqlServerUserInfo.DBNAME));
        if (data.get(SqlServerUserInfoBean.SqlServerUserInfo.DISABLED) != null) {
            loginSet.setValue(Boolean.valueOf(data.get(SqlServerUserInfoBean.SqlServerUserInfo.DISABLED).equals("1")).toString());
        }
        String verify = data.get(SqlServerUserInfoBean.SqlServerUserInfo.ISNTNAME).equals("1") ? "windows" : "sqlserver";
        verifyBox.setValue(verify);
        verifyBox.setEnabled(false);
        pwInput.setEnabled(!verifyBox.getValue().equals("windows"));
    
        roleGranteComp.refreshTable(getUsrRoles());
    
        oldUser = getUserBean();
    }

    @Override
    public String getSql() {
        StringBuffer buffer = new StringBuffer();
        Map<Boolean, List<String>> map = roleGranteComp.getNameMap();
        String sb = "",grant = "",revok = "";
        if (usrBean.isUser()) {
            if (nameInput.isEnabled()) {
                sb = UserSqlUtil.getCreateSql(usrBean.getDbType(), usrBean.getVersion(), getUserBean());
            } else {
                sb = UserSqlUtil.getUpdateSql(usrBean.getDbType(), usrBean.getVersion(), oldUser, getUserBean());
            }
            buffer.append(StringUtils.isNotBlank(sb) ? sb + "\n" : "");
            grant = UserSqlUtil.getGrantRolesSql(usrBean.getDbType(), usrBean.getVersion(), map.get(true), nameInput.getValue());
            revok = UserSqlUtil.getRevokRolesSql(usrBean.getDbType(), usrBean.getVersion(), map.get(false), nameInput.getValue());
        } else if (usrBean.getOprType() == UsrBean.OprType.PERM) {
            grant = SqlserverUserSqlUtil.getGrantPrivSql( usrBean.getVersion(), map.get(true), nameInput.getValue());
            revok = SqlserverUserSqlUtil.getRevokPrivSql( usrBean.getVersion(), map.get(false), nameInput.getValue());
        }
        
        if (StringUtils.isNotBlank(grant)) buffer.append(grant + ";\n");
        if (StringUtils.isNotBlank(revok)) buffer.append(revok);
        return StringUtils.isNotBlank(buffer) ? buffer.toString() : "";
    }
    
    @Override
    public String checkForm() {
        if (StringUtils.isBlank(nameInput.getValue())) {
            return LangMgr.sLang(CsL.PLEASE_FILL_NAME);
        }
        if (nameInput.isEnabled() && StringUtils.isBlank(pwInput.getValue()) && !verifyBox.getValue().equals("windows")) {
            return LangMgr.sLang(CsL.PLEASE_ENTER_PASSWORD);
        }
        return null;
    }
    
    @Override
    public void inform() {
        hTabPanel.selectPanel(LangMgr.lang(Word2.CODE));
    }
    
    /**
     * 获取用户角色集合
     * @return
     * @throws SQLException
     */
    private List<Map<String, String>> getUsrRoles() throws SQLException {
        List<String> userRoles = SqlQueryUtil.selectOneColumn(conn, SqlserverUserSqlUtil.getUseRoleNamesSql(nameInput.getValue()));
        List<String> allRoles = SqlQueryUtil.selectOneColumn(conn, SqlserverUserSqlUtil.getRoleNamesSql());
        List<Map<String,String>> roleMaps = new ArrayList<>();
        for(String role:allRoles){
            if("public".equalsIgnoreCase(role)) continue;
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("name",role);
            hashMap.put("grant", String.valueOf(userRoles.contains(role)));
            roleMaps.add(hashMap);
        }
        return roleMaps;
    }
    
    /**
     * 获取用户权限集合
     * @return
     * @throws SQLException
     */
    private List<Map<String, String>> getUsrPrivs() throws SQLException {
        List<Map<String, String>> mapList = new ArrayList<>();
        List<String> myPerms = SqlQueryUtil.selectOneColumn(conn, SqlserverUserSqlUtil.getUsePrivsNamesSql(nameInput.getValue()));
        List<String> allPerms = SqlQueryUtil.selectOneColumn(conn, SqlserverUserSqlUtil.getPrivsNamesSql());
        for (String perm : allPerms) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", perm);
            hashMap.put("grant", myPerms.contains(perm) ? "true" : "false");
            mapList.add(hashMap);
        }
        return mapList;
    }
    
    private SqlServerUserBean getUserBean() {
        SqlServerUserBean uBean = new SqlServerUserBean(nameInput.getValue(),pwInput.getValue());
        uBean.setDefault_database(defaultDBBox.getValue());
        uBean.setAuthentication_type(verifyBox.getValue());
        uBean.setAllow_login(loginSet.isChecked());
        return uBean;
    }
}
