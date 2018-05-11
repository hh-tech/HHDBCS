package com.hhdb.csadmin.plugin.type_create.component;

import java.util.List;

import javax.swing.JComboBox;

/**
 * 重写JComboBox，用于表格单元格
 * 
 * 
 */
public class ComboBoxCellEditor extends JComboBox<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ComboBoxCellEditor() {
		super();
		setBorder(null);
	}

	public String getValue() {
        if(getSelectedItem()==null){
        	return "";
        }
		return getSelectedItem().toString();
	}

	public String getEditorValue() {
		return getStringValue();
	}

	public String getStringValue() {
		return getValue();
	}

	public void setValue(List<String> list) {
		for (String type : list) {
			addItem(type);
		}
	}
}
