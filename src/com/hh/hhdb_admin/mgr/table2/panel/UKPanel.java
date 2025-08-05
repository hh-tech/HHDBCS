package com.hh.hhdb_admin.mgr.table2.panel;

import com.hh.frame.create_dbobj.table2.CreateNewTabTool;
import com.hh.frame.create_dbobj.table2.CreateNewTabUk;
import com.hh.frame.json.Json;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.mgr.table2.TableUtil;
import com.hh.hhdb_admin.mgr.table2.column.SelectColumn;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class UKPanel extends AbsCodePanel {
    private static final String UNIQUE_COL_NAME = "uniqueColName";
    
    
    public UKPanel(HTable colTable) {
        table = new HTable();
        table.addCols(new SelectColumn(UNIQUE_COL_NAME, LangMgr.lang(Word1.COLUMN), colTable));
        table.setRowHeight(30);
        table.hideSeqCol();
        table.setEvenBgColor(table.getOddBgColor());
        
        createAddButton(LangMgr.sLang(CsL.ADD_UK));
        createDelButton(LangMgr.sLang(CsL.DELETE_UK));
        initBar();
        
    }
    
    @Override
    public void build(CreateNewTabTool tabTool) {
        TableUtil.stopEditing(table);
        List<CreateNewTabUk> uks = new ArrayList<>();
        List<HTabRowBean> row =  null != table.getRowBeans(null) ? table.getRowBeans(null) : new LinkedList<>();
        for (HTabRowBean be : row) {
            String json = TableUtil.getTableCol(be,UNIQUE_COL_NAME);
            if (StringUtils.isNotBlank(json)) {
                String colName = Json.parse(json).asObject().getString(JsonCol.__TEXT);
                if (StringUtils.isBlank(colName)) {
                    continue;
                }
                CreateNewTabUk uk = new CreateNewTabUk(Arrays.stream(colName.split(",")).collect(Collectors.toList()));
                uks.add(uk);
            }
        }
        tabTool.setUks(uks);
    }
}
