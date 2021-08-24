package com.hh.hhdb_admin.mgr.table_open.ui;

import java.util.Map;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.swingui.view.tab.HTable;

/**
 * @author ouyangxu
 * @date 2021-01-13 0013 15:49:35
 */
public class AddTable extends HTable {
//	private final DBTypeEnum dbTypeEnum;
//	private Map<String, String> colTypeNameMap;
//	private AbsTableObjFun tableObjFun;

	public AddTable(DBTypeEnum dbTypeEnum, Map<String, String> colTypeNameMap) {
//		this.dbTypeEnum = dbTypeEnum;
//		this.colTypeNameMap = colTypeNameMap;
//		this.tableObjFun = CreateTableUtil.getDateType(dbTypeEnum);
//		jTab = new AddJTable();
//		((AddJTable) jTab).setTable(this);
//		jTab.setFillsViewportHeight(true);
//		jTab.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//		comp = jTab;
	}

//	private class AddJTable extends HTipTable.TipJTable {
//		private static final long serialVersionUID = 554627810626602045L;
//		private HTable hTable;
//
//		@Override
//		public TableCellEditor getCellEditor() {
//			return super.getCellEditor();
//		}
//
//		@Override
//		public TableCellRenderer getCellRenderer(int row, int column) {
//			TableColumn tableColumn = getColumnModel().getColumn(column);
//			TableCellRenderer renderer = tableColumn.getCellRenderer();
//			if (renderer == null) {
//				renderer = getDefaultRenderer(getColumnClass(column));
//			}
//
//			if (column == 2) {
//				Object valueAt = getValueAt(row, column - 1);
//				String columnName = valueAt.toString();
//				String typeName = colTypeNameMap.get(columnName);
//				if (columnName != null && typeName != null) {
//					Enum<?> typeToEnum = tableObjFun.typeToEnum(typeName);
//					boolean isLob = ModifyTabDataUtil.isLob(dbTypeEnum, typeToEnum);
//					if (isLob) {
//						LobJsonCol lobJsonCol = new LobJsonCol(dbTypeEnum, null, null, typeToEnum);
//						lobJsonCol.setTab(hTable);
//						renderer = new JsonColRender(lobJsonCol);
//					}
//				}
//			}
//
//
//			return renderer;
//		}
//
//		public void setTable(HTable table) {
//			this.hTable = table;
//		}
//	}
}
