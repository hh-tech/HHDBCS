package com.hh.hhdb_admin.mgr.usr.comp.form;

import com.hh.frame.create_dbobj.userMr.base.UsrFormType;
import com.hh.frame.create_dbobj.userMr.base.UsrItem;
import com.hh.frame.dbobj2.ora.OraRole;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.PasswordInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.mgr.usr.comp.GrantTableComp;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * oracle数据库的用户form
 */
public class OrRoleForm extends UsrBaseForm {
    private final HPanel panel;
    private final TextInput usrNameInput;
    private final PasswordInput pwInput;
    private final PasswordInput rePwInput;
    private final CheckBoxInput cbi;

    private final GrantTableComp roleGranteComp;
    private final HashMap<UsrItem, String> map = new HashMap<>();
    private final static String LK_ROLE_NAME = "ROLE_NAME";
    private final static String LK_PWD = "PWD";
    private final static String LK_RE_PWD = "RE_PWD";

    private final static String LK_ROLE_INFO = "ROLE_INFO";
    private final static String LK_ROLE_AUTH = "ROLE_AUTH";
    private final static String LK_PLEASE_ENTER_ROLE_NAME = "PLEASE_ENTER_ROLE_NAME";

    private final static String LK_PASSWORD_NOT_EQUAL = "PASSWORD_NOT_EQUAL";
    private final static String LK_VALIDATE_OK = "VALIDATE_OK";
    private final static String LK_PASSWORD_AUTH = "PASSWORD_AUTH";
    private boolean isUpdate;

    public OrRoleForm(Connection conn) {
        super(conn);
        panel = new HPanel(new HDivLayout(0, 5, GridSplitEnum.C12));
        HPanel panelInfo = new HPanel(new HDivLayout(0, 5, GridSplitEnum.C12));
        usrNameInput = new TextInput(UsrItem.usr_name.name());
        pwInput = new PasswordInput(UsrItem.usr_pwd.name());
        rePwInput = new PasswordInput(UsrItem.usr_rePwd.name());
        HPanel szPanel = new HPanel(getLayout());
        cbi = new CheckBoxInput(UsrItem.password_authentication.name());
        cbi.addListen(e -> {
            JCheckBox checkBox = (JCheckBox) e.getSource();
            pwInput.setEnabled(checkBox.isSelected());
            rePwInput.setEnabled(checkBox.isSelected());

        });
        cbi.setText(getLang(LK_PASSWORD_AUTH));
        cbi.setValue("false");
        szPanel.add(new HeightComp(10));
        szPanel.add(cbi);
        pwInput.setEnabled(false);
        rePwInput.setEnabled(false);
        panelInfo.add(new HeightComp(5));
        panelInfo.add(getLabelInput(getLang(LK_ROLE_NAME), usrNameInput));
        panelInfo.add(szPanel);
        panelInfo.add(getLabelInput(getLang(LK_PWD), pwInput));
        panelInfo.add(getLabelInput(getLang(LK_RE_PWD), rePwInput));

        panelInfo.add(new HeightComp(5));
        panelInfo.setTitle(getLang(LK_ROLE_INFO));
        HPanel panelRole = new HPanel();
        roleGranteComp = new GrantTableComp(GrantTableComp.PrivsType.role, conn);
        roleGranteComp.setHeight(260);
        panelRole.add(roleGranteComp);
        panelRole.setTitle(getLang(LK_ROLE_AUTH));
        panel.add(panelInfo);
        panel.add(panelRole);
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
            cbi.setValue("false");
        }

        roleGranteComp.refreshTable();

    }


    @Override
    public void initForm(String usrName) throws Exception {
        if (conn == null) {
            return;
        }
        map.clear();
        roleGranteComp.setPermUsr(usrName);
        roleGranteComp.refreshTable();
        usrNameInput.setValue(usrName);
        usrNameInput.setEnabled(false);
        OraRole oraRole = new OraRole(conn);
        Map<String, String> prop;
        prop = oraRole.getRoleProp(usrName);
        pwInput.setValue("");
        rePwInput.setValue("");
        cbi.setValue(String.valueOf("PASSWORD".equals(prop.get("role_type"))));
        map.put(UsrItem.password_authentication, cbi.getValue());
        map.put(UsrItem.usr_name, DbCmdStrUtil.toDbCmdStr(usrName, this.dbType));
        map.put(UsrItem.usr_pwd, pwInput.getValue());

    }

    @Override
    public String getSql() throws Exception {
        String rolName = usrNameInput.getValue();
        String passwords = pwInput.getValue();
        HashMap<UsrItem, String> newMap = new HashMap<>();
        newMap.put(UsrItem.usr_name, rolName);
        newMap.put(UsrItem.usr_pwd, passwords);
        newMap.put(UsrItem.password_authentication, cbi.getValue());
        StringBuffer buffer = new StringBuffer();
        if (isUpdate) {
            newMap.put(UsrItem.usr_name, DbCmdStrUtil.toDbCmdStr(rolName, this.dbType));
            buffer.append(usrMr.getUpdateSql(UsrFormType.role, map, newMap));
        } else {
            buffer.append(usrMr.getCreateSql(UsrFormType.role, newMap));
        }
        roleGranteComp.setPermUsr(rolName);
        buffer.append(roleGranteComp.getSql());
        return buffer.toString();
    }

    @Override
    public void setUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
        roleGranteComp.setPermUpdate(isUpdate);

    }

    @Override
    public ResultBean checkForm() {
        if (!isUpdate) {
            if (StringUtils.isBlank(usrNameInput.getValue())) {
                return new ResultBean(-1, getLang(LK_PLEASE_ENTER_ROLE_NAME));
            }
        }
        if (!rePwInput.getValue().equals(pwInput.getValue())) {
            return new ResultBean(-1, getLang(LK_PASSWORD_NOT_EQUAL));
        }
        return new ResultBean(0, getLang(LK_VALIDATE_OK));
    }

}
