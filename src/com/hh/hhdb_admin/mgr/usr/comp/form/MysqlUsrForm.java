package com.hh.hhdb_admin.mgr.usr.comp.form;

import com.hh.frame.create_dbobj.userMr.UsrMrUtil;
import com.hh.frame.create_dbobj.userMr.base.UsrFormType;
import com.hh.frame.create_dbobj.userMr.base.UsrItem;
import com.hh.frame.dbobj2.mysql.MysqlUser;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.PasswordInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MysqlUsrForm extends UsrBaseForm {
    private final TextInput usrNameInput;
    private final PasswordInput pwInput;
    private final PasswordInput rePwInput;

    private final static String LK_CON_CONS = "CON_CONS";
    private final static String LK_MAX_UPD = "MAX_UPD";
    private final static String LK_HOST_LIMIT = "HOST_LIMIT";
    private final static String LK_PASSWORD_NOT_EQUAL = "PASSWORD_NOT_EQUAL";
    private final static String LK_USER_NAME = "USER_NAME";

    private final static String LK_USER_INFO = "USER_INFO";
    private final static String LK_MAX_QUERY = "MAX_QUERY";
    private final static String LK_MAX_CONN = "MAX_CONN";
    private final static String LK_VALIDATE_OK = "VALIDATE_OK";
    private final static String LK_RE_PWD = "RE_PWD";
    private final static String LK_PLEASE_ENTER_USER_NAME = "PLEASE_ENTER_USER_NAME";
    private final static String LK_PWD = "PWD";
    private final static String LK_ACCOUNT_LIMIT = "ACCOUNT_LIMIT";

    private final HashMap<UsrItem, String> map = new HashMap<>();


    private final TextInput hostLimitInput;
    private final TextInput maxQueInput;
    private final TextInput maxConInput;
    private final TextInput maxUpdateInput;
    private final TextInput conConInput;
    private boolean isUpdate;


    public MysqlUsrForm(Connection conn) {
        super(conn);

        HPanel panel = new HPanel(new HDivLayout(0, 10, GridSplitEnum.C12));

        //基础信息
        HPanel panelInfo = new HPanel(new HDivLayout(0, 15, GridSplitEnum.C12));
        usrNameInput = new TextInput(UsrItem.usr_name.name());
        pwInput = new PasswordInput(UsrItem.usr_pwd.name());
        rePwInput = new PasswordInput(UsrItem.usr_rePwd.name());
        hostLimitInput = new TextInput(UsrItem.host_limit.name());
        panelInfo.add(new HeightComp(10));
        panelInfo.add(getLabelInput(getLang(LK_USER_NAME), usrNameInput));
        panelInfo.add(getLabelInput(getLang(LK_HOST_LIMIT), hostLimitInput));
        panelInfo.add(getLabelInput(getLang(LK_PWD), pwInput));
        panelInfo.add(getLabelInput(getLang(LK_RE_PWD), rePwInput));

        panelInfo.add(new HeightComp(10));
        panelInfo.setTitle(getLang(LK_USER_INFO));


        //账户限制信息
        HPanel accountLimit = new HPanel(new HDivLayout(0, 15, GridSplitEnum.C12));
        accountLimit.setTitle(getLang(LK_ACCOUNT_LIMIT));
        maxQueInput = new TextInput(UsrItem.max_query.name());
        maxConInput = new TextInput(UsrItem.max_cons.name());
        maxUpdateInput = new TextInput(UsrItem.max_update.name());
        conConInput = new TextInput(UsrItem.con_cons.name());
        accountLimit.add(getLabelInput(getLang(LK_MAX_CONN), maxConInput));
        accountLimit.add(getLabelInput(getLang(LK_MAX_QUERY), maxQueInput));
        accountLimit.add(getLabelInput(getLang(LK_CON_CONS), conConInput));
        accountLimit.add(getLabelInput(getLang(LK_MAX_UPD), maxUpdateInput));
        panel.add(panelInfo);
        panel.add(accountLimit);
        this.comp = panel.getComp();
    }

    @Override
    public void clearForm() {
        if (isUpdate) {
            pwInput.setValue("");
            rePwInput.setValue("");
        } else {
            usrNameInput.setEnabled(true);
            hostLimitInput.setValue("");
            pwInput.setValue("");
            rePwInput.setValue("");
            maxQueInput.setValue("");
            maxConInput.setValue("");
            maxUpdateInput.setValue("");
            conConInput.setValue("");
        }

    }


    @Override
    public void initForm(String usrName) throws Exception {
        if (conn == null) {
            return;
        }
        usrNameInput.setValue(UsrMrUtil.subMysqlUsr(usrName));
        hostLimitInput.setValue(UsrMrUtil.subMysqlHost(usrName));
        MysqlUser user = new MysqlUser(conn);
        Map<String, String> props = user.getUserProp(usrName);
        if (props.isEmpty()) {
            throw new Exception("用户不存在");
        }
        maxQueInput.setValue(props.get("max_questions"));
        maxUpdateInput.setValue(props.get("max_updates"));
        maxConInput.setValue(props.get("max_connections"));
        conConInput.setValue(props.get("max_user_connections"));
        map.put(UsrItem.usr_name, usrNameInput.getValue());
        map.put(UsrItem.usr_pwd, pwInput.getValue());
        map.put(UsrItem.host_limit, hostLimitInput.getValue());
        map.put(UsrItem.max_cons, maxConInput.getValue());
        map.put(UsrItem.max_query, maxQueInput.getValue());
        map.put(UsrItem.max_update, maxUpdateInput.getValue());
        map.put(UsrItem.con_cons, conConInput.getValue());

    }

    @Override
    public String getSql() {
        String rolName = usrNameInput.getValue();
        String passwords = pwInput.getValue();
        HashMap<UsrItem, String> newMap = new HashMap<>();
        newMap.put(UsrItem.usr_name, rolName);
        newMap.put(UsrItem.usr_pwd, passwords);
        newMap.put(UsrItem.host_limit, hostLimitInput.getValue());
        newMap.put(UsrItem.max_query, maxQueInput.getValue());
        newMap.put(UsrItem.max_cons, maxConInput.getValue());
        newMap.put(UsrItem.max_update, maxUpdateInput.getValue());
        newMap.put(UsrItem.con_cons, conConInput.getValue());
        StringBuilder buffer = new StringBuilder();
        if (isUpdate) {
            buffer.append(Objects.requireNonNull(usrMr).getUpdateSql(UsrFormType.usr, map, newMap));
        } else {
            buffer.append(Objects.requireNonNull(usrMr).getCreateSql(UsrFormType.usr, newMap));
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
        }
        if (!rePwInput.getValue().equals(pwInput.getValue())) {
            return new ResultBean(-1, getLang(LK_PASSWORD_NOT_EQUAL));
        }
        return new ResultBean(0, getLang(LK_VALIDATE_OK));
    }


}
