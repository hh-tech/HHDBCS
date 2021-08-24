package com.hh.hhdb_admin.mgr.table.common;

import com.hh.frame.create_dbobj.table.CreateTableTool;
import com.hh.frame.create_dbobj.table.base.AbsTableObjFun;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.abs.AbsCol;
import com.hh.hhdb_admin.mgr.table.TableComp;
import com.hh.hhdb_admin.mgr.table.comp.ColumnPanel;
import com.hh.hhdb_admin.mgr.table.comp.TableNamePanel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author oyx
 * @date 2020-9-24  17:14:18
 */
public abstract class TableCreatePanel extends HPanel implements CreateTableSqlSyntax {
	public HTable table;
	public final AtomicInteger atomicId = new AtomicInteger();
	protected HButton addButton;
	protected HButton delButton;
	private TableNamePanel tableNamePanel;
	protected String[] columnNames;
	protected AbsHComp baseComp;
	protected LastPanel lastPanel;
	protected JScrollPane scrollPane;

	public TableCreatePanel() {
		this(new HDivLayout(GridSplitEnum.C12));
	}

	public TableCreatePanel(AbsHComp baseComp) {
		this.baseComp = baseComp;
	}

	public TableCreatePanel(HDivLayout hDivLayout) {
		super(hDivLayout);
	}


	public HButton createAddButton(String text) {
		if (addButton == null) {
			addButton = new HButton(text);
			addButton.setIcon(TableUtil.getIcon(TableUtil.add_col_icon));
		}
		return addButton;
	}

	protected class AddListener implements ActionListener {
		private final HTable table;
		private final TableCreatePanel createPanel;

		public AddListener(HTable table, TableCreatePanel createPanel) {
			this.table = table;
			this.createPanel = createPanel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JTable jTable = ((JTable) table.getComp());
			TableUtil.stopEditing(table);
			table.add(table.getRowCount(), createPanel.getData(1).get(0));
			jTable.setRowSelectionInterval(jTable.getRowCount() - 1, jTable.getRowCount() - 1);
			Point p = new Point();
			p.setLocation(0, jTable.getSelectedRow() * jTable.getRowHeight());
			callBack();
		}
	}

	protected void callBack() {

	}

	public HButton createDelButton(String text, HTable table) {
		if (delButton == null) {
			delButton = new HButton(text) {
				@Override
				protected void onClick() {
					TableUtil.stopEditing(table);
					int[] row = ((JTable) table.getComp()).getSelectedRows();
					if (row.length > 0) {
						int result = JOptionPane.showConfirmDialog(null, "确定删除所选行?", "提示", JOptionPane.YES_NO_OPTION);
						if (result == JOptionPane.YES_OPTION) {
							table.deleteSelectRow();
						}
					}
				}
			};
			delButton.setIcon(TableUtil.getIcon(TableUtil.del_col_icon));
		}
		return delButton;
	}

	public HButton[] getToolButtons() {
		return new HButton[]{addButton, delButton};
	}

	public List<Map<String, String>> getData(int num) {
		List<Map<String, String>> data = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			Map<String, String> map = new HashMap<>();
			map.put(ColumnPanel.TAB_ID, String.valueOf(atomicId.incrementAndGet()));
			data.add(map);
		}
		return data;
	}

	public TableNamePanel getTableNamePanel() {
		return tableNamePanel;
	}

	public void setTableNamePanel(TableNamePanel tableNamePanel) {
		this.tableNamePanel = tableNamePanel;
	}

	/**
	 * 添加一行
	 */
	public void addCol() {
//		table.setNullSymbol("");
		table.load(getData(1), 0);
	}


	/**
	 * @return
	 */
	public abstract void build(CreateTableTool createTableTool) throws Exception;

	/**
	 * 将字段封装成json格式
	 *
	 * @return
	 */
	protected JsonArray getRowColJson() {

		Map<String, Integer> rowIndex = getColIndex(table, columnNames);
		int rowCount = ((JTable) table.getComp()).getRowCount();
		JsonArray array = new JsonArray();
		for (int i = 0; i < rowCount; i++) {
			JsonObject columnJson = new JsonObject();
			for (String columnName : columnNames) {
				Integer index = rowIndex.get(columnName);
				if (index != null && index > -1) {
					colJsonAdd(i, columnJson, columnName, index);
				}
			}
			array.add(columnJson);
		}
		return array;
	}

	protected Map<String, Integer> getColIndex(HTable table, String[] columnNames) {
		List<AbsCol> columns = table.getColumns();
		Map<String, Integer> map = new LinkedHashMap<>();
		for (AbsCol absCol : columns) {
			for (String columnName : columnNames) {
				if (table.getColumn(columnName) == absCol) {
					map.put(columnName, absCol.getColIndex());
				}
			}
		}
		return map;
	}


	protected List<String> getColNames(JsonObject row, String name) {
		List<String> cols = new ArrayList<>();
		JsonValue names = row.get(name);
		if (names != null && StringUtils.isNoneBlank(names.toString())) {
			JsonArray jsonColNames = names.asArray();
			if (jsonColNames != null && jsonColNames.size() > 0) {
				for (JsonValue colName : jsonColNames) {
					cols.add(colName.asString());
				}
			}

		}
		return cols;
	}

	public AbsTableObjFun getTableObjFun() {
		return TableComp.getCreateTabTool().getTableObjFun();
	}

	protected void colJsonAdd(int i, JsonObject columnJson, String columnName, Integer index) {

	}

	public HTable getTable() {
		return table;
	}

	public AbsHComp getBaseComp() {
		return baseComp;
	}

	public void setBaseComp(AbsHComp baseComp) {
		this.baseComp = baseComp;
	}


	protected void initBar() {

		lastPanel = new LastPanel(false);
		scrollPane = new JScrollPane(table.getComp());
		HBarPanel hBarPanel = new HBarPanel(TableUtil.newBarPan());
		HButton[] toolButtons = getToolButtons();
		for (HButton toolButton : toolButtons) {
			hBarPanel.add(toolButton);
		}
		lastPanel.setHead(hBarPanel.getComp());
		lastPanel.set(scrollPane);
		setLastPanel(lastPanel);
	}
}
