package com.hh.hhdb_admin.mgr.function.run;

import com.hh.frame.common.base.DBTypeEnum.Version;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;

import java.sql.Connection;

public class RunMysql extends Run {

//    private final int bigVer;
    private TreeMrType objType=TreeMrType.PROCEDURE;
    private Version ver;
    public RunMysql(JdbcBean jdbc,Connection conn, String schema, String name, boolean isFunNotProc) {
        super(conn, schema, name);
//        this.bigVer = VersionUtil.getDbVersion(conn).getBigVer();
        if(isFunNotProc) {
        	objType=TreeMrType.FUNCTION;
        }
        this.ver=jdbc.getCurrentVersion();
    }

    @Override
    public boolean isOverWrite() {
        return false;
    }

    @Override
    public void handParams() throws Exception {
        String sql = "select parameter_mode in_out,parameter_name parameter,data_type type, '' value " +
                "from information_schema.parameters " +
                "where specific_schema = '" + schema + "' and specific_name = '" + name + "'" +
                " and routine_type = '" + objType + "' and parameter_name is not null";
        paramLists = SqlQueryUtil.selectStrMapList(conn, sql);
        if (objType==TreeMrType.FUNCTION) {
        	
//            if (bigVer >= 8) {
//                sql = "select data_type returns from information_schema.ROUTINES " +
//                        "where routine_schema = '%s' and routine_type = 'FUNCTION' and specific_name = '%s'";
//            } else {
//                sql = "select returns from mysql.proc where db = '%s' and type = 'FUNCTION' and name = '%s'";
//            }
            if (ver==Version.mysql5x) {
                sql = "select returns from mysql.proc where db = '%s' and type = 'FUNCTION' and name = '%s'";
            } else {
            	sql = "select data_type returns from information_schema.ROUTINES " +
                        "where routine_schema = '%s' and routine_type = 'FUNCTION' and specific_name = '%s'";
            }
            retType = SqlQueryUtil.selectOneStrMap(conn, String.format(sql, schema, name)).get("returns");
        }
    }

}
