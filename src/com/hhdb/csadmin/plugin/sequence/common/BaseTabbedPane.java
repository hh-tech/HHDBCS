package com.hhdb.csadmin.plugin.sequence.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.common.util.IconUtilities;


/**
 * 面板类
 * 
 * @author tsp
 */
public class BaseTabbedPane extends JPanel {
	private static final long serialVersionUID = -9007068462231539973L;
	private BaseTabbedPaneCustom pane = null;
	private Icon closeInactiveButtonIcon;
	private Icon closeActiveButtonIcon;
	private boolean closeEnabled = false;
	/**
	 * The default Hand cursor.
	 */
	public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	/**
	 * The default Text Cursor.
	 */
	public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

	public BaseTabbedPane() {
		this(JTabbedPane.TOP);
	}

	/**
	 * @param type
	 *            对齐方式
	 * @param closeEnabled
	 *            是否可关闭面板
	 */
	public BaseTabbedPane(int type, boolean closeEnabled) {
		this(type);
		this.closeEnabled = closeEnabled;
	}

	/**
	 * @param closeEnabled
	 *            是否可关闭面板
	 */
	public BaseTabbedPane(boolean closeEnabled) {
		this(JTabbedPane.TOP);
		this.closeEnabled = closeEnabled;
		if (closeEnabled) {
			pane.setUI(new TabbedPaneUI());
		}
	}

