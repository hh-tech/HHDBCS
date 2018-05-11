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
import com.hhdb.csadmin.plugin.table_operate.bean.TableForeignBean;
import com.hhdb.csadmin.plugin.table_operate.component.ComboBoxCellEditor;
import com.hhdb.csadmin.plugin.table_operate.component.CreateTableSQLSyntax;
import com.hhdb.csadmin.plugin.table_operate.component.TextCellEditor;
import com.hhdb.csadmin.plugin.table_operate.component.listcombox.MulitCellEditor;

/**
 * 
 * @Description: 表格外键
 * @Copyright: Copyright (c) 2017年10月25日
 * @Company:H2 Technology
 * @author zhipeng.zhang
 * @version 1.0
 */
public class HandleForeignPanel extends JPanel implements TableModelListener, CreateTableSQLSyntax {
	private static final long serialVersionUID = 1L;
	private TableEditPanel tabp;
	
	private TablePanelUtil tablePanel;
	private HandleTablePanel tabPanel;
	private ComboBoxCellEditor ftable;
	private ComboBoxCellEditor deltable;
	private ComboBoxCellEditor updtable;
	private String[] values;
	private BaseTable baseTable;
	private Map<String, TableForeignBean> map = new HashMap<String, TableForeignBean>();
	private static Map<String, String> mtype = new HashMap<String, String>();
	private List<String> dels = new ArrayList<String>();
	private JTextField zs;
	private int prerow = -1;
	private static List<String> lists = new ArrayList<String>();
	static {
		lists.add("名");
		lists.add("栏位");
		lists.add("外键表");
		lists.add("外键表栏位");
		lists.add("删除时");
		lists.add("更新时");
		lists.add("oid");
		lists.add("comment");
		mtype.put("n", "SET NULL");
		mtype.put("d", "SET DEFAULT");
		mtype.put("r", "RESTRICT");
		mtype.put("a", "NO ACTION");
		mtype.put("c", "CASCADE");
	}

