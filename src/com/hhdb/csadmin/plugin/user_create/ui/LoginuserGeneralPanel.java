package com.hhdb.csadmin.plugin.user_create.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.hhdb.csadmin.plugin.user_create.UserCreate;



public class LoginuserGeneralPanel extends JPanel{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextField roleName= new JTextField();
	JTextField roleId= new JTextField();
	JPasswordField password= new JPasswordField();
	JPasswordField surePassword= new JPasswordField();
	JTextField connectionLimit= new JTextField();
	JButton time=new JButton("...");
	JTextField deadDate= new JTextField();
	JCheckBox superUser=new JCheckBox("超级用户");
	private Boolean flag=true;
	private String generalsql;
	private UserCreate usecreate ;
	
	public LoginuserGeneralPanel(UserCreate usecreate, boolean b) {
		this.usecreate = usecreate;
		flag=b;
		setBackground(Color.WHITE);
		setLayout(new GridBagLayout());
		//角色名
		add(new BaseLabel("角色名"), new GridBagConstraints(0, 1, 1, 1,0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 10, 0, 0), 0, 0));
		roleName.setPreferredSize(new Dimension(150,20));
		add(  roleName, new GridBagConstraints(1, 1, 1, 1,0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 10, 0, 0), 0, 0));
		if(!flag){
			roleName.setEnabled(false);
		}
		//密码
		add(new BaseLabel("密码"), new GridBagConstraints(0, 7, 1, 1,0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 10, 0, 0), 0, 0));
		password.setPreferredSize(new Dimension(150,20));
		add(  password, new GridBagConstraints(1, 7, 1, 1,0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 10, 0, 0), 0, 0));				
	//	password.setEnabled(false);
		//确认密码
		add(new BaseLabel("确认密码"), new GridBagConstraints(0, 9, 1, 1,0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 10, 0, 0), 0, 0));
		surePassword.setPreferredSize(new Dimension(150,20));
		add(  surePassword, new GridBagConstraints(1, 9, 1, 1,0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 10, 0, 0), 0, 0));						
	//	surePassword.setEnabled(false);
		//超级用户
		add(  superUser, new GridBagConstraints(0,17, 1, 1,0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 10, 0, 0), 0, 0));
		JPanel jpl = new JPanel();
		
		jpl.setBackground(Color.WHITE);
		add(jpl, new GridBagConstraints(0, 27, 4, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,new Insets(0, 0, 0, 0), 0, 0));
		
	}
	/**
	 * 获取常规页面sql
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String getParaForCreate() {
		String rolename=roleName.getText();
		String passwords=password.getText();
		String surePasswords=surePassword.getText();
		String sql="";
		if(!"".equals(rolename)){//角色名不为空
			sql="CREATE user "+rolename;
			
				if(!passwords.equals(surePasswords)){//密码和确认密码不同
					flag=false;
				}else{
					flag=true;
					sql+=" login "+" PASSWORD \'"+surePasswords+"\' ";
				}
			//超级用户superUser
			if(superUser.isSelected()){
				sql+=" SUPERUSER ";
			}
			sql+="; ";
		}else{
			flag=false;
		}
		
		return generalsql=sql;
		
	}
	
	public Boolean getFlag() {
		return flag;
		
	}
	public String getgeneralsql(){
		return generalsql;
	}
	
	public Boolean saveUser() {
		generalsql =getParaForCreate();
		if(!flag){
			UIUtils.showWarningBox("请检查密码及用户名");
			return flag;
    	}
		try {
			//保存用户
			boolean bool = usecreate.SaveData(generalsql);
			if(bool){
				return true;
			}
			return false;
		} catch (Exception e) {
			UIUtils.showErrorBox(e.getMessage());
		//	JOptionPane.showMessageDialog(ApplicationLauncher.getFrame(),message);
			return false;
		}
	}

	
	

}
