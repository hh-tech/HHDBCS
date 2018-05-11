package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;


public class BaseMenuItem extends JMenuItem {
	
	/**
	 * 
	 */
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
