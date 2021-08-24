package com.hh.hhdb_admin.mgr.table_open.common;


import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.common.util.db.lob.LobUtil;
import com.hh.frame.create_dbobj.table.base.AbsTableObjFun;
import com.hh.frame.create_dbobj.table.comm.CreateTableUtil;
import com.hh.frame.create_dbobj.table.sqlserver.SqlServerDataTypeEnum;
import com.hh.frame.dbmg.exp.data.seltype.OraSelType;
import com.hh.frame.dbmg.exp.data.seltype.SqlserverSelType;
import com.hh.frame.dbobj2.db2.Db2Table;
import com.hh.frame.dbobj2.hhdb.tab.HHdbTable;
import com.hh.frame.dbobj2.mysql.tab.MysqlTable;
import com.hh.frame.dbobj2.ora.tab.OraTable;
import com.hh.frame.dbobj2.sqlserver.SqlServerTable;
import com.hh.frame.dbobj2.version.VersionBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.mgr.table_open.ModifyTabDataComp;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ModifyTabDataUtil {

	public static final String ORA_HIDE_ROW_NUM = "ROWNUM";
	public static final String ORA_HIDE_ROW_ID = "ROWID";
	public static final String PG_HIDE_UPD = "upd";
	public static final String PG_HIDE_C_TID = "ctid";

	public static String SQLSERVER_HIDE_PHY_ID = "%%physloc%%";
	public static String SQLSERVER_HIDE_LOCKRES_ID = "%%lockres%%";
	public static final String SQLSERVER_HIDE_ID_SHOW = "sqlserver_physloc";

	public static final String DB2_HIDE_ID = "RID()";
	public static final String DB2_HIDE_ID_SHOW = "DB2_RID";

	private static Set<String> hideColNames;


	public static final String ADD_ICON = "add";
	public static final String DEL_ICON = "del";
	public static final String SUBMIT_ICON = "submit";
	public static final String CANCEL_ICON = "close";
	public static final String REFRESH_ICON = "refresh";
	public static final String SQL_VIEW_ICON = "sql_view";
	public static final String PREVIOUS_PAGE_ICON = "previous_page";
	public static final String NEXT_PAGE_ICON = "next_page";

	/**
	 * 对应数据库的查询表sql
	 *
	 * @param dbType  数据库类型
	 * @param schema  模式名
	 * @param tabName 表名
	 * @return sql
	 */
	public static String getSelAllSql(DBTypeEnum dbType, VersionBean dbVersion, String schema, String tabName) {
		String sql = null;
		String tabFullName = getTabFullName(dbType, schema, tabName);
		switch (dbType) {
			case hhdb:
			case pgsql:
				sql = String.format("SELECT *,TRUE %s,%s FROM %s", PG_HIDE_UPD, PG_HIDE_C_TID, tabFullName);
				break;
			case oracle:
				sql = String.format("SELECT t.*,%s , %s FROM %s t", ORA_HIDE_ROW_ID, ORA_HIDE_ROW_NUM, tabFullName);
				break;
			case sqlserver:
				int bigVer = 10;
				if (dbVersion != null) {
					bigVer = dbVersion.getBigVer();
				}
				sql = String.format("SELECT t.*,%s AS %s  FROM %s t", bigVer <= 9 ? SQLSERVER_HIDE_LOCKRES_ID : SQLSERVER_HIDE_PHY_ID, SQLSERVER_HIDE_ID_SHOW, tabFullName);
				break;
			case db2:
				sql = String.format("SELECT t.*, %s AS %s FROM %s t ", DB2_HIDE_ID, DB2_HIDE_ID_SHOW, tabFullName);
				break;
			default:
				sql = String.format("SELECT * FROM %s", tabFullName);
		}
		return sql;
	}

	/**
	 * 对应数据库的查询总行数sql
	 *
	 * @param dbType  数据库类型
	 * @param schema  模式名
	 * @param tabName 表名
	 * @return sql
	 */
	public static String getCountSql(DBTypeEnum dbType, String schema, String tabName) {
		String tabFullName = getTabFullName(dbType, schema, tabName);
		return String.format("select count(1) from %s", tabFullName);
	}

	/**
	 * 格式化
	 *
	 * @param dbType
	 * @param schema
	 * @param other
	 * @return
	 */
	public static String getTabFullName(DBTypeEnum dbType, String schema, String other) {
		String markStart = "\"", markEnd = "\"";
		boolean isPg = dbType == DBTypeEnum.hhdb || dbType == DBTypeEnum.pgsql;
		if (dbType == DBTypeEnum.mysql) {
			markEnd = markStart = "`";
		} else if (dbType == DBTypeEnum.sqlserver) {
			markStart = "[";
			markEnd = "]";
		} else if (isPg) {
			schema = StringUtils.isNoneBlank(schema) ? SqlStrUtil.dealDoubleQuote(dbType, schema) : schema;
			other = SqlStrUtil.dealDoubleQuote(dbType, other);
		}
		schema = CreateTableUtil.formatMark(schema, markStart, markEnd);
		other = CreateTableUtil.formatMark(other, markStart, markEnd);
		return StringUtils.isNotBlank(schema) ? schema + "." + other : other;
	}

	public static String getSchema(DBTypeEnum dbType, JdbcBean jdbc) {
		String schema = jdbc.getSchema();
		if (dbType == DBTypeEnum.hhdb || dbType == DBTypeEnum.pgsql) {
			return StringUtils.isBlank(schema) ? (dbType == DBTypeEnum.hhdb ? "public" : jdbc.getUser()) : schema;
		}
		return SqlStrUtil.toProperDbObjName(StringUtils.isBlank(schema) ? jdbc.getUser() : schema, dbType);
	}

	public static Map<String, String> getNameTypeMap(DBTypeEnum dbType, Connection conn, String schemaName, String tableName) throws SQLException {
		List<Map<String, String>> allColumn = null;
		Map<String, String> nameTypeMap = new LinkedHashMap<>();
		String colName = "column_name", type = "data_type";
		switch (dbType) {
			case oracle:
				OraTable oraTable = new OraTable(conn, schemaName, tableName);
				allColumn = oraTable.getAllColumn();
				break;
			case hhdb:
			case pgsql:
				HHdbTable hdbTable = new HHdbTable(conn, schemaName, tableName, dbType == DBTypeEnum.pgsql ? HHdbPgsqlPrefixEnum.pg : HHdbPgsqlPrefixEnum.hh);
				allColumn = hdbTable.getAllColumn();
				type = "type";
				break;
			case mysql:
				MysqlTable mysqlTable = new MysqlTable(conn, schemaName, tableName);
				allColumn = mysqlTable.getAllColumn();
				type = "column_type";
				break;
			case sqlserver:
				SqlServerTable sqlServerTable = new SqlServerTable(conn, schemaName, tableName);
				allColumn = sqlServerTable.getAllColumn();
				type = "column_type";
				break;
			case db2:
				Db2Table db2Table = new Db2Table(conn, schemaName, tableName);
				allColumn = db2Table.getAllColumn();
				allColumn = allColumn.stream().sorted(Comparator.comparing(map -> map.get("col_no"))).collect(Collectors.toList());
				type = "column_type";
				break;
			default:
		}
		if (allColumn != null) {
			for (Map<String, String> map : allColumn) {
				nameTypeMap.put(map.get(colName), map.get(type));
			}
		}
		return nameTypeMap;

	}

	/**
	 * 获取隐藏的列名
	 *
	 * @return 集合
	 */
	public static Set<String> getHideColNames() {
		if (hideColNames == null) {
			hideColNames = new LinkedHashSet<>();
			hideColNames.add(ORA_HIDE_ROW_NUM);
			hideColNames.add(ORA_HIDE_ROW_ID);
			hideColNames.add(PG_HIDE_UPD);
			hideColNames.add(PG_HIDE_C_TID);
			hideColNames.add(SQLSERVER_HIDE_LOCKRES_ID);
			hideColNames.add(SQLSERVER_HIDE_ID_SHOW);
			hideColNames.add(DB2_HIDE_ID);
			hideColNames.add(DB2_HIDE_ID_SHOW);
		}
		return hideColNames;
	}

	/**
	 * 判断是否为大字段类型
	 *
	 * @param dbType 数据库类型
	 * @param type   字段类型
	 * @return
	 */
	public static boolean isLob(DBTypeEnum dbType, Enum<?> type) {
		AbsTableObjFun tableObjFun = CreateTableUtil.getDateType(dbType);
		String name = type.name();
		boolean isBlob = isBlob(dbType, type, tableObjFun);
		boolean isClob = tableObjFun.getClobType().contains(CreateTableUtil.formatTypeName(type)) || "clob".equalsIgnoreCase(name);
		return isBlob || isClob;
	}

	public static boolean isBlob(DBTypeEnum dbType, Enum<?> type, AbsTableObjFun tableObjFun) {
		if (tableObjFun == null) {
			tableObjFun = CreateTableUtil.getDateType(dbType);
		}
		return tableObjFun.getBlobType().contains(CreateTableUtil.formatTypeName(type)) || "blob".equalsIgnoreCase(type.name());
	}

	public static boolean isCellEditable(DBTypeEnum dbType, String typeName) {
		if (StringUtils.isNoneBlank(typeName)) {
			typeName = typeName.toUpperCase();
			switch (dbType) {
				case sqlserver:
					if (typeName.equals(SqlServerDataTypeEnum.TIMESTAMP.name())) {
						return false;
					}
				case oracle:
				default:
			}
		}
		return true;
	}


	public static JsonObject getLobJson(Enum<?> selType, String value, String lobFilePath) {
		JsonObject jObj = new JsonObject();
		jObj.set(JsonCol.__TEXT, selType.name());
		String[] split = value.split("_");
		if (split.length > 0) {
			jObj.set(ModifyTabTool.OFFSET, split[0]);
			jObj.set(ModifyTabTool.LEN, split[1]);
		}
		jObj.set(ModifyTabTool.FILE_PATH, lobFilePath);
		jObj.set(ModifyTabTool.TYPE, selType.name());
		return jObj;
	}

	/**
	 * 判断是否需要格式化显示
	 *
	 * @param dbType
	 * @param type
	 * @return
	 */
	public static String formatVal(DBTypeEnum dbType, Enum<?> type, String colName, String value, String lobFilePath) {
		if (StringUtils.isBlank(value)) {
			return value;
		}
		switch (dbType) {
			case oracle:
				if (type == OraSelType.DATE) {
					return formatValDate(value);
				}
				break;
			case sqlserver:
				if (type == SqlserverSelType.BLOB && colName.equals(SQLSERVER_HIDE_ID_SHOW)) {
					return formatSqlServerRowId(lobFilePath, value);
				}
				break;
			default:
		}
		return value;
	}

	public static String formatSqlServerRowId(String lobFilePath, String value) {
		try {
			File file = new File(lobFilePath);
			if (StringUtils.isNoneBlank(value) && file.exists()) {
				String[] s = value.split("_");
				try (InputStream stream = LobUtil.getStreamFromFile(file, Long.parseLong(s[0]), Long.parseLong(s[1]))) {
					byte[] bytes = IOUtils.toByteArray(stream);
					return "0x" + bytesToHexString(bytes);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static String formatValDate(String value) {
		if (StringUtils.isBlank(value)) {
			return value;
		}
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date parse = dateFormat.parse(value);
			value = dateFormat.format(parse);
		} catch (ParseException ignored) {

		}
		return value;
	}

	public static boolean isOraTime(DBTypeEnum typeEnum, String type) {
		if (typeEnum == DBTypeEnum.oracle && EnumUtils.isValidEnum(OraSelType.class, type)) {
			OraSelType selType = OraSelType.valueOf(type);
			switch (selType) {
				case DATE:
				case TIME:
				case TIME_ZONE:
				case TIME_ZONE_LOCAL:
					return true;
				default:
			}
		}
		return false;
	}

	public static String valToOraDate(String type, String value) {
		if (StringUtils.isBlank(value)) {
			return value;
		}
		boolean validEnum = EnumUtils.isValidEnum(OraSelType.class, type);
		if (validEnum) {
			OraSelType selType = OraSelType.valueOf(type);
			switch (selType) {
				case DATE:
					value = String.format("TO_DATE('%s', 'SYYYY-MM-DD HH24:MI:SS')", value);
					break;
				case TIME_ZONE:
					value = String.format("TO_TIMESTAMP_TZ('%s', 'SYYYY-MM-DD HH24:MI:SS:FF5 TZR')", value);
					break;
				case TIME_ZONE_LOCAL:
				case TIME:
					value = String.format("TO_TIMESTAMP('%s', 'SYYYY-MM-DD HH24:MI:SS:FF6')", value);
					break;
				default:
					break;
			}
		}
		return value;
	}


	/**
	 * 文件大小的格式化 * * @PARAM SIZE * 文件大小，单位为BYTE * @RETURN 文件大小格式化后的文本
	 */
	public static String formatSize(long size) {
		DecimalFormat df = new DecimalFormat("####.00");
		float n = 1024f;
		if (size < n) {
			return size + "B";
		} else if (size < n * n) {
			float kSize = size / n;
			return df.format(kSize) + "KB";
		} else if (size < n * n * n) {
			float mSize = size / n / n;
			return df.format(mSize) + "MB";
		} else if (size < n * n * n * n) {
			float gSize = size / n / n / n;
			return df.format(gSize) + "GB";
		} else {
			return "size: error";
		}
	}

	/**
	 * byte数组转换成16进制字符串
	 *
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (byte b : src) {
			int v = b & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static void requestFocus(HTable tab, int column, ModifyTabDataComp.ColListSelectionListener selectionListener) {
		JTable jTable = ((JTable) tab.getComp());
		if (selectionListener == null) {
			selectionListener = new ModifyTabDataComp.ColListSelectionListener(jTable);
		}
		jTable.getColumnModel().getSelectionModel().addListSelectionListener(selectionListener);
		int n = jTable.getRowCount() - 1;
		jTable.setRowSelectionInterval(n, n);
		jTable.changeSelection(n, column, false, false);
		jTable.editCellAt(n, column);
		Component component = jTable.getEditorComponent();
		if (component instanceof JTextField) {
			component.requestFocus();
			((JTextField) component).selectAll();
		}
	}

	/**
	 * @param name
	 * @return
	 */
	public static ImageIcon getIcon(String name) {
		return IconFileUtil.getIcon(new IconBean(CsMgrEnum.TABLE_OPEN.name(), name, IconSizeEnum.SIZE_16));
	}
}
