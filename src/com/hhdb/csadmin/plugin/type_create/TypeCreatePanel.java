package com.hhdb.csadmin.plugin.type_create;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.hhdb.csadmin.common.ui.textEdit.QueryTextPane;
import com.hhdb.csadmin.common.util.IconUtilities;
import com.hhdb.csadmin.plugin.type_create.component.BaseButton;
import com.hhdb.csadmin.plugin.type_create.component.BaseToolBar;
import com.hhdb.csadmin.plugin.type_create.handle.TypesPaneHandle;

public class TypeCreatePanel extends JPanel implements ActionListener{
	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		// 按钮栏
		private BaseToolBar toolBar;
		//tab页
		private JTabbedPane tab;
		// 创建类型 面板
		private TypesPaneHandle typesPaneHandle;
		public QueryTextPane sqlView;
		private String schemaName ;
		private TypeCreate typecreate;
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("save")) {
			boolean flag = typesPaneHandle.saveType();
			if(flag){
				sqlChange();
				typecreate.refreshTree(schemaName);
				JOptionPane.showMessageDialog(null, "创建类型成功");
			}
		} else if (e.getActionCommand().equals("addfield")) {
		
			typecreate.createType(schemaName);
		} 
		
	}
	/**
	 * 初始化新建面板
	 */
	public TypeCreatePanel(TypeCreate typecreate,String schemaName, QueryTextPane sqlView){
		this.typecreate=typecreate;
		this.schemaName=schemaName;
		this.sqlView=sqlView;
		initTableCreate();
		new GridBagLayout();
	}
	
	private void initTableCreate() {
		toolBar = new BaseToolBar();
		//得到SQL面板
		//常规
		typesPaneHandle = new TypesPaneHandle(typecreate,schemaName);
		tab = new JTabbedPane(JTabbedPane.TOP);
		tab.addTab("常规", new JScrollPane(typesPaneHandle));
		tab.addTab("SQL预览", new JScrollPane(sqlView));
		//默认工具栏
		initTableTool();
		// 标签页改变时间 , 显示不同的工具栏
		tab.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
				int selectedIndex = tabbedPane.getSelectedIndex();
				if (selectedIndex == 0) {
					initTableTool();
				}
				else if (selectedIndex == 1) {
					initPreViewTool();
				    sqlChange();
				}
				
			}
		});
		setLayout(new GridBagLayout());
		//工具条
		add(toolBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		//签页
		add(tab, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));	

	}
	/**
	 * 创建类型 工具栏
	 */
	private void initTableTool() {
		toolBar.removeAll();
		toolBar.repaint();
		createBtn("新建", IconUtilities.loadIcon("addtype.png"), "addfield");
		toolBar.addSeparator();
		createBtn("保存", IconUtilities.loadIcon("save.png"), "save");
		toolBar.setFloatable(false);
	}


	/**
	 * 预览工具栏
	 */
	private void initPreViewTool() {
		toolBar.removeAll();
		toolBar.repaint();
		createBtn("保存", IconUtilities.loadIcon("save.png"), "save");
		toolBar.setFloatable(false);
	}
	/**
	 * 创建按钮
	 * 
	 * @param text
	 * @param icon
	 * @param comand
	 */
	//创建按钮添加监听 到工具栏
	private void createBtn(String text, Icon icon, String comand) {
		BaseButton basebtn = new BaseButton(text, icon);
		basebtn.setActionCommand(comand);
		basebtn.addActionListener(this);
		toolBar.add(basebtn);
	}
	//sql预览
		public void sqlChange() {
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append(typesPaneHandle.createTypeSql());
			sqlView.setText(sqlBuffer.toString());
		}

	
}
