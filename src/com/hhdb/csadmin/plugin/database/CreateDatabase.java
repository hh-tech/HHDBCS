package com.hhdb.csadmin.plugin.database;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.hh.frame.common.log.LM;
import com.hh.frame.dbobj.hhdb.HHdbDatabase;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.tree.ui.BaseTextArea;


/**
 * 创建数据库
 * 
 * @author ZL
 * 
 */
public class CreateDatabase extends JPanel implements BaseChangeInterface {
	private static final long serialVersionUID = 1L;
	private JTextField jbase = new JTextField();
	private JComboBox<String> jowner = new JComboBox<String>();
	private JComboBox<String> jspace = new JComboBox<String>();
	private BaseTextArea comment;
	private DataBase database ;

	public void Createdatabase( DataBase database) {
		this.database=database;
		final ServerBean serverBean = database.getServerBean();
		setLayout(new BorderLayout());
		initCompant(serverBean);
		BaseDialog baseDialog = new BaseDialog(null, this, "新建数据库", "");
		baseDialog.setSize(310, 280);
		baseDialog.showDialog();
	}

	private void initCompant(ServerBean serverBean) {
		comment = new BaseTextArea(100);
		comment.setRowAsColumn(3, 6);
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setPreferredSize(new Dimension(280, 180));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		panel.add(new JLabel("数据库名："), gbc);
		gbc.gridx = 1;
		panel.add(jbase, gbc);
		//判断当前用户是否管理员
		String usql="select usesuper from "+StartUtil.prefix+"_user where usename='"+serverBean.getUserName()+"';";
		List<Map<String,Object>>list =  database.findData(usql);
		for (Map<String, Object> map : list) {
			
		if("true".equals(map.get("usesuper").toString())){
			String sql="select * from "+StartUtil.prefix+"_user";
			List<Map<String, Object>> woners = database.findData(sql);
			for (Map<String, Object> usermap : woners) {
				jowner.addItem(usermap.get("usename").toString());
			}
		}else{
			jowner.addItem(serverBean.getUserName());
		}
		}
			
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(new JLabel("拥有者："), gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		panel.add(jowner, gbc);
		String sqltb="select * from "+StartUtil.prefix+"_tablespace";
		List<Map<String, Object>> spaces = database.findData(sqltb);
		for (Map<String, Object> map : spaces) {
			jspace.addItem(map.get("spcname").toString());
		}
		gbc.gridx = 0;
		gbc.gridy = 2;
		panel.add(new JLabel("表空间："), gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		panel.add(jspace, gbc);
		gbc.gridx = 0;
		gbc.gridy = 3;
		panel.add(new JLabel("注释："), gbc);
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		panel.add(comment, gbc);
		add(panel, BorderLayout.NORTH);
	}

	public boolean execute() {
		String databasename = jbase.getText();
		if (databasename.trim().equals("")) {
			JOptionPane.showMessageDialog(null, "数据库名不能为空！！", "提示", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}else{
			//创建数据库
			StringBuffer sb = new StringBuffer();
			sb.append("CREATE DATABASE \"" + databasename + "\"\n");
			sb.append("\t WITH OWNER = " + jowner.getSelectedItem().toString() + "\n");
			sb.append("\t\t TABLESPACE = " + jspace.getSelectedItem().toString() +";"+ "\n");
			String comments = comment.getText();
			//如果输入了备注信息
			if(comments!=null&&!comments.equals("")){
				sb.append("\n");
				sb.append("\n");
				sb.append("COMMENT ON DATABASE " + jbase.getText());
				sb.append(" IS '" + comments + "'");
			}
			String sql =sb.toString();
			String toID="com.hhdb.csadmin.plugin.conn";
			CmdEvent event = new CmdEvent(database.PLUGIN_ID, toID, "ExecuteUpdateBySqlEvent");
			event.addProp("sql_str", sql.toString());
			Boolean bool = true;
			//发送事件保存数据库
			HHEvent ev = database.sendEvent(event);
			if(ev instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null,((ErrorEvent) ev).getErrorMessage());
				bool =false;
			}
			if(bool){
			//将public权限移除
			CmdEvent getconn = new CmdEvent(database.PLUGIN_ID,"com.hhdb.csadmin.plugin.conn", "GetConn");
			//发送事件获取conn
			HHEvent connevent = database.sendEvent(getconn);
			Connection conn = (Connection) connevent.getObj();
			try {
				HHdbDatabase hhdb = new HHdbDatabase(conn, databasename, true,StartUtil.prefix);
				hhdb.rmPublic();
			} catch (SQLException e) {
				
				LM.error(LM.Model.CS.name(), e);
			}
			JOptionPane.showMessageDialog(null, "创建成功", "提示", JOptionPane.INFORMATION_MESSAGE);
			//刷新节点
			database.refreshData();
			}
		}
		
		return true;
	}
	
	
	
}
