package com.hh.hhdb_admin.mgr.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;

import org.apache.commons.io.IOUtils;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.BaseConnBean;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.JUrlUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.create_dbobj.treeMr.TreeMrUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.ssh.JdbcSshTunnelTool;
import com.hh.frame.swingui.cs_desk.GlobalVarUtil;
import com.hh.frame.swingui.view.connTree2.BasicOpType;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tree2.LeafBean;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.login.comp.ConnMgrComp;
import com.hh.hhdb_admin.mgr.login.comp.LoginTabComp;
import com.hh.hhdb_admin.mgr.login.comp.LoginToolBar;

public abstract class LoginComp  {
	private final int width = 850, height = 600;
	private HDialog dialog;
	public static HFrame loginFrame = null;
	private static final String saveButtonId = "__saveBut";
	private static final String updateButtonId = "__updateBut";
	private static final String loginButtonId = "__loginBut";
	private static final String testButtonId = "__testBut";
	private static final String cancelButtonId = "__cancelBut";
	private static final String toAddButtonId = "__createBut";

	private HPanel footPanel;

	private HButton loginBtn, testConnBtn, cancelBtn, saveBtn, updateBtn, toAddBtn;
	private ConnMgrComp connMgrComp; // 左侧树结构
	private LoginTabComp tabComp; // 右侧表单
	private HSplitPanel splitPanel;
	private LoginToolBar loginToolBar;
	private HBarPanel leftPanel;
	private HBarPanel rightPanel;

	public LoginComp() {
		loginFrame = new HFrame(width, height);
		loginFrame.setIconImage(IconFileUtil.getLogo());
		loginFrame.setCloseType(true);
		loginFrame.getWindow().setLocationRelativeTo(null);
		splitPanel = new HSplitPanel();
		try {
			initConnMgrComp();
		} catch (Exception e) {
			PopDlgUtil.error(loginFrame, e);
		}
		initTabComp();
		setSplitPanel();
	}

	/**
	 * 显示登录界面
	 */
	public void showLogin() {
		loginFrame.setWindowTitle(LangMgr.lang(Word2.LOGIN));
		loginFrame.setRootPanel(splitPanel);
		loginToolBar = initMenuBar();
		loginFrame.setToolBar(loginToolBar.getHBarPanel());
		loginFrame.show();
		nameInputFocus();
	}

	/**
	 * 切换登录
	 */
	public void switchLogin() {
		dialog = new HDialog(CsVarUtil.parentFrame, width, height, true) {
			@Override
			protected void closeEvent() {
				GlobalVarUtil.setLocale(CsVarUtil.CONF.getLang());
			}
		};
		dialog.setIconImage(IconFileUtil.getLogo());
		dialog.setRootPanel(splitPanel);
		dialog.setWindowTitle(LangMgr.lang(Word4.CONNECT_MANAGER));
		dialog.setStatusBar(new HBarPanel());
		((JDialog) dialog.getWindow()).setResizable(true);
		tabComp.setValues(CsVarUtil.jdbc);
		changeToModify();
		dialog.show();
		nameInputFocus();
	}

