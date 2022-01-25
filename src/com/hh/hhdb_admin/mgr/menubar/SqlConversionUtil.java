package com.hh.hhdb_admin.mgr.menubar;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.dbtrans.mysql2hhdb.Mysql2hhdbTool;
import com.hh.frame.dbtrans.ora2mysql.Ora2MysqlTool;
import com.hh.frame.ora2pg1.Ora2PgTool;
import com.hh.frame.parser.AbsStmt;
import com.hh.frame.parser.ParserUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ouyangxu
 * @date 2021-12-10 0010 15:20:21
 */
public class SqlConversionUtil {
	public static final String SUFFIX = ";";

	/**
	 * @param srcDbType  源数据库类型
	 * @param destDbType 目标数据库类型
	 * @param srcSql     源sql语句
	 * @return 转换完成的sql语句
	 * @throws Exception 数据库异常
	 */
	public static String convert(DBTypeEnum srcDbType, DBTypeEnum destDbType, String srcSql) throws Exception {
		StringBuilder convertSql = new StringBuilder();
		List<DBTypeEnum> destTypeList = getDestTypeList(srcDbType);
		if (!destTypeList.contains(destDbType)) {
			return String.format("暂不支持%s -->> %s的转换", srcDbType, destDbType);
		}
		if (srcDbType == DBTypeEnum.oracle) {
			List<AbsStmt> oraStmts = ParserUtil.getOraStmts(srcSql);
			switch (destDbType) {
				case pgsql:
				case hhdb:
					Ora2PgTool ora2PgTool = new Ora2PgTool(null, new ArrayList<>());
					for (AbsStmt abs : oraStmts) {
						List<AbsStmt> absStmtList = ora2PgTool.toPgStmt(abs);
						convertSql.append(appendAbsStmt(absStmtList));
					}
					break;
				case mysql:
					Ora2MysqlTool ora2MySqlTool = new Ora2MysqlTool(new ArrayList<>());
					for (AbsStmt abs : oraStmts) {
						List<AbsStmt> absStmtList = ora2MySqlTool.toMysqlStmt(abs);
						convertSql.append(appendAbsStmt(absStmtList));
					}
					break;
				default:
			}
		} else if ((srcDbType == DBTypeEnum.mysql)) {
			if (destDbType == DBTypeEnum.hhdb || destDbType == DBTypeEnum.pgsql) {
				Mysql2hhdbTool mysql2hhdbTool = new Mysql2hhdbTool(new ArrayList<>());
				List<AbsStmt> mysqlStmts = ParserUtil.getMysqlStmts(srcSql);
				for (AbsStmt mysqlStmt : mysqlStmts) {
					List<AbsStmt> absStmtList = mysql2hhdbTool.toHHdbStmt(mysqlStmt);
					convertSql.append(appendAbsStmt(absStmtList));
				}
			}
		}
		return convertSql.toString();
	}

	public static String appendAbsStmt(List<AbsStmt> absStmtList) {
		StringBuilder convertSql = new StringBuilder();
		for (AbsStmt absStmt : absStmtList) {
			convertSql.append(absStmt.toString()).append(SUFFIX).append("\n");
		}
		return convertSql.toString();
	}

	/**
	 * 根据源数据库返回支持转换的数据库类型
	 *
	 * @param dbTypeEnum 源数据库类型
	 * @return 支持转换的目标数据库类型集合
	 */
	public static List<DBTypeEnum> getDestTypeList(DBTypeEnum dbTypeEnum) {
		List<DBTypeEnum> destTypeList;
		switch (dbTypeEnum) {
			case oracle:
				destTypeList = Arrays.asList(DBTypeEnum.mysql, DBTypeEnum.hhdb, DBTypeEnum.pgsql);
				break;
			case mysql:
				destTypeList = Arrays.asList(DBTypeEnum.hhdb, DBTypeEnum.pgsql);
				break;
			default:
				return new ArrayList<>();
		}
		return destTypeList;
	}

	/**
	 * @return 支持转换的源数据库类型
	 */
	public static List<DBTypeEnum> getSrcTypeEnumList() {
		return Arrays.asList(DBTypeEnum.oracle, DBTypeEnum.mysql);
	}

	/**
	 * 将枚举类型集合转成字符串集合
	 *
	 * @param enumList 枚举类型集合
	 * @return 字符串集合
	 */

	public static List<String> getEnumToStrList(List<DBTypeEnum> enumList) {
		if (enumList == null) {
			return null;
		}
		return enumList.stream().map(Enum::name).collect(Collectors.toList());
	}
}
