package com.hh.hhdb_admin.mgr.function.run;

import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.sun.DefaultTableCellRenderer;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 显示同名函数
 */
public abstract class Run {

    protected Connection conn;
    protected String schema;
    protected String name;
    protected Map<String, List<Map<String, String>>> argueMap;
    protected Map<String, String> retMap;
    protected List<Map<String, String>> paramLists;
    protected String retType = "";
    protected HDialog dialog;
    protected HTable table;

    protected Run(Connection conn, String schema, String name) {
        this.conn = conn;
        this.schema = schema;
        this.name = name;
        this.argueMap = new HashMap<>();
        this.retMap = new HashMap<>();
        this.paramLists = new ArrayList<>();
        this.dialog = new HDialog(CsVarUtil.parentFrame, 800, 600){
            @Override
            protected void closeEvent() {
                ConnUtil.close(conn);
            }
        };
        HPanel panel = new HPanel();
        table = new HTable() {
            @Override
            protected void onDClick(HTabRowBean rowBean) {
                dialog.dispose();
                String name = rowBean.getOldRow().get("name");
                paramLists = argueMap.get(name);
                retType = retMap.get(name);
                next(table.getSelectedRowBeans());
            }
        };
        table.hideSeqCol();
        table.setDClick(true);
        table.setCellEditable(false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170)));
        table.getComp().getTableHeader().setDefaultRenderer(renderer);
        DataCol nameCol = new DataCol("name", "name");
        nameCol.setShow(false);
        table.addCols(nameCol, new DataCol("value", ""));
        LastPanel lastPanel = new LastPanel();
        lastPanel.setWithScroll(table.getComp());
        panel.setLastPanel(lastPanel);
        ((JDialog) dialog.getWindow()).setResizable(true);
        dialog.setWindowTitle(LangMgr.lang(Word2.CHOOSE));
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setRootPanel(panel);
        dialog.setStatusBar(new HBarPanel());
        dialog.getWindow().setAlwaysOnTop(dialog.getWindow().isAlwaysOnTopSupported());
    }

    public List<Map<String, String>> getParamLists() {
        return paramLists;
    }

    public abstract boolean isOverWrite() throws Exception;

    public abstract void handParams() throws Exception;

    public void show() {
        dialog.show();
    }
    
    /**
     * 选择
     * @param lisi  当前选择行
     */
    public void next(List<HTabRowBean> lisi) {

    }

    protected static HGridPanel getWithLabelInput(String label, AbsInput input) {
        HGridLayout gridLayout = new HGridLayout(GridSplitEnum.C3);
        HGridPanel gridPanel = new HGridPanel(gridLayout);
        LabelInput labelInput = new LabelInput(label);
        gridPanel.setComp(1, labelInput);
        gridPanel.setComp(2, input);
        return gridPanel;
    }
}
