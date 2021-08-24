package com.hh.hhdb_admin.mgr.sequence.common;

import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.TextAreaInput;
/**
 * SQL预览
 * @author HuBingBing
 * @date 2020年12月22日上午9:18:48
 */
public class PreviewComp extends HPanel{

	private TextAreaInput sqlView= new TextAreaInput();
	
	public PreviewComp() {
		
		sqlView.setEnabled(false);
		add(sqlView);
	}

	public String getSqlViewValue() {
		
		
		return sqlView.getValue();
	}

	public void setSqlViewValue(String sql) {
		
		sqlView.setValue(sql);
		
	}
	
	
}
