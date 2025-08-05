package com.hh.hhdb_admin.mgr.function2.debug.panel;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.mgr.function2.debug.base.ColNameCons;

class PkgStackPanel extends LastPanel{

	
	private HTable table;
	public PkgStackPanel() {
		table = new HTable();
		table.hideSeqCol();
		DataCol r =  new DataCol(ColNameCons.ROW, LangMgr.lang(Word1.ROW));
		r.setCellEditable(false);
		table.addCols(r);
		DataCol dc = new DataCol(ColNameCons.NAME, LangMgr.lang(Word2.NAME));
		dc.setCellEditable(false);
		DataCol dcType = new DataCol(ColNameCons.TYPE, LangMgr.lang(Word2.TYPE));
		dcType.setCellEditable(false);
		table.addCols(dc, dcType, new DataCol(ColNameCons.VALUE, LangMgr.lang(Word1.VALUE)));
		
		
		table.load(null, 1);
        setWithScroll(table.getComp());
	}
	
	
	public HTable getTable() {
		return table;
	}
	
	

}
