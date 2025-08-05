package com.hh.hhdb_admin.comp.colsel;

import java.awt.event.ActionEvent;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;

class PkgSelTableCheckBox extends CheckBoxInput{
	private HTable table;
	private BoolCol col;
	public PkgSelTableCheckBox(HTable table,BoolCol col) {
		super("");
		this.table = table;
		this.col = col;
		setText(LangMgr.lang(Word2.CHECK_ALL));
		
	}
	
	
	
	
	
	@Override
	protected void onClick(ActionEvent e) {
		int index = this.col.getColIndex();
		if(this.col.isShow()){
			int rowCount = this.table.getComp().getRowCount();
			for(int i=0;i<table.getComp().getColumnCount();i++) {
				String name = table.getComp().getColumnName(i);
				if(name.equals(col.getName())) {
					index = i;
				}
			}
			
			for(int i=0;i<rowCount;i++) {
				this.table.setValueAt(getValue(), i, index);
			}
			
		}
	}
	
	
	
	
	


	
}
