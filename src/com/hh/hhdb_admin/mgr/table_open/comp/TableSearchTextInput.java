package com.hh.hhdb_admin.mgr.table_open.comp;

import com.alee.extended.layout.FormLayout;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.hh.hhdb_admin.mgr.login.base.ExpandTextInput;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabTool;

/**
 * @author ouyangxu
 * @date 2021-12-01 0001 15:07:16
 * @description 表格查找文本框
 */
public class TableSearchTextInput extends ExpandTextInput {

	protected String tips = "当前页面查找";

	public TableSearchTextInput() {
		super();
		setTips(tips);
	}

	@Override
	protected void initLeadingComponent() {
		leadingPanel = new WebPanel(new FormLayout());
//		WebSplitButton searchButton = new WebSplitButton(StyleId.splitbuttonUndecorated);
		WebLabel searchButton = new WebLabel();
		searchButton.setIcon(ModifyTabTool.getIcon(TableSearchToolBar.SEARCH));
		leadingPanel.add(searchButton);
		leadingPanel.setPadding(2, 5, 0, 5);
	}

}
