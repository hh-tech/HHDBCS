package com.hh.hhdb_admin.mgr.main_frame;

import com.alee.laf.combobox.WebComboBox;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.style.Skin;
import com.alee.managers.style.StyleManager;
import com.alee.utils.CoreSwingUtils;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.container.tab_panel.HSplitTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.hmenu.HMenuBar;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.ui.other.BottomStatusBar;
import com.hh.frame.swingui.view.ui.skin.AbstractHhSkin;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.query.QueryComp;
import com.hh.hhdb_admin.mgr.query.util.QuerUtil;
import com.hh.hhdb_admin.mgr.table_open.TableOpenMgr;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Jiang
 * @date 2020/10/10
 */

public class MainFrameComp extends HFrame {

	private static final String DOMAIN_NAME = MainFrameComp.class.getName();
	private AbsHComp statusBar;

	static {
		try {
			LangMgr2.loadMerge(MainFrameComp.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private final Map<String, CsMgrEnum> tabMap = new HashMap<>();

	private HSplitPanel splitPanel;
	private HBarPanel toolBar;
	private HMenuBar menubar;
	private HBarPanel statusPanel;
	private HSplitTabPanel tabPane;
	private Integer divideLocation = 0;

	public MainFrameComp() {
		setWindowTitle(getLang("windowTitle"));
		setIconImage(IconFileUtil.getLogo());
		window.setPreferredSize(getPreSize());
		window.pack();
		window.setLocationRelativeTo(null);
		((JFrame) window).setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (PopPaneUtil.confirm(getLang("exit"))) {
					window.dispose();
					if (TableOpenMgr.threadPool != null && !TableOpenMgr.threadPool.isShutdown()) {
						TableOpenMgr.threadPool.shutdownNow();
					}
					System.exit(0);
				}
			}
		});
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
		setWindowTitle(getLang("windowTitle"));
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
	 * @param tree    树插件
	 * @param tabPane tab面板
	 */
	public void setRootPanel(HPanel tree, HSplitTabPanel tabPane) {
		HSplitPanel splitPanel = new HSplitPanel();
		splitPanel.setSplitWeight(0.2);
		LastPanel rightPanel = new LastPanel(false);
		rightPanel.set(tabPane.getComp());
		splitPanel.setPanelOne(tree);
		splitPanel.setLastComp4Two(rightPanel);
		this.splitPanel = splitPanel;
		this.tabPane = tabPane;
		setRootPanel(splitPanel);

		if (statusBar != null) {
			window.remove(statusBar.getComp());
		}
		statusBar = new BottomStatusBar(new SwitchListener());
		window.add(statusBar.getComp(), BorderLayout.SOUTH);

		// Custom status bar margin for notification manager
		NotificationManager.setMargin(0, 0, statusBar.getComp().getPreferredSize().height, 0);


	}


	public class SwitchListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			CoreSwingUtils.invokeLater(() -> {
				WebComboBox comboBox = (WebComboBox) e.getSource();
				Skin skin = (Skin) comboBox.getSelectedItem();
				Skin oldSkin = StyleManager.getSkin();
				if (skin != null && oldSkin != skin) {
					//HHSwingUi.switchSkin((AbstractHhSkin) skin);
					//System.out.println(UIManager.get("MenuBar.background"));
					boolean confirm = PopPaneUtil.confirm(window, "切换主题需要重新加载!");
					if (confirm) {
						try {
							HHSwingUi.switchSkin((AbstractHhSkin) skin);
							StartUtil.writeSkinToFile((AbstractHhSkin) skin);
						} catch (IOException ioException) {
							System.out.println("写入主题文件失败");
						}
						showNewComp();
					} else {
						comboBox.setSelectedItem(oldSkin);
					}
				}
			});
		}

	}

	protected void showNewComp() {
		closeAllTab();
		window.dispose();
		StartUtil.init();
		IconFileUtil.reset();
		StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.CMD_SHOW));
	}

	/**
	 * 设置shu
	 *
	 * @param tree 树插件
	 */
	public void setTree(HPanel tree) {
		this.splitPanel.setPanelOne(tree);
		setRootPanel(splitPanel);
		PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("switchSchemaSuccess"));
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
	 * 设置左侧树插件是否显示
	 *
	 * @param visible 是否显示
	 */
	public void setTreeVisible(boolean visible) {
		if (visible) {
			splitPanel.setDividerLocation(divideLocation);
		} else {
			divideLocation = splitPanel.getDividerLocation();
			splitPanel.setDividerLocation(0);
		}
		splitPanel.setOneTouchBtnShow(visible);
		window.validate();
		window.repaint();
	}

	/**
	 * 设置工具栏是否显示
	 *
	 * @param visible 是否显示
	 */
	public void setToolbarVisible(boolean visible) {
		window.remove(toolBar.getComp());
		super.setToolBar(visible ? toolBar : new HBarPanel());
		window.validate();
		window.repaint();
	}

	/**
	 * 设置菜单栏是否显示
	 *
	 * @param visible 是否显示
	 */
	public void setMenubarVisible(boolean visible) {
		window.remove(menubar.getComp());
		super.setMenubar(visible ? menubar : new HMenuBar());
		window.validate();
		window.repaint();
	}

	/**
	 * 设置状态栏是否显示
	 *
	 * @param visible 是否显示
	 */
	public void setStatusVisible(boolean visible) {
		window.remove(statusPanel.getComp());
		setStatusPanel(visible ? statusPanel : new HBarPanel());
		window.validate();
		window.repaint();
	}

	/**
	 * 新增一个tab页
	 *
	 * @param jsonObject TAB页面信息json
	 */
	public void addTabPaneItem(JsonObject jsonObject) {
		String id = jsonObject.getString(MainFrameMgr.PARAM_ID);
		String title = jsonObject.getString(MainFrameMgr.PARAM_TITLE);
		String mgrType = jsonObject.getString(MainFrameMgr.PARAM_MGR_TYPE);
		Object obj = StartUtil.eng.getSharedObj(id);
		if (!tabMap.containsKey(id)) {
			HeaderConfig config = new HeaderConfig(title);
			config.setTitleEditable(true);
			config.setDetachEnabled(true);
			
			if (obj instanceof LastPanel) {
				tabPane.addPanel(id, ((LastPanel) obj), config);
			} else if (obj instanceof HBasePanel) {
				tabPane.addPanel(id, ((HBasePanel) obj), config);
			} else if (obj instanceof HSplitPanel) {
				tabPane.addPanel(id, ((HSplitPanel) obj), config);
			} else {
				LastPanel lastPanel = new LastPanel();
				lastPanel.set(((AbsHComp) obj).getComp());
				tabPane.addPanel(id, lastPanel, config);
			}
			tabMap.put(id, CsMgrEnum.valueOf(mgrType));
		}
		tabPane.selectPanel(id);
	}

	/**
	 * tab页关闭处理
	 *
	 * @param id tab id
	 */
	public void onTabPaneClose(String id) {
		CsMgrEnum target = tabMap.get(id);
		if (target == null) {
			return;
		}
		StartUtil.eng.doPush(target, GuiJsonUtil.toJsonCmd(StartUtil.CMD_CLOSE).add(StartUtil.CMD_ID, id));
		tabMap.remove(id);
	}
	
	/**
	 * 查询器关闭前处理
	 * @param id
	 */
	public boolean quTabPaneClose(String id) {
		CsMgrEnum target = tabMap.get(id);
		if (target == null) return true;
		
		if (target == CsMgrEnum.QUERY) {
			QueryComp query = (QueryComp)StartUtil.eng.getSharedObj(id);
			if (!StringUtils.isNotBlank(query.getTextArea().getText())) return true;
			Object[] options = {getLang("yes"),getLang("no"),getLang("cancel")};
			int result= JOptionPane.showOptionDialog(StartUtil.parentFrame.getWindow(),getLang("saveSQL"),getLang("hint"),JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
			if (result == 0){
				QuerUtil.saveSqlBook(query.getTextArea().getText(),false);
				return true;
			} else if (result == 1){
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
	
	public String getLang(String key) {
		LangMgr2.setDefaultLang(StartUtil.default_language);
		return LangMgr2.getValue(DOMAIN_NAME, key);
	}

	/**
	 * 关闭所有tab
	 */
	public void closeAllTab() {
		tabMap.forEach((id, csMgrEnum) -> {
			quTabPaneClose(id);
			StartUtil.eng.doPush(csMgrEnum, GuiJsonUtil.toJsonCmd(StartUtil.CMD_CLOSE).add(StartUtil.CMD_ID, id));
		});
		tabMap.clear();
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

	public HSplitTabPanel getTabPane() {
		return tabPane;
	}
}
