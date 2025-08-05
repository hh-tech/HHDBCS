package com.hh.hhdb_admin.mgr.constraint3;

import com.hh.frame.db_sql.TabKeySqlUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.comp.colsel.ColSelPanel;

public class PrimaryKeyPanel extends PkgAbsBasePanel{
	protected ColSelPanel selPanel;
	public PrimaryKeyPanel(String schema,String tabName) throws Exception{
		super(schema, tabName);

		setHead(getBarPanel().getComp());

		selPanel = new ColSelPanel();
		selPanel.setTitle(LangMgr.sLang(CsL.SELECT_COLUMN));
		selPanel.loadData(CsVarUtil.jdbc, CsVarUtil.conn, schema, tabName, null);

		HSplitPanel split = new HSplitPanel(false);
		split.setLastComp4One(selPanel);
		split.setLastComp4Two(getSqlPanel(sqlTextArea));
		split.setDividerLocation(200);

		set(split.getComp());
	}

	@Override
	protected void check() throws Exception {
		if (selPanel.getSelected().isEmpty()) {
			throw new Exception(LangMgr.lang(Word4.NOT_NULL));
		}
	}

	@Override
	protected void inform() {
	}

	@Override
	protected String genSql() {
		return TabKeySqlUtil.getAddPriKeySql(CsVarUtil.getDbtype(),CsVarUtil.jdbc.getCurrentVersion(),schema, tabName, selPanel.getSelected());
	}
}
