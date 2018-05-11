package com.hhdb.csadmin.common.util.commonxmltool;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.hh.frame.common.log.LM;

public class XmlTools {
	public static final String NODEPATH_SPLIT = ".";

	/** 换行符. */
	public static final String NEW_LINE = System.getProperty("line.separator", "\n");

	/** 文件分隔符. */
	public static final String FILE_SEPARATOR = System.getProperty("file.separator", "\\");

	/** 缺省日期格式（java). */
	public static final java.lang.String DATE_FORMAT = "yyyy-MM-dd";

	/** 缺省日期时间格式(java). */
	public static final java.lang.String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 判断Collection是否为空.
	 * 
	 * @param collection
	 *            集合对象
	 * 
	 * @return 集合为空返回真
	 */
	public static boolean isEmpty(final Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}

	/**
	 * 判断Map是否为空.
	 * 
	 * @param map
	 *            Map对象
	 * 
	 * @return map为空返回真
	 */
	public static boolean isEmpty(final Map<?, ?> map) {
		return (map == null || map.isEmpty());
	}

	/**
	 * 判断数组是否为空.
	 * 
	 * @param obj
	 *            数组
	 * 
	 * @return 数组为空返回真
	 */
	public static boolean isEmpty(final Object[] obj) {
		return (obj == null || obj.length == 0);
	}

	/**
	 * 判断字符串是否为空.
	 * 
	 * @param str
	 *            字符串
	 * 
	 * @return 字符串为空返回真
	 */
	public static boolean isEmpty(final String str) {
		return !hasLength(str);
	}

	/**
	 * 判断字符串长度.
	 * 
	 * @param str
	 *            字符串
	 * 
	 * @return 真表示非空，假表示空字符串
	 */
	public static boolean hasLength(final CharSequence str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * 判断字符串长度.
	 * 
	 * @param str
	 *            字符串
	 * 
	 * @return 真表示非空，假表示空字符串
	 */
	public static boolean hasLength(final String str) {
		return hasLength((CharSequence) str);
	}

	/**
	 * 获取字符串.
	 * 
	 * @param map
	 *            Map对象
	 * @param key
	 *            键(不存在返回空字符串)
	 * 
	 * @return 字符串值
	 */
	public static String getString(final Map<?, ?> map, final Object key) {
		return getString(map, key, "");
	}

	/**
	 * 获取字符串.
	 * 
	 * @param map
	 *            Map对象
	 * @param key
	 *            键(不存在返回缺省值)
	 * @param defaultVal
	 *            缺省值
	 * 
	 * @return 字符串值
	 */
	public static String getString(final Map<?, ?> map, final Object key, final String defaultVal) {
		if (isEmpty(map) || key == null) {
			return defaultVal;
		}

		Object val = map.get(key);
		return val == null ? defaultVal : val.toString();
	}

	/**
	 * 根据类名构建实例.
	 * 
	 * @param className
	 *            类名
	 * @param loader
	 *            类加载器
	 * @return 类对象
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	public static Class<?> forName(String className) throws ClassNotFoundException {
		return Class.forName(className);
	}

	/**
	 * @Description 根据类名构建实例.
	 * @date 2014-5-30
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @return Object
	 * @exception
	 */
	public static Object forObject(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> clazz = forName(className);
		return clazz.newInstance();
	}

	/**
	 * 获取异常字符串.
	 * 
	 * @param t
	 *            异常对象 a
	 * @return 异常描述字符串
	 */
	public static String getThrowableStr(Throwable t) {
		if (t == null) {
			return "";
		}

		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);

			t.printStackTrace(pw);
			pw.flush();
			return sw.toString();
		} catch (Exception ex) {
			LM.error(LM.Model.CS.name(), ex);
			return ex.getMessage();
		}
	}

	/**
	 * 获取错误信息.
	 * 
	 * @param t
	 *            the t
	 * @return the error str
	 */
	public static String getErrorStr(Throwable t) {
		if (t == null) {
			return "";
		}

		Throwable inner = t;
		while (inner.getCause() != null) {
			inner = inner.getCause();
		}

		String msg = inner.getMessage();
		if (XmlTools.isEmpty(msg)) {
			msg = inner.getClass().getName();
		}
		return msg;
	}

	/**
	 * 把日期变量转换为字符串.
	 * 
	 * @param dtDate
	 *            要转换的日期变量
	 * 
	 * @return String 日期转换后的字符串(yyyy-MM-dd)
	 */
	public static String dateToString(Date dtDate) {
		SimpleDateFormat sdfDate = new SimpleDateFormat(DATE_FORMAT);
		return sdfDate.format(dtDate);
	}

	/**
	 * 把日期变量按指定的格式转换为字符串.
	 * 
	 * @param dtDate
	 *            要转换的日期变量
	 * @param strFormat
	 *            格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @return 日期转换后的字符串
	 */
	public static String dateToString(Date dtDate, String strFormat) {
		SimpleDateFormat df = new SimpleDateFormat(strFormat);
		return df.format(dtDate);
	}

	/**
	 * 把日期变量按datetime型（yyyy-MM-dd HH:mm:ss）转换为字符串.
	 * 
	 * @param dtDate
	 *            要转换的日期变量 return 日期转换后的字符串
	 * 
	 * @return the string
	 */
	public static String datetimeToString(Date dtDate) {
		SimpleDateFormat sdfDatetime = new SimpleDateFormat(DATETIME_FORMAT);
		return sdfDatetime.format(dtDate);
	}

	public static boolean CreateFile(String destFileName) {
		File file = new File(destFileName);
		if (file.exists()) {
			System.out.println("创建单个文件" + destFileName + "失败，目标文件已存在！");
			return false;
		}
		if (destFileName.endsWith(File.separator)) {
			System.out.println("创建单个文件" + destFileName + "失败，目标不能是目录！");
			return false;
		}
		if (!file.getParentFile().exists()) {
			System.out.println("目标文件所在路径不存在，准备创建。。。");
			if (!file.getParentFile().mkdirs()) {
				System.out.println("创建目录文件所在的目录失败！");
				return false;
			}
		}

		// 创建目标文件
		try {
			if (file.createNewFile()) {
				System.out.println("创建单个文件" + destFileName + "成功！");
				return true;
			} else {
				System.out.println("创建单个文件" + destFileName + "失败！");
				return false;
			}
		} catch (IOException e) {
			LM.error(LM.Model.CS.name(), "创建单个文件" +e.getMessage());
			return false;
		}
	}

	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {
			System.out.println("创建目录" + destDirName + "失败，目标目录已存在！");
			return false;
		}
		if (!destDirName.endsWith(File.separator))
			destDirName = destDirName + File.separator;
		// 创建单个目录
		if (dir.mkdirs()) {
			System.out.println("创建目录" + destDirName + "成功！");
			return true;
		} else {
			System.out.println("创建目录" + destDirName + "成功！");
			return false;
		}
	}

	public static String createTempFile(String prefix, String suffix, String dirName) {
		File tempFile = null;
		try {
			if (dirName == null) {
				// 在默认文件夹下创建临时文件
				tempFile = File.createTempFile(prefix, suffix);
				return tempFile.getCanonicalPath();
			} else {
				File dir = new File(dirName);
				// 如果临时文件所在目录不存在，首先创建
				if (!dir.exists()) {
					if (!XmlTools.createDir(dirName)) {
						System.out.println("创建临时文件失败，不能创建临时文件所在目录！");
						return null;
					}
				}
				tempFile = File.createTempFile(prefix, suffix, dir);
				return tempFile.getCanonicalPath();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("创建临时文件失败" + e.getMessage());
			return null;
		}
	}
}

