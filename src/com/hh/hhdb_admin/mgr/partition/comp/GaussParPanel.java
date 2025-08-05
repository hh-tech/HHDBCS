package com.hh.hhdb_admin.mgr.partition.comp;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.gauss.GaussParSqlUtil;
import com.hh.frame.db_sql.gauss.info.GaussParTabInfo;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

public class GaussParPanel extends AbsParPanel {
    public GaussParPanel(Connection conn, JdbcBean jdbc, String schemaName, String tabName) throws Exception {
        super(conn, jdbc, schemaName, tabName);
        String sql = GaussParSqlUtil.getParTabInfo(schemaName, tabName, GaussParTabInfo.class);
        String type = SqlQueryUtil.selectOneStrMap(conn, sql, GaussParTabInfo.class).get(GaussParTabInfo.PARTSTRATEGY);
        parType = GaussParSqlUtil.convertType(type);
        firstTextInput = new TextInput("first");
    }

    @Override
    public LastPanel getLastPanel() {
        HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
        layout.setRightWidth(100);
        HPanel panel = new HPanel(layout);
        panel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
        panel.add(new HeightComp(5));
        panel.add(getLabelInput(LangMgr.lang(Word2.UPPER_LIMIT) + "/" + LangMgr.lang(Word1.VALUE) + " ：", firstTextInput));
        panel.add(new HeightComp(5));

        LastPanel areaLast = new LastPanel();
        areaLast.setTitle(LangMgr.lang(Word2.CODE));
        areaLast.set(area.getComp());

        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(panel.getComp());
        lastPanel.set(areaLast.getComp());
        return lastPanel;
    }

    @Override
    public String getSql() {
        String sql = GaussParSqlUtil.getAddParSql(null, schemaName, tabName, nameInput.getValue(), parType, firstTextInput.getValue());
        if (StringUtils.isBlank(sql))
            PopDlgUtil.info(LangMgr.lang(Word3.DATABASE) + LangMgr.lang(Word4.NOT_SUPPORT));
        return sql;
    }
}
