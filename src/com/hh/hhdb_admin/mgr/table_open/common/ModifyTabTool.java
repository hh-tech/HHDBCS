package com.hh.hhdb_admin.mgr.table_open.common;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.common.util.db.lob.LobUtil;
import com.hh.frame.create_dbobj.table.base.AbsTableObjFun;
import com.hh.frame.create_dbobj.table.comm.CreateTableUtil;
import com.hh.frame.csv.CsvUtil;
import com.hh.frame.dbmg.exp.data.seltype.SqlserverSelType;
import com.hh.frame.dbobj2.TabObjEnum;
import com.hh.frame.dbobj2.mysql.tab.MysqlPk;
import com.hh.frame.dbobj2.mysql.tab.MysqlTable;
import com.hh.frame.dbobj2.sqlserver.tab.SqlPk;
import com.hh.frame.dbobj2.sqlserver.tab.SqlTable;
import com.hh.frame.dbobj2.version.VersionBean;
import com.hh.frame.dbquery.QueryTool;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.abs.AbsCol;
import com.hh.frame.swingui.view.tab.col.bigtext.BigTextCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.lob_panel.LobViewer;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.table_open.ModifyTabDataComp;
import com.hh.hhdb_admin.mgr.table_open.ui.LobJsonCol;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.ActionEvent;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ouyangxu
 * @date 2020-12-24 10:30:08
 */
public class ModifyTabTool {
	public static final String OFFSET = "offset";
	public static final String LEN = "len";
	public static final String FILE_PATH = "file_path";
	public static final String TYPE = "type";
	private HTable table;
	private DBTypeEnum dbTypeEnum;
	private Map<String, Enum<?>> lobMap;
	private ModifyTabDataComp dataComp;
	private boolean readOnly = false;
	private AbsTableObjFun tableObjFun;
	private VersionBean dbVersion;
	private Map<Integer, List<AddLobBean>> addLobNumMap;
	Map<String, String> nameTypeMap;


	public ModifyTabTool(DBTypeEnum dbTypeEnum, HTable table) {
		this.table = table;
		this.dbTypeEnum = dbTypeEnum;
		this.lobMap = new LinkedHashMap<>();
		this.tableObjFun = CreateTableUtil.getDateType(dbTypeEnum);
		addLobNumMap = new LinkedHashMap<>();
	}

	public ModifyTabTool(DBTypeEnum dbTypeEnum, HTable table, ModifyTabDataComp dataComp) {
		this(dbTypeEnum, table);
		this.dataComp = dataComp;
	}

	/**
	 * 把查出来的数据转换成表格的数据
	 *
	 * @return list<Map>
	 * @throws IOException
	 */
	public List<Map<String, String>> toDataMap(List<Enum<?>> selTypes, List<String> colNames, File csv) throws IOException {
		if (csv == null || !csv.exists()) {
			return new ArrayList<>();
		}
		List<Map<String, String>> data = new ArrayList<>();
		List<List<String>> csvData = CsvUtil.csv2Data(csv, "");
		String lobFilePath = csv.getAbsolutePath().replaceAll("csv", "dat");
		for (List<String> csvDatum : csvData) {
			Map<String, String> rowMap = new LinkedHashMap<>(16);
			for (int j = 0; j < selTypes.size(); j++) {
				String colName = colNames.get(j);
				if (rowMap.containsKey(colName)) {
					//防止普通列名称相同的情况
					colName = colName + j;
				}
				String value = toTabValue(selTypes.get(j), colName, csvDatum.get(j), lobFilePath);
				rowMap.put(colName, value);
			}
			data.add(rowMap);
		}
		return data;
	}

	private String toTabValue(Enum<?> selType, String colName, String value, String lobFilePath) {
		if (StringUtils.isBlank(value) || "____null".equals(value)) {
			return null;
		}
		if (ModifyTabDataUtil.isLob(dbTypeEnum, selType) && !ModifyTabDataUtil.getHideColNames().contains(colName)) {
			return ModifyTabDataUtil.getLobJson(selType, value, lobFilePath).toString();
		}
		value = ModifyTabDataUtil.formatVal(dbTypeEnum, selType, colName, value, lobFilePath);
		return value;
	}


