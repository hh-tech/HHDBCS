package com.hhdb.csadmin.plugin.table_operate.handle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.hh.frame.swingui.swingcontrol.displayTable.TablePanelUtil;
import com.hh.frame.swingui.swingcontrol.displayTable.basis.BaseTable;
import com.hhdb.csadmin.common.util.HHSqlUtil.ITEM_TYPE;
import com.hhdb.csadmin.plugin.cmd.console.CommonsHelper;
import com.hhdb.csadmin.plugin.table_operate.TableEditPanel;
import com.hhdb.csadmin.plugin.table_operate.bean.TableIndexBean;
import com.hhdb.csadmin.plugin.table_operate.component.ComboBoxCellEditor;
import com.hhdb.csadmin.plugin.table_operate.component.CreateTableSQLSyntax;
import com.hhdb.csadmin.plugin.table_operate.component.TextCellEditor;
import com.hhdb.csadmin.plugin.table_operate.component.checkbox.CheckBoxCellEditor;
import com.hhdb.csadmin.plugin.table_operate.component.checkbox.CheckBoxRender;
import com.hhdb.csadmin.plugin.table_operate.component.listcombox.MulitCellEditor;

/**
 * 
 * @Description: 表格索引
 * @Copyright: Copyright (c) 2017年10月25日
 * @Company:H2 Technology
 * @author zhipeng.zhang
 * @version 1.0
 */
public class HandleIndexPanel extends JPanel implements TableModelListener, CreateTableSQLSyntax {
	private static final long serialVersionUID = 1L;
	private TableEditPanel tabp;
	
	private TablePanelUtil tablePanel;
	private ComboBoxCellEditor comboBoxcell;
	private String[] values;
	private BaseTable indexTable;
	private Map<String, TableIndexBean> map = new HashMap<String, TableIndexBean>();
	private List<String> dels = new ArrayList<String>();
	private JTextField zs;
	private int prerow = -1;
	private HandleTablePanel tabPanel;
	private static List<String> lists = new ArrayList<String>();
	static {
		lists.add("名");
		lists.add("字段");
		lists.add("索引方法");
		lists.add("唯一");
		lists.add("簇");
		lists.add("oid");
		lists.add("definition");
	}

