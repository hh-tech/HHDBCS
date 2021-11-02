package com.hh.hhdb_admin.mgr.login;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.ViewType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangEnum;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.event.HHEventUtil;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.PasswordInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.util.StartUtil;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.Locale;

public abstract class LoginForm {
    protected LoginInput nameLabelInput;
    private final LoginInput viewLabelInput;
    final LoginInput langLabelInput;
    private final LoginInput typeLabelInput;
    private final AbsInput connInput;
    private final AbsInput connNameInput;
    protected LoginInput connLabelInput;
    protected LoginInput userLabelInput;
    protected LoginInput passLabelInput;
    private final LoginInput schemaLabelInput;
    private final LoginInput userTypeLabelInput;
    private final LoginInput defaultSaveInput;
    private final SelectBox langSelect;
    private final SelectBox viewSelect;
    private final SelectBox typeSelect;
    private final CheckBoxInput isSysAdmin;
    private final HPanel formPanel;
    private final HButton tempBtn;
    public LangEnum oldLang = StartUtil.default_language;
    private final LastPanel lastPanel;

    protected LoginForm() {
        lastPanel = new LastPanel();
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(10);
        divLayout.setTopHeight(10);
        formPanel = new HPanel(divLayout);
        connNameInput = getTextInput("conn_name");
        nameLabelInput = new LoginInput(LoginComp.getLang("conn_name"), connNameInput);
        viewSelect = new SelectBox("mgr_model");
        viewSelect.addOption(LoginComp.getLang("user_view"), ViewType.USER.name());
        viewSelect.addOption(LoginComp.getLang("dba_view"), ViewType.DBA.name());
        viewLabelInput = new LoginInput(LoginComp.getLang("conn_view"), viewSelect);
        langSelect = new SelectBox("lang");
        langSelect.addOption(LoginComp.getLang("ZH"), LangEnum.ZH.name());
        langSelect.addOption(LoginComp.getLang("EN"), LangEnum.EN.name());
        langSelect.setValue(StartUtil.default_language.name());
        langSelect.addListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (null != langLabelInput) {
                    String v = langLabelInput.getValue();
                    if (v != null) {
                        StartUtil.default_language = LangEnum.valueOf(langSelect.getValue());
                        LangMgr2.setDefaultLang(LangEnum.valueOf(v));
                        System.out.println("当前语言" + v);
                        StartUtil.setLocale(LangEnum.valueOf(v));
                        JComponent.setDefaultLocale(Locale.getDefault());
                        setLang();
                    }
                }
            }
        });
        langLabelInput = new LoginInput(LoginComp.getLang("language"), langSelect);

        isSysAdmin = new CheckBoxInput("is_admin");
        isSysAdmin.setText(LoginComp.getLang("sys_dba"));
        userTypeLabelInput = new LoginInput(LoginComp.getLang("user_type"), isSysAdmin);
        typeSelect = new SelectBox("db_type") {
            @Override
            protected void onItemChange(ItemEvent e) {
                if (typeLabelInput != null) {
                    HPanel panel = new HPanel();
                    if (DBTypeEnum.oracle.equals(DBTypeEnum.valueOf(typeLabelInput.getValue()))) {
                        panel.add(userTypeLabelInput, defaultSaveInput);
                    } else {
                        panel.add(defaultSaveInput);
                    }
                    lastPanel.set(panel.getComp());
                    template();
                }
            }
        };
        StartUtil.supportDbTypeList.forEach(item -> typeSelect.addOption(LoginComp.getLang(item), item));
        typeLabelInput = new LoginInput(LoginComp.getLang("db_type"), typeSelect);
        connInput = new TextInput("db_url");
        tempBtn = new HButton(LoginComp.getLang("template")) {
            @Override
            protected void onClick() {
                template();
            }
        };
        tempBtn.setIcon(LoginComp.getIcon("template"));
        connLabelInput = new LoginInput(LoginComp.getLang("db_url"), connInput, tempBtn);
        userLabelInput = new LoginInput(LoginComp.getLang("db_user"), getTextInput("db_user"));
        passLabelInput = new LoginInput(LoginComp.getLang("db_pass"), getTextInput("db_pass"));
        schemaLabelInput = new LoginInput(LoginComp.getLang("db_schema"), getTextInput("db_schema"));
        CheckBoxInput defaultSave = new CheckBoxInput("");
        defaultSave.setValue("true");
        defaultSaveInput = new LoginInput(LoginComp.getLang("defaultSave"), defaultSave);
        HPanel panel = new HPanel();
        panel.add(defaultSaveInput);
        lastPanel.set(panel.getComp());
        HHEventUtil.addUndoableEvent((JTextComponent) connNameInput.getComp(), (JTextComponent) connInput.getComp(),
                (JTextComponent) userLabelInput.getInput().getComp(),
                (JTextComponent) passLabelInput.getInput().getComp(),
                (JTextComponent) schemaLabelInput.getInput().getComp()
        );
        template();
    }

    /**
     * 添加输入框回车监听
     *
     * @param action 监听事件
     */
    protected void addInputsEvents(Action action) {
        HHEventUtil.addEnterEvent(action, (JTextField) connNameInput.getComp(), (JTextField) connInput.getComp(),
                (JTextField) userLabelInput.getInput().getComp(),
                (JTextField) passLabelInput.getInput().getComp(),
                (JTextField) schemaLabelInput.getInput().getComp()
        );
    }

    protected HPanel getFormPanel() {
        if (typeLabelInput != null) {
            formPanel.add(nameLabelInput, viewLabelInput, langLabelInput);
            formPanel.add(typeLabelInput, connLabelInput, userLabelInput);
            formPanel.add(passLabelInput, schemaLabelInput);
            formPanel.setLastPanel(lastPanel);
        }
        return formPanel;
    }

    protected JsonObject getData(boolean flag) {
        JsonObject dataJson = new JsonObject();
        String connName = nameLabelInput.getValue();
        dataJson.add("conn_name", connName)
                .add("db_view", viewLabelInput.getValue())
                .add("db_type", typeLabelInput.getValue())
                .add("db_clazz", DriverUtil.getDriverClass(typeLabelInput.getValue()))
                .add("db_url", connLabelInput.getValue())
                .add("db_pass", passLabelInput.getValue())
                .add("db_role", userTypeLabelInput.getValue())
                .add("db_user", flag ? userLabelInput.getValue() : LoginUtil.getRealName(userLabelInput.getValue(), typeLabelInput.getValue()))
                .add("db_schema", flag ? schemaLabelInput.getValue() : LoginUtil.getRealName(schemaLabelInput.getValue(), typeLabelInput.getValue()))
                .add("is_save", defaultSaveInput.getValue())
                .add("is_admin", userTypeLabelInput.getValue());
        return dataJson;
    }

    protected void reset() {
        nameLabelInput.setValue(null);
        connLabelInput.setValue(null);
        userLabelInput.setValue(null);
        passLabelInput.setValue(null);
        schemaLabelInput.setValue(null);
    }

    protected abstract void changeBtnStatus();

    protected abstract void setLang();

    protected AbsInput getConnNameInput() {
        return connNameInput;
    }

    protected void updateFormData(JsonObject data) {
        nameLabelInput.setValue(data.getString("conn_name"));
        typeLabelInput.setValue(data.getString("db_type"));
        connLabelInput.setValue(data.getString("db_url"));
        userLabelInput.setValue(data.getString("db_user"));
        passLabelInput.setValue(data.getString("db_pass"));
        schemaLabelInput.setValue(data.getString("db_schema"));
        userTypeLabelInput.setValue(data.getString("is_admin"));
    }

    protected void template() {
        String dbType = typeLabelInput.getValue();
        connLabelInput.setValue(DriverUtil.getDriverUrl(DBTypeEnum.valueOf(dbType)));
    }

    protected void resetLang() {
        String v = langLabelInput.getValue();
        langSelect.addOption(LoginComp.getLang("ZH"), LangEnum.ZH.name());
        langSelect.addOption(LoginComp.getLang("EN"), LangEnum.EN.name());
        langSelect.setValue(v);

        v = viewSelect.getValue();
        viewSelect.addOption(LoginComp.getLang("user_view"), ViewType.USER.name());
        viewSelect.addOption(LoginComp.getLang("dba_view"), ViewType.DBA.name());
        if (v != null) {
            viewSelect.setValue(v);
        }
        v = typeSelect.getValue();
        StartUtil.supportDbTypeList.forEach(item -> typeSelect.addOption(LoginComp.getLang(item), item));
        if (v != null) {
            typeSelect.setValue(v);
        }
        isSysAdmin.setText(LoginComp.getLang("sys_dba"));
        nameLabelInput.setLabel(LoginComp.getLang("conn_name"));
        viewLabelInput.setLabel(LoginComp.getLang("conn_view"));
        langLabelInput.setLabel(LoginComp.getLang("language"));
        typeLabelInput.setLabel(LoginComp.getLang("db_type"));
        connLabelInput.setLabel(LoginComp.getLang("db_url"));
        tempBtn.setText(LoginComp.getLang("template"));
        userLabelInput.setLabel(LoginComp.getLang("db_user"));
        passLabelInput.setLabel(LoginComp.getLang("db_pass"));
        schemaLabelInput.setLabel(LoginComp.getLang("db_schema"));
        userTypeLabelInput.setLabel(LoginComp.getLang("user_type"));
        defaultSaveInput.setLabel(LoginComp.getLang("defaultSave"));
    }

    private AbsInput getTextInput(String name) {
        AbsInput input;
        if ("db_pass".equals(name)) {
            input = new PasswordInput(name) {
                @Override
                protected void doChange() {
                    changeBtnStatus();
                }
            };
        } else {
            input = new TextInput(name) {
                @Override
                protected void doChange() {
                    changeBtnStatus();
                }
            };
        }
        return input;
    }

}
