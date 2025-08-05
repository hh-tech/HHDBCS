package com.hh.hhdb_admin.mgr.usr2.form;

import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.UserSqlUtil;
import com.hh.frame.db_sql.ora.OraRoleUserSqlUtil;
import com.hh.frame.db_sql.ora.OraTableSpaceSqlUtil;
import com.hh.frame.db_sql.ora.OraUserSqlUtil;
import com.hh.frame.db_sql.ora.info.OraSelUserInfoBean;
import com.hh.frame.db_sql.ora.info.OraUserBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.mgr.usr2.UsrBean;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


public class OrUsrForm extends UsrBaseForm {
    protected SelectBox defaultTsBox;
    protected SelectBox tmpTsBox;
    protected SelectBox profileBox;
    protected HTabPanel hTabPanel;

    public OrUsrForm(UsrBean usrBean, Connection conn) throws Exception {
        super(usrBean,conn);
        if (usrBean.getOprType() != UsrBean.OprType.PERM) {
            init();
        } else {
            nameInput.setValue(usrBean.getName());
            roleGranteComp.refreshTable(getUsrPrivs());
        }
    }
    
    protected void init() throws Exception{
        defaultTsBox = new SelectBox();
        defaultTsBox.addOption("","");
        tmpTsBox = new SelectBox();
        tmpTsBox.addOption("","");
        profileBox = new SelectBox();
        profileBox.addOption("","");
        
        if (usrBean.isUser()) {
            //设置用户的表空间
            Map<String,String> hashMap = SqlQueryUtil.selectTowColMap(conn, OraTableSpaceSqlUtil.getNameAndContentsSql());
            for (String s : hashMap.keySet()) {
                if (hashMap.get(s).equals("PERMANENT")) {
                    defaultTsBox.addOption(s, s);
                } else if (hashMap.get(s).equals("TEMPORARY")) {
                    tmpTsBox.addOption(s, s);
                }
            }
            //设置用户的概要文件
            List<String> list = SqlQueryUtil.selectOneColumn(conn, OraTableSpaceSqlUtil.getProfilesNamesSql());
            list.forEach(a -> profileBox.addOption(a, a));
        }
        
        //显示所有可选角色
        roleGranteComp.refreshTable(getUsrRoles());
    }
    
    @Override
    public LastPanel getLastPanel() {
        LastPanel lastPanel = new LastPanel();
        if (usrBean.isUser()) {
            HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
            layout.setRightWidth(100);
            HPanel panel = new HPanel(layout);
            panel.add(new HeightComp(5));
            panel.add(getLabelInput(LangMgr.sLang(CsL.DEFAULT_TS)+" ：", defaultTsBox));
            panel.add(getLabelInput(LangMgr.sLang(CsL.TMP_TS)+" ：", tmpTsBox));
            panel.add(getLabelInput(LangMgr.sLang(CsL.PROFILE)+" ：", profileBox));
            panel.add(new HeightComp(5));
            panel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
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
        roleGranteComp.refreshTable(getUsrRoles());
    
        String sql = OraUserSqlUtil.getSelUserInfoSql(usrBean.getVersion(), new OraSelUserInfoBean(usrName));
        Map<OraSelUserInfoBean.OraUserInfo, String> map=SqlQueryUtil.selectOneStrMap( conn, sql, OraSelUserInfoBean.OraUserInfo.class);
        defaultTsBox.setValue(map.get(OraSelUserInfoBean.OraUserInfo.DEFAULT_TABLESPACE));
        tmpTsBox.setValue(map.get(OraSelUserInfoBean.OraUserInfo.TEMPORARY_TABLESPACE));
        profileBox.setValue(map.get(OraSelUserInfoBean.OraUserInfo.PROFILE));
        
        oldUser = getUserBean();
    }

    @Override
    public String getSql() {
        StringBuffer buffer = new StringBuffer();
        String sb = "";
        String grant = "",revok = "";
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
     * 获取用户角色集合
     * @return
     * @throws SQLException
     */
    protected List<Map<String, String>> getUsrRoles() throws SQLException {
        List<String> userRoles = SqlQueryUtil.selectOneColumn(conn,OraRoleUserSqlUtil.getUseRoleNamesSql(nameInput.getValue()));
        List<String> allRoles = SqlQueryUtil.selectOneColumn(conn, OraRoleUserSqlUtil.getRoleNamesSql());
        List<Map<String, String>> roleMaps = new ArrayList<>();
        for (String role : allRoles) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", role);
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
    protected List<Map<String, String>> getUsrPrivs() throws SQLException {
        List<String> myPerms = SqlQueryUtil.selectOneColumn(conn, OraRoleUserSqlUtil.getUsePrivsNamesSql(nameInput.getValue()));
        List<String> allPerms = SqlQueryUtil.selectOneColumn(conn, OraRoleUserSqlUtil.getPrivsNamesSql());
        List<Map<String, String>> mapList = new ArrayList<>();
        for(String s:allPerms){
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", s);
            hashMap.put("grant", myPerms.contains(s) ? "true" : "false");
            mapList.add(hashMap);
        }
        return mapList;
    }
    
    protected OraUserBean getUserBean() {
    	OraUserBean uBean=new OraUserBean(nameInput.getValue(),pwInput.getValue());
    	uBean.setDefaultTabSpace(defaultTsBox.getValue());
    	uBean.setTempTabSpace(tmpTsBox.getValue());
    	if (null != profileBox) uBean.setProfile(profileBox.getValue());
    	return uBean;
    }
}
