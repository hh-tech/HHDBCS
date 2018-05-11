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
import com.hhdb.csadmin.plugin.table_operate.component.ComboBoxCellEditor;
import com.hhdb.csadmin.plugin.table_operate.component.CreateTableSQLSyntax;
import com.hhdb.csadmin.plugin.table_operate.component.NumberCellEditor;
import com.hhdb.csadmin.plugin.table_operate.component.TableColumnsBean;
import com.hhdb.csadmin.plugin.table_operate.component.TextCellEditor;
import com.hhdb.csadmin.plugin.table_operate.component.checkbox.CheckBoxCellEditor;
import com.hhdb.csadmin.plugin.table_operate.component.checkbox.CheckBoxRender;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.BasePopupMenu;

/**
 * 
 * @Description: 设计数据表列内容
 * @Copyright: Copyright (c) 2017年10月25日
 * @Company:H2 Technology
 * @author zhipeng.zhang
 * @version 1.0
 */
public class HandleTablePanel extends JPanel implements TableModelListener, CreateTableSQLSyntax {
	private static final long serialVersionUID = 1L;
	
	private TableEditPanel tabp;
	
	private TablePanelUtil tablePanel;
	private ComboBoxCellEditor comboBoxcell;
	private BaseTable baseTable;
	private String keyname = "";
	private String oldkeyname = "";
	private boolean iskeys = false;
	private Map<String, String[]> typemap = new HashMap<String, String[]>();
	private Map<String, TableColumnsBean> map = new HashMap<String, TableColumnsBean>();
	private List<String> dels = new ArrayList<String>();
	private JTextField mr;
	private JTextField zs;
	private int prerow = -1;
	
	private static List<String> lists = new ArrayList<String>();
	static BasePopupMenu popMenu = null;
	static {
		lists.add("名称");
		lists.add("类型");
		lists.add("长度");
		lists.add("小数点");
		lists.add("不是null");
		lists.add("主键");
		lists.add("position");
		lists.add("col_default");
		lists.add("comment");
		lists.add("collname");
	}

	public HandleTablePanel(TableEditPanel tableeditpanel)throws Exception {
		this.tabp = tableeditpanel;
		
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);// 整个分页面板背景颜色
		
