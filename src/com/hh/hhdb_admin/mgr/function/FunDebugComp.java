package com.hh.hhdb_admin.mgr.function;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.ui.deBug_from.OrDebugForm;
import com.hh.hhdb_admin.mgr.function.util.DebugUtil;
import com.hh.hhdb_admin.mgr.function.util.FunUtil;

import javax.swing.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调试初始化页面
 */
public class FunDebugComp {
    private JdbcBean jdbcBean;
    private Connection conn;

    public HDialog dialog;
    private HTable hTable;

    private AbsFunMr funMr;
    private TreeMrNode treeNode;

    /**
     * 构造函数调试面板
     */
    public FunDebugComp(TreeMrNode treeNode,JdbcBean jdbcBean) {
        try {
            this.treeNode = treeNode;
            this.jdbcBean = jdbcBean;
            this.conn = ConnUtil.getConn(jdbcBean);
            this.funMr = AbsFunMr.genFunMr(DriverUtil.getDbType(jdbcBean),treeNode);

            DebugUtil.examine(treeNode.getSchemaName(),jdbcBean);
            show();
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame .getWindow(), FunctionMgr.getLang("error")+":"+e.getMessage());
        }
    }

    /**
     * 构造调试面板
     */
    public FunDebugComp(JdbcBean jdbcBean) {
        try {
            DebugUtil.examine(jdbcBean.getSchema(),jdbcBean);
            new OrDebugForm(jdbcBean,null);
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame .getWindow(), FunctionMgr.getLang("error")+":"+e.getMessage());
        }
    }

    private void show(){
        try {
            dialog = new HDialog(StartUtil.parentFrame, 600, 400){
                @Override
                protected void closeEvent() {
                    ConnUtil.close(conn);
                }
            };
            dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
            dialog.setWindowTitle(FunctionMgr.getLang("debug"));

            List<Map<String, String>> list = DebugUtil.getInPara(funMr,conn);
            dialog.setRootPanel(getParamTable(list));
            if(list.size() == 0){          //无参数则直接进入调试面板
                FunUtil.getDebugBaseForm(treeNode,jdbcBean, DebugUtil.getSql(hTable,funMr,conn),null);
                dialog.hide();
            } else {
                dialog.show();
            }
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame .getWindow(), FunctionMgr.getLang("error")+":"+e.getMessage());
        }
    }

    private HPanel getParamTable(List<Map<String, String>> list) throws Exception {
        HPanel hPanel = new HPanel();
        hTable = new HTable();
        DataCol parCol = new DataCol("parameter",FunctionMgr.getLang("parameter"));
        parCol.setCellEditable(false);
        hTable.addCols(parCol);
        DataCol typeCol = new DataCol("dbType",FunctionMgr.getLang("type"));
        typeCol.setCellEditable(false);
        hTable.addCols(typeCol);
        hTable.addCols(new DataCol("value",FunctionMgr.getLang("value")));
        hTable.hideSeqCol();
        hTable.setRowHeight(25);
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setWithScroll(hTable.getComp());
        hPanel.setLastPanel(lastPanel);
        hTable.load(list,1);

        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel toolBarPane = new HBarPanel(l);
        //调试按钮
        HButton sqlBut = new HButton(FunctionMgr.getLang("debug")) {
            @Override
            public void onClick() {
                FunUtil.getDebugBaseForm(treeNode,jdbcBean, DebugUtil.getSql(hTable,funMr,conn), getParam());
                dialog.dispose();
            }
        };
        sqlBut.setIcon(FunctionMgr.getIcon("formatsql"));
        toolBarPane.add(sqlBut);

        hPanel.add(toolBarPane);
        return hPanel;
    }

    private List<Map<String, String>> getParam() {
        List<Map<String, String>> pars = new ArrayList<Map<String, String>>();
        JTable parmtable = hTable.getComp();
        if (parmtable.isEditing()) parmtable.getCellEditor().stopCellEditing();

        int rows = parmtable.getRowCount();
        for (int i = 0; i < rows; i++) {
            Map<String, String> maps = new HashMap<String, String>();
            maps.put("name", parmtable.getValueAt(i, 0) + "");
            maps.put("type", parmtable.getValueAt(i, 1) + "");
            maps.put("value", parmtable.getValueAt(i, 2) + "");
            pars.add(maps);
        }
        return pars;
    }
}
