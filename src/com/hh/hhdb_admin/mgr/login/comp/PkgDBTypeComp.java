package com.hh.hhdb_admin.mgr.login.comp;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HBorderPanel;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.hmenu.HPopMenu;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.HBorderLayout.PositionType;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tree2.FolderBean;
import com.hh.frame.swingui.view.tree2.HTree2;
import com.hh.frame.swingui.view.tree2.LeafBean;
import com.hh.frame.swingui.view.util.ImgUtil;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.ExtDbTypeUtil;

class PkgDBTypeComp extends AbsHComp {
	private HSplitPanel splipPanel;
	private boolean isUpdate = false;
	private String oldName = "";
	private File iconDir;
	private File libDir;
	private HBarPanel statusPanel;
	private LabelInput statusText;
	private PkgAddDBTypeComp comp2;
	private HBorderPanel treePanel;

	public PkgDBTypeComp(File iconDir, File libDir) {
		this.iconDir = iconDir;
		this.libDir = libDir;

		splipPanel = new HSplitPanel(true);
		comp2 = new PkgAddDBTypeComp(iconDir, libDir);

		treePanel = new HBorderPanel();
		treePanel.set(PositionType.CENTER, getTree());
		splipPanel.setPanelOne(treePanel);

		HBorderPanel borderPanel2 = new HBorderPanel();
		borderPanel2.set(PositionType.CENTER, comp2);

		initStatusPanel();

		borderPanel2.set(PositionType.NORTH, statusPanel);

		HBarLayout layout = new HBarLayout();
		layout.setAlign(AlignEnum.CENTER);
		layout.setBottomHeight(10);
		layout.setTopHeight(10);
		HBarPanel barPanel = new HBarPanel(layout);
		HButton save = new HButton(LangMgr.lang(Word2.SAVE)) {
			@Override
			protected void onClick() {
				try {
					DBTypeBean b = comp2.getDBtypeBean();
					verify(b);
					verifyDirs();
					
					if (isUpdate&&!b.getName().equals(oldName)) {
						ExtDbTypeUtil.removeTypeAndJar(oldName);
					} 
						
					ExtDbTypeUtil.saveAndLoadTypeJar(b);
					PopDlgUtil.info(splipPanel.getComp(), LangMgr.lang(Word4.SAVED_SUCCESSFULLY));
					reloadTreeData();
					infoRefresh();
				} catch (Exception e) {
					Lg.error(getClass(), e);
					PopDlgUtil.error(splipPanel.getComp(), e);
				}
			}
		};
		save.setIcon(FaSolidEnum.FLOPPY_DISK, 16);
		barPanel.add(save);
		borderPanel2.set(PositionType.SOUTH, barPanel);
		splipPanel.setPanelTwo(borderPanel2);
		splipPanel.setDividerLocation(200);
		comp = splipPanel.getComp();
	}

	private void initStatusPanel() {
		statusText = new LabelInput(LangMgr.sLang(CsL.ADD_STATUS), AlignEnum.RIGHT);
		HBarLayout barLayout = new HBarLayout();
		barLayout.setTopHeight(10);
		barLayout.setAlign(AlignEnum.CENTER);
		barLayout.setxGap(10);
		statusPanel = new HBarPanel(barLayout);
		statusText.setIcon(FaSolidEnum.LIGHTBULB, 16);
		statusPanel.add(statusText);
	}

	private HTree2 getTree() {
		HTree2 tree = new HTree2(toData()) {
			@Override
			protected void oneClickNode(LeafBean bean) {
				if (bean.isLeaf()) {
					statusPanel.removeAll();
					comp2.loadDbTypeBean(new DBTypeBean(bean.getJobj()));
					statusText.setValue(LangMgr.sLang(CsL.UPDATE_STATUS));
					statusPanel.add(statusText);
					isUpdate = true;
					oldName = bean.getName();
					HButton button = new HButton(LangMgr.sLang(CsL.SWITCH_TO_ADD)) {
						protected void onClick() {
							reload();
							changeToAdd();
						};
					};
					button.setToolTipText(LangMgr.sLang(CsL.SWITCH_TIP));
					statusPanel.add(button);
				} else {
					changeToAdd();
				}
			}

			@Override
			protected void rightClickNode(List<LeafBean> selBeans, MouseEvent e) {
				HPopMenu menu = new HPopMenu();
				if (selBeans.isEmpty()) {
					return;
				}
				if (selBeans.size() == 1) {
					if (selBeans.get(0).isLeaf()) {
						menu.addItem(getDelMenuItem(selBeans));
					} else {
						return;
					}
				} else {
					menu.addItem(getDelMenuItem(selBeans));
				}

				menu.showPopup(e);
			}
		};
		return tree;
	}

