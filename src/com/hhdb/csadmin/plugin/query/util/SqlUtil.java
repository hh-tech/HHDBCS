package com.hhdb.csadmin.plugin.query.util;

import com.hhdb.csadmin.plugin.query.syntax.Constants;

public class SqlUtil {
	public static boolean isFuncEnd(String sql) {
		int firstDollorindex = sql.indexOf("$$");
		if (firstDollorindex == -1) {
			//return false;
			return isFuncEnd2(sql);
		} else {
			int lastDollorindex = sql.substring(firstDollorindex + 2).indexOf(
					"$$");
			if (lastDollorindex == -1) {
				return false;
			} else {
				int funcEndIndex = sql
						.substring(firstDollorindex + 2 + lastDollorindex)
						.toLowerCase().indexOf("plhhsql");
				if (funcEndIndex == -1) {
					if (sql.substring(firstDollorindex + 4 + lastDollorindex)
							.trim().equals(Constants.QUERY_DELIMITER)) {
						return true;
					}
					return false;
				} else {
					return true;
				}
			}
		}
	}

	private static boolean isFuncEnd2(String sql) {
		int firstDollorindex = sql.toUpperCase().indexOf("$BODY$");
		if (firstDollorindex == -1) {
			return false;
		} else {
			int lastDollorindex = sql.toUpperCase().substring(firstDollorindex + 6).indexOf(
					"$BODY$");
			if (lastDollorindex == -1) {
				return false;
			} else {
				int funcEndIndex = sql
						.substring(firstDollorindex + 6 + lastDollorindex)
						.toLowerCase().indexOf("plhhsql");
				if (funcEndIndex == -1) {
					if (sql.substring(firstDollorindex + 12 + lastDollorindex)
							.trim().equals(Constants.QUERY_DELIMITER)) {
						return true;
					}
					return false;
				} else {
					return true;
				}
			}
		}
	}
}
