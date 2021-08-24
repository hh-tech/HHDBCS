package com.hh.hhdb_admin.mgr.table.common;

/**
 * @author oyx
 * @date 2020-10-20  11:19:41
 * 请使用com.hh.frame.create_dbobj.table中的枚举类型 如OraDataTypeEnum
 */
@Deprecated
public class DataType {
	public enum TypeEnum {
		/**
		 * HHDB常用数据类型
		 */
		SERIAL, INT2, INT4, INT8, FLOAT, FLOAT8, CHAR, VARCHAR, TEXT, DATE, DECIMAL, TIMESTAMP, BOOLEAN, BYTEA, XML, NUMERIC;
	}

	public enum OracleTypeEnum {
		/**
		 * Oracle 常用数据类型
		 */
		BFILE, BLOB, CHAR, CLOB, DATE, DECIMAL, FLOAT, INT, INTEGER, LONG, NCHAR, NUMBER, NUMERIC,
		NVARCHAR2, SMALLINT, TIMESTAMP, VARCHAR, VARCHAR2
	}

	public enum MySqlTypeEnum {
		/**
		 * MySQl常用数据类型
		 */
		BIGINT, BINARY, BIT, BLOB, CHAR, DATE, DATETIME, DECIMAL, DOUBLE, ENUM, FLOAT, INT, INTEGER, NUMERIC, SET, SMALLINT,
		TEXT, TIME, TIMESTAMP, TINYINT, VARBINARY, VARCHAR

	}


}
