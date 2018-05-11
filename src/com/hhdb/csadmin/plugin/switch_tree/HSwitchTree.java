package com.hhdb.csadmin.plugin.switch_tree;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.hh.frame.common.log.LM;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.dbobj.hhdb.HHdbUser;
import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.dao.ConnService;
import com.hhdb.csadmin.common.ui.BaseFrame;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.switch_tree.service.SqlOperationService;
import com.hhdb.csadmin.plugin.switch_tree.ui.PropertyPanel;

public class HSwitchTree extends AbstractPlugin {
	
	public String PLUGIN_ID = HSwitchTree.class.getPackage().getName();
	public SqlOperationService sqlOperationService;
	public BaseFrame bf;
	
	private PropertyPanel propertyPanel;
	private JDialog d;
	private JOptionPane pane;
	
	public HSwitchTree(){
		sqlOperationService = new SqlOperationService(this);
	}

	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent treeEvent= EventUtil.getReplyEvent(HSwitchTree.class, event);
		if(event.getType().equals(EventTypeEnum.COMMON.name())){
			Object[] options = { "确定", "取消" };
			propertyPanel = new PropertyPanel();
			propertyPanel.setBackground(null);  
			propertyPanel.setOpaque(false); 
			pane = new JOptionPane(propertyPanel, JOptionPane.PLAIN_MESSAGE,
					JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
			JOptionPane p = new JOptionPane();
			pane.setBackground(null);  
			pane.setOpaque(false); 
			try {
				
				bf = sqlOperationService.getBaseFrame();
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null,"错误信息：" + e.getMessage(), "错误",JOptionPane.ERROR_MESSAGE);
			}
			d = p.createDialog(bf, "切换数据库连接：");
			d.setContentPane(pane);
			d.setSize(400, 330);
			PropertyChangeListener changeListener = connOk();
			pane.addPropertyChangeListener(changeListener);
			d.setLocationRelativeTo(null); // 中间显示
			d.setVisible(true);
			return treeEvent;
		}else{
			ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,event.getFromID(),ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage(PLUGIN_ID + "不能接受如下事件:\n"+ event.toString());
			return errorEvent;
		}
	}

	// 登入表单监控
	private PropertyChangeListener connOk() {
		PropertyChangeListener changeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				try {
					String value = (String) pane.getValue();
					if ("取消".equals(value)) {
						pane.removePropertyChangeListener(this);
						d.dispose();
					} else if ("确定".equals(value)) {
						ServerBean sb = sqlOperationService.getServerbean();
						String old_superuser_value = sqlOperationService.getSuperuserValue();
						String hosts = propertyPanel.getHostValue();
						String port = propertyPanel.getPortNumberValue();
						String dbName = propertyPanel.getMaintainDatabaseValue();
						String userName = propertyPanel.getUserNameValue();
						String password = propertyPanel.getPasswordValue();
						String superuser_value = "false";
						if(propertyPanel.getGroupValue().equals("超级用户")){
							superuser_value = "true";
						}
						
						if (hosts.equals(sb.getHost())&&port.equals(sb.getPort())&&
								dbName.equals(sb.getDBName())&&userName.equals(sb.getUserName())&&
								superuser_value.equals(old_superuser_value)) {
							// 登入原来的不做处理
							JOptionPane.showMessageDialog(bf,"提示信息：此连接已登入", "提示",JOptionPane.INFORMATION_MESSAGE);
							pane.setValue("OK");
						} else {							
							try {
								ServerBean tempServerbean = new ServerBean();
								tempServerbean.setHost(hosts);
								tempServerbean.setPort(port);
								tempServerbean.setDBName(dbName);
								tempServerbean.setUserName(userName);
								tempServerbean.setPassword(password);
								Connection conn = ConnService.createConnection(tempServerbean);
								SqlQueryUtil.selectOne(conn, "select 1;").toString();
								if(superuser_value.equals("true")){
									HHdbUser hu = new HHdbUser(conn, userName,true,StartUtil.prefix);
									if(!hu.isSuper()){
										JOptionPane.showMessageDialog(propertyPanel, userName+"不是超级用户，不能以超级管理员身份登录","错误",JOptionPane.ERROR_MESSAGE);
										pane.setValue("OK");
										conn.close();
										return;
									}
								}
								conn.close();
								try {
									String toId = "com.hhdb.csadmin.plugin.tree";
									CmdEvent switchTreeEvent = new CmdEvent(PLUGIN_ID, toId, "ChangeTreeEvent");
									switchTreeEvent.addProp("host_str", hosts);
									switchTreeEvent.addProp("port_str", port);
									switchTreeEvent.addProp("dbname_str", dbName);
									switchTreeEvent.addProp("username_str",userName);
									switchTreeEvent.addProp("pass_str", password);
									switchTreeEvent.addProp("superuser_value", superuser_value);
									sendEvent(switchTreeEvent);
									pane.removePropertyChangeListener(this);
									d.dispose();
									StartUtil.updateConnXml(tempServerbean, StartUtil.prefix);
								} catch (Exception ee) {
									LM.error(LM.Model.CS.name(), ee);
									sb.setHost("");
									JOptionPane.showMessageDialog(bf,"错误信息：" + ee.getMessage(), "错误",JOptionPane.ERROR_MESSAGE);
									pane.setValue("OK");
								}
							} catch (Exception ee) {
								LM.error(LM.Model.CS.name(), ee);
								JOptionPane.showMessageDialog(propertyPanel,"错误信息：" + ee.getMessage(), "错误",JOptionPane.ERROR_MESSAGE);
								pane.setValue("OK");
							}
						}
					}
				} catch (Exception e2) {
					LM.error(LM.Model.CS.name(), e2);
					JOptionPane.showMessageDialog(null,"错误信息：" + e2.getMessage(), "错误",JOptionPane.ERROR_MESSAGE);
				}
				
			}
		};
		return changeListener;
	}

	@Override
	public Component getComponent() {
		return null;
	}
}
