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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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
import com.hhdb.csadmin.plugin.table_operate.bean.TableRuleBean;
import com.hhdb.csadmin.plugin.table_operate.component.ComboBoxCellEditor;
import com.hhdb.csadmin.plugin.table_operate.component.CreateTableSQLSyntax;
import com.hhdb.csadmin.plugin.table_operate.component.TextCellEditor;
import com.hhdb.csadmin.plugin.table_operate.component.checkbox.CheckBoxCellEditor;
import com.hhdb.csadmin.plugin.table_operate.component.checkbox.CheckBoxRender;

/**
 * 
 * @Description: 规则
 * @Copyright: Copyright (c) 2017年10月25日
 * @Company:H2 Technology
 * @author zhipeng.zhang
 * @version 1.0
 */
public class HandleRulePanel extends JPanel implements TableModelListener,CreateTableSQLSyntax {
	private static final long serialVersionUID = 1L;
	private TableEditPanel tabp;
	
	private TablePanelUtil tablePanel;
	private ComboBoxCellEditor comboBoxcell;
	private BaseTable baseTable;
	private Map<String, TableRuleBean> map = new HashMap<String, TableRuleBean>();
	private static Map<String, String> mtype = new HashMap<String, String>();
	private static List<String> lists = new ArrayList<String>();
	private List<String> dels = new ArrayList<String>();
	private JTextField tj;
	private JTextArea dy;
	private JTextField zs;
	private int prerow = -1;

	static {
		lists.add("名");
		lists.add("oid");
		lists.add("事件");
		lists.add("代替运行");
		lists.add("id");
		lists.add("factor");
		lists.add("defini");
		lists.add("comment");
		mtype.put("1", "select");
		mtype.put("2", "update");
		mtype.put("3", "insert");
		mtype.put("4", "delete");
	}

