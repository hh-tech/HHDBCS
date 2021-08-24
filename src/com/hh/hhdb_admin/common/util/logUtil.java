package com.hh.hhdb_admin.common.util;

import org.apache.logging.log4j.Logger;

import com.hh.frame.common.util.LM;

/**
 * 日志工具类
 * @author hexu
 */
public class logUtil {
	private static Logger logger=null;
	
	public static void setLogger(String csadmin) {
		logger = LM.newLogger(csadmin);
	}
	
	public static void logFunBegin(Class<?> clazz, String funName) {
        doLog(true, clazz.getSimpleName(), "进入{" + funName + '}');
    }

    public static void logFunEnd(Class<?> clazz, String funName) {
        doLog(true, clazz.getSimpleName(), "完成{" + funName + '}');
    }

    public static void info(String modelName, String msg, Object... args) {
        doLog(true, modelName, msg, args);
    }

    public static void error(String modelName, String msg, Object... args) {
        doLog(false, modelName, msg, args);
    }
	
	public static void error(String modelName, Exception e) {
        if (logger == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage()).append("\n");
        StackTraceElement[] stacks = e.getStackTrace();
        for (StackTraceElement se : stacks) {
            sb.append(se.toString()).append("\n");
        }
        if (!LM.hasLogger(modelName)) {
        	 LM.setLogger(modelName, logger);
        }
        LM.error(modelName, sb.toString());
    }
	
	public static void debug(String modelName, String format, Object... args) {
		if (logger == null)
			return;
		if (LM.hasLogger(modelName)) {
			LM.debug(modelName, format, args);
			return;
		}
		LM.setLogger(modelName, logger);
		LM.debug(modelName, format, args);
	}
	
	private static void doLog(boolean isInfo, String modelName, String msg, Object... args) {
        if (logger == null) return;
        if (!LM.hasLogger(modelName)) {
            LM.setLogger(modelName, logger);
        }
        if (isInfo) LM.info(modelName, msg, args);
        else LM.error(modelName, msg, args);
    }
}
