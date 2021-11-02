package com.hh.hhdb_admin.mgr.monitor.util;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.lang.LangEnum;
import com.hh.frame.lang.LangMgr2;

public class MonitorUtil {

    public static void getColumnAndData(boolean flag, String info, List<String> columns, List<Map<String, String>> columnValues, Map<String, Integer> data) {
        if (flag) {
            int start = info.lastIndexOf("{");
            int end = info.indexOf("}");
            info = info.substring(start + 1, end).trim();
        }
        String[] separated = info.split("\n");
        String[] column;
        for (int i = 0; i < separated.length; i++) {
            if (i == 0) {
                String columnStr = separated[i];
                int num = columnStr.indexOf("=");
                String columnStrResults = columnStr.substring(num + 1);
                column = columnStrResults.split("\\u007C");
                columns.addAll(Arrays.asList(column));
            } else if (!separated[i].contains("-------")) {
                String[] valueList = separated[i].split("\\u007C");
                List<String> valueListStr = Arrays.asList(valueList);
                Map<String, String> valueMap = new HashMap<>();
                for (int j = 0; j < columns.size(); j++) {
                    String columnStr = columns.get(j);
                    String valueStr = valueListStr.get(j);
                    valueMap.put(columnStr, valueStr);
                }
                columnValues.add(valueMap);
            }
        }
        if (null != data) {
            String user;
            String total;
            if (LangMgr2.getDefaultLang().equals(LangEnum.EN)) {
                user = columnValues.get(0).get("USE_MEM");
                total = columnValues.get(0).get("TOTAL_MEM");
            } else {
                user = columnValues.get(0).get("使用内存");
                total = columnValues.get(0).get("总内存");
            }
            int userMem = 0;
            if (!StringUtils.isEmpty(user)) {
                userMem = Integer.parseInt(user.substring(0, user.length() - 1));
            }
            int memTotal = 0;
            if (!StringUtils.isEmpty(total)) {
                memTotal = Integer.parseInt(total.substring(0, total.length() - 1));
            }
            data.put("userMem", userMem);
            data.put("memTotal", memTotal);
        }
    }

    /**
     * 获取服务器网络监控信息
     *
     * @param bool 老版本需要带后缀名
     * @return sql
     */
    public static String getNetInfo(Boolean bool) {
        String sql;
        String name = bool ? "eth_util.sh" : "eth_util";
        if (LangMgr2.getDefaultLang().equals(LangEnum.EN)) {
            sql = "select sys_util('" + name + "','lang=en eth_list') as eth_list;";
        } else {
            sql = "select sys_util('" + name + "','eth_list') as eth_list;";
        }
        return sql;
    }

    /**
     * 实时获取硬盘详细使用信息
     *
     * @param bool 老版本需要带后缀名
     * @return sql
     */
    public static String getDiskUsage(Boolean bool) {
        String sql;
        String name = bool ? "disk_util.sh" : "disk_util";
        if (LangMgr2.getDefaultLang().equals(LangEnum.EN)) {
            sql = "select sys_util('" + name + "','lang=en show_disk_usage') as show_disk_usage;";
        } else {
            sql = "select sys_util('" + name + "','show_disk_usage') as show_disk_usage;";
        }
        return sql;
    }

    /**
     * 实时获取硬盘简要使用信息
     *
     * @param bool 老版本需要带后缀名
     * @return sql
     */
    public static String getDiskPerf(Boolean bool) {
        String sql;
        String name = bool ? "disk_util.sh" : "disk_util";
        if (LangMgr2.getDefaultLang().equals(LangEnum.EN)) {
            sql = "select sys_util('" + name + "','lang=en show_disk_perf') as show_disk_perf;";
        } else {
            sql = "select sys_util('" + name + "','show_disk_perf') as show_disk_perf;";
        }
        return sql;
    }

    /**
     * 获取操作系统信息
     *
     * @param bool 老版本需要带后缀名
     * @return sql
     */
    public static String getOsInfo(Boolean bool) {
        String sql;
        String name = bool ? "sys_util.sh" : "sys_util";
        if (LangMgr2.getDefaultLang().equals(LangEnum.EN)) {
            sql = "select sys_util('" + name + "','lang=en os_info') as os_info;";
        } else {
            sql = "select sys_util('" + name + "','os_info') as os_info;";
        }
        return sql;
    }

    /**
     * 获取实时内存信息
     *
     * @param bool 老版本需要带后缀名
     * @return sql
     */
    public static String getMem(Boolean bool) {
        String sql;
        String name = bool ? "mem_util.sh" : "mem_util";
        if (LangMgr2.getDefaultLang().equals(LangEnum.EN)) {
            sql = "select sys_util('" + name + "','lang=en show_mem') as show_mem;";
        } else {
            sql = "select sys_util('" + name + "','show_mem') as show_mem;";
        }
        return sql;
    }

    /**
     * 获取CPU信息
     *
     * @param bool 老版本需要带后缀名
     * @return sql
     */
    public static String getCpuInfo(Boolean bool) {
        String sql;
        String name = bool ? "cpu_util.sh" : "cpu_util";
        if (LangMgr2.getDefaultLang().equals(LangEnum.EN)) {
            sql = "select  sys_util('" + name + "','lang=en cpu_info') as cpu_info;";
        } else {
            sql = "select  sys_util('" + name + "','cpu_info') as cpu_info;";
        }
        return sql;
    }

    /**
     * 获取CPU实时简要信息
     *
     * @param bool 老版本需要带后缀名
     * @return sql
     */
    public static String getCpuPerf(Boolean bool) {
        String sql;
        String name = bool ? "cpu_util.sh" : "cpu_util";
        if (LangMgr2.getDefaultLang().equals(LangEnum.EN)) {
            sql = "select  sys_util('" + name + "','lang=en cpu_perf') as cpu_perf;";
        } else {
            sql = "select  sys_util('" + name + "','cpu_perf') as cpu_perf;";
        }
        return sql;
    }

    public static boolean isTrue(List<Map<String, Object>> li, String name) {
        boolean bb = false;
        for (Map<String, Object> mp : li) {
            if (mp.get("name").equals(name)) {
                bb = true;
            }
        }
        return bb;
    }

    /**
     * 获取所有安装的扩展
     *
     * @return 扩展集合
     * @throws Exception 异常
     */
    public static List<Map<String, Object>> getExtendList(Connection conn, DBTypeEnum dbTypeEnum) throws Exception {
        String prefix = "hh";
        if (dbTypeEnum.equals(DBTypeEnum.hhdb)) {
            prefix = "hh";
        } else if (dbTypeEnum.equals(DBTypeEnum.pgsql)) {
            prefix = "pg";
        }
        String sql = "select extname as name,extowner,extnamespace,extrelocatable,extversion,extconfig,extcondition from " + prefix + "_extension;";
        return SqlQueryUtil.select(conn, sql);
    }

    /**
     * 安装扩展插件
     *
     * @param extendName 扩展名称
     * @throws Exception 异常
     */
    public static void installExtend(String extendName, Connection conn) throws Exception {
        String sql = "create extension " + extendName;
        SqlExeUtil.executeUpdate(conn, sql);
    }

    public static boolean isHhOrPg(DBTypeEnum dbTypeEnum) {
        return DBTypeEnum.hhdb.equals(dbTypeEnum) || DBTypeEnum.pgsql.equals(dbTypeEnum);
    }

}
