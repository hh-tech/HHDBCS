package com.hhdb.csadmin.plugin.type_create.handle;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hh.frame.swingui.swingcontrol.displayTable.TablePanelUtil;
import com.hh.frame.swingui.swingcontrol.displayTable.basis.BaseTable;
import com.hhdb.csadmin.common.util.HHSqlUtil;
import com.hhdb.csadmin.common.util.HHSqlUtil.ITEM_TYPE;
import com.hhdb.csadmin.plugin.type_create.TypeCreate;
import com.hhdb.csadmin.plugin.type_create.component.ComboBoxCellEditor;
import com.hhdb.csadmin.plugin.type_create.component.TextCellEditor;


/**
 * @Description: 创建类型
 * @date: 2017年12月8日
 * @Company: H2 Technology
 * @author: lidongjiao
 * @version 1.0
 */
public class TypesPaneHandle extends JPanel{
	private static final long serialVersionUID = 1L;
	private String schemaName ;
	private TypeCreate typeCreate;
	private JPanel jPanel;
	private String flagName = "basic";
	private ComboBoxCellEditor propertyBox;//属性选择 下拉组件
	private ComboBoxCellEditor basicTypeBox;//基本类型 下拉组件
	private JTextField defaultValue = new JTextField(20);//默认值;
	private JCheckBox isNull = new JCheckBox("非空");// 是否为空(复选框)
	private ComboBoxCellEditor isCheck=new ComboBoxCellEditor();//检查;
	private BaseTable baseTable;
	private TablePanelUtil tablePanel;
	private List<String> lists;//表头
	private JTextField typeName;//类型名称
	
