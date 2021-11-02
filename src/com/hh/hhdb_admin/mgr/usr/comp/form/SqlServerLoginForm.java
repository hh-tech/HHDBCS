package com.hh.hhdb_admin.mgr.usr.comp.form;

import com.hh.frame.create_dbobj.userMr.base.SqlServerAuthType;
import com.hh.frame.create_dbobj.userMr.base.UsrFormType;
import com.hh.frame.create_dbobj.userMr.base.UsrItem;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.PasswordInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.usr.util.ServiceUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sqlserver数据库的登录名form
 */
public class SqlServerLoginForm extends UsrBaseForm {


    private TextInput usrNameInput;
    private PasswordInput pwInput;
    private PasswordInput rePwInput;
    private CheckBoxInput loginSet;
    private SelectBox defaultDBBox;
    private SelectBox verifyBox;
    private final HashMap<UsrItem, String> map = new HashMap<>();
    private final static String LK_LOGIN_NAME = "LOGIN_NAME";
    private final static String LK_PWD = "PWD";
    private final static String LK_RE_PWD = "RE_PWD";
    private final static String LK_LOGIN_INFO = "LOGIN_INFO";
    private final static String LK_ROLE_AUTH = "AUTH_SERVER_ROLE";


    private final static String LK_PLEASE_ENTER_USER_NAME = "PLEASE_ENTER_USER_NAME";
    private final static String LK_PLEASE_ENTER_PASSWORD = "PLEASE_ENTER_PASSWORD";
    private final static String LK_PLEASE_ENTER_RE_PASSWORD = "PLEASE_ENTER_RE_PASSWORD";
    private final static String LK_PASSWORD_NOT_EQUAL = "PASSWORD_NOT_EQUAL";
    private final static String LK_VALIDATE_OK = "VALIDATE_OK";
    private static final String LK_DEFAULT_DB = "DEFAULT_DB";
    private static final String LK_AUTHENTICATION = "AUTHENTICATION";


    private boolean isUpdate;
    private HTable sysRoleTable;


