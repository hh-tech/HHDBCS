package com.hh.hhdb_admin.mgr.usr.comp.form;

import com.hh.frame.create_dbobj.userMr.base.UsrFormType;
import com.hh.frame.create_dbobj.userMr.base.UsrItem;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.DateTimePickerInput;
import com.hh.frame.swingui.view.input.PasswordInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.mgr.usr.util.ServiceUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: yangxianhui
 * @date: 2020-09-14 10:50
 */
public class HHUsrForm extends UsrBaseForm {
    private final HPanel panel;
    private final TextInput usrNameInput;
    private final PasswordInput pwInput;
    private final PasswordInput rePwInput;
    private final TextInput maxConsInput;
    private DateTimePickerInput expiredTimeInput;


    private final CheckBoxInput loginRole;
    private final CheckBoxInput superRole;
    private final CheckBoxInput dbRole;
    private final CheckBoxInput authRole;
    private final HashMap<UsrItem, String> map = new HashMap<>();

    private final static String LK_USER_INFO = "USER_INFO";
    private final static String LK_DB_ROLE = "DB_ROLE";
    private final static String LK_LOGIN_ROLE = "LOGIN_ROLE";
    private final static String LK_SUPER_ROLE = "SUPER_ROLE";
    private final static String LK_AUTH_ROLE = "AUTH_ROLE";
    private final static String LK_CONN_NUM = "CONN_NUM";
    private final static String LK_PWD_EXPIRED_TIME = "PWD_EXPIRED_TIME";
    private final static String LK_USER_NAME = "USER_NAME";
    private final static String LK_PWD = "PWD";
    private final static String LK_RE_PWD = "RE_PWD";
    private final static String LK_DB_CLICK_SELECT = "DB_CLICK_SELECT";
    private final static String LK_PLEASE_ENTER_USER_NAME = "PLEASE_ENTER_USER_NAME";
    private final static String LK_PLEASE_ENTER_PASSWORD = "PLEASE_ENTER_PASSWORD";
    private final static String LK_PLEASE_ENTER_RE_PASSWORD = "PLEASE_ENTER_RE_PASSWORD";
    private final static String LK_PASSWORD_NOT_EQUAL = "PASSWORD_NOT_EQUAL";
    private final static String LK_MAX_CONN_NOT_CORRECT = "MAX_CONN_NOT_CORRECT";
    private final static String LK_VALIDATE_OK = "VALIDATE_OK";

    private boolean isUpdate;

    public HHUsrForm(Connection conn) {
        super(conn);
        panel = new HPanel(new HDivLayout(10, 25, GridSplitEnum.C12));
        usrNameInput = new TextInput(UsrItem.usr_name.name());
        pwInput = new PasswordInput(UsrItem.usr_pwd.name());
        rePwInput = new PasswordInput(UsrItem.usr_rePwd.name());
        maxConsInput = new TextInput(UsrItem.max_cons.name());
        initDateTimeInput();
        HPanel checkPanel = new HPanel(new HDivLayout(GridSplitEnum.C6));

        loginRole = new CheckBoxInput(UsrItem.login_role.name(), getLang(LK_LOGIN_ROLE));
        superRole = new CheckBoxInput(UsrItem.super_role.name(), getLang(LK_SUPER_ROLE));
        dbRole = new CheckBoxInput(UsrItem.db_role.name(), getLang(LK_DB_ROLE));
        authRole = new CheckBoxInput(UsrItem.auth_role.name(), getLang(LK_AUTH_ROLE));

        superRole.addListen(e -> {
            JCheckBox checkBox = (JCheckBox) e.getSource();
            if (checkBox.getText().equals(getLang(LK_SUPER_ROLE))) {
                if (checkBox.isSelected()) {
                    dbRole.setValue("false");
                    authRole.setValue("false");
                }
                dbRole.setEnabled(!checkBox.isSelected());
                authRole.setEnabled(!checkBox.isSelected());
            }
        });
        checkPanel.add(loginRole);
        checkPanel.add(superRole);
        checkPanel.add(dbRole);
        checkPanel.add(authRole);
        panel.add(new HeightComp(15));
        panel.add(getLabelInput(getLang(LK_USER_NAME), usrNameInput));
        panel.add(getLabelInput(getLang(LK_PWD), pwInput));
        panel.add(getLabelInput(getLang(LK_RE_PWD), rePwInput));
        panel.add(getLabelInput(getLang(LK_CONN_NUM), maxConsInput));
        panel.add(getLabelInput(getLang(LK_PWD_EXPIRED_TIME), expiredTimeInput));
        HDivLayout layout = new HDivLayout(GridSplitEnum.C4);
        layout.setMaxWidth(400);
        HPanel hPanel = new HPanel(layout);
        hPanel.add(new HeightComp(10));
        hPanel.add(checkPanel);
        panel.add(hPanel);
        panel.add(new HeightComp(20));
        panel.setTitle(getLang(LK_USER_INFO));
        this.comp = panel.getComp();

    }


