package com.hhdb.csadmin.plugin.user_create;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.common.ui.textEdit.QueryTextPane;
import com.hhdb.csadmin.common.util.IconUtilities;
import com.hhdb.csadmin.plugin.user_create.ui.BaseButton;
import com.hhdb.csadmin.plugin.user_create.ui.BaseTabbedPaneCustom;
import com.hhdb.csadmin.plugin.user_create.ui.BaseToolBar;
import com.hhdb.csadmin.plugin.user_create.ui.LoginuserGeneralPanel;
import com.hhdb.csadmin.plugin.user_create.ui.UIUtils;
import com.hhdb.csadmin.plugin.user_create.ui.updUserGeneralPanel;

public class UserCreatePanel {
	
	private JPanel jPanel = new JPanel();
	private BaseTabbedPaneCustom tab;
	private BaseToolBar toolBar = new BaseToolBar();
	private QueryTextPane querytext = new QueryTextPane();
	private LoginuserGeneralPanel loginuserGeneralPanel;
	private updUserGeneralPanel updusergeneralpanel;
	private boolean isedit;
	private Integer userid;
	private String userName;
	private UserCreate usercreate;
	
	public void init(UserCreate usecreate, boolean isEdit) {
		this.usercreate=usecreate;
		isedit=isEdit;
		tab = new BaseTabbedPaneCustom(JTabbedPane.TOP);
		tab.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		loginuserGeneralPanel=new LoginuserGeneralPanel(usecreate,isedit);
		tab.addTab("常规", new JScrollPane(loginuserGeneralPanel));
		tab.addTab("预览sql",new JScrollPane(querytext));
		createBtn("保存", IconUtilities.loadIcon("save.png"), "save");
		toolBar.addSeparator();	
		toolBar.setFloatable(false);
		jPanel.setLayout(new GridBagLayout());
		tab.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
			    int selectedIndex = tabbedPane.getSelectedIndex();
			    if(selectedIndex==1)
			    { 	
			    	if(isedit){
			    		tabControlerForCreate();
			    	}
			    }
			}
		});
		//工具条
		jPanel.add(toolBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		//签页
		jPanel.add(tab, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
	}
	public void upduser(UserCreate usercreate,String id ,final String username){
		this.usercreate=usercreate;
		userid = Integer.parseInt(id);
		userName=username;
		tab = new BaseTabbedPaneCustom(JTabbedPane.TOP);
		tab.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		updusergeneralpanel=new updUserGeneralPanel(usercreate,userid,userName);
		tab.addTab("常规", new JScrollPane(updusergeneralpanel));
		tab.addTab("预览sql",new JScrollPane(querytext));
		createBtn("保存", IconUtilities.loadIcon("save.png"), "upd");
		toolBar.addSeparator();	
		toolBar.setFloatable(false);
		jPanel.setLayout(new GridBagLayout());
		tab.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
			    int selectedIndex = tabbedPane.getSelectedIndex();
			    if(selectedIndex==1)
			    { 	
			    		tabControlerForDesign();
			    }
			}
		});
		//工具条
		jPanel.add(toolBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		//签页
		jPanel.add(tab, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}
	
	public void deluser(UserCreate usercreate,String username){
		int n = JOptionPane.showConfirmDialog(null, "您将删除该用户，确认继续吗？", "温馨提示", JOptionPane.YES_NO_OPTION);
		if (n == 0) {
			String sql="drop user \""+username+"\"";
			try {
			 boolean bool = usercreate.SaveData(sql);
				if(bool){
					JOptionPane.showMessageDialog(null, "删除成功");//消息提示框
				}
			} catch (Exception e) {
				UIUtils.showErrorBox(e.getMessage());
				LM.error(LM.Model.CS.name(), e);
			}
			usercreate .refreshData();//刷新
		}
	}
	
	public void renameuser(UserCreate usercreate,String username){
		//弹窗重命名
		String sequenceName = JOptionPane.showInputDialog(null, "输入新用户名", "新用户名", JOptionPane.PLAIN_MESSAGE);
		if(!"".equals(sequenceName)&&!(sequenceName==null)){
			String sql="ALTER ROLE \""+username+"\"" +" RENAME TO \""+sequenceName+"\"";
			try {
				boolean bool = usercreate.SaveData(sql);
				if(bool){
					JOptionPane.showMessageDialog(null, "修改成功");
				}
			} catch (Exception e) {
				UIUtils.showErrorBox(e.getMessage());
			}
			
			usercreate .refreshData();//刷新
		}
	}
	
	/**
	 * 获取主面板
	 * 
	 * @return
	 */
	public JPanel getjPanel() {
		return jPanel;
	}
	/**
	 * 添加用户
	 */
	private void tabControlerForCreate() {	
		//响应鼠标点击事件
		//tab.setSelectedIndex(0);      
		loginuserGeneralPanel.getParaForCreate();
    	Boolean flagForDesign=loginuserGeneralPanel.getFlag();
    	if(!flagForDesign){
    		tab.setSelectedIndex(0);
    		JOptionPane.showMessageDialog(null,"请检查用户名及密码");
    	}else{
//    		String seqName="NewSequence";
        	String sql =loginuserGeneralPanel.getParaForCreate();
        	querytext.setText(sql);
    	}  	
}
	
	/**
	 * 修改用户
	 */
	private void tabControlerForDesign() {	
			//响应鼠标点击事件
			//tab.setSelectedIndex(0);      
			updusergeneralpanel.getParaForDesign();
        	Boolean flagForDesign=updusergeneralpanel.getFlag();
        	if(!flagForDesign){
        		tab.setSelectedIndex(0);
        		JOptionPane.showMessageDialog(null,"两次密码不相同");
        	}else{
//        		String seqName="NewSequence";
            	String sql =updusergeneralpanel.getParaForDesign();
            	querytext.setText(sql);
        	}  	
	}
	
	
	/**
	 * 创建‘保存’按钮
	 * @param string
	 * @param loadIcon
	 * @param string1
	 */
	private void createBtn(String text, ImageIcon icon, String actionCommand) {
		BaseButton bb=new BaseButton(text, icon);
		bb.setActionCommand(actionCommand);
		bb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if("save".equals(e.getActionCommand())){
					Boolean designflag=false;
					designflag=loginuserGeneralPanel.saveUser();//getgeneralsql
					if(designflag){
						JOptionPane.showMessageDialog(null, "保存成功");
						usercreate .refreshData();//刷新节点
					}
				}
				if("upd".equals(e.getActionCommand())){
					Boolean designflag=false;
					designflag=updusergeneralpanel.designUser();//getgeneralsql
					if(designflag){
						JOptionPane.showMessageDialog(null, "保存成功");
						usercreate .refreshData();
					}
				}
			}
		});
		toolBar.add(bb);
	}



}
