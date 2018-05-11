package com.hhdb.csadmin.plugin.monitor.ui;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * 表格模型
 */
public class MonitorTableDataModel extends DefaultTableModel {
	private static final long serialVersionUID = 1L;
	/**
	 * 表格只读 true:编辑 false：只读
	 */
	private boolean editable;
	/**
	 * 列只读
	 */
	private int[] cloumns;

	public MonitorTableDataModel(boolean editable, int[] cloumns) {
		this.cloumns = cloumns;
		this.editable = editable;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (cloumns != null) {
			for (int cl : cloumns) {
				if (cl == column) {
					return false;
				}
			}
		}
		return editable;
	}

	public void setDataVectors(Vector<Object> data, Vector<Object> columnNames) {
		setDataVector(data, columnNames);
	}
}
