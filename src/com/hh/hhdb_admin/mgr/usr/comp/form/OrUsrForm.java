package com.hh.hhdb_admin.mgr.usr.comp.form;

import com.hh.frame.create_dbobj.userMr.base.UsrFormType;
import com.hh.frame.create_dbobj.userMr.base.UsrItem;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.PasswordInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.usr.comp.GrantTableComp;
import com.hh.hhdb_admin.mgr.usr.util.ServiceUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * oracle数据库的用户form
 */
public class OrUsrForm extends UsrBaseForm {
    private HPanel panel;
    private TextInput usrNameInput;
    private PasswordInput pwInput;
    private PasswordInput rePwInput;
    private SelectBox defaultTsBox;
    private SelectBox tmpTsBox;
    private SelectBox profileBox;
    private GrantTableComp roleGranteComp;
    private final HashMap<UsrItem, String> map = new HashMap<>();
    private final static String LK_USER_NAME = "USER_NAME";
    private final static String LK_PWD = "PWD";
    private final static String LK_RE_PWD = "RE_PWD";
    private final static String LK_DEFAULT_TS = "DEFAULT_TS";
    private final static String LK_TMP_TS = "TMP_TS";
    private final static String LK_PROFILE = "PROFILE";
    private final static String LK_USER_INFO = "USER_INFO";
    private final static String LK_ROLE_AUTH = "ROLE_AUTH";
    private final static String LK_PLEASE_ENTER_USER_NAME = "PLEASE_ENTER_USER_NAME";
    private final static String LK_PLEASE_ENTER_PASSWORD = "PLEASE_ENTER_PASSWORD";
    private final static String LK_PLEASE_ENTER_RE_PASSWORD = "PLEASE_ENTER_RE_PASSWORD";
    private final static String LK_PASSWORD_NOT_EQUAL = "PASSWORD_NOT_EQUAL";
    private final static String LK_VALIDATE_OK = "VALIDATE_OK";


    private boolean isUpdate;

    /**
     * 表空间类型
     */
    public enum SpaceType {
        PERMANENT, TEMPORARY, UNDO
    }

    public OrUsrForm(Connection conn) {
        super(conn);
        try {
            panel = new HPanel(new HDivLayout(0, 5, GridSplitEnum.C12));

            HPanel panelInfo = new HPanel(new HDivLayout(0, 5, GridSplitEnum.C12));
            usrNameInput = new TextInput(UsrItem.usr_name.name());
            pwInput = new PasswordInput(UsrItem.usr_pwd.name());
            rePwInput = new PasswordInput(UsrItem.usr_rePwd.name());

            defaultTsBox = new SelectBox();
            tmpTsBox = new SelectBox();
            profileBox = new SelectBox();

            initTsBoxValues();
            initProfileBoxValues();
            setInitValue();
            panelInfo.add(new HeightComp(5));
            panelInfo.add(getLabelInput(getLang(LK_USER_NAME), usrNameInput));
            panelInfo.add(getLabelInput(getLang(LK_PWD), pwInput));
            panelInfo.add(getLabelInput(getLang(LK_RE_PWD), rePwInput));
            panelInfo.add(getLabelInput(getLang(LK_DEFAULT_TS), defaultTsBox));
            panelInfo.add(getLabelInput(getLang(LK_TMP_TS), tmpTsBox));
            panelInfo.add(getLabelInput(getLang(LK_PROFILE), profileBox));
            panelInfo.add(new HeightComp(5));
            panelInfo.setTitle(getLang(LK_USER_INFO));
            HPanel panelRole = new HPanel();
            roleGranteComp = new GrantTableComp(GrantTableComp.PrivsType.role, conn);
            roleGranteComp.setHeight(230);
            panelRole.add(roleGranteComp);
            panelRole.setTitle(getLang(LK_ROLE_AUTH));
            panel.add(panelInfo);
            panel.add(panelRole);
            this.comp = panel.getComp();
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
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
        }
        initTsBoxValues();
        initProfileBoxValues();
        setInitValue();
        roleGranteComp.refreshTable();

    }


