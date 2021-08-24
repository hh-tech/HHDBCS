package com.hh.hhdb_admin.mgr.login;

import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.swingui.event.HHEventUtil;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.PasswordInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.fc.FileChooserInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ouyangxu
 * @date 2021-06-11 0011 15:48:48
 */
public class SshLoginComp extends HPanel {

    protected static final String SSH_CONFIG = "ssh_config";
    protected static final String SSH_ENABLED = "ssh_enabled";
    protected static final String SSH_HOST = "ssh_host";
    protected static final String SSH_PORT = "ssh_port";
    protected static final String SSH_USER = "ssh_user";
    protected static final String SSH_TYPE = "ssh_type";
    protected static final String SSH_PRIVATE_KEY = "ssh_private_key";
    protected static final String SSH_PRIVATE_KEY_PWD = "ssh_private_key_pwd";
    protected static final String SSH_PASSWORD = "ssh_password";
    protected static final String SSH_PUBLIC_KEY = "ssh_public_key";
    private static final String STR = "* : ";

    private List<AbsInput> inputsList;
    private CheckBoxInput enabledBox;
    private HPanel formPanel;

    private SshLoginBen sshLoginBen;
    private JsonObject jsonData;
    private LoginInput hostWithInput;
    private LoginInput portWithInput;
    private LoginInput userWithInput;

    private LoginInput pwdWithInput;
    private LoginInput keyPwdWithInput;

    private Action action;

    private FileChooserInput keyFileInput;
    private LoginInput keyFileWithInput;
    private SelectBox sshTypeBox;
    private LoginInput sshTypeWithInput;
    private TextInput portInput;

    public static void main(String[] args) throws Exception {
        HHSwingUi.init();
        HFrame hFrame = new HFrame(HFrame.SMALL_WIDTH);
        SshLoginComp sshLoginComp = new SshLoginComp(new SshLoginBen("192.168.2.1", 22, "root", "123456"));
        hFrame.setRootPanel(sshLoginComp);
        hFrame.show();
    }

    public SshLoginComp(SshLoginBen sshLoginBen) {
        this.sshLoginBen = sshLoginBen;
        init(sshLoginBen);
    }

    public void initFromJson(JsonObject jsonData) {
        this.jsonData = jsonData;
        if (jsonData != null) {
            sshLoginBen = new SshLoginBen();
            sshLoginBen.setEnabled(jsonData.get(SshLoginComp.SSH_ENABLED).asBoolean());
            JsonValue value = jsonData.get(SshLoginComp.SSH_HOST);
            sshLoginBen.setHost(jsonValueIsNull(value) ? value.asString() : null);
            value = jsonData.get(SshLoginComp.SSH_PORT);
            sshLoginBen.setPort(jsonValueIsNull(value) ? value.asInt() : 22);
            value = jsonData.get(SshLoginComp.SSH_USER);
            sshLoginBen.setUser(jsonValueIsNull(value) ? value.asString() : null);
            value = jsonData.get(SshLoginComp.SSH_TYPE);
            SshLoginBen.SshType sshType = SshLoginBen.SshType.PASSWORD;
            if (value != null) {
                sshType = SshLoginBen.SshType.valueOf(value.asString());
            }
            sshLoginBen.setType(sshType);
            if (sshType == SshLoginBen.SshType.PASSWORD) {
                value = jsonData.get(SshLoginComp.SSH_PASSWORD);
                sshLoginBen.setPassword(jsonValueIsNull(value) ? value.asString() : null);
            } else {
                sshLoginBen.setPrivateKey(jsonData.get(SshLoginComp.SSH_PRIVATE_KEY).asString());
                value = jsonData.get(SshLoginComp.SSH_PRIVATE_KEY_PWD);
                sshLoginBen.setKeyPassphrase(jsonValueIsNull(value) ? value.asString() : null);
            }
        } else {
            sshLoginBen = null;
        }
        init(sshLoginBen);
    }

    public JsonObject getJsonData() {
        sshLoginBen = getSshLoginBen();
        jsonData = new JsonObject();
        jsonData.set(SshLoginComp.SSH_ENABLED, sshLoginBen.isEnabled());
        jsonData.set(SshLoginComp.SSH_HOST, sshLoginBen.getHost());
        jsonData.set(SshLoginComp.SSH_PORT, sshLoginBen.getPort());
        jsonData.set(SshLoginComp.SSH_USER, sshLoginBen.getUser());
        jsonData.set(SshLoginComp.SSH_TYPE, sshLoginBen.getType().name());
        jsonData.set(SshLoginComp.SSH_PRIVATE_KEY, sshLoginBen.getPrivateKey());
        jsonData.set(SshLoginComp.SSH_PRIVATE_KEY_PWD, sshLoginBen.getKeyPassphrase());
        jsonData.set(SshLoginComp.SSH_PASSWORD, sshLoginBen.getPassword());
        return jsonData;
    }

