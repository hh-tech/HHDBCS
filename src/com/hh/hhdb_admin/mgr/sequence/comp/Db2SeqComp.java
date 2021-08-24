package com.hh.hhdb_admin.mgr.sequence.comp;

import java.util.HashMap;
import java.util.Map;

import com.hh.frame.create_dbobj.sequenceMr.Db2SeqMr;
import com.hh.frame.create_dbobj.sequenceMr.SeqItem;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.WithLabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.mgr.sequence.SequenceMgr;
import com.hh.hhdb_admin.mgr.sequence.common.AbsSeqComp;
import com.hh.hhdb_admin.test.sequence.CompTest;

public class Db2SeqComp extends AbsSeqComp {
	
	
	
	private Map<SeqItem,String> oldData;
	private Db2SeqMr smr;
	private SelectBox type;
	private CheckBoxInput order;
	
	public static void main(String[] args) throws Exception {
		HHSwingUi.init();
		new CompTest();
	}
	public Db2SeqComp(String schema, String seq, boolean flag) {
		this.schema=schema;
		this.seq=seq;
		this.isEdit=flag;
		type = new SelectBox();
		order = new CheckBoxInput(SequenceMgr.getLang("order"));
		oldData = new HashMap<SeqItem, String>();
		smr = new Db2SeqMr();
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
		add(getLabelInput(SequenceMgr.getLang("cache"),cache));
		add(getLabelInput(SequenceMgr.getLang("loop"),checkCycle));
		add(getLabelInput(SequenceMgr.getLang("order"),order)); 
		getComp().repaint();
		setStartVal(isEdit);
	}

	@Override
	public void setStartVal(boolean isEdit) {
		String[] dataType= {"integer","smallint","bigint"};
		for(String str:dataType) {
			type.addOption(str, str);
		}
		if(isEdit) {
			setEditVal();
		}else {
			increase.setValue("1");
			startValue.setValue("1");
			cache.setValue("20");
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
	       data.put(SeqItem.order,order.getValue());
	       return data;
	}

	@Override
	public void setEditVal() {
		oldData=smr.getSeqInfo(schema, seq, SequenceMgr.conn);
		System.out.println("查询到的序列信息");
		   for(SeqItem key:oldData.keySet()) {
		    	  System.out.println(key+"    "+oldData.get(key));
		      }	 
		
		increase.setValue(oldData.get(SeqItem.increment));
		type.setValue(oldData.get(SeqItem.type));
		type.setEnabled(false);
//		startValue.setValue(oldData.get(SeqItem.start_value));
		lastValue.setValue(oldData.get(SeqItem.last_value));
		min.setValue(oldData.get(SeqItem.min_value));
		max.setValue(oldData.get(SeqItem.max_value));
		cache.setValue(oldData.get(SeqItem.cache));
		
		if("Y".equals(oldData.get(SeqItem.is_cycle)))
			checkCycle.setValue("true");
		if("Y".equals(oldData.get(SeqItem.order)))
			order.setValue("true");
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
	public WithLabelInput getLabelInput1(String label,AbsInput input) {
		HDivLayout hdiv = new HDivLayout(30,0,GridSplitEnum.C3);
		hdiv.setMaxWidth(400);
		HPanel hPane2 = new HPanel(hdiv);
		WithLabelInput winput =new WithLabelInput(hPane2,label,input);
		return winput;
	}


}
