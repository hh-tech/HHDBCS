package com.hhdb.csadmin.common.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.util.StartUtil;

public class ConnService {
	public static Connection createConnection(ServerBean serverbean)
			throws Exception {
		Connection conn = null;
		String classStr = "";
		
		String url = "";
		if(StartUtil.prefix.equals("hh")){
			classStr = "org.hhdbsql.Driver";
			url = "jdbc:hhdbsql://" + serverbean.getHost() + ":"
					+ serverbean.getPort() + "/" + serverbean.getDBName();
		}else{
			classStr = "org.postgresql.Driver";
			url = "jdbc:postgresql://" + serverbean.getHost() + ":"
					+ serverbean.getPort() + "/" + serverbean.getDBName();
		}
		Class.forName(classStr);
		Properties props = new Properties();
		props.setProperty("user", serverbean.getUserName());
		props.setProperty("password", serverbean.getPassword());
		conn = DriverManager.getConnection(url, props);

		return conn;
	}

	public static void closeConn(Connection connection) throws SQLException {
		if (connection != null) {

			connection.close();
		}
	}
}
