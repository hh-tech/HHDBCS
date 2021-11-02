package com.hh.hhdb_admin.mgr.function.run;

import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.dbobj2.version.VersionUtil;
import com.hh.frame.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.*;

public class RunHhPg extends Run {

    private final int bigVer;
    private final String prefix;

    public RunHhPg(Connection conn, String schema, String name, HHdbPgsqlPrefixEnum prefixEnum) {
        super(conn, schema, name);
        this.prefix = prefixEnum.name();
        this.bigVer = VersionUtil.getDbVersion(conn).getBigVer();
        table.load(getData(), 1);
    }

    @Override
    public boolean isOverWrite() throws Exception {
        String sql = "select p.proname as name \n" +
                "FROM %s_proc p\n" +
                "where p.pronamespace in (select oid from %s_namespace where nspname = '%s') \n" +
                "and p.proname = '%s' order by p.proname";
        return SqlQueryUtil.selectStrMapList(conn, String.format(sql, prefix, prefix, schema, name)).size() > 1;
    }

    @Override
    public void handParams() throws Exception {
        List<Map<String, String>> maps = getData();
        if (maps.size() > 0) {
            Map<String, String> map = maps.get(0);
            common(map, new StringBuilder());
            paramLists = argueMap.get(map.get("name"));
            retType = retMap.get(map.get("name"));
        }
    }

    private List<Map<String, String>> getData() {
        List<Map<String, String>> retMaps = new ArrayList<>();
        try {
            String sql = "SELECT p.proname as name, typ.typname as type,  p.oid id, " + (bigVer > 10 ? "p.prokind" : "p.proisagg") + " as kind\n" +
                    "FROM %s_proc p JOIN %s_type typ ON typ.oid = prorettype WHERE p.pronamespace IN (select oid from %s_namespace where nspname = '%s')\n" +
                    "and p.proname = '%s' ORDER BY kind";
            for (Map<String, String> map : SqlQueryUtil.selectStrMapList(conn, String.format(sql, prefix, prefix, prefix, schema, name))) {
                String name = map.get("name");
                StringBuilder builder = new StringBuilder();
                builder.append("f".equals(map.get("kind")) ? "function " : "procedure ").append(schema).append(".").append(name);
                common(map, builder);
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("name", map.get("id"));
                dataMap.put("value", builder.toString());
                retMaps.add(dataMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retMaps;
    }

    private LinkedHashMap<String, JsonObject> getParams(String id) throws Exception {
        LinkedHashMap<String, JsonObject> retMap = new LinkedHashMap<>();
        String sql = "SELECT proname,oid,%s_catalog.%s_get_function_arguments(oid) AS arguments,prosrc FROM %s_proc where oid=%s;";
        for (Map<String, Object> map : SqlQueryUtil.select(true, conn, String.format(sql, prefix, prefix, prefix, id))) {
            String str = "".equals(map.get("arguments").toString().trim()) ? null : map.get("arguments").toString();
            if (StringUtils.isEmpty(str)) {
                return null;
            }
            if (str.contains(",")) {
                Arrays.stream(str.split(",")).forEach(string -> setJsonMap(retMap, string.trim().split(" ")));
            } else {
                setJsonMap(retMap, str.trim().split(" "));
            }
        }
        return retMap;
    }

    private static void setJsonMap(Map<String, JsonObject> jmap, String[] s) {
        boolean bool = s[0].equalsIgnoreCase("IN") || s[0].equalsIgnoreCase("INOUT") || s[0].equalsIgnoreCase("OUT");
        StringBuilder type = new StringBuilder();
        for (int i = bool ? 2 : 1; i < s.length; i++) {
            type.append(type.length() > 0 ? " " + s[i] : s[i]);
        }
        JsonObject json = new JsonObject();
        json.add("type", type.toString());
        json.add("out_in", bool ? s[0] : "IN");
        jmap.put(bool ? s[1] : s[0], json);
    }

    private void common(Map<String, String> map, StringBuilder builder) throws Exception {
        List<Map<String, String>> listMap = new ArrayList<>();
        Map<String, JsonObject> jsonMap = getParams(map.get("id"));
        if (null != jsonMap && jsonMap.size() > 0) {
            builder.append("(");
            int i = 0;
            for (String key : jsonMap.keySet()) {
                JsonObject jsb = jsonMap.get(key);
                if (jsb != null) {
                    Map<String, String> argueMap = new HashMap<>();
                    argueMap.put("in_out", jsb.getString("out_in"));
                    argueMap.put("parameter", key);
                    argueMap.put("type", jsb.getString("type"));
                    argueMap.put("value", "");
                    listMap.add(argueMap);
                    builder.append(key).append(" ").append(jsb.getString("type"));
                }
                if (i < jsonMap.keySet().size() - 1) {
                    builder.append(", ");
                }
                i++;
            }
            builder.append(")");
        }
        if ("f".equals(map.get("kind"))) {
            builder.append(" return ").append(map.get("type"));
            retMap.put(map.get("id"), map.get("type"));
        } else {
            retMap.put(map.get("id"), "");
        }
        builder.append(";");
        argueMap.put(map.get("id"), listMap);
    }

}