	private void changeToAdd() {
		comp2.reset();
		statusPanel.removeAll();
		statusText.setValue(LangMgr.sLang(CsL.ADD_STATUS));
		statusPanel.add(statusText);
		isUpdate = false;
		oldName = "";
	}

	private void reloadTreeData() {
		treePanel.removeAll();
		treePanel.set(PositionType.CENTER, getTree());
	}

	protected void infoRefresh() {

	}

	private HMenuItem getDelMenuItem(List<LeafBean> selBeans) {
		HMenuItem item = new HMenuItem(LangMgr.lang(Word2.DELETE)) {
			@Override
			protected void onAction() {
				
				
				if(!PopDlgUtil.confirm(treePanel.getComp(), LangMgr.sLang(FrameL.CONFIRM_DELETE))) {
					return;
				}
				try {
					for (LeafBean b : selBeans) {
						if (b.isLeaf()) {
							ExtDbTypeUtil.removeTypeAndJar(b.getName());
						}
					}
				} catch (Exception e) {
					PopDlgUtil.error(getComp(), e);
					Lg.error(getClass(), e);
				}

				infoRefresh();
				reloadTreeData();
				changeToAdd();
			}
		};
		item.setFontIcon(FaSolidEnum.TRASH_CAN);
		return item;
	}

	private void verify(DBTypeBean b) throws Exception {
		if (StringUtils.isBlank(b.getName())) {
			throw new Exception(LangMgr.sLang(CsL.NAME_EMPTY_TIP));
		}
		if (StringUtils.isBlank(b.getClazz())) {
			throw new Exception(LangMgr.sLang(CsL.CLASS_EMPTY_TIP));
		}
		if (StringUtils.isBlank(b.getUrl())) {
			throw new Exception(LangMgr.sLang(CsL.URL_EMPTY_TIP));
		}
		if (b.getDriver() != null && b.getDriver().size() == 0) {
			throw new Exception(LangMgr.sLang(CsL.UPLOAD_TIP));
		}
	}

	private void verifyDirs() throws IOException {
		if (!libDir.exists()) {
			FileUtils.forceMkdir(libDir);
		}
		if (!iconDir.exists()) {
			FileUtils.forceMkdir(iconDir);
		}
	}

	private FolderBean toData() {
		FolderBean root = new FolderBean(LangMgr.sLang(CsL.EXTENDED_DATABASE_TYPE));

		root.setFontType(FaSolidEnum.HOUSE);
		for (Entry<String, DBTypeBean> a : ExtDbTypeUtil.selfDefinedDbTypeMaps.entrySet()) {
			LeafBean leaf = new LeafBean(a.getKey());
			leaf.setJobj(a.getValue().getJobj());
			leaf.setFontType(FaSolidEnum.DATABASE);
			if (!StringUtils.isBlank(a.getValue().getLogo())) {
				File f = new File(iconDir, a.getValue().getLogo());
				if (f.exists()) {
					if (f.getName().toLowerCase().endsWith("svg")) {
						leaf.setIcon(ImgUtil.readSvgIcon(f));
					} else {
						leaf.setIcon(ImgUtil.readImgIcon(f));
					}
				}
			}

			root.add(leaf);
		}
		return root;
	}

	public static void main(String[] args) {
		HFrame frame = new HFrame();
		CsVarUtil.init();
		PkgDBTypeComp c = new PkgDBTypeComp(new File("icon"), new File("lib"));
		frame.setRootPanel(c);
		frame.show();

	}
}
