package com.hh.hhdb_admin.mgr.table2.panel;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.create_dbobj.table2.CreateNewTabTool;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.table2.TableUtil;

import java.util.HashMap;
import java.util.Map;

/*
 * Author:Zhang Dagang
 * Date:2024年2月20日
 */
public abstract class AbsCodePanel {
    protected HPanel hPanel;
    protected HBarPanel hBarPanel;
    protected HTable table;
    protected HButton addButton;
    protected HButton delButton;
    
    public AbsCodePanel() {
        hPanel = new HPanel();
        hBarPanel = new HBarPanel(TableUtil.newBarPan(AlignEnum.LEFT));
    }
    
    public abstract void build(CreateNewTabTool tabTool);
    
    protected void initBar() {
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setHead(hBarPanel.getComp());
        lastPanel.setWithScroll(table.getComp());
        hPanel.setLastPanel(lastPanel);
        addCol();
    }
    
    protected void createAddButton(String text) {
        if (addButton == null) {
            addButton = new HButton(text, FaSolidEnum.PLUS) {
                @Override
                protected void onClick() {
                    TableUtil.stopEditing(table);
                    addCol();
                }
            };
            hBarPanel.add(addButton);
        }
    }
    
    protected void createDelButton(String text) {
        if (delButton == null) {
            delButton = new HButton(text, FaSolidEnum.TRASH_CAN) {
                @Override
                protected void onClick() {
                    TableUtil.stopEditing(table);
                    int[] row = table.getComp().getSelectedRows();
                    if (row.length > 0) {
                        if (PopDlgUtil.confirm(CsVarUtil.parentFrame, LangMgr.sLang(CsL.SURE_DELETE))) {
                            table.deleteSelectRow();
                        }
                    }
                }
            };
            hBarPanel.add(delButton);
        }
    }
    
    public void addCol() {
        Map<String, String> map = new HashMap<>();
        map.put("id", "");
        table.add(map);
    }
    
    public HPanel gethPanel() {
        return hPanel;
    }
    
    public HTable getTable() {
        return table;
    }
}
