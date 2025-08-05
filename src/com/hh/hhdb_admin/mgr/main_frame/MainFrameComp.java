package com.hh.hhdb_admin.mgr.main_frame;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HBasePanel;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HLayeredComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HMgrTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.ctrl.HImgButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.hmenu.HMenuBar;
import com.hh.frame.swingui.view.layout.HLayerLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CSProduct;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.bean.CloseBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.comp.cmd.SqlCmdSplitPanel;
import com.hh.hhdb_admin.comp.sql.SqlSplitPanel;
import com.hh.hhdb_admin.mgr.mongo.MongodbOpenCollComp;
import com.hh.hhdb_admin.mgr.multi_view.comp.MongodbViewComp;
import com.hh.hhdb_admin.mgr.pack.PackageComp;
import com.hh.hhdb_admin.mgr.quick_query.QuickQueryComp;
import com.hh.hhdb_admin.mgr.table_open3.gui.OpenTableComp;
import com.hh.hhdb_admin.mgr.vm_editor.VmComp;


/**
 * @author Jiang
 * @date 2020/10/10
 */

public class MainFrameComp extends HFrame {
	private final Map<String, SharePaneObj> tabMap = new HashMap<>();
	private HSplitPanel splitPanel;
	private HBarPanel toolBar;
	private HMenuBar menubar;
	private HBarPanel statusPanel;
	private HMgrTabPanel tabPane;

