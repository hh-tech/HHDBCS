package com.hh.hhdb_admin.mgr.sequence.comp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.LM;
import com.hh.frame.create_dbobj.sequenceMr.HHdbSeqMr;
import com.hh.frame.create_dbobj.sequenceMr.SeqItem;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.hhdb_admin.mgr.sequence.SequenceMgr;
import com.hh.hhdb_admin.mgr.sequence.common.AbsSeqComp;
/**
 * HHDB、PG数据库序列面板
 * @author HuBingBing
 * @date 2020年12月22日上午9:20:20
 */
public class HHdbSeqComp extends AbsSeqComp {
	
	private CheckBoxInput checkOwner;
	private SelectBox tableOwner;
	private SelectBox columnOwner;
	private TextAreaInput aonotation;
	private Map<SeqItem,String> oldData;
	private HHdbSeqMr smr;

	public HHdbSeqComp(DBTypeEnum type ,String schema, String seq, boolean isEdit){
		this.schema=schema;
		this.seq=seq;
		this.isEdit=isEdit;
		oldData = new HashMap<SeqItem, String>();
		try {
			smr = new HHdbSeqMr(type);
		} catch (Exception e) {
			LM.error(getClass(), e);
		}
		initPanel();
		
	}

	@Override
	public void initPanel() {
		checkCycle = new CheckBoxInput(SequenceMgr.getLang("loop"));
		checkOwner = new CheckBoxInput(SequenceMgr.getLang("listOwner"));
		tableOwner = new SelectBox();
		columnOwner = new SelectBox();	
		aonotation = new TextAreaInput(null,null,2);
		add(new HeightComp(20));
		add(getLabelInput(SequenceMgr.getLang("increase"),increase));
		add(getLabelInput(SequenceMgr.getLang("startValue"),startValue));
		if(isEdit)
		add(getLabelInput(SequenceMgr.getLang("currentVal"),lastValue));
		add(getLabelInput(SequenceMgr.getLang("minVal"),min));
		add(getLabelInput(SequenceMgr.getLang("maxVal"),max));
		add(getLabelInput(SequenceMgr.getLang("cache"),cache));
		add(getLabelInput(SequenceMgr.getLang("loop"),checkCycle));
		add(getLabelInput(SequenceMgr.getLang("listOwner"),checkOwner));
		add(getLabelInput(SequenceMgr.getLang("tabOwner"),tableOwner));
		add(getLabelInput(SequenceMgr.getLang("colOwner"),columnOwner));
		add(getLabelInput(SequenceMgr.getLang("aonotation"),aonotation));
		
		setStartVal(isEdit);
		
	}

	@Override
	public void setStartVal(boolean isEdit) {
		setTableOwner();
		if(isEdit) {
			setEditVal();
		}else {
			increase.setValue("1");
			startValue.setValue("1");
			lastValue.setValue("1");
			min.setValue("1");
			max.setValue("9223372036854775807");
			cache.setValue("1");
			tableOwner.setEnabled(false);
			columnOwner.setEnabled(false);	
			
		}
	}

	@Override
	public Map<SeqItem, String> getCompData() {
		
		Map<SeqItem,String> data = new HashMap<SeqItem,String>();
		
	       data.put(SeqItem.increment, increase.getValue());
	       data.put(SeqItem.start_value,startValue.getValue());
	       if(isEdit)
	       data.put(SeqItem.last_value, lastValue.getValue());
	       data.put(SeqItem.min_value, min.getValue());
	       data.put(SeqItem.max_value, max.getValue());
	       data.put(SeqItem.cache, cache.getValue());
	       data.put(SeqItem.is_cycle,checkCycle.getValue());
	       data.put(SeqItem.is_owner, checkOwner.getValue());
	       data.put(SeqItem.table_name, tableOwner.getValue());
	       data.put(SeqItem.column_name, columnOwner.getValue());
	       data.put(SeqItem.comment, aonotation.getValue());
	       return data;
	}


	@Override
	public void setEditVal() {
		oldData = smr.getSeqInfo(schema, seq, SequenceMgr.conn);
		//设计序列时设置值
		increase.setValue(oldData.get(SeqItem.increment));
		startValue.setValue(oldData.get(SeqItem.start_value));
		lastValue.setValue(oldData.get(SeqItem.last_value));
		min.setValue(oldData.get(SeqItem.min_value));
		max.setValue(oldData.get(SeqItem.max_value));
		cache.setValue(oldData.get(SeqItem.cache));
		checkCycle.setValue("false");
		aonotation.setValue(oldData.get(SeqItem.comment));
		if(oldData.get(SeqItem.is_cycle).equals("t")||oldData.get(SeqItem.is_cycle).equals("true")) {
			checkCycle.setValue("true");
		}
		String tableName=oldData.get(SeqItem.table_name);
		String columnName=oldData.get(SeqItem.column_name);
		if(!StringUtils.isBlank(tableName)) {
	        	 
		   checkOwner.setValue("true");
		
	       if(checkOwner.getValue().equals("true")) {
		 	      tableOwnerSet();
		 	      columnOwnerSet();
		 	      tableOwner.setValue(tableName);
		 	      columnOwner.setValue(columnName);
	       }
	 	   }    
	     //查看序列信息
	      for(SeqItem key:oldData.keySet()) {
	    	  System.out.println(key+"    "+oldData.get(key));
	      }	           
	}
	
	private void tableOwnerSet() {
		tableOwner.removeAllItems();
//		tableOwner.addOption("","");
		Set<String> tables = smr.getTables(schema, SequenceMgr.conn);
		for (String tableName : tables) {
			tableOwner.addOption(tableName, tableName);
		}
	}

	/**
	 * 设置绑定列的值
	 * @author HuBingBing
	 * @date 2020年12月21日下午5:52:50
	 * void
	 *
	 */
	private void columnOwnerSet() {
		
		
		tableOwner.addListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setColumn();
			}
		});
	
		
	}
	
	private void setColumn() {
		columnOwner.removeAllItems();
		String tableName=tableOwner.getValue();
		if(tableName!=null){
			List<String> columnList = smr.getColumnList(schema, tableName, SequenceMgr.conn);
			for(String columnName:columnList){
				columnOwner.addOption(columnName, columnName);
			}
		}
	}
	/**
	 *  监听序列绑定状态
	 * @author HuBingBing
	 * @date 2020年12月21日下午6:09:05
	 * void
	 *
	 */
	private void setTableOwner() {
		checkOwner.addListen(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(checkOwner.isChecked()) {
					tableOwner.setEnabled(true);
					columnOwner.setEnabled(true);
					tableOwnerSet();
					if(StringUtils.isBlank(columnOwner.getValue())){
					  setColumn();
					}
					columnOwnerSet();
				}else {
					tableOwner.setEnabled(false);
					columnOwner.setEnabled(false);	
				}
				
			}

		});
	}

	@Override
	public String getCreateSql() {
		
		return smr.getAddSeqSql(getCompData(), schema, seq);
	}

	@Override
	public Map<String, String> getDesignSql() {
		
		return  smr.getDesignSeqSql(getCompData(),oldData,schema,seq);
		
	}

	@Override
	public String getRenameSql() {
	
		return smr.getRenameSeqSql(schema,seq);
	}

	@Override
	public String getDeleteSql() {
		return smr.getDeleteSeqSql(schema, seq);
	}

}
