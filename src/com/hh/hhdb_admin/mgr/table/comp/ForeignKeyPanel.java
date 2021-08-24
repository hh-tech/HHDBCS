package com.hh.hhdb_admin.mgr.table.comp;

import com.hh.frame.create_dbobj.table.CreateTableTool;
import com.hh.frame.create_dbobj.table.base.AbsCreateFk;
import com.hh.frame.create_dbobj.table.base.AbsTableObjFun;
import com.hh.frame.create_dbobj.table.comm.CreateTableUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.HTipTable;
import com.hh.frame.swingui.view.tab.col.ListCol;
import com.hh.hhdb_admin.mgr.table.TableComp;
import com.hh.hhdb_admin.mgr.table.column.ForeignKeyColumn;
import com.hh.hhdb_admin.mgr.table.column.ForeignTableColumn;
import com.hh.hhdb_admin.mgr.table.column.SelectColumn;
import com.hh.hhdb_admin.mgr.table.common.TableCreatePanel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.table.TableModel;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

/**
 * @author oyx
 * @Description: 外键管理
 * @date 2020-10-12  0012 16:20:12
 */
public class ForeignKeyPanel extends TableCreatePanel {
	private static final String COL_NAME = "colName";
	private static final String FOREIGN_TABLE_NAME = "foreignTableName";
	private static final String FOREIGN_TABLE_COL_NAME = "foreignTableColName";
	private static final String FOREIGN_ON_DELETE = "foreignOnDelete";
	private static final String FOREIGN_ON_UPDATE = "foreignOnUpdate";

	private final ColumnPanel columnPanel;
	private final Connection conn;
	AbsTableObjFun tableObjFun;

	public ForeignKeyPanel(ColumnPanel columnPanel, Connection conn) {
		tableObjFun = getTableObjFun();
		this.columnPanel = columnPanel;
		this.conn = conn;
		this.table = initTable();
		initButtons();
		initBar();
		columnNames = new String[]{COL_NAME, FOREIGN_TABLE_NAME, FOREIGN_TABLE_COL_NAME, FOREIGN_ON_DELETE, FOREIGN_ON_UPDATE};
	}

	public HTable initTable() {
		HTable table = new HTipTable();
		table.setNullSymbol("");
		SelectColumn selectColumn = new SelectColumn(COL_NAME, TableComp.getLang("column"),
				table, TableComp.getLang("selectColumn"), columnPanel.getTable());
		selectColumn.setWidth(120);
		ForeignTableColumn tableColumn = new ForeignTableColumn(FOREIGN_TABLE_NAME,
				TableComp.getLang("ForeKeyTableName"), table, conn);
		ForeignKeyColumn foreignKeyColumn = new ForeignKeyColumn(FOREIGN_TABLE_COL_NAME, TableComp.getLang("foreignKeyTableColumn"),
				table, TableComp.getLang("selectColumn"), null, conn);
		foreignKeyColumn.setIndex(1);
		table.addCols(selectColumn,tableColumn,foreignKeyColumn);

		String[] onUpdateArray = tableObjFun.getOnUpdateArray();
		String[] onDelArray = tableObjFun.getOnDelArray();
		if (onUpdateArray != null && onUpdateArray.length > 0) {
			table.addCols(new ListCol(FOREIGN_ON_UPDATE, TableComp.getLang("onUpdating"), Arrays.asList(onUpdateArray)));
		}
		if (onDelArray != null && onDelArray.length > 0) {
			table.addCols(new ListCol(FOREIGN_ON_DELETE, TableComp.getLang("onDeletion"), Arrays.asList(onDelArray)));
		}
		table.setRowHeight(30);
		table.hideSeqCol();
		table.setEvenBgColor(table.getOddBgColor());
		return table;
	}

	private void initButtons() {
		addButton = createAddButton(TableComp.getLang("addForeignKey"));
		addButton.addActionListener(new AddListener(table, this));
		delButton = createDelButton(TableComp.getLang("deleteForeignKey"), table);
	}


	@Override
	public void build(CreateTableTool createTableTool) throws Exception {
		JsonArray jsonArray = getRowColJson();
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject row = jsonArray.get(i).asObject();
			JsonValue jsonValue = row.get(ForeignTableColumn.FOREIGN_TABLE_NAME);
			if (jsonValue == null) {
				continue;
			}
			String foreignTableName = jsonValue.asString();
			List<String> colNames = getColNames(row, COL_NAME);
			if (colNames.size() > 0 && StringUtils.isNoneBlank(foreignTableName)) {
				jsonValue= row.get(ForeignTableColumn.FOREIGN_SCHEMA_NAME);
				if (jsonValue == null) {
					continue;
				}
				String fSchema= jsonValue.asString();
				List<String> fColNames = getColNames(row, FOREIGN_TABLE_COL_NAME);
				AbsCreateFk createFk = CreateTableUtil.getCreateFk(TableComp.getDbType(), colNames, fSchema, foreignTableName, fColNames);
				if (createFk != null) {
					String onUpdate = row.getString(FOREIGN_ON_UPDATE);
					if (StringUtils.isNoneBlank(onUpdate)) {
						createFk.setOnUpdate(onUpdate);
					}
					String onDelete = row.getString(FOREIGN_ON_DELETE);
					if (StringUtils.isNoneBlank(onDelete)) {
						createFk.setOnDelete(onDelete);
					}
					createTableTool.addKey(createFk);
				}
			}

		}
	}


	@Override
	protected void colJsonAdd(int i, JsonObject columnJson, String columnName, Integer index) {
		TableModel model =  table.getComp().getModel();
		Object valueAt = model.getValueAt(i, index);
		if (valueAt == null) {
			return;
		}
		switch (columnName) {
			case COL_NAME:
			case FOREIGN_TABLE_COL_NAME:
				JsonValue jsonValue = Json.parse(valueAt.toString()).asObject().get(SelectColumn.COL_NAMES);
				columnJson.add(columnName, jsonValue != null ? jsonValue.asArray() : new JsonArray());
				break;
			case FOREIGN_TABLE_NAME:
				JsonObject object = Json.parse(valueAt.toString()).asObject();
				jsonValue = object.get(ForeignTableColumn.FOREIGN_SCHEMA_NAME);
				columnJson.add(ForeignTableColumn.FOREIGN_SCHEMA_NAME, jsonValue.toString() == null ? EMPTY : jsonValue.asString());
				String tableName = object.getString(ForeignTableColumn.FOREIGN_TABLE_NAME);
				columnJson.add(ForeignTableColumn.FOREIGN_TABLE_NAME, tableName == null ? EMPTY : tableName);
				break;
			default:
				columnJson.add(columnName, valueAt.toString().trim());
		}
	}

}
