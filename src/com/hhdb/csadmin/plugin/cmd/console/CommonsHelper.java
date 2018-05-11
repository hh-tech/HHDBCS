package com.hhdb.csadmin.plugin.cmd.console;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import com.hh.frame.common.log.LM;


public abstract class CommonsHelper {
	public static String getClassPath() {
		return System.getProperty("user.dir") + "/etc/";
	}

	public static String[] listMap2Array(List<Map<String, String>> columnsList) {
		String[] columns = new String[columnsList.size()];
		for (int i = 0; i < columnsList.size(); i++) {
			Map<String, String> column = columnsList.get(i);
			columns[i] = column.get("name");
		}
		return columns;
	}

	public static Object[][] listMap2Array2(Object[] columns, List<Map<String, Object>> dataList) {
		Object[][] datas = new Object[dataList.size()][columns.length];
		for (int i = 0; i < dataList.size(); i++) {
			Map<String, Object> data = dataList.get(i);
			for (int j = 0; j < columns.length; j++) {
				datas[i][j] = data.get(columns[j]);
			}
		}
		return datas;
	}

	public static int parseInt(Object str) {
		if (str == null || str.equals("")) {
			return 0;
		}
		return Integer.parseInt(str.toString());
	}

	public static void invokeLater(Runnable runnable) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(runnable);
		} else {
			runnable.run();
		}
	}

	public static void invokeAndWait(Runnable runnable) {
		if (!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(runnable);
			} catch (InterruptedException e) {
				LM.error(LM.Model.CS.name(), e);
			} catch (InvocationTargetException e) {
				LM.error(LM.Model.CS.name(), e);
			}
		} else {
			runnable.run();
		}
	}

	public static boolean isNull(String value) {
		return value == null || value.trim().length() == 0;
	}
	
	public static String nullOfStr(Object value) {
		if(value==null){
			return "";
		}
		return value.toString();
	}


	public static boolean isNotNull(String value) {
		return !isNull(value);
	}

	public static String getIP() {
		InetAddress address = null;
		try {
			address = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			LM.error(LM.Model.CS.name(), e);
		}
		return address.getHostAddress();
	}

	public static void main(String[] args) {
		System.out.println(ipCheck("localhost"));
	}

	/**
	 * 
	 * 获取密码输入框字符
	 * 
	 * @param c
	 * @return
	 * @see
	 */
	public static String charToString(char[] c) {
		if (c == null || c.length <= 0) {
			return "";
		}
		StringBuffer sBuffer = new StringBuffer(10);
		for (int i = 0; i < c.length; i++) {
			sBuffer.append(c[i]);
			c[i] = 0;
		}
		return sBuffer.toString();
	}
	
	/**
     * 判断IP地址的合法性，这里采用了正则表达式的方法来判断
     * return true，合法
     * */
    public static boolean ipCheck(String text) {
        if (text != null && !text.isEmpty()) {
        	
        	if(text.trim().equalsIgnoreCase("localhost")){
        		return true;
        	}
        	
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."+
                      "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
                      "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
                      "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        return false;
    }
    
    public static boolean isNumeric(String str){ 
	   Pattern pattern = Pattern.compile("[0-9]*"); 
	   Matcher isNum = pattern.matcher(str);
	   if( !isNum.matches() ){
	       return false; 
	   } 
	   return true; 
	}
}
