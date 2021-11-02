package com.hh.hhdb_admin.mgr.function.run;

import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.dbobj2.version.VersionUtil;

import java.sql.Connection;

public class RunMysql extends Run {

    private final String type;
    private final int bigVer;

    public RunMysql(Connection conn, String schema, String name, String type) {
        super(conn, schema, name);
        this.type = type;
        this.bigVer = VersionUtil.getDbVersion(conn).getBigVer();
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
                " and routine_type = '" + type + "' and parameter_name is not null";
        paramLists = SqlQueryUtil.selectStrMapList(conn, sql);
        if ("FUNCTION".equals(type)) {
            if (bigVer >= 8) {
                sql = "select data_type returns from information_schema.ROUTINES " +
                        "where routine_schema = '%s' and routine_type = 'FUNCTION' and specific_name = '%s'";
            } else {
                sql = "select returns from mysql.proc where db = '%s' and type = 'FUNCTION' and name = '%s'";
            }
            retType = SqlQueryUtil.selectOneStrMap(conn, String.format(sql, schema, name)).get("returns");
        }
    }

}
