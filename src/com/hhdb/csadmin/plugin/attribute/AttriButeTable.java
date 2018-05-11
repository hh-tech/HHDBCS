package com.hhdb.csadmin.plugin.attribute;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AttriButeTable extends JTable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AttriButeTable(DefaultTableModel tableModel){
		super(tableModel);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}
}
