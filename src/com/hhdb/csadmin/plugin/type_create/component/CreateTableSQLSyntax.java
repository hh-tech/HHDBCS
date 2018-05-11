package com.hhdb.csadmin.plugin.type_create.component;

public interface CreateTableSQLSyntax {

	/** SQL 'CREATE TABLE ' */
	String CREATE_TABLE = "CREATE TABLE ";

	String CREATE_FUNCTION = "CREATE FUNCTION ";
	/** SQL ' NOT NULL' */
	String NOT_NULL = "NOT NULL ";

	/** SQL 'DATE' */
	String DATE = "DATE";

	/** SQL 'pk_' */
	String PK_PREFIX = "pk_";

	/** SQL 'NUMBER' */
	String NUMBER = "NUMBER";

	/** SQL 'CONSTRAINT' */
	String CONSTRAINT = "CONSTRAINT ";

	/** ' RENAME CONSTRAINT ' */
	String RENAME_CONSTRAINT = " RENAME CONSTRAINT ";

	/** ' ADD CONSTRAINT ' */
	String ADD_CONSTRAINT = " ADD CONSTRAINT ";

	/** ' TO ' */
	String TO = " TO ";

	/** 换行 */
	String INDENT = "\n       ";

	/** SQL 'REFERENCES ' */
	String REFERENCES = " REFERENCES ";

	/** SQL ' PRIMARY' */
	String PRIMARY = " PRIMARY";

	/** SQL ' KEY ' */
	String KEY = " KEY ";

	/** SQL '\n' */
	char NEW_LINE = '\n';

	String NEW_LINE_2 = "\n    ";

	/** SQL ' ' */

	/** 'ALTER TABLE ' */
	String ALTER_TABLE = " ALTER TABLE ";

	/** ' ADD ' */
	String ADD = " ADD ";

	String ADD_COLUMN = " ADD COLUMN ";
	
	String ALTER_COLUMN = " ALTER COLUMN ";
	
	String TYPE = " TYPE ";
	
	String SPACE = " ";

	/** An empty <code>String</code> */
	String EMPTY = "";

	/** SQL '(' */
	String B_OPEN = "(";

	/** SQL ',' */
	char COMMA = ',';

	/** SQL ')' */
	char B_CLOSE = ')';

	/** SQL '.' */
	char DOT = '.';

	/** SQL ';' */
	char SEMI_COLON = ';';
	
	char EQUALS = '=';

	/** SQL ' DROP CONSTRAINT ' */
	String DROP_CONSTRAINT = " DROP CONSTRAINT ";
	
	String DROP_COLUMN = " DROP COLUMN ";

	String RENAME=" RENAME ";
	
	String SET=" SET ";
	
	String DROP=" DROP ";
	
	String RETURNS=" RETURNS ";
	
	String AS =" AS ";
	String BODY="$BODY$";
	
	String BEGIN=" BEGIN ";
	
	String END=" END ";
	
	String LANGUAGE=" LANGUAGE ";
	
	String VOLATILE=" VOLATILE";
	
	String ALTER = " ALTER ";
	
	String CREATE=" CREATE";
	
	String UNIQUE=" UNIQUE";
	
	String INDEX=" INDEX ";
	
	String USING=" USING ";
	
	String ON=" ON ";
	
	String CLUSTER=" CLUSTER";
	
	String CHECK=" CHECK ";
	
	String RULE=" RULE ";
	
	String REPLACE=" REPLACE";
	
	String OR=" OR";
	
	String INSTEAD=" INSTEAD";
	
}