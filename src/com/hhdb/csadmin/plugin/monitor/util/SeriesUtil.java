package com.hhdb.csadmin.plugin.monitor.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeriesUtil {
	/**
	 * 解析获取CPU信息
	 * @param retMap
	 * @param resultCpuInfo
	 */
	public static  void getServerCpuPerfMap(List<Map<String, Object>> resultCpuInfo,List<String> columnNames ,List<Map<String, Object>> columnValueList) {
		String cpuInfo = resultCpuInfo.toString();
		int start = cpuInfo.lastIndexOf("{");
		int end = cpuInfo.indexOf("}");
		String cpuInfoResults = cpuInfo.substring(start + 1, end).trim();
		String[] separated = cpuInfoResults.split("\n");

		String[] column = null;

		for (int i = 0; i < separated.length; i++) {
			if (i == 0) {
				String columnStr = separated[i];
				int num = columnStr.indexOf("=");
				String columnStrResults = columnStr.substring(num + 1, columnStr.length());
				column = columnStrResults.split("\\u007C");
				columnNames.addAll(Arrays.asList(column));
			} else if (separated[i].contains("-------")) {
				continue;
			} else {
				String[] valueList = separated[i].split("\\u007C");
				List<String> valueListStr = Arrays.asList(valueList);
				Map<String, Object> valueMap = new HashMap<String, Object>();
				for (int j = 0; j < columnNames.size(); j++) {
					String columnStr = columnNames.get(j);
					String valueStr = valueListStr.get(j);
					valueMap.put(columnStr, valueStr);
				}
				columnValueList.add(valueMap);
			}
		}
//		String cpuUser = (String) columnValueList.get(0).get("用户使用率");
//		String cpuSys = (String) columnValueList.get(0).get("系统使用率");
//		double cpuUserNum = Double.parseDouble(cpuUser.substring(0, cpuUser.length() - 1));
//		double cpuSysNum = Double.parseDouble(cpuSys.substring(0, cpuSys.length() - 1));
//		DecimalFormat df = new DecimalFormat("######0");
//		double cpuValue = cpuUserNum + cpuSysNum;
//		int cpu = Integer.parseInt(df.format(cpuValue * 100));
	}
	
	/**
	 * 公共处理数据
	 * 
	 * @param retMap
	 * @param resultCpuInfo
	 */
	public static void getServerValueMap(List<Map<String, Object>> resultCpuInfo,List<String> columnNames ,List<Map<String, Object>> columnValueList) {
		String cpuInfo = resultCpuInfo.toString();
		int start = cpuInfo.lastIndexOf("{");
		int end = cpuInfo.indexOf("}");
		String cpuInfoResults = cpuInfo.substring(start + 1, end).trim();
		String[] separated = cpuInfoResults.split("\n");

		String[] column = null;

		for (int i = 0; i < separated.length; i++) {
			if (i == 0) {
				String columnStr = separated[i];
				int num = columnStr.indexOf("=");
				String columnStrResults = columnStr.substring(num + 1, columnStr.length());
				column = columnStrResults.split("\\u007C");
				columnNames.addAll(Arrays.asList(column));
			} else if (separated[i].contains("-------")) {
				continue;
			} else {
				String[] valueList = separated[i].split("\\u007C");
				List<String> valueListStr = Arrays.asList(valueList);
				Map<String, Object> valueMap = new HashMap<String, Object>();
				for (int j = 0; j < columnNames.size(); j++) {
					String columnStr = columnNames.get(j);
					String valueStr = valueListStr.get(j);
					valueMap.put(columnStr, valueStr);
				}
				columnValueList.add(valueMap);
			}
		}
	}
	/**
	 * 内存处理数据
	 * 
	 * @param retMap
	 * @param resultCpuInfo
	 */
	public static void getServerShowMemMap(List<Map<String, Object>> resultCpuInfo,List<String> columnNames ,List<Map<String, Object>> columnValueList,Map<String,Integer> data) {
		String cpuInfo = resultCpuInfo.toString();
		int start = cpuInfo.lastIndexOf("{");
		int end = cpuInfo.indexOf("}");
		String cpuInfoResults = cpuInfo.substring(start + 1, end).trim();
		String[] separated = cpuInfoResults.split("\n");
		String[] column = null;
		for (int i = 0; i < separated.length; i++) {
			if (i == 0) {
				String columnStr = separated[i];
				int num = columnStr.indexOf("=");
				String columnStrResults = columnStr.substring(num + 1, columnStr.length());
				column = columnStrResults.split("\\u007C");
				columnNames.addAll(Arrays.asList(column));
			} else if (separated[i].contains("-------")) {
				continue;
			} else {
				String[] valueList = separated[i].split("\\u007C");
				List<String> valueListStr = Arrays.asList(valueList);
				Map<String, Object> valueMap = new HashMap<String, Object>();
				for (int j = 0; j < columnNames.size(); j++) {
					String columnStr = columnNames.get(j);
					String valueStr = valueListStr.get(j);
					valueMap.put(columnStr, valueStr);
				}
				columnValueList.add(valueMap);
			}
		}
		String user = (String) columnValueList.get(0).get("使用内存");
		String total = (String) columnValueList.get(0).get("总内存");
		int userMem = Integer.parseInt(user.substring(0, user.length() - 1));
		int memTotal = Integer.parseInt(total.substring(0, total.length() - 1));
		data.put("userMem", userMem);
		data.put("memTotal", memTotal);
	}
	
	/**
	 * 进程数据处理
	 * 
	 * @param retMap
	 * @param resultCpuInfo
	 */
	public static void getServerPsValueMap(List<Map<String, Object>> resultCpuInfo, List<String> columnNames,List<Map<String, Object>> columnValueList ,
			List<String> columnNameDcLy,List<Map<String, Object>> columnValueDcLy ,List<String> columnNameAt,List<Map<String, Object>> columnValueAt ) {
		String cpuInfo = resultCpuInfo.toString();
		int start = cpuInfo.lastIndexOf("{");
		int end = cpuInfo.indexOf("}");
		String cpuInfoResults = cpuInfo.substring(start + 1, end).trim();
		String[] resultsInfo = cpuInfoResults.split("=========================");

		// 进程
		String[] separated = resultsInfo[0].split("\n");
		String[] column = null;
		for (int i = 0; i < separated.length; i++) {
			if (i == 0) {
				String columnStr = separated[i];
				int num = columnStr.indexOf("=");
				String columnStrResults = columnStr.substring(num + 1, columnStr.length());
				column = columnStrResults.split("\\u007C");
				columnNames.addAll(Arrays.asList(column));
			} else if (separated[i].contains("-------")) {
				continue;
			} else {
				String[] valueList = separated[i].split("\\u007C");
				List<String> valueListStr = Arrays.asList(valueList);
				Map<String, Object> valueMap = new HashMap<String, Object>();
				for (int j = 0; j < columnNames.size(); j++) {
					String columnStr = columnNames.get(j);
					String valueStr = valueListStr.get(j);
					valueMap.put(columnStr, valueStr);
				}
				columnValueList.add(valueMap);
			}
		}

		// 动态库
		String[] separatedDcLy = resultsInfo[1].split("\n");
		String[] columns = null;
		for (int i = 0; i < separatedDcLy.length; i++) {
			if (i == 0 || i == 1) {
				String columnStr = separatedDcLy[i];
				if (columnStr.equals("")) {
					continue;
				}
				columns = columnStr.split("\\u007C");
				columnNameDcLy.addAll(Arrays.asList(columns));
			} else if (separatedDcLy[i].contains("-------")) {
				continue;
			} else {
				String[] valueList = separatedDcLy[i].split("\\u007C");
				List<String> valueListStr = Arrays.asList(valueList);
				Map<String, Object> valueMap = new HashMap<String, Object>();
				for (int j = 0; j < columnNameDcLy.size(); j++) {
					String columnStr = columnNameDcLy.get(j);
					String valueStr = valueListStr.get(j);
					valueMap.put(columnStr, valueStr);
				}
				columnValueDcLy.add(valueMap);
			}
		}

		String[] separatedAt = resultsInfo[2].split("\n");
		String[] columnsAt = null;
		for (int i = 0; i < separatedAt.length; i++) {
			if (i == 0 || i == 1) {
				String columnStr = separatedAt[i];
				if (columnStr.equals("")) {
					continue;
				}
				columnsAt = columnStr.split("\\u007C");
				columnNameAt.addAll(Arrays.asList(columnsAt));
			} else if (separatedAt[i].contains("-------")) {
				continue;
			} else {
				String[] valueList = separatedAt[i].split("\\u007C");
				List<String> valueListStr = Arrays.asList(valueList);
				Map<String, Object> valueMap = new HashMap<String, Object>();
				for (int j = 0; j < columnNameAt.size(); j++) {
					String columnStr = columnNameAt.get(j);
					String valueStr = valueListStr.get(j);
					valueMap.put(columnStr, valueStr);
				}
				columnValueAt.add(valueMap);
			}
		}
	}
}
