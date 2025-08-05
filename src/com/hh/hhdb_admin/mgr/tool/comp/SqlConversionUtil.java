package com.hh.hhdb_admin.mgr.tool.comp;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.dbtrans.ToDbStmtEnum;
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
	 * @param errorSql   错误信息
	 * @return 转换完成的sql语句
	 * @throws Exception 数据库异常
	 */
	public static String convert(DBTypeEnum srcDbType, DBTypeEnum destDbType, String srcSql,StringBuilder errorSql) throws Exception {
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
					List<com.hh.frame.ora2pg1.ConverBean> converBeanList1 = new ArrayList<>();
					Ora2PgTool ora2PgTool = new Ora2PgTool(null, converBeanList1);
					for (AbsStmt abs : oraStmts) {
						List<AbsStmt> absStmtList = ora2PgTool.toPgStmt(abs);
						convertSql.append(appendAbsStmt(absStmtList));
					}
					converBeanList1.forEach(a -> errorSql.append(a.getMsg()+"\n"));
					break;
				case mysql:
				case mariadb:
					List<com.hh.frame.dbtrans.ConverBean> converBeanList = new ArrayList<>();
					Ora2MysqlTool ora2MySqlTool = new Ora2MysqlTool(converBeanList);
					for (AbsStmt abs : oraStmts) {
						List<AbsStmt> absStmtList = ora2MySqlTool.toMysqlStmt(abs);
						convertSql.append(appendAbsStmt(absStmtList));
					}
					converBeanList.forEach(a -> errorSql.append(a.getMsg()+"\n"));
					break;
				default:
			}
		} else if ((srcDbType == DBTypeEnum.mysql)) {
			if (destDbType == DBTypeEnum.hhdb || destDbType == DBTypeEnum.pgsql) {
				List<com.hh.frame.dbtrans.ConverBean> converBeanList = new ArrayList<>();
				Mysql2hhdbTool mysql2hhdbTool = new Mysql2hhdbTool(converBeanList);
				List<AbsStmt> mysqlStmts=ParserUtil.getSplitStmts(DBTypeEnum.mysql,null, srcSql);
				for (AbsStmt mysqlStmt : mysqlStmts) {
					String sql =mysqlStmt.toString();
					String ss = mysql2hhdbTool.toHHdbSqlStr(sql,ToDbStmtEnum.toDbStmtEnum(mysqlStmt.getType(),sql));
					convertSql.append(ss);
				}
				converBeanList.forEach(a -> errorSql.append(a.getMsg()+"\n"));
			}
		}
		return convertSql.toString();
	}

	public static String appendAbsStmt(List<AbsStmt> absStmtList) {
		StringBuilder convertSql = new StringBuilder();
		for (AbsStmt absStmt : absStmtList) {
			convertSql.append(absStmt).append(SUFFIX).append("\n");
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
			case mariadb:
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
