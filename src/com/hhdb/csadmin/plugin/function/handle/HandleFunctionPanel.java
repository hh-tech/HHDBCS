package com.hhdb.csadmin.plugin.function.handle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.swingcontrol.displayTable.TablePanelUtil;
import com.hh.frame.swingui.swingcontrol.displayTable.basis.BaseTable;
import com.hhdb.csadmin.common.ui.textEdit.QueryEditorUi2;
import com.hhdb.csadmin.common.util.HHSqlUtil;
import com.hhdb.csadmin.common.util.HHSqlUtil.ITEM_TYPE;
import com.hhdb.csadmin.plugin.function.FunctionPanel;
import com.hhdb.csadmin.plugin.function.FunctionTab;
import com.hhdb.csadmin.plugin.function.component.ComboBoxCellEditor;
import com.hhdb.csadmin.plugin.function.component.CreateTableSQLSyntax;
import com.hhdb.csadmin.plugin.function.component.TextCellEditor;

/**
 * @Description: 创建函数面板
 * @date: 2017年11月6日
 * @Company: H2 Technology
 * @author: Liziyan
 * @version 1.0
 */
public class HandleFunctionPanel extends JPanel implements CreateTableSQLSyntax, TableModelListener {
	private static final long serialVersionUID = 2805619451637353405L;

	private String schemaName;
	private String functionName;

	private BaseTable table;
	private TablePanelUtil tablePanel;
	private TextCellEditor textcell;
	private ComboBoxCellEditor comboBoxcell;
	private ComboBoxCellEditor typecell;
	private ComboBoxCellEditor fh;
	private ComboBoxCellEditor yy;
	private boolean isEdit;
	private FunctionTab functionTab;
	private List<String> lists = new ArrayList<String>();
	public QueryEditorUi2 jtp;			//sql编辑面板
	public FunctionPanel fp;
	

