package com.hh.hhdb_admin.mgr.function.run;

import com.hh.frame.common.util.db.SqlQueryUtil;

import java.sql.Connection;
import java.util.*;

public class RunDb2 extends Run {

    private final String type;

    protected RunDb2(Connection conn, String schema, String name, String type) {
        super(conn, schema, name);
        this.type = "FUNCTION".equals(type) ? "F" : "P";
        table.load(getData(), 1);
    }

    @Override
    public boolean isOverWrite() throws Exception {
        String sql = String.format("SELECT routinename FROM syscat.routines\n" +
                        "WHERE routinetype = '%s' and routineschema = '%s' and routinename = '%s'",
                type, schema, name);
        return SqlQueryUtil.selectStrMapList(conn, sql).size() > 1;
    }

    @Override
    public void handParams() {
        List<Map<String, String>> maps = getData();
        if (maps.size() > 0) {
            Map<String, String> map = maps.get(0);
            paramLists = argueMap.get(map.get("name"));
            retType = retMap.get(map.get("name"));
        }
    }

    private List<Map<String, String>> getData() {
        List<Map<String, String>> result = new ArrayList<>();
        try {
            String sql = String.format("SELECT routinename name, return_typename, specificname from syscat.routines\n" +
                            "WHERE routinetype = '%s' and routineschema = '%s' and routinename = '%s'",
                    type, schema, name);
            for (Map<String, String> map : SqlQueryUtil.selectStrMapList(conn, sql)) {
                List<Map<String, String>> listMap = new ArrayList<>();
                String specificName = map.get("specificname");
                String paramSql;
                if ("F".equals(type)) {
                    paramSql = String.format("select 'in' in_out, parmname parameter, typename type \n" +
                            "from syscat.funcparms\n" +
                            "where funcschema = '%s' and funcname = '%s' and specificname = '%s' and parmname is not null \n" +
                            "order by ordinal", schema, name, specificName);
                } else {
                    paramSql = String.format("select parm_mode in_out, parmname parameter, typename type \n" +
                            "from syscat.procparms\n" +
                            "where procschema = '%s' and procname = '%s' and specificname = '%s' \n" +
                            "order by ordinal", schema, name, specificName);
                }
                List<Map<String, String>> paramMaps = SqlQueryUtil.selectStrMapList(conn, paramSql);
                StringBuilder builder = new StringBuilder();
                builder.append("F".equals(type) ? "function " : "procedure ").append(schema).append(".").append(map.get("name"));
                if (paramMaps.size() != 0) {
                    builder.append("(");
                    int i = 0;
                    for (Map<String, String> paramMap : paramMaps) {
                        listMap.add(paramMap);
                        builder.append(paramMap.get("parameter")).append(" ").append(paramMap.get("type"));
                        if (i < paramMaps.size() - 1) {
                            builder.append(", ");
                        }
                        i++;
                    }
                    builder.append(")");
                }
                if ("F".equals(type)) {
                    builder.append(" return").append(" ").append(map.get("return_typename"));
                    retMap.put(specificName, map.get("return_typename"));
                } else {
                    retMap.put(specificName, "");
                }
                builder.append(";");
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("name", specificName);
                dataMap.put("value", builder.toString());
                result.add(dataMap);
                argueMap.put(specificName, listMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
