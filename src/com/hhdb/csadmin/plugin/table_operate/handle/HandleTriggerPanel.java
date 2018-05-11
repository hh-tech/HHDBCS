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
import javax.swing.JComboBox;
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
import com.hhdb.csadmin.plugin.table_operate.TableEditPanel;
import com.hhdb.csadmin.plugin.table_operate.bean.TableTriggerBean;
import com.hhdb.csadmin.plugin.table_operate.component.ComboBoxCellEditor;
import com.hhdb.csadmin.plugin.table_operate.component.CreateTableSQLSyntax;
import com.hhdb.csadmin.plugin.table_operate.component.TextCellEditor;
import com.hhdb.csadmin.plugin.table_operate.component.checkbox.CheckBoxCellEditor;
import com.hhdb.csadmin.plugin.table_operate.component.checkbox.CheckBoxRender;
import com.hhdb.csadmin.plugin.table_operate.component.listcombox.MulitCellEditor;

/**
 * 
 * @Description: 表格触发器
 * @Copyright: Copyright (c) 2017年10月25日
 * @Company:H2 Technology
 * @author zhipeng.zhang
 * @version 1.0
 */
public class HandleTriggerPanel extends JPanel implements TableModelListener, CreateTableSQLSyntax {
	private static final long serialVersionUID = 1L;
	private TableEditPanel tabp;
	
	private TablePanelUtil tablePanel;
	private HandleTablePanel tabPanel;
	private ComboBoxCellEditor comboBoxcell;
	private String[] values;
	private BaseTable baseTable;
	private JComboBox<String> tgfunc;
	private JTextField funparm;
	private JTextField zs;
	private int prerow = -1;
	private Map<String, TableTriggerBean> map = new HashMap<String, TableTriggerBean>();
	private static List<String> lists = new ArrayList<String>();
	private List<String> dels = new ArrayList<String>();
	private List<String> boxv = new ArrayList<String>();   //触发器函数

	static {
		lists.add("名");
		lists.add("行触发器");
		lists.add("触发");
		lists.add("插入");
		lists.add("更新");
		lists.add("删除");
		lists.add("更新栏位");
		lists.add("oid");
		lists.add("tgfun");
		lists.add("funparm");
		lists.add("comment");
	}

