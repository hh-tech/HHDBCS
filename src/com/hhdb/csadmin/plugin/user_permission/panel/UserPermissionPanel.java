package com.hhdb.csadmin.plugin.user_permission.panel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.hh.frame.swingui.swingcontrol.displayTable.TablePanelUtil;
import com.hh.frame.swingui.swingcontrol.displayTable.basis.BaseTable;
import com.hhdb.csadmin.common.util.IconUtilities;
import com.hhdb.csadmin.plugin.user_permission.UserPermission;
import com.hhdb.csadmin.plugin.user_permission.UI.BaseButton;
import com.hhdb.csadmin.plugin.user_permission.UI.BaseToolBar;
import com.hhdb.csadmin.plugin.user_permission.UI.CheckBoxCellEditor;
import com.hhdb.csadmin.plugin.user_permission.UI.CheckBoxRender;
import com.hhdb.csadmin.plugin.user_permission.service.UPservice;
public class UserPermissionPanel  extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
    private UPservice service;
	private BaseToolBar toolBar = new BaseToolBar();
	private TablePanelUtil tablePanel;
	private BaseTable baseTable;
	private boolean flag;
	private static List<String> lists=new ArrayList<String>();
	static{
		lists.add("用户");
		lists.add("数据库");
		lists.add("权限");
	}
	public  UserPermissionPanel(UserPermission up, String userName){
		service=new UPservice(up);
		setLayout(new GridBagLayout());
		this.setBackground(Color.WHITE);
		tablePanel = new TablePanelUtil(new int[]{0,1});
		baseTable = tablePanel.getBaseTable();
		baseTable.setBackground(Color.WHITE);
		tablePanel.setPreferredSize(new Dimension(400, 400));
		tablePanel.getViewport().setBackground(Color.WHITE);
		tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		createBtn("保存", IconUtilities.loadIcon("save.png"), "save");
		toolBar.setFloatable(false);
		tablePanel.setData(lists,getInitData(userName));
		initCellEditor();
		//工具条
		add(toolBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				//签页
		add(tablePanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST , GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
	}
   
	/**
	 * 创建按钮
	 * 
	 * @param text
	 * @param icon
	 * @param comand
	 */
	private void createBtn(String text, Icon icon, String comand) {
		BaseButton basebtn = new BaseButton(text, icon);
		basebtn.setActionCommand(comand);
		basebtn.addActionListener(this);
		toolBar.add(basebtn);
	}
	
    /**
    * 获得所有的数据库
    * @return
    */
	private List<Map<String, Object>> getInitData(String userName){
		List<Map<String, Object>> li=new ArrayList<Map<String, Object>>();
		Set<String> dbNames = service.getDbName();
		for (String dbName : dbNames) {
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("用户", userName);
			map.put("数据库", dbName);
			map.put("权限", service.isPermission(userName,dbName));
			li.add(map);
		}
		return li;
	}

	/**
	 * 初始化单元格控件
	 */
	public void initCellEditor() {
		CheckBoxCellEditor checkboxcell = new CheckBoxCellEditor();
		CheckBoxRender checkboxRenderer = new CheckBoxRender();
		baseTable.getColumnModel().getColumn(2).setCellEditor(checkboxcell);
		baseTable.getColumnModel().getColumn(2).setCellRenderer(checkboxRenderer);
	}

    /****
     * 点击按钮保存
     */
	@Override
	public void actionPerformed(ActionEvent e) {
	       String command = e.getActionCommand();
			if(command.equals("save")){
				if (baseTable.isEditing()) {
					baseTable.getCellEditor().stopCellEditing();
				}
				int rows = baseTable.getRowCount();
				for (int i = 0; i < rows; i++){
					String userName=baseTable.getValueAt(i, 0).toString();
					String datName=baseTable.getValueAt(i, 1).toString();
					String premssion=baseTable.getValueAt(i, 2).toString();
					if(premssion.equals("true")){
						 flag =service.grantPermission(datName, userName);
						
					}else{
						 flag=service.RmPermission(datName, userName);
					}
				}
				if(flag){
					JOptionPane.showMessageDialog(null, "更新成功");
				}else{
					JOptionPane.showMessageDialog(null, "更新失败");
				}
			}
		
	}

}
