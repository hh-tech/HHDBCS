package com.hh.hhdb_admin.mgr.partition.comp;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.greenplum.GreenPlumParTabSqlUtil;
import com.hh.frame.db_sql.pg.info.PgPartitionBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;

import java.sql.Connection;


public class GreenPlumParPanel extends AbsParPanel {
    private TextAreaInput descriptionInput;

    public GreenPlumParPanel(Connection conn, JdbcBean jdbc, String schemaName, String tabName) throws Exception {
        super(conn, jdbc, schemaName, tabName);
        String sql = "select distinct partitiontype from pg_partitions where schemaname='%s' and tablename ='%s'";
        String str = SqlQueryUtil.selectValue(conn, String.format(sql, schemaName, tabName));
        if (str.contains("list")) {
            parType = "LIST";
        } else if (str.contains("range")) {
            parType = "RANGE";
        }
        firstTextInput = new TextInput("first");
        secondTextInput = new TextInput("second");
        descriptionInput = new TextAreaInput("description", "", 5);
    }

    @Override
    public LastPanel getLastPanel() {
        HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
        layout.setRightWidth(50);
        HPanel panel = new HPanel(layout);
        panel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
        panel.add(new HeightComp(5));
        if (parType.contains("LIST")) {
            panel.add(getLabelInput(LangMgr.lang(Word2.CONDITION) + " ：", firstTextInput));
        } else if (parType.contains("RANGE")) {
            panel.add(getLabelInput(LangMgr.lang(Word2.LOWER_LIMIT) + " ：", firstTextInput));
            panel.add(getLabelInput(LangMgr.lang(Word2.UPPER_LIMIT) + " ：", secondTextInput));
        }
        panel.add(getLabelInput(LangMgr.lang(Word2.NOTES)+" ：", descriptionInput));
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
        String firstParam = firstTextInput.getValue();
        String secondParam = secondTextInput.getValue();

        PgPartitionBean bean = new PgPartitionBean(tabName, nameInput.getValue(), parType);
        bean.setOwner(schemaName);
        bean.setLowerLimit(firstParam);
        bean.setConditions(firstParam);
        bean.setModulus(firstParam);
        bean.setUpperLimit(secondParam);
        bean.setRemainder(secondParam);
        bean.setComment(descriptionInput.getValue());
        return GreenPlumParTabSqlUtil.getAddParSql(jdbc.getCurrentVersion(), bean);
    }
}
