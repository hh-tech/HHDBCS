package com.hh.hhdb_admin.mgr.partition.comp;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.db_sql.db2.Db2PartitionBean;
import com.hh.frame.db_sql.db2.Db2PartitionSqlUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;

import java.sql.Connection;


public class Db2PartPanel extends AbsParPanel {
    
    public Db2PartPanel(Connection conn, JdbcBean jdbc, String schemaName, String tabName) throws Exception {
        super(conn, jdbc, schemaName, tabName);
        parType = "RANGE";
        
        firstTextInput = new TextInput();
        secondTextInput = new TextInput();
    }
    
    @Override
    public LastPanel getLastPanel() {
        HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
        layout.setRightWidth(50);
        HPanel panel = new HPanel(layout);
        panel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
        panel.add(new HeightComp(5));
        panel.add(getLabelInput(LangMgr.lang(Word2.LOWER_LIMIT) + " ：", firstTextInput));
        panel.add(getLabelInput(LangMgr.lang(Word2.UPPER_LIMIT) + " ：", secondTextInput));
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
        
        Db2PartitionBean bean = new Db2PartitionBean(tabName, nameInput.getValue(), parType);
        bean.setOwner(schemaName);
        bean.setLowerLimit(firstParam);
        bean.setUpperLimit(secondParam);
        return Db2PartitionSqlUtil.getAddParSql(jdbc.getCurrentVersion(), bean);
    }
}

