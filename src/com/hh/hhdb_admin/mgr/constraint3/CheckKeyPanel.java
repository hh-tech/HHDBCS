package com.hh.hhdb_admin.mgr.constraint3;

import com.hh.frame.db_sql.TabKeySqlUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import org.apache.commons.lang3.StringUtils;

public class CheckKeyPanel extends PkgAbsBasePanel {
    private final HTextArea checkTextArea;

    public CheckKeyPanel(String schema, String tabName) {
        super(schema, tabName);

        setHead(getBarPanel().getComp());

        checkTextArea = getSqlTextArea();
        LastPanel checkPanel = getSqlPanel(checkTextArea);
        checkPanel.setTitle(LangMgr.lang(Word3.CHECK_KEY));

        HSplitPanel split = new HSplitPanel(false);
        split.setLastComp4One(checkPanel);
        split.setLastComp4Two(getSqlPanel(sqlTextArea));
        split.setDividerLocation(200);

        set(split.getComp());
    }

    @Override
    protected void check() throws Exception {
		if (StringUtils.isBlank(checkTextArea.getText())) {
			throw new Exception(LangMgr.lang(Word4.NOT_NULL));
		}
    }

    @Override
    protected void inform() {

    }

    @Override
    protected String genSql() {
        return TabKeySqlUtil.getAddCheckSql(CsVarUtil.getDbtype(),CsVarUtil.jdbc.getCurrentVersion(),schema, tabName, checkTextArea.getText());
    }
}
