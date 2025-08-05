package com.hh.hhdb_admin.mgr.table2.panel;

import com.hh.frame.create_dbobj.table2.CreateNewTabTool;
import com.hh.frame.create_dbobj.table2.db.PgCreateNewTab;
import com.hh.frame.json.Json;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.ListCol;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.mgr.table2.TableUtil;
import com.hh.hhdb_admin.mgr.table2.column.SelectColumn;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class PgPartitionPanel extends AbsCodePanel {
    private static final String PAR_COL_NAME = "parColName";
    private static final String PAR_TYPE = "parType";
    
    
    public PgPartitionPanel(HTable colTable) {
        table = new HTable();
    
        List<String> values = new ArrayList<>();
        values.add("");
        for (PgCreateNewTab.ParTabType value : PgCreateNewTab.ParTabType.values()) {
            values.add(value.name());
        }
        table.addCols(new ListCol(PAR_TYPE, LangMgr.lang(Word2.TYPE), values ));
        table.addCols(new SelectColumn(PAR_COL_NAME, LangMgr.lang(Word1.COLUMN), colTable));
        table.setRowHeight(30);
        table.hideSeqCol();
        table.setEvenBgColor(table.getOddBgColor());
    
        initBar();
    }
    
    @Override
    public void build(CreateNewTabTool tabTool) {
        TableUtil.stopEditing(table);
        List<HTabRowBean> row = null != table.getRowBeans(null) ? table.getRowBeans(null) : new LinkedList<>();
        for (HTabRowBean be : row) {
            String colNames = TableUtil.getTableCol(be,PAR_COL_NAME);
            String type = TableUtil.getTableCol(be,PAR_TYPE);
            if (StringUtils.isBlank(colNames) || StringUtils.isBlank(type)) {
                continue;
            }
            colNames = Json.parse(colNames).asObject().getString(JsonCol.__TEXT);
            
            PgCreateNewTab pgTab = (PgCreateNewTab) tabTool.getNewTab();
            pgTab.setParType(PgCreateNewTab.ParTabType.valueOf(type));
            pgTab.setParColName(colNames);
        }
    }
}

