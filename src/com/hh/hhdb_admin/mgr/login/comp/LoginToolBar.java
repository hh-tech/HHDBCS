package com.hh.hhdb_admin.mgr.login.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.LangType;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.cs_desk.BRConfigsComp;
import com.hh.frame.swingui.cs_desk.LangSelectBox;
import com.hh.frame.swingui.cs_desk.PingPanel;
import com.hh.frame.swingui.cs_desk.PortTestPanel;
import com.hh.frame.swingui.laf.LafIdEnum;
import com.hh.frame.swingui.laf.LafMgr;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.ctrl.PopMenuBtn;
import com.hh.frame.swingui.view.fa.FaRegularEnum;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.hmenu.HPopMenu;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.HHdbAdmin;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.ExtDbTypeUtil;
import com.hh.hhdb_admin.mgr.menubar.MenuBarUtil;

import java.io.File;

/**
 * 顶部菜单栏
 */
public class LoginToolBar {
	private PopMenuBtn toolBtn,helpBtn;
	private HFrame loginFrame;
	private HBarPanel barPanel;
	
	private File etcFile = CsVarUtil.getEtcFile();
	private String[] backUpNames =  CsVarUtil.backUpNames;
	private String[] nessaryFile =  CsVarUtil.nessaryFile;
	private BRConfigsComp configs;
	
	
	public LoginToolBar(HFrame loginFrame) {
		HBarLayout barLayout=new HBarLayout();
		barLayout.setAlign(AlignEnum.LEFT);
		barLayout.setxGap(5);
		barLayout.setTopHeight(5);
		barLayout.setBottomHeight(5);
		barPanel = new HBarPanel(barLayout);
		this.loginFrame = loginFrame;
		init();
		configs = new BRConfigsComp(loginFrame.getWindow(), backUpNames, nessaryFile, etcFile) {
			@Override
			protected void restart() {
				LoginToolBar.this.restart();
			}
		};
	}

	protected void init() {
		SelectBox skinComboBox = LafMgr.getSkinSelectBox();
		skinComboBox.addListener(e -> {
			CsVarUtil.CONF.setSkin(LafIdEnum.valueOf(skinComboBox.getValue()));
			CsVarUtil.saveConfig();});
		
		LangSelectBox langBox = new LangSelectBox(){
			@Override
			protected LangType loadConf() {
				return CsVarUtil.CONF.getLang();
			}
			@Override
			protected void saveConf() {
				CsVarUtil.saveLang(getValue());
			}
			@Override
			protected void doRestart() {
				new Thread(() -> {
					loginFrame.dispose();
					HHdbAdmin.main(null);
				}).start();
			}
		};
		
		HMenuItem formatSqlItem = new HMenuItem(LangMgr.sLang(CsL.SQL_FORMAT), FaSolidEnum.WAND_MAGIC_SPARKLES);
		formatSqlItem.getComp().addActionListener(e -> CsVarUtil.eng2.doCall(CsMgrEnum.TOOL, new BaseOpBean(BaseOpEnum.SQL_FORMAT)));
		HMenuItem sqlConversionItem = new HMenuItem(LangMgr.sLang(CsL.SQL_CONVERSION), FaSolidEnum.BARS_STAGGERED);
		sqlConversionItem.getComp().addActionListener(e -> CsVarUtil.eng2.doCall(CsMgrEnum.TOOL, new BaseOpBean(BaseOpEnum.SQL_CONVERSION)));
		HMenuItem dataToSqlItem = new HMenuItem(LangMgr.sLang(CsL.TO_SQL), FaSolidEnum.BOLT_LIGHTNING);
		dataToSqlItem.getComp().addActionListener(e -> CsVarUtil.eng2.doCall(CsMgrEnum.TOOL, new BaseOpBean(BaseOpEnum.DATA_TO_SQL)));
		
		HMenuItem backUpItem = new HMenuItem(LangMgr.lang(Word2.BACKUP), FaSolidEnum.FILE_EXPORT) {
			protected void onAction() {
				configs.showBackup(CsVarUtil.exportConfigName);
			}
		};
		
		
		HMenuItem restoreItem = new HMenuItem(LangMgr.lang(Word2.RESTORE), FaSolidEnum.FILE_IMPORT) {
			protected void onAction() {
				configs.showRestore();
			}
		};
		
		
		HMenuItem addTypeItem = new HMenuItem(LangMgr.sLang(CsL.EXTENDED_DATABASE_TYPE), FaSolidEnum.FILE_CIRCLE_PLUS) {
			protected void onAction() {
				PkgDBTypeComp addDcomp = new PkgDBTypeComp(ExtDbTypeUtil.ICON_FILE,ExtDbTypeUtil.LIB_FILE) {
					protected void infoRefresh() {
						LoginToolBar.this.informRefresh();
					}
				};
				HDialog dialog = new HDialog(loginFrame.getWindow(),600,500,false);
				dialog.setWindowTitle(LangMgr.sLang(CsL.EXTENDED_DATABASE_TYPE));
				dialog.setStatusBar(new HBarPanel());
				dialog.setDisposeOnHide(false);
				dialog.setRootPanel(addDcomp);
				dialog.show();
			}
		};
		
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
		
		
		HPopMenu toolMenu =new HPopMenu();
		toolMenu.addItem(formatSqlItem,sqlConversionItem,dataToSqlItem,pingMenuItem,portMenuItem,backUpItem,restoreItem,addTypeItem);
		toolBtn=new PopMenuBtn(LangMgr.lang(Word2.TOOL), toolMenu);
		toolBtn.setIcon(FaSolidEnum.TOOLBOX,16);
		
		HPopMenu helpMenu =new HPopMenu();
		helpMenu.addItem(MenuBarUtil.getHelpMenuItems(loginFrame));
		helpBtn = new PopMenuBtn(LangMgr.lang(Word2.HELP), helpMenu);
		helpBtn.setIcon(FaRegularEnum.CIRCLE_QUESTION,16);
//		skinComboBox.getComp().setPreferredSize(new Dimension(120,skinComboBox.getHeight()));
//		langBox.getComp().setPreferredSize(new Dimension(120,langBox.getHeight()));
		barPanel.add(langBox,skinComboBox,toolBtn,helpBtn);
		
//		HBarPanel panel = new HBarPanel();
//		LabelInput input = new LabelInput();
//		input.setValue("新建");
//		input.setIcon(SvgUrlUtil.RENAME);
//		panel.add(input);
//		barPanel.setLastHComp(panel);
	}
	
	
	public HBarPanel getHBarPanel() {
		return barPanel;
	}
	
	protected void informRefresh() {
		
	}
	
	private void restart() {
		loginFrame.dispose();
//		StartUtil.init();
		HHdbAdmin.main(null);
	}
}
