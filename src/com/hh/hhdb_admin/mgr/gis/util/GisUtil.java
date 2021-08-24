package com.hh.hhdb_admin.mgr.gis.util;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.create_dbobj.attributeMr.AttrUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GisUtil {
	/**
	 * 获取所有shp表
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getTable(Connection conn) throws SQLException {
		//没有数据的表格就不要
		String sql = "select distinct table_schema,table_name from information_schema.columns where udt_name='geometry' and is_updatable='YES' order by table_schema;";
		List<Map<String, String>> list = SqlQueryUtil.selectStrMapList(true, conn, sql);
		List<Map<String, String>> datalist =new ArrayList<Map<String,String>>();
		for(Map<String,String> map:list){
			String tableName=map.get("table_name");
			String schemaName=map.get("table_schema");
			String datasql = "select count(1) from \""+schemaName+"\".\""+tableName+"\"";
			Map<String,Object> countmap=SqlQueryUtil.selectOne(conn, datasql);
			if((long)countmap.get("count")>0){
				datalist.add(map);
			}
		}
		return datalist;
	}
	
	/**
	 * 判断有没有gis扩展
	 * @param conn
	 * @throws SQLException
	 */
	public static void getversion(Connection conn) throws SQLException{
		String str = DriverUtil.getDbType(conn).equals(DBTypeEnum.hhdb) ? "hh" : "pg";
		str = str.equals("hh")?"hhdb":"post";
		String sql="select "+str+"gis_full_version()";
		SqlQueryUtil.selectList(conn, sql);
	}

	/**
	 * 获取登入信息
	 * @param jdbcBean
	 * @param schemaName
	 * @return
	 */
	public static Map<String, Object> getLogInfo(JdbcBean jdbcBean,String schemaName){
		String str = DriverUtil.getDbType(jdbcBean).equals(DBTypeEnum.hhdb) ? "hh" : "pg";
		Map<String, String> map = AttrUtil.connInformation(jdbcBean);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dbtype", str.equals("hh") ? "hhdbgis" : "postgis");
		params.put("host", map.get("ip"));
		params.put("port", map.get("port"));
		params.put("schema", schemaName);
		params.put("database", map.get("dbName"));
		params.put("user", jdbcBean.getUser());
		params.put("passwd", jdbcBean.getPassword());
		return params;
	}

	public static ArrayList<String> selectSql(Connection conn, String sql)throws Exception {
		ArrayList<String> arr = new ArrayList<String>();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			String str;
			while (rs.next()) {
				str = rs.getString(1);
				arr.add(str);
			}
		} finally {
			if (statement != null) statement.close();
			if (rs != null) rs.close();
		}
		return arr;
	}
}
