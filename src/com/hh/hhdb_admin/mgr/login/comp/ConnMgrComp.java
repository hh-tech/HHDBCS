package com.hh.hhdb_admin.mgr.login.comp;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.BaseConnBean;
import com.hh.frame.common.base.ConnType;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.JUrlUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.bg.conn_mgr.data.ExportTool;
import com.hh.frame.swingui.bg.conn_mgr.data.ImportTool;
import com.hh.frame.swingui.cs_desk.PingPanel;
import com.hh.frame.swingui.cs_desk.PortTestPanel;
import com.hh.frame.swingui.cs_desk.changePass.JdbcChangePassPanel;
import com.hh.frame.swingui.view.connTree2.BasicOpType;
import com.hh.frame.swingui.view.connTree2.ConnTreeComp;
import com.hh.frame.swingui.view.connTree2.ExportComp;
import com.hh.frame.swingui.view.connTree2.ImportComp;
import com.hh.frame.swingui.view.connTree2.base.TreeMenu;
import com.hh.frame.swingui.view.connTree2.base.TreeMenuItem;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tree2.HTree2;
import com.hh.frame.swingui.view.tree2.LeafBean;
import com.hh.frame.swingui.view.util.ImgUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.ExtDbTypeUtil;

public  abstract class ConnMgrComp extends ConnTreeComp {

	public ConnMgrComp(Component parent) throws Exception {
		super(new File(CsVarUtil.getEtcFile(), "login.dat"), true, false);
		addBasicOp(BasicOpType.addDir, BasicOpType.rename, BasicOpType.del, BasicOpType.copy, BasicOpType.paste,
				BasicOpType.clear, BasicOpType.refresh);
		getOpMenuCtrl().addBefore(BasicOpType.rename.name(), "test", createTestMenu());
		
		getOpMenuCtrl().addBefore(BasicOpType.rename.name(), "changepass", changeServerPass());
		
		getOpMenuCtrl().addBefore(BasicOpType.addDir.name(), "import",
				createImportMenuItem(parent, this, new File(CsVarUtil.getEtcFile(), "login.dat")));
		getOpMenuCtrl().addBefore(BasicOpType.addDir.name(), "export",
				createExportMenuItem(parent, new File(CsVarUtil.getEtcFile(), "login.dat")));
	}

	
	@Override
	protected boolean canPaste(BaseConnBean bean) throws Exception {
		if(bean.getType() == ConnType.JDBC) {
			return true;
		}
		return false;
	}
	
	@Override
	protected BaseConnBean toBaseConnBean(JsonObject obj) {
		return new JdbcInfo(obj);
	}
	
	public abstract void dbLogin(LeafBean bean);

	@Override
	protected void updateIcon(LeafBean node) {
		if (node.isLeaf()) {
			JdbcInfo info = new JdbcInfo(node.getJobj());
			if (StringUtils.isBlank(info.getOtherDbType())) {
				ImageIcon icon = IconFileUtil.getDbIcon(DriverUtil.getDbType(info), IconSizeEnum.SIZE_16);
				node.setIcon(icon);
			} else {
				DBTypeBean a = ExtDbTypeUtil.selfDefinedDbTypeMaps.get(info.getOtherDbType());
				if (null != a && !StringUtils.isBlank(a.getLogo())) {
					File f = new File(ExtDbTypeUtil.ICON_FILE, a.getLogo());
					if (f.exists()) {
						if (f.getName().toLowerCase().endsWith("svg")) {
							node.setIcon(ImgUtil.readSvgIcon(f));
						} else {
							node.setIcon(ImgUtil.readImgIcon(f));
						}
					} else {
						node.setFontType(FaSolidEnum.DATABASE);
					}
				} else {
					node.setFontType(FaSolidEnum.DATABASE);
				}
			}
		}
	}

	@Override
	public void dbClickNode(LeafBean bean) {
		if (bean.isLeaf())
			dbLogin(bean);
	}

	public void save(JdbcInfo info) throws Exception {
		List<LeafBean> beans = getTree().getSelected();
		String currentId = null;
		if (beans.size() == 1) {
			currentId = beans.get(0).getId();
		}
		saveResource(currentId, info);
	}

	static boolean isShowTestMenu(List<LeafBean> beans) {

		if (beans.isEmpty()) {
			return false;
		}

		if (beans.size() > 1) {
			return false;
		}
		for (LeafBean lb : beans) {
			if (!lb.isLeaf()) {
				return false;
			}

		}
		return true;
	}

	
	TreeMenuItem changeServerPass() {
		TreeMenuItem item =  new TreeMenuItem(LangMgr.lang(Word2.CHANGE_PASSWORD),FaSolidEnum.ANCHOR_LOCK) {
			@Override
			public void setTree(HTree2 tree) {
				super.setTree(tree);
			}
			
			@Override
			protected void onAction() {
				try {
					HDialog dialog = new HDialog(getTree().getComp(),500,250,false);
					dialog.setWindowTitle(LangMgr.lang(Word2.CHANGE_PASSWORD));
					JdbcChangePassPanel changePass = new JdbcChangePassPanel() {
						protected void onChangeSeverPassSuccess(JdbcBean bean) {
							try {
								updateResource(bean);
							} catch (Exception e) {
								Lg.error(getClass(), e);
								PopDlgUtil.error(getComp(), e);
							}
						};
					};
					changePass.setBean(new JdbcBean(getTree().getSelectBeans().get(0).getJobj()));
					dialog.setStatusBar(new HBarPanel());
					dialog.setRootPanel(changePass);
					dialog.show();
				} catch (Exception e1) {
					Lg.error(getClass(), e1);
					PopDlgUtil.error(getComp(), e1);
				}
			}

			@Override
			public void setStateWhenPop(HTree2 tree) {
				
				boolean isShow;
				List<LeafBean> leafs = tree.getSelectBeans();
				int size = leafs.size();
				if(size == 0||size>1) {
					isShow = false;
				}else {
					isShow = leafs.get(0).isLeaf();
				}
				setVisible(isShow);
				
			}
		};
		
		return item;

	}
	
