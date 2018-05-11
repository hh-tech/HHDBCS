package com.hhdb.csadmin.plugin.flow_editor.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * JTabbedPane,页面
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
			//showPopupMenu(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// 关闭图标只响应左键
		if (SwingUtilities.isRightMouseButton(e)) {
			//showPopupMenu(e);
		}
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