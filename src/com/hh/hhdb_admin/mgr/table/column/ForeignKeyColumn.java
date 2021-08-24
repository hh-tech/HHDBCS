package com.hh.hhdb_admin.mgr.table.column;

import com.hh.frame.create_dbobj.table.base.AbsTableObjFun;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.hhdb_admin.mgr.table.TableComp;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author oyx
 * @date 2020-10-12  0012 16:22:17
 */
public class ForeignKeyColumn extends SelectColumn {

	private final HTable foreignKeyTab;
	private final Connection conn;

	public ForeignKeyColumn(String name, String value, HTable hTab, String title, HTable colTbale, Connection conn) {
		super(name, value, hTab, title, colTbale);
		this.foreignKeyTab = hTab;
		this.conn = conn;
	}

	/**
	 * 获取外键表的列名
	 *
	 * @return
	 */
	@Override
	public Set<String> getSelCol(HTable table, int index, int row) throws SQLException {
		AbsTableObjFun tableObjFun = TableComp.getCreateTabTool().getTableObjFun();
		Set<String> colNames = new LinkedHashSet<>();
		TableModel model = ((JTable) foreignKeyTab.getComp()).getModel();
		Object valueAt = model.getValueAt(row, getIndex());
		if (valueAt != null) {
			JsonObject object = Json.parse(valueAt.toString()).asObject();
			JsonValue jsonTable = object.get(ForeignTableColumn.FOREIGN_TABLE_NAME);
			JsonValue jsonSchema = object.get(ForeignTableColumn.FOREIGN_SCHEMA_NAME);
			String schemaName = jsonSchema.isNull() ? null : object.getString(ForeignTableColumn.FOREIGN_SCHEMA_NAME);
			String tableName = jsonTable.isNull() ? null : object.getString(ForeignTableColumn.FOREIGN_TABLE_NAME);
			if (schemaName == null || tableName == null) {
				return colNames;
			}
			setTitle(String.format(TableComp.getLang("selectTableColumn"), tableName));
			return new LinkedHashSet<>(tableObjFun.getAllColNames(conn, schemaName, tableName));
		}
		return colNames;
	}

	@Override
	protected String getTips() {
		return TableComp.getLang("cannotGetTable");
	}
}
