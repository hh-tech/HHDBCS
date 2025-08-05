package com.hh.hhdb_admin.mgr.usr2.form;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.*;

/**
 * 权限表格
 */
public class GrantTableComp extends LastPanel {
    protected HTable hTable;
    protected CheckBoxInput allInput;
    
    
    public GrantTableComp()throws Exception {
        hTable = new HTable();
        hTable.setRowHeight(25);
        hTable.addCols(new DataCol("name", LangMgr.lang(Word2.KEY_NAME)));
        BoolCol boolCol = new BoolCol("grant", LangMgr.lang(Word2.GRANT)){
            @Override
            protected void actionPerformed(ActionEvent e) {
                if (!((JCheckBox)e.getSource()).isSelected()) {
                    allInput.setValue(Boolean.FALSE.toString());
                }
            }
        };
        boolCol.setWidth(200);
        hTable.addCols(boolCol);
    
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel toolBar = new HBarPanel(l);
        toolBar.add(allInput = new CheckBoxInput("ALL","全选") {
            @Override
            protected void onClick(ActionEvent e) {
                try {
                    if (null == allInput) return;
                    for (int i = 0; i < hTable.getRowCount(); i++) {
                        hTable.setValueAt(allInput.isChecked(),i,hTable.getColumn("grant").getColIndex());
                    }
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        });
        HPanel headPanel = new HPanel(new HDivLayout(GridSplitEnum.C6));
        SearchToolBar searchToolBar = new SearchToolBar(hTable);
        headPanel.add(toolBar, searchToolBar);
    
        setHead(headPanel.getComp());
        setWithScroll(hTable.getComp());
        hTable.load(new ArrayList<>(), 1);
    }

    public void refreshTable(List<Map<String, String>> list) throws SQLException {
        hTable.load(list, 1);
    }

    public Map<Boolean, List<String>> getNameMap() {
        Map<Boolean, List<String>> map = new HashMap<>();
        List<String> tlist = new LinkedList<>();
        List<String> flist = new LinkedList<>();
        for (HTabRowBean bean : hTable.getRowBeans(RowStatus.UPDATE)) {
            if (!bean.getCurrRow().get("grant").equalsIgnoreCase(bean.getOldRow().get("grant"))) {
                if (bean.getCurrRow().get("grant").equalsIgnoreCase(Boolean.TRUE.toString())) {
                    tlist.add(bean.getOldRow().get("name"));
                } else {
                    flist.add(bean.getOldRow().get("name"));
                }
            }
        }
        map.put(true,tlist);
        map.put(false,flist);
        return map;
    }
}
