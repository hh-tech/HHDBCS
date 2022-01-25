package com.hh.hhdb_admin.mgr.sequence.comp;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import com.hh.frame.create_dbobj.sequenceMr.SeqItem;
import com.hh.frame.create_dbobj.sequenceMr.SqlServerSeqMr;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.mgr.sequence.SequenceMgr;
import com.hh.hhdb_admin.mgr.sequence.common.AbsSeqComp;
import com.hh.hhdb_admin.test.sequence.CompTest;

public class SqlServerSeqComp extends AbsSeqComp {
	
	private Map<SeqItem,String> oldData;
	private SqlServerSeqMr smr;
	private SelectBox type;
	private CheckBoxInput is_cache;

	
	public static void main(String[] args) throws Exception {
		HHSwingUi.init();
		new CompTest();
	}
	public SqlServerSeqComp(String schema, String seq, boolean flag) {
		this.schema=schema;
		this.seq=seq;
		this.isEdit=flag;
		type = new SelectBox();
		is_cache= new CheckBoxInput("iscache") {

			@Override
			protected void onClick(ActionEvent e) {
				if(this.isChecked()) {
					cache.setValue("");
					cache.setEnabled(false);
				}else {
					cache.setValue(oldData.get(SeqItem.cache));
					cache.setEnabled(true);
				}
			}
			
		};
		oldData = new HashMap<SeqItem, String>();
		smr = new SqlServerSeqMr();
		initPanel();
	}

	@Override
	public void initPanel() {
		add(new HeightComp(30));
		add(getLabelInput(SequenceMgr.getLang("type"),type));
		add(getLabelInput(SequenceMgr.getLang("increase"),increase));
		if(!isEdit) {
		add(getLabelInput(SequenceMgr.getLang("startValue"),startValue));
		}else {
		add(getLabelInput(SequenceMgr.getLang("currentVal"),lastValue));
		}
		add(getLabelInput(SequenceMgr.getLang("minVal"),min));
		add(getLabelInput(SequenceMgr.getLang("maxVal"),max));
		add(getLabelInput(SequenceMgr.getLang("nocache"),is_cache));
		add(getLabelInput(SequenceMgr.getLang("cache"),cache));
		add(getLabelInput(SequenceMgr.getLang("loop"),checkCycle));
		getComp().repaint();
		setStartVal(isEdit);
	}

	@Override
	public void setStartVal(boolean isEdit) {
		String[] dataType= {"bigint","tinyint","smallint","int","decimal","numeric"};
		for(String str:dataType) {
			type.addOption(str, str);
		}
		if(isEdit) {
			setEditVal();
		}else {
			increase.setValue("1");
			startValue.setValue("1");
		}
	}

	@Override
	public Map<SeqItem, String> getCompData() {
		Map<SeqItem,String> data = new HashMap<SeqItem,String>();
		   data.put(SeqItem.type,type.getValue());
	       data.put(SeqItem.increment, increase.getValue());
	       data.put(SeqItem.start_value,startValue.getValue());
	       if(isEdit)
	       data.put(SeqItem.last_value, lastValue.getValue());
	       data.put(SeqItem.min_value, min.getValue());
	       data.put(SeqItem.max_value, max.getValue());
	       data.put(SeqItem.cache, cache.getValue());
	       data.put(SeqItem.is_cycle,checkCycle.getValue());
	       data.put(SeqItem.is_cache,is_cache.getValue());
	       return data;
	}

	@Override
	public void setEditVal() {
		oldData=smr.getSeqInfo(schema, seq, SequenceMgr.conn);
//		System.out.println("查询到的序列信息");
//		   for(SeqItem key:oldData.keySet()) {
//		    	  System.out.println(key+"    "+oldData.get(key));
//		      }	 
		
		increase.setValue(oldData.get(SeqItem.increment));
		type.setValue(oldData.get(SeqItem.type));
		type.setEnabled(false);
		startValue.setValue(oldData.get(SeqItem.start_value));
		lastValue.setValue(oldData.get(SeqItem.last_value));
		min.setValue(oldData.get(SeqItem.min_value));
		max.setValue(oldData.get(SeqItem.max_value));
		if("1".equals(oldData.get(SeqItem.is_cycle)))
			checkCycle.setValue("true");
		
		if("1".equals(oldData.get(SeqItem.is_cache))) {
			is_cache.setValue("false");
			cache.setValue(oldData.get(SeqItem.cache));
		}else {
			is_cache.setValue("true");
			cache.setEnabled(false);
		}
	}

	@Override
	public String getCreateSql() {
		return smr.getAddSeqSql(getCompData(), schema, seq);
	}

	@Override
	public String getRenameSql() {
		return smr.getRenameSeqSql(schema, seq);
	}

	@Override
	public String getDeleteSql() {
		return smr.getDeleteSeqSql(schema, seq);
	}

	@Override
	public Map<String, String> getDesignSql() {
		return smr.getDesignSeqSql(getCompData(), oldData, schema, seq);
	}

}
