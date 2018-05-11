package com.hhdb.csadmin.plugin.table_operate.component.basetab;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class TabbedPaneUI extends BasicTabbedPaneUI {

	private static final Insets NO_INSETS = new Insets(0, 0, 0, 0);
	private ColorSet selectedColorSet;
	private ColorSet defaultColorSet;
	private ColorSet hoverColorSet;
	private boolean contentTopBorderDrawn = true;
	// private Insets contentInsets = new Insets(1, 1, 1, 1);
	private int lastRollOverTab = -1;

	public static ComponentUI createUI(JComponent c) {
		return new TabbedPaneUI();
	}

	protected LayoutManager createLayoutManager() {
		if (tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT) {
			return super.createLayoutManager();
		} else {
			return new TabbedPaneLayout();
		}
	}

	public TabbedPaneUI() {

		defaultColorSet = new ColorSet();
		defaultColorSet.topGradColor = new Color(240, 240, 240);

		selectedColorSet = new ColorSet();
		selectedColorSet.topGradColor = new Color(253, 253, 253);

		hoverColorSet = new ColorSet();
		hoverColorSet.topGradColor = new Color(253, 253, 253);

		maxTabHeight = 27;

		// setContentInsets(1);
	}

	public void setContentTopBorderDrawn(boolean b) {
		contentTopBorderDrawn = b;
	}

	/*
	 * public void setContentInsets(Insets i) { contentInsets = i; }
	 * 
	 * public void setContentInsets(int i) { contentInsets = new Insets(i, 0, i,
	 * i); }
	 */

	//
	public int getTabRunCount(JTabbedPane pane) {
		return 1;
	}

	protected void installDefaults() {
		super.installDefaults();

		RollOverListener l = new RollOverListener();
		tabPane.addMouseListener(l);
		tabPane.addMouseMotionListener(l);

		tabAreaInsets = NO_INSETS;
		tabInsets = new Insets(0, 16, 0, 16);
	}

	protected boolean scrollableTabLayoutEnabled() {
		return false;
	}

	/*
	 * protected Insets getContentBorderInsets(int tabPlacement) { return
	 * contentInsets; }
	 */

	protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
		return maxTabHeight;
	}

	protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
		int w = super.calculateTabWidth(tabPlacement, tabIndex, metrics) - 20;
		return w;
	}

	protected int calculateMaxTabHeight(int tabPlacement) {
		return maxTabHeight;
	}

	protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
		Graphics2D g2d = (Graphics2D) g;
		super.paintTabArea(g, tabPlacement, selectedIndex);

		if (contentTopBorderDrawn) {
			g2d.setColor(Color.WHITE);// lineColor);
			g2d.drawLine(0, maxTabHeight, tabPane.getWidth() - 1, maxTabHeight);
		}
	}

	/**
	 * 选项卡背景颜色设置
	 */
	protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h,
			boolean isSelected) {
		Graphics2D g2d = (Graphics2D) g;
		ColorSet colorSet;
		Rectangle rect = rects[tabIndex];

		if (isSelected) {
			colorSet = selectedColorSet;
		} else if (getRolloverTab() == tabIndex) {
			colorSet = hoverColorSet;
		} else {
			colorSet = defaultColorSet;
		}

		g2d.setColor(colorSet.topGradColor);
		int width = rect.width;
		int xpos = rect.x;
		int yPos = rect.y;
		if (tabIndex > -1) {
			width--;
		}
		g2d.fill(this.getUpArea(xpos, yPos, width + 1, h + 2));
	}

	private Shape getUpArea(int x, int y, int w, int h) {
		Rectangle2D rec = new Rectangle2D.Float(x, y, w, h);
		Area a = new Area(rec);
		return a;
	}

	/*
	 * protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
	 * int x, int y, int w, int h, boolean isSelected) { Rectangle rect =
	 * getTabBounds(tabIndex, new Rectangle(x, y, w, h)); Graphics2D g2 =
	 * (Graphics2D) g; Composite old = g2.getComposite(); AlphaComposite comp =
	 * AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
	 * g2.setComposite(comp); g2.setColor(Color.RED); g2.drawLine(rect.x +
	 * rect.width, 0, rect.x + rect.width,0); g2.setComposite(comp); }
	 */

	private class ColorSet {
		Color topGradColor;
	}

	private class RollOverListener implements MouseMotionListener, MouseListener {

		public void mouseDragged(MouseEvent e) {
		}

		public void mouseMoved(MouseEvent e) {
			checkRollOver();
		}

		public void mouseClicked(MouseEvent e) {

		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
			checkRollOver();
		}

		public void mouseExited(MouseEvent e) {
			// 标记：java.lang.NullPointerException
			if (tabPane != null) {
				tabPane.repaint();
			}
		}

		/**
		 * 鼠标以上去重绘选项卡的背景颜色
		 */
		private void checkRollOver() {
			int currentRollOver = getRolloverTab();
			if (currentRollOver != lastRollOverTab) {
				lastRollOverTab = currentRollOver;
				Rectangle tabsRect = new Rectangle(0, 0, tabPane.getWidth(), maxTabHeight);
				tabPane.repaint(tabsRect);
			}
		}
	}

	public class TabbedPaneLayout extends BasicTabbedPaneUI.TabbedPaneLayout {
		public TabbedPaneLayout() {
			TabbedPaneUI.this.super();
		}

		protected void calculateTabRects(int tabPlacement, int tabCount) {
			super.calculateTabRects(tabPlacement, tabCount);
			for (int i = 0; i < rects.length; i++) {
				rects[i].x = rects[i].x + (5 * i);
			}
		}
	}
}
