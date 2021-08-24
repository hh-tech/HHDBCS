package com.hh.hhdb_admin.mgr.table.column;

import com.hh.frame.common.util.JsonUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.frame.swingui.view.tab.col.json.JsonColEditor;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.mgr.table.TableComp;
import com.hh.hhdb_admin.mgr.table.common.SelectColDialog;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author oyx
 * @date 2020-10-09  15:09:16
 */
public class SelectColumn extends JsonCol {
	private String title;
	protected HTable colTable;
	private int index = 0;
	public static final String COL_NAMES = "col_names";

	public SelectColumn(String name, String value, HTable hTab, String title, HTable colTable) {
		super(name, value);
		this.title = title;
		this.colTable = colTable;
		((JTable) hTab.getComp()).setCellEditor(new JsonColEditor(this));
	}

	/**
	 * 根据自己的业务要求进行覆盖
	 */
	@Override
	public JsonObject onClick(JsonObject json, int row, int column) {
		String colName = null;
		JsonObject res = new JsonObject();
		try {
			if (json != null) {
				colName = json.getString(__TEXT);
			}
			SelectColDialog selectColDialog = new SelectColDialog(getSelCol(colTable, getIndex(), row), getTitle(), colName);
			selectColDialog.setTips(getTips());
			selectColDialog.loadTable(colName);
			Set<String> selectCol = selectColDialog.getSelectCol();
			if (selectCol != null && selectCol.size() > 0) {
				res.add(JsonCol.__TEXT, StringUtils.join(selectCol, ","));
				res.add(COL_NAMES, JsonUtil.parseArray(selectCol.toArray()));
			}
		} catch (Exception e) {
			PopPaneUtil.error(e);
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * 获取选择的列名
	 */
	public Set<String> getSelCol(HTable table, int index, int row) throws SQLException {
		TableModel tableModel =  ((JTable) table.getComp()).getModel();
		int rowCount = tableModel.getRowCount();
		Set<String> colNames = new LinkedHashSet<>();
		if (rowCount > 0) {
			for (int i = 0; i < rowCount; i++) {
				String s = tableModel.getValueAt(i, index).toString();
				if (StringUtils.isNoneBlank(s)) {
					colNames.add(s);
				}
			}
		}
		return colNames;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	protected String getTips() {
		return TableComp.getLang("addColumnFirst");
	}
}
