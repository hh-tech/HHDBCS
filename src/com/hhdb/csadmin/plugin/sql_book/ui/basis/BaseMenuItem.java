package com.hhdb.csadmin.plugin.sql_book.ui.basis;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

/**
 * 菜单栏
 * @author hhxd
 *
 */
public class BaseMenuItem extends JMenuItem {
	private static final long serialVersionUID = 1L;

	public BaseMenuItem(String text) {
		super(text);
	}
	public BaseMenuItem(Action a) {
		super(a);
	}

	public BaseMenuItem(Icon icon) {
		super(icon);
	}

	public BaseMenuItem(String text, Icon icon) {
		super(text, icon);
	}
	
}
