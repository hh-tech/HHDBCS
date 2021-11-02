package com.hh.hhdb_admin.mgr.function.run;

import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.*;

public class RunOracle extends Run {

    private final String packName;
    private final boolean isPack;

    public RunOracle(Connection conn, String schema, String packName, String name) {
        super(conn, schema, name);
        this.packName = packName;
        this.isPack = StringUtils.isNotEmpty(packName);
        table.load(getData(), 1);
    }

    @Override
    public boolean isOverWrite() throws Exception {
        if (isPack) {
            String sql = String.format("select procedure_name\n" +
                    "  from all_procedures\n" +
                    " where owner = '%s'\n" +
                    "   and object_name = '%s'\n" +
                    "   and procedure_name = '%s'", schema, packName, name);
            return SqlQueryUtil.selectStrMapList(conn, sql).size() > 1;
        }
        return false;
    }

    @Override
    public void handParams() throws Exception {
        paramLists = queryParam();
        String sql = String.format("SELECT O.OBJECT_NAME as name, O.OBJECT_TYPE as type, O.OBJECT_ID as id, A.DATA_TYPE\n" +
                "FROM SYS.ALL_OBJECTS O\n" +
                "LEFT JOIN ALL_ARGUMENTS A ON O.OBJECT_ID = A.OBJECT_ID AND O.OWNER =A.OWNER AND A.ARGUMENT_NAME IS NULL\n" +
                "WHERE O.OBJECT_TYPE IN ('PROCEDURE', 'FUNCTION') AND O.OWNER = '%s' AND O.OBJECT_NAME = '%s'\n" +
                "AND O.OBJECT_ID NOT IN (SELECT PURGE_OBJECT FROM RECYCLEBIN)\n" +
                "ORDER BY O.OBJECT_NAME", schema, name);
        List<Map<String, String>> maps = SqlQueryUtil.selectStrMapList(conn, sql);
        if (maps.size() > 0) {
            Map<String, String> map = maps.get(0);
            retType = "FUNCTION".equals(map.get("type")) ? map.get("data_type") : "";
        }
    }

    private List<Map<String, String>> queryParam() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT IN_OUT, ARGUMENT_NAME parameter, DATA_TYPE type, '' value\n");
        builder.append("FROM ALL_ARGUMENTS\n");
        builder.append("WHERE OWNER = '").append(schema).append("'\n");
        if (StringUtils.isNotBlank(packName)) {
            builder.append("AND PACKAGE_NAME = '").append(packName).append("'\n");
        } else {
            builder.append("AND PACKAGE_NAME IS NULL\n");
        }
        builder.append("AND ARGUMENT_NAME IS NOT NULL\n");
        builder.append("AND OBJECT_NAME = '").append(name).append("'\n");
        builder.append("AND DATA_TYPE IS NOT NULL\n");
        builder.append("AND DATA_LEVEL = 0\n");
        builder.append("ORDER BY SEQUENCE, IN_OUT");
        return SqlQueryUtil.selectStrMapList(conn, builder.toString());
    }

    private List<Map<String, String>> getData() {
        String funcSql = "select distinct object_name, overload, data_type from all_arguments\n" +
                " where owner = '%s' and package_name = '%s' and object_name = '%s'\n" +
                "and data_level = 0 and data_type is not null and argument_name is null order by overload";
        List<Map<String, String>> retMaps = new ArrayList<>(getDataLists(funcSql, "function"));
        String procSql = "select object_name, overload from all_arguments a\n" +
                " where a.owner = '%s' and a.package_name = '%s' and a.object_name = '%s'\n" +
                "and a.data_level = 0\n" +
                "   and not exists (select 1 from all_arguments b\n" +
                "         where a.owner = b.owner and a.package_name = b.package_name and a.object_name = b.object_name and a.data_level = b.data_level\n" +
                "           and data_type is not null and b.argument_name is null and a.overload = b.overload)\n" +
                " group by object_name, overload" +
                " order by overload";
        retMaps.addAll(getDataLists(procSql, "procedure"));
        return retMaps;
    }

    private List<Map<String, String>> getDataLists(String sql, String type) {
        List<Map<String, String>> result = new ArrayList<>();
        try {
            for (Map<String, String> funcMap : SqlQueryUtil.selectStrMapList(conn, String.format(sql, schema, packName, name))) {
                StringBuilder builder = new StringBuilder();
                builder.append(type).append(" ").append(packName).append(".").append(funcMap.get("object_name"));
                common(type, funcMap, builder, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
        return result;
    }

    private void common(String type, Map<String, String> map, StringBuilder builder, List<Map<String, String>> retMaps) throws Exception {
        String funcName = map.get("object_name");
        String overload = map.get("overload");
        String paramSql = "select in_out, argument_name parameter, data_type type, '' value from all_arguments where owner = '%s' and package_name = '%s'\n" +
                " and object_name  = '%s'\n";
        if (StringUtils.isNotEmpty(overload)) {
            paramSql += " and overload = '" + overload + "'";
        }
        paramSql += " and data_level = 0 and argument_name is not null\n" +
                " order by position";
        List<Map<String, String>> paramMaps = SqlQueryUtil.selectStrMapList(conn, String.format(paramSql, schema, packName, funcName));
        List<Map<String, String>> listMap = new ArrayList<>();
        if (paramMaps.size() > 0) {
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
        if ("function".equals(type)) {
            builder.append(" return").append(" ").append(map.get("data_type"));
            retMap.put(funcName + "_" + overload, map.get("data_type"));
        } else {
            retMap.put(funcName + "_" + overload, "");
        }
        builder.append(";");
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("name", funcName + "_" + overload);
        dataMap.put("value", builder.toString());
        retMaps.add(dataMap);
        argueMap.put(funcName + "_" + overload, listMap);
    }

}
