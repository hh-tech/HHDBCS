package com.hhdb.csadmin.plugin.table_space;

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
import com.hhdb.csadmin.plugin.user_create.ui.BaseButton;
import com.hhdb.csadmin.plugin.user_create.ui.BaseTabbedPaneCustom;


public class TableSpaceHandle extends JPanel implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel jPanel = new JPanel();
	private BaseTabbedPaneCustom tab;
	private BaseToolBar toolBar = new BaseToolBar();
	private QueryTextPane querytext = new QueryTextPane();
	TablesSpacePanleHandle tablesSpacePanleHandle;
	
	public void TablesSpaceHandle(TableSpace tableSpace){
		tab = new BaseTabbedPaneCustom(JTabbedPane.TOP);
		tab.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		setLayout(new GridBagLayout());
		createBtn("保存", IconUtilities.loadIcon("save.png"), "save");
		tablesSpacePanleHandle = new TablesSpacePanleHandle(tableSpace);
		toolBar.addSeparator();	
		toolBar.setFloatable(false);
		jPanel.setLayout(new GridBagLayout());
		tab.addTab("常规",tablesSpacePanleHandle); 
		tab.addTab("SQL预览",new JScrollPane(querytext));
		tab.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
			    int selectedIndex = tabbedPane.getSelectedIndex();
			    if(selectedIndex==1){ 
			    	tablesSpacePanleHandle.sqlPreview(querytext);
			    }
			}
		});
		//工具条
		jPanel.add(toolBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		//签页
		jPanel.add(tab, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}
	
	public void delTableSpace(TableSpace tablespace,String tablespacename){
		int n = JOptionPane.showConfirmDialog(null, "这将删除此表空间，你确定想要继续吗？", "温馨提示", JOptionPane.YES_NO_OPTION);
		if (n == 0) {
			String sql="drop tablespace \""+tablespacename+"\"";
			boolean bool = tablespace.SaveData(sql);
			if(bool){
				JOptionPane.showMessageDialog(null, "删除成功");//消息提示框
			}
			//刷新节点
			tablespace.refreshData();
		}
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

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if("save".equals(actionCmd)){
			tablesSpacePanleHandle.saveTab();
			querytext.setText("");
		}
	}
	
	/**
	 * 获取主面板
	 * 
	 * @return
	 */
	public JPanel getJPanel() {
		return jPanel;
	}
	
}
