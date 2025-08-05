package com.hh.hhdb_admin.comp;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.SearchSelectBox;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.ExtDbTypeUtil;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.Collection;
import java.util.Set;

/**
 * 数据库类型，版本选择器
 */
public class DBTypeComboBox {
	private HPanel hp;
	private SearchSelectBox dbTypeBox;
	private SelectBox versBox;
	
	
	public DBTypeComboBox(){
		dbTypeBox = new SearchSelectBox() {
			@Override
			protected void onValueChanged(String value) {
				dbTypeItemChange(value);
				
				versBox.removeAllItems();
				versBox.addOption(LangMgr.lang(Word3.PRINCIPAL_EDITION),null);
				if(!ExtDbTypeUtil.selfDefinedDbTypeMaps.containsKey(dbTypeBox.getValue())) {
					Set<DBTypeEnum.Version> set = DBTypeEnum.getVersions(DBTypeEnum.valueOf(dbTypeBox.getValue()));
					for (DBTypeEnum.Version s : set) {
						versBox.addOption(s.name(),s.name());
					}
				}
			}
		};
		
		versBox = new SelectBox(){
			protected void onItemChange(ItemEvent e) {
				versItemChange(e);
			}
		};
		
		HDivLayout playout = new HDivLayout(GridSplitEnum.C8);
		playout.setLeftWidth(0);
		playout.setRightWidth(0);
		hp = new HPanel(playout);
		hp.add(dbTypeBox,versBox);
	}
	
	public HPanel getHpanel(){
		return hp;
	}

	public void setDataList(Collection<String> dbTypeList) {
		for (String type : dbTypeList) {
			ImageIcon icon = IconFileUtil.getDbIcon(DBTypeEnum.valueOf(type), IconSizeEnum.SIZE_16);
			if(icon==null) {
				dbTypeBox.addOption(type, type, FaSolidEnum.DATABASE);
			}else {
				dbTypeBox.addOption(type, type, icon);
			}
		}
	}
	
	public void addOption(String label, String value, Icon icon) {
		dbTypeBox.addOption(label,value,icon);
	}
	public void addOption(String label, String value, Enum<?> fontType) {
		dbTypeBox.addOption(label,value,fontType);
	}
	
	public void setEnabled(boolean enabled) {
		dbTypeBox.setEnabled(enabled);
		versBox.setEnabled(enabled);
	}
	
	public String getDbTypeValue() {
		return dbTypeBox.getValue();
	}
	
	public void setDbTypeValue(String value) {
		dbTypeBox.setValue(value);
	}
	
	public String getVersValue() {
		return versBox.getValue();
	}
	
	public void setVersValue(String value) {
		versBox.setValue(value);
	}
	
	public void removeAllOption(){
		dbTypeBox.removeAllOption();
	}
	
	protected void dbTypeItemChange(String value) {
	
	}
	
	protected void versItemChange(ItemEvent e) {
	
	}
}
