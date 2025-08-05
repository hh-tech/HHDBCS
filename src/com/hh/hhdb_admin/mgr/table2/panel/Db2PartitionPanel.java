package com.hh.hhdb_admin.mgr.table2.panel;

import com.hh.frame.create_dbobj.table2.CreateNewTabTool;
import com.hh.frame.create_dbobj.table2.db.Db2CreateNewTab;
import com.hh.frame.json.Json;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.ListCol;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.mgr.table2.TableUtil;
import com.hh.hhdb_admin.mgr.table2.column.SelectColumn;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Db2PartitionPanel extends AbsCodePanel {
    private static final String PAR_COL_NAME = "parColName";
    private static final String PAR_TYPE = "parType";
    private static final String UPPER_LIMIT = "upperLimit";
    private static final String LOWER_LIMIT = "lowerLimit";
    private static final String INTERVAL = "interval";
    
    
    public Db2PartitionPanel(HTable colTable) {
        table = new HTable();
    
        List<String> values = new ArrayList<>();
        values.add("");
        for (Db2CreateNewTab.ParTabType value : Db2CreateNewTab.ParTabType.values()) {
            values.add(value.name());
        }
        table.addCols(new ListCol(PAR_TYPE, LangMgr.lang(Word2.TYPE), values ));
        table.addCols(new SelectColumn(PAR_COL_NAME, LangMgr.lang(Word1.COLUMN), colTable));
        table.addCols(new DataCol(LOWER_LIMIT, LangMgr.lang(Word2.LOWER_LIMIT)));
        table.addCols(new DataCol(UPPER_LIMIT, LangMgr.lang(Word2.UPPER_LIMIT)));
        table.addCols(new DataCol(INTERVAL, LangMgr.lang(Word2.INCREMENT2)));
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
            String lower_limit = TableUtil.getTableCol(be,LOWER_LIMIT);
            String upper_limit = TableUtil.getTableCol(be,UPPER_LIMIT);
            String interval = TableUtil.getTableCol(be,INTERVAL);
            if (StringUtils.isBlank(colNames) || StringUtils.isBlank(type) || StringUtils.isBlank(lower_limit)
                    || StringUtils.isBlank(upper_limit) || StringUtils.isBlank(interval)) {
                continue;
            }
            colNames = Json.parse(colNames).asObject().getString(JsonCol.__TEXT);
    
            Db2CreateNewTab newTab = (Db2CreateNewTab) tabTool.getNewTab();
            newTab.setParType(Db2CreateNewTab.ParTabType.valueOf(type));
            newTab.setParColName(colNames);
            newTab.setLowerLimit(lower_limit);
            newTab.setUpperLimit(upper_limit);
            newTab.setInterval(interval);
        }
    }
}

