package com.hh.hhdb_admin.mgr.function.run;

import com.hh.frame.common.util.db.SqlQueryUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class RunSqlServer extends Run {

    private final boolean isFunc;

    public RunSqlServer(Connection conn, String schema, String name, String type) {
        super(conn, schema, name);
        this.isFunc = "FUNCTION".equals(type);
    }

    @Override
    public boolean isOverWrite() {
        return false;
    }

    @Override
    public void handParams() throws Exception {
        String sql = "SELECT PARAMETER_MODE in_out,PARAMETER_NAME parameter,DATA_TYPE type, '' value " +
                "FROM information_schema.PARAMETERS " +
                "WHERE SPECIFIC_SCHEMA = '" + schema + "' AND SPECIFIC_NAME = '" + name + "'";
        List<Map<String, String>> maps = SqlQueryUtil.selectStrMapList(conn, sql);
        if (isFunc) {
            for (Map<String, String> map : maps) {
                if ("OUT".equals(map.get("in_out"))) {
                    if (StringUtils.isEmpty(map.get("parameter"))) {
                        retType = map.get("type");
                    }
                } else {
                    paramLists.add(map);
                }
            }
        } else {
            paramLists.addAll(maps);
        }
    }

}