	public HandleRulePanel(TableEditPanel tableeditpanel)throws Exception {
		this.tabp = tableeditpanel;
		setBackground(Color.WHITE);
		tablePanel = new TablePanelUtil(new int[] { 1 });
		tablePanel.setPreferredSize(new Dimension(352, 110));
		tablePanel.setBackground(Color.WHITE);
		tablePanel.getViewport().setBackground(Color.WHITE);
		tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		baseTable = tablePanel.getBaseTable();
		baseTable.setBackground(new Color(220, 255, 220));
		editRule();
		setLayout(new GridBagLayout());
		tj = new JTextField();
		tj.setPreferredSize(new Dimension(300, 20));
		dy = new JTextArea();
		dy.setLineWrap(true);        //激活自动换行功能
		zs = new JTextField();
		zs.setPreferredSize(new Dimension(300, 20));
		inputSetup(false);
		add(tablePanel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(0, 0, 0, 0), 0, 0));
		add(new JLabel("条件："), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(50, 10, 0, 0), 0, 0));
		add(tj, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(50, 10, 0, 0), 0, 0));
		add(new JLabel("定义："), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(10, 10, 0, 0), 0, 0));
		JScrollPane jsp = new JScrollPane(dy);
		jsp.setPreferredSize(new Dimension(300, 100));
		add(jsp, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(10, 10, 0, 0), 0, 0));
		add(new JLabel("注释："), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(10, 10, 0, 0), 0, 0));
		add(zs, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(10, 10, 0, 0), 0, 0));
		JPanel jpl = new JPanel();
		jpl.setBackground(Color.WHITE);
		add(jpl, new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,new Insets(0, 0, 0, 0), 0, 0));
		tablePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tablePanel.requestFocus();
			}
		});
		baseTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				rowClicked();
			}
		});
	}

	/**
	 * 编辑表格数据
	 * 
	 * @throws Exception
	 */
	public void editRule() throws Exception {
		int rowcount = baseTable.getRowCount();
		for (int i = rowcount - 1; i >= 0; i--) {
			tablePanel.getTableDataModel().removeRow(i);
		}
		// 查询表的规则
		List<Map<String, Object>> li = tabp.sqls.getListBySql(ITEM_TYPE.RULES, "prop", new String[] { tabp.getTableoId() });
		for (int i = 0; i < li.size(); i++) {
			TableRuleBean bean = new TableRuleBean();
			Map<String, Object> m = li.get(i);
			bean.setOid(m.get("oid").toString());
			bean.setRulename(m.get("名").toString());
			bean.setId(m.get("id").toString());
			bean.setEvtype(m.get("ev_type").toString());
			m.put("事件", mtype.get(m.get("ev_type").toString()));
			if (m.get("代替运行").equals("t") || m.get("代替运行").toString().equals("true")) {
				m.put("代替运行", true);
			} else {
				m.put("代替运行", false);
			}
			bean.setIsinstead(Boolean.parseBoolean(m.get("代替运行").toString()));
			if (m.get("comment") != null) {
				bean.setComment(m.get("comment").toString());
			} else {
				bean.setComment("");
			}
			String def = m.get("definition").toString();
			if (def.toUpperCase().indexOf("WHERE") > 0) {
				String factor = def.substring(def.toUpperCase()
						.indexOf("WHERE") + 5, def.toUpperCase().indexOf("DO"));
				bean.setFactor(factor);
				m.put("factor", factor);
			} else {
				bean.setFactor("");
				m.put("factor", "");
			}
			String defini = "";
			if (def.toUpperCase().indexOf("INSTEAD") > 0) {
				defini = def.substring(
						def.toUpperCase().indexOf("INSTEAD") + 7, def.length());
			} else { 
				defini = def.substring(def.toUpperCase().indexOf("DO") + 2,
						def.length());
			}
			defini = defini.replace(";", "");
			bean.setDefinition(defini);
			m.put("defini", defini);
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
		comboBoxcell = new ComboBoxCellEditor();
		CheckBoxCellEditor checkboxcell = new CheckBoxCellEditor();
		CheckBoxRender checkboxRender = new CheckBoxRender();
		// 将索引类型加载到comboBox
		getIndexTypes();
		tablePanel.getTableDataModel().addTableModelListener(this);
		tablePanel.getBaseTable().getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(textcell));
		tablePanel.getBaseTable().getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(textcell));
		tablePanel.getBaseTable().getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBoxcell));
		tablePanel.getBaseTable().getColumnModel().getColumn(3).setCellEditor(checkboxcell);
		tablePanel.getBaseTable().getColumnModel().getColumn(3).setCellRenderer(checkboxRender);
		setColumnWidth(new int[] { 0 });
	}

	/**
	 * 设置表格列的宽度
	 * @param column
	 * @param table
	 */
	private void setColumnWidth(int[] column) {
		for (int i = 0; i < baseTable.getColumnCount(); i++) {
			TableColumn firsetColumn = baseTable.getColumnModel().getColumn(i);
			firsetColumn.setPreferredWidth(60);
			firsetColumn.setMaxWidth(200);
			if (i == column[0]) {
				firsetColumn.setPreferredWidth(230);
				firsetColumn.setMaxWidth(230);
			}
			firsetColumn.setMinWidth(30);
		}
		hideColumn(new int[] { 1, 4, 5, 6, 7 });
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
	 * 索引类型
	 */
	private void getIndexTypes() {
		String[] indexs = new String[] { "Select", "Insert", "Update", "Delete" };
		for (String index : indexs) {
			comboBoxcell.addItem(index);
		}
	}

	/**
	 * 删除一行
	 */
	public void delRow() {
		int row = baseTable.getSelectedRow();
		int sum = baseTable.getRowCount();
		if (row != -1) {
			int result = JOptionPane.showConfirmDialog(null, "是否删除当前行", "提示信息",
					JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				if (baseTable.getValueAt(baseTable.getSelectedRow(), 4) != null) {
					dels.add(baseTable.getValueAt(baseTable.getSelectedRow(), 0).toString());
				}
				//获取下一行的下面输入框的值填入，防止自动赋值时将已删除的赋予下行
				if(row+1<sum){   //不能是最后一行
					zs.setText((String) baseTable.getModel().getValueAt(row+1, 7));
					dy.setText((String) baseTable.getModel().getValueAt(row+1, 6));
					tj.setText((String) baseTable.getModel().getValueAt(row+1, 5));
				}else{
					zs.setText("");
					dy.setText("");
					tj.setText("");
				}
				tablePanel.getTableDataModel().removeRow(row);
			}
		}
	}

	/**
	 * 添加一行
	 */
	public void addRows() {
		Object[] object = new Object[] { null, null, null, false };
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
			dtm.setValueAt(zs.getText(), row, 7);
			dtm.setValueAt(dy.getText(), row, 6);
			dtm.setValueAt(tj.getText(), row, 5);
			if (baseTable.isEditing()) {
				baseTable.getCellEditor().stopCellEditing();  //停止编辑
			}
		}
	}

	public void rowClicked() {
		tabp.getToolBar().getComponentAtIndex(0).setEnabled(tabp.controlButton = true);
		inputSetup(true);
		DefaultTableModel dtm = (DefaultTableModel) baseTable.getModel();
		if (prerow != -1 && (prerow + 1) <= baseTable.getRowCount()) {   //当值改变时反向赋予表中
			dtm.setValueAt(zs.getText(), prerow, 7);
			dtm.setValueAt(dy.getText(), prerow, 6);
			dtm.setValueAt(tj.getText(), prerow, 5);
		}
		int row = baseTable.getSelectedRow();
		if (row != -1) {
			String factor = (String) dtm.getValueAt(row, 5);
			String defini = (String) dtm.getValueAt(row, 6);
			String comment = (String) dtm.getValueAt(row, 7);
			tj.setText(factor);
			dy.setText(defini);
			zs.setText(comment);
			prerow = row;
		}
	}

	/**
	 * 组装编辑索引sql
	 * 
	 * @return
	 */
	public String editRuleSql() {
		StringBuffer sqlBuffer = new StringBuffer();
		StringBuffer comment = new StringBuffer();
		//删除约束
		if (dels.size() > 0) {       
			for (String st : dels) {
				sqlBuffer.append(NEW_LINE).append(DROP).append(RULE).append(st).append(ON).append("\"" + tabp.getSchemaName() + "\"").append(DOT).append("\"" + tabp.getTableName() + "\"").append(SEMI_COLON);
			}
		}
		int indexrows = baseTable.getRowCount();
		for (int i = 0; i < indexrows; i++) {
			StringBuffer lsdef = new StringBuffer();
			if (baseTable.getValueAt(i, 1) != null) {
				TableRuleBean bean = map.get(baseTable.getValueAt(i, 1).toString());
				//修改
				if (!bean.getRulename().equals(baseTable.getValueAt(i, 0)) || !bean.isIsinstead() == Boolean.parseBoolean(baseTable.getValueAt(i, 3).toString()) ||
						!mtype.get(bean.getEvtype()).equals(baseTable.getValueAt(i, 2)) || !bean.getFactor().equals(baseTable.getValueAt(i, 5))
						|| !bean.getDefinition().equals(baseTable.getValueAt(i, 6))) {
					//删除原来的约束
					sqlBuffer.append(NEW_LINE).append(" DROP RULE ").append(bean.getRulename()).append(" ON ").append("\"" + tabp.getSchemaName() + "\".\"" + tabp.getTableName()+ "\"").append(SEMI_COLON);
					//添加新的约束
					sqlBuffer.append(NEW_LINE).append(CREATE).append(OR).append(REPLACE).append(RULE).append(baseTable.getValueAt(i, 0));
					sqlBuffer.append(AS).append(ON).append(baseTable.getValueAt(i, 2).toString());
					sqlBuffer.append(TO).append("\"" + tabp.getSchemaName() + "\".\"" + tabp.getTableName() + "\"");
					//添加条件
					if (!"".equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 5)))) {
						sqlBuffer.append(" WHERE ").append(baseTable.getValueAt(i, 5));
					}
					sqlBuffer.append(" DO ");
					//是否替代运行
					if (Boolean.parseBoolean(baseTable.getValueAt(i, 3).toString())) {
						sqlBuffer.append(INSTEAD);
					}
					//添加定义
					if (baseTable.getValueAt(i, 6) != null) {
						sqlBuffer.append(baseTable.getValueAt(i, 6));
					} else {
						sqlBuffer.append(" NOTHING");
					}
					//给约束加上原来的注释
					lsdef.append(NEW_LINE).append(" COMMENT ON RULE ").append(baseTable.getValueAt(i, 0)).append(" ON ").append("\"" + tabp.getSchemaName() + "\".\""+ tabp.getTableName() + "\"");
					lsdef.append(" IS ").append("'" + baseTable.getValueAt(i, 7) + "'");
					lsdef.append(SEMI_COLON);
					sqlBuffer.append(SEMI_COLON);
				}
				//如果只改变了注释可单独修改
				if (!bean.getComment().equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 7))) && lsdef.length() == 0) {
					lsdef.append(NEW_LINE).append(" COMMENT ON RULE ").append(baseTable.getValueAt(i, 0)).append(" ON ").append("\"" + tabp.getSchemaName() + "\".\"" + tabp.getTableName()+ "\"");
					lsdef.append(" IS ").append("'" + baseTable.getValueAt(i, 7) + "'").append(SEMI_COLON);
				}
				comment.append(lsdef);
			} else {   //添加
				if ( null != baseTable.getValueAt(i, 0) || "".equals(baseTable.getValueAt(i, 0))) {
					sqlBuffer.append(NEW_LINE).append(CREATE).append(RULE).append(baseTable.getValueAt(i, 0));
					sqlBuffer.append(AS).append(ON).append(baseTable.getValueAt(i, 2).toString());
					sqlBuffer.append(TO).append("\"" + tabp.getSchemaName() + "\".\"" + tabp.getTableName() + "\"");
					if (!"".equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 5)))) {
						sqlBuffer.append(" WHERE ").append(baseTable.getValueAt(i, 5));
					}
					sqlBuffer.append(" DO ");
					//是否替代运行
					if (Boolean.parseBoolean(baseTable.getValueAt(i, 3).toString())) {
						sqlBuffer.append(INSTEAD);
					}
					if (!"".equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 6)))) {
						sqlBuffer.append(baseTable.getValueAt(i, 6));
					} else {
						sqlBuffer.append(" NOTHING");
					}
					if (!"".equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 7)))) {
						comment.append(NEW_LINE).append(" COMMENT ON RULE ").append(baseTable.getValueAt(i, 0)).append(" ON ")
								.append("\"" + tabp.getSchemaName() + "\".\"" + tabp.getTableName()+ "\"");
						comment.append(" IS ").append("'" + baseTable.getValueAt(i, 7) + "'").append(SEMI_COLON);
					}
					sqlBuffer.append(SEMI_COLON);
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
			tj.setBackground(new Color(220, 255, 220));
			dy.setBackground(new Color(220, 255, 220));
			zs.setBackground(new Color(220, 255, 220));
			tj.setEditable(true);
			dy.setEditable(true);
			zs.setEditable(true);
		}else{
			tj.setEditable(false);
			dy.setEditable(false);
			zs.setEditable(false);
		}
	}
	
	
	public BaseTable getBaseTable() {
		return baseTable;
	}

	public JTextField getTj() {
		return tj;
	}

	public void setTj(JTextField tj) {
		this.tj = tj;
	}

	public JTextArea getDy() {
		return dy;
	}

	public void setDy(JTextArea dy) {
		this.dy = dy;
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
