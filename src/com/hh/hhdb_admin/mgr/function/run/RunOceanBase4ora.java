package com.hh.hhdb_admin.mgr.function.run;

import com.hh.frame.common.util.db.SqlQueryUtil;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class RunOceanBase4ora extends RunOracle {

    public RunOceanBase4ora(Connection conn, String schema, String packName, String name) {
        super(conn, schema,packName, name);
    }


    @Override
    public void handParams() throws Exception {
        paramLists = queryParam();
        String sql = String.format("SELECT O.OBJECT_NAME as name, O.OBJECT_TYPE as type, O.OBJECT_ID as id, A.DATA_TYPE\n" +
                "FROM SYS.ALL_OBJECTS O\n" +
                "LEFT JOIN ALL_ARGUMENTS A ON O.OBJECT_ID = A.OBJECT_ID AND O.OWNER =A.OWNER AND A.ARGUMENT_NAME IS NULL\n" +
                "WHERE O.OBJECT_TYPE IN ('PROCEDURE', 'FUNCTION') AND O.OWNER = '%s' AND O.OBJECT_NAME = '%s'\n" +
                "ORDER BY O.OBJECT_NAME", schema, name);
        List<Map<String, String>> maps = SqlQueryUtil.selectStrMapList(conn, sql);
        if (maps.size() > 0) {
            Map<String, String> map = maps.get(0);
            retType = "FUNCTION".equals(map.get("type")) ? map.get("data_type") : "";
        }
    }
}
