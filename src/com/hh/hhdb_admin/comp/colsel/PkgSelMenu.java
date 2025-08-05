package com.hh.hhdb_admin.comp.colsel;

import java.awt.event.MouseEvent;
import java.util.List;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.menu.body.AbsTabBodyPopMenu;

class PkgSelMenu extends AbsTabBodyPopMenu{
	ColSelPanel stc;
	
	private HMenuItem nullItem;
	private HMenuItem selectItem;
	PkgSelMenu(ColSelPanel comp){
		this.stc = comp;
		
	}
	
	@Override
	public void init(HTable tab) {
		this.tab = tab;
		
		this.selectItem = new HMenuItem(LangMgr.lang(Word2.SELECT)) {
			@Override
			protected void onAction() {
				int[] sels = tab.getComp().getSelectedRows();
				if(sels.length == 0) {
					return;
				}
				
				for(int i:sels) {
					stc.addSelection(i, i);
				}
			}
		};
		
		
		
		
		
		
		 nullItem = new HMenuItem( LangMgr.lang(Word2.SET_NULL)) {
			@Override
			protected void onAction() {
				int[] sels = tab.getComp().getSelectedRows();
				if(sels.length == 0) {
					return;
				}
				
				for(int i:sels) {
					stc.cancelSelection(i, i);
				}
			}
		};
		
		
		
		addItem(selectItem);
		addItem(nullItem);
	}
	
	
	
	@Override
	public void showPopup(MouseEvent e) {
		List<HTabRowBean> beans = this.tab.getSelectedRowBeans();
		List<HTabRowBean> selBeans = this.stc.getSelectionBeans();
		boolean hasSelected = false;
		boolean allSelected = true;
		for(HTabRowBean b :beans) {
			if(selBeans.contains(b)){
				if(!hasSelected) {
					hasSelected = true;
				}
			}else {
				if(allSelected) {
					allSelected = false;
				}
				
			}
		}
		
		if(!hasSelected) {
			nullItem.setEnabled(false);
		}else {
			nullItem.setEnabled(true);
		}
		
		if(allSelected) {
			selectItem.setEnabled(false);
		}else {
			selectItem.setEnabled(true);
		}
		
		
		super.showPopup(e);
	}

	
	
	
}