	public HandleForeignPanel(TableEditPanel tableeditpanel,HandleTablePanel tabsPanel) throws Exception {
		this.tabp = tableeditpanel;
		this.tabPanel = tabsPanel;
		setBackground(Color.WHITE);
		tablePanel = new TablePanelUtil(true);
		tablePanel.setPreferredSize(new Dimension(680, 210));
		tablePanel.setBackground(Color.WHITE);
		tablePanel.getViewport().setBackground(Color.WHITE);
		tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		baseTable = tablePanel.getBaseTable();
		baseTable.setBackground(new Color(220, 255, 220));
		editForeign();
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
		tablePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tablePanel.requestFocus();
			}
		});
		baseTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				rowClicked();
			}
		});
	}

	/**
	 * 初始化单元格控件
	 * 
	 * @throws Exception
	 */
	public void initCellEditor() throws Exception {
		TextCellEditor textcell = new TextCellEditor();
		MulitCellEditor mulitcell = new MulitCellEditor(values);
		MulitCellEditor fcell = new MulitCellEditor(values);
		ftable = new ComboBoxCellEditor();
		deltable = new ComboBoxCellEditor();
		updtable = new ComboBoxCellEditor();
		setComboBoxDefault();
		tablePanel.getTableDataModel().addTableModelListener(this);
		
		tablePanel.getBaseTable().getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(textcell));
		tablePanel.getBaseTable().getColumnModel().getColumn(1).setCellEditor(mulitcell);
		tablePanel.getBaseTable().getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(ftable));
		tablePanel.getBaseTable().getColumnModel().getColumn(3).setCellEditor(fcell);
		tablePanel.getBaseTable().getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(deltable));
		tablePanel.getBaseTable().getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(updtable));
		
		setColumnWidth(new int[] { 1, 3 });
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
			firsetColumn.setPreferredWidth(80);
			firsetColumn.setMaxWidth(200);
			if (i == column[0] || i == column[1]) {
				firsetColumn.setPreferredWidth(230);
				firsetColumn.setMaxWidth(230);
			}
			firsetColumn.setMinWidth(80);
		}
		hideColumn(new int[] { 6, 7 });
	}
	
	/**
	 * 隐藏列
	 * @param cols
	 */
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
	 * combox赋值 将索引类型加载到comboBox
	 * 
	 * @throws Exception
	 */
	private void setComboBoxDefault() throws Exception {
		List<Map<String, Object>> list = tabp.sqls.getListBySql(ITEM_TYPE.TABLE, "prop_coll", new String[] { "'"+tabp.getSchemaName()+"'" });
		for (int i = 0; i < list.size(); i++) {
			ftable.addItem(list.get(i).get("name").toString());
		}
		String[] combox = new String[] { "", "RESTRICT", "NO ACTION", "CASCADE", "SET NULL", "SET DEFAULT" };
		for (String str : combox) {
			deltable.addItem(str);
			updtable.addItem(str);
		}
	}

	/**
	 * 添加一行
	 */
	public void addRows() {
		Object[] object = new Object[] { null, null, null, null, null, null };
		tablePanel.getTableDataModel().addRow(object);
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
				if (baseTable.getValueAt(baseTable.getSelectedRow(), 6) != null) {
					dels.add(baseTable.getValueAt(baseTable.getSelectedRow(), 0).toString());
				}
				//获取下一行的下面输入框的值填入，防止自动赋值时将已删除的赋予下行
				if(row+1<sum){   //不能是最后一行
					zs.setText((String) baseTable.getModel().getValueAt(row+1, 7));
				}else{
					zs.setText("");
				}
				tablePanel.getTableDataModel().removeRow(row);
			}
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		int rows = tabPanel.getBaseTable().getRowCount();
		try {
			if (baseTable.getSelectedRow() != -1) {
				if (baseTable.getValueAt(baseTable.getSelectedRow(), 2) != null && !"".equals(baseTable.getValueAt(baseTable.getSelectedRow(), 2))) {
					MulitCellEditor cell = (MulitCellEditor) baseTable.getCellEditor(0, 3);
					List<Map<String, Object>> lic = tabp.sqls.getListBySql(ITEM_TYPE.TABLE, "columnsbyname", new String[] { tabp.getSchemaName(), baseTable.getValueAt(baseTable.getSelectedRow(), 2).toString() });
					String[] vals = new String[lic.size()];
					for (int i = 0; i < lic.size(); i++) {
						Map<String, Object> m = lic.get(i);
						vals[i] = m.get("名称").toString();
					}
					cell.getCellEditor().setData(vals);
				}
			}
			String columns = "";
			for (int i = 0; i < rows; i++) {
				if (tabPanel.getBaseTable().getValueAt(i, 0) != null) {
					columns += tabPanel.getBaseTable().getValueAt(i, 0).toString() + ",";
				}
			}
			if (columns.length() > 0) {
				columns = columns.substring(0, columns.length() - 1);
			}
			MulitCellEditor cell = (MulitCellEditor) baseTable.getCellEditor(0, 1);
			cell.getCellEditor().setData(columns.split(","));
		} catch (Exception ex) {
			System.out.println(ex.getMessage() + "此异常不处理");
		}
	}
	/**
	 * 取消表格编辑状态
	 */
	public void cancleEdit() {
		int row = baseTable.getSelectedRow();
		if (row != -1) {
			DefaultTableModel dtm = (DefaultTableModel) baseTable.getModel();
			dtm.setValueAt(zs.getText(), row, 7);
			
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
			dtm.setValueAt(zs.getText(), prerow, 7);
		}
		int row = baseTable.getSelectedRow();
		if (row != -1) {
			String comment = (String) dtm.getValueAt(row, 7);
			zs.setText(comment);
			prerow = row;
		}
	}

	/**
	 * 编辑外键
	 * 
	 * @throws Exception
	 */
	public void editForeign() throws Exception {
		int rowcount = baseTable.getRowCount();
		for (int i = rowcount - 1; i >= 0; i--) {
			tablePanel.getTableDataModel().removeRow(i);
		}

		List<Map<String, Object>> lic = tabp.sqls.getListBySql(ITEM_TYPE.TABLE, "columnsinfo", new String[] { tabp.getTableoId() });
		values = new String[lic.size()];
		for (int i = 0; i < lic.size(); i++) {
			Map<String, Object> m = lic.get(i);
			values[i] = m.get("名称").toString();
		}
		// 查询表的外键
		List<Map<String, Object>> li = tabp.sqls.getListBySql(ITEM_TYPE.FOREIGN, "prop_coll", new String[] { tabp.getTableoId() });
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < li.size(); i++) {
			Map<String, Object> mp = new HashMap<String, Object>();
			TableForeignBean bean = new TableForeignBean();
			Map<String, Object> m = li.get(i);
			bean.setOid(m.get("oid").toString());
			mp.put("oid", m.get("oid").toString());
			bean.setForeignName(m.get("conname").toString());
			mp.put("名", m.get("conname").toString());
			String conkey = m.get("conkey").toString().replace("{", "").replace("}", "").replace("\"", "");
			// 获取外键表字段
			List<Map<String, Object>> ll = tabp.sqls.getListBySql(ITEM_TYPE.TABLE, "attname", new String[] { tabp.getTableoId(), conkey });
			String foricol = "";
			for (int j = 0; j < ll.size(); j++) {
				foricol += ll.get(j).get("attname").toString() + ",";
			}
			if (foricol.length() > 0) {
				foricol = foricol.substring(0, foricol.length() - 1);
			}
			bean.setRelcolumn(foricol);
			mp.put("栏位", foricol);
			bean.setForeign_table(m.get("foreign_table").toString());
			mp.put("外键表", m.get("foreign_table").toString());

			String columns = "";
			Map<String, List<String>> ml = tabp.sqls.grouping(tabp.sqls.getConstraint(tabp.getTableName(),"FOREIGN KEY"));
			if(ml.size()>0){
				for (String s : ml.get(m.get("foreign_table").toString())) {
					columns += s + ",";
				}
			}
			
			if (columns.length() > 0) {
				columns = columns.substring(0, columns.length() - 1);
			}
			bean.setForeign_column(columns);
			mp.put("外键表栏位", columns);
			bean.setDeltype(mtype.get(m.get("confdeltype").toString()));
			mp.put("删除时", mtype.get(m.get("confdeltype").toString()));
			bean.setUpdatetype(mtype.get(m.get("confupdtype").toString()));
			mp.put("更新时", mtype.get(m.get("confupdtype").toString()));
			if (m.get("comment") != null) {
				mp.put("comment", m.get("comment"));
				bean.setComment(m.get("comment").toString());
			} else {
				mp.put("comment", m.get("comment"));
				bean.setComment("");
			}
			map.put(bean.getOid(), bean);
			list.add(mp);
		}
		tablePanel.setData(lists, list);
		initCellEditor();
	}

	/**
	 * 组装编辑sql
	 * 
	 * @return
	 */
	public String editForeignSql() {
		int rows = baseTable.getRowCount();
		StringBuffer sqlBuffer = new StringBuffer();
		StringBuffer comment = new StringBuffer();
		if (dels.size() > 0) {
			for (String str : dels) {
				sqlBuffer.append(NEW_LINE).append(ALTER+" TABLE " ).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(" DROP CONSTRAINT ").append(str.trim());
				sqlBuffer.append(SEMI_COLON);
			}
		}
		for (int i = 0; i < rows; i++) {
			StringBuffer ldef = new StringBuffer();
			if (baseTable.getValueAt(i, 6) != null) {   //修改
				TableForeignBean bean = map.get(baseTable.getValueAt(i, 6).toString());
				if (!bean.getForeignName().equals(baseTable.getValueAt(i, 0))
						|| !bean.getRelcolumn().equals(baseTable.getValueAt(i, 1))
						|| !bean.getForeign_table().equals(baseTable.getValueAt(i, 2))
						|| !bean.getForeign_column().equals(baseTable.getValueAt(i, 3))
						|| !bean.getDeltype().equals(baseTable.getValueAt(i, 4))
						|| !bean.getUpdatetype().equals(baseTable.getValueAt(i, 5))
				) {
					sqlBuffer.append(NEW_LINE).append(ALTER_TABLE).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"");
					sqlBuffer.append(NEW_LINE).append(" DROP CONSTRAINT ").append(bean.getForeignName()).append(COMMA);
					sqlBuffer.append(NEW_LINE).append(ADD_CONSTRAINT).append(baseTable.getValueAt(i, 0)).append(" FOREIGN KEY ");
					sqlBuffer.append(B_OPEN).append(baseTable.getValueAt(i, 1)).append(B_CLOSE).append(REFERENCES);
					sqlBuffer.append("\""+tabp.getSchemaName()+"\"").append(DOT).append("\""+baseTable.getValueAt(i, 2)+"\"").append(B_OPEN).append(baseTable.getValueAt(i, 3)).append(B_CLOSE);
					if (baseTable.getValueAt(i, 4) != null && !"".equals(baseTable.getValueAt(i, 4))) {
						sqlBuffer.append(ON).append("DELETE ").append(baseTable.getValueAt(i, 4));
					}
					if (baseTable.getValueAt(i, 5) != null && !"".equals(baseTable.getValueAt(i, 5))) {
						sqlBuffer.append(ON).append("UPDATE ").append(baseTable.getValueAt(i, 5));
					}
					sqlBuffer.append(SEMI_COLON);
					ldef.append(NEW_LINE).append(" COMMENT ON CONSTRAINT ").append(baseTable.getValueAt(i, 0));
					ldef.append(ON).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(" IS ").append("'" + baseTable.getValueAt(i, 7) + "'").append(SEMI_COLON);
				}
				if (!bean.getComment().equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 7))) && ldef.length() == 0) {
					ldef.append(NEW_LINE).append(" COMMENT ON CONSTRAINT ").append(baseTable.getValueAt(i, 0));
					ldef.append(ON).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(" IS ").append("'" + baseTable.getValueAt(i, 7) + "'").append(SEMI_COLON);
				}
				comment.append(ldef);
			} else {   //添加
				if (baseTable.getValueAt(i, 0) != null || "".equals(baseTable.getValueAt(i, 0))) {
					sqlBuffer.append(NEW_LINE).append(ALTER_TABLE).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"");
					sqlBuffer.append(NEW_LINE).append(ADD_CONSTRAINT).append(baseTable.getValueAt(i, 0)).append(" FOREIGN KEY ");
					sqlBuffer.append(B_OPEN).append(baseTable.getValueAt(i, 1)).append(B_CLOSE).append(REFERENCES);
					sqlBuffer.append("\""+tabp.getSchemaName()+"\"").append(DOT).append("\""+baseTable.getValueAt(i, 2)+"\"").append(B_OPEN).append(baseTable.getValueAt(i, 3)).append(B_CLOSE);
					if (baseTable.getValueAt(i, 4) != null && !"".equals(baseTable.getValueAt(i, 4))) {
						sqlBuffer.append(ON).append("DELETE ").append(baseTable.getValueAt(i, 4));
					}
					if (baseTable.getValueAt(i, 5) != null && !"".equals(baseTable.getValueAt(i, 5))) {
						sqlBuffer.append(ON).append("UPDATE ").append(baseTable.getValueAt(i, 5));
					}
					sqlBuffer.append(SEMI_COLON);
					if (!"".equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 7)))) {
						comment.append(NEW_LINE).append(" COMMENT ON CONSTRAINT ").append(baseTable.getValueAt(i, 0));
						comment.append(ON).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(" IS ").append("'" + baseTable.getValueAt(i, 7) + "'").append(SEMI_COLON);
					}
				}
			}
		}
		sqlBuffer.append(comment);
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
