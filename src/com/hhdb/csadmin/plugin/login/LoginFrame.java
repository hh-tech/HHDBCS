package com.hhdb.csadmin.plugin.login;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.hh.frame.common.log.LM;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.dbobj.hhdb.HHdbUser;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.dao.ConnService;
import com.hhdb.csadmin.common.ui.PropertyPanel;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.common.util.StringUtil;
import com.hhdb.csadmin.plugin.main.MainFramePlugin;
/**
 * 登录数据库窗口
 * @author 胡圆锥
 *
 */
public class LoginFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PropertyPanel propertyPanel;
	private LoginPlugin plugin;
	private boolean removeKeyEventFlag = false;
	
	public LoginFrame(LoginPlugin plugin) {
		this.plugin=plugin;
		ImageIcon icon=new ImageIcon(System.getProperty("user.dir")+"/etc/icon/manage.png");
		setIconImage(icon.getImage());
		setAutoRequestFocus(false);
		setResizable(false);
		setTitle("数据库登录");
		Dimension size = new Dimension(500, 410);
		setSize(size);
		
		init();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int n = JOptionPane.showConfirmDialog(null, "确定退出吗？", "温馨提示", JOptionPane.YES_NO_OPTION);
				if (n == 0) {
					System.exit(0);
				}else {
					setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				}
			}
		});
		
		// 设置Frame居中显示
		setLocationRelativeTo(null);
		
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		//得到当前键盘事件的管理器
		//然后为管理器添加一个新的键盘事件监听者。
		manager.addKeyEventPostProcessor(new KeyEventPostProcessor()//返回一个实现KeyEventPostProcessor接口的匿名内部类。
		{
			public boolean postProcessKeyEvent(KeyEvent e)//实现postProcessKeyEvent方法
			{
				if(removeKeyEventFlag){
					return false;
				}
				if(e.getID()==KeyEvent.KEY_PRESSED){
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						connOk();
					}
				}
				return false;
				
			}
		});
	}
	
	private void init(){
		JPanel gImage = new JPanel() {  	  
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {  
                ImageIcon icon = new ImageIcon(StringUtil.getProIcoPath("backgroud.jpg"));  
                java.awt.Image img = icon.getImage();  
                g.drawImage(img, 0, 0, 500,380, icon.getImageObserver());  
            }  
        };
		
		JPanel panel_up = new JPanel();
		getContentPane().add(gImage);
		
		gImage.add(panel_up, BorderLayout.NORTH);
		panel_up.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));		
		JLabel label = new JLabel("数据库连接配置：");
		label.setFont(new Font("宋体", Font.PLAIN, 22));
		panel_up.add(label);
		panel_up.setBackground(null);  
		panel_up.setOpaque(false);  
		
		propertyPanel = new PropertyPanel();
		gImage.add(propertyPanel, BorderLayout.CENTER);
		propertyPanel.setPreferredSize(new Dimension(420, 260)); 
		propertyPanel.setBackground(null);  
		propertyPanel.setOpaque(false);  
		JPanel panel_down = new JPanel();
		gImage.add(panel_down, BorderLayout.SOUTH);
		panel_down.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		
		JButton btn_ok = new JButton("确定");
		panel_down.add(btn_ok);
		JButton btn_no = new JButton("退出");
		panel_down.add(btn_no);
		panel_down.setBackground(null);  
		panel_down.setOpaque(false); 
		
		btn_ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				connOk();
			}
		});
		
		btn_no.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showConfirmDialog(null, "确定退出吗？", "温馨提示", JOptionPane.YES_NO_OPTION);
				if (n == 0) {
					System.exit(0);
				}
			}
		});
		
	}

	private void connOk(){
		if(this.propertyPanel.getDbDriverGroupValue().equals("hhdb")){
			StartUtil.prefix = "hh";
		}else{
			StartUtil.prefix = "pg";
		}
		String host = this.propertyPanel.getHostValue();
		String port = this.propertyPanel.getPortNumberValue();
		String dbName = this.propertyPanel.getMaintainDatabaseValue();
		String userName = this.propertyPanel.getUserNameValue();
		String password = this.propertyPanel.getPasswordValue();
		String superuser_value = "false";
		if(this.propertyPanel.getGroupValue().equals("超级用户")){
			superuser_value = "true";
		}
		ServerBean tempServerbean = new ServerBean();
		tempServerbean.setHost(host);
		tempServerbean.setPort(port);
		tempServerbean.setDBName(dbName);
		tempServerbean.setUserName(userName);
		tempServerbean.setPassword(password);
		Connection conn = null;
		try {
			conn = ConnService.createConnection(tempServerbean);
			if (conn != null && !conn.isClosed()) {
				try {
					SqlQueryUtil.selectOne(conn, "select 1;").toString();
					if(superuser_value.equals("true")){
						HHdbUser hu = new HHdbUser(conn, userName,true,StartUtil.prefix);
						if(!hu.isSuper()){
							removeKeyEventFlag = true;
							JOptionPane.showMessageDialog(propertyPanel, userName+"不是超级用户，不能以超级管理员身份登录","错误",JOptionPane.ERROR_MESSAGE);
							removeKeyEventFlag = false;
							conn.close();
							return;
						}
					}
					conn.close();
					CmdEvent event = new CmdEvent(LoginPlugin.class.getPackage().getName(), "com.hhdb.csadmin.plugin.conn", "SetConn");
					event.addProp("host_str", host);
					event.addProp("port_str", port);
					event.addProp("dbname_str", dbName);
					event.addProp("username_str", userName);
					event.addProp("pass_str", password);
					event.addProp("superuser_value", superuser_value);
					plugin.sendEvent(event);
					HHEvent showMainFrame=new HHEvent(LoginPlugin.class.getPackage().getName(),MainFramePlugin.class.getPackage().getName(),EventTypeEnum.COMMON.name());
					plugin.sendEvent(showMainFrame);
					this.dispose();
					StartUtil.updateConnXml(tempServerbean, StartUtil.prefix);
					removeKeyEventFlag = true;
				} catch (Exception eee) {
					LM.error(LM.Model.CS.name(), eee);
					JOptionPane.showMessageDialog(propertyPanel, "错误信息：" + eee.getMessage(),"错误",JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception ee) {
			LM.error(LM.Model.CS.name(), ee);
			removeKeyEventFlag = true;
			JOptionPane.showMessageDialog(propertyPanel, "错误信息：" + ee.getMessage(),"错误",JOptionPane.ERROR_MESSAGE);
			removeKeyEventFlag = false;
		}
	}
	
	
}
