package com.hh.hhdb_admin.mgr.sequence.comp;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.sequenceMr.DmSeqMr;
import com.hh.frame.create_dbobj.sequenceMr.SeqItem;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.hhdb_admin.mgr.sequence.SequenceMgr;
import com.hh.hhdb_admin.mgr.sequence.common.AbsSeqComp;
/**
 * dm数据库序列面板
 * @author lz
 * @date 2021年3月4日
 */
public class DmSeqComp extends AbsSeqComp {

	private CheckBoxInput order;
	/**
	 * 保存查询出来的序列
	 */
	private Map<SeqItem,String> oldData;
	private DmSeqMr smr;

	public DmSeqComp(String schema,String seqName, boolean isEdit) {
		this.schema=schema;
		this.seq=seqName;
		this.isEdit=isEdit;
		oldData = new HashMap<SeqItem, String>();
		smr = new DmSeqMr();
		initPanel();
	}
	@Override
	public void initPanel() {
		order = new CheckBoxInput(SequenceMgr.getLang("order"));
		add(new HeightComp(30));
		add(getLabelInput(SequenceMgr.getLang("increase"),increase));
		if(!isEdit) {
			add(getLabelInput(SequenceMgr.getLang("startValue"),startValue));
		}else {
			add(getLabelInput(SequenceMgr.getLang("currentVal"),lastValue));
			lastValue.setEnabled(false);
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
		if(isEdit) {
			setEditVal();
		}else {
			increase.setValue("1");
			startValue.setValue("1");
			min.setValue("1");
			max.setValue("9999999999999999");
		}
	}

	@Override
	public Map<SeqItem, String> getCompData() {
		Map<SeqItem,String> data = new HashMap<SeqItem,String>();
		data.put(SeqItem.increment, increase.getValue());
		if(!isEdit) {
			data.put(SeqItem.start_value,startValue.getValue());
		}else {
			data.put(SeqItem.last_value, lastValue.getValue());
		}
		data.put(SeqItem.min_value, min.getValue());
		data.put(SeqItem.max_value, max.getValue());
		data.put(SeqItem.cache, cache.getValue());
		data.put(SeqItem.is_cycle,checkCycle.getValue());
		data.put(SeqItem.order, order.getValue());
		return data;
	}

	public boolean saveCreateSql() {
		boolean flag=true;
		try {
			 SqlExeUtil.executeUpdate(SequenceMgr.conn,getCreateSql());
			 JOptionPane.showMessageDialog(null,SequenceMgr.getLang("addSuccess"));
		} catch (Exception e) {
			flag=false;
			JOptionPane.showMessageDialog(null, e.getMessage()+SequenceMgr.getLang("addFail"), "",JOptionPane.ERROR_MESSAGE);
			
		}
		return flag;
	}
	
	public boolean saveUpdataSql() {
		boolean flag=true;
		String designsql=getDesignSql().get(SeqItem.updateSQL.name());
		try {
			if(!"".equals(designsql)){
				SqlExeUtil.executeUpdate(SequenceMgr.conn, designsql);
			}
			JOptionPane.showMessageDialog(null,SequenceMgr.getLang("modifySuccess"));
		} catch (Exception e) {
			flag=false;
			JOptionPane.showMessageDialog(null, e.getMessage()+SequenceMgr.getLang("addFail"), "error",JOptionPane.ERROR_MESSAGE);
		}
		return flag;
	}
	
	@Override
	public void setEditVal() {
		oldData = smr.getSeqInfo(schema, seq, SequenceMgr.conn);
		increase.setValue(oldData.get(SeqItem.increment));
//		startValue.setValue(oldData.get(SeqItem.start_value));
		lastValue.setValue(oldData.get(SeqItem.last_value));
		min.setValue(oldData.get(SeqItem.min_value));
		max.setValue(oldData.get(SeqItem.max_value));
		cache.setValue(oldData.get(SeqItem.cache));
		checkCycle.setValue("false");
		order.setValue("false");
		
		if(oldData.get(SeqItem.is_cycle).equals("Y")) {
			checkCycle.setValue("true");
		}else {
			checkCycle.setValue("false");
		}
		
		if(oldData.get(SeqItem.order).equals("Y")) {
			order.setValue("true");
		}else {
			order.setValue("false");
		}
	}
	
	@Override
	public String getRenameSql() {
		return smr.getRenameSeqSql(schema,seq);
	}
	@Override
	public String getDeleteSql() {
		return smr.getDeleteSeqSql(schema, seq);
	}
	public String getCreateSql() {
		return smr.getAddSeqSql(getCompData(),schema,seq);
	}
	
	@Override
	public Map<String, String> getDesignSql() {
		return  smr.getDesignSeqSql(getCompData(),oldData,schema,seq);
	}

}