    @Override
    public void initForm(String usrName) throws SQLException {
        if (conn == null) {
            return;
        }
        map.clear();
        roleGranteComp.setPermUsr(usrName);
        roleGranteComp.refreshTable();
        usrNameInput.setValue(usrName);
        usrNameInput.setEnabled(false);

        List<Map<String, String>> maps;
        maps = ServiceUtil.getTabSpaceDefault(conn, usrName);
        for (Map<String, String> map : maps) {
            defaultTsBox.setValue(map.get("default_tablespace"));
            tmpTsBox.setValue(map.get("temporary_tablespace"));
            profileBox.setValue(map.get("profile"));
        }


        map.put(UsrItem.usr_name, usrName);
        map.put(UsrItem.usr_pwd, pwInput.getValue());
        map.put(UsrItem.default_table_space, defaultTsBox.getValue());
        map.put(UsrItem.tmp_table_space, tmpTsBox.getValue());
        map.put(UsrItem.profile, profileBox.getValue());

    }

    @Override
    public String getSql() throws Exception {
        String rolName = usrNameInput.getValue();
        String passwords = pwInput.getValue();
        HashMap<UsrItem, String> newMap = new HashMap<>();
        newMap.put(UsrItem.usr_name, rolName);
        newMap.put(UsrItem.usr_pwd, passwords);
        newMap.put(UsrItem.default_table_space, defaultTsBox.getValue());
        newMap.put(UsrItem.tmp_table_space, tmpTsBox.getValue());
        newMap.put(UsrItem.profile, profileBox.getValue());
        StringBuffer buffer = new StringBuffer();
        if (isUpdate) {
            newMap.put(UsrItem.usr_name, DbCmdStrUtil.toDbCmdStr(rolName, this.dbType));
            buffer.append(usrMr.getUpdateSql(UsrFormType.usr, map, newMap));
        } else {
            buffer.append(usrMr.getCreateSql(UsrFormType.usr, newMap));
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
        return new ResultBean(0, getLang(LK_VALIDATE_OK));
    }


    /**
     * 初始化表空间和概要文件
     */
    private void setInitValue() throws SQLException {
        defaultTsBox.setValue(getDefaultValue(SpaceType.PERMANENT));
        tmpTsBox.setValue(getDefaultValue(SpaceType.TEMPORARY));
        profileBox.setValue("DEFAULT");

    }

    /**
     * 获取默认表空间名字
     */
    private String getDefaultValue(SpaceType type) throws SQLException {
        List<Map<String, String>> maps = ServiceUtil.getTabSpaceDefault(conn, type);
        return maps.isEmpty() ? "" : maps.get(0).get("property_value");
    }

    /**
     * 设置用户的表空间
     */
    private void initTsBoxValues() throws Exception {
        defaultTsBox.removeAllItems();
        tmpTsBox.removeAllItems();
        List<Map<String, String>> permentMaps = ServiceUtil.getTabSpace(conn, SpaceType.PERMANENT);
        List<Map<String, String>> tempMaps = ServiceUtil.getTabSpace(conn, SpaceType.TEMPORARY);
        for (Map<String, String> map : permentMaps) {
            defaultTsBox.addOption(map.get("tablespace_name"), map.get("tablespace_name"));
        }
        for (Map<String, String> map : tempMaps) {
            tmpTsBox.addOption(map.get("tablespace_name"), map.get("tablespace_name"));
        }

    }

    /**
     * 设置用户的概要文件
     */
    private void initProfileBoxValues() throws SQLException {
        profileBox.removeAllItems();
        List<Map<String, String>> maps = ServiceUtil.getProfile(conn);
        for (Map<String, String> map : maps) {
            profileBox.addOption(map.get("profile"), map.get("profile"));
        }

    }


    @Override
    public Dimension getSize() {
        return new Dimension(780, 660);
    }
}
