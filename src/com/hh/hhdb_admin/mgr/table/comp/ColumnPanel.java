package com.hh.hhdb_admin.mgr.table.comp;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.table.CreateTableTool;
import com.hh.frame.create_dbobj.table.base.AbsCreateKey;
import com.hh.frame.create_dbobj.table.base.impl.CreatePk;
import com.hh.frame.create_dbobj.table.base.type.AbsCreateCol;
import com.hh.frame.create_dbobj.table.comm.CreateStrCommon;
import com.hh.frame.create_dbobj.table.comm.CreateTableUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.HTipTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;
import com.hh.hhdb_admin.mgr.table.TableComp;
import com.hh.hhdb_admin.mgr.table.column.TypeColumn;
import com.hh.hhdb_admin.mgr.table.common.TableCreatePanel;
import com.hh.hhdb_admin.mgr.table_open.ModifyTabDataComp;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabDataUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author oyx
 * @Description: 列管理
 * @date 2020-9-26   16:39:20
 */
public class ColumnPanel extends TableCreatePanel {
	public static final String TAB_ID = "id";
	private static final String TAB_COL_NAME = "colName";
	private static final String TAB_DATA_TYPE = "dataType";
	private static final String TAB_DEFAULT = "default";
	private static final String TAB_IS_NOT_NULL = "isNotNull";
	private static final String TAB_IS_PRIMARY_KEY = "isPrimaryKey";
	private static final String TAB_ANNOTATE = "annotate";

	private ModifyTabDataComp.ColListSelectionListener colListSelectionListener;

	public ColumnPanel() {
		this.table = initTable();
		initButtons();
		initBar();
		columnNames = new String[]{TAB_COL_NAME, TAB_DATA_TYPE, TAB_DEFAULT, TAB_IS_NOT_NULL, TAB_IS_PRIMARY_KEY, TAB_ANNOTATE};

	}

	public HTable initTable() {
		HTable table = new HTipTable();
		table.addCols(new DataCol(TAB_COL_NAME, TableComp.getLang("columnName")));
		TypeColumn typeCol = new TypeColumn(TAB_DATA_TYPE, TableComp.getLang("type"));
		typeCol.setWidth(140);

		table.addCols(typeCol);
		table.addCols(new DataCol(TAB_DEFAULT, TableComp.getLang("defaultValue")));
		BoolCol notNullCol = new BoolCol(TAB_IS_NOT_NULL, TableComp.getLang("notNull"));
		notNullCol.setWidth(80);
		table.addCols(notNullCol);
		BoolCol primaryCol = new BoolCol(TAB_IS_PRIMARY_KEY, TableComp.getLang("primaryKey"));
		primaryCol.setWidth(60);
		table.addCols(primaryCol);
		table.addCols(new DataCol(TAB_ANNOTATE, TableComp.getLang("TABLE_COMMENT")));
		table.setRowHeight(30);
		table.hideSeqCol();
		table.setEvenBgColor(table.getOddBgColor());
		table.setRowHeight(30);
		return table;
	}

	private void initButtons() {
		addButton = createAddButton(TableComp.getLang("addColumn"));
		addButton.addActionListener(new AddListener(table, this));
		delButton = createDelButton(TableComp.getLang("deleteColumn"), table);
	}

	@Override
	protected void callBack() {
		ModifyTabDataUtil.requestFocus(table, 0, colListSelectionListener);
	}

	@Override
	public List<Map<String, String>> getData(int num) {
		List<Map<String, String>> data = new ArrayList<>();
		int rowCount = table.getRowCount();
		for (int i = 0; i < num; i++) {
			String colName = "column_" + (rowCount + i + 1);
			Map<String, String> map = new HashMap<>(6);
			map.put(TAB_DATA_TYPE, TypeColumn.getDefType().toString());
			map.put(TAB_COL_NAME, colName);
			map.put(TAB_DEFAULT, "");
			map.put(TAB_IS_NOT_NULL, "false");
			map.put(TAB_IS_PRIMARY_KEY, "false");
			map.put(TAB_ANNOTATE, "");
			data.add(map);
		}
		return data;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}


	@Override
	public void build(CreateTableTool createTableTool) throws Exception {
		DBTypeEnum dbTypeEnum = TableComp.getDbType();
		JsonArray jsonArray = getRowColJson();
		List<AbsCreateCol> cols = new ArrayList<>();
		List<AbsCreateKey> keys = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject row = jsonArray.get(i).asObject();
			String colName = row.getString(TAB_COL_NAME);
			if (StringUtils.isBlank(colName)) {
				continue;
			}
			String dataType = row.getString(TAB_DATA_TYPE);
			String jsonLength = row.getString(TypeColumn.JSON_LENGTH);
			String jsonScale = row.getString(TypeColumn.JSON_SCALE);
			String defaultValue = row.getString(TAB_DEFAULT);
			Boolean isNotNull = row.getBoolean(TAB_IS_NOT_NULL);
			Boolean isPrimaryKey = row.getBoolean(TAB_IS_PRIMARY_KEY);
			String annotate = row.getString(TAB_ANNOTATE);

			Integer length = null, scale = null;
			if (StringUtils.isNoneBlank(jsonLength) && !getTableObjFun().getNoLengthType().contains(dataType.toUpperCase())) {
				length = Integer.valueOf(jsonLength);
			}
			if (StringUtils.isNoneBlank(jsonScale) && getTableObjFun().getTwoLengthType().contains(dataType.toUpperCase())) {
				scale = Integer.valueOf(jsonScale);
			}
			AbsCreateCol createCol = CreateTableUtil.getCreateCol(dbTypeEnum, dataType, colName, length, scale);
			if (createCol != null) {
				createCol.setDefValue(defaultValue);
				createCol.setIsNoNull(isNotNull);
				createCol.setColComment(annotate);
				cols.add(createCol);
				if (isPrimaryKey != null && isPrimaryKey) {
					CreatePk createPk = CreateTableUtil.getCreatePk(getTableObjFun().getTypeEnum(), createCol);
					if (createPk.toString().equals(CreateStrCommon.BUILD_ERROR)) {
						String error = String.format(" The column named \"%s\" cannot be a column of a primary key or unique key constraint because it can contain null values..",
								createCol.getName() == null ? "" : createCol.getName());
						throw new SQLException(error);
					}
					keys.add(createPk);
				}
			}
		}
		createTableTool.setCols(cols);
		createTableTool.setKeys(keys);
	}


	@Override
	protected void colJsonAdd(int i, JsonObject columnJson, String columnName, Integer index) {
		TableModel model = table.getComp().getModel();

		Object valueAt = model.getValueAt(i, index);
		if (valueAt == null) {
			return;
		}
		if (columnName.equalsIgnoreCase(TAB_IS_NOT_NULL) || columnName.equalsIgnoreCase(TAB_IS_PRIMARY_KEY)) {
			columnJson.add(columnName, Boolean.parseBoolean(valueAt.toString()));
		} else if (columnName.equalsIgnoreCase(TAB_DATA_TYPE)) {
			JsonObject typeJson = Json.parse(valueAt.toString()).asObject();
			String length = typeJson.getString(TypeColumn.JSON_LENGTH);
			String dataType = typeJson.getString(TypeColumn.__TEXT);
			String scale = typeJson.getString(TypeColumn.JSON_SCALE);
			columnJson.add(TAB_DATA_TYPE, dataType);
			columnJson.add(TypeColumn.JSON_LENGTH, length);
			columnJson.add(TypeColumn.JSON_SCALE, scale == null ? EMPTY : scale);
		} else {
			columnJson.add(columnName, valueAt.toString());
		}
	}

}
