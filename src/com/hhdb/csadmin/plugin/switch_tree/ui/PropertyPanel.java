package com.hhdb.csadmin.plugin.switch_tree.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.ui.BaseTextField;
import com.hhdb.csadmin.common.util.StartUtil;


/**
 * 数据库配置页面
 *
 */
public class PropertyPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  JTextField name = new JTextField(""); // 名称
	private  JTextField host = new JTextField(""); // 主机
	private  BaseTextField portNumber = new BaseTextField(true); // 端口号
	private  JTextField service = new JTextField("数据库连接"); // 服务
	private  JTextField maintainDatabase = new JTextField(""); // 维护数据库
	private  JTextField userName = new JTextField(""); // 用户名称
	private  JPasswordField password = new JPasswordField(""); // 密码
	private  JCheckBox savePassword = new JCheckBox("保存密码", true); // 保存密码
	private  JComboBox<?> group; // 组
	private  JComboBox<?> conngroup; // 选择连接组
	private  Map<String,ServerBean> connmap;
	public PropertyPanel() {
		super(new BorderLayout());
		init();
	}

	private void init() {
		
		JLabel label = new JLabel();
		label.setForeground(Color.red);
		label.setText("*");

		portNumber.setText("1432");
		buildGroup();
		service.setEditable(false);
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBackground(null);  
		mainPanel.setOpaque(false); 
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridy = 0;
		gbc.gridx = 0;
		addLabelFieldPair(mainPanel, "选择连接:", conngroup, gbc, null);
		addLabelFieldPair(mainPanel, "用户形式:", group, gbc, null);
		addLabelFieldPair(mainPanel, "主机名*:", host, gbc, keyListener);
		addLabelFieldPair(mainPanel, "端口*：", portNumber, gbc, keyListener);
		addLabelFieldPair(mainPanel, "维护数据库*:", maintainDatabase, gbc, keyListener);
		addLabelFieldPair(mainPanel, "用户名称*：", userName, gbc, keyListener);
		JPanel passwordOptionsPanel = new JPanel(new GridBagLayout());
//		gbc.gridy++;
//		gbc.gridx = 1;
//		gbc.weightx = 1.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		mainPanel.add(passwordOptionsPanel, gbc);
		addLabelFieldPair(mainPanel, "密码*：", password, gbc, keyListener);
		
		gbc.insets.right = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		mainPanel.setBorder(null);
		add(mainPanel);
	}


	private void buildGroup() {
		final String[] labels = { "普通用户","超级用户" };
		final DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(labels);
		group = new JComboBox<>(model);		
		try {
			connmap = StartUtil.initConnConfig(StartUtil.prefix);
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		}
		
		String[] connlabels={"--请选择--"};
		if(connmap!=null){
			if(connmap.keySet().size()>0){
				connlabels = new String[connmap.keySet().size()+1];
				connlabels[0] = "--请选择--";
				int i=1;
				for(String key:connmap.keySet()){
					connlabels[i]=key;
					i++;
				}
			}
		}
		final DefaultComboBoxModel<String> connmodel = new DefaultComboBoxModel<String>(connlabels);
		conngroup = new JComboBox<>(connmodel);	
		conngroup.addActionListener(new ActionListener() {   
		      public void actionPerformed(ActionEvent e) {   
		        String key = (String)((JComboBox<?>)e.getSource()).getSelectedItem();
		        ServerBean sb = connmap.get(key);
		        if(sb!=null){
		        	host.setText(sb.getHost());
		        	portNumber.setText(sb.getPort());
		        	maintainDatabase.setText(sb.getDBName());
		        	userName.setText(sb.getUserName());
		        	password.setText(sb.getPassword());
		        }
		      }   
		});
	}

	private void addLabelFieldPair(JPanel panel, String label, JComponent field, GridBagConstraints gbc, KeyListener l) {
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.insets.top = 10;
		if (panel.getComponentCount() > 0) {
			gbc.insets.top = 0;
		}
		gbc.insets.left = 10;
		gbc.weightx = 0;
		panel.add(new JLabel(label), gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridx = 1;
		gbc.insets.left = 5;
		gbc.weightx = 1.0;
		if (l != null) {
			field.addKeyListener(l);
		}
		panel.add(field, gbc);
	}

	public String getNameValue() {
		return name.getText();
	}
	public void setNameValue(String text) {
		name.setText(text);
	}
	
	public String getHostValue() {
		return host.getText();
	}
	public void setHostValue(String text) {
		host.setText(text);
	}
	
	public String getPortNumberValue() {
		return portNumber.getText();
	}
	
	public void setPortNumberValue(String text) {
		portNumber.setText(text);
	}
	
	public String getServiceValue() {
		return service.getText();
	}

	public String getMaintainDatabaseValue() {
		return maintainDatabase.getText();
	}
	
	public String getUserNameValue() {
		return userName.getText();
	}
	public void setUserNameValue(String text) {
		userName.setText(text);
	}
	public String getPasswordValue() {
		char[] pwd = password.getPassword();
		StringBuffer pwdBuffer = new StringBuffer(10);
		for (int i = 0; i < pwd.length; i++) {
			pwdBuffer.append(pwd[i]);
			pwd[i] = 0;
		}
		return pwdBuffer.toString();
	}
	public void setPasswordValue(String text) {
		password.setText(text);
	}
	public boolean isSavePassword() {
		return savePassword.isSelected();
	}


	public String getGroupValue() {
		return group.getSelectedItem().toString();
	}

	KeyListener keyListener = new KeyListener() {
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {
		}
	};
}