	public BaseTabbedPane(final int type) {
		pane = new BaseTabbedPaneCustom(type);
		pane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		setLayout(new BorderLayout());
		add(pane);
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
				int index = sourceTabbedPane.getSelectedIndex();
				if (index >= 0) {
					// fireTabSelected(getTabAt(index),
					// getTabAt(index).getComponent(), index);
				}
			}
		};
		pane.addChangeListener(changeListener);

		closeInactiveButtonIcon = IconUtilities.loadIcon("close.gif");
		
		closeActiveButtonIcon = IconUtilities.loadIcon("close1.gif");
		
	}

	public void addTab(String title, Icon icon, final JComponent component) {
		 addTab(title, icon, component, null);
	}

	public void addTab(String title, Icon icon, final JComponent component, boolean closeAble) {
		 addTab(title, icon, component, "decsption", closeAble,true);
	}
	/**
	 * 
	 * @param title 标题
	 * @param icon  图标
	 * @param component 添加的控件
	 * @param closeAble 是否关闭功能
	 * @param tip 标识控件ID
	 * @param moreopen 是否打开多次
	 * @return
	 */
	public void addTab(String title, Icon icon, final JComponent component, boolean closeAble,String tip,boolean moreopen) {
		 addTab(title, icon, component, tip, closeAble,moreopen);
	}
	
	public void addTab(String title, Icon icon, final JComponent component, String tip, boolean closeAble,boolean moreopen) {
			component.setToolTipText(tip);
			final BaseTab tab = new BaseTab(this, component);
			ShrTabPanel tabpanel = new ShrTabPanel(tab, title, icon, closeAble);
			pane.addTab(null, null, tab, tip);
			pane.setTabComponentAt(pane.getTabCount() - 1, tabpanel);
			pane.setSelectedIndex(pane.getTabCount() - 1);
			pane.getSelectedComponent();
			//pane.setUI(new TabbedPaneUI());
	}
	
	public boolean selectComponent(String ident){
		boolean isexits=false;
		Component[] ts=pane.getComponents();
		for(Component cp:ts){
			if(cp instanceof BaseTab){
				//BaseTab bt=(BaseTab)cp;
				//JComponent jcom=(JComponent)bt.getComponent();
				/*if(ident.equals(jcom.getToolTipText())){
					pane.setSelectedIndex(MainTabbedPaneInit.getInstance().getTabbedPane().getTabPosition(bt));
					isexits=true;
				}*/
			}	
		}
		return isexits;
	}
	
	public int selectComponentIndex(String ident){//hw
		Component[] ts=pane.getComponents();
		for(Component cp:ts){
			if(cp instanceof BaseTab){
				//BaseTab bt=(BaseTab)cp;
				//JComponent jcom=(JComponent)bt.getComponent();
				/*if(ident.equals(jcom.getToolTipText())){
					pane.setSelectedIndex(MainTabbedPaneInit.getInstance().getTabbedPane().getTabPosition(bt));
				}*/
			}	
		}
		return pane.getSelectedIndex();
	}
	
	public BaseTab addTab(String title, Icon icon, final JComponent component, String tip) {
		component.setToolTipText(tip);
		final BaseTab tab = new BaseTab(this, component);
		ShrTabPanel tabpanel = new ShrTabPanel(tab, title, icon);
		pane.addTab(null, null, tab, tip);
		pane.setTabComponentAt(pane.getTabCount() - 1, tabpanel);
		pane.setSelectedIndex(pane.getTabCount() - 1);
		pane.getSelectedComponent();
		return tab;
	}

	public BaseTab getTabAt(int index) {
		return ((BaseTab) pane.getComponentAt(index));
	}

	public int getTabPosition(BaseTab tab) {
		return pane.indexOfComponent(tab);
	}

	public Component getComponentInTab(BaseTab tab) {
		return tab.getComponent();
	}

	public void setIconAt(int index, Icon icon) {
		Component com = pane.getTabComponentAt(index);
		if (com instanceof ShrTabPanel) {
			ShrTabPanel panel = (ShrTabPanel) com;
			panel.setIcon(icon);
		}
	}

	public void setTitleAt(int index, String title) {
		if (index > 0) {
			Component com = pane.getTabComponentAt(index);
			if (com instanceof ShrTabPanel) {
				ShrTabPanel panel = (ShrTabPanel) com;
				panel.setTitle(title);
			}
		}
	}

	public void setTitleColorAt(int index, Color color) {
		Component com = pane.getTabComponentAt(index);
		if (com instanceof ShrTabPanel) {
			ShrTabPanel panel = (ShrTabPanel) com;
			panel.setTitleColor(color);
		}
	}

	public void setTitleBoldAt(int index, boolean bold) {
		Component com = pane.getTabComponentAt(index);
		if (com instanceof ShrTabPanel) {
			ShrTabPanel panel = (ShrTabPanel) com;
			panel.setTitleBold(bold);
		}
	}

	public void setTitleFontAt(int index, Font font) {
		Component com = pane.getTabComponentAt(index);
		if (com instanceof ShrTabPanel) {
			ShrTabPanel panel = (ShrTabPanel) com;
			panel.setTitleFont(font);
		}
	}

	public Font getDefaultFontAt(int index) {
		Component com = pane.getTabComponentAt(index);
		if (com instanceof ShrTabPanel) {
			ShrTabPanel panel = (ShrTabPanel) com;
			return panel.getDefaultFont();
		}
		return null;
	}

	public String getTitleAt(int index) {
		return pane.getTitleAt(index);
	}

	public int getTabCount() {
		return pane.getTabCount();
	}

	public void setSelectedIndex(int index) {
		pane.setSelectedIndex(index);
	}

	public int indexOfComponent(Component component) {
		for (Component comp : pane.getComponents()) {
			if (comp instanceof BaseTab) {
				BaseTab tab = (BaseTab) comp;
				if (tab.getComponent() == component)
					return pane.indexOfComponent(tab);
			}
		}
		return -1;
	}

	public Component getComponentAt(int index) {
		return ((BaseTab) pane.getComponentAt(index)).getComponent();
	}

	public Component getTabComponentAt(int index) {
		return pane.getTabComponentAt(index);
	}

	public Component getTabComponentAt(BaseTab tab) {
		return pane.getTabComponentAt(indexOfComponent(tab));
	}

	public Component getSelectedComponent() {
		if (pane.getSelectedComponent() instanceof BaseTab) {
			BaseTab tab = (BaseTab) pane.getSelectedComponent();
			return tab.getComponent();
		}
		return null;
	}

	public void removeTabAt(int index) {
		pane.remove(index);
	}

	public int getSelectedIndex() {
		return pane.getSelectedIndex();
	}

	public void setCloseButtonEnabled(boolean enable) {
		closeEnabled = enable;
	}

	public JPanel getMainPanel() {
		return this;
	}

	public void removeComponent(Component comp) {
		int index = indexOfComponent(comp);
		if (index != -1) {
			removeTabAt(index);
		}
	}
	/**
	 * 关闭对应控件中的线程及socket
	 * @param tab
	 */
	public void close(BaseTab tab) {
		int closeTabNumber = pane.indexOfComponent(tab);
		/*if (tab.getComponent() instanceof QueryEditorMainPanel) {
			QueryEditorMainPanel sqlmain=(QueryEditorMainPanel)tab.getComponent();
			sqlmain.closeNewDbResult();
		}else if(tab.getComponent() instanceof MonitorCpuPanel){
			MonitorCpuPanel monistor=(MonitorCpuPanel)tab.getComponent();
			monistor.closeRunable();
		} else if(tab.getComponent() instanceof MonitorPanel4Disk){
			MonitorPanel4Disk monistor=(MonitorPanel4Disk)tab.getComponent();
			monistor.closeRunable();
		} else if(tab.getComponent() instanceof MonitorPanel4Net){
			MonitorPanel4Net monistor=(MonitorPanel4Net)tab.getComponent();
			monistor.closeRunable();
		}else if(tab.getComponent() instanceof MonitorMemPanel){
			MonitorMemPanel monistor=(MonitorMemPanel)tab.getComponent();
			monistor.closeRunable();
		}else if(tab.getComponent() instanceof QueryEditorMainPanel){
			QueryEditorMainPanel sqlmain=(QueryEditorMainPanel)tab.getComponent();
			sqlmain.closeDbResult();
		}else if(tab.getComponent() instanceof ConsolePanel){
			ConsolePanel sqlmain=(ConsolePanel)tab.getComponent();
			sqlmain.close();
		}else if(tab.getComponent() instanceof NaturePanel){
			NaturePanel sqlmain=(NaturePanel)tab.getComponent();
			sqlmain.closeRunable();;
		}*/
		pane.removeTabAt(closeTabNumber);
	}

	private class ShrTabPanel extends JPanel {
		private static final long serialVersionUID = -8249981130816404360L;
		private final Font defaultFont = new Font("SimSun", Font.PLAIN, 13);
		private JLabel iconLabel;
		private JLabel titleLabel;

		public ShrTabPanel(final BaseTab tab, String title, Icon icon, boolean close) {
			setOpaque(false);
			this.setLayout(new BorderLayout());
			titleLabel = new JLabel(title);
			titleLabel.setFont(defaultFont);
			if (icon != null) {
				iconLabel = new JLabel(icon);
				add(iconLabel, BorderLayout.WEST);
			}
			add(titleLabel, BorderLayout.CENTER);
			if (close) {
				final JLabel tabCloseButton = new JLabel(closeInactiveButtonIcon);
				tabCloseButton.addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent mouseEvent) {
						tabCloseButton.setIcon(closeActiveButtonIcon);
						setCursor(HAND_CURSOR);
					}

					public void mouseExited(MouseEvent mouseEvent) {
						tabCloseButton.setIcon(closeInactiveButtonIcon);
						setCursor(DEFAULT_CURSOR);
					}

					public void mousePressed(MouseEvent mouseEvent) {
						new Thread() {
							@Override
							public void run() {
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
								
									LM.error(LM.Model.CS.name(), e);
								}
								close(tab);
							}
						}.start();
					}
				});
				add(tabCloseButton, BorderLayout.EAST);
			}
		}

		public ShrTabPanel(final BaseTab tab, String title, Icon icon) {
			setOpaque(false);
			this.setLayout(new BorderLayout());
			titleLabel = new JLabel(title);
			titleLabel.setFont(defaultFont);
			if (icon != null) {
				iconLabel = new JLabel(icon);
				add(iconLabel, BorderLayout.WEST);
			}
			add(titleLabel, BorderLayout.CENTER);
			if (closeEnabled) {
				final JLabel tabCloseButton = new JLabel(closeInactiveButtonIcon);
				tabCloseButton.addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent mouseEvent) {
						tabCloseButton.setIcon(closeActiveButtonIcon);
						setCursor(HAND_CURSOR);
					}

					public void mouseExited(MouseEvent mouseEvent) {
						tabCloseButton.setIcon(closeInactiveButtonIcon);
						setCursor(DEFAULT_CURSOR);
					}

					public void mousePressed(MouseEvent mouseEvent) {
						new Thread() {
							@Override
							public void run() {
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									
									LM.error(LM.Model.CS.name(), e);
								}
								close(tab);
							}
						}.start();
					}
				});
				add(tabCloseButton, BorderLayout.EAST);
			}
		}

		public Font getDefaultFont() {
			return defaultFont;
		}

		public void setIcon(Icon icon) {
			iconLabel.setIcon(icon);
		}

		public void setTitle(String title) {
			titleLabel.setText(title);
		}

		public void setTitleColor(Color color) {
			titleLabel.setForeground(color);
			titleLabel.validate();
			titleLabel.repaint();
		}

		public void setTitleBold(boolean bold) {
			Font oldFont = titleLabel.getFont();
			Font newFont;
			if (bold) {
				newFont = new Font(oldFont.getFontName(), Font.BOLD, oldFont.getSize());
			} else {
				newFont = new Font(oldFont.getFontName(), Font.PLAIN, oldFont.getSize());
			}
			titleLabel.setFont(newFont);
			titleLabel.validate();
			titleLabel.repaint();
		}

		public void setTitleFont(Font font) {
			titleLabel.setFont(font);
			titleLabel.validate();
			titleLabel.repaint();
		}
	}
	public BaseTabbedPaneCustom getTabCompants(){
		return pane;
	}
}
