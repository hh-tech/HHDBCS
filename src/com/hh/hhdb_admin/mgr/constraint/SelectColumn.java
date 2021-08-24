package com.hh.hhdb_admin.mgr.constraint;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.JsonUtil;
import com.hh.frame.dbobj2.db2.tab.Db2Table;
import com.hh.frame.dbobj2.dm.tab.DmTable;
import com.hh.frame.dbobj2.hhdb.tab.HHdbTable;
import com.hh.frame.dbobj2.mysql.tab.MysqlTable;
import com.hh.frame.dbobj2.ora.tab.OraTable;
import com.hh.frame.dbobj2.sqlserver.tab.SqlTable;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.login.LoginUtil;
import com.hh.hhdb_admin.mgr.table.common.SelectColDialog;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yusai
 */
public class SelectColumn extends JsonCol {

	private static final String LOG_NAME = SelectColumn.class.getSimpleName();
	private String title;
	protected Connection conn;
	private String schema;
	private String tableName;
	static final String COL_NAMES = "col_names";

	public SelectColumn(String name, String value, String title, Connection conn, String schema, String tableName) {
		super(name, value);
		this.title = title;
		this.conn = conn;
		this.schema = schema;
		this.tableName = tableName;
	}

	@Override
	public JsonObject onClick(JsonObject json, int row, int column) {
		String colName = null;
		JsonObject res = new JsonObject();
		try {
			if (json != null) {
				colName = json.getString(__TEXT);
			}
			SelectColDialog selectColDialog = new SelectColDialog(getSelCol(), getTitle(), colName);
			selectColDialog.setTips(getTips());
			selectColDialog.loadTable(colName);
			Set<String> selectCol = selectColDialog.getSelectCol();
			if (selectCol != null && selectCol.size() > 0) {
				res.add(JsonCol.__TEXT, StringUtils.join(selectCol, ","));
				res.add(COL_NAMES, JsonUtil.parseArray(selectCol.toArray()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logUtil.error(LOG_NAME, e);
		}
		return res;
	}

	private Set<String> getSelCol() throws Exception {
		DBTypeEnum dbTypeEnum = DriverUtil.getDbType(conn);
		schema = LoginUtil.getRealName(schema, dbTypeEnum.name());
		tableName = LoginUtil.getRealName(tableName, dbTypeEnum.name());
		switch (dbTypeEnum) {
		case hhdb:
		case pgsql:
			String prefix = dbTypeEnum.name().substring(0, 2);
			HHdbTable hhTable = new HHdbTable(conn, schema, tableName, HHdbPgsqlPrefixEnum.valueOf(prefix));
			List<Map<String, String>> allColumn = hhTable.getAllColumn();
			return allColumn.stream().map(item -> item.get("column_name"))
					.collect(Collectors.toCollection(LinkedHashSet::new));
		case oracle:
			return new OraTable(conn, schema, tableName).getColumnNameList();
		case mysql:
			return new MysqlTable(conn, schema, tableName).getColumnNameList(schema);
		case sqlserver:
			return new SqlTable(conn, schema, tableName).getColumnNameList(schema);
		case db2:
			return new Db2Table(conn, schema, tableName).getColumnNameList(schema);
		case dm:
			return new DmTable(conn, schema, tableName).getColumnNameList();
		default:
			break;
		}
		return null;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private String getTips() {
		return "请先添加列！";
	}
}