    private void init(SshLoginBen sshLoginBen) {
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setHead(initHead().getComp());
        initForm(sshLoginBen);
        lastPanel.set(formPanel.getComp());
        setLastPanel(lastPanel);
    }

    private HPanel initHead() {
        HDivLayout divLayout = new HDivLayout();
        divLayout.setTopHeight(10);
        divLayout.setBottomHeight(15);
        HPanel hPanel = new HPanel(divLayout);
        enabledBox = new CheckBoxInput(SSH_ENABLED, LoginComp.getLang(SSH_ENABLED));
        enabledBox.addListen(e -> {
            for (AbsInput input : inputsList) {
                input.getComp().setEnabled(enabledBox.isChecked());
            }
            keyFileInput.setEnabled(enabledBox.isChecked());
            JTextField hostInput = (JTextField) inputsList.get(0).getComp();
            hostInput.requestFocus();
            SwingUtilities.invokeLater(() -> hostInput.setCaretPosition(hostInput.getText().length()));
        });
        hPanel.add(enabledBox);
        return hPanel;
    }

    private void initForm(SshLoginBen sshLoginBen) {
        inputsList = new ArrayList<>();
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(17);
        formPanel = new HPanel(divLayout);
        formPanel.setTitle("SSH" + LoginComp.getLang(SSH_CONFIG));

        TextInput hostInput = new TextInput(SSH_HOST);
        hostWithInput = new LoginInput(LoginComp.getLang(SSH_HOST) + STR, hostInput);

        portInput = new TextInput(SSH_PORT);
        portInput.setValue("22");
        portWithInput = new LoginInput(LoginComp.getLang(SSH_PORT) + STR, portInput);

        TextInput userInput = new TextInput(SSH_USER);
        userWithInput = new LoginInput(LoginComp.getLang(SSH_USER) + STR, userInput);

        sshTypeBox = new SelectBox(SSH_TYPE);
        sshTypeBox.addOption(LoginComp.getLang(SSH_PASSWORD), SshLoginBen.SshType.PASSWORD.name());
        sshTypeBox.addOption(LoginComp.getLang(SSH_PUBLIC_KEY), SshLoginBen.SshType.PUBLIC_KEY.name());
        sshTypeWithInput = new LoginInput(LoginComp.getLang(SSH_TYPE) + STR, sshTypeBox);

        PasswordInput pwdInput = new PasswordInput(SSH_PASSWORD);
        pwdWithInput = new LoginInput(LoginComp.getLang(SSH_PASSWORD) + "   : ", pwdInput);

        keyFileInput = new FileChooserInput(SSH_PRIVATE_KEY);
        keyFileWithInput = new LoginInput(LoginComp.getLang(SSH_PRIVATE_KEY) + STR, keyFileInput);

        PasswordInput keyPwdInput = new PasswordInput(SSH_PRIVATE_KEY_PWD);
        keyPwdWithInput = new LoginInput(LoginComp.getLang(SSH_PRIVATE_KEY_PWD) + "   : ", keyPwdInput);
        sshTypeBox.addListener(e -> changeTypeSelect());

        formPanel.add(hostWithInput, portWithInput, userWithInput, sshTypeWithInput);

        if (sshLoginBen != null) {
            hostInput.setValue(sshLoginBen.getHost() != null ? sshLoginBen.getHost() : "");
            portInput.setValue(sshLoginBen.getPort() > 0 ? String.valueOf(sshLoginBen.getPort()) : "22");
            userInput.setValue(sshLoginBen.getUser() != null ? sshLoginBen.getUser() : "");
            pwdInput.setValue(sshLoginBen.getPassword() != null ? sshLoginBen.getPassword() : "");
            keyFileInput.setValue(sshLoginBen.getPrivateKey() != null ? sshLoginBen.getPrivateKey() : "");
            keyPwdInput.setValue(sshLoginBen.getKeyPassphrase() != null ? sshLoginBen.getKeyPassphrase() : "");
            enabledBox.getComp().setSelected(sshLoginBen.isEnabled());
            if (sshLoginBen.isEnabled()) {
                sshTypeBox.setValue(sshLoginBen.getType().name());
            }
            changeTypeSelect();
        } else {
            formPanel.add(pwdWithInput);
        }

        inputsList.addAll(Arrays.asList(hostInput, portInput, userInput, sshTypeBox, pwdInput, keyFileInput, keyPwdInput));
        inputsList.forEach(absInput -> {
            absInput.setEnabled(sshLoginBen != null && sshLoginBen.isEnabled());
            if (absInput.getComp() instanceof JTextComponent) {
                HHEventUtil.addUndoableEvent((JTextComponent) absInput.getComp());
            }
        });
        if (action != null) {
            addInputsEvents(action);
        }
    }

