package com.hh.hhdb_admin.mgr.monitor.base;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorUtil {

    public static void getColumnAndData(boolean flag, String info, List<String> columns, List<Map<String, String>> columnValues) {
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
            	String[] valueList = StringUtils.split(separated[i], '|');
//                String[] valueList = separated[i].split("\\u007C");
                List<String> valueListStr = Arrays.asList(valueList);
                Map<String, String> valueMap = new HashMap<>();
                for (int j = 0; j < columns.size(); j++) {
                    String columnStr = columns.get(j);
                    String valueStr = "";
                    if(j<valueListStr.size()) {
                    	valueStr = valueListStr.get(j);
                    }
                    valueMap.put(columnStr, valueStr);
                }
                columnValues.add(valueMap);
            }
        }
    }
}
