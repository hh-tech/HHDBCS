package com.hh.hhdb_admin.mgr.table_open;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.LabelCol;
import com.hh.frame.swingui.view.tab.col.bigtext.BigTextCol;
import com.hh.hhdb_admin.mgr.table_open.ui.AddTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ouyangxu
 * @date 2021-01-13 0013 9:43:45
 */
public class AddDataPanel extends LastPanel {
	private DBTypeEnum dbTypeEnum;
	private Map<String, String> colTypeNameMap;

	private HTable table;


	private static final String COL_NAME = "列名";
	private static final String COL_TYPE = "类型";
	private static final String COL_VALUE = "值";
	private static final String NAME = "name";
	private static final String TYPE = "type";

	public AddDataPanel(DBTypeEnum dbTypeEnum, Map<String, String> colTypeNameMap) {
		this.dbTypeEnum = dbTypeEnum;
		this.colTypeNameMap = colTypeNameMap;

		initView();
	}

	private void initView() {
		table = new AddTable(dbTypeEnum, colTypeNameMap);
		table.setEvenBgColor(table.getOddBgColor());
		LabelCol colName = new LabelCol(NAME, COL_NAME);
		colName.setWidth(150);
		colName.setCellEditable(false);
		LabelCol colType = new LabelCol(TYPE, COL_TYPE);
		colType.setWidth(150);
		colType.setCellEditable(false);

		BigTextCol colValue = new BigTextCol("value", COL_VALUE);


		table.addCols(colName, colType, colValue);
		table.setRowHeight(30);
		table.setRowStyle(true);

		setWithScroll(table.getComp());

		List<Map<String, String>> data = new ArrayList<>();
//
		colTypeNameMap.forEach((name, typeName) -> {
			Map<String, String> hashMap = new HashMap<>();
			hashMap.put(NAME, name);
			hashMap.put(TYPE, typeName);
			data.add(hashMap);
//			Enum<?> typeToEnum = tableObjFun.typeToEnum(typeEnum);
//			boolean isLob = ModifyTabDataUtil.isLob(dbTypeEnum, typeToEnum);

		});
		table.load(data, 1);
		setFoot(getFootPanel().getComp());

	}


	private HBarPanel getFootPanel() {
		HButton saveButton = new HButton("保存");
		HButton cancelButton = new HButton("取消");
		HBarLayout barLayout = new HBarLayout();
		barLayout.setBottomHeight(10);
		barLayout.setTopHeight(10);
		barLayout.setxGap(10);
		HBarPanel barPanel = new HBarPanel(barLayout);
		barPanel.add(saveButton);
		barPanel.add(cancelButton);
		return barPanel;
	}

	public DBTypeEnum getDbTypeEnum() {
		return dbTypeEnum;
	}

	public void setDbTypeEnum(DBTypeEnum dbTypeEnum) {
		this.dbTypeEnum = dbTypeEnum;
	}

	public Map<String, String> getColTypeNameMap() {
		return colTypeNameMap;
	}

	public void setColTypeNameMap(Map<String, String> colTypeNameMap) {
		this.colTypeNameMap = colTypeNameMap;
	}

	public HTable getTable() {
		return table;
	}
}
