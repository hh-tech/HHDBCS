package com.hhdb.csadmin.plugin.conn.dao;

import java.sql.Connection;

import com.hh.frame.common.log.LM;
import com.hh.frame.common.util.db.ConnUtil;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.dao.ConnService;

public class ConnFactory {
	private ServerBean serverbean = new ServerBean();
	/**
	 * 是否是超级用户
	 */
	public boolean superuser = false;
	private Connection connection = null;
	public Connection getConnection() throws Exception{
		if(connection==null||connection.isClosed()||!ConnUtil.isConnected(connection)) {
			connection = createConnection(serverbean);
		}
//		else{
//			try {
//				if(SqlQueryUtil.selectOne(connection, "select 1;")==null){
//					connection = createConnection(serverbean);
//				}				
//			}catch (Exception e){
//				connection = createConnection(serverbean);
//			}
//		}
		return connection;
	}

	private Connection createConnection(ServerBean serverbean) throws Exception {
		return ConnService.createConnection(serverbean);
	}
	
	public  void closeConn() {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				throw new RuntimeException(e);
			}
		}
	}
	
	public ServerBean getServerBean(){
		return serverbean;
	}
}
