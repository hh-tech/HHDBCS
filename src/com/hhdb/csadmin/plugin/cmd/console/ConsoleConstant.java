package com.hhdb.csadmin.plugin.cmd.console;

import com.hhdb.csadmin.common.util.StartUtil;

class ConsoleConstant {
	/**
	 * swing控制台字体名称
	 */
	static final String FONT_NAME = "宋体";

	/**
	 * swing控制台字体大小
	 */
	static final int FONT_SIZE = 16;
	static final int FONT_SIZE_14 = 14;

	/**
	 * 查询后显示行数
	 */
	static final int SHOW_ROWS = 20;

	/**
	 * 显示更多行提示
	 */
	static final String MORE_PROMPT = "-- More --";

//	public static final String MSG_HELP = "输入 \"help\" 来获取帮助信息\n";
	static final String MSG_START_TIMING = "启用计时功能\n";
	static final String MSG_CLOSE_TIMING = "关闭计时功能\n";
	static final String MSG_INVALID_SWITCH_COMMAND = "无效的数据库切换命令\n";
	static final String MSG_INIT_PGCMD_FAILED = "初始化PG查询命令\" \\* \"失败\n";
	static final String MSG_UNKNOWN_ERROR = "未知的错误";
	static final String MSG_SWITCH_FAILED = "切换数据库失败, 保留上一次连接\n";
	static final String MSG_CANCEL_REQUEST_FAILED = "终止进行中的请求失败\n";
	
	static final String REGEX_CLEAR = "\\clear";
	static final String REGEX_TIMING= "\\timing";
	static final String REGEX_SWITCH_DB= "\\\\c[\\s]+[\\S]+";
	static final String REGEX_RESTORE= "\\\\i[\\s]+[\\S]+";
	static final String REGEX_FETCH_COUNT= "\\\\set[\\s]+fetch_count[\\s]+[\\d]+;?";
	static final String REGEX_D_PATTERN= "d[S]?[+]?\\s+\\S+";
	
	static final String SQL_SELECT_OID = "SELECT c.oid, n.nspname, c.relname FROM "+StartUtil.prefix
			+"_catalog."+StartUtil.prefix+"_class c LEFT JOIN "+StartUtil.prefix+"_catalog."+StartUtil.prefix+"_namespace n ON n.oid = c.relnamespace WHERE c.relname ~ ? AND "+StartUtil.prefix+"_catalog."+StartUtil.prefix+"_table_is_visible(c.oid) ORDER BY 2, 3;";

	static final String SQL_SELECT_REL = "SELECT c.relchecks, c.relkind, c.relhasindex, c.relhasrules, c.relhastriggers, c.relhasoids, '', c.reltablespace, CASE WHEN c.reloftype = 0 THEN '' ELSE c.reloftype::"+StartUtil.prefix+"_catalog.regtype::"+StartUtil.prefix+"_catalog.text END, c.relpersistence, c.relreplident FROM "+StartUtil.prefix+"_catalog."+StartUtil.prefix+"_class c LEFT JOIN "+StartUtil.prefix+"_catalog."+StartUtil.prefix+"_class tc ON (c.reltoastrelid = tc.oid) WHERE c.oid = ?;";
}
