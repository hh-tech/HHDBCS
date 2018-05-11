package com.hhdb.csadmin.plugin.user_create.ui;

import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JLabel;


public class BaseLabel extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BaseLabel() {
		super();
	}

	public BaseLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	public BaseLabel(Icon image) {
		super(image);
	}

	public BaseLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}

	public BaseLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	public BaseLabel(String text) {
		super(text);
	}

	public Insets getMargin() {
		return HHUIConstants.DEFAULT_FIELD_MARGIN;
	}

	public int getHeight() {
		return Math.max(super.getHeight(), HHUIConstants.DEFAULT_FIELD_HEIGHT);
	}
}