    public SqlServerLoginForm(Connection conn) {
        super(conn);
        try {

            HPanel panelInfo = new HPanel(new HDivLayout(0, 5, GridSplitEnum.C12));
            usrNameInput = new TextInput(UsrItem.usr_name.name());
            pwInput = new PasswordInput(UsrItem.usr_pwd.name());
            rePwInput = new PasswordInput(UsrItem.usr_rePwd.name());
            defaultDBBox = new SelectBox();
            initDbBoxValues();
            verifyBox = new SelectBox();
            verifyBox.addOption(getLang("SQLSERVER_AUTH"), SqlServerAuthType.sqlserver.name());
            verifyBox.addOption(getLang("WINDOWS_AUTH"), SqlServerAuthType.windows.name());
            verifyBox.addListener(e -> resetItems());


            panelInfo.add(new HeightComp(5));
            panelInfo.add(getLabelInput(getLang(LK_LOGIN_NAME), usrNameInput));
            panelInfo.add(getLabelInput(getLang(LK_AUTHENTICATION), verifyBox));
            panelInfo.add(getLabelInput(getLang(LK_PWD), pwInput));
            panelInfo.add(getLabelInput(getLang(LK_RE_PWD), rePwInput));
            panelInfo.add(getLabelInput(getLang(LK_DEFAULT_DB), defaultDBBox));

            panelInfo.add(new HeightComp(5));
            loginSet = new CheckBoxInput("login");
            loginSet.setText(getLang("NOT_ALLOW_LOGIN"));
            HPanel loginSetPanel = new HPanel(getLayout());
            loginSetPanel.add(new HeightComp(10));
            loginSetPanel.add(loginSet);

            panelInfo.add(loginSetPanel);
            HPanel panelRole = new HPanel();
            LastPanel LastRole = new LastPanel();
            initSysRoleTable();
            SearchToolBar searchToolBar = new SearchToolBar(sysRoleTable);
            LastRole.setHead(searchToolBar.getComp());
            LastRole.setWithScroll(sysRoleTable.getComp());
            panelRole.setLastPanel(LastRole);
            panelRole.getComp().setPreferredSize(new Dimension(panelRole.getComp().getWidth(), 300));

            panelInfo.setTitle(getLang(LK_LOGIN_INFO));
            panelRole.setTitle(getLang(LK_ROLE_AUTH));

            HPanel panel = new HPanel();
            panel.add(panelInfo);
            panel.add(panelRole);
            this.comp = panel.getComp();
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
    }


    private void resetItems() {
        pwInput.setEnabled(!verifyBox.getValue().equals(SqlServerAuthType.windows.name()));
        rePwInput.setEnabled(!verifyBox.getValue().equals(SqlServerAuthType.windows.name()));

    }

//
//
//    private List<Map<String, String>> toLoadMap(List<HTabRowBean> rowBeans) {
//        Map<String, String> map;
//        List<Map<String, String>> values = new ArrayList<>();
//        for (HTabRowBean row : rowBeans) {
//            map = new HashMap<>();
//            for (String str : row.getOldRow().keySet()) {
//                map.put(str, row.getOldRow().get(str));
//            }
//            if (row.getCurrRow() != null) {
//                for (String str : row.getCurrRow().keySet()) {
//                    map.put(str, row.getCurrRow().get(str));
//                }
//            }
//
//            values.add(map);
//
//        }
//        return values;
//    }


    private void initSysRoleTable() throws SQLException {
        sysRoleTable = new HTable();
        sysRoleTable.addCols(new BoolCol("grant", getLang("SELECT")),new DataCol("role", getLang("ROLES")));
        sysRoleTable.setRowHeight(25);
        if (!isUpdate) {
            sysRoleTable.load(usrMr.getUsrRoles(conn, ""), 1);
        }

    }

    @Override
    public void clearForm() throws Exception {
        if (isUpdate) {
            pwInput.setValue("");
            rePwInput.setValue("");
        } else {
            usrNameInput.setEnabled(true);
        }

    }


    @Override
    public void initForm(String usrName) throws SQLException {
        if (conn == null) {
            return;
        }
        map.clear();

        usrNameInput.setValue(usrName);
        usrNameInput.setEnabled(false);
        List<Map<String, String>> datas = ServiceUtil.getSqlServeLoginInfo(conn, usrName);
        if (datas.isEmpty()) {
            throw new SQLException(getLang("LOGIN_NAME_NOT_EXIST"));
        }
        Map<String, String> data = datas.get(0);
        defaultDBBox.setValue(data.get("dbname"));
        if (data.get("is_disabled") != null) {
            loginSet.setValue(Boolean.valueOf(data.get("is_disabled").equals("1")).toString());
        }


        sysRoleTable.load(usrMr.getUsrRoles(conn, usrName), 1);
        String verify = data.get("isntname").equals("1") ? SqlServerAuthType.windows.name() : SqlServerAuthType.sqlserver.name();
        verifyBox.setValue(verify);
        verifyBox.setEnabled(false);
        resetItems();
        map.put(UsrItem.usr_name, usrName);
        map.put(UsrItem.usr_pwd, pwInput.getValue());
        map.put(UsrItem.default_database, defaultDBBox.getValue());
        map.put(UsrItem.authentication_type, verifyBox.getValue());
        map.put(UsrItem.allow_login, String.valueOf(!Boolean.parseBoolean(loginSet.getValue())));


    }

    @Override
    public String getSql() {
        String rolName = usrNameInput.getValue();
        String passwords = pwInput.getValue();
        HashMap<UsrItem, String> newMap = new HashMap<>();
        newMap.put(UsrItem.usr_name, rolName);
        newMap.put(UsrItem.usr_pwd, passwords);
        newMap.put(UsrItem.default_database, defaultDBBox.getValue());
        newMap.put(UsrItem.authentication_type, verifyBox.getValue());
        newMap.put(UsrItem.allow_login, String.valueOf(!Boolean.parseBoolean(loginSet.getValue())));
        StringBuffer buffer = new StringBuffer();
        if (isUpdate) {
            buffer.append(usrMr.getUpdateSql(UsrFormType.login, map, newMap));
            buffer.append(getRoleTableSql(newMap.get(UsrItem.usr_name)));
        } else {
            buffer.append(usrMr.getCreateSql(UsrFormType.login, newMap));
            buffer.append(getRoleTableSql(rolName));
        }

        return buffer.toString();
    }

    @Override
    public void setUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    @Override
    public ResultBean checkForm() {
        if (!isUpdate) {
            if (StringUtils.isBlank(usrNameInput.getValue())) {
                return new ResultBean(-1, getLang(LK_PLEASE_ENTER_USER_NAME));
            }

            if (!verifyBox.getValue().equals(SqlServerAuthType.windows.name())) {
                if (StringUtils.isBlank(pwInput.getValue())) {
                    return new ResultBean(-1, getLang(LK_PLEASE_ENTER_PASSWORD));
                }
                if (StringUtils.isBlank(rePwInput.getValue())) {
                    return new ResultBean(-1, getLang(LK_PLEASE_ENTER_RE_PASSWORD));
                }
            }

        }
        if (!rePwInput.getValue().equals(pwInput.getValue())) {
            return new ResultBean(-1, getLang(LK_PASSWORD_NOT_EQUAL));
        }
        return new ResultBean(0, getLang(LK_VALIDATE_OK));
    }


    @Override
    public Dimension getSize() {
        return new Dimension(800, 720);
    }


    /**
     * 默认数据库
     */
    private void initDbBoxValues() throws SQLException {
        defaultDBBox.removeAllItems();
        List<Map<String, String>> maps = ServiceUtil.getSqlServerDB(conn);
        for (Map<String, String> map : maps) {
            defaultDBBox.addOption(map.get("db"), map.get("db"));
        }

    }

    private String getRoleTableSql(String user) {
        StringBuffer buffer = new StringBuffer();
        List<HTabRowBean> rowBeans = sysRoleTable.getRowBeans(RowStatus.UPDATE);
        for (HTabRowBean rowBean : rowBeans) {
            if (rowBean.getCurrRow().get("grant").equalsIgnoreCase(Boolean.TRUE.toString())) {
                buffer.append(getGrantRoleSql(rowBean.getOldRow().get("role"), user));
            } else {
                buffer.append(getRevokeRoleSql(rowBean.getOldRow().get("role"), user));
            }
        }
        return buffer.toString();

    }

    private String getGrantRoleSql(String role, String usr) {
        return String.format("EXEC sp_addsrvrolemember @loginame='%s', @rolename = N'%s';%s", usr, role, System.lineSeparator());
    }

    private String getRevokeRoleSql(String role, String usr) {
        return String.format("EXEC sp_dropsrvrolemember @loginame='%s', @rolename = N'%s';%s", usr, role, System.lineSeparator());
    }

}
