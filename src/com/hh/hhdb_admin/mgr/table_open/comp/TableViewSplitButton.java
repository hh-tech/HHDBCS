package com.hh.hhdb_admin.mgr.table_open.comp;

import com.alee.extended.button.WebSplitButton;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.managers.style.StyleId;
import com.alee.utils.swing.UnselectableButtonGroup;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabTool;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author ouyangxu
 * @date 2021-12-02 0002 15:20:18
 * @description 切换表格视图按钮(横向 / 纵向)
 */
public class TableViewSplitButton extends AbsHComp {
	protected WebSplitButton splitButton;
	protected WebPopupMenu popupMenu;
	protected JCheckBoxMenuItem autoSizeItem;
	protected JCheckBoxMenuItem showLineItem;
	protected JRadioButtonMenuItem hViewItem;
	protected JRadioButtonMenuItem vViewItem;
	protected JMenuItem nullSiteItem;

	protected HTable table;

	public TableViewSplitButton(HTable table) {
		this(null, table);
	}

	public TableViewSplitButton(String id, HTable table) {
		super(id);
		this.table = table;
		initSplitButton();
	}

	protected void initSplitButton() {
		createPopMenu();
		splitButton = new WebSplitButton(StyleId.splitbuttonIconHover, ModifyTabTool.getIcon("table_view_type"));
		splitButton.setPopupMenu(popupMenu);
		splitButton.setAlwaysShowMenu(true);
		splitButton.setToolTip("显示切换");
		comp = splitButton;
	}

	protected void createPopMenu() {
		boolean tableModel = table.isRowStyle();
		boolean autoResizeCol = table.isAutoResizeCol();
		boolean showLine = table.getComp().getShowHorizontalLines() && table.getComp().getShowVerticalLines();
		popupMenu = new WebPopupMenu();
		autoSizeItem = new JCheckBoxMenuItem("自动列宽", !autoResizeCol);
		showLineItem = new JCheckBoxMenuItem("显示内边框", showLine);
		hViewItem = new JRadioButtonMenuItem("横向显示", tableModel);
		vViewItem = new JRadioButtonMenuItem("纵向显示", !tableModel);
		nullSiteItem = new JMenuItem("空值显示设置");
		nullSiteItem.addActionListener(new NullSiteAction());
		UnselectableButtonGroup.group(hViewItem, vViewItem);
		popupMenu.setRequestFocusEnabled(false);
		popupMenu.add(autoSizeItem);
		popupMenu.add(showLineItem);
		popupMenu.addSeparator();
		popupMenu.add(hViewItem);
		popupMenu.add(vViewItem);
		popupMenu.addSeparator();
		popupMenu.add(nullSiteItem);

	}

	@Override
	public WebSplitButton getComp() {
		return splitButton;
	}

	public WebPopupMenu getPopupMenu() {
		return popupMenu;
	}

	public JCheckBoxMenuItem getAutoSizeItem() {
		return autoSizeItem;
	}

	public JRadioButtonMenuItem getHorViewItem() {
		return hViewItem;
	}

	public JRadioButtonMenuItem getVerViewItem() {
		return vViewItem;
	}

	public JCheckBoxMenuItem getShowLineItem() {
		return showLineItem;
	}

	public JMenuItem getNullSiteItem() {
		return nullSiteItem;
	}

	protected class NullSiteAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object inputObj = JOptionPane.showInputDialog(popupMenu, "请输入空值", "表格空值设置", JOptionPane.QUESTION_MESSAGE, null, null, table.getNullSymbol());
			if (inputObj != null) {
				table.setNullSymbol(inputObj.toString());
				table.getComp().updateUI();
			}
		}

	}
}
