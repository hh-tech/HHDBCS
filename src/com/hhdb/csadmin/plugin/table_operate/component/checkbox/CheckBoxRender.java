package com.hhdb.csadmin.plugin.table_operate.component.checkbox;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * 重写JCheckBox，用于表格单元格
 * 
 */
public class CheckBoxRender extends JCheckBox implements TableCellRenderer {
	private static final long serialVersionUID = 1L;
	Border border = new EmptyBorder(1, 2, 1, 2);

	public CheckBoxRender() {
		super();
		setOpaque(true);
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof Boolean) {
			setSelected(((Boolean) value).booleanValue());
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}
		return this;
	}
}
