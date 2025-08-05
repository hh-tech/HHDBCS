package com.hh.hhdb_admin.mgr.menubar;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.cs_desk.BRConfigsComp;
import com.hh.frame.swingui.cs_desk.PingPanel;
import com.hh.frame.swingui.cs_desk.PortTestPanel;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.fa.FaBrandsEnum;
import com.hh.frame.swingui.view.fa.FaRegularEnum;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.hmenu.CheckMenuItem;
import com.hh.frame.swingui.view.hmenu.HMenu;
import com.hh.frame.swingui.view.hmenu.HMenuBar;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.HHdbAdmin;
import com.hh.hhdb_admin.common.CommToolMar;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.bean.BarVisibelBean;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;

import java.io.File;
import java.util.Map;

/**
 * @author Jiang
 * @date 2020/10/13
 */

public class MenubarComp extends HMenuBar {
	private File etcFile = CsVarUtil.getEtcFile();
	private String[] backUpNames =  CsVarUtil.backUpNames;
	private String[] nessaryFile =  CsVarUtil.nessaryFile;
	

	private BRConfigsComp configs;

	public MenubarComp() {
		configs = new BRConfigsComp(this.getComp(), backUpNames, nessaryFile, etcFile) {
			@Override
			protected void restart() {
				MenubarComp.this.restart();
			}
		};
		HMenu fileMenu = new HMenu(LangMgr.lang(Word2.FILE)+"(F)",FaSolidEnum.FOLDER_OPEN);
		fileMenu.setMnemonic('F');
		CheckMenuItem checkedItem = new CheckMenuItem(LangMgr.sLang(CsL.HIDE_TOOLBAR)) {
			@Override
			protected void onAction() {
				CsVarUtil.eng2.doCall(CsMgrEnum.MAIN_FRAME, new BarVisibelBean(BaseOpEnum.SET_TOOLBAR_VISIBLE,this.isSelected()));
				this.setFontIcon(this.isSelected() ? FaSolidEnum.EYE_SLASH : FaSolidEnum.EYE);
				this.setText(this.isSelected() ? LangMgr.sLang(CsL.HIDE_TOOLBAR) : LangMgr.sLang(CsL.VIEW_TOOLBAR));
				CsVarUtil.CONF.setShowToolBar(this.isSelected());
				CsVarUtil.saveConfig();
			}
		};
		checkedItem.setSelected(CsVarUtil.CONF.IsShowToolBar());
		checkedItem.setFontIcon(CsVarUtil.CONF.IsShowToolBar()?FaSolidEnum.EYE_SLASH:FaSolidEnum.EYE);
		
		
		CheckMenuItem showStatusBarItem = new CheckMenuItem(LangMgr.lang(Word3.STATUS_BAR)) {
			@Override
			protected void onAction() {
				CsVarUtil.eng2.doCall(CsMgrEnum.MAIN_FRAME, new BarVisibelBean(BaseOpEnum.SET_STATUSBAR_VISIBLE,this.isSelected()));
				this.setFontIcon(this.isSelected() ? FaSolidEnum.EYE_SLASH : FaSolidEnum.EYE);
				CsVarUtil.CONF.setShowStatusBar(this.isSelected());
				CsVarUtil.saveConfig();
			}
		};
		
		showStatusBarItem.setFontIcon(CsVarUtil.CONF.isShowStatusBar()?FaSolidEnum.EYE_SLASH:FaSolidEnum.EYE);
		showStatusBarItem.setSelected(CsVarUtil.CONF.isShowStatusBar());
		HMenuItem backupItem = new HMenuItem(LangMgr.lang(Word2.BACKUP)) {
			@Override
			protected void onAction() {
				backup();
			}
		};
		backupItem.setFontIcon(FaSolidEnum.FILE_ARROW_DOWN);

		HMenuItem restoreItem = new HMenuItem(LangMgr.lang(Word2.RESTORE)) {
			@Override
			protected void onAction() {
				restore();
			}
		};
		restoreItem.setFontIcon(FaSolidEnum.FILE_ARROW_UP);

		HMenuItem exitItem = new HMenuItem(LangMgr.lang(Word2.EXIT)) {
			@Override
			protected void onAction() {
				MenuBarUtil.onMenuItemClick(MenuBarUtil.EXIT);
			}
		};
		exitItem.setFontIcon(FaSolidEnum.RIGHT_FROM_BRACKET);
		fileMenu.addCheckedItem(checkedItem);
		fileMenu.addCheckedItem(showStatusBarItem);
		fileMenu.addItem(backupItem, restoreItem, exitItem);

		CommToolMar commToolMar = new CommToolMar();
		HMenu manageMenu = new HMenu(LangMgr.lang(Word2.MANAGE)+"(M)", FaSolidEnum.GEAR);
		manageMenu.setMnemonic('M');
		Map<String, Map<String, Enum<?>>> manageBtnList = commToolMar.genManageNames();
		MenuBarUtil.setItemAction(manageMenu, commToolMar, manageBtnList);
		HMenu windowMenu = new HMenu(LangMgr.lang(Word2.WINDOW)+"(W)", FaBrandsEnum.WINDOWS);
		windowMenu.setMnemonic('W');
		
		Map<String, Map<String, Enum<?>>> windowBtnList = commToolMar.genWindowNames();
		MenuBarUtil.setItemAction(windowMenu, commToolMar, windowBtnList);
		
		HMenuItem formatSqlItem = new HMenuItem(LangMgr.sLang(CsL.SQL_FORMAT), FaSolidEnum.WAND_MAGIC_SPARKLES);
		formatSqlItem.getComp().addActionListener(e -> CsVarUtil.eng2.doCall(CsMgrEnum.TOOL, new BaseOpBean(BaseOpEnum.SQL_FORMAT)));
		HMenuItem sqlConversionItem = new HMenuItem(LangMgr.sLang(CsL.SQL_CONVERSION), FaSolidEnum.RETWEET);
		sqlConversionItem.getComp().addActionListener(e -> CsVarUtil.eng2.doCall(CsMgrEnum.TOOL, new BaseOpBean(BaseOpEnum.SQL_CONVERSION)));
		HMenuItem dataToSqlItem = new HMenuItem(LangMgr.sLang(CsL.TO_SQL), FaSolidEnum.SHARE);
		dataToSqlItem.getComp().addActionListener(e -> CsVarUtil.eng2.doCall(CsMgrEnum.TOOL, new BaseOpBean(BaseOpEnum.DATA_TO_SQL)));
		HMenu toolMenu = new HMenu(LangMgr.lang(Word2.TOOL)+"(T)", FaSolidEnum.SCREWDRIVER_WRENCH);
		
		
		HMenuItem portMenuItem = new HMenuItem("port" + LangMgr.lang(Word2.TEST),FaSolidEnum.CODE_COMMIT) {
			@Override
			protected void onAction() {
				PortTestPanel panel = new PortTestPanel();
				HDialog dialog = new HDialog(getComp(),1000,700,false) {
					@Override
					protected void closeEvent() {
						panel.close();
					}
				};
				dialog.setRootPanel(panel);
				dialog.setWindowTitle("port");
				dialog.setOkBtnText(LangMgr.lang(Word2.CLOSE));
				dialog.setStatusBar(new HBarPanel());
				dialog.show();
			}
		};
		
		HMenuItem pingMenuItem = new HMenuItem("ping" + LangMgr.lang(Word2.TEST),FaSolidEnum.SIGNAL) {
			@Override
			protected void onAction() {
				PingPanel panel = new PingPanel();
				HDialog dialog = new HDialog(getComp(),800,500,false) {
					@Override
					protected void closeEvent() {
						panel.close();
					}
				};
				dialog.setRootPanel(panel);
				dialog.setWindowTitle("ping" );
				dialog.setOkBtnText(LangMgr.lang(Word2.CLOSE));
				dialog.removeCancleBtn();
				dialog.show();
			}
		};
		
		HMenuItem screenLockMenuItem = new HMenuItem(LangMgr.lang(Word4.SCREEN_LOCK),FaSolidEnum.LOCK) {
			@Override
			protected void onAction() {
				CsVarUtil.eng2.doCall(CsMgrEnum.MAIN_FRAME, new BaseOpBean(BaseOpEnum.LOCK));
			}
		};
		toolMenu.addItem(screenLockMenuItem,formatSqlItem,sqlConversionItem,dataToSqlItem,portMenuItem,pingMenuItem);
		toolMenu.setMnemonic('T');

		HMenuItem[] helpMenuItems = MenuBarUtil.getHelpMenuItems(null);
		HMenu helpMenu = new HMenu(LangMgr.lang(Word2.HELP)+"(H)",FaRegularEnum.CIRCLE_QUESTION);
		helpMenu.addItem(helpMenuItems);
		helpMenu.setMnemonic('H');
		add(fileMenu, manageMenu, windowMenu, toolMenu, helpMenu);
	}


	private void restore() {
		configs.showRestore();
		
	}


	private void backup() {
		configs.showBackup(CsVarUtil.exportConfigName);
	}


	private void restart() {
		CsVarUtil.eng2.doCall(CsMgrEnum.MAIN_FRAME, new BaseOpBean(BaseOpEnum.CLOSE_ALL_TAB));
		CsVarUtil.eng2.doCall(CsMgrEnum.MAIN_FRAME, new BaseOpBean(BaseOpEnum.DISPOSE));

		CsVarUtil.init();
		HHdbAdmin.main(null);
	}


}