	/**
	 * 创建表格列
	 *
	 * @param queryTool  查询器(大字段需要)
	 * @param jdbc       jdbc连接
	 * @param schemaName 模式名
	 * @param tableName  表名
	 * @return
	 */
	public List<AbsCol> createCol(QueryTool queryTool, JdbcBean jdbc, String schemaName, String tableName) {
		List<AbsCol> cols = new ArrayList<>();
		List<String> colNames = queryTool.getColNames();
		List<Enum<?>> selTypes = queryTool.getSelTypes();
		if (colNames == null || selTypes == null) {
			return cols;
		}
		Map<String, String> rowMap = new LinkedHashMap<>(16);
		for (int i = 0; i < colNames.size(); i++) {
			String colName = colNames.get(i);
			AbsCol col;
			//导出到csv的类型
			Enum<?> type = selTypes.get(i);
			//数据库中查询到的类型
			String typeName = null;
			if (!readOnly) {
				typeName = nameTypeMap.get(colName);
			}
			if (ModifyTabDataUtil.isLob(dbTypeEnum, type) && !ModifyTabDataUtil.getHideColNames().contains(colName)) {
				lobMap.put(colNames.get(i), selTypes.get(i));
				col = new LobJsonCol(dbTypeEnum, rowMap.containsKey(colName) ? colName + i : colName, colName, type);
				((LobJsonCol) col).setReadOnly(isReadOnly());
				((LobJsonCol) col).setCleanListener(new CleanListener());
				SaveLobListener lobListener = new SaveLobListener(queryTool, jdbc, ((LobJsonCol) col), schemaName, tableName, colName, type) {
					@Override
					protected void callBack(boolean isAdd) throws Exception {
						((LobJsonCol) col).dispose();
						if (!isAdd) {
							PopPaneUtil.info("保存成功");
							dataComp.refreshTab();
						}
					}
				};
				((LobJsonCol) col).setSaveListener(lobListener);
			} else if (typeName != null && tableObjFun.getBigTextColType().contains(typeName.toUpperCase())) {
				col = new BigTextCol(rowMap.containsKey(colName) ? colName + i : colName, colName);
			} else {
				col = new DataCol(rowMap.containsKey(colName) ? colName + i : colName, colName);
			}
			if (typeName != null) {
				col.setCellEditable(ModifyTabDataUtil.isCellEditable(dbTypeEnum, typeName));
			}

			cols.add(col);
			rowMap.put(colName, colName);
		}
		return cols;
	}

	private StringBuilder buildUpdateSql(QueryTool queryTool, Connection conn, String schemaName, String tableName, boolean isUpdate, LinkedHashSet<String> pks, HTabRowBean rowBean, Map<String, String> nameType) throws SQLException, IOException {
		StringBuilder sql = new StringBuilder();
		Map<String, String> oldRow = rowBean.getOldRow();
		if (oldRow == null) {
			return sql;
		}
		String name = tableName;
		if (StringUtils.isNoneBlank(schemaName)) {
			name = ModifyTabDataUtil.getTabFullName(dbTypeEnum, schemaName, tableName);
		}
		if (isUpdate) {
			String setColNames = getSetColNames(nameType, rowBean);
			if (StringUtils.isBlank(setColNames)) {
				return null;
			}
			sql.append("UPDATE ").append(name).append(" SET ").append(setColNames).append(" WHERE ");
		} else {
			sql.append("DELETE ").append("FROM ").append(name).append(" WHERE ");
		}
		if (dbTypeEnum == DBTypeEnum.mysql) {
			sql.append(getMysqlWhereSql(queryTool, conn, schemaName, tableName, pks, oldRow));
		} else if (dbTypeEnum == DBTypeEnum.sqlserver && pks != null && pks.size() > 0) {
			String whereSql = getSqlServerPkWhereSql(conn, schemaName, tableName, pks, oldRow);
			sql.append(StringUtils.isNotBlank(whereSql) ? whereSql : appendOtherWhereSql(oldRow));
		} else {
			sql.append(appendOtherWhereSql(oldRow));
		}
		return sql;
	}


