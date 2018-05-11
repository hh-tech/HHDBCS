package com.hhdb.csadmin.plugin.sql_book.util;

import org.hsqldb.util.DatabaseManagerSwing;

/**
 * hsql数据库查看
 * @author hhxd
 *
 */
public class Test {
	public static void main(String[] args) {
		//./db/mydb
		DatabaseManagerSwing dms = new DatabaseManagerSwing();
		dms.main();
	}
}