    /**
     * 添加输入框回车监听
     *
     * @param action 监听事件
     */
    public void addInputsEvents(Action action) {
        this.action = action;
        inputsList.forEach(absInput -> {
            if (absInput.getComp() instanceof JTextField) {
                HHEventUtil.addEnterEvent(action, (JTextField) absInput.getComp());
            }
        });
    }

    public SshLoginBen getSshLoginBen() {
        if (sshLoginBen == null) {
            sshLoginBen = new SshLoginBen();
            sshLoginBen.setType(SshLoginBen.SshType.PASSWORD);
        }
        if (!enabledBox.isChecked()) {
            sshLoginBen.setEnabled(false);
            return sshLoginBen;
        }
        String type = sshTypeBox.getValue();
        boolean isKey = type.equalsIgnoreCase(SshLoginBen.SshType.PUBLIC_KEY.name());
        for (AbsInput absInput : inputsList) {
            String value = absInput.getValue();
            if (value == null) {
                continue;
            }
            value = value.trim();
            switch (absInput.getId()) {
                case SSH_HOST:
                    sshLoginBen.setHost(value);
                    break;
                case SSH_PORT:
                    if (StringUtils.isBlank(value) || !LoginComp.pattern.matcher(value).matches()) {
                        throw new NumberFormatException(" Port Error");
                    }
                    sshLoginBen.setPort(Integer.parseInt(value));
                    break;
                case SSH_USER:
                    sshLoginBen.setUser(value);
                    break;
                case SSH_TYPE:
                    sshLoginBen.setType(SshLoginBen.SshType.valueOf(value));
                    break;
                case SSH_PRIVATE_KEY:
                    sshLoginBen.setPrivateKey(isKey ? value : "");
                    break;
                case SSH_PRIVATE_KEY_PWD:
                    sshLoginBen.setKeyPassphrase(isKey ? value : "");
                    break;
                case SSH_PASSWORD:
                    sshLoginBen.setPassword(isKey ? "" : value);
                    break;
                default:
            }
        }
        sshLoginBen.setEnabled(enabledBox.getComp().isSelected());
        return sshLoginBen;
    }

    public void resLang() {
        enabledBox.setText(LoginComp.getLang(SSH_ENABLED));
        formPanel.setTitle("SSH" + LoginComp.getLang(SSH_CONFIG));
        hostWithInput.setLabel(LoginComp.getLang(SSH_HOST) + STR);
        portWithInput.setLabel(LoginComp.getLang(SSH_PORT) + STR);
        userWithInput.setLabel(LoginComp.getLang(SSH_USER) + STR);
        pwdWithInput.setLabel(LoginComp.getLang(SSH_PASSWORD) + STR);
        sshTypeWithInput.setLabel(LoginComp.getLang(SSH_TYPE) + STR);
        keyFileWithInput.setLabel(LoginComp.getLang(SSH_PRIVATE_KEY) + STR);
        keyPwdWithInput.setLabel(LoginComp.getLang(SSH_PRIVATE_KEY_PWD) + "   : ");
        sshTypeBox.addOption(LoginComp.getLang(SSH_PASSWORD), SshLoginBen.SshType.PASSWORD.name());
        sshTypeBox.addOption(LoginComp.getLang(SSH_PUBLIC_KEY), SshLoginBen.SshType.PUBLIC_KEY.name());
    }

    public void reset() {
        inputsList.forEach(absInput -> {
            if (absInput.getComp() instanceof JTextComponent) {
                absInput.setValue("");
            }
        });
        sshTypeBox.setValue(SshLoginBen.SshType.PASSWORD.name());
        portInput.setValue("22");
        changeTypeSelect();
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    private void changeTypeSelect() {
        String type = sshTypeBox.getValue();
        boolean isKey = type.equalsIgnoreCase(SshLoginBen.SshType.PUBLIC_KEY.name());
        if (isKey) {
            formPanel.remove(pwdWithInput);
            formPanel.add(keyFileWithInput, keyPwdWithInput);
        } else {
            formPanel.remove(keyFileWithInput, keyPwdWithInput);
            formPanel.add(pwdWithInput);
        }
        formPanel.updateUI();
    }

    private boolean jsonValueIsNull(JsonValue value) {
        if (value == null || value.isNull()) {
            return false;
        }
        return !StringUtils.isBlank(value.toPrettyString());
    }
}
