package com.hh.hhdb_admin.common.util;

import com.hh.frame.common.base.DBTypeEnum;
import org.apache.commons.lang3.StringUtils;

public class DbCmdStrUtil {
    /**
     * 放到sql命令中时的字符处理
     * 如：hhdb中数据库中取出的 模式名称是SSS,含有大写则需要使用双引号。则sql预览显示的sql语句为  ALTER SCHEMA "SSS" 。。。。
     * 如：hhdb中数据库中取出的 模式名称是sss,不含大写则不需要使用双引号。则sql预览显示的sql语句为  ALTER SCHEMA sss 。。。。
     */
    public static String toDbCmdStr(String str, DBTypeEnum dbType) {
        char[] chars = new char[]{'<', '>', '%', '\'', '-', '$', '#', '\\', '@', '=', '?', '*', '/', '|', '&', '$', '!', '~', '(', ')', '^', '|', ';', ','};
        
        String prefix = "\"", suffix = "\"";
		boolean containSpecial = false;
		boolean needTrans = false;
        if (StringUtils.containsAny(str, chars)) {
			containSpecial = true;
        }
        
        String retStr = str;
        switch (dbType) {
			case hhdb:
			case pgsql:
				str = str.replaceAll("\"","\"\"");
				needTrans = !str.trim().equals(str.toLowerCase());
				break;
			case oracle:
			case db2:
			case dm:
				str = str.replaceAll("\"","\"\"");
				needTrans = !str.trim().equals(str.toUpperCase());
				break;
			case sqlserver:
				prefix = "[";
				suffix = "]";
				needTrans = false;
				break;
			case mysql:
				prefix = suffix = "`";
				str = str.replaceAll("`","``");
				needTrans = false;
				break;
			default:
				break;
		}

		if(needTrans||containSpecial){
			return decorate(str,prefix,suffix);
		}
        return retStr;
    }
    
    /**
	 * 将name加上前缀后缀
	 *
	 * @param name 名称
	 * @return
	 */
	private static String decorate(String name, String prefix, String suffix) {
		if (StringUtils.isNoneBlank(name)) {
			name = name.trim();
			name = prefix + name + suffix;
		}
		return name;
	}


	public static void main(String[] args) {
		System.out.println(DBTypeEnum.hhdb +":"+toDbCmdStr("$basd\"'",DBTypeEnum.hhdb));
		System.out.println(DBTypeEnum.mysql +":"+toDbCmdStr("$basd\"",DBTypeEnum.mysql));
		System.out.println(DBTypeEnum.mysql +":"+toDbCmdStr("$basd\"'",DBTypeEnum.mysql));
		System.out.println(DBTypeEnum.mysql +":"+toDbCmdStr("$basd\"''",DBTypeEnum.mysql));
		System.out.println(DBTypeEnum.sqlserver +":"+toDbCmdStr("$basd\"''",DBTypeEnum.sqlserver));
		System.out.println(DBTypeEnum.db2 +":"+toDbCmdStr("$basd\"''",DBTypeEnum.db2));
		System.out.println(DBTypeEnum.pgsql +":"+toDbCmdStr("#basd\"''",DBTypeEnum.pgsql));
		System.out.println(DBTypeEnum.pgsql +":"+toDbCmdStr("aaaa",DBTypeEnum.pgsql));
		System.out.println(DBTypeEnum.oracle +":"+toDbCmdStr("aaaa",DBTypeEnum.oracle));
		System.out.println(DBTypeEnum.mysql +":"+toDbCmdStr("aaaa",DBTypeEnum.mysql));
		System.out.println(DBTypeEnum.sqlserver +":"+toDbCmdStr("aaaa",DBTypeEnum.sqlserver));
		System.out.println(DBTypeEnum.db2 +":"+toDbCmdStr("aaaa",DBTypeEnum.db2));
		System.out.println(DBTypeEnum.pgsql +":"+toDbCmdStr("BBBB",DBTypeEnum.pgsql));
		System.out.println(DBTypeEnum.oracle +":"+toDbCmdStr("BBBB",DBTypeEnum.oracle));
		System.out.println(DBTypeEnum.mysql +":"+toDbCmdStr("BBBB",DBTypeEnum.mysql));
		System.out.println(DBTypeEnum.sqlserver +":"+toDbCmdStr("BBBB",DBTypeEnum.sqlserver));
		System.out.println(DBTypeEnum.db2 +":"+toDbCmdStr("BBBB",DBTypeEnum.db2));
	}
}