	public MainFrameComp() {
		setWindowTitle(new CSProduct().getName(LangMgr.langType));
		setIconImage(IconFileUtil.getLogo());
		window.setPreferredSize(getPreSize());
//		window.pack();
		window.setLocationRelativeTo(null);
		((JFrame) window).setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (PopDlgUtil.confirm(LangMgr.sLang(CsL.EXIT_THE_PROGRAM))) {
					window.dispose();
//					if (TableOpenMgr.threadPool != null && !TableOpenMgr.threadPool.isShutdown()) {
//						TableOpenMgr.threadPool.shutdownNow();
//					}
					System.exit(0);
				}
			}
		});
		
		HLayeredComp layer = new HLayeredComp(this);
		layer.setLayout(new HLayerLayout() {
			@Override
			public void layoutContainer(Container parent) {
				AbsHComp comp = get(LayerType.RRIGHT_TOP_BTN);
				if(comp == null) {
					return;
				}
				Component c = comp.getComp();
				if(menubar!=null) {
					Insets insets=parent.getInsets();
					c.setBounds(parent.getWidth()-insets.right-c.getPreferredSize().width-xGap, insets.top + menubar.getComp().getY(), c.getPreferredSize().width, c.getPreferredSize().height);
				}
			}
		});
		HImgButton btn = new HImgButton() {
			@Override
			protected void onClick() {
				if(menubar!=null) {
					boolean isVisible = !menubar.getComp().isShowing();
					setMenubarVisible(isVisible);
					FaSolidEnum fa = isVisible?FaSolidEnum.ANGLES_UP:FaSolidEnum.ANGLES_DOWN;
					String text = isVisible?LangMgr.sLang(FrameL.HIDE_MENUBAR):LangMgr.sLang(FrameL.SHOW_MENUBAR);
					setMouseEnteredIcon(fa, 18);
					setMouseExitedIcon(fa, 16);
					setToolTipText(text);
				}
			}
		};
		
		btn.setMouseEnteredIcon(FaSolidEnum.ANGLES_UP,18);
		btn.setMouseExitedIcon(FaSolidEnum.ANGLES_UP,16);
		btn.setToolTipText(LangMgr.sLang(FrameL.HIDE_MENUBAR));
		layer.set(HLayerLayout.LayerType.RRIGHT_TOP_BTN, btn);
	}

	/**
	 * 设置菜单栏
	 *
	 * @param menuBar 菜单栏
	 */
	@Override
	public void setMenubar(HMenuBar menuBar) {
		this.menubar = menuBar;
		super.setMenubar(menuBar);
		setWindowTitle(new CSProduct().getName(LangMgr.langType));
	}

	/**
	 * 设置工具栏
	 *
	 * @param toolBar 工具栏
	 */
	@Override
	public void setToolBar(HBarPanel toolBar) {
		this.toolBar = toolBar;
		super.setToolBar(toolBar);
	}

	/**
	 * 设置主面板
	 *
	 * @param tabPane tab面板
	 */
	public void setRootPanel(HMgrTabPanel tabPane) {
		HSplitPanel splitPanel = new HSplitPanel();
		splitPanel.setSplitWeight(0.2);
		if(tabPane!=null) {
			LastPanel rightPanel = new LastPanel(false);
			rightPanel.set(tabPane.getComp());
			splitPanel.setLastComp4Two(rightPanel);
			this.tabPane = tabPane;
		}
		//解决切换登录时HSplitPanel会全展开的问题，由于左边面板未设置，setSplitWeight(0.2)会失效导致全展开
		splitPanel.setPanelOne(new HPanel());
		this.splitPanel = splitPanel;
		setRootPanel(splitPanel);
	}

	/**
	 * 设置shu
	 *
	 * @param tree 树插件
	 */
	public void setTree(HPanel tree) {
		this.splitPanel.setPanelOne(tree);
		setRootPanel(splitPanel);
	}

	/**
	 * 设置状态栏
	 *
	 * @param statusPanel 状态栏
	 */
	public void setStatusPanel(HBarPanel statusPanel) {
		this.statusPanel = statusPanel;
		setStatusBar(statusPanel);
	}

	/**
	 * 设置工具栏是否显示
	 *
	 * @param visible 是否显示
	 */
	public void setToolbarVisible(boolean visible) {
		if(toolBar!=null) {
			window.remove(toolBar.getComp());
			super.setToolBar(visible ? toolBar : new HBarPanel());
			window.validate();
			window.repaint();
		}
	}

	/**
	 * 设置菜单栏是否显示
	 *
	 * @param visible 是否显示
	 */
	public void setMenubarVisible(boolean visible) {
		if(menubar!=null) {
			window.remove(menubar.getComp());
			super.setMenubar(visible ? menubar : new HMenuBar());
			window.validate();
			window.repaint();
		}
		
	}

	/**
	 * 设置状态栏是否显示
	 *
	 * @param visible 是否显示
	 */
	public void setStatusVisible(boolean visible) {
		if(statusPanel!=null) {
			window.remove(statusPanel.getComp());
			setStatusBar(visible ? statusPanel : new HBarPanel());
			window.validate();
			window.repaint();
		}
	}

	/**
	 * 新增一个tab页
	 * @param shareObj TAB页对象
	 */
	public void addTabPaneItem(SharePaneObj shareObj) {
		if (!tabMap.containsKey(shareObj.getId())) {
			HeaderConfig config = new HeaderConfig(shareObj.getTitle());
			config.setTitleEditable(true);
			config.setDetachEnabled(true);
			config.setTitleEditable(false);
			config.setFixTab(shareObj.isFixTab());
			
			Object obj = shareObj.getObj();
			if (obj instanceof LastPanel) {
				tabPane.setPanel(shareObj.getId(), ((LastPanel) obj), config);
			} else if (obj instanceof HBasePanel) {
				tabPane.setPanel(shareObj.getId(), ((HBasePanel) obj), config);
			} else if (obj instanceof HSplitPanel) {
				tabPane.setPanel(shareObj.getId(), ((HSplitPanel) obj), config);
			} else {
				LastPanel lastPanel = new LastPanel();
				lastPanel.set(((AbsHComp) obj).getComp());
				tabPane.setPanel(shareObj.getId(), lastPanel, config);
			}
			tabMap.put(shareObj.getId(), shareObj);
		}
		tabPane.selectPanel(shareObj.getId());
	}

	/**
	 * tab页关闭处理
	 *
	 * @param id tab id
	 */
	public void onTabPaneClose(String id) {
		SharePaneObj obj = tabMap.get(id);
		if (obj == null || obj.getCsMgrEnum() == null) return;
		CsVarUtil.eng2.doCall(obj.getCsMgrEnum(),new CloseBean(id));
		tabMap.remove(id);
	}

	/**
	 * 确认关闭提示
	 *
	 * @param id
	 */
	public boolean quTabPaneClose(String id) {
		SharePaneObj obj = tabMap.get(id);
		if (obj == null || obj.getCsMgrEnum() == null) return true;

		if (obj.getCsMgrEnum() == CsMgrEnum.QUERY) {
			SqlSplitPanel query = (SqlSplitPanel) obj.getObj();
			if (!StringUtils.isNotBlank(query.getText())) {
				return true;
			}
			Object[] options = {LangMgr.lang(Word1.YES), LangMgr.lang(Word1.NO), LangMgr.lang(Word2.CANCEL)};
			int result = JOptionPane.showOptionDialog(CsVarUtil.parentFrame.getWindow(), LangMgr.sLang(CsL.SAVE_SQL), LangMgr.lang(Word2.HINT), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			if (result == 0) {
				PackageComp.saveSqlBook(query.getText(), false);
				return true;
			} else {
				return result == 1;
			}
		} else if (obj.getCsMgrEnum() == CsMgrEnum.TABLE_OPEN) {
			
//			if(obj.getObj() instanceof ModifyTabDataComp) {
//				ModifyTabDataComp modifyTabDataComp = (ModifyTabDataComp) obj.getObj();
//				return modifyTabDataComp.isCancelSaveData();
//			}else {
				OpenTableComp tabComp = (OpenTableComp) obj.getObj();
				return tabComp.isCancelSaveData();
//			}
			
			
		} else if (obj.getCsMgrEnum() == CsMgrEnum.MONGO_MGR) {
			if(obj.getObj() instanceof MongodbViewComp) {
				MongodbViewComp comp = (MongodbViewComp)obj.getObj();
				return comp.isCancelSaveData();
			}
			if(obj.getObj() instanceof MongodbOpenCollComp) {
				MongodbOpenCollComp comp = (MongodbOpenCollComp)obj.getObj();
				return comp.isCancelSaveData();
			}
		}
		return true;

	}
	
	/**
	 * 设置编辑器焦点
	 * @param id
	 */
	public void setTextAreaFocus(String id){
		SharePaneObj obj = tabMap.get(id);
		if (obj == null) return;
		if (obj.getCsMgrEnum() == CsMgrEnum.QUERY) {
			SqlSplitPanel query = (SqlSplitPanel) obj.getObj();
			query.getHarea().requestFocusInWindow();
		} else if (obj.getCsMgrEnum() == CsMgrEnum.CMD) {
			SqlCmdSplitPanel cmd = (SqlCmdSplitPanel) obj.getObj();
			cmd.getHarea().requestFocusInWindow();
		} else if (obj.getCsMgrEnum() == CsMgrEnum.VM) {
			VmComp vm = (VmComp) obj.getObj();
			vm.getHarea().requestFocusInWindow();
		} else if (obj.getCsMgrEnum() == CsMgrEnum.QUICK_CMD) {
			QuickQueryComp queryComp = (QuickQueryComp) obj.getObj();
			queryComp.textArea.requestFocusInWindow();
		}
	}

	/**
	 * 关闭所有tab
	 */
	public void closeAllTab() {
		tabMap.forEach((id, obj) -> {
			boolean close = quTabPaneClose(id);
			if (close) {
				CsVarUtil.eng2.doCall(obj.getCsMgrEnum(),new CloseBean(id));
			}
		});
		Map<String, SharePaneObj> idMap = new LinkedHashMap<>(tabMap);
		tabMap.clear();
		idMap.keySet().forEach(id -> tabPane.close(id));
	}

	/**
	 * 根据屏幕分辨率获取默认大小
	 *
	 * @return 默认大小
	 */
	private Dimension getPreSize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		if (width > 1920) {
			return new Dimension(1800, 1150);
		} else {
			return new Dimension(1300, 850);
		}
	}

	public HMgrTabPanel getTabPane() {
		return tabPane;
	}
}
