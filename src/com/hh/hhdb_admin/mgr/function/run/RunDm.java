package com.hh.hhdb_admin.mgr.function.run;

import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.*;

public class RunDm extends Run {

    private final String packName;
    private final boolean isPack;

    public RunDm(Connection conn, String schema, String packName, String name) {
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
        String sql = "select in_out, argument_name parameter, data_type type " +
                "from all_arguments where owner = '%s' ";
        if (isPack) {
            sql += " and argument_name != 'RET'";
            sql += " and package_name = '" + packName + "'";
        } else {
            sql += " and package_name is null";
        }
        sql += " and object_name = '%s' order by position";
        for (Map<String, String> map : SqlQueryUtil.selectStrMapList(conn, String.format(sql, schema, name))) {
            if ("V_RET".equals(map.get("parameter"))) {
                retType = map.get("type");
            } else {
                paramLists.add(map);
            }
        }
    }

    private List<Map<String, String>> getData() {
        String funcSql = "select distinct object_name, overload, data_type from all_arguments\n" +
                " where owner = '%s' and package_name = '%s' and object_name = '%s'\n" +
                "and data_level = 0 and argument_name = 'RET' order by overload";
        List<Map<String, String>> retMaps = new ArrayList<>(getDataLists(funcSql, "function"));
        String procSql = "select distinct b.object_name, b.overload, b.data_type\n" +
                "  from all_arguments b\n" +
                " where b.owner = 'SYSDBA'\n" +
                "   and b.package_name = 'AAA'\n" +
                "   and b.object_name = 'SAYHELLO'\n" +
                "   and b.data_level = 0\n" +
                "   and not exists (select 1\n" +
                "          from all_arguments a\n" +
                "         where a.owner = b.owner\n" +
                "           and a.package_name = b.package_name\n" +
                "           and a.object_name = b.object_name\n" +
                "           and a.data_level = b.data_level\n" +
                "           and a.overload = b.overload\n" +
                "           and a.argument_name = 'RET'\n" +
                "         order by a.overload)\n" +
                " order by b.overload";
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
        String paramSql = "select in_out, argument_name parameter, data_type type, '' value from all_arguments" +
                " where owner = '%s' and package_name = '%s'\n" +
                " and object_name  = '%s' \n";
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
                if (!"RET".equals(paramMap.get("parameter"))) {
                    listMap.add(paramMap);
                    builder.append(paramMap.get("parameter")).append(" ").append("DEC".equals(paramMap.get("type")) ? "NUMBER" : paramMap.get("type"));
                    if (i < paramMaps.size() - 1) {
                        builder.append(", ");
                    }
                }
                i++;
            }
            builder.append(")");
        }
        if ("function".equals(type)) {
            String ret = "DEC".equals(map.get("data_type")) ? "NUMBER" : map.get("data_type");
            builder.append(" return").append(" ").append(ret);
            retMap.put(funcName + "_" + overload, ret);
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
