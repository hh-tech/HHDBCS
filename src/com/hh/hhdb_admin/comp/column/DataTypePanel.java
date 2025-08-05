package com.hh.hhdb_admin.comp.column;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.db_sql.datatype.DbAllTypeUtil;
import com.hh.frame.db_sql.datatype.TypeDefineEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.NumTextCheckInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/*
* Author:Zhang Dagang
* Date:2024年2月20日
*/
public class DataTypePanel extends HPanel {
	private SelectBox typeSelect=null;
	private CheckBoxInput oftenUsedCheckBox = null;
	private NumTextCheckInput lenInput=new NumTextCheckInput();
	private NumTextCheckInput scaleInput=new NumTextCheckInput();
	
	private List<String> oftenUsed;
	private List<String> rarelyUsed;
	
	
	/**
	 * 用于已经存在数据类型的修改
	 * @param dbtype
	 * @param ver
	 * @param fullTypeDefine
	 * @throws Exception
	 */
	public DataTypePanel(DBTypeEnum dbtype,DBTypeEnum.Version ver,String fullTypeDefine) throws Exception {
		this(dbtype,ver);
		setFullTypeDefine(fullTypeDefine);
	}
	
	/**
	 * 用于数据类型的新建
	 * @param dbtype
	 * @param ver
	 */
	public DataTypePanel(DBTypeEnum dbtype,DBTypeEnum.Version ver) {
		this.getLayout().setLeftWidth(0);
		this.getLayout().setRightWidth(0);
		oftenUsed = DbAllTypeUtil.getTypes(dbtype, ver, null, true);
		rarelyUsed = DbAllTypeUtil.getTypes(dbtype, ver, null, false);
		
		oftenUsedCheckBox=new CheckBoxInput(null,LangMgr.lang(Word2.OFTEN_USED)) {
			@Override
			protected void onSelected(boolean v) {
				oftenUsedBoxOnSelected(v);
			}
		};
		oftenUsedCheckBox.setValue(true);
		typeSelect=new SelectBox(oftenUsed) {
			@Override
			public void onItemSelected(String v) {
				setLenScale(dbtype, ver, v);
			}
		};
		setLenScale(dbtype, ver, typeSelect.getValue());
		
		HBarPanel typeBar=new HBarPanel();
		typeBar.add(oftenUsedCheckBox);
		typeBar.setLastHComp(typeSelect);
		new HGridLayout(GridSplitEnum.C6);
		HGridPanel labelPanel=new HGridPanel(getGridLayout());
		labelPanel.setComp(1,new LabelInput("长度",AlignEnum.LEFT));
		labelPanel.setComp(2,new LabelInput("刻度",AlignEnum.LEFT));
		
		HGridPanel valuePanel=new HGridPanel(getGridLayout());
		valuePanel.setComp(1,lenInput);
		valuePanel.setComp(2,scaleInput);
		this.add(typeBar,labelPanel,valuePanel);
	}
	
	private HGridLayout getGridLayout() {
		HGridLayout layout=new HGridLayout(GridSplitEnum.C6);
		layout.setLeftWidth(0);
		layout.setRightWidth(0);
		return layout;
	}
	
	private void setLenScale(DBTypeEnum dbtype,DBTypeEnum.Version ver, String v) {
		int[] arr=DbAllTypeUtil.getDefaultNumValues(dbtype, ver, v);
		TypeDefineEnum type=DbAllTypeUtil.getDefineType(dbtype, ver, v);
		if(type==TypeDefineEnum.STAND_ALONE) {
			lenInput.setEnabled(false);
			lenInput.setValue("");
			scaleInput.setEnabled(false);
			scaleInput.setValue("");
		}else if(type==TypeDefineEnum.CAN_HAS_ONE_NUM||type==TypeDefineEnum.FORCE_ONE_NUM) {
			lenInput.setEnabled(true);
			scaleInput.setEnabled(false);
			scaleInput.setValue("");
			if(arr!=null && arr.length>0) {
				lenInput.setValue(arr[0]+"");
			}
		}else {
			lenInput.setEnabled(true);
			scaleInput.setEnabled(true);
			if(arr!=null && arr.length==2) {
				lenInput.setValue(arr[0]+"");
				scaleInput.setValue(arr[1]+"");
			}
		}
	}
	
	public String getDataType() {
		return typeSelect.getValue();
	}
	
	public Integer getLenth() {
		String v=lenInput.getValue();
		return StringUtils.isBlank(v)?null:Integer.valueOf(v);
	}
	public Integer getScale() {
		String v=scaleInput.getValue();
		return StringUtils.isBlank(v)?null:Integer.valueOf(v);
	}
	
	public String getFullTypeDefine() {
		if(getScale()!=null)
			return String.format("%s(%d,%d)", getDataType(),getLenth(),getScale());
		if(getLenth()!=null)
			return String.format("%s(%d)", getDataType(),getLenth());
		return getDataType();
	}
	
	public void setFullTypeDefine(String fullTypeDefine) throws Exception {
		try {
			fullTypeDefine=fullTypeDefine.toLowerCase();
			String dataType=fullTypeDefine;
			Integer len=null;
			Integer scale=null;
			if(dataType.indexOf('(')>0){
				dataType=StringUtils.substringBefore(dataType, '(').trim();
				String define=StringUtils.substringBetween(fullTypeDefine, "(", ")").trim();
				if(define.indexOf(',')>0) {
					String[] split=StringUtils.split(define,",");
					len=Integer.valueOf(split[0]);
					scale=Integer.valueOf(split[1]);
					
				}else {
					len=Integer.valueOf(define);
				}
			}
			oftenUsedBoxOnSelected(oftenUsed.contains(dataType.toLowerCase()) || oftenUsed.contains(dataType.toUpperCase()));
			typeSelect.setValue(dataType);
			if(len!=null)lenInput.setValue(len+"");
			if(scale!=null)scaleInput.setValue(scale+"");
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	private void oftenUsedBoxOnSelected(boolean bool){
		if(bool) {
			for (String i : rarelyUsed) {
				typeSelect.remove(i);
			}
		}else {
			for (String i : rarelyUsed) {
				typeSelect.addOption(i);
			}
		}
	}
}
