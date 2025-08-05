package com.hh.hhdb_admin.mgr.function2.debug.panel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.function.ui.run.RunBaseForm;
import com.hh.hhdb_admin.mgr.function2.debug.base.ColNameCons;

class PkgParamPanel extends LastPanel{

	
	private HTable table;
	public PkgParamPanel() {
		table = new HTable();
		DataCol dc = new DataCol(ColNameCons.NAME, LangMgr.lang(Word2.PARAM));
		dc.setCellEditable(false);
		DataCol dcType = new DataCol(ColNameCons.TYPE, LangMgr.lang(Word2.TYPE));
		dcType.setCellEditable(false);
		table.addCols(dc, dcType, new DataCol(ColNameCons.VALUE, LangMgr.lang(Word1.VALUE)));
		// 根据用户输入参数值更新sql
		table.getComp().addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				try {
					if ("tableCellEditor".equalsIgnoreCase(evt.getPropertyName().trim())) {
						inform();
					}
				} catch (Exception e) {
					Lg.error(RunBaseForm.class, e);
					PopDlgUtil.error(CsVarUtil.parentFrame, e);
				}
			}
		});
		table.load(null, 1);
		table.getComp().putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		
		setWithScroll(table.getComp());
	}
	
	public void setValueEditable(boolean cellEditable) {
		table.getColumn(ColNameCons.VALUE).setCellEditable(cellEditable);
	}
	
	public void inform(){
		
	}

	public HTable getTable() {
		return table;
	}
	
	
	
}
