package com.hh.hhdb_admin.mgr.login;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DateUtil;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.create_dbobj.treeMr.base.ViewType;
import com.hh.frame.json.JsonObject;

import java.sql.Connection;
import java.util.Date;

public class LoginBean {
	private JdbcBean jdbc;
	private ViewType viewType;
	private Connection conn;
	private String connName;
	private Date loginDate;
	private boolean isSshAuth;
	/**
	 * 如果开启了SSH登录,jdbc中的url和端口会被替换 originalJdbc将存放最原始的jdbc信息
	 */
	private JdbcBean originalJdbc;

	public JdbcBean getJdbc() {
		return jdbc;
	}

	public void setJdbc(JdbcBean jdbc) {
		this.jdbc = jdbc;
	}

	public ViewType getViewType() {
		return viewType;
	}

	public void setViewType(ViewType viewType) {
		this.viewType = viewType;
	}

	public Connection getConn() {
		if (conn != null) {
			reConn();
		}
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getConnName() {
		return connName;
	}

	public void setConnName(String connName) {
		this.connName = connName;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public boolean isSshAuth() {
		return isSshAuth;
	}

	public void setSshAuth(boolean sshAuth) {
		isSshAuth = sshAuth;
	}

	public JdbcBean getOriginalJdbc() {
		return originalJdbc;
	}

	public void setOriginalJdbc(JdbcBean originalJdbc) {
		JsonObject object = originalJdbc.toJson();
		this.originalJdbc =JdbcBean.toJdbc(object);
	}

	@Override
	public String toString() {
		return jdbc + "\n" +
				conn + "\n" +
				connName + "\n" +
				DateUtil.dateToStr(loginDate, false) + "\n" +
				viewType.name() + "\n";
	}

	private void reConn() {
		try {
			DBTypeEnum dbTypeEnum = DriverUtil.getDbType(jdbc);
			if (dbTypeEnum != null) {
				switch (dbTypeEnum) {
					case hhdb:
					case pgsql:
						SqlQueryUtil.select(conn, "select 1");
						break;
					case oracle:
						SqlQueryUtil.select(conn, "select 1 from user_tables where table_name = 'test'");
						break;
					case mysql:
						SqlQueryUtil.select(conn, String.format("SHOW PROCEDURE STATUS WHERE DB = '%s'", jdbc.getSchema()));
						break;
					case db2:
						SqlQueryUtil.select(conn, "select * from sysibm.sysroles WHERE roleid = 1");
						break;
					case sqlserver:
						SqlQueryUtil.select(conn, "select 1 from sys.objects where object_id = 1");
						break;
					case dm:
						SqlQueryUtil.select(conn, "select 1 from all_objects where object_id = 1");
						break;
					default:
						throw new Exception("未实现的数据库类型:" + dbTypeEnum.name());
				}
			}
		} catch (Exception throwables) {
			try {
				conn = ConnUtil.getConn(jdbc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
