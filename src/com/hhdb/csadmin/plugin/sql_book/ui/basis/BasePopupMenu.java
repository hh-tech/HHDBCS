package com.hhdb.csadmin.plugin.sql_book.ui.basis;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JPopupMenu;

/**
 * 弹出栏
 * @author hhxd
 *
 */
public abstract class BasePopupMenu extends JPopupMenu implements ActionListener {
	private static final long serialVersionUID = 1L;

	public BasePopupMenu() {
		super();
	}

	public BasePopupMenu(String label) {
		super(label);
	}

	public void showPopup(Component invoker, int x, int y) {
		show(invoker, x, y);
	}

	protected BaseMenuItem createMenuItem(String text, String actionCommand, ActionListener listener) {
		BaseMenuItem menuItem = new BaseMenuItem(text);
		menuItem.setActionCommand(actionCommand);
		menuItem.addActionListener(listener);
		return menuItem;
	}
	protected BaseMenuItem createMenuItem(String text, String actionCommand, ActionListener listener,Icon icon) {
		BaseMenuItem menuItem = new BaseMenuItem(text);
		menuItem.setActionCommand(actionCommand);
		menuItem.addActionListener(listener);
		menuItem.setIcon(icon);
		return menuItem;
	}
	protected BaseMenuItem createMenuItem(String text, String actionCommand, ActionListener listener,Icon icon,boolean flag) {
		BaseMenuItem menuItem = new BaseMenuItem(text);
		menuItem.setActionCommand(actionCommand);
		menuItem.addActionListener(listener);
		menuItem.setIcon(icon);
		menuItem.setEnabled(flag);
		return menuItem;
	}
}
