package com.hhdb.csadmin.plugin.menu.ui;

import javax.swing.JTextField;

/**
 * 重写JTextField，用于表格单元格
 * 
 * 
 */
public class TextCellEditor extends JTextField {
	private static final long serialVersionUID = 3515456120233483711L;

	public TextCellEditor() {
		super();
		setBorder(null);
		setHorizontalAlignment(JTextField.LEFT);
	}

	public String getEditorValue() {
		return getText();
	}

	public void resetValue() {
		setText("");
	}

	public String getValue() {
		return getText();
	}

	public void setValue(String value) {
		setText(value);
	}
}