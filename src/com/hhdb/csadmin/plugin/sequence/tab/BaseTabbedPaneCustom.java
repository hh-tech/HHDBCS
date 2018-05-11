package com.hhdb.csadmin.plugin.sequence.tab;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * <p>
 * ShrTabbedPaneCustom.java
 * </p>
 * <p>
 * <Method Simple Comment>
 * </p>
 * 扩展JTabbedPane,增加关闭、右键菜单、缩略图提示功能
 * 
 * @since 2015年4月30日 上午11:16:12
 */
public class BaseTabbedPaneCustom extends JTabbedPane implements MouseListener {
	private static final long serialVersionUID = 2985098138273905480L;
	/**
	 * 缩略图缩放大小
	 */
	private double scaleRatio = 0.3d;
	private HashMap<String, Component> maps = new HashMap<String, Component>();

	public BaseTabbedPaneCustom() {
		super();
		setBorder(new EmptyBorder(0, 0, 0, 0));
		addMouseListener(this);
	}

	public BaseTabbedPaneCustom(int tabPlacement, int tabLayoutPolicy) {
		super(tabPlacement, tabLayoutPolicy);
		addMouseListener(this);
	}

	public BaseTabbedPaneCustom(int tabPlacement) {
		super(tabPlacement);
		addMouseListener(this);
	}

	@Override
	public void addTab(String title, Icon icon, Component component, String tip) {
		super.addTab(title, icon, component, tip);
	}

	@Override
	public void addTab(String title, Icon icon, Component component) {
		super.addTab(title, icon, component);
	}

	@Override
	public void addTab(String title, Component component) {
		addTab(title, null, component);
	}

	public void insertTab(String title, Icon icon, Component component, String tip, int index) {
		tip = "tab" + component.hashCode();
		maps.put(tip, component);
		super.insertTab(title, icon, component, null, index);
	}

	public void removeTabAt(int index) {
		Component component = getComponentAt(index);
		maps.remove("tab" + component.hashCode());
		super.removeTabAt(index);
	}

	/*public JToolTip createToolTip() {
		ThumbnailToolTip tooltip = new ThumbnailToolTip();
		tooltip.setComponent(this);
		return tooltip;
	}*/

	@Override
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			showPopupMenu(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// 关闭图标只响应左键
		if (SwingUtilities.isRightMouseButton(e)) {
			showPopupMenu(e);
		}
	}

	private void showPopupMenu(final MouseEvent event) {
		// 如果当前事件与右键菜单有关（单击右键），则弹出菜单
			final int index = ((BaseTabbedPaneCustom) event.getComponent()).getUI().tabForCoordinate(this, event.getX(), event.getY());
			final int count = ((BaseTabbedPaneCustom) event.getComponent()).getTabCount();
			JPopupMenu pop = new JPopupMenu();
			JMenuItem closeCurrent = new JMenuItem("关闭当前");
			closeCurrent.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if(index != 0){
						((BaseTabbedPaneCustom) event.getComponent()).removeTabAt(index);
					}
				}
			});
			pop.add(closeCurrent);
			JMenuItem closeLeft = new JMenuItem("关闭左侧标签");
			closeLeft.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					for (int j = (index - 1); j >= 0; j--) {
						BaseTabbedPaneCustom basetab=((BaseTabbedPaneCustom) event.getComponent());
						Component component = getComponentAt(j);
						if (component instanceof BaseTab) {
							JComponent jcom=(JComponent)((BaseTab)component).getComponent();
							if(!"property".equals(jcom.getToolTipText())){
								basetab.removeTabAt(j);
							}
						}else{
							basetab.removeTabAt(j);
						}
					}
				}
			});
			pop.add(closeLeft);
			JMenuItem closeRight = new JMenuItem("关闭右侧标签");
			closeRight.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					for (int j = (count - 1); j > index; j--) {
						BaseTabbedPaneCustom basetab=((BaseTabbedPaneCustom) event.getComponent());
							basetab.removeTabAt(j);
					}
				}
			});
			pop.add(closeRight);
			JMenuItem closeAll = new JMenuItem("全部关闭");
			closeAll.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					for (int j = (count-1); j >=0; j--) {
						BaseTabbedPaneCustom basetab=((BaseTabbedPaneCustom) event.getComponent());
						Component component = getComponentAt(j);
						if (component instanceof BaseTab) {
							JComponent jcom=(JComponent)((BaseTab)component).getComponent();
							if(!"property".equals(jcom.getToolTipText())){
								basetab.removeTabAt(j);
							}
						}else{
							basetab.removeTabAt(j);
						}
					}
				}
			});
			pop.add(closeAll);
			pop.show(event.getComponent(), event.getX(), event.getY());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * <p>
	 * ShrTabbedPaneCustom.java
	 * </p>
	 * <p>
	 * <Method Simple Comment>
	 * </p>
	 * 缩略图
	 * 
	 * @since 2015年4月30日 上午11:18:17
	 */
	class ThumbnailToolTip extends JToolTip {
		private static final long serialVersionUID = -7317621488447910306L;

		public Dimension getPreferredSize() {
			String tip = getTipText();
			Component component = maps.get(tip);
			if (component != null) {
				return new Dimension((int) (getScaleRatio() * component.getWidth()), (int) (getScaleRatio() * component.getHeight()));
			} else {
				return super.getPreferredSize();
			}
		}

		public void paintComponent(Graphics g) {
			String tip = getTipText();
			Component component = maps.get(tip);
			if (component instanceof JComponent) {
				JComponent jcomponent = (JComponent) component;
				Graphics2D g2d = (Graphics2D) g;
				AffineTransform at = g2d.getTransform();
				g2d.transform(AffineTransform.getScaleInstance(getScaleRatio(), getScaleRatio()));
				ArrayList<JComponent> dbcomponents = new ArrayList<JComponent>();
				updateDoubleBuffered(jcomponent, dbcomponents);
				jcomponent.paint(g);
				resetDoubleBuffered(dbcomponents);
				g2d.setTransform(at);
			}
		}

		private void updateDoubleBuffered(JComponent component, ArrayList<JComponent> dbcomponents) {
			if (component.isDoubleBuffered()) {
				dbcomponents.add(component);
				component.setDoubleBuffered(false);
			}
			for (int i = 0; i < component.getComponentCount(); i++) {
				Component c = component.getComponent(i);
				if (c instanceof JComponent) {
					updateDoubleBuffered((JComponent) c, dbcomponents);
				}
			}
		}

		private void resetDoubleBuffered(ArrayList<JComponent> dbcomponents) {
			for (JComponent component : dbcomponents) {
				component.setDoubleBuffered(true);
			}
		}
	}

	public double getScaleRatio() {
		return scaleRatio;
	}

	public void setScaleRatio(double scaleRatio) {
		this.scaleRatio = scaleRatio;
	}
}