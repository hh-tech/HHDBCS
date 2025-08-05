package com.hh.hhdb_admin.mgr.usr2.form;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.UserSqlUtil;
import com.hh.frame.db_sql.info.UsrItem;
import com.hh.frame.db_sql.pg.PgInstanceUtil;
import com.hh.frame.db_sql.pg.PgRoleUserSqlUtil;
import com.hh.frame.db_sql.pg.info.PgRoleInfo;
import com.hh.frame.db_sql.pg.info.PgUserBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.DateTimePickerInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.mgr.usr2.UsrBean;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PgUsrForm extends UsrBaseForm {
    private TextInput maxConsInput;
    private DateTimePickerInput expiredTimeInput;
    private CheckBoxInput loginRole;
    private CheckBoxInput superRole;
    private CheckBoxInput dbRole;
    private CheckBoxInput authRole;
    private HTabPanel hTabPanel;

    public PgUsrForm(UsrBean usrBean, Connection conn) throws Exception {
        super(usrBean,conn);
        if (usrBean.getOprType() != UsrBean.OprType.PERM) {
            maxConsInput = new TextInput(UsrItem.max_cons.name());
            initDateTimeInput();
    
            loginRole = new CheckBoxInput(UsrItem.login_role.name(), LangMgr.lang(Word4.LOGIN_ROLE));
            dbRole = new CheckBoxInput(UsrItem.db_role.name(), LangMgr.lang(Word4.DATABASE_ROLE));
            authRole = new CheckBoxInput(UsrItem.auth_role.name(), LangMgr.lang(Word4.AUTHORIZED_ROLE));
            superRole = new CheckBoxInput(UsrItem.super_role.name(), LangMgr.lang(Word4.SUPER_USER)) {
                @Override
                protected void onClick(ActionEvent e) {
                    dbRole.setEnabled(!isChecked());
                    authRole.setEnabled(!isChecked());
                }
            };
            if (usrBean.getDbType() == DBTypeEnum.gaussdb) superRole.setEnabled(false);
    
            //显示所有可选角色
            roleGranteComp.refreshTable(getUsrRoles());
        } else {
            nameInput.setValue(usrBean.getName());
            roleGranteComp.refreshTable(getUsrDB());
        }
    }
    
    @Override
    public LastPanel getLastPanel() {
        LastPanel lastPanel = new LastPanel();
        if (usrBean.getOprType() != UsrBean.OprType.PERM) {
            HDivLayout layout = new HDivLayout(GridSplitEnum.C4);
            layout.setLeftWidth(200);
            HPanel checkPanel = new HPanel(layout);
            checkPanel.add(loginRole,superRole,dbRole,authRole);
    
            layout = new HDivLayout(0, 5, GridSplitEnum.C12);
            layout.setRightWidth(100);
            HPanel panel = new HPanel(layout);
            panel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
            panel.add(new HeightComp(5));
            panel.add(getLabelInput(LangMgr.sLang(CsL.CONN_NUM)+" :", maxConsInput));
            panel.add(getLabelInput(LangMgr.lang(Word4.FAILURE_TIME)+" :", expiredTimeInput));
            panel.add(checkPanel);
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
        roleGranteComp.refreshTable(getUsrRoles());
        
        String sql = PgInstanceUtil.getRoleInfoSql(usrBean.getDbType(), usrName, PgRoleInfo.class);
        
        Map<PgRoleInfo, String> map = SqlQueryUtil.selectOneStrMap( conn, sql, PgRoleInfo.class);
        if (null != map.get(PgRoleInfo.ROLCONNLIMIT)) {
            maxConsInput.setValue(map.get(PgRoleInfo.ROLCONNLIMIT).equals("-1") ? "" : map.get(PgRoleInfo.ROLCONNLIMIT));
        }
        expiredTimeInput.setValue(null != map.get(PgRoleInfo.ROLVALIDUNTIL) ? map.get(PgRoleInfo.ROLVALIDUNTIL)+"" : "");
        superRole.setValue(map.get(PgRoleInfo.ROLSUPER).equals("t") ? "true":"false");
        loginRole.setValue(map.get(PgRoleInfo.ROLCANLOGIN).equals("t") ? "true":"false");
        if (map.get(PgRoleInfo.ROLSUPER).equals("t")) {
            dbRole.setEnabled(false);
            authRole.setEnabled(false);
        } else {
            dbRole.setValue(map.get(PgRoleInfo.ROLCREATEDB).equals("t") ? "true":"false");
            authRole.setValue(map.get(PgRoleInfo.ROLCREATEROLE).equals("t") ? "true":"false");
        }
        oldUser = getUserBean();
    }
    
    @Override
    public String getSql() {
        StringBuffer buffer = new StringBuffer();
        String sb,grant = "",revok = "";
        Map<Boolean, List<String>> map = roleGranteComp.getNameMap();
        if (usrBean.isUser() || usrBean.isRole()) {
            if (usrBean.isUser()) {
                if (nameInput.isEnabled()) {
                    sb = UserSqlUtil.getCreateSql(usrBean.getDbType(), usrBean.getVersion(), getUserBean());
                } else {
                    sb = UserSqlUtil.getUpdateSql(usrBean.getDbType(), usrBean.getVersion(), oldUser, getUserBean());
                }
            } else {
                if (nameInput.isEnabled()) {
                    sb = UserSqlUtil.getCreateRolesSql(usrBean.getDbType(), usrBean.getVersion(), getUserBean());
                } else {
                    sb = UserSqlUtil.getUpdateRolesSql(usrBean.getDbType(), usrBean.getVersion(), oldUser, getUserBean());
                }
            }
            buffer.append(StringUtils.isNotBlank(sb) ? sb + ";\n" : "");
            grant = UserSqlUtil.getGrantRolesSql(usrBean.getDbType(), usrBean.getVersion(), map.get(true), nameInput.getValue());
            revok = UserSqlUtil.getRevokRolesSql(usrBean.getDbType(), usrBean.getVersion(), map.get(false), nameInput.getValue());
        } else if (usrBean.getOprType() == UsrBean.OprType.PERM) {
            grant = PgRoleUserSqlUtil.getGrantDataBaseSql(usrBean.getVersion(),map.get(true),nameInput.getValue());
            revok = PgRoleUserSqlUtil.getRevokDataBaseSql(usrBean.getVersion(),map.get(false),nameInput.getValue());
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
        if (nameInput.isEnabled() && StringUtils.isBlank(pwInput.getValue()) && usrBean.isUser()) {
            return LangMgr.sLang(CsL.PLEASE_ENTER_PASSWORD);
        }
        if (!StringUtils.isBlank(maxConsInput.getValue()) && !StringUtils.isNumeric(maxConsInput.getValue())) {
            return LangMgr.sLang(CsL.MAX_CONN_NOT_CORRECT);
        }

        if (!StringUtils.isBlank(expiredTimeInput.getValue()) && !isValidDate(expiredTimeInput.getValue())) {
            return LangMgr.sLang(CsL.TIMING_ERROR);
        }
        return null;
    }
    
    @Override
    public void inform() {
        hTabPanel.selectPanel(LangMgr.lang(Word2.CODE));
    }
    
    private PgUserBean getUserBean() {
        PgUserBean uBean=new PgUserBean(nameInput.getValue(),pwInput.getValue());
        uBean.setMaxCons(maxConsInput.getValue());
        uBean.setExpiredTime(expiredTimeInput.getValue());
        uBean.setLoginRole(loginRole.isChecked());
        uBean.setSuperRole(superRole.isChecked());
        uBean.setDbRole(dbRole.isChecked());
        uBean.setAuthRole(authRole.isChecked());
        return uBean;
    }
    
    /**
     * 获取用户角色集合
     */
    protected List<Map<String, String>> getUsrRoles() throws SQLException {
        List<String> userRoles = SqlQueryUtil.selectOneColumn(conn, PgRoleUserSqlUtil.getUseRoleNamesSql(nameInput.getValue()));
        List<String> allRoles = SqlQueryUtil.selectOneColumn(conn, PgInstanceUtil.getRoleNamesSql());
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
     * 获取用户数据库集合
     */
    private List<Map<String, String>> getUsrDB() throws SQLException {
        List<Map<String, String>> mapList = new ArrayList<>();
        Map<String,String> map = SqlQueryUtil.selectTowColMap(conn, PgRoleUserSqlUtil.getUserDataBaseSql(nameInput.getValue()));
        for(String str : map.keySet()){
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("name",str);
            hashMap.put("grant", map.get(str).equals("t") ? "true":"false");
            mapList.add(hashMap);
        }
        return mapList;
    }
    
    /**
     * 初始化时间选择插件
     */
    private void initDateTimeInput() {
        String placeholder = LangMgr.sLang(CsL.DB_CLICK_SELECT);
        expiredTimeInput = new DateTimePickerInput(UsrItem.expired_time.name(), "", DateTimePickerInput.PART.ALL, true) {
            @Override
            public String getValue() {
                if (super.getValue().equals(placeholder)) {
                    return "";
                }
                return super.getValue();
            }

            @Override
            public void setValue(String value) {
                if (StringUtils.isBlank(value)) {
                    super.setValue(placeholder);
                } else {
                    super.setValue(value);
                }
            }
        };
        expiredTimeInput.setValue(placeholder);
        expiredTimeInput.getComp().setForeground(Color.GRAY);
        expiredTimeInput.getComp().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                //获取焦点时，清空提示内容
                String temp = expiredTimeInput.getValue();
                if (temp.equals(placeholder)) {
                    expiredTimeInput.setValue("");
                    expiredTimeInput.getComp().setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                //失去焦点时，没有输入内容，显示提示内容
                String temp = expiredTimeInput.getValue();
                if (temp.equals("")) {
                    expiredTimeInput.setValue(placeholder);
                    expiredTimeInput.getComp().setForeground(Color.GRAY);
                }
            }
        });
    }
}
