package com.hhdb.csadmin.plugin.user_permission.UI;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

public class CheckBoxCellEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long serialVersionUID = 1L;
	protected JCheckBox checkBox;

	public CheckBoxCellEditor() {
		checkBox = new JCheckBox();
		checkBox.setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Object getCellEditorValue() {
		return Boolean.valueOf(checkBox.isSelected());
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		checkBox.setSelected(((Boolean) value).booleanValue());
		return checkBox;
	}
}