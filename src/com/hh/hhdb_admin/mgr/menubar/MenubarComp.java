package com.hh.hhdb_admin.mgr.menubar;

import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.hmenu.*;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CommToolMar;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.about.AboutMgr;
import com.hh.hhdb_admin.mgr.login.base.ToolSplitButton;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

/**
 * @author Jiang
 * @date 2020/10/13
 */

public class MenubarComp extends HMenuBar {

	public static final String DOMAIN_NAME = MenubarComp.class.getName();

	static {
		try {
			LangMgr2.loadMerge(MenubarComp.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final String SETTING = "setting";
	private static final String EXIT = "exit";

	private static final String ABOUT = "about";
	private static final String LICENSE = "license";
	private static final String VERSION = "version";

	public MenubarComp() {
		HMenu fileMenu = new HMenu(getLang("file"));
		fileMenu.setMnemonic('f');
		fileMenu.setIcon(getIcon("file"));
		HMenuItem setItem = new HMenuItem(getLang(SETTING)) {
			@Override
			protected void onAction() {
				onMenuItemClick(SETTING);
			}
		};
		setItem.setIcon(getIcon("setting"));
		CheckMenuItem checkedItem = new CheckMenuItem(getLang("hideToolbar")) {
			@Override
			protected void onAction() {
				JsonObject jsonObject = GuiJsonUtil.toJsonCmd(MainFrameMgr.SET_TOOLBAR_VISIBLE);
				jsonObject.set("visible", this.isSelected());
				StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, jsonObject);
				this.setIcon(this.isSelected() ? getIcon("hide") : getIcon("view"));
				this.setText(this.isSelected() ? getLang("hideToolbar") : getLang("viewToolbar"));
			}
		};
		checkedItem.setSelected(true);
		checkedItem.setIcon(getIcon("hide"));
		HMenuItem exitItem = new HMenuItem(getLang(EXIT)) {
			@Override
			protected void onAction() {
				onMenuItemClick(EXIT);
			}
		};
		exitItem.setIcon(getIcon("exit"));
		fileMenu.addItem(setItem);
		fileMenu.addCheckedItem(checkedItem);
		fileMenu.addItem(exitItem);
		HMenu navigateMenu = new HMenu(getLang("navigate"), getIcon("navigate"));
		navigateMenu.setMnemonic('n');
		CommToolMar tm = new CommToolMar();
		List<String> btnList = tm.genToolInfo();
		for (String key : btnList) {
			HMenuItem ditem = new HMenuItem(tm.genTitle(key)) {
				@Override
				protected void onAction() {
					tm.onBtnClick(key);
				}
			};
			ditem.setIcon(tm.getIcon(key));
			navigateMenu.addItem(ditem);
		}
		ToolSplitButton toolSplitButton = new ToolSplitButton();
		HMenu toolMenu = new HMenu(getLang("tool"), getIcon("tool"));
		toolMenu.addItem(toolSplitButton.getMenuItems().toArray(new HMenuItem[0]));
		toolMenu.setMnemonic('T');

		HMenu helpMenu = new HMenu(getLang("help"));
		helpMenu.setMnemonic('h');
		helpMenu.setIcon(getIcon("help"));
		HMenuItem aboutItem = new HMenuItem(getLang(ABOUT)) {
			@Override
			protected void onAction() {
				onMenuItemClick(ABOUT);
			}
		};
		aboutItem.setIcon(getIcon("about"));
		HMenuItem licenseItem = new HMenuItem(getLang(LICENSE)) {
			@Override
			protected void onAction() {
				onMenuItemClick(LICENSE);
			}
		};
		licenseItem.setIcon(getIcon("license"));
		HMenuItem versionItem = new HMenuItem(getLang("version")) {
			@Override
			protected void onAction() {
				onMenuItemClick(VERSION);
			}
		};
		versionItem.setIcon(getIcon("version"));
		helpMenu.addItem(aboutItem, licenseItem, versionItem);
		add(fileMenu, navigateMenu, toolMenu, helpMenu);
	}

	private void onMenuItemClick(String action) {
		switch (action) {
			case EXIT:
				int option = JOptionPane.showConfirmDialog(null, getLang("isExit"), getLang("tip"),
						JOptionPane.OK_CANCEL_OPTION);
				if (JOptionPane.OK_OPTION == option) {
					System.exit(0);
				}
				break;
			case ABOUT:
				sendMsg(CsMgrEnum.ABOUT, GuiJsonUtil.toJsonCmd(AboutMgr.CMD_SHOW_ABOUT));
				break;
			case LICENSE:
				sendMsg(CsMgrEnum.MENUBAR, GuiJsonUtil.toJsonCmd(MenubarMgr.CMD_SHOW_LICENSE));
				break;
			case VERSION:
				sendMsg(CsMgrEnum.MENUBAR, GuiJsonUtil.toJsonCmd(MenubarMgr.CMD_SHOW_VERSION));
				break;
			case SETTING:
				sendMsg(CsMgrEnum.MENUBAR, GuiJsonUtil.toJsonCmd(MenubarMgr.CMD_SHOW_SETTING));
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + action);
		}
	}

	public static String getLang(String key) {
		LangMgr2.setDefaultLang(StartUtil.default_language);
		return LangMgr2.getValue(DOMAIN_NAME, key);
	}

	private void sendMsg(CsMgrEnum mgrEnum, JsonObject msg) {
		System.out.println(mgrEnum + "---" + msg.toPrettyString());
		if (StartUtil.eng != null) {
			StartUtil.eng.doPush(mgrEnum, msg);
		}
	}

	public static ImageIcon getIcon(String name) {
		return IconFileUtil.getIcon(new IconBean(CsMgrEnum.MENUBAR.name(), name, IconSizeEnum.SIZE_16));
	}
}