		tablePanel = new TablePanelUtil(true);
		baseTable = tablePanel.getBaseTable();
		tablePanel.getBaseTable().setBackground(new Color(220, 255, 220));// 表格单元格背景颜色
		tablePanel.setPreferredSize(new Dimension(700, 210));// 表格面板大小
		tablePanel.getViewport().setBackground(Color.WHITE);// 表格面板背景颜色
		tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		editTable();
		mr = new JTextField();
		mr.setPreferredSize(new Dimension(300, 20));
		zs = new JTextField();
		zs.setPreferredSize(new Dimension(300, 20));
		inputSetup(false);
		add(tablePanel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(new JLabel("默认："), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(50, 10, 0, 0), 0, 0));
		add(mr, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(50, 10, 0, 0), 0, 0));
		add(new JLabel("注释："), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		add(zs, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(10, 10, 0, 0), 0, 0));
		JPanel jpl = new JPanel();
		jpl.setBackground(Color.WHITE);
		add(jpl, new GridBagConstraints(0, 5, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,new Insets(0, 0, 0, 0), 0, 0));
		
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
		baseTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				baseTable.requestFocusInWindow();
			}
		});
	}

	/**
	 * 编辑表格
	 * 
	 * @throws Exception
	 */
	public void editTable() throws Exception {
		int rowcount = baseTable.getRowCount();
		for (int i = rowcount - 1; i >= 0; i--) {
			tablePanel.getTableDataModel().removeRow(i);
		}
		List<Map<String, Object>> lis = tabp.sqls.getListBySql(ITEM_TYPE.TABLE, "columnspaykey", new String[] { tabp.getSchemaName(), tabp.getTableName(), tabp.getTableName(), tabp.getSchemaName() });
		Map<String, Object> mp = new HashMap<String, Object>();
		for (Map<String, Object> m : lis) {
			mp.put(m.get("ordinal_position").toString(), true);
			keyname = m.get("pk_name").toString();
			oldkeyname = m.get("pk_name").toString();
			iskeys = true;
		}
		List<Map<String, Object>> li = tabp.sqls.getListBySql(ITEM_TYPE.TABLE, "columnsinfo", new String[] { tabp.getTableoId() });
		for (int i = 0; i < li.size(); i++) {
			Map<String, Object> m = li.get(i);
			TableColumnsBean col = new TableColumnsBean();
			col.setPosition(m.get("position").toString());
			if (mp.get(m.get("position").toString()) != null) {
				m.put("主键", true);
				col.setPrimaryKey(true);
			} else {
				m.put("主键", false);
				col.setPrimaryKey(false);
			}
			col.setTable(tabp.getTableName());
			col.setColumnName(m.get("名称").toString());
			col.setTypeName(m.get("类型").toString());
			if (m.get("长度") != null) {
				col.setColumnSize(m.get("长度").toString());
//			}else if(m.get("长度")==null&&m.get("类型").toString().equals("varchar")){  //varchar类型默认一个GB
			}else {  
				m.put("长度", "");
				col.setColumnSize("");
			}
			if (m.get("小数点") != null) {
				col.setDecimals(m.get("小数点").toString());
			} else {
				m.put("小数点", "");
				col.setDecimals("");
			}
			if ("t".equals(m.get("不是null").toString()) || "true".equals(m.get("不是null").toString())) {
				m.put("不是null", true);        
				col.setNull(true);
			} else {
				m.put("不是null", false);
				col.setNull(false);
			}
			if (m.get("collname") != null) {
				col.setCollname(m.get("collname").toString());
			} else {
				col.setCollname("");
			}
			if (m.get("comment") != null) {
				col.setComment(m.get("comment").toString());
			} else {
				col.setComment("");
			}
			if (m.get("col_default") != null) {
				col.setColdefault(m.get("col_default").toString());
			} else {
				col.setColdefault("");
			}
			col.copyColumn();
			map.put(m.get("position").toString(), col);
		}
		tablePanel.setData(lists, li);
		initCellEditor();
		setColumnWidth(new int[] { 0, 1 });
	}

	/**
	 * 初始化单元格控件
	 */
	public void initCellEditor() {
		NumberCellEditor numbercell = new NumberCellEditor();
		TextCellEditor textcell = new TextCellEditor();
		comboBoxcell = new ComboBoxCellEditor();
		CheckBoxCellEditor checkboxcell = new CheckBoxCellEditor();
		CheckBoxRender checkboxRenderer = new CheckBoxRender();
		CheckBoxCellEditor keyboxcell = new CheckBoxCellEditor();
		CheckBoxRender keyboxRenderer = new CheckBoxRender();
		// 将数据类型加载到comboBox
		getDataTypes();
		tablePanel.getTableDataModel().addTableModelListener(this);
		//设置单元格显示组件
		tablePanel.getBaseTable().getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(textcell));
		tablePanel.getBaseTable().getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(comboBoxcell));
		tablePanel.getBaseTable().getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(numbercell));
		tablePanel.getBaseTable().getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(numbercell));
		tablePanel.getBaseTable().getColumnModel().getColumn(4).setCellEditor(checkboxcell);
		tablePanel.getBaseTable().getColumnModel().getColumn(4).setCellRenderer(checkboxRenderer);
		tablePanel.getBaseTable().getColumnModel().getColumn(5).setCellEditor(keyboxcell);
		tablePanel.getBaseTable().getColumnModel().getColumn(5).setCellRenderer(keyboxRenderer);
	}

	/**
	 * 设置表格列的宽度
	 * 
	 * @param column
	 * @param table
	 */
	public void setColumnWidth(int[] column) {
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
		hideColumn(new int[] { 6, 7, 8, 9 });
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
	 * 数据类型
	 */
	private void getDataTypes() {
		List<Map<String, Object>> list = tabp.sqls.getListBySql(ITEM_TYPE.DATASERVICE, "datatypelist", new String[] { tabp.getTableoId() });
		for (Map<String, Object> map : list) {
			if (!"serial".equals(map.get("tn").toString()) && !"\"char\"".equals(map.get("tn").toString())) {
				comboBoxcell.addItem(map.get("tn").toString());
				typemap.put(map.get("tn").toString(),
						new String[] { map.get("has_length") + "", map.get("usage_count") + "" });
			} else if (!"\"char\"".equals(map.get("tn").toString())) {
				comboBoxcell.addItem(map.get("tn").toString());
				typemap.put(map.get("tn").toString(),
						new String[] { map.get("has_length") + "", map.get("usage_count") + "" });
			}
		}
	}

	/**
	 * 添加一行
	 */
	public void addRows() {
		tablePanel.getTableDataModel().addRow(new Object[] { null, null, null, null, false, false, null, null, null, null });
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
					dels.add(baseTable.getValueAt(baseTable.getSelectedRow(), 6).toString());
				}
				//获取下一行的下面输入框的值填入，防止自动赋值时将已删除的赋予下行
				if(row+1<sum){   //不能是最后一行
					mr.setText((String) baseTable.getModel().getValueAt(row+1, 7));
					zs.setText((String) baseTable.getModel().getValueAt(row+1, 8));
				}else{
					mr.setText("");
					zs.setText("");
				}
				tablePanel.getTableDataModel().removeRow(row);
			}
		} else {
			JOptionPane.showMessageDialog(tablePanel, "没有选择行");
		}
	}

	/**
	 * 取消表格编辑状态
	 */
	public void cancleEdit() {
		DefaultTableModel dtm = (DefaultTableModel) baseTable.getModel();
		int row = baseTable.getSelectedRow();
		if (row != -1) {
			dtm.setValueAt(mr.getText(), row, 7);
			dtm.setValueAt(zs.getText(), row, 8);
			if (dtm.getValueAt(row, 1) != null
					&& ("varchar".equals(dtm.getValueAt(row, 1)) || "char".equals(dtm.getValueAt(row, 1))
							|| "text".equals(dtm.getValueAt(prerow, 1)) || "bpchar".equals(dtm.getValueAt(prerow, 1))
							|| "character varying".equals(dtm.getValueAt(prerow, 1))
							|| "character".equals(dtm.getValueAt(prerow, 1)))) {
				// dtm.setValueAt(pxgz.getSelectedItem().toString(), row,9);
			} else {
				dtm.setValueAt("", row, 9);
			}
			if (baseTable.isEditing()) {
				baseTable.getCellEditor().stopCellEditing();
			}
		}
	}
	
	public void rowClicked() {
		inputSetup(true);
		tabp.getToolBar().getComponentAtIndex(0).setEnabled(tabp.controlButton = true);
		DefaultTableModel dtm = (DefaultTableModel) baseTable.getModel();
		int row = baseTable.getSelectedRow();
		if (prerow != -1 && (prerow + 1) <= baseTable.getRowCount() && row != -1) {   
			dtm.setValueAt(mr.getText(), prerow, 7);
			dtm.setValueAt(zs.getText(), prerow, 8);
			if (dtm.getValueAt(prerow, 1) != null && ("varchar".equals(dtm.getValueAt(prerow, 1))
					|| "char".equals(dtm.getValueAt(prerow, 1)) || "character varying".equals(dtm.getValueAt(prerow, 1))
					|| "character".equals(dtm.getValueAt(prerow, 1)) || "text".equals(dtm.getValueAt(prerow, 1))
					|| "bpchar".equals(dtm.getValueAt(prerow, 1)))) {
				// dtm.setValueAt(pxgz.getSelectedItem().toString(), prerow,9);
			} else {
				dtm.setValueAt("", prerow, 9);
			}
		}
		if (row != -1) {
			String coldefault = (String) dtm.getValueAt(row, 7);
			String comment = (String) dtm.getValueAt(row, 8);
			// String collname = (String) dtm.getValueAt(row, 9);
			mr.setText(coldefault);
			zs.setText(comment);
			// pxgz.setSelectedItem(collname);
			prerow = row;
		}
	}
	
	public void tableChanged(TableModelEvent e) {
//		int row = baseTable.getEditingRow();
//		int col = baseTable.getEditingColumn();
//		System.out.println(row);
//		System.out.println(col);
	}
	
	/**
	 * 设置主键名称
	 */
	public void updateKeyName() {
		String keyName = JOptionPane.showInputDialog(tablePanel, "请输入主键限制名", keyname, JOptionPane.PLAIN_MESSAGE);
		if (keyName != null && !keyName.equals("")) {
			if(tabp.getSign()){     //新增
				oldkeyname = keyName;
			}
			keyname = keyName;
		}
	}

	/**
	 * 组装编辑sql
	 * @return
	 */
	public String editTableSql() {
		// 表格编辑组装sql
		StringBuffer sqlBuffer = new StringBuffer();
		StringBuffer sqlText = new StringBuffer();
		StringBuffer coldefault = new StringBuffer();
		int rows = baseTable.getRowCount();
		StringBuffer alter = new StringBuffer();
		StringBuffer altername = new StringBuffer();
		String parkey = "(";
		String dropkey = "";
		boolean iskey = false;
		for (String str : dels) {
			sqlBuffer.append(NEW_LINE).append(ALTER_TABLE).append(SPACE).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(SPACE);
			sqlBuffer.append(DROP_COLUMN).append(map.get(str).getColumnName()).append(SEMI_COLON);
		}
		for (int i = 0; i < rows; i++) {
			if(!tabp.getSign()){   //修改
				alter.setLength(0);
				altername.setLength(0);
				if(!oldkeyname.equals(keyname)){  //判断是否需要改主键名称
					iskey = true;
				}
				if (keyname.equals("") && oldkeyname.equals("")) {
					keyname = tabp.getTableName() + "_pkey";
				}
				//主键字段
				if (Boolean.parseBoolean(baseTable.getValueAt(i, 5).toString())) {
					parkey += "\""+baseTable.getValueAt(i, 0)+"\"" + ",";
				}
				if (baseTable.getValueAt(i, 6) != null) {   //修改
					TableColumnsBean coln = map.get(baseTable.getValueAt(i, 6).toString());
					if(    !coln.getTypeName().trim().equals(baseTable.getValueAt(i, 1).toString())    			//类型名字
						|| !coln.getDecimals().trim().equals(baseTable.getValueAt(i, 3).toString())    			//小数
						|| !coln.getColumnSize().trim().equals( baseTable.getValueAt(i, 2).toString() )   		//长度
						|| coln.isNull() != Boolean.parseBoolean(baseTable.getValueAt(i, 4).toString())			//是否为空
						|| coln.isPrimaryKey() != Boolean.parseBoolean(baseTable.getValueAt(i, 5).toString())   //主键
						|| !coln.getColdefault().equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 7)))   	//默认值
					){
						if (coln.isPrimaryKey() != Boolean.parseBoolean(baseTable.getValueAt(i, 5).toString())) {
							if ((coln.isPrimaryKey() && "".equals(dropkey)) || iskeys) {
								if ("".equals(dropkey)) {
									alter.append(NEW_LINE).append(ALTER_TABLE).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(SPACE);
									dropkey = DROP_CONSTRAINT + oldkeyname + SEMI_COLON;
									alter.append(dropkey);
								}
							}
							iskey = true;
						}
						//是否为空
						if (coln.isNull() != Boolean.parseBoolean(baseTable.getValueAt(i, 4).toString())) {
							alter.append(NEW_LINE).append(ALTER_TABLE).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(SPACE);
							if (Boolean.parseBoolean(baseTable.getValueAt(i, 4).toString())) {
								alter.append(ALTER_COLUMN).append("\""+coln.getColumnName().trim()+"\"").append(SET).append(NOT_NULL).append(SEMI_COLON);
							} else {
								alter.append(ALTER_COLUMN).append("\""+coln.getColumnName().trim()+"\"").append(DROP).append(NOT_NULL).append(SEMI_COLON);
							}
						}
						//字段属性
						if ( !coln.getTypeName().trim().equals(baseTable.getValueAt(i, 1).toString())    //类型名字
							 || !coln.getColdefault().equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 7)))   //默认值
							 || !coln.getDecimals().trim().equals(baseTable.getValueAt(i, 3).toString())    //小数
							 || !coln.getColumnSize().trim().equals(baseTable.getValueAt(i, 2).toString())   //长度
						){
							String datatype = baseTable.getValueAt(i, 1).toString().toUpperCase();
							alter.append(NEW_LINE).append(ALTER_TABLE).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(SPACE);
							alter.append(ALTER_COLUMN).append("\""+coln.getColumnName().trim()+"\"").append(TYPE);
							if ( (datatype.equals("CHAR") || datatype.equals("VARCHAR") || datatype.equals("BPCHAR") || datatype.equals("CHARACTER VARYING") || datatype.equals("CHARACTER")) && !"".equals(baseTable.getValueAt(i, 2)) && baseTable.getValueAt(i, 2) != null ){   
								alter.append(datatype).append(B_OPEN).append(baseTable.getValueAt(i, 2)).append(B_CLOSE).append(SEMI_COLON);  //整数长度
							} else if (datatype.equals("NUMERIC")) {    //有小数的长度
								if (!"".equals(baseTable.getValueAt(i, 2)) && baseTable.getValueAt(i, 2) != null) {
									if (!"".equals(baseTable.getValueAt(i, 3)) && baseTable.getValueAt(i, 3) != null) {
										alter.append(datatype).append(B_OPEN).append(baseTable.getValueAt(i, 2)).append(COMMA).append(baseTable.getValueAt(i, 3)).append(B_CLOSE).append(SEMI_COLON);
									} else {
										alter.append(datatype).append(B_OPEN).append(baseTable.getValueAt(i, 2)).append(COMMA).append(0).append(B_CLOSE).append(SEMI_COLON);
									}
								}
							}else{  //不需要设置长度的
								alter.append(datatype).append(SEMI_COLON);
							}
							//默认值
							if (!coln.getColdefault().equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 7)))) {
								alter.append(NEW_LINE).append(ALTER_TABLE).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(SPACE);
								alter.append(ALTER_COLUMN).append("\""+coln.getColumnName().trim()+"\"").append(" SET DEFAULT ").append("'" + baseTable.getValueAt(i, 7) + "'").append(SEMI_COLON);
							}
						}
					}
					// 字段名不一样，组装修改字段名称语句
					if (!coln.getColumnName().trim().equals(baseTable.getValueAt(i, 0).toString())) {
						altername.append(NEW_LINE).append(ALTER_TABLE).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(SPACE);
						altername.append(RENAME).append("\""+coln.getColumnName().trim()+"\"").append(TO).append("\""+baseTable.getValueAt(i, 0).toString()+"\"").append(SEMI_COLON);
					}
					//注释
					if (!coln.getComment().equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 8)))) {
						coldefault.append(NEW_LINE).append(" COMMENT ON COLUMN ").append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(DOT)
								.append(baseTable.getValueAt(i, 0)).append(" IS ").append("'" + baseTable.getValueAt(i, 8) + "'").append(SEMI_COLON);
					}
				} else {   //添加列
					alter.append(NEW_LINE).append(ALTER_TABLE).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(SPACE);
					alter.append(ADD_COLUMN).append(SPACE).append(baseTable.getValueAt(i, 0)).append(SPACE);
					//类型设置
					if (baseTable.getValueAt(i, 1) != null) {
						String datatype = baseTable.getValueAt(i, 1).toString().toUpperCase();
						if ((datatype.equals("CHAR") || datatype.equals("VARCHAR") || datatype.equals("BPCHAR") || datatype.equals("CHARACTER VARYING") || datatype.equals("CHARACTER")) && !"".equals(baseTable.getValueAt(i, 2)) && baseTable.getValueAt(i, 2) != null) {
							alter.append(datatype).append(B_OPEN).append(baseTable.getValueAt(i, 2)).append(B_CLOSE).append(SEMI_COLON);
						} else if (datatype.equals("NUMERIC")) {
							if (!"".equals(baseTable.getValueAt(i, 2)) && baseTable.getValueAt(i, 2) != null) {
								if (!"".equals(baseTable.getValueAt(i, 3)) && baseTable.getValueAt(i, 3) != null) {
									alter.append(datatype).append(B_OPEN).append(baseTable.getValueAt(i, 2)).append(COMMA).append(baseTable.getValueAt(i, 3)).append(B_CLOSE).append(SEMI_COLON);
								} else {
									alter.append(datatype).append(B_OPEN).append(baseTable.getValueAt(i, 2)).append(COMMA).append(0).append(B_CLOSE).append(SEMI_COLON);
								}
							}
						}else{
							alter.append(datatype).append(SEMI_COLON);
						}
					}
					if (!"".equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 7)))) {   //默认值
						alter.append(NEW_LINE).append(ALTER_TABLE).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(SPACE);
						alter.append(ALTER_COLUMN).append(baseTable.getValueAt(i, 0)).append(" SET DEFAULT ").append("'" + baseTable.getValueAt(i, 7) + "'").append(SEMI_COLON);
					}
					if (!"".equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 8)))) {
						coldefault.append(NEW_LINE).append(" COMMENT ON COLUMN ").append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(DOT)
								.append(baseTable.getValueAt(i, 0)).append(" IS ").append("'" + baseTable.getValueAt(i, 8) + "'").append(SEMI_COLON);
					}
					
					if (Boolean.parseBoolean(baseTable.getValueAt(i, 5).toString())) {
						iskey = true;
					}
				}
				sqlBuffer.append(alter);
				sqlBuffer.append(altername);
			}else{   //新建
				if (null != tabp.getTableName() && !tabp.getTableName().equals("") && oldkeyname.equals("")) {
					keyname = tabp.getTableName() + "_pkey";
				}
				sqlText.append(NEW_LINE).append(SPACE).append(tablePanel.getBaseTable().getValueAt(i, 0) == null ? EMPTY : tablePanel.getBaseTable().getValueAt(i, 0)).append(SPACE);
				if (baseTable.getValueAt(i, 1) != null) {
					sqlText.append(baseTable.getValueAt(i, 1).toString().toUpperCase());
					String datatype = baseTable.getValueAt(i, 1).toString().toUpperCase();
					if ((datatype.equals("CHAR") || datatype.equals("VARCHAR") || datatype.equals("BPCHAR") || datatype.equals("CHARACTER VARYING") || datatype.equals("CHARACTER")) && !"".equals(baseTable.getValueAt(i, 2)) && baseTable.getValueAt(i, 2) != null) {
						sqlText.append(B_OPEN).append(baseTable.getValueAt(i, 2)).append(B_CLOSE);
					} else if (datatype.equals("NUMERIC")) {
						if (!"".equals(baseTable.getValueAt(i, 2)) && baseTable.getValueAt(i, 2) != null) {
							if (!"".equals(baseTable.getValueAt(i, 3)) && baseTable.getValueAt(i, 3) != null) {
								sqlText.append(B_OPEN).append(baseTable.getValueAt(i, 2)).append(COMMA).append(baseTable.getValueAt(i, 3)).append(B_CLOSE);
							} else {
								sqlText.append(B_OPEN).append(baseTable.getValueAt(i, 2)).append(COMMA).append(0).append(B_CLOSE);
							}
						}
					}
				}
				if (!"".equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 9)))) {
					sqlText.append(SPACE).append(" COLLATE ").append(baseTable.getValueAt(i, 9));
				}
				if (!"".equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 7)))) {
					sqlText.append(SPACE).append(" DEFAULT ").append("'" + baseTable.getValueAt(i, 7) + "'");
				}
				sqlText.append(SPACE).append(baseTable.getValueAt(i, 4).toString().equals("true") ? NOT_NULL : EMPTY);
				if (i != rows - 1) {
					sqlText.append(COMMA);
				}
				if (Boolean.parseBoolean(baseTable.getValueAt(i, 5).toString())) {
					parkey += baseTable.getValueAt(i, 0) + ",";
				}
				if (!"".equals(CommonsHelper.nullOfStr(baseTable.getValueAt(i, 8)))) {
					coldefault.append(NEW_LINE).append(" COMMENT ON COLUMN ").append("\""+tabp.getSchemaName()+"\"").append(DOT)
							.append("\""+tabp.getTableName()+"\"").append(DOT).append(baseTable.getValueAt(i, 0)).append(" IS").append(SPACE)
							.append("'" + baseTable.getValueAt(i, 8) + "'").append(SEMI_COLON);
				}
			}
		}
		if(tabp.getSign()){
			if (parkey != "(") { //组装主键
				sqlText.append(COMMA).append(NEW_LINE).append(CONSTRAINT).append(keyname).append(PRIMARY).append(KEY).append(parkey.substring(0, parkey.length() - 1) + ")");
			} 
			//组装建表语句
			sqlBuffer.append(NEW_LINE).append(CREATE_TABLE);
			sqlBuffer.append("\""+tabp.getSchemaName()+"\"").append(DOT);
			sqlBuffer.append("\""+tabp.getTableName()+"\"").append(SPACE).append(B_OPEN);
			sqlBuffer.append(sqlText.toString());
			sqlBuffer.append(NEW_LINE);
			sqlBuffer.append(B_CLOSE).append(SEMI_COLON);
		}else{
			if (iskey) {  //组装主键 
				if (parkey != "(") {
					if ("".equals(dropkey) && !oldkeyname.equals("")) {   //只修改了键名称
						sqlBuffer.append(NEW_LINE).append(ALTER_TABLE).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"").append(SPACE);
						sqlBuffer.append(DROP_CONSTRAINT + oldkeyname + SEMI_COLON);
					}
					sqlBuffer.append(NEW_LINE).append(ALTER_TABLE).append("\""+tabp.getSchemaName()+"\".\""+tabp.getTableName()+"\"")
					.append(" ADD CONSTRAINT ").append(keyname).append(PRIMARY).append(KEY).append(parkey.substring(0, parkey.length() - 1) + ")").append(SEMI_COLON);
					
				}
			}
		}
		sqlBuffer.append(coldefault);
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
			mr.setBackground(new Color(220, 255, 220));
			zs.setBackground(new Color(220, 255, 220));
			mr.setEditable(true);
			zs.setEditable(true);
		}else{
			mr.setEditable(false);
			zs.setEditable(false);
		}
	}
	
	public BaseTable getBaseTable() {
		return baseTable;
	}

	public void setBaseTable(BaseTable baseTable) {
		this.baseTable = baseTable;
	}

	public JTextField getMr() {
		return mr;
	}

	public void setMr(JTextField mr) {
		this.mr = mr;
	}

	public JTextField getZs() {
		return zs;
	}

	public void setZs(JTextField zs) {
		this.zs = zs;
	}
}
