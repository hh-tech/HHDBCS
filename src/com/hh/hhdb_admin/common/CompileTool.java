package com.hh.hhdb_admin.common;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.ProcFunSqlUtil;
import com.hh.frame.db_sql.ora.OraSqlUtil;
import com.hh.frame.db_sql.ora.OraSqlUtil.CompileType;
import com.hh.frame.lg.Lg;

public class CompileTool {
	
	private DBTypeEnum dbtype;
	private DBTypeEnum.Version ver;
	private Connection conn;
	private String schema;
	private String objName;
	private CompileType type;
	public CompileTool(DBTypeEnum dbtype, DBTypeEnum.Version ver, Connection conn,
			String schema,  String objName,CompileType type) {
		this.dbtype = dbtype;
		this.ver = ver;
		this.conn = conn;
		this.schema = schema;
		this.type = type;
		this.objName = objName;
	};
	
	
	
	
	public boolean compile() throws Exception {
		if(ProcFunSqlUtil.isSupportCompile(dbtype, ver)) {
			String sql = OraSqlUtil.getCompileSql( schema, objName, type);
			 return SqlExeUtil.execute(conn, sql);
		}
		throw new Exception("不支持编译");
		
	}
	
	
	
	
	 public String getErrorMsg() {
	    	StringBuilder errorText = new StringBuilder();
	    	try {
	    		for (Map<String, String> map : getErrorList()) {
	        		String line = "0".equals((map.get("line") + "")) ? "1" : (map.get("line") + "");
	                errorText.append(map.get("type")).append("  ").append(line).append("  ")
	                        .append((map.get("text") + "").replace("\n", "")).append("\n");
	        	}	
	    		
	        } catch (SQLException e) {
	            Lg.error(CompileTool.class, e);
	            errorText.append(e.getMessage());
	        }
	    	return errorText.toString();
	    }

	    public List<Map<String, String>> getErrorList() throws SQLException {
	        String sql = OraSqlUtil.getErrorSql( schema, objName, type,false);
	        return SqlQueryUtil.selectStrMapList(conn, sql);
	    }

}