	/**
	 * @param conn       数据库连接
	 * @param schemaName 模式名
	 * @param tableName
	 * @throws SQLException
	 */
	public List<String> getUpdateOrDelSql(QueryTool query, Connection conn, String schemaName, String tableName) throws SQLException, IOException {
		List<String> listSql = new ArrayList<>();
		List<HTabRowBean> updateRowBeans = table.getRowBeans(RowStatus.UPDATE);
		List<HTabRowBean> delRowBeans = table.getRowBeans(RowStatus.DEL);
		if (delRowBeans != null) {
			updateRowBeans.addAll(delRowBeans);
		}
		if (updateRowBeans == null || updateRowBeans.size() <= 0) {
			return listSql;
		}
		LinkedHashSet<String> pks = null;
		Map<String, String> nameType = getNameType(query);

		if (dbTypeEnum == DBTypeEnum.mysql) {
			MysqlTable mysqlTable = new MysqlTable(conn, schemaName, tableName);
			pks = mysqlTable.getSubObjNames(TabObjEnum.PK);
		} else if (dbTypeEnum == DBTypeEnum.sqlserver) {
			SqlTable mssql = new SqlTable(conn, schemaName, tableName);
			pks = mssql.getSubObjNames(TabObjEnum.PK);
		}
		for (HTabRowBean rowBean : updateRowBeans) {
			RowStatus status = rowBean.getStatus();
			StringBuilder sql = buildUpdateSql(query, conn, schemaName, tableName, status == RowStatus.UPDATE, pks, rowBean, nameType);
			if (sql != null && sql.length() > 0) {
				listSql.add(sql.toString());
			}
		}
		return listSql;
	}


	public List<String> getAddSql(String schemaName, String tableName) {
		List<InsertSqlBean> sqlBeans = buildInsertSqlBeans(schemaName, tableName);
		return sqlBeans.stream().map(InsertSqlBean::toString).collect(Collectors.toList());
	}

	public List<InsertSqlBean> buildInsertSqlBeans(String schemaName, String tableName) {
		List<HTabRowBean> rowBeans = table.getRowBeans(RowStatus.ADD);
		List<InsertSqlBean> sqlBeans = new ArrayList<>();
		if (rowBeans == null || rowBeans.size() <= 0) {
			return sqlBeans;
		}
		for (HTabRowBean rowBean : rowBeans) {
			Map<String, String> currRow = rowBean.getCurrRow();
			InsertSqlBean sqlBean = new InsertSqlBean(dbTypeEnum, schemaName, tableName);
			currRow.forEach((key, value) -> {
				boolean flag = true;
				StringBuilder values = new StringBuilder();
				if (value != null) {
					sqlBean.addColNames(ModifyTabDataUtil.getTabFullName(dbTypeEnum, null, key));
					String type = nameTypeMap.get(key);
					boolean isMssqlText = dbTypeEnum == DBTypeEnum.sqlserver && type != null && tableObjFun.getCharacterStringType().contains(type.toUpperCase());
					if (isMssqlText) {
						values.append("N");
					} else if (addLobNumMap != null) {
//						sqlBean.setAddLobs(addLobNumList)
						List<AddLobBean> addLobBeans = addLobNumMap.get(rowBean.getCurrRowNum());
						if (addLobBeans != null) {
							sqlBean.setAddLobs(addLobBeans);
							for (AddLobBean addLob : addLobBeans) {
								if (addLob.getName().equals(key)) {
									addLob.setType(type);
									values.append("?");
									flag = false;
									break;
								}
							}
						}
					}
					if (flag) {
						values.append("'").append(value).append("'");
					}
					sqlBean.addValues(values.toString());
				}
			});
			sqlBeans.add(sqlBean);
		}
		return sqlBeans;
	}

