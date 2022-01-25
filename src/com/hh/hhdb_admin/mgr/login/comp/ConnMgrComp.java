package com.hh.hhdb_admin.mgr.login.comp;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.ViewType;
import com.hh.frame.lang.LangEnum;
import com.hh.frame.swingui.event.HHEventUtil;
import com.hh.frame.swingui.view.connMgr.ConnMgrTool;
import com.hh.frame.swingui.view.connMgr.base.ConnTreeNode;
import com.hh.frame.swingui.view.connMgr.base.NodeTypeEnum;
import com.hh.frame.swingui.view.container.HWindow;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.tab_files.TabFileRequires;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginUtil;
import com.hh.hhdb_admin.mgr.login.base.LoginConnMsg;

import javax.swing.text.JTextComponent;
import java.awt.event.ActionListener;
import java.util.*;

public abstract class ConnMgrComp extends ConnMgrTool {

	public LoginTabComp tabComp;
	public LangEnum oldLang = StartUtil.default_language;
	private JTextComponent[] jTextComponents;

	public ConnMgrComp(HWindow window, String rootDir, String clazz, boolean drag) {
		super(rootDir, clazz, drag);
		TabFileRequires requires = StartUtil.requires;
		requires.setWindow(window);
		setRequires(requires);
		setExcelHeaders(getExcelHeaders());
		setCellStyle(getColumnStyle(), new LinkedHashMap<>());
	}

	public abstract void updateBtnStatus(boolean enabled);

	public abstract void dbLogin(ConnTreeNode node);

	@Override
	public void select(ConnTreeNode node) {
		LoginUtil.PATH = node.getPath();
		if (NodeTypeEnum.leaf.name().equals(node.getType())) {
			tabComp.setValues((LoginConnMsg) node.getConnMsg());
		} else {
			reset();
		}
	}

	@Override
	public void dbClick(ConnTreeNode node) {
		if (NodeTypeEnum.leaf.name().equals(node.getType())) {
			dbLogin(node);
		} else {
			refresh();
		}
	}

	public HTabPanel getTabPanel() {
		tabComp = new LoginTabComp() {
			@Override
			protected void changeBtnStatus(boolean enabled) {
				updateBtnStatus(enabled);
			}
		};
		return tabComp.getTabPane();
	}

	public LoginConnMsg getLoginConnMsg(boolean flag) {
		return tabComp.getLoginConnMsg(flag);
	}

	public void setValues(LoginConnMsg connMsg) {
		tabComp.setValues(connMsg);
	}

	public void resetLabel() {
		tabComp.resetLabel();
		resetNodeName();
	}

	public void reset() {
		ConnTreeNode treeNode = getSelectNode();
		if (treeNode == null || !NodeTypeEnum.leaf.name().equals(treeNode.getType())) {
			LoginConnMsg connMsg = new LoginConnMsg();
			connMsg.setView(ViewType.USER.name());
			connMsg.setType(DBTypeEnum.hhdb.name());
			connMsg.setConnect(DriverUtil.getDriverUrl(DBTypeEnum.hhdb));
			connMsg.setEncrypted(false);
			connMsg.setUserType(false);
			connMsg.setEnabled(false);
			connMsg.setSshEncrypted(false);
			tabComp.setValues(connMsg);
		} else {
			tabComp.setValues((LoginConnMsg) treeNode.getConnMsg());
		}
	}

	private List<String> getExcelHeaders() {
		List<String> headerList = new ArrayList<>();
		for (String name : Arrays.asList(CommonComp.NAME, CommonComp.VIEW, CommonComp.TYPE, CommonComp.CONNECT,
				CommonComp.USERNAME, CommonComp.ENCRYPTED, CommonComp.PASSWORD, CommonComp.SCHEMA, CommonComp.USER_TYPE,
				CommonComp.ENABLED, CommonComp.HOST, CommonComp.PORT, CommonComp.SSH_USERNAME, CommonComp.SSH_TYPE,
				CommonComp.SSH_ENCRYPTED, CommonComp.SSH_PASSWORD, CommonComp.PRIVATE_KEY, CommonComp.PRIVATE_PASSWORD)) {
			headerList.add(CommonComp.getLang(name).replace(":", "")
					.replace("：", "").replace("*", ""));
		}
		return headerList;
	}

	private LinkedHashMap<Integer, Integer> getColumnStyle() {
		LinkedHashMap<Integer, Integer> columnMap = new LinkedHashMap<>();
		columnMap.put(1, 20);
		columnMap.put(2, 10);
		columnMap.put(3, 10);
		columnMap.put(4, 83);
		columnMap.put(5, 20);
		columnMap.put(6, 10);
		columnMap.put(7, 20);
		columnMap.put(8, 20);
		columnMap.put(9, 20);
		columnMap.put(10, 40);
		columnMap.put(11, 20);
		columnMap.put(12, 10);
		columnMap.put(13, 20);
		columnMap.put(14, 20);
		columnMap.put(15, 10);
		columnMap.put(16, 20);
		columnMap.put(17, 50);
		columnMap.put(18, 20);
		return columnMap;
	}

	public void addUndoableEvent() {
		if (jTextComponents == null) {
			initTextComponents();
		}
		if (jTextComponents != null) {
			HHEventUtil.addUndoableEvent(jTextComponents);
		}
	}

	public void addEnterEvent(ActionListener enterListener) {
		if (jTextComponents == null) {
			initTextComponents();
		}
		if (jTextComponents != null) {
			HHEventUtil.addEnterEvent(enterListener,jTextComponents);
		}
	}

	private void initTextComponents() {
		if (tabComp != null && tabComp.getInputList() != null) {
			jTextComponents = tabComp.getInputList().stream()
					.map(input -> input != null ? ((JTextComponent) input.getInput().getComp()) : null)
					.filter(Objects::nonNull)
					.toArray(JTextComponent[]::new);
		}
	}
}
