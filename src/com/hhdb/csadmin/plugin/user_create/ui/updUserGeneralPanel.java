package com.hhdb.csadmin.plugin.user_create.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.user_create.UserCreate;

public class updUserGeneralPanel extends JPanel {

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
	private UserCreate usercreate ;
	private String generalsql;
	private boolean flag = true;
	
	public updUserGeneralPanel(UserCreate usercreate,Integer userid, String username) {
		this.usercreate = usercreate;
		setBackground(Color.WHITE);
		setLayout(new GridBagLayout());
		//角色名
		add(new BaseLabel("角色名"), new GridBagConstraints(0, 1, 1, 1,0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 10, 0, 0), 0, 0));
		roleName.setPreferredSize(new Dimension(150,20));
		roleName.setText(username);
		add(  roleName, new GridBagConstraints(1, 1, 1, 1,0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 10, 0, 0), 0, 0));
		roleName.setEnabled(false);
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
		//发送事件查看该用户是否为超级用户
		String sql = "select usesuper from "+StartUtil.prefix+"_user    where usename= '"+ username+"'";
		CmdEvent getsuper = new CmdEvent(usercreate.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "ExecuteListMapBySqlEvent");
		getsuper.addProp("sql_str", sql);
		HHEvent event = usercreate.sendEvent(getsuper);
		@SuppressWarnings("unchecked")
		List<Map<String,Object>>list = (List<Map<String, Object>>) event.getObj();
		for(Map<String,Object> map:list){
			//是超级用户的话复选框自动选中
			if("true".equals(map.get("usesuper").toString())){
				superUser.setSelected(true);
			}
		}
		JPanel jpl = new JPanel();
		
		jpl.setBackground(Color.WHITE);
		add(jpl, new GridBagConstraints(0, 27, 4, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,new Insets(0, 0, 0, 0), 0, 0));
		
	}
	public Boolean designUser() {
		generalsql=getParaForDesign();
		if(!flag){
			UIUtils.showWarningBox("两次密码不相同");
			return false;
		}
		try {
			boolean bool = usercreate.SaveData(generalsql);
			if(bool){
				return true;
			}
			return false;
		} catch (Exception e) {
			UIUtils.showErrorBox(e.getMessage());
			LM.error(LM.Model.CS.name(), e);
		//	JOptionPane.showMessageDialog(ApplicationLauncher.getFrame(),message);
			return false;
		}
}
	@SuppressWarnings("deprecation")
	public String getParaForDesign() {
		String rolName=roleName.getText();
		String passwords=password.getText();
		String surePasswords=surePassword.getText();
		Boolean superUsers=superUser.isSelected();
		String sql="";
		if(!passwords.equals(surePasswords)){//密码和确认密码不同
			flag=false;
		}
		//输入了密码则修改密码
		if(!passwords.equals("")&&passwords.equals(surePasswords)){
			flag=true;
			sql+="update  "+StartUtil.prefix+"_authid set rolpassword ='"+passwords+"' ";
			//超级用户superUser
			if(superUsers){
				sql+=" , rolsuper = true  ";
			}else{
				sql+=" , rolsuper = false  ";
			}
			sql+=" where rolname = '"+rolName+"' ";
		}else
		//没输入密码则表示不修改密码
		if(passwords.equals("")&&passwords.equals(surePasswords)){
			flag=true;
			sql+="update "+StartUtil.prefix+"_authid set ";
			//超级用户superUser
			if(superUsers){
				sql+=" rolsuper = true  ";
			}else{
				sql+=" rolsuper = false  ";
			}
			sql+=" where rolname = '"+rolName+"' ";
		}
		
		sql+=";";
		return sql;
	}
	
	public Boolean getFlag() {
		return flag;
		
	}
}
