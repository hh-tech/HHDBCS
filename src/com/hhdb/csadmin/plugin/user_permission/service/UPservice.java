package com.hhdb.csadmin.plugin.user_permission.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.dbobj.hhdb.HHdbSession;
import com.hh.frame.dbobj.util.StringUtil;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.user_permission.UserPermission;

public class UPservice {
	private UserPermission up;
	
	public UPservice(UserPermission up){
		this.up=up;
	}
	
	/**
	 * 判断用户是否对数据库有权限
	*/
	public boolean isPermission(String username,String dataname){
		Connection conn = getConn();
		try {
			String sql="select has_database_privilege permission  from has_database_privilege('%s','%s','connect')";
			Map<String, String> map = SqlQueryUtil.selectOneStrMap(conn, sql,StringUtil.sqlQuotTrans(username), StringUtil.sqlQuotTrans(dataname));
			String permission = map.get("permission");
			if(permission.equals("true")||permission.equals("t")){
				return true;
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage()+"！", "提示",JOptionPane.ERROR_MESSAGE);
		}
		   return false;
	}
    
	 /**
	 * 获得所有的数据库
	 */
	  
	public Set<String> getDbName(){
		Connection conn = getConn();
		Set<String> userDbNames=null;
		HHdbSession session=new HHdbSession(conn,StartUtil.prefix);
		try {
		    userDbNames = session.getUserDbNames();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage()+"！", "提示",JOptionPane.ERROR_MESSAGE);
		}
		return userDbNames;
	}
	
	/**
	 * 删除数据库用户权限
	 * 
	 */
	public boolean RmPermission(String datName,String userName){
		Connection conn = getConn();
		try {
			String sql="revoke all on database  %s from %s;";
		    SqlExeUtil.executeUpdate(conn, sql, "\""+datName+"\"","\""+userName+"\"");
		    return true;
		} catch (SQLException e) {
			return false;
//			JOptionPane.showMessageDialog(null, e.getMessage()+"！", "提示",JOptionPane.ERROR_MESSAGE);
		}
		
	}
	

	 /**
	 * 数据库用户权限
	 * 
	 */
	public boolean grantPermission(String datName,String userName){
		Connection conn = getConn();
		try {
			String sql="grant all on database  %s to %s;";
		    SqlExeUtil.executeUpdate(conn, sql, "\""+datName+"\"","\""+userName+"\"");
		   return true;
		} catch (SQLException e) {
			return false;
//			JOptionPane.showMessageDialog(null, e.getMessage()+"！", "提示",JOptionPane.ERROR_MESSAGE);
		}
		
	}
	/**
	 * 发送事件获得连接
	 */
	private Connection getConn(){
		CmdEvent cmd=new CmdEvent(up.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetConn");
		HHEvent replyE = up.sendEvent(cmd);
		return (Connection) replyE.getObj();
	}
	
}
