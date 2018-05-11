package com.hhdb.csadmin.plugin.monitor.ui;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

public class MonitorTablePanel extends JScrollPane{
	private static final long serialVersionUID = 1L;
	private MonitorTable table;
	private MonitorTableDataModel tableModel;
	private String tableId;//表格编码
	
	
	public String getTableId() {
		return tableId;
	}


	public void setTableId(String tableId) {
		this.tableId = tableId;
	}


	/**
	 * 
	 * @param editable
	 *            是否编辑
	 * @param tableId
	 *            表格编码
	 */
	public MonitorTablePanel(boolean editable,String tableId) {
		this.tableId=tableId;
		addTableModel(editable, null);
	}

	
	/**
	 * 
	 * @param editable
	 *            是否编辑
	 */
	public MonitorTablePanel(boolean editable) {
		addTableModel(editable, null);
	}

	private void addTableModel(boolean editable, int[] cloumns) {
		setBorder(BorderFactory.createLineBorder(new Color(187, 187,187),1));
		table = new MonitorTable();
		tableModel = new MonitorTableDataModel(editable, cloumns);
		table.setModel(tableModel);
		getViewport().add(table);
	}

	/**
	 * 表头和数据一起添加,格式已组装好
	 * 
	 * @param dataVector
	 *            数据
	 * @param columnIdentifiers
	 *            表头
	 */
	public void setDataList(Vector<Object> columnIdentifiers, Vector<Object> dataVector) {
		tableModel.setDataVector(dataVector, columnIdentifiers);
	}

	/**
	 * 表头和数据一起添加,无格式时
	 * 
	 * @param header
	 * @param data
	 */
	public void setDataList(List<String> header, List<Map<String, Object>> data) {
		Vector<Object> columnIdentifiers = initVector(header);
		Vector<Object> dataVector = initVector(data, header);
		tableModel.setDataVector(dataVector, columnIdentifiers);
	}
	
	/**
	 * 表头和数据一起添加,无格式时
	 * 
	 * @param header
	 * @param data
	 */
	public void setTableDataList(List<String> header, List<List<String>> data) {
		Vector<Object> columnIdentifiers = initVector(header);
		Vector<Object> dataVector = initRowVector(data, header);
		tableModel.setDataVector(dataVector, columnIdentifiers);
	}
	
	/**
	 * 表头转换
	 * 
	 * @param cloumns
	 * @return
	 */
	private Vector<Object> initVector(List<String> cloumns) {
		Vector<Object> colNames = new Vector<Object>();
		for (String key : cloumns) {
			colNames.add(key);
		}
		return colNames;
	}

	/**
	 * 数据转换
	 * 
	 * @param datas
	 * @param cloumns
	 * @return
	 */
	private Vector<Object> initVector(List<Map<String, Object>> datas, List<String> cloumns) {
		Vector<Object> colNames = new Vector<Object>();
		if(datas!=null)
		{
			for (Map<String, Object> map : datas) {
				Vector<Object> item = new Vector<Object>();
				for (String key : cloumns) {
					item.add(map.get(key));
				}
				colNames.add(item);
			}
		}
		return colNames;
	}
	
	/**
	 * 数据转换
	 * 
	 * @param datas
	 * @param cloumns
	 * @return
	 */
	private Vector<Object> initRowVector(List<List<String>> rows, List<String> cloumns) {
		Vector<Object> colNames = new Vector<Object>();
		if(rows!=null)
		{
			for (List<String> row : rows) {
				Vector<Object> item = new Vector<Object>();
				for (String value : row) {
					item.add(value);
				}
				colNames.add(item);
			}
		}
		return colNames;
	}

	public MonitorTable getBaseTable() {
		return table;
	}

	public MonitorTableDataModel getTableDataModel() {
		return tableModel;
	}
}
