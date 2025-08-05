package com.hh.hhdb_admin.mgr.usr2.form;

import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.UserSqlUtil;
import com.hh.frame.db_sql.hana.HanaInstanceUtil;
import com.hh.frame.db_sql.hana.HanaRoleUserSqlUtil;
import com.hh.frame.db_sql.info.SqlUserBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.mgr.usr2.UsrBean;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HanaUsrForm extends UsrBaseForm {
    protected HTabPanel hTabPanel;
    
    public HanaUsrForm(UsrBean usrBean, Connection conn) throws Exception {
        super(usrBean, conn);
        if (usrBean.getOprType() != UsrBean.OprType.PERM) {
            //显示所有可选角色
            roleGranteComp.refreshTable(getUsrRoles());
        } else {
            nameInput.setValue(usrBean.getName());
            roleGranteComp.refreshTable(getUsrPrivs());
        }
    }
    
    @Override
    public LastPanel getLastPanel() {
        LastPanel lastPanel = new LastPanel();
        LastPanel areaLast = new LastPanel();
        areaLast.set(area.getComp());
        hTabPanel = new HTabPanel();
        hTabPanel.setPanel(LangMgr.lang(Word2.CODE), areaLast, newHeaderConfig(LangMgr.lang(Word2.CODE)));
        hTabPanel.setPanel(LangMgr.lang(Word2.AUTHORIZATION), roleGranteComp, newHeaderConfig(LangMgr.lang(usrBean.getOprType() == UsrBean.OprType.PERM ? Word2.PRIVILEGE : Word2.ROLE)));
        hTabPanel.selectPanel(LangMgr.lang(Word2.AUTHORIZATION));
        
        lastPanel.set(hTabPanel.getComp());
        return lastPanel;
    }
    
    @Override
    public void upInitForm(String usrName) throws SQLException {
        nameInput.setValue(usrName);
        nameInput.setEnabled(false);
        roleGranteComp.refreshTable(getUsrRoles());
        
        oldUser = getUserBean();
    }
    
    @Override
    public String getSql() {
        StringBuffer buffer = new StringBuffer();
        String sb = "";
        String grant, revok;
        Map<Boolean, List<String>> map = roleGranteComp.getNameMap();
        if (usrBean.isUser()) {
            if (nameInput.isEnabled()) {
                sb = UserSqlUtil.getCreateSql(usrBean.getDbType(), usrBean.getVersion(), getUserBean());
            } else {
                sb = UserSqlUtil.getUpdateSql(usrBean.getDbType(), usrBean.getVersion(), oldUser, getUserBean());
            }
        } else if (usrBean.isRole()) {
            if (nameInput.isEnabled()) {
                sb = UserSqlUtil.getCreateRolesSql(usrBean.getDbType(), usrBean.getVersion(), getUserBean());
            } else {
                sb = UserSqlUtil.getUpdateRolesSql(usrBean.getDbType(), usrBean.getVersion(), oldUser, getUserBean());
            }
        }
        buffer.append(StringUtils.isNotBlank(sb) ? sb + ";\n" : "");
        grant = UserSqlUtil.getGrantRolesSql(usrBean.getDbType(), usrBean.getVersion(), map.get(true), nameInput.getValue());
        revok = UserSqlUtil.getRevokRolesSql(usrBean.getDbType(), usrBean.getVersion(), map.get(false), nameInput.getValue());
        if (StringUtils.isNotBlank(grant)) buffer.append(grant + ";\n");
        if (StringUtils.isNotBlank(revok)) buffer.append(revok);
        
        return StringUtils.isNotBlank(buffer) ? buffer.toString() : "";
    }
    
    @Override
    public String checkForm() {
        if (StringUtils.isBlank(nameInput.getValue())) {
            return LangMgr.sLang(CsL.PLEASE_FILL_NAME);
        }
        if (usrBean.isUser() && nameInput.isEnabled() && StringUtils.isBlank(pwInput.getValue())) {
            return LangMgr.sLang(CsL.PLEASE_ENTER_PASSWORD);
        }
        return null;
    }
    
    @Override
    public void inform() {
        hTabPanel.selectPanel(LangMgr.lang(Word2.CODE));
    }
    
    /**
     * 获取角色集合
     */
    protected List<Map<String, String>> getUsrRoles() throws SQLException {
        List<String> userRoles = SqlQueryUtil.selectOneColumn(conn, HanaRoleUserSqlUtil.getUseRoleNamesSql(nameInput.getValue()));
        List<String> allRoles = SqlQueryUtil.selectOneColumn(conn, HanaInstanceUtil.getRoleNamesSql());
        List<Map<String, String>> roleMaps = new ArrayList<>();
        for (String role : allRoles) {
            if (role.equals("PUBLIC")) continue;
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", role);
            hashMap.put("grant", String.valueOf(userRoles.contains(role)));
            roleMaps.add(hashMap);
        }
        return roleMaps;
    }
    
    /**
     * 获取用户权限集合
     */
    protected List<Map<String, String>> getUsrPrivs() throws SQLException {
        List<String> myPerms = SqlQueryUtil.selectOneColumn(conn, HanaRoleUserSqlUtil.getUsePrivsNamesSql(nameInput.getValue()));
        List<String> allPerms = SqlQueryUtil.selectOneColumn(conn, HanaRoleUserSqlUtil.getPrivsNamesSql());
        List<Map<String, String>> mapList = new ArrayList<>();
        for (String s : allPerms) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", s);
            hashMap.put("grant", myPerms.contains(s) ? "true" : "false");
            mapList.add(hashMap);
        }
        return mapList;
    }
    
    protected SqlUserBean getUserBean() {
        return new SqlUserBean(nameInput.getValue(), pwInput.getValue());
    }
}
