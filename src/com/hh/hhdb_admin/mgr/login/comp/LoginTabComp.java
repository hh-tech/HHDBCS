package com.hh.hhdb_admin.mgr.login.comp;

import com.alee.laf.button.WebButton;
import com.alee.managers.style.StyleId;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.ViewType;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.PasswordInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.fc.FileChooserInput;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginUtil;
import com.hh.hhdb_admin.mgr.login.base.DatabaseTypeComboBox;
import com.hh.hhdb_admin.mgr.login.base.ExpandTextInput;
import com.hh.hhdb_admin.mgr.login.base.LoginConnMsg;
import com.hh.hhdb_admin.mgr.login.base.TestInput;
import com.hh.hhdb_admin.mgr.table.common.TableUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class LoginTabComp extends CommonComp {

	private HPanel generalPanel;
	private HPanel sshPanel;

	public TestInput nameInput;
	private TestInput viewInput;
	private TestInput typeInput;
	private TestInput connectInput;
	private TestInput usernameInput;
	private TestInput passwordInput;
	private CheckBoxInput encryptInput;
	private TestInput schemaInput;
	private TestInput userTypeInput;

	private SelectBox viewBox;
	private DatabaseTypeComboBox typeBox;
	private SelectBox sshTypeBox;

	private CheckBoxInput enabledInput;
	private TestInput hostInput;
	private TestInput portInput;
	private TestInput sshUsernameInput;
	private TestInput sshTypeInput;
	private CheckBoxInput sshEncryptedInput;
	private TestInput sshPasswordInput;
	private TestInput privateKeyInput;
	private CheckBoxInput privateEncryptedInput;
	private TestInput privatePasswordInput;
	private final HTabPanel tabPane;
	private List<TestInput> inputList;

	public enum SshTypeEnum {

		/**
		 * 密码,公钥
		 */
		PASSWORD, PUBLIC_KEY

	}

	public LoginTabComp() {
		this.tabPane = new HTabPanel();
		HeaderConfig generalHeaderConfig = TableUtil.newHeaderConfig(getLang("general"));
		generalHeaderConfig.setTabIcon(getIcon("general"));

		HeaderConfig sshHeaderConfig = TableUtil.newHeaderConfig("SSH");
		sshHeaderConfig.setTabIcon(getIcon("ssh"));

		tabPane.addPanel("ssh", getSshPanel(), sshHeaderConfig);
		tabPane.addPanel("general", getGeneralPanel(), generalHeaderConfig);
		tabPane.selectPanel("general");

		inputList = Arrays.asList(nameInput, connectInput, usernameInput, passwordInput, schemaInput,
				hostInput, portInput, sshUsernameInput, sshPasswordInput);
	}

	public List<TestInput> getInputList() {
		return inputList;
	}

	public HTabPanel getTabPane() {
		return tabPane;
	}

	public void setValues(LoginConnMsg connMsg) {
		nameInput.setValue(connMsg.getConnName());
		viewInput.setValue(connMsg.getView());
		typeInput.setValue(connMsg.getType());
		connectInput.setValue(connMsg.getConnect());
		usernameInput.setValue(connMsg.getUsername());
		passwordInput.setValue(connMsg.getPassword());
		encryptInput.setValue(String.valueOf(connMsg.isEncrypted()));
		passwordInput.setEnable(!connMsg.isEncrypted());
		((PasswordInput) passwordInput.getInput()).setShowEye(!connMsg.isEncrypted());
		encryptInput.setEnabled(!connMsg.isEncrypted());
		schemaInput.setValue(connMsg.getSchema());
		userTypeInput.setValue(String.valueOf(connMsg.isUserType()));
		enabledInput.setValue(String.valueOf(connMsg.isEnabled()));
		hostInput.setValue(connMsg.getHost());
		portInput.setValue(connMsg.getPort());
		sshUsernameInput.setValue(connMsg.getSshUsername());
		sshTypeInput.setValue(connMsg.getSshType());
		sshPasswordInput.setValue(connMsg.getSshPassword());
		privateKeyInput.setValue(connMsg.getPrivateKey());
		privatePasswordInput.setValue(connMsg.getPrivatePassword());
		sshEncryptedInput.setValue(String.valueOf(connMsg.isSshEncrypted()));
		privateEncryptedInput.setValue(String.valueOf(connMsg.isSshEncrypted()));
		if (connMsg.isEnabled()) {
			hostInput.setEnable(true);
			portInput.setEnable(true);
			sshUsernameInput.setEnable(true);
			sshTypeInput.setEnable(true);
			if (connMsg.isSshEncrypted()) {
				sshEncryptedInput.setEnabled(false);
				privateEncryptedInput.setEnabled(false);
				sshPasswordInput.setEnable(false);
				privatePasswordInput.setEnable(false);
				((PasswordInput) sshPasswordInput.getInput()).setShowEye(false);
				((PasswordInput) privatePasswordInput.getInput()).setShowEye(false);
			} else {
				sshEncryptedInput.setEnabled(true);
				privateEncryptedInput.setEnabled(true);
				((PasswordInput) sshPasswordInput.getInput()).setShowEye(true);
				((PasswordInput) privatePasswordInput.getInput()).setShowEye(true);
			}
		} else {
			hostInput.setEnable(false);
			portInput.setEnable(false);
			sshUsernameInput.setEnable(false);
			sshTypeInput.setEnable(false);
			privateKeyInput.setEnable(false);
			sshEncryptedInput.setEnabled(false);
			privateEncryptedInput.setEnabled(false);
			sshPasswordInput.setEnable(false);
			privatePasswordInput.setEnable(false);
			sshEncryptedInput.setEnabled(false);
			privateEncryptedInput.setEnabled(false);
			((PasswordInput) sshPasswordInput.getInput()).setShowEye(true);
			((PasswordInput) privatePasswordInput.getInput()).setShowEye(true);
		}
	}

	public LoginConnMsg getLoginConnMsg(boolean flag) {
		LoginConnMsg connMsg = new LoginConnMsg();
		connMsg.setConnName(nameInput.getValue());
		connMsg.setView(viewInput.getValue());
		connMsg.setType(typeInput.getValue());
		connMsg.setConnect(connectInput.getValue());
		connMsg.setUsername(flag ? usernameInput.getValue() : LoginUtil.getRealName(usernameInput.getValue(), typeInput.getValue()));
		connMsg.setEncrypted(encryptInput.isChecked());
		connMsg.setPassword(passwordInput.getValue());
		if (encryptInput.isEnabled()) {
			if (encryptInput.isChecked()) {
				connMsg.encryptPassword();
			}
		}
		connMsg.setSchema(flag ? schemaInput.getValue() : LoginUtil.getRealName(schemaInput.getValue(), typeInput.getValue()));
		connMsg.setUserType(Boolean.parseBoolean(userTypeInput.getValue()));
		connMsg.setEnabled(enabledInput.isChecked());
		connMsg.setHost(hostInput.getValue());
		connMsg.setPort(portInput.getValue());
		connMsg.setSshUsername(sshUsernameInput.getValue());
		connMsg.setSshType(sshTypeInput.getValue());
		connMsg.setSshPassword(sshPasswordInput.getValue());
		connMsg.setPrivateKey(privateKeyInput.getValue());
		connMsg.setPrivatePassword(privatePasswordInput.getValue());
		if (SshTypeEnum.PASSWORD.name().equalsIgnoreCase(sshTypeInput.getValue())) {
			connMsg.setSshEncrypted(sshEncryptedInput.isChecked());
			if (sshEncryptedInput.isEnabled()) {
				if (sshEncryptedInput.isChecked()) {
					connMsg.encryptedSshPassword();
				}
			}
		} else {
			connMsg.setSshEncrypted(privateEncryptedInput.isChecked());
			if (privateEncryptedInput.isEnabled()) {
				if (privateEncryptedInput.isChecked()) {
					connMsg.encryptedPrivatePassword();
				}
			}
		}
		return connMsg;
	}

	public void resetLabel() {
		//generalPanel.setTitle(getLang("generalConfig"));
		nameInput.setLabel(getLang(NAME));
		viewInput.setLabel(getLang(VIEW));
		String v = viewBox.getValue();
		viewBox.addOption(getLang("user_view"), ViewType.USER.name());
		viewBox.addOption(getLang("dba_view"), ViewType.DBA.name());
		if (v != null) {
			viewBox.setValue(v);
		}
		typeInput.setLabel(getLang(TYPE));
		v = typeBox.getValue();
		String connect = connectInput.getValue();
		Map<String, String> stringMap = StartUtil.supportDbTypeList.stream().collect(Collectors.toMap(CommonComp::getLang, item -> item, (a, b) -> b, LinkedHashMap::new));
		typeBox.setOptionMap(stringMap);
		typeBox.getComp().setModel(new DefaultComboBoxModel<>(stringMap.keySet().toArray(new String[0])));
		if (v != null) {
			typeBox.setValue(v);
		}
		connectInput.setLabel(getLang(CONNECT));
		connectInput.setValue(connect);
		usernameInput.setLabel(getLang(USERNAME));
		passwordInput.setLabel(getLang(PASSWORD));
		schemaInput.setLabel(getLang(SCHEMA));
		userTypeInput.setLabel(getLang(USER_TYPE));
		//sshPanel.setTitle(getLang("sshConfig"));
		enabledInput.setText(getLang(ENABLED));
		hostInput.setLabel(getLang(HOST));
		portInput.setLabel(getLang(PORT));
		sshUsernameInput.setLabel(getLang(SSH_USERNAME));
		sshTypeInput.setLabel(getLang(SSH_TYPE));
		v = typeBox.getValue();
		sshTypeBox.addOption(getLang("password2"), SshTypeEnum.PASSWORD.name());
		sshTypeBox.addOption(getLang("publicKey"), SshTypeEnum.PUBLIC_KEY.name());
		if (v != null) {
			sshTypeBox.setValue(v);
		}
		sshPasswordInput.setLabel(getLang(SSH_PASSWORD));
		privateKeyInput.setLabel(getLang(PRIVATE_KEY));
		privatePasswordInput.setLabel(getLang(PRIVATE_PASSWORD));
		tabPane.updateTabName("general", getLang("general"));
	}

	protected abstract void changeBtnStatus(boolean enabled);

	protected void template() {
		String dbType = typeInput.getValue();
		connectInput.setValue(DriverUtil.getDriverUrl(DBTypeEnum.valueOf(dbType)));
	}

	private HPanel getGeneralPanel() {
		viewBox = new SelectBox("view");
		viewBox.addOption(getLang("user_view"), ViewType.USER.name());
		viewBox.addOption(getLang("dba_view"), ViewType.DBA.name());
		Map<String, String> stringMap = StartUtil.supportDbTypeList.stream().collect(Collectors.toMap(CommonComp::getLang, item -> item, (a, b) -> b, LinkedHashMap::new));
		nameInput = new TestInput(getLang(NAME), getInput(NAME));
		viewInput = new TestInput(getLang(VIEW), viewBox);
		typeBox = new DatabaseTypeComboBox(TYPE, e -> {
			if (typeInput != null) {
				if (DBTypeEnum.oracle.equals(DBTypeEnum.valueOf(typeInput.getValue()))) {
					generalPanel.add(userTypeInput);
				} else {
					generalPanel.remove(userTypeInput);
				}
				generalPanel.updateUI();
				template();
			}
		}, stringMap.keySet());
		typeBox.setOptionMap(stringMap);
		typeInput = new TestInput(getLang(TYPE), typeBox);
		WebButton templateBtn = new WebButton(StyleId.buttonIconHover, getIcon("template"));
		templateBtn.setCursor(Cursor.getDefaultCursor());
		templateBtn.addActionListener(e -> template());
		templateBtn.setToolTip(getLang("template"));
		connectInput = new TestInput(getLang(CONNECT), new ExpandTextInput(CONNECT, templateBtn));
		connectInput.setValue(DriverUtil.getDriverUrl(DBTypeEnum.valueOf(typeBox.getValue())));
		usernameInput = new TestInput(getLang(USERNAME), getInput(USERNAME));
		encryptInput = new CheckBoxInput(ENCRYPTED);
		encryptInput.setIcon(getIcon("unlock"));
		encryptInput.getComp().setToolTipText(getLang("unencrypted"));
		passwordInput = new TestInput(getLang(PASSWORD), getInput(PASSWORD), encryptInput);
		schemaInput = new TestInput(getLang(SCHEMA), getInput(SCHEMA));
		userTypeInput = new TestInput(getLang(USER_TYPE), new CheckBoxInput(USER_TYPE, getLang("sys_dba")));
		HPanel panel = new HPanel();
		generalPanel = new HPanel(new HDivLayout(0, 10));
		//generalPanel.setTitle(getLang("generalConfig"));
		generalPanel.add(new HeightComp(5), nameInput, viewInput, typeInput, connectInput, usernameInput, passwordInput, schemaInput);
		LastPanel lastPanel = new LastPanel();
		lastPanel.set(generalPanel.getComp());
		panel.setLastPanel(lastPanel);
		encryptInput.getComp().addItemListener(e -> {
			encryptInput.setIcon(getIcon(encryptInput.isChecked() ? "lock" : "unlock"));
			encryptInput.getComp().setToolTipText(getLang(encryptInput.isChecked() ? "encrypted" : "unencrypted"));
		});

		return panel;
	}

	private HPanel getSshPanel() {
		HPanel panel = new HPanel();
		enabledInput = new CheckBoxInput(ENABLED, getLang(ENABLED)) {

			@Override
			protected void onClick(ActionEvent e) {
				changeEnableInput();
				changeBtnStatus(isBtnEnabled());
			}
		};
		hostInput = new TestInput(getLang(HOST), getInput(HOST));
		portInput = new TestInput(getLang(PORT), getInput(PORT));
		sshUsernameInput = new TestInput(getLang(SSH_USERNAME), getInput(SSH_USERNAME));
		sshTypeBox = new SelectBox(SSH_TYPE);
		sshTypeBox.addOption(getLang("password2"), SshTypeEnum.PASSWORD.name());
		sshTypeBox.addOption(getLang("publicKey"), SshTypeEnum.PUBLIC_KEY.name());
		sshTypeInput = new TestInput(getLang(SSH_TYPE), sshTypeBox);
		sshEncryptedInput = new CheckBoxInput(SSH_ENCRYPTED);
		sshEncryptedInput.setIcon(getIcon("unlock"));
		sshEncryptedInput.getComp().setToolTipText(getLang("unencrypted"));
		sshPasswordInput = new TestInput(getLang(SSH_PASSWORD), getInput(SSH_PASSWORD), sshEncryptedInput);
		privateKeyInput = new TestInput(getLang(PRIVATE_KEY), new FileChooserInput(PRIVATE_KEY));
		privateEncryptedInput = new CheckBoxInput(SSH_ENCRYPTED);
		privateEncryptedInput.setIcon(getIcon("unlock"));
		privateEncryptedInput.getComp().setToolTipText(getLang("unencrypted"));
		privatePasswordInput = new TestInput(getLang(PRIVATE_PASSWORD), getInput(PRIVATE_PASSWORD), privateEncryptedInput);
		sshTypeBox.addListener(e -> changeTypeSelect());
		sshPanel = new HPanel(new HDivLayout(0, 10));
		//sshPanel.setTitle(getLang("sshConfig"));
		sshPanel.add(new HeightComp(5), hostInput, portInput, sshUsernameInput, sshTypeInput);
		LastPanel lastPanel = new LastPanel();
		HDivLayout headPanelLayout = new HDivLayout();
		headPanelLayout.setTopHeight(10);
		HPanel headPanel = new HPanel(headPanelLayout);
		headPanel.add(enabledInput);
		headPanel.add(new HeightComp(10));
		lastPanel.setHead(headPanel.getComp());
		lastPanel.set(sshPanel.getComp());
		panel.setLastPanel(lastPanel);
		changeTypeSelect();
		changeEnableInput();
		sshEncryptedInput.getComp().addItemListener(e -> {
			sshEncryptedInput.setIcon(getIcon(sshEncryptedInput.isChecked() ? "lock" : "unlock"));
			sshEncryptedInput.getComp().setToolTipText(getLang(sshEncryptedInput.isChecked() ? "encrypted" : "unencrypted"));
		});
		privateEncryptedInput.getComp().addItemListener(e -> {
			privateEncryptedInput.setIcon(getIcon(privateEncryptedInput.isChecked() ? "lock" : "unlock"));
			privateEncryptedInput.getComp().setToolTipText(getLang(privateEncryptedInput.isChecked() ? "encrypted" : "unencrypted"));
		});
		return panel;
	}

	private AbsInput getInput(String name) {
		AbsInput input;
		if (Arrays.asList(PASSWORD, SSH_PASSWORD, PRIVATE_PASSWORD).contains(name)) {
			input = new PasswordInput(name) {
				@Override
				protected void doChange() {
					changeBtnStatus(isBtnEnabled());
				}
			};
		} else {
			input = new TextInput(name) {
				@Override
				protected void doChange() {
					changeBtnStatus(isBtnEnabled());
				}
			};
		}
		return input;
	}

	public boolean isBtnEnabled() {
		boolean enabled = StringUtils.isNotEmpty(nameInput.getValue())
				&& StringUtils.isNotEmpty(connectInput.getValue())
				&& StringUtils.isNotEmpty(usernameInput.getValue())
				&& StringUtils.isNotEmpty(passwordInput.getValue());
		if (enabledInput.isChecked()) {
			boolean a = StringUtils.isNotEmpty(hostInput.getValue())
					&& StringUtils.isNotEmpty(portInput.getValue())
					&& StringUtils.isNotEmpty(sshUsernameInput.getValue());
			boolean b;
			if (SshTypeEnum.PUBLIC_KEY.name().equalsIgnoreCase(sshTypeInput.getValue())) {
				b = StringUtils.isNotEmpty(privateKeyInput.getValue())
						&& StringUtils.isNotEmpty(privatePasswordInput.getValue());
			} else {
				b = StringUtils.isNotEmpty(sshPasswordInput.getValue());
			}
			enabled = enabled && a && b;
		}
		return enabled;
	}

	private void changeEnableInput() {
		setNullAndEnabled(enabledInput.isChecked(), hostInput, portInput,
				sshUsernameInput, sshTypeInput, sshPasswordInput, privateKeyInput, privatePasswordInput);
		sshEncryptedInput.setEnabled(enabledInput.isChecked());
		privateEncryptedInput.setEnabled(enabledInput.isChecked());
	}

	private void changeTypeSelect() {
		String type = sshTypeInput.getValue();
		boolean isKey = SshTypeEnum.PUBLIC_KEY.name().equalsIgnoreCase(type);
		setNullAndEnabled(enabledInput.isChecked(), sshPasswordInput, privateKeyInput, privatePasswordInput);
		sshEncryptedInput.setEnabled(true);
		sshEncryptedInput.setValue("false");
		privateEncryptedInput.setEnabled(true);
		privateEncryptedInput.setValue("false");
		((PasswordInput) sshPasswordInput.getInput()).setShowEye(true);
		((PasswordInput) privatePasswordInput.getInput()).setShowEye(true);
		if (isKey) {
			sshPanel.remove(sshPasswordInput);
			sshPanel.add(privateKeyInput, privatePasswordInput);
		} else {
			sshPanel.remove(privateKeyInput, privatePasswordInput);
			sshPanel.add(sshPasswordInput);
		}
		sshPanel.updateUI();
	}

	private void setNullAndEnabled(boolean enabled, TestInput... inputs) {
		for (TestInput input : inputs) {
			input.setEnable(enabled);
			input.setValue("");
		}
	}

}