	private TextCellEditor textcellEnum=new TextCellEditor();//enum面板的单元格
	private ComboBoxCellEditor comboBoxcell = new ComboBoxCellEditor();//composite的数据类型下拉框
	private TextCellEditor textcellComposite  = new TextCellEditor();//composite面板的单元格
	public TypesPaneHandle(){}
	public TypesPaneHandle(TypeCreate typeCreate,String schemaName){
		//填充检查
		isCheck.addItem("");
		isCheck.addItem("false");
		isCheck.addItem("true");
		this.schemaName = schemaName;
		this.typeCreate = typeCreate;
		setBackground(Color.WHITE);
		setLayout(new GridBagLayout());
		getDataTypes();//填充基本类型
		propertyBox=new ComboBoxCellEditor();
		//填充属性类型
		propertyBox.addItem("basic");
		propertyBox.addItem("composite");
		propertyBox.addItem("enum");
		JLabel label=new JLabel("类型名称:");
		label.setPreferredSize(new Dimension(80,20));
		typeName = new JTextField(20);//类型名称
		add(label, new GridBagConstraints(0 , 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		add(typeName, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 30, 0, 0), 0, 0));
		add(new JLabel("属性选择:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		add(propertyBox, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 30, 0, 0), 48, 0));
		jPanel = new JPanel();
		jPanel.setLayout(new GridBagLayout());
		jPanel.setBackground(Color.WHITE);
		add(jPanel, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		//进入basic面板
		editPanelBasic();
		JPanel jpl = new JPanel();
		jpl.setBackground(Color.WHITE);
		add(jpl, new GridBagConstraints(0, 10, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		propertyBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(1 == e.getStateChange()){
					jPanel.setVisible(false);
					remove(jPanel);
					jPanel = new JPanel();
					jPanel.setLayout(new GridBagLayout());
					jPanel.setBackground(Color.WHITE);
					add(jPanel, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					if("basic".equals(e.getItem())){
						editPanelBasic();
					}else if("composite".equals(e.getItem())){
						editPanelComposite();
					}else if("enum".equals(e.getItem())){
						editPanelEnum();
					}
					flagName = (String)e.getItem();
				}
			}
		});
	}
	
	/**
	 * 进入basic面板
	 */
	public void editPanelBasic(){
		JLabel label2=new JLabel("基本类型:");
		label2.setPreferredSize(new Dimension(80,20));
		jPanel.add(label2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		jPanel.add(basicTypeBox, new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 30, 0, 0), -60, 0));
		jPanel.add(new JLabel("默认值:"), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		jPanel.add(defaultValue, new GridBagConstraints(GridBagConstraints.RELATIVE, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 30, 0, 0), 0, 0));
		jPanel.add(isNull, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		jPanel.add(new JLabel("检查:"), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		jPanel.add(isCheck, new GridBagConstraints(GridBagConstraints.RELATIVE, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 30, 0, 0), 70, 0));
	}
	
	/**
	 * 进入enum面板
	 */
	public void editPanelEnum(){
		JButton jb = new JButton("新增枚举值");
		JButton jb2 = new JButton("删除枚举值");
		jPanel.add(jb, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		jPanel.add(jb2, new GridBagConstraints(1,0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		jb.addMouseListener(new MouseAdapter() {
			@Override
	        public void mouseClicked(MouseEvent e) {
				addRows();
	        }
		});
		jb2.addMouseListener(new MouseAdapter() {
			@Override
	        public void mouseClicked(MouseEvent e) {
				delRow();
	        }
		});
		
		lists=new ArrayList<String>();
		lists.add("值");
		tablePanel = new TablePanelUtil(true);
		initTablePanel();
		jPanel.add(tablePanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		baseTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(textcellEnum));
		
	}
	
	/**
	 * 进入composite面板
	 */
	public void editPanelComposite(){
		JButton jb = new JButton("新增属性");
		jPanel.add(jb, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		JButton jb2 = new JButton("删除属性");
		jPanel.add(jb2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));

		jb.addMouseListener(new MouseAdapter() {
			@Override
	        public void mouseClicked(MouseEvent e) {
				addRows();
	        }
		});
		jb2.addMouseListener(new MouseAdapter() {
			@Override
	        public void mouseClicked(MouseEvent e) {
				delRow();
	        }
		});
		lists=new ArrayList<String>();
		lists.add("名称");
		lists.add("数据类型");
		tablePanel = new TablePanelUtil(true);
		initTablePanel();
		jPanel.add(tablePanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		// 将数据类型加载到comboBox
		comboBoxcell=basicTypeBox;
		baseTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(textcellComposite));
		baseTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(comboBoxcell));
	}
	
	/**
	 * 初始化表格面板
	 */
	public void initTablePanel(){
		baseTable = tablePanel.getBaseTable();
//		baseTable.setBackground(new Color(220, 255, 220));// 表格单元格背景颜色
		tablePanel.setPreferredSize(new Dimension(600, 400));
		tablePanel.setBackground(Color.WHITE);
		tablePanel.getViewport().setBackground(Color.WHITE);
		tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		tablePanel.setData(lists, null);
		addRows();
		
	}
	
	
	/**
	 * 数据类型
	 */
	@SuppressWarnings("unchecked")
	private void getDataTypes() {
		basicTypeBox = new ComboBoxCellEditor();
		basicTypeBox.addItem("");
		String datatypeSql = HHSqlUtil.getSqlBean(ITEM_TYPE.DATASERVICE, "datatypelist").getSql();
		String toID = "com.hhdb.csadmin.plugin.conn";
		CmdEvent executeEvent = new CmdEvent(typeCreate.PLUGIN_ID, toID, "ExecuteListMapBySqlEvent");
		executeEvent.addProp("sql_str", datatypeSql);
		HHEvent event = typeCreate.sendEvent(executeEvent);
		List<Map<String, Object>> typeLists = null;
		try {
			typeLists = (List<Map<String, Object>>) event.getObj();
		} catch (Exception e) {
			
			LM.error(LM.Model.CS.name(), e);
		}
		for (Map<String, Object> map : typeLists) {
			if (!"serial".equals(map.get("tn").toString()) && !"\"char\"".equals(map.get("tn").toString())) {
				basicTypeBox.addItem(map.get("tn").toString());
			} else if (!"\"char\"".equals(map.get("tn").toString())) {
				basicTypeBox.addItem(map.get("tn").toString());
			}
		}
	}
	
	/**
	 * 添加一行
	 */
	public void addRows() {
		if(propertyBox.getSelectedItem().equals("enum")){
			Object[] object = new Object[] { ""};
			tablePanel.getTableDataModel().addRow(object);
		}else if(propertyBox.getSelectedItem().equals("composite")){
			Object[] object = new Object[] { "",null};
			tablePanel.getTableDataModel().addRow(object);
		}
	}
	
	/**
	 * 删除一行
	 */
	public void delRow() {
		int row = baseTable.getSelectedRow();
		if(row!=-1){
		int result=JOptionPane.showConfirmDialog(null, "是否删除成员","提示信息",JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION){
				tablePanel.getTableDataModel().removeRow(row);
			}
		}else{
			String message="";
			if(propertyBox.getSelectedItem().equals("enum")){
				message="请选择枚举值";
			}else if(propertyBox.getSelectedItem().equals("composite")){
				message="请选择属性";
			}
			JOptionPane.showMessageDialog(null, message);
		}
	}
	
	
	/**
	 * 保存类型
	 * @return
	 */
	public boolean saveType() {
		String name = typeName.getText();
		if("".equals(name) || null == name ){
			JOptionPane.showMessageDialog(null, "类型名称不能为空");
			return false;
		}
	    if (baseTable!=null&&baseTable.isEditing()) {
			baseTable.getCellEditor().stopCellEditing();
		}
		int result=JOptionPane.showConfirmDialog(null, "是否保存", "类型名", JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION){
			if("enum".equals(flagName)){
				String textValue = textcellEnum.getEditorValue();
				if("".equals(textValue)){
					JOptionPane.showMessageDialog(null, "值不能为空");
					return false;
				}
			}else if("composite".equals(flagName)){
				String textValue = textcellComposite.getEditorValue();
				String boxValue = comboBoxcell.getValue();
				if("".equals(textValue)){
					JOptionPane.showMessageDialog(null, "名称不能为空");
					return false;
				}else if("".equals(boxValue)){
					JOptionPane.showMessageDialog(null, "数据类型不能为空");
					return false;
				}
			}else{
				String  property = basicTypeBox.getStringValue();
				if(property.equals("")){
					JOptionPane.showMessageDialog(null, "基本类型不能为空");
					return false;
				}
			}
			   // 获取数据表创建语句
				String typeSql = createTypeSql();
			try {
				CmdEvent getcfEvent = new CmdEvent(typeCreate.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "ExecuteUpdateBySqlEvent");
				getcfEvent.addProp("sql_str", typeSql);
				HHEvent revent = typeCreate.sendEvent(getcfEvent);
				if(revent instanceof ErrorEvent){
					JOptionPane.showMessageDialog(null, ((ErrorEvent) revent).getErrorMessage(), "提示",JOptionPane.ERROR_MESSAGE);
					return false;
				}
				textcellComposite.setValue("");
				textcellEnum.setValue("");
				typeName.setText("");
				isNull.setSelected(false);
				defaultValue.setText("");
				isCheck.setSelectedIndex(0);
				basicTypeBox.setSelectedIndex(0);
				propertyBox.setSelectedIndex(0);
			} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "类型创建失败！"+e.getMessage(), "提示",JOptionPane.ERROR_MESSAGE);
					return false;
			}
			   return true;
		}
		      return false;
	}
	
	
	/**
	 * sql预览监听
	 * @param e
	 */
	public String createTypeSql(){
//		 Rectangle rect = tab.getBoundsAt(tab.getTabCount()-1); //拿到标签的边界
		//取消表格编辑状态
		if (baseTable!=null&&baseTable.isEditing()) {
			baseTable.getCellEditor().stopCellEditing();
		 }
//         if (rect.contains(e.getX(), e.getY())) { //判断是否点在边界内
        	 String nx = typeName.getText();
			   if(nx.isEmpty()){
					nx ="NewType";
				}
			   String sql ="";
        	 if("basic".equals(flagName)){
        		Boolean orNull =isNull.isSelected();
				String mr = defaultValue.getText();
				String jc = isCheck.getValue();//checkText.getText();
				sql = "CREATE DOMAIN \""+schemaName+"\".\""+nx+"\" AS "+basicTypeBox.getValue()+" ";
				if (!mr.isEmpty()) {
					sql += " DEFAULT '" + mr+"'";
				}
				if (orNull) {
					sql += " NOT NULL";
				}
				if (!jc.isEmpty()) {
					sql += " CHECK " + "("+ jc +")";
				}
        	}else if("enum".equals(flagName)){
				sql = "CREATE TYPE \""+schemaName+"\".\""+nx+"\" AS ENUM ";
				int rows = tablePanel.getBaseTable().getRowCount();
				String enumcon="";
					for (int i = 0; i < rows; i++) {
						String ss=baseTable.getValueAt(i, 0).toString();
						if(!"".equals(ss)){
							enumcon+="'"+ss+"',";
						}
					}
				  if(!enumcon.equals("")){
					  sql += "("+enumcon.substring(0,enumcon.length()-1)+")";
				  }
				
        	}else if("composite".equals(flagName)){
				sql = "CREATE TYPE \""+schemaName+"\".\""+nx+"\" ";
				int rows = tablePanel.getBaseTable().getRowCount();
				String compcon="";
				for (int i = 0; i < rows; i++) {
					Object valueAt = baseTable.getValueAt(i, 0);
					Object valueAt2 = baseTable.getValueAt(i, 1);
					String col1="";
					String col2="";
					if((valueAt!=null&&valueAt2!=null)){
						 col1=baseTable.getValueAt(i, 0).toString();
					     col2=baseTable.getValueAt(i, 1).toString();
					}
					if(!"".equals(col1)&&!"".equals(col2)){
						compcon+="\""+col1+"\" "+col2+",";
					}
				}
				if(!compcon.equals("")){
					sql += " AS ("+compcon.substring(0,compcon.length()-1)+") ";
				}
        	}
        	 return sql;
        }

	 
	
}