	TreeMenu createTestMenu() {
		List<TreeMenuItem> items = new ArrayList<>();
		TreeMenu menu = new TreeMenu(LangMgr.lang(Word2.TEST), FaSolidEnum.STETHOSCOPE) {

			@Override
			public void setStateWhenPop(HTree2 tree) {
				if (isShowTestMenu(tree.getSelectBeans())) {
					for (TreeMenuItem i : items) {
						i.setTree(tree);
						i.refresh();
					}
					setVisible(true);
				} else {
					setVisible(false);
				}

			}
		};

		TreeMenuItem ping = new TreeMenuItem("ping", FaSolidEnum.SIGNAL) {

			@Override
			public void setStateWhenPop(HTree2 tree) {
				setEnabled(true);
				setVisible(true);
			}

			@Override
			protected void onAction() {
				PingPanel panel = new PingPanel();
				HDialog dialog = new HDialog(getTree().getComp(), 800, 500, false) {
					@Override
					protected void closeEvent() {
						panel.close();
					}
				};
				dialog.setRootPanel(panel);
				dialog.setWindowTitle("ping");
				dialog.setOkBtnText(LangMgr.lang(Word2.CLOSE));
				dialog.removeCancleBtn();
				dialog.show();
				
				
				JsonObject o = getTree().getSelectBeans().get(0).getJobj();
				
				JdbcBean bean = new JdbcBean(o);
				
				String ip = JUrlUtil.getIp(DriverUtil.getDbType(bean),bean.getCurrentVersion(), bean.getDbUrl());
				if(!StringUtils.isBlank(ip)) {
					panel.startPing(ip);
				}
				

			}
		};

		TreeMenuItem port = new TreeMenuItem("port", FaSolidEnum.CODE_COMMIT) {

			@Override
			public void setStateWhenPop(HTree2 tree) {
				setEnabled(true);
				setVisible(true);
			}

			@Override
			protected void onAction() {

				PortTestPanel panel = new PortTestPanel();
				HDialog dialog = new HDialog(getTree().getComp(), 1000, 700, false) {
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
				
				try {
				JsonObject o = getTree().getSelectBeans().get(0).getJobj();
				JdbcBean bean = new JdbcBean(o);
				String ip = JUrlUtil.getIp(DriverUtil.getDbType(bean),bean.getCurrentVersion(), bean.getDbUrl());
				String port = JUrlUtil.getPort(DriverUtil.getDbType(bean),bean.getCurrentVersion(), bean.getDbUrl());
				if(!StringUtils.isBlank(ip)&&!StringUtils.isBlank(port)) {
					panel.portTest(ip,Integer.parseInt(port));
				}
				}catch (Exception e) {
					Lg.error(getClass(), e);
				}



			}
		};
		items.add(ping);
		items.add(port);
		menu.addTreeMenuItem(ping, port);

		return menu;
	}

	TreeMenuItem createImportMenuItem(Component parent, ConnTreeComp treeComp, File data) {
		return new TreeMenuItem(LangMgr.lang(Word2.IMPORT), FaSolidEnum.FILE_IMPORT) {
			@Override
			protected void onAction() {
				try {
					ImportTool tool = new JdbcImportTool(ConnMgrComp.this.getConnFileMgrTool());
					ImportComp comp = new ImportComp(tool) {
						@Override
						public void doAfterImport() {
							try {
								treeComp.init();
							} catch (Exception e) {
								PopDlgUtil.error(parent, e);
							}
						}
					};
					comp.doImport(parent);
				} catch (Exception e) {
					Lg.error(getClass(), e);
					PopDlgUtil.error(parent, e);
				}
			}

			@Override
			public void setStateWhenPop(HTree2 tree) {
				setVisible(false);
				setEnabled(false);
				List<LeafBean> beans = getTree().getSelectBeans();
				if (beans.size() == 1 && getTree().isRoot(beans.get(0).getId())) {
					setEnabled(true);
					setVisible(true);
				}

			}
		};
	}

	TreeMenuItem createExportMenuItem(Component parent, File data) {
		return new TreeMenuItem(LangMgr.lang(Word2.EXPORT), FaSolidEnum.FILE_EXPORT) {
			@Override
			protected void onAction() {
				try {
					ExportTool tool1 = new JdbcExportTool(ConnMgrComp.this.getConnFileMgrTool());
					ExportComp comp = new ExportComp(tool1);
					comp.doExport( "JDBC连接备份");
				} catch (Exception e) {
					Lg.error(getClass(), e);
					PopDlgUtil.error(parent, e);
				}
			}

			@Override
			public void setStateWhenPop(HTree2 tree1) {
				setVisible(false);
				setEnabled(false);
				List<LeafBean> beans = getTree().getSelectBeans();
				if (beans.size() == 1 && getTree().isRoot(beans.get(0).getId())) {
					setEnabled(true);
					setVisible(true);
				}
			}
		};
	}


	
}