	/**
	 * 切换模式
	 *
	 * @param schemaOrDb
	 */
	public void switchSchema(String schemaOrDb) {
			JdbcInfo jdbc=new JdbcInfo(CsVarUtil.jdbc.clone().getJobj());
			DBTypeEnum dbtype=CsVarUtil.getDbtype();
			try {
				if(TreeMrUtil.swithDbModel(dbtype)) {
					jdbc.setDbUrl(JUrlUtil.replaceDbName(dbtype, jdbc.getDbUrl(), schemaOrDb));
					jdbc.setSchema(null);
				}else {
					jdbc.setSchema(SqlStrUtil.getSuitableDbName(schemaOrDb,dbtype, null));
				}
				CsVarUtil.jdbc=jdbc;
				if(CsVarUtil.doConn()) {
					CsVarUtil.eng2.doCall(CsMgrEnum.TREE, new BaseOpBean(BaseOpEnum.INIT));
					PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.sLang(CsL.SWITCH_SCHEMA_SUCCESS));
				}
				
			} catch (Exception e) {
				PopDlgUtil.logErr(getClass(),CsVarUtil.parentFrame, e);
			}

	}

	/**
	 * 初始化顶部状态栏
	 *
	 * @return ToolBar
	 */
	private LoginToolBar initMenuBar() {
		return new LoginToolBar(loginFrame) {
			protected void informRefresh() {
				tabComp.reset();
				changeToAdd();
			}
		};
	}

	/**
	 * 保存
	 * @param isPop 是否提示
	 */
	private void save(boolean isPop) {
		save(tabComp.getLoginConnMsg(),isPop);
	}
	
	
	
	/**
	 * 保存
	 * @param isPop 是否提示
	 */
	private void save(JdbcInfo info,boolean isPop) {
		try {
			if (!connMgrComp.isLockStatus()) {
				connMgrComp.save(info);
				LeafBean b = connMgrComp.getTree().get(info.getId());
				tabComp.setValues(info);
				if (b != null) {
					changeToModify();
					connMgrComp.getTree().reload(b.getFolder().getId());
					connMgrComp.getTree().expandFolder(b.getFolder().getId());
				} else {
					changeToModify();
					connMgrComp.getTree().reload();
				}
			} else {
				PopDlgUtil.error(splitPanel.getComp(), "请先解锁");
				return;
			}

			if (isPop)
				PopDlgUtil.info(dialog == null ? loginFrame : dialog, LangMgr.lang(Word4.SAVED_SUCCESSFULLY));
		} catch (Exception e) {
			Lg.error(LoginComp.class, e);
			PopDlgUtil.error(dialog == null ? loginFrame : dialog, e);
		}
	}
	

	protected void nameInputFocus() {
		tabComp.nameInput.getComp().requestFocus();
	}

	/**
	 * 初始化底部按钮区域面板
	 *
	 * @return
	 */
	private void initFootBtnPanel() {
		HDivLayout divLayout = new HDivLayout(GridSplitEnum.C6);
		divLayout.setBottomHeight(10);
		divLayout.setTopHeight(10);
		footPanel = new HPanel(divLayout);

		testConnBtn = new HButton(testButtonId, LangMgr.lang(Word2.TEST));
		testConnBtn.setEnabled(false);
		testConnBtn.setIcon(FaSolidEnum.TOWER_BROADCAST, 16);
		testConnBtn.addActionListener(new TestConnListener());
		saveBtn = new HButton(saveButtonId, LangMgr.lang(Word2.SAVE));
		saveBtn.setEnabled(false);
		saveBtn.setIcon(FaSolidEnum.FLOPPY_DISK, 16);
		saveBtn.addActionListener(e -> save(true));

		updateBtn = new HButton(updateButtonId, LangMgr.lang(Word2.SAVE));
		updateBtn.setIcon(FaSolidEnum.FLOPPY_DISK, 16);
		updateBtn.addActionListener(e -> save(true));

		toAddBtn = new HButton(toAddButtonId, LangMgr.lang(Word2.CREATE)) {
			protected void onClick() {
				if ((updateBtn.getComp().isShowing() && updateBtn.getComp().isEnabled())
						|| (saveBtn.getComp().isShowing() && saveBtn.getComp().isEnabled())) {
					if (!PopDlgUtil.confirm(loginFrame, LangMgr.sLang(CsL.CONFIRM_GIVE_UP_MODIFY))) {
						return;
					}
				}
				changeToAdd();
			}
		};
		toAddBtn.setIcon(FaSolidEnum.REPLY_ALL, 16);
		loginBtn = new HButton(loginButtonId, LangMgr.lang(Word2.LOGIN));
		loginBtn.addActionListener(e -> login(tabComp.getLoginConnMsg()));
		loginBtn.setEnabled(false);
		loginBtn.setIcon(FaSolidEnum.ARROW_RIGHT_TO_BRACKET, 16);
		cancelBtn = new HButton(cancelButtonId, LangMgr.lang(Word2.CANCEL)) {
			@Override
			protected void onClick() {
				if (dialog != null) {
					GlobalVarUtil.setLocale(CsVarUtil.CONF.getLang());
					dialog.hide();
				} else {
					loginFrame.dispose();
					System.exit(0);
				}
			}
		};
		cancelBtn.setIcon(FaSolidEnum.REPLY, 16);
		HBarLayout rightLayout = new HBarLayout();
		rightLayout.setAlign(AlignEnum.RIGHT);
		rightLayout.setxGap(10);
		HBarLayout leftLayout = new HBarLayout();
		leftLayout.setAlign(AlignEnum.LEFT);
		leftPanel = new HBarPanel(leftLayout);

		rightPanel = new HBarPanel(rightLayout);

		footPanel.add(leftPanel, rightPanel);
		changeToAdd();
	}

	private void changeToAdd() {
		leftPanel.removeAll();
		leftPanel.add(toAddBtn, testConnBtn, saveBtn);
		rightPanel.removeAll();
		rightPanel.add(loginBtn, cancelBtn);
		tabComp.reset();
		setButEnabled(false);
		tabComp.getTabPane().updateTabIcon(LoginTabComp.TABID, FaSolidEnum.PLUS);
	}

	private void changeToModify() {
		leftPanel.removeAll();
		updateBtn.setEnabled(false);
		leftPanel.add(toAddBtn, testConnBtn, updateBtn);
		rightPanel.removeAll();
		rightPanel.add(loginBtn, cancelBtn);
		tabComp.getTabPane().updateTabIcon(LoginTabComp.TABID, FaSolidEnum.PENCIL);
	}

	/**
	 * 测试连接
	 */
	class TestConnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Map<String, Boolean> maps = getButStatus();
			setButEnabled(false);
			if (dialog != null) {
				dialog.setWindowTitle(LangMgr.sLang(CsL.TESTING_CONNECTION));
			} else {
				loginFrame.setWindowTitle(LangMgr.sLang(CsL.TESTING_CONNECTION));
			}
			new Thread(() -> {
				Connection conn = null;
				JdbcSshTunnelTool tunnel = null;
				try {
					JdbcInfo info = tabComp.getLoginConnMsg();
					info.setConnTimeoutSec(5);
					if (info.isUseSshTunnel()) {
						tunnel = new JdbcSshTunnelTool(info);
						conn = ConnUtil.getConn(tunnel.getLocalJdbc());
					} else {
						conn = ConnUtil.getConn(info);
					}

					PopDlgUtil.info(LangMgr
							.lang(ConnUtil.isConnected(DriverUtil.getDbType(info), info.getCurrentVersion(), conn)
									? Word4.CONNECTION_SUCCESSFUL
									: Word4.CONNECTION_FAIL));
					nameInputFocus();
				} catch (Exception exception) {
					exception.printStackTrace();
					Lg.error(LoginComp.class, exception);
					PopDlgUtil
							.error(new Exception(LangMgr.lang(Word4.CONNECTION_FAIL) + " : " + exception.getMessage()));
				} finally {
					ConnUtil.close(conn);
					IOUtils.closeQuietly(tunnel);
					loginFrame.setWindowTitle(LangMgr.lang(Word2.LOGIN));
					if (dialog != null)
						dialog.setWindowTitle(LangMgr.lang(Word4.CONNECT_MANAGER));

					resetButStatus(maps);
				}
			}).start();
		}
	}

	private void login(JdbcInfo jdbcBean) {
		
		
		if(!loginBtn.getComp().isEnabled()) {
			return ;
		}
		
		Map<String, Boolean> maps = getButStatus();
		setButEnabled(false);
		if (dialog != null) {
			dialog.setWindowTitle(LangMgr.sLang(CsL.CONNECTING));
		} else {
			loginFrame.setWindowTitle(LangMgr.sLang(CsL.CONNECTING));
		}
		
		new Thread(() -> {
			try {
				CsVarUtil.jdbc=jdbcBean;
				
				if(!CsVarUtil.canLogin(jdbcBean)) {
					throw new Exception(LangMgr.sLang(CsL.DB_SUPPORT_LOGIN));
				}
				
				
				boolean suc=CsVarUtil.doConn();
				if(!suc)return;
				if (jdbcBean.getView() == JdbcInfo.View.DBA && !DBTypeEnum.hasDbaView(DriverUtil.getDbType(jdbcBean), jdbcBean.getCurrentVersion()))
					throw new Exception(LangMgr.sLang(CsL.NOT_DBA));
				if (dialog != null) {
					dialog.dispose();
					CsVarUtil.eng2.doCall(CsMgrEnum.MAIN_FRAME, new BaseOpBean(BaseOpEnum.CLOSE_ALL_TAB));
				} else {
					loginFrame.hide();
				}
				if (tabComp.getAutoSaveInput().isChecked()) {
					if (!connMgrComp.isLockStatus()) {
						save(jdbcBean,false);
					}
				}
				inform();
			} catch (Exception e) {
				Lg.error(LoginComp.class, e);
				PopDlgUtil.error(e);
			} finally {
				loginFrame.setWindowTitle(LangMgr.lang(Word2.LOGIN));
				if (dialog != null)
					dialog.setWindowTitle(LangMgr.lang(Word4.CONNECT_MANAGER));
				resetButStatus(maps);
			}
		}).start();
	}

	private void initTabComp() {
		tabComp = new LoginTabComp() {

			@Override
			protected void changeBtnStatus(boolean enabled) {
				setButEnabled(enabled);
			}
		};
	}

	private void initConnMgrComp() throws Exception {
		connMgrComp = new ConnMgrComp(splitPanel.getComp()) {
			@Override
			protected void onOp(String op) {
				if (op.equals(BasicOpType.del.name()) || op.equals(BasicOpType.clear.name())) {
					tabComp.reset();
					changeToAdd();
				}
			}

			@Override
			public void updateResource(BaseConnBean bean) throws Exception {
				super.updateResource(bean);
				tabComp.setValues(new JdbcInfo(bean.getJobj()));
				changeToModify();
			}
			
			@Override
			protected void oneClickNode(LeafBean bean) {
				if (bean.isLeaf() && tabComp.getLoginConnMsg().getId().equals(bean.getId())) {
					return;
				}
				if ((updateBtn.getComp().isShowing() && updateBtn.getComp().isEnabled())
						|| (saveBtn.getComp().isShowing() && saveBtn.getComp().isEnabled())) {
					if (!connMgrComp.isLockStatus()&&!PopDlgUtil.confirm(splitPanel.getComp(), LangMgr.sLang(CsL.CONFIRM_GIVE_UP_MODIFY))) {
						if (bean.getFolder() != null) {
							connMgrComp.getTree().reload(bean.getFolder().getId());
						} else {
							connMgrComp.getTree().reload();
						}
						return;
					}
				}

				if (bean.isLeaf()) {
					tabComp.setValues(new JdbcInfo(bean.getJobj()));
					changeToModify();
				} else {
					changeToAdd();
				}
			}


			@Override
			public void dbLogin(LeafBean bean) {
				JdbcInfo info = new JdbcInfo(bean.getJobj());
				login(info);
			}
		};
	}

	/**
	 * 初始化登录面板
	 */
	private void setSplitPanel() {
		try {
			LastPanel lastPanel = new LastPanel();
			lastPanel.set(tabComp.getTabPane().getComp());
			initFootBtnPanel();
			lastPanel.setFoot(footPanel.getComp());
			splitPanel.setDividerLocation(310);
			splitPanel.setPanelOne(connMgrComp.getHComp());
			splitPanel.setLastComp4Two(lastPanel);
		} catch (Exception e) {
			Lg.error(LoginComp.class, e);
		}
	}

	private Map<String, Boolean> getButStatus() {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		map.put(loginButtonId, loginBtn.getComp().isEnabled());
		map.put(testButtonId, testConnBtn.getComp().isEnabled());
		map.put(cancelButtonId, cancelBtn.getComp().isEnabled());
		map.put(saveButtonId, saveBtn.getComp().isEnabled());
		map.put(updateButtonId, updateBtn.getComp().isEnabled());
		map.put(toAddButtonId, toAddBtn.getComp().isEnabled());
		return map;
	}

	private void resetButStatus(Map<String, Boolean> map) {
		for (String s : map.keySet()) {
			switch (s) {
			case loginButtonId:
				loginBtn.setEnabled(map.get(s));
				break;
			case testButtonId:
				testConnBtn.setEnabled(map.get(s));
				break;
			case cancelButtonId:
				cancelBtn.setEnabled(map.get(s));
				break;
			case saveButtonId:
				saveBtn.setEnabled(map.get(s));
				break;
			case updateButtonId:
				updateBtn.setEnabled(map.get(s));
				break;
			case toAddButtonId:
				toAddBtn.setEnabled(map.get(s));
				break;

			default:
				break;
			}
		}
	}

	private void setButEnabled(boolean bool) {
		loginBtn.setEnabled(bool);
		testConnBtn.setEnabled(bool);
		cancelBtn.setEnabled(bool);
		saveBtn.setEnabled(bool);
		updateBtn.setEnabled(bool);
		toAddBtn.setEnabled(bool);
	}

	/**
	 * 登录成功之后做的操作
	 *
	 * @param loginBean 连接bean
	 */
	public abstract void inform();
}