	public HandleFunctionPanel(FunctionTab functionTab, String schemaName, String functionName, boolean isEdit,FunctionPanel fp) {
		this.functionTab = functionTab;
		this.schemaName = schemaName;
		this.isEdit = isEdit;
		this.fp = fp;
		//获取sql编辑面板
		jtp = new QueryEditorUi2();
		getKeyName();

		if (isEdit) {
			setLayout(new BorderLayout());
			add(jtp.getContentPane());
			this.functionName = functionName;
		} else {
			lists.add("参数名称");
			lists.add("模式");
			lists.add("数据类型");
			setBackground(Color.WHITE);
			tablePanel = new TablePanelUtil(true, lists, null, false, false);
			tablePanel.setPreferredSize(new Dimension(380, 150));
			table = tablePanel.getBaseTable();
			table.setBackground(new Color(220, 255, 220));
			
			addRow();
			initCellEditor();
			setColumnWidth(new int[] { 1, 2 });
			setLayout(new GridBagLayout());
			fh = new ComboBoxCellEditor();
			fh.addItem("void");
			getDataTypes(fh);
			yy = new ComboBoxCellEditor();
			yy.addItem("plhhsql");
			jtp.getContentPane().setBorder(BorderFactory.createLineBorder(new Color(172, 173, 179), 1));
			jtp.getContentPane().setPreferredSize(new Dimension(580, 300));
			add(tablePanel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(0, 70, 0, 0), 0, 0));
			add(new JLabel("返回:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
			add(fh, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					new Insets(20, 30, 0, 0), 0, 0));
			add(new JLabel("语言:"), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
			add(yy, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					new Insets(20, 30, 0, 0), 0, 0));
			add(new JLabel("定义:"), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
			add(jtp.getContentPane(), new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE, new Insets(20, 30, 0, 0), 0, 0));
			JPanel jpl = new JPanel();
			jpl.setBackground(Color.WHITE);
			add(jpl, new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			table.addKeyListener(SaveFuntionKey());
			jtp.getContentPane().requestFocusInWindow();
			fh.addKeyListener(SaveFuntionKey());
			yy.addKeyListener(SaveFuntionKey());
			jtp.getContentPane().addKeyListener(SaveFuntionKey());
		}
	}
	
	/**
	 * 快捷键保存
	 * @return
	 */
	private KeyListener SaveFuntionKey() {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
					boolean flag = saveFunction();
					fp.toolBar.getComponentAtIndex(0).setEnabled(flag);
				}
			}

		};
	}

	/**
	 * 初始化单元格控件
	 */
	public void initCellEditor() {
		textcell = new TextCellEditor();
		comboBoxcell = new ComboBoxCellEditor();
		typecell = new ComboBoxCellEditor();
		// 将数据类型加载到comboBox
		getDataTypes(typecell);
		getIndexTypes();
		KeyAdapter valueKeyListener = new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				functionChanged();
			}
		};
		textcell.addKeyListener(valueKeyListener);
		tablePanel.getTableDataModel().addTableModelListener(this);
		table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(textcell));
		table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(comboBoxcell));
		table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(typecell));
	}

	/**
	 * 设置表格列的宽度
	 * 
	 * @param column
	 * @param table
	 */
	private void setColumnWidth(int[] column) {
		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn firsetColumn = table.getColumnModel().getColumn(i);
			firsetColumn.setPreferredWidth(60);
			firsetColumn.setMaxWidth(60);
			if (i == column[0] || i == column[1]) {
				firsetColumn.setPreferredWidth(160);
				firsetColumn.setMaxWidth(160);
			}
			firsetColumn.setMinWidth(30);
		}
	}

	/**
	 * 模式类型
	 */
	private void getIndexTypes() {
		String[] indexs = new String[] { "IN", "OUT", "INOUT" };
		for (String index : indexs) {
			comboBoxcell.addItem(index);
		}
	}

	/**
	 * 获取数据类型
	 */
	private void getDataTypes(ComboBoxCellEditor cell) {
		String datatypeSql = HHSqlUtil.getSqlBean(ITEM_TYPE.DATASERVICE, "datatypelist").getSql();
		List<Map<String, Object>> list = null;
		try {
			list = functionTab.sosi.getListMap(datatypeSql);
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		}
		for (Map<String, Object> map : list) {
			if (!"\"char\"".equals(map.get("tn").toString())) {
				cell.addItem(map.get("tn").toString());
			}
		}

		cell.addItem("record");
	}

	/**
	 * 组装新建函数数据为sql语句
	 * 
	 * @param row
	 * @param col
	 */
	public String functionChanged() {
		StringBuffer sqlText = new StringBuffer();
		
		if(isEdit){
			sqlText.append(jtp.getText());
		}else{
			int rows = table.getRowCount();
			sqlText.append(CREATE_FUNCTION).append("\""+schemaName+"\"").append(".").append("\""+functionName+"\"").append(B_OPEN);
			for (int i = 0; i < rows; i++) {
				if (table.getValueAt(i, 1) != null) {
					sqlText.append(table.getValueAt(i, 1)).append(SPACE);
				}
				if (table.getValueAt(i, 0) != null) {
					sqlText.append(table.getValueAt(i, 0)).append(SPACE);
				}
				if (table.getValueAt(i, 2) != null) {
					sqlText.append(table.getValueAt(i, 2));
				}
				if (i != rows - 1) {
					sqlText.append(COMMA);
				}
			}
			sqlText.append(B_CLOSE);
			sqlText.append(RETURNS).append(fh.getValue()).append(AS).append(BODY);
			sqlText.append(NEW_LINE).append(jtp.getText());
			sqlText.append(NEW_LINE).append(BODY);
			sqlText.append(NEW_LINE).append(LANGUAGE);
			if (yy.getValue().equals("sql")) {
				sqlText.append("'").append(yy.getValue()).append("'");
			} else {
				sqlText.append(yy.getValue());
			}
			sqlText.append(VOLATILE).append(SEMI_COLON);
		}
		return sqlText.toString();
	}

	/**
	 * 保存修改函数
	 */
	public boolean saveFunction() {
		try {
			if (isEdit) {  //修改
				System.out.println("修改");
			} else {	//保存
				functionName = (String) JOptionPane.showInputDialog(new JFrame(), "输入函数名", "函数名", JOptionPane.PLAIN_MESSAGE, null, null, functionName);
				if (functionName == null) {  //取消
					return true;
				}else if(functionName.equals("")){ 
					JOptionPane.showMessageDialog(this, "函数名称不能为空");
					return true;
				}
			}
			String functionSql = functionChanged();
			try {
				functionTab.sosi.sqlOperation(functionSql);
				functionTab.sosi.refresh();
				JOptionPane.showMessageDialog(this, "保存函数成功");
				return false;
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null,"错误信息：" + e.getMessage(), "错误",JOptionPane.ERROR_MESSAGE);
				return true;
			}
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(this, "请新建函数！", "提示", JOptionPane.ERROR_MESSAGE);
			return true;
		}
	}
	
	/**
	 * 删除一行
	 */
	public void delRow() {
		int row = table.getSelectedRow();
		if (row != -1) {
			int result = JOptionPane.showConfirmDialog(null, "是否删除当前行", "提示信息", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				tablePanel.getTableDataModel().removeRow(row);
			}
		}
	}

	/**
	 * 添加一行
	 */
	public void addRow() {
		Object[] object = new Object[] { null, null, null, null };
		tablePanel.getTableDataModel().addRow(object);
	}


	public BaseTable getBaseTable() {
		return table;
	}
	
	/**
	 * 设置表名视图名提示关键词
	 */
	public void getKeyName() {
		List<Map<String, Object>> list = new ArrayList<>();
		List<String> lis = new ArrayList<>();
		try {
			// 表名
			list = functionTab.sosi.getNameByType(HHSqlUtil.ITEM_TYPE.TABLE, "prop_coll"); 
			for (Map<String, Object> maps : list) {
				lis.add(maps.get("name").toString());
			}
			jtp.setTableCompletionProvider(lis);
			// 视图
			lis.clear();
			list = functionTab.sosi.getNameByType(HHSqlUtil.ITEM_TYPE.VIEW, "prop_coll");
			for (Map<String, Object> maps : list) {
				lis.add(maps.get("name").toString());
			}
			jtp.setViewCompletionProvider(lis);
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(null, e.getMessage(), "提示",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
	}
}
