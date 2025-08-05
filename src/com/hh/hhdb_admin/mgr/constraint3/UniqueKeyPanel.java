package com.hh.hhdb_admin.mgr.constraint3;

import com.hh.frame.db_sql.TabKeySqlUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;

public class UniqueKeyPanel extends PrimaryKeyPanel{
	public UniqueKeyPanel(String schema,String tabName) throws Exception{
		super(schema, tabName);
	}
	@Override
	protected void inform() {
	}

	@Override
	protected String genSql() {
		return TabKeySqlUtil.getAddUniKeySql(CsVarUtil.getDbtype(),CsVarUtil.jdbc.getCurrentVersion(),schema, tabName, selPanel.getSelected());
	}
}
