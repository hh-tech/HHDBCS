package com.hh.hhdb_admin.mgr.function2.debug.panel;

import java.util.ArrayList;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.mgr.function2.debug.base.ColNameCons;

public class PkgResultPanel extends LastPanel{
	
	private HTable varTable;
	public PkgResultPanel() {
		varTable = new HTable();
    	varTable.setRowHeight(25);
    	varTable.hideSeqCol();
    	
    	varTable.addCols(new DataCol(ColNameCons.NAME, LangMgr.lang(Word2.NAME)));
    	varTable.addCols(new DataCol(ColNameCons.VALUE, LangMgr.lang(Word1.VALUE)));
    	varTable.setCellEditable(false);
        setWithScroll(varTable.getComp());
        varTable.load(new ArrayList<>(), 1);
	}
	
	
	public HTable getVarTable() {
		return varTable;
	}
	
	
	
	
	

}
