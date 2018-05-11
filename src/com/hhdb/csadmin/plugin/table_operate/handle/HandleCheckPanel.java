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
import com.hhdb.csadmin.plugin.table_operate.bean.TableCheckBean;
import com.hhdb.csadmin.plugin.table_operate.component.CreateTableSQLSyntax;
import com.hhdb.csadmin.plugin.table_operate.component.TextCellEditor;

/**
 * 
 * @Description: 表格约束
 * @Copyright: Copyright (c) 2017年10月25日
 * @Company:H2 Technology
 * @author zhipeng.zhang
 * @version 1.0
 */
public class HandleCheckPanel extends JPanel implements TableModelListener, CreateTableSQLSyntax {
	private static final long serialVersionUID = 1L;
	private TableEditPanel tabp;
	
	private TablePanelUtil tablePanel;
	private BaseTable baseTable;
	private List<String> dels = new ArrayList<String>();
	private Map<String, TableCheckBean> map = new HashMap<String, TableCheckBean>();
	private JTextField zs;
	private int prerow = -1;

	private static List<String> lists = new ArrayList<String>();
	static {
		lists.add("名");
		lists.add("检查条件");
		lists.add("oid");
		lists.add("comment");
	}

	public HandleCheckPanel(TableEditPanel tableeditpanel) throws Exception {
		this.tabp = tableeditpanel;
		setBackground(Color.WHITE);
		tablePanel = new TablePanelUtil(true);
		tablePanel.setPreferredSize(new Dimension(360, 210));
		tablePanel.setBackground(Color.WHITE);
		tablePanel.getViewport().setBackground(Color.WHITE);
		tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		baseTable = tablePanel.getBaseTable();
		baseTable.setBackground(new Color(220, 255, 220));
		editCheck();
		setLayout(new GridBagLayout());
		zs = new JTextField();
		zs.setPreferredSize(new Dimension(300, 20));
		inputSetup(false);
		add(tablePanel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(new JLabel("注释："), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(50, 10, 0, 0), 0, 0));
		add(zs, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(50, 10, 0, 0), 0, 0));
		JPanel jpl = new JPanel();
		jpl.setBackground(Color.WHITE);
		add(jpl, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		
		baseTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				rowClicked();
			}
		});
		tablePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tablePanel.requestFocus();
			}
		});
	}

	/**
	 * 编辑表格
	 * 
	 * @throws Exception
	 */
	public void editCheck() throws Exception {
		int rowcount = baseTable.getRowCount();
		for (int i = rowcount - 1; i >= 0; i--) {
			tablePanel.getTableDataModel().removeRow(i);
		}
		List<Map<String, Object>> li = tabp.sqls.getListBySql(ITEM_TYPE.CHECK, "prop_coll", new String[] { tabp.getTableoId() });
		for (int i = 0; i < li.size(); i++) {
			TableCheckBean bean = new TableCheckBean();
			Map<String, Object> m = li.get(i);
			int relid = Integer.parseInt(m.get("conindid").toString());
			bean.setOid(m.get("oid").toString());
			bean.setConindid(relid);
			bean.setUnqiueName(m.get("名").toString());
			m.put("检查条件", m.get("consrc").toString());
			bean.setColumns(m.get("consrc").toString());
			if (m.get("comment") != null) {
				bean.setComment(m.get("comment").toString());
			} else {
				bean.setComment("");
			}
			map.put(bean.getOid(), bean);
		}
		tablePanel.setData(lists, li);
		initCellEditor();
	}

	/**
	 * 初始化单元格控件
	 */
	public void initCellEditor() {
		TextCellEditor textcell = new TextCellEditor();
		tablePanel.getTableDataModel().addTableModelListener(this);
		tablePanel.getBaseTable().getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(textcell));
		tablePanel.getBaseTable().getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(textcell));
		setColumnWidth(new int[] { 1 });
	}

	/**
	 * 设置表格列的宽度
	 * 
	 * @param column
	 * @param table
	 */
	private void setColumnWidth(int[] column) {
		for (int i = 0; i < baseTable.getColumnCount(); i++) {
			TableColumn firsetColumn = baseTable.getColumnModel().getColumn(i);
			firsetColumn.setPreferredWidth(60);
			firsetColumn.setMaxWidth(200);
			if (i == column[0]) {
				firsetColumn.setPreferredWidth(300);
				firsetColumn.setMaxWidth(300);
			}
			firsetColumn.setMinWidth(30);
		}
		hideColumn(new int[] { 2, 3 });
	}

	private void hideColumn(int[] cols) {
		for (int c : cols) {
			TableColumn coln = baseTable.getColumnModel().getColumn(c);
			coln.setMinWidth(0);
			coln.setMaxWidth(0);
			coln.setWidth(0);
			coln.setPreferredWidth(0);
		}
	}

	/**
	 * 删除一行
	 */
	public void delRow() {
		int row = baseTable.getSelectedRow();
		int sum = baseTable.getRowCount();
		if (row != -1) {
			int result = JOptionPane.showConfirmDialog(null, "是否删除当前行", "提示信息", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				if (baseTable.getValueAt(baseTable.getSelectedRow(), 2) != null) {
					dels.add(baseTable.getValueAt(baseTable.getSelectedRow(), 0).toString());
				}
				//获取下一行的下面输入框的值填入，防止自动赋值时将已删除的赋予下行
				if(row+1<sum){   //不能是最后一行
					zs.setText((String) baseTable.getModel().getValueAt(row+1, 3));
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
		Object[] object = new Object[] { null, null };
		tablePanel.getTableDataModel().addRow(object);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
	}
	/**
	 * 取消表格编辑状态
	 */
	public void cancleEdit() {
		int row = baseTable.getSelectedRow();
		if (row != -1) {
			DefaultTableModel dtm = (DefaultTableModel) baseTable.getModel();
			dtm.setValueAt(zs.getText(), row, 3);
			if (baseTable.isEditing()) {
				baseTable.getCellEditor().stopCellEditing();
			}
		}
	}

	public void rowClicked() {
		tabp.getToolBar().getComponentAtIndex(0).setEnabled(tabp.controlButton = true);
		inputSetup(true);
		DefaultTableModel dtm = (DefaultTableModel) baseTable.getModel();
		if (prerow != -1 && (prerow + 1) <= baseTable.getRowCount()) {
			dtm.setValueAt(zs.getText(), prerow, 3);
		}
		int row = baseTable.getSelectedRow();
		if (row != -1) {
			String comment = (String) dtm.getValueAt(row, 3);
			zs.setText(comment);
			prerow = row;
		}
	}

	/**
	 * 组装编辑sql
	 * 
	 * @return
	 */
	public String editCheckSql() {
		int rows = baseTable.getRowCount();
		StringBuffer sqlBuffer = new StringBuffer();
		StringBuffer cont = new StringBuffer();
		StringBuffer comments = new StringBuffer();
		if (dels.size() > 0) {
			for (String st : dels) {
				sqlBuffer.append(ALTER_TABLE).append("\""+tabp.getSchemaName()+"\"").append(DOT).append("\""+tabp.getTableName()+"\"").append(DROP_CONSTRAINT).append(st).append(SEMI_COLON);
			}
		}
		for (int i = 0; i < rows; i++) {
			StringBuffer lsdft = new StringBuffer();
			if (baseTable.getValueAt(i, 2) != null) {   //修改
				TableCheckBean bean = map.get(baseTable.getValueAt(i, 2).toString());
				if (!bean.getUnqiueName().equals(baseTable.getValueAt(i, 0).toString()) || !bean.getColumns().equals(baseTable.getValueAt(i, 1).toString())) {
					cont.append(NEW_LINE).append(ALTER_TABLE).append("\""+tabp.getSchemaName()+"\"").append(DOT).append("\""+tabp.getTableName()+"\"");
					cont.append(DROP_CONSTRAINT).append(bean.getUnqiueName()).append(COMMA);
					cont.append(ADD_CONSTRAINT).append(baseTable.getValueAt(i, 0)).append(CHECK).append(baseTable.getValueAt(i, 1)).append(SEMI_COLON);
					lsdft.append(NEW_LINE).append(" COMMENT ON CONSTRAINT ").append(baseTable.getValueAt(i, 0))
							.append(" ON ").append("\""+tabp.getSchemaName()+"\"").append(DOT).append("\""+tabp.getTableName()+"\"").append(" IS ")
							.append("'" + baseTable.getValueAt(i, 3) + "'").append(SEMI_COLON);
				}
				if ( !bean.getComment().equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 3))) && lsdft.length() == 0 ) {
					comments.append(NEW_LINE).append(" COMMENT ON CONSTRAINT ").append(baseTable.getValueAt(i, 0))
							.append(" ON ").append("\""+tabp.getSchemaName()+"\"").append(DOT).append("\""+tabp.getTableName()+"\"").append(" IS ")
							.append("'" + baseTable.getValueAt(i, 3) + "'").append(SEMI_COLON);
				}
				comments.append(lsdft);
			} else {   //添加
				if (baseTable.getValueAt(i, 0) != null || "".equals(baseTable.getValueAt(i, 0))) {
					cont.append(NEW_LINE).append(ALTER_TABLE).append("\""+tabp.getSchemaName()+"\"").append(DOT).append("\""+tabp.getTableName()+"\"");
					cont.append(ADD).append(CONSTRAINT).append(baseTable.getValueAt(i, 0)).append(CHECK).append(baseTable.getValueAt(i, 1)).append(SEMI_COLON);
					if (baseTable.getValueAt(i, 3) != null) {
						comments.append(NEW_LINE).append(" COMMENT ON CONSTRAINT ").append(baseTable.getValueAt(i, 0))
								.append(" ON ").append("\""+tabp.getSchemaName()+"\"").append(DOT).append("\""+tabp.getTableName()+"\"").append(" IS ")
								.append("'" + baseTable.getValueAt(i, 3) + "'").append(SEMI_COLON);
					}
				}
			}
		}
		sqlBuffer.append(cont.toString());
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
		return baseTable;
	}

	public JTextField getZs() {
		return zs;
	}

	public void setZs(JTextField zs) {
		this.zs = zs;
	}

	public void setBaseTable(BaseTable baseTable) {
		this.baseTable = baseTable;
	}

}
