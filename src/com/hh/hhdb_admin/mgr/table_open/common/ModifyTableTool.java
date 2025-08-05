package com.hh.hhdb_admin.mgr.table_open.common;

import com.hh.frame.common.util.db.SelectTableSqlUtil;
import com.hh.frame.create_dbobj.table.base.AbsTableObjFun;
import com.hh.frame.create_dbobj.table.comm.CreateTableUtil;
import com.hh.frame.dbobj3.edit.EditTableSqlTool;
import com.hh.frame.dbobj3.edit.EditTableUtil;
import com.hh.frame.dbquery.QueryTool;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonValue;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.abs.AbsCol;
import com.hh.frame.swingui.view.tab.col.bigtext.BigTextCol;
import com.hh.hhdb_admin.mgr.table_open.ModifyTabDataComp;
import com.hh.hhdb_admin.mgr.table_open.comp.LobJsonCol;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.*;

/**
 * @author ouyangxu
 * @date 2022-01-04 0004 11:28:28
 */
@Deprecated
public class ModifyTableTool extends EditTableSqlTool {
	protected boolean readOnly = false;

	public ModifyTableTool(String nullSymbol, QueryTool queryTool) {
		super(nullSymbol, queryTool);
	}

	public ModifyTableTool(String schemaName, String tableName, String nullSymbol, QueryTool queryTool) {
		super(schemaName, tableName, nullSymbol, queryTool);
	}


	/**
	 * 创建表格列
	 *
	 * @param table
	 * @return
	 */
	public List<AbsCol> createCol(HTable table) {
		List<AbsCol> cols = new ArrayList<>();
		List<String> colNames = queryTool.getColNames();
		List<Enum<?>> selTypes = queryTool.getSelTypes();
		Map<String, Enum<?>> nameTypeMap = EditTableUtil.getNameType(queryTool);
		if (colNames == null || selTypes == null) {
			return cols;
		}
		AbsTableObjFun tableObjFun = CreateTableUtil.getDateType(dbtype,ver);
		Map<String, String> rowMap = new LinkedHashMap<>(16);
		for (int i = 0; i < colNames.size(); i++) {
			String colName = colNames.get(i);
			AbsCol col;
			//导出到csv的类型
			Enum<?> type = selTypes.get(i);
			//数据库中查询到的类型
			Enum<?> typeEnum = null;
			if (!readOnly) {
				typeEnum = nameTypeMap.get(colName);
			}
			if (SelectTableSqlUtil.isLob(this.dbtype, ver,type) && !SelectTableSqlUtil.getHideColNames().contains(colName)) {
				//lobMap.put(colNames.get(i), selTypes.get(i));
				col = new LobJsonCol(this.dbtype, ver,rowMap.containsKey(colName) ? colName + i : colName, colName, type);
				((LobJsonCol) col).setReadOnly(isReadOnly());
				((LobJsonCol) col).setCleanListener(new LobViewListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							viewer.loadData(null);
						} catch (IOException ioException) {
							PopDlgUtil.error(ioException);
						}
					}
				});
				SaveLobListener lobListener = new SaveLobListener(queryTool, ((LobJsonCol) col), tableName, type) {
					@Override
					protected void callBack() {
						((LobJsonCol) col).dispose();
					}
				};
				lobListener.setTable(table);
				((LobJsonCol) col).setSaveListener(lobListener);
			} else if (typeEnum != null && tableObjFun!=null &&tableObjFun.getBigTextColType().contains(typeEnum.name().toUpperCase())) {
				col = new BigTextCol(rowMap.containsKey(colName) ? colName + i : colName, colName);
			} else {
				col = new DataCol(rowMap.containsKey(colName) ? colName + i : colName, colName,true);
			}
			if (typeEnum != null) {
				col.setCellEditable(SelectTableSqlUtil.isCellEditable(this.dbtype, typeEnum.name()));
			}

			cols.add(col);
			rowMap.put(colName, colName);
		}
		return cols;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public static void requestFocus(HTable tab, int column, ModifyTabDataComp.ColListSelectionListener selectionListener) {
		JTable jTable = tab.getComp();
		if (selectionListener == null) {
			selectionListener = new ModifyTabDataComp.ColListSelectionListener(jTable);
		}
		jTable.getColumnModel().getSelectionModel().addListSelectionListener(selectionListener);
		int n = jTable.getRowCount() - 1;
		jTable.setRowSelectionInterval(n, n);
		jTable.changeSelection(n, column, false, false);
//		jTable.editCellAt(n, column);
		Component component = jTable.getEditorComponent();
		if (component instanceof JTextField) {
			component.requestFocus();
			((JTextField) component).selectAll();
		}
	}

	public static boolean isLobChange(List<HTabRowBean> changedValue, boolean change) {
		int lobCount = 0;
		int changeCount = 0;
		if (change) {
			for (HTabRowBean hTabRowBean : changedValue) {
				Map<String, String> currRow = hTabRowBean.getCurrRow();
				Set<String> keySet = currRow.keySet();
				changeCount += keySet.size();
				for (String key : keySet) {
					String text = currRow.get(key);
					String oldText = hTabRowBean.getOldRow().get(key);
					boolean isLob = text != null && text.contains("\"__TEXT\":") && text.contains("\"file_path\":") &&
							oldText != null && oldText.contains("\"__TEXT\":");
					if (!isLob) {
						continue;
					}
					JsonValue newJsonValue = Json.parse(text);
					JsonValue oldJsonValue = Json.parse(oldText);
					boolean equals = newJsonValue.toPrettyString().equals(oldJsonValue.toPrettyString());
					if (equals) {
						lobCount++;
					}
				}
			}
			if (lobCount > 0 && changeCount == lobCount) {
				change = false;
			}
		}
		return change;
	}
	
	/**
	 * 获取剪切板内容
	 * @return
	 */
	public static String getStringFromClipboard() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		try {
			return (String) clipboard.getData(DataFlavor.stringFlavor);
		} catch (Exception ex) {
			return null;
		}
	}
}