	public HandleIndexPanel(TableEditPanel tableeditpanel,HandleTablePanel tablesPanel) throws Exception {
		this.tabp = tableeditpanel;
		this.tabPanel = tablesPanel;
		setBackground(Color.WHITE);
		tablePanel = new TablePanelUtil(true);
		tablePanel.setPreferredSize(new Dimension(600, 250));
		tablePanel.setBackground(Color.WHITE);
		tablePanel.getViewport().setBackground(Color.WHITE);
		tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		indexTable = tablePanel.getBaseTable();
		indexTable.setBackground(new Color(220, 255, 220));
		editIndex();
		setLayout(new GridBagLayout());
		zs = new JTextField();
		zs.setPreferredSize(new Dimension(300, 20));
		inputSetup(false);
		add(tablePanel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(new JLabel("注释："), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(zs, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(10, 10, 0, 0), 0, 0));
		JPanel jpl = new JPanel();
		jpl.setBackground(Color.WHITE);
		add(jpl, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		tablePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tablePanel.requestFocus();
			}
		});
		indexTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				rowClicked();
			}
		});
	}

	/**
	 * 初始化单元格控件
	 */
	public void initCellEditor() {
		TextCellEditor textcell = new TextCellEditor();
		MulitCellEditor mulitcell = new MulitCellEditor(values);
		comboBoxcell = new ComboBoxCellEditor();
		CheckBoxCellEditor checkboxcell = new CheckBoxCellEditor();
		CheckBoxRender checkboxRender = new CheckBoxRender();
		// 将索引类型加载到comboBox
		getIndexTypes();
		tablePanel.getTableDataModel().addTableModelListener(this);
		
		//设置单元格显示组件
		tablePanel.getBaseTable().getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(textcell));
		tablePanel.getBaseTable().getColumnModel().getColumn(1).setCellEditor(mulitcell);
		tablePanel.getBaseTable().getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBoxcell));
		tablePanel.getBaseTable().getColumnModel().getColumn(3).setCellEditor(checkboxcell);
		tablePanel.getBaseTable().getColumnModel().getColumn(3).setCellRenderer(checkboxRender);
		tablePanel.getBaseTable().getColumnModel().getColumn(4).setCellEditor(checkboxcell);
		tablePanel.getBaseTable().getColumnModel().getColumn(4).setCellRenderer(checkboxRender);
		
		setColumnWidth(new int[] { 0, 1 });
	}

	/**
	 * 设置表格列的宽度
	 * 
	 * @param column
	 * @param table
	 */
	private void setColumnWidth(int[] column) {
		for (int i = 0; i < indexTable.getColumnCount(); i++) {
			TableColumn firsetColumn = indexTable.getColumnModel().getColumn(i);
			firsetColumn.setPreferredWidth(60);
			firsetColumn.setMaxWidth(200);
			if (i == column[0] || i == column[1]) {
				firsetColumn.setPreferredWidth(230);
				firsetColumn.setMaxWidth(230);
			}
			firsetColumn.setMinWidth(30);
		}
		hideColumn(new int[] {4, 5, 6 });
	}
	/**
	 * 隐藏列
	 * @param cols
	 */
	private void hideColumn(int[] cols) {
		for (int c : cols) {
			TableColumn coln = indexTable.getColumnModel().getColumn(c);
			coln.setMinWidth(0);
			coln.setMaxWidth(0);
			coln.setWidth(0);
			coln.setPreferredWidth(0);
		}
	}

	/**
	 * 索引类型
	 */
	private void getIndexTypes() {
		String[] indexs = new String[] { "BTREE", "HASH", "GiST", "SP-GiST", "GIN" };
		for (String index : indexs) {
			comboBoxcell.addItem(index);
		}
	}

	/**
	 * 删除一行
	 */
	public void delRow() {
		int row = indexTable.getSelectedRow();
		int sum = indexTable.getRowCount();
		if (row != -1) {
			int result = JOptionPane.showConfirmDialog(null, "是否删除当前行", "提示信息", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				if (indexTable.getValueAt(indexTable.getSelectedRow(), 5) != null) {
					dels.add(indexTable.getValueAt(indexTable.getSelectedRow(), 0).toString());
				}
				//获取下一行的下面输入框的值填入，防止自动赋值时将已删除的赋予下行
				if(row+1<sum){   //不能是最后一行
					zs.setText((String) indexTable.getModel().getValueAt(row+1, 6));
				}else{
					zs.setText("");
				}
				tablePanel.getTableDataModel().removeRow(row);
			}
		}
	}

	/**
	 * 添加一行
	 */
	public void addRows() {
		Object[] object = new Object[] { null, "", null, false, false };
		tablePanel.getTableDataModel().addRow(object);
	}

	/**
	 * 当点击单元格时获取此表的所有列，用于选择列名弹出框使用
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		int rows = tabPanel.getBaseTable().getRowCount();
		String columns = "";
		for (int i = 0; i < rows; i++) {
			if (tabPanel.getBaseTable().getValueAt(i, 0) != null) {
				columns += tabPanel.getBaseTable().getValueAt(i, 0).toString() + ",";
			}
		}
		if (columns.length() > 0) {
			columns = columns.substring(0, columns.length() - 1);
		}
		MulitCellEditor cell = (MulitCellEditor) indexTable.getCellEditor(0, 1);   //获取需要列名弹出框的列的编辑器
		cell.getCellEditor().setData(columns.split(","));
	}
	
	/**
	 * 取消表格编辑状态
	 */
	public void cancleEdit() {
		int row = indexTable.getSelectedRow();
		if (row != -1) {
			DefaultTableModel dtm = (DefaultTableModel) indexTable.getModel();
			dtm.setValueAt(zs.getText(), row, 6);
			if (indexTable.isEditing()) {
				indexTable.getCellEditor().stopCellEditing();
			}
		}
	}

	public void rowClicked() {
		inputSetup(true);
		tabp.getToolBar().getComponentAtIndex(0).setEnabled(tabp.controlButton = true);
		DefaultTableModel dtm = (DefaultTableModel) indexTable.getModel();
		if (prerow != -1 && (prerow + 1) <= indexTable.getRowCount()) {
			dtm.setValueAt(zs.getText(), prerow, 6);
		}
		int row = indexTable.getSelectedRow();
		if (row != -1) {
			String comment = (String) dtm.getValueAt(row, 6);
			zs.setText(comment);
			prerow = row;
		}
	}

	/**
	 * 编辑表格
	 * 
	 * @throws Exception
	 */
	public void editIndex() throws Exception {
		int rowcount = indexTable.getRowCount();
		for (int i = rowcount - 1; i >= 0; i--) {
			tablePanel.getTableDataModel().removeRow(i);
		}
		// 索引字段查询
		List<Map<String, Object>> lic = tabp.sqls.getListBySql(ITEM_TYPE.TABLE, "columnsinfo", new String[] { tabp.getTableoId() });
		values = new String[lic.size()];
		for (int i = 0; i < lic.size(); i++) {
			Map<String, Object> m = lic.get(i);
			values[i] = m.get("名称").toString();
		}
		List<Map<String, Object>> li = tabp.sqls.getListBySql(ITEM_TYPE.INDEXS, "prop", new String[] { tabp.getTableoId() });
		for (int i = 0; i < li.size(); i++) {
			TableIndexBean indexbean = new TableIndexBean();
			Map<String, Object> m = li.get(i);
			int relid = Integer.parseInt(m.get("indexrelid").toString());
			indexbean.setOid(m.get("oid").toString());
			indexbean.setAmname(m.get("索引方法").toString());
			if (m.get("簇").toString().equals("t") || m.get("簇").toString().equals("true")) {
				m.put("簇", true);
			} else {
				m.put("簇", false);
			}
			indexbean.setExclusion(Boolean.parseBoolean(m.get("簇").toString()));
			indexbean.setIndexrelid(relid);
			indexbean.setIndexName(m.get("名").toString());
			indexbean.setOwner(m.get("owner").toString());
			if (m.get("唯一").toString().equals("t") || m.get("唯一").toString().equals("true")) {
				m.put("唯一", true);
			} else {
				m.put("唯一", false);
			}
			indexbean.setUnique(Boolean.parseBoolean(m.get("唯一").toString()));
			String columns = "";
			if (m.get("indnatts") != null && !"".equals(m.get("indnatts").toString())) {
				for (int k = 1; k <= Integer.parseInt(m.get("indnatts").toString()); k++) {
					columns += tabp.sqls.getIndexColumn(relid, k) + ",";
				}
				m.put("字段", columns.substring(0, columns.trim().length() - 1));
				indexbean.setColumns(columns.substring(0, columns.trim().length() - 1));
			}
			if (m.get("definition") != null) {
				indexbean.setDefinition(m.get("definition").toString());
			} else {
				indexbean.setDefinition("");
			}
			map.put(indexbean.getOid(), indexbean);
		}
		tablePanel.setData(lists, li);
		initCellEditor();
	}

	/**
	 * 组装编辑索引sql
	 * 
	 * @return
	 */
	public String editIndexSql() {
		int rows = indexTable.getRowCount();
		StringBuffer sqlBuffer = new StringBuffer();
		StringBuffer comments = new StringBuffer();
		if (dels.size() > 0) {
			for (String str : dels) {
				sqlBuffer.append(NEW_LINE).append(DROP).append(INDEX).append("\""+tabp.getSchemaName()+"\"").append(DOT).append(str.trim());
				sqlBuffer.append(SEMI_COLON);
			}
		}
		for (int i = 0; i < rows; i++) {
			if (indexTable.getValueAt(i, 5) != null) {   //修改
				StringBuffer lsdef = new StringBuffer();
				TableIndexBean coln = map.get(indexTable.getValueAt(i, 5).toString());
				if (   coln.isUnique() != Boolean.parseBoolean(indexTable.getValueAt(i, 3).toString())
						|| !coln.getAmname().toUpperCase().equals(indexTable.getValueAt(i, 2).toString().toUpperCase())
						|| !coln.getColumns().equals(indexTable.getValueAt(i, 1).toString())
						|| !coln.getIndexName().trim().equals(indexTable.getValueAt(i, 0).toString())
				) {
					sqlBuffer.append(NEW_LINE).append(DROP).append(INDEX).append("\""+tabp.getSchemaName()+"\"").append(DOT).append(coln.getIndexName().trim()).append(SEMI_COLON);
					sqlBuffer.append(NEW_LINE).append(CREATE);
					if (Boolean.parseBoolean(indexTable.getValueAt(i, 3).toString())) {
						sqlBuffer.append(UNIQUE);
					}
					sqlBuffer.append(INDEX).append(indexTable.getValueAt(i, 0)).append(ON).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"");
					if (indexTable.getValueAt(i, 2) != null && !"".equals(indexTable.getValueAt(i, 2))) {
						sqlBuffer.append(USING);
					}
					if (indexTable.getValueAt(i, 2) != null && !"SP-GiST".equals(indexTable.getValueAt(i, 2))) {
						sqlBuffer.append(indexTable.getValueAt(i, 2).toString());
					}
					sqlBuffer.append(B_OPEN).append(indexTable.getValueAt(i, 1)).append(B_CLOSE).append(SEMI_COLON);
					sqlBuffer.append(NEW_LINE).append(ALTER_TABLE).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(CLUSTER).append(ON)
							.append(indexTable.getValueAt(i, 0)).append(SEMI_COLON);
					lsdef.append(NEW_LINE).append(" COMMENT ON INDEX ").append("\""+tabp.getSchemaName()+"\"").append(DOT)
							.append(indexTable.getValueAt(i, 0)).append(" IS ").append("'" + indexTable.getValueAt(i, 6) + "'").append(SEMI_COLON);
				}
				if (!coln.getDefinition().equals(CommonsHelper.nullOfStr(indexTable.getValueAt(i, 6))) && lsdef.length() == 0) {
					comments.append(NEW_LINE).append(" COMMENT ON INDEX ").append("\""+tabp.getSchemaName()+"\"").append(DOT).append(indexTable.getValueAt(i, 0)).append(" IS ").append("'" + indexTable.getValueAt(i, 6) + "'").append(SEMI_COLON);
				} else {
					comments.append(lsdef);
				}
			} else {    //添加索引
				if (indexTable.getValueAt(i, 0) != null || "".equals(indexTable.getValueAt(i, 0))) {
					sqlBuffer.append(NEW_LINE).append(CREATE);
					if (Boolean.parseBoolean(indexTable.getValueAt(i, 3).toString())) {
						sqlBuffer.append(UNIQUE);
					}
					sqlBuffer.append(INDEX).append(indexTable.getValueAt(i, 0)).append(ON).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"");
					if (indexTable.getValueAt(i, 2) != null && !"".equals(indexTable.getValueAt(i, 2))) {
						sqlBuffer.append(USING);
					}
					if (indexTable.getValueAt(i, 2) != null && !"SP-GiST".equals(indexTable.getValueAt(i, 2))) {
						sqlBuffer.append(indexTable.getValueAt(i, 2).toString());
					}
					sqlBuffer.append(B_OPEN).append(indexTable.getValueAt(i, 1)).append(B_CLOSE).append(SEMI_COLON);
					if (Boolean.parseBoolean(indexTable.getValueAt(i, 4).toString())) {
						sqlBuffer.append(NEW_LINE).append(ALTER_TABLE).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(CLUSTER).append(ON).append(indexTable.getValueAt(i, 0)).append(SEMI_COLON);
					}
					if (indexTable.getValueAt(i, 6) != null) {
						comments.append(NEW_LINE).append(" COMMENT ON INDEX ").append("\""+tabp.getSchemaName()+"\"").append(DOT).append(indexTable.getValueAt(i, 0)).append(" IS ").append("'" + indexTable.getValueAt(i, 6) + "'").append(SEMI_COLON);
					}
				}
			}
		}
		sqlBuffer.append(comments);
		if(sqlBuffer.length()>0){
			return sqlBuffer.append(NEW_LINE).toString();
		}else{
			sqlBuffer.setLength(0);
			return sqlBuffer.toString();
		}
	}
	
	/**
	 * 输入框设置
	 * @param bool
	 */
	public void inputSetup(Boolean bool){
		if(bool){
			zs.setBackground(new Color(220, 255, 220));
			zs.setEditable(true);
		}else{
			zs.setEditable(false);
		}
	}

	public BaseTable getBaseTable() {
		return indexTable;
	}

	public BaseTable getIndexTable() {
		return indexTable;
	}

	public void setIndexTable(BaseTable indexTable) {
		this.indexTable = indexTable;
	}

	public JTextField getZs() {
		return zs;
	}

	public void setZs(JTextField zs) {
		this.zs = zs;
	}
}
