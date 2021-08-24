package com.hh.hhdb_admin.mgr.query.ui;
import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.sqlwin.rs.MultiRsBean;
import com.hh.frame.swingui.view.container.HTabPane;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.mgr.query.QueryMgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据tab页
 * @author hexu
 *
 */
public class MultiRsDataTab extends LastPanel {
	//消耗时间
	private LabelInput msglabel = new LabelInput();
	HTabPane hTabPane = new HTabPane();


	public MultiRsDataTab(MultiRsBean mb,long runMills)throws Exception{
		super(false);
		set(hTabPane.getComp());
		int idnum = 1;
		for(String key:mb.getRsMap().keySet()) {
			List<List<String>> list = mb.getRsMap().get(key);
			HTable table = new HTable();
			List<String> head = list.get(0);
			for(String colName:head) {
				table.addCols(new DataCol(colName, colName));
			}
			SearchToolBar sToolbar = new SearchToolBar(table);
	        table.setRowHeight(25);
	        table.setRowStyle(true);
//			addBtn(sToolbar);
	        LastPanel last = new LastPanel(false);// 无滚动条
			last.setHead(sToolbar.getComp());
			last.setWithScroll(table.getComp());
	        hTabPane.addPanel(idnum+"", key, last.getComp(),false);
	        table.load(gendata(list), 1);
	        idnum++;
		}
		msglabel.setValue(QueryMgr.getLang("timeSpent") + "：" + runMills + "ms");
		msglabel.setAlign(AlignEnum.LEFT);
		setFoot(msglabel.getComp());
	}
	private List<Map<String, String>> gendata(List<List<String>> list){
		List<Map<String, String>> datas= new ArrayList<Map<String,String>>();
		List<String> head = list.get(0);
		for(int i=1;i<list.size();i++) {
			List<String> datalist = list.get(i);
			Map<String,String> data = new HashMap<String, String>();
			for(int j=0;j<head.size();j++) {
				data.put(head.get(j), datalist.get(j));
			}
			datas.add(data);
		}
		return datas;
	}
}