	public HandleTriggerPanel(TableEditPanel tableeditpanel, HandleTablePanel tabsPanel) throws Exception {
		this.tabp = tableeditpanel;
		this.tabPanel = tabsPanel;
		setBackground(Color.WHITE);
		tgfunc = new JComboBox<String>();
		tgfunc.setPreferredSize(new Dimension(300, 20));
		funparm = new JTextField();
		funparm.setPreferredSize(new Dimension(300, 20));
		zs = new JTextField();
		zs.setPreferredSize(new Dimension(300, 20));
		inputSetup(false);
		tablePanel = new TablePanelUtil(true);
		tablePanel.setPreferredSize(new Dimension(750, 210));
		tablePanel.setBackground(Color.WHITE);
		tablePanel.getViewport().setBackground(Color.WHITE);
		tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		baseTable = tablePanel.getBaseTable();
		baseTable.setBackground(new Color(220, 255, 220));
		editTrigger();
		setLayout(new GridBagLayout());
		add(tablePanel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(new JLabel("触发器函数："), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(50, 10, 0, 0), 0, 0));
		add(tgfunc, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(50, 10, 0, 0), 0, 0));
		add(new JLabel("函数参数："), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(funparm, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(10, 10, 0, 0), 0, 0));
		add(new JLabel("注释："), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(zs, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(10, 10, 0, 0), 0, 0));
		JPanel jpl = new JPanel();
		jpl.setBackground(Color.WHITE);
		add(jpl, new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
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
	 * 编辑
	 * 
	 * @throws Exception
	 */
	public void editTrigger() throws Exception {
		int rowcount = baseTable.getRowCount();
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
		// 查询触发器函数
		List<Map<String, Object>> funbean = tabp.sqls.getListBySql(ITEM_TYPE.TRIGGER, "source", new String[] { tabp.getSchemaName() });
		for (Map<String, Object> m : funbean) {
			tgfunc.addItem(m.get("proname").toString());
			boxv.add(m.get("proname").toString());
		}
		List<Map<String, Object>> li = tabp.sqls.getListBySql(ITEM_TYPE.TRIGGER, "prop", new String[] { tabp.getTableoId() });
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < li.size(); i++) {
			Map<String, Object> mp = new HashMap<String, Object>();
			TableTriggerBean bean = new TableTriggerBean();
			Map<String, Object> m = li.get(i);
			mp.put("名", m.get("trigger_name"));
			bean.setTreggerName(m.get("trigger_name").toString());
			if (m.get("action_orientation").toString().equals("ROW")) {
				mp.put("行触发器", true);
				bean.setIsrow(true);
			} else {
				mp.put("行触发器", false);
				bean.setIsrow(false);
			}
			mp.put("触发", m.get("condition_timing"));
			bean.setCondtion(m.get("condition_timing").toString());
			if (m.get("tginsert").toString().equals("0")) {
				mp.put("插入", false);
				bean.setTginsert(false);
			} else {
				mp.put("插入", true);
				bean.setTginsert(true);
			}
			if (m.get("tgupdate").toString().equals("0")) {
				mp.put("更新", false);
				bean.setTgupdate(false);
			} else {
				mp.put("更新", true);
				bean.setTgupdate(true);
			}
			if (m.get("tgdelete").toString().equals("0")) {
				mp.put("删除", false);
				bean.setTgdelete(false);
			} else {
				mp.put("删除", true);
				bean.setTgdelete(true);
			}
			mp.put("更新栏位", m.get("event_object"));
			bean.setColumns(m.get("event_object").toString());
			mp.put("oid", m.get("oid"));
			bean.setOid(m.get("oid").toString());
			String actionstatement = m.get("action_statement").toString();
			String tgfun = actionstatement.substring(actionstatement.indexOf("PROCEDURE") + 9,
					actionstatement.indexOf("("));
			mp.put("tgfun", tgfun.trim());
			bean.setTgfunc(tgfun.trim());
			String funparm = "";
			funparm = actionstatement.substring(actionstatement.indexOf("(") + 1, actionstatement.indexOf(")"));
			if (m.get("obj_description") != null && !"".equals(m.get("obj_description").toString())) {
				mp.put("comment", m.get("obj_description"));
				bean.setComment(m.get("obj_description").toString());
			} else {
				mp.put("comment", "");
				bean.setComment("");
			}
			funparm = funparm.replace("'", "");
			if (null != funparm && !funparm.equals("")) {
				mp.put("funparm", funparm);
				bean.setFunparm(funparm);
			} else {
				mp.put("funparm", "");
				bean.setFunparm("");
			}
			list.add(mp);
			map.put(bean.getOid(), bean);
		}
		tablePanel.setData(lists, list);
		initCellEditor();
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
		tablePanel.getBaseTable().getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(textcell));
		tablePanel.getBaseTable().getColumnModel().getColumn(1).setCellEditor(checkboxcell);
		tablePanel.getBaseTable().getColumnModel().getColumn(1).setCellRenderer(checkboxRender);
		tablePanel.getBaseTable().getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBoxcell));
		tablePanel.getBaseTable().getColumnModel().getColumn(3).setCellEditor(checkboxcell);
		tablePanel.getBaseTable().getColumnModel().getColumn(3).setCellRenderer(checkboxRender);
		tablePanel.getBaseTable().getColumnModel().getColumn(4).setCellEditor(checkboxcell);
		tablePanel.getBaseTable().getColumnModel().getColumn(4).setCellRenderer(checkboxRender);
		tablePanel.getBaseTable().getColumnModel().getColumn(5).setCellEditor(checkboxcell);
		tablePanel.getBaseTable().getColumnModel().getColumn(5).setCellRenderer(checkboxRender);
		tablePanel.getBaseTable().getColumnModel().getColumn(6).setCellEditor(mulitcell);
		
		setColumnWidth(new int[] { 0, 6 });
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
			if (i == column[0] || i == column[1]) {
				firsetColumn.setPreferredWidth(230);
				firsetColumn.setMaxWidth(230);
			}
			firsetColumn.setMinWidth(30);
		}
		hideColumn(new int[] {6, 7, 8, 9, 10 });
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
	 * 触发器类型类型
	 */
	private void getIndexTypes() {
		String[] indexs = new String[] { "Before", "After" };
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
			int result = JOptionPane.showConfirmDialog(null, "是否删除当前行", "提示信息", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				if (baseTable.getValueAt(baseTable.getSelectedRow(), 0) != null) {
					dels.add(baseTable.getValueAt(baseTable.getSelectedRow(), 0).toString());
				}
				//获取下一行的下面输入框的值填入，防止自动赋值时将已删除的赋予下行
				if(row+1<sum){   //不能是最后一行
					String factor = (String) baseTable.getModel().getValueAt(row+1,8);
					if(boxv.contains(factor)){
						tgfunc.setSelectedItem(factor);
					}else{
						tgfunc.setSelectedIndex(-1);
					}
					funparm.setText((String) baseTable.getModel().getValueAt(row+1, 9));
					zs.setText((String) baseTable.getModel().getValueAt(row+1, 10));
				}else{
					tgfunc.setSelectedIndex(-1);
					funparm.setText("");
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
		Object[] object = new Object[] { null, false, null, false, false, false, "", null, null, null, null };
		tablePanel.getTableDataModel().addRow(object);
	}
	
	/**
	 * 初始化字段选择弹出页面
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
		MulitCellEditor cell = (MulitCellEditor) baseTable.getCellEditor(0, 6);
		cell.getCellEditor().setData(columns.split(","));
	}

	/**
	 * 组装编辑sql
	 * 
	 * @return
	 */
	public String editTriggerSql() {
		int rows = baseTable.getRowCount();
		StringBuffer sqlBuffer = new StringBuffer();
		StringBuffer comment = new StringBuffer();
		if (dels.size() > 0) { 
			for (String str : dels) {
				sqlBuffer.append(NEW_LINE).append(DROP ).append("TRIGGER ").append(str.trim()).append(" ON ").append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(SEMI_COLON);
			}
		}
		for (int i = 0; i < rows; i++) {
			if (baseTable.getValueAt(i, 7) != null) {
				StringBuffer lsdef = new StringBuffer();
				TableTriggerBean bean = map.get(baseTable.getValueAt(i, 7).toString());
				//修改
				if (	!bean.getTreggerName().equals(baseTable.getValueAt(i, 0).toString())   
						||bean.isIsrow() != Boolean.parseBoolean(baseTable.getValueAt(i, 1).toString())
						|| !bean.getCondtion().equals(baseTable.getValueAt(i, 2).toString())
						|| bean.isTginsert() != Boolean.parseBoolean(baseTable.getValueAt(i, 3).toString())
						|| bean.isTgupdate() != Boolean.parseBoolean(baseTable.getValueAt(i, 4).toString())
						|| bean.isTgdelete() != Boolean.parseBoolean(baseTable.getValueAt(i, 5).toString())
						//|| !bean.getColumns().equals(baseTable.getValueAt(i, 6).toString())
						|| !bean.getTgfunc().equals(baseTable.getValueAt(i, 8).toString())    //函数
						|| !bean.getFunparm().equals(baseTable.getValueAt(i, 9).toString())   //函数参数
						) {
					//删除原来的
					sqlBuffer.append(NEW_LINE).append(DROP ).append("TRIGGER ").append(bean.getTreggerName()).append(" ON ").append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"");
					sqlBuffer.append(SEMI_COLON).append(NEW_LINE);
					//添加新的
					sqlBuffer.append(" CREATE TRIGGER ").append(baseTable.getValueAt(i, 0)).append(SPACE).append(baseTable.getValueAt(i, 2));
					//添加类别
					if (Boolean.parseBoolean(baseTable.getValueAt(i, 3).toString())) {
						sqlBuffer.append(" INSERT ");
					}
					if (Boolean.parseBoolean(baseTable.getValueAt(i, 4).toString())) {
						if (Boolean.parseBoolean(baseTable.getValueAt(i, 3).toString())) {
							sqlBuffer.append(" OR ");
						}
						sqlBuffer.append(" UPDATE ");//.append(baseTable.getValueAt(i, 6));
					}
					if (Boolean.parseBoolean(baseTable.getValueAt(i, 5).toString())) {
						if (Boolean.parseBoolean(baseTable.getValueAt(i, 3).toString()) || Boolean.parseBoolean(baseTable.getValueAt(i, 4).toString())) {
							sqlBuffer.append(" OR ");
						}
						sqlBuffer.append(" DELETE ");
					}
					sqlBuffer.append(" ON ").append("\""+tabp.getSchemaName()+"\"").append(DOT).append("\""+tabp.getTableName()+"\"");
					//是否行触发
					if (Boolean.parseBoolean(baseTable.getValueAt(i, 1).toString())) {
						sqlBuffer.append(" FOR EACH ROW ");
					} else {
						sqlBuffer.append(" FOR EACH STATEMENT ");
					}
					//函数名称
					sqlBuffer.append(" EXECUTE PROCEDURE ").append("\""+tabp.getSchemaName()+"\"").append(DOT).append(baseTable.getValueAt(i, 8));
					//函数参数
					if (!"".equals(tabp.sqls.nullOfStr(baseTable.getValueAt(i, 9)))) {
						sqlBuffer.append("(").append(baseTable.getValueAt(i, 9)).append(")");
					} else {
						sqlBuffer.append("()");
					}
					sqlBuffer.append(SEMI_COLON);
					//添加注释
					if (baseTable.getValueAt(i, 10) != null ) {
						lsdef.append(NEW_LINE).append(" COMMENT ON TRIGGER ").append(baseTable.getValueAt(i, 0)).append(" ON ");
						lsdef.append("\""+tabp.getSchemaName()+"\"").append(DOT).append("\""+tabp.getTableName()+"\"").append(" IS ").append("'" + baseTable.getValueAt(i, 10) + "'").append(SEMI_COLON);
					}
				}
				//单独修改注释
				if (!bean.getComment().equals(tabp.sqls.nullOfStr(baseTable.getValueAt(i, 10))) && lsdef.length() == 0) {
					lsdef.append(" COMMENT ON TRIGGER ").append(baseTable.getValueAt(i, 0)).append(" ON ");
					lsdef.append(NEW_LINE).append("\""+tabp.getSchemaName()+"\"").append(DOT).append("\""+tabp.getTableName()+"\"").append(" IS ").append("'" + baseTable.getValueAt(i, 10) + "'").append(SEMI_COLON);
				}
				comment.append(lsdef);
			} else {     //新增
				if (baseTable.getValueAt(i, 0) != null || "".equals(baseTable.getValueAt(i, 0))) {
					sqlBuffer.append(NEW_LINE).append(" CREATE TRIGGER ").append(baseTable.getValueAt(i, 0)).append(SPACE).append(baseTable.getValueAt(i, 2));
					if (Boolean.parseBoolean(baseTable.getValueAt(i, 3).toString())) {
						sqlBuffer.append(" INSERT ");
					}
					if (Boolean.parseBoolean(baseTable.getValueAt(i, 4).toString())) {
						if (Boolean.parseBoolean(baseTable.getValueAt(i, 3).toString())) {
							sqlBuffer.append(" OR ");
						}
						sqlBuffer.append(" UPDATE ");//.append(baseTable.getValueAt(i, 6));
					}
					if (Boolean.parseBoolean(baseTable.getValueAt(i, 5).toString())) {
						if (Boolean.parseBoolean(baseTable.getValueAt(i, 3).toString()) || Boolean.parseBoolean(baseTable.getValueAt(i, 4).toString())) {
							sqlBuffer.append(" OR ");
						}
						sqlBuffer.append(" DELETE ");
					}
					sqlBuffer.append(" ON ").append("\""+tabp.getSchemaName()+"\"").append(DOT).append("\""+tabp.getTableName()+"\"");
					if (Boolean.parseBoolean(baseTable.getValueAt(i, 1).toString())) {
						sqlBuffer.append(" FOR EACH ROW ");
					} else {
						sqlBuffer.append(" FOR EACH STATEMENT ");
					}
					sqlBuffer.append(" EXECUTE PROCEDURE ").append("\""+tabp.getSchemaName()+"\"").append(DOT).append(baseTable.getValueAt(i, 8));
					if (!"".equals(tabp.sqls.nullOfStr(baseTable.getValueAt(i, 9)))) {
						sqlBuffer.append("(").append(baseTable.getValueAt(i, 9)).append(")");
					} else {
						sqlBuffer.append("()");
					}
					sqlBuffer.append(SEMI_COLON);
					if (!"".equals(tabp.sqls.nullOfStr(baseTable.getValueAt(i, 10)))) {
						comment.append(NEW_LINE).append(" COMMENT ON TRIGGER ").append(baseTable.getValueAt(i, 0)).append(" ON ");
						comment.append("\""+tabp.getSchemaName()+"\"").append(DOT).append("\""+tabp.getTableName()+"\"")
						.append(" IS ").append("'" + baseTable.getValueAt(i, 10) + "'").append(SEMI_COLON);
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
	 * 取消表格编辑状态
	 */
	public void cancleEdit() {
		DefaultTableModel dtm = (DefaultTableModel) baseTable.getModel();
		int row = baseTable.getSelectedRow();
		if (row != -1) {
			if (tgfunc.getSelectedItem() != null) {
				dtm.setValueAt(tgfunc.getSelectedItem().toString(), row, 8);
			} else {
				dtm.setValueAt("", row, 8);
			}
			dtm.setValueAt(funparm.getText(), row, 9);
			dtm.setValueAt(zs.getText(), row, 10);
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
			if (tgfunc.getSelectedItem() != null) {
				dtm.setValueAt(tgfunc.getSelectedItem().toString(), prerow, 8);
			} else {
				dtm.setValueAt("", prerow, 8);
			}
			dtm.setValueAt(funparm.getText(), prerow, 9);
			dtm.setValueAt(zs.getText(), prerow, 10);
		}
		int row = baseTable.getSelectedRow();
		if (row != -1) {
			String factor = (String) dtm.getValueAt(row, 8);
			String defini = (String) dtm.getValueAt(row, 9);
			String comment = (String) dtm.getValueAt(row, 10);
			
			if(boxv.contains(factor)){
				tgfunc.setSelectedItem(factor);
			}else{
				tgfunc.setSelectedIndex(-1);
			}
			funparm.setText(defini);
			zs.setText(comment);
			prerow = row;
		}
	}
	
	/**
	 * 输入框设置
	 * @param bool
	 */
	public void inputSetup(Boolean bool){
		if(bool){
			tgfunc.setBackground(new Color(220, 255, 220));
			funparm.setBackground(new Color(220, 255, 220));
			zs.setBackground(new Color(220, 255, 220));
			tgfunc.setEnabled(true);
			funparm.setEditable(true);
			zs.setEditable(true);
		}else{
			tgfunc.setEnabled(false);
			funparm.setEditable(false);
			zs.setEditable(false);
		}
	}
	
	
	public BaseTable getBaseTable() {
		return baseTable;
	}

	public JComboBox<String> getTgfunc() {
		return tgfunc;
	}

	public void setTgfunc(JComboBox<String> tgfunc) {
		this.tgfunc = tgfunc;
	}

	public JTextField getFunparm() {
		return funparm;
	}

	public void setFunparm(JTextField funparm) {
		this.funparm = funparm;
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