    @Override
    public void clearForm() throws Exception {
        if (isUpdate) {
            pwInput.setValue("");
            rePwInput.setValue("");
        } else {
            usrNameInput.setEnabled(true);
            pwInput.setValue("");
            rePwInput.setValue("");
            maxConsInput.setValue("");
            dbRole.setValue("false");
            authRole.setValue("false");
            loginRole.setValue("false");
            superRole.setValue("false");
            expiredTimeInput.setValue("");
        }
    }

    @Override
    public String getSql() throws Exception {

        String rolName = usrNameInput.getValue();
        String passwords = pwInput.getValue();
        String maxCons = maxConsInput.getValue();
        String expiredTime = expiredTimeInput.getValue();

        String loginRoleValue = loginRole.getValue();
        String superRoleValue = superRole.getValue();
        String dbRoleValue = dbRole.getValue();
        String authRoleValue = authRole.getValue();
        StringBuilder buffer = new StringBuilder();
        Map<UsrItem, String> newMap = new HashMap<>();
        newMap.put(UsrItem.usr_name, rolName);
        newMap.put(UsrItem.usr_pwd, passwords);
        newMap.put(UsrItem.max_cons, maxCons);
        newMap.put(UsrItem.expired_time, expiredTime);
        newMap.put(UsrItem.login_role, loginRoleValue);
        newMap.put(UsrItem.super_role, superRoleValue);
        newMap.put(UsrItem.db_role, dbRoleValue);
        newMap.put(UsrItem.auth_role, authRoleValue);
        if (isUpdate) {
            newMap.put(UsrItem.usr_name, DbCmdStrUtil.toDbCmdStr(rolName, this.dbType));
            buffer.append(usrMr.getUpdateSql(UsrFormType.usr, map, newMap));
        } else {
            map.clear();
            buffer.append(usrMr.getCreateSql(UsrFormType.usr, newMap));
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
            if (StringUtils.isBlank(pwInput.getValue())) {
                return new ResultBean(-1, getLang(LK_PLEASE_ENTER_PASSWORD));
            }
            if (StringUtils.isBlank(rePwInput.getValue())) {
                return new ResultBean(-1, getLang(LK_PLEASE_ENTER_RE_PASSWORD));
            }
        }
        if (!rePwInput.getValue().equals(pwInput.getValue())) {
            return new ResultBean(-1, getLang(LK_PASSWORD_NOT_EQUAL));
        }
        if (!StringUtils.isBlank(maxConsInput.getValue()) && !StringUtils.isNumeric(maxConsInput.getValue())) {
            return new ResultBean(-1, getLang(LK_MAX_CONN_NOT_CORRECT));
        }

        if (!StringUtils.isBlank(expiredTimeInput.getValue()) && !isValidDate(expiredTimeInput.getValue())) {
            return new ResultBean(-1, getLang(LK_PWD_EXPIRED_TIME));
        }

        return new ResultBean(0, getLang(LK_VALIDATE_OK));
    }


    @Override
    public void initForm(String usrName) throws Exception {
        if (conn == null) {
            return;
        }
        map.clear();
        usrNameInput.setValue(usrName);
        usrNameInput.setEnabled(false);
        List<Map<String, Object>> list = ServiceUtil.getconnInformation(conn, usrName);
        if (list == null || list.isEmpty()) {
            throw new Exception("用户不存在" + usrName);
        }
        String sum = list.get(0).get("rolconnlimit").toString().equals("-1") ? "" : list.get(0).get("rolconnlimit").toString();
        maxConsInput.setValue(sum);
        String time = list.get(0).get("rolvaliduntil") != null ? list.get(0).get("rolvaliduntil").toString() : "";
        expiredTimeInput.setValue(time);
        loginRole.setValue(list.get(0).get("rolcanlogin").toString());
        superRole.setValue(list.get(0).get("rolsuper").toString());
        if (Boolean.parseBoolean(list.get(0).get("rolsuper").toString())) {
            dbRole.setEnabled(false);
            authRole.setEnabled(false);
        } else {
            dbRole.setValue(list.get(0).get("rolcreatedb").toString());
            authRole.setValue(list.get(0).get("rolcreaterole").toString());
        }

        map.put(UsrItem.usr_name, usrNameInput.getValue());
        map.put(UsrItem.usr_pwd, pwInput.getValue());
        map.put(UsrItem.max_cons, maxConsInput.getValue());
        map.put(UsrItem.expired_time, expiredTimeInput.getValue());
        map.put(UsrItem.login_role, loginRole.getValue());
        map.put(UsrItem.super_role, superRole.getValue());
        map.put(UsrItem.auth_role, authRole.getValue());
        map.put(UsrItem.db_role, dbRole.getValue());
    }


    /**
     * 初始化时间选择插件
     */
    private void initDateTimeInput() {
        String placeholder = getLang(LK_DB_CLICK_SELECT);
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