	/**
	 * 执行对数据库的修改删除操作
	 *
	 * @param listUpdateSql sql集合
	 */
	public int doUpdate(Connection conn, List<String> listUpdateSql, List<InsertSqlBean> sqlBeanList) throws Exception {
		int count = 0;
		try {
			//设置为手动提交
			conn.setAutoCommit(false);
			for (String sql : listUpdateSql) {
				SqlExeUtil.executeUpdate(conn, sql);
				count++;
			}
			if (sqlBeanList != null) {
				for (InsertSqlBean sqlBean : sqlBeanList) {
					List<AddLobBean> addLobs = sqlBean.getAddLobs();
					if (addLobs != null && addLobs.size() > 0) {
						updateLob(conn, sqlBean.toString(), addLobs);
					} else {
						SqlExeUtil.executeUpdate(conn, sqlBean.toString());
					}
					count++;
				}
			}
			conn.commit();
		} catch (Exception exception) {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.rollback();
				}
			} catch (SQLException e) {
				exception = e;
			}
			logUtil.error(ModifyTabDataComp.LOG_NAME, exception);
			throw exception;

		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					logUtil.error(ModifyTabDataComp.LOG_NAME, e);
				}
			}
		}
		return count;
	}


	private String getSetColNames(Map<String, String> nameType, HTabRowBean rowBean) {
		Map<String, String> map = rowBean.getCurrRow();
		StringBuilder buffer = new StringBuilder();
		for (String str : map.keySet()) {
			if (lobMap.size() > 0 && lobMap.get(str) != null) {
				continue;
			}
			String val = StringUtils.replace(map.get(str), "'", "''");
			val = (StringUtils.isBlank(val) ? "" : val).trim();
			buffer.append(buffer.length() == 0 ? "" : ",");
			if (dbTypeEnum == DBTypeEnum.mysql) {
				buffer.append("`").append(str).append("`");
			} else if (dbTypeEnum == DBTypeEnum.sqlserver) {
				buffer.append("[").append(str).append("]");
			} else {
				buffer.append(SqlStrUtil.dealDoubleQuote(dbTypeEnum, str));
			}
			String type = nameType.get(str);
			if (ModifyTabDataUtil.isOraTime(dbTypeEnum, type)) {
				val = ModifyTabDataUtil.valToOraDate(type, val);
				buffer.append(" = ").append(val);
			} else if (dbTypeEnum == DBTypeEnum.sqlserver) {
				type = nameTypeMap.get(str);
				buffer.append(" = ");
				if (type != null && tableObjFun.getCharacterStringType().contains(type.toUpperCase())) {
					buffer.append("N");
				}
				buffer.append("'").append(val).append("'");
			} else {
				buffer.append(" = ").append("'").append(val).append("'");
			}
		}
		return buffer.toString();
	}

	private String appendOtherWhereSql(Map<String, String> oldRow) {
		StringBuilder sql = new StringBuilder();
		String name = null, value = null;
		if (oldRow == null) {
			return "";
		}
		switch (dbTypeEnum) {
			case hhdb:
			case pgsql:
				name = value = ModifyTabDataUtil.PG_HIDE_C_TID;
				break;
			case oracle:
				name = value = ModifyTabDataUtil.ORA_HIDE_ROW_ID;
				break;
			case sqlserver:
				value = ModifyTabDataUtil.SQLSERVER_HIDE_ID_SHOW;
				value = oldRow.get(value);
				if (dbVersion != null && dbVersion.getBigVer() <= 9) {
					name = ModifyTabDataUtil.SQLSERVER_HIDE_LOCKRES_ID;
					value = "'" + value + "'";
				} else {
					name = ModifyTabDataUtil.SQLSERVER_HIDE_PHY_ID;
				}
				sql.append(name).append(" = ").append(value);
				return sql.toString();
			case db2:
				value = ModifyTabDataUtil.DB2_HIDE_ID_SHOW;
				name = ModifyTabDataUtil.DB2_HIDE_ID;
			default:
				break;
		}
		if (name == null) {
			return "";
		}
		sql.append(name).append(" ='").append(oldRow.get(value)).append("'");
		return sql.toString();
	}

	private String getSqlServerPkWhereSql(Connection conn, String schemaName, String tableName, LinkedHashSet<String> pks, Map<String, String> oldRow) throws SQLException {
		StringBuilder sql = new StringBuilder();
		if (pks != null && pks.size() > 0) {
			SqlPk sqlPk = new SqlPk(conn, schemaName, tableName, new ArrayList<>(pks));
			for (String pk : pks) {
				List<String> columns = sqlPk.getBindColumNames(pk);
				for (String column : columns) {
					String pkName = ModifyTabDataUtil.getTabFullName(dbTypeEnum, "", column);
					sql.append(pkName).append(" ='").append(oldRow.get(column)).append("'").append(",");
				}
			}
			if (sql.length() > 0) {
				sql.deleteCharAt(sql.length() - 1);
			}
		}
		return sql.toString();

	}

	private String getMysqlWhereSql(QueryTool queryTool, Connection conn, String schemaName, String tableName, LinkedHashSet<String> pks, Map<String, String> rowValues) throws SQLException, IOException {
		StringBuilder sql = new StringBuilder();
		if (pks != null && pks.size() > 0) {
			MysqlPk mysqlPk = new MysqlPk(conn, schemaName, tableName, new ArrayList<>(pks));
			for (String pk : pks) {
				List<String> columns = mysqlPk.getBindColumNames(pk);
				sql.append(joinMysqlWhere(queryTool, columns, null, rowValues, table.getNullSymbol()));
			}
		} else {
			sql.append(joinMysqlWhere(queryTool, queryTool.getColNames(), queryTool.getSelTypes(), rowValues, table.getNullSymbol()));
		}

		return sql.toString();
	}

	/**
	 * 创建MySql 的where条件
	 *
	 * @param cols
	 * @param rowValues
	 * @param nullSymbol
	 * @return
	 */
	private String joinMysqlWhere(QueryTool queryTool, List<String> cols, List<Enum<?>> selTypes, Map<String, String> rowValues, String nullSymbol) throws IOException {
		StringBuilder where = new StringBuilder();
		for (int i = 0; i < cols.size(); i++) {
			String colName = cols.get(i);
			boolean isLob = false;
			if (selTypes != null) {
				//如果没有主键 判断是否为blob
				Enum<?> type = selTypes.get(i);
				isLob = ModifyTabDataUtil.isLob(dbTypeEnum, type);
			}
			String cellValue = rowValues.get(colName);
			if (where.length() > 0) {
				where.append(" AND ");
			}
			if (null == cellValue || nullSymbol.equals(cellValue)) {
				where.append(" `").append(colName).append("` ").append("IS ").append("NULL ");

			} else if (!isLob) {
				where.append(" `").append(colName).append("`='").append(cellValue).append("' ");
			} else {
				//blob处理
				if (StringUtils.isNotBlank(cellValue)) {
					JsonObject json = Json.parse(cellValue).asObject();
					JsonValue offset = json.get(ModifyTabTool.OFFSET);
					JsonValue len = json.get(ModifyTabTool.LEN);
					boolean isNUll = offset == null || len == null || StringUtils.isBlank(offset.asString()) || StringUtils.isBlank(len.asString());
					if (isNUll) {
						where.append(" `").append(colName).append("` ").append("IS ").append("NULL ");
					} else {
						try (InputStream is = queryTool.getBinary(offset.asString() + "_" + len.asString())) {
							byte[] bytes = IOUtils.toByteArray(is);
							String hexString = ModifyTabDataUtil.bytesToHexString(bytes);
							where.append(" `").append(colName);
							if (hexString != null) {
								where.append("`=Concat(0x").append(hexString.replaceAll(" ", "").toUpperCase());
							} else {
								where.append("`=Concat('')");
							}

							where.append(")");
						}
					}
				}
			}
		}
		if (selTypes != null) {
			where.append(" LIMIT 1 ");
		}
		return where.toString();
	}

	private void updateLob(Connection conn, String sql, List<AddLobBean> addLobBeans) throws SQLException {
		ByteArrayInputStream in = null;
		Reader reader = null;
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			int count = 0;
			if (addLobBeans != null && addLobBeans.size() > 0) {
				for (int i = 0; i < addLobBeans.size(); i++) {
					AddLobBean lobBean = addLobBeans.get(i);
					byte[] data = lobBean.getData();
					if (data == null || data.length <= 0) {
						continue;
					}
					if (lobBean.isBlob()) {
						in = new ByteArrayInputStream(lobBean.getData());
						ps.setBinaryStream(i + 1, in, in.available());
					} else {
						reader = new InputStreamReader(new ByteArrayInputStream(lobBean.getData()));
						ps.setCharacterStream(i + 1, reader);
					}
					count++;
				}

			}
			if (count > 0 || sql.startsWith("UPDATE")) {
				ps.execute();
			}
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		}
	}

	/**
	 * 保存大字段监听
	 */
	public class SaveLobListener extends LobViewListener {
		private QueryTool queryTool;
		private JdbcBean jdbc;
		private String schemaName;
		private String tableName;
		private String colName;
		private Enum<?> type;

		private Connection conn;
		private LinkedHashSet<String> pks;
		//		private LobJsonCol jsonCol;
		private File tmpAddDir;
		private JsonObject lobJson;

		public SaveLobListener(QueryTool queryTool, JdbcBean jdbc, LobJsonCol jsonCol, String schemaName, String tableName, String colName, Enum<?> type) {
			this.jdbc = jdbc;
			this.queryTool = queryTool;
			this.schemaName = schemaName;
			this.tableName = tableName;
			this.colName = colName;
			this.type = type;
//			this.jsonCol = jsonCol;
			File baseDir = queryTool.getBaseDir();
			tmpAddDir = new File(baseDir, "addLob" + File.separator + tableName);
			tmpAddDir.mkdirs();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			byte[] data = null;
			ByteArrayOutputStream os = null;
			try {
				os = new ByteArrayOutputStream();
				String code = viewer.getCode();
				boolean isGet = (code == null || EncodingDetect.getUsedCode().contains(code.trim()) || viewer.genType().equals(LobViewer.NULL));
				if (isGet) {
					viewer.getTextArea().getArea().getTextArea().write(new OutputStreamWriter(os));
					data = os.toByteArray();
				} else {
					data = viewer.getData();
				}

				HTabRowBean addBean = table.getSelectedRowBeans().get(0);
				boolean isAdd = addBean.getOldRow() == null;
				boolean isBlob = ModifyTabDataUtil.isBlob(dbTypeEnum, type, tableObjFun);
				int selectedColumn = table.getComp().getSelectedColumn();
				int selectedRow = table.getComp().getSelectedRow();
				if (isAdd) {
					//					JsonColEditor cellEditor = (JsonColEditor) ((JTable) table.getComp()).getColumnModel().getColumn(selectedColumn).getCellEditor();
					if (data != null && data.length > 0) {
						File tmpData = new File(tmpAddDir, selectedRow + "_" + selectedColumn + ".dat");
						if (tmpData.exists()) {
							FileUtils.deleteQuietly(tmpData);
						}
						tmpData.createNewFile();
						try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
							String length = "0";
							if (isBlob) {
								length = String.valueOf(LobUtil.writeStream2File(is, tmpData, 0));
							} else {
								Reader reader = new InputStreamReader(is);
								length = LobUtil.reader2File(reader, tmpData, 0).split("_")[1];
							}
							lobJson = ModifyTabDataUtil.getLobJson(type, 0 + "_" + length, tmpData.getAbsolutePath());
							AddLobBean addLobBean = new AddLobBean(selectedRow, selectedColumn, colName, isBlob, data);
							List<AddLobBean> addLobBeans = addLobNumMap.get(selectedRow);
							if (addLobBeans == null) {
								addLobBeans = new ArrayList<>();
							}
							if (addLobBeans.contains(addLobBean)) {
								addLobBeans.set(addLobBeans.indexOf(addLobBean), addLobBean);
							} else {
								addLobBeans.add(addLobBean);
							}
							addLobNumMap.put(selectedRow, addLobBeans);
						}
					}
				} else {
					conn = ConnUtil.getConn(jdbc);
					conn.setAutoCommit(false);
					if (dbTypeEnum == DBTypeEnum.mysql) {
						MysqlTable mysqlTable = new MysqlTable(conn, schemaName, tableName);
						pks = mysqlTable.getSubObjNames(TabObjEnum.PK);
					}
					saveLob(isBlob, selectedRow, selectedColumn, data);
					conn.commit();
				}

				callBack(isAdd);
			} catch (Exception e2) {
				try {
					if (conn != null) {
						conn.rollback();
					}
				} catch (SQLException e1) {
					e2 = e1;
				}
				e2.printStackTrace();
				PopPaneUtil.error(e2);
			} finally {
				ConnUtil.close(conn);
				IOUtils.closeQuietly(os);
			}
		}

		public void saveLob(boolean isBlob, int row, int column, byte[] data) throws Exception {
			HTabRowBean rowBean = table.getSelectedRowBeans().get(0);
			String sql;
			String tabFullName = ModifyTabDataUtil.getTabFullName(dbTypeEnum, schemaName, tableName);
			//boolean isOra = dbTypeEnum == DBTypeEnum.oracle;
			boolean isMsSqlText = dbTypeEnum == DBTypeEnum.sqlserver && (type.name().equalsIgnoreCase(SqlserverSelType.CLOB.name()));
			//更新
			String where = getWhere(rowBean);
			if (data != null && data.length > 0) {
				if (isMsSqlText) {
					String text = viewer.getTextArea().getArea().getTextArea().getText();
					//SqlServer NText乱码问题
					sql = String.format("UPDATE %s SET %s = N'%s' WHERE %s", tabFullName, getSetName(colName), text, where);
				} else {
					sql = String.format("UPDATE %s SET %s = ? WHERE %s", tabFullName, getSetName(colName), where);
				}
			} else {
				sql = String.format("UPDATE %s SET %s =NULL WHERE %s", tabFullName, getSetName(colName), where);
			}
			if (!isMsSqlText) {
				AddLobBean addLobBean = new AddLobBean(row, column, colName, isBlob, data);
				updateLob(conn, sql, Collections.singletonList(addLobBean));
			}
		}


		private String getSetName(String colName) {
			return ModifyTabDataUtil.getTabFullName(dbTypeEnum, null, colName);
		}

		private String getWhere(HTabRowBean rowBean) throws Exception {
			StringBuilder sql = new StringBuilder();
			if (dbTypeEnum == DBTypeEnum.mysql) {
				sql.append(getMysqlWhereSql(queryTool, conn, schemaName, tableName, pks, rowBean.getOldRow()));
			} else {
				sql.append(appendOtherWhereSql(rowBean.getOldRow()));
			}
			return sql.toString();
		}

		protected void callBack(boolean isAdd) throws Exception {
		}

		public JsonObject getLobJson() {
			return lobJson;
		}

		public void setLobJson(JsonObject lobJson) {
			this.lobJson = lobJson;
		}
	}

	/**
	 * 构建列名对应的数据类型map
	 *
	 * @param query
	 * @return
	 */
	private Map<String, String> getNameType(QueryTool query) {
		List<String> colNames = query.getColNames();
		List<Enum<?>> selTypes = query.getSelTypes();

		Map<String, String> nameType = new LinkedHashMap<>();
		if (colNames != null && selTypes != null && colNames.size() == selTypes.size()) {
			for (int i = 0; i < colNames.size(); i++) {
				nameType.put(colNames.get(i), selTypes.get(i).name());
			}
		}
		return nameType;
	}

	/**
	 * 查询表字段详细信息
	 *
	 * @param conn
	 * @param schemaName
	 * @param tableName
	 */
	public Map<String, String> initColumnTypeMap(Connection conn, String schemaName, String tableName) {
		try {
			return nameTypeMap = ModifyTabDataUtil.getNameTypeMap(dbTypeEnum, conn, schemaName, tableName);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return null;
	}

	public static class CleanListener extends LobViewListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				viewer.loadData(null);
			} catch (IOException ioException) {
				PopPaneUtil.error(ioException);
			}
		}
	}

	public HTable getTable() {
		return table;
	}

	public void setTable(HTable table) {
		this.table = table;
	}

	public DBTypeEnum getDbTypeEnum() {
		return dbTypeEnum;
	}

	public void setDbTypeEnum(DBTypeEnum dbTypeEnum) {
		this.dbTypeEnum = dbTypeEnum;
	}

	public Map<String, Enum<?>> getLobMap() {
		return lobMap;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public VersionBean getDbVersion() {
		return dbVersion;
	}

	public void setDbVersion(VersionBean dbVersion) {
		this.dbVersion = dbVersion;
	}
}
