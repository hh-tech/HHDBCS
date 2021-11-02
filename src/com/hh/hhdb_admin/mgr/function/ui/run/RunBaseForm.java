package com.hh.hhdb_admin.mgr.function.ui.run;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditorTextArea;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import com.hh.hhdb_admin.mgr.function.run.*;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class RunBaseForm extends LastPanel {
    protected TreeMrType type;
    protected AbsFunMr funMr;
    protected JdbcBean jdbcBean;
    protected Connection conn;      //运行连接
    protected RunTool runTool;
    protected Run run;

    protected HButton stopBut, runBut;
    protected QueryEditorTextArea textArea = new QueryEditorTextArea(false);
    protected TextAreaInput messageText = new TextAreaInput("messageText");
    protected HTable paTable;
    protected HTabPane hTabPane = new HTabPane();
    
    protected String packName;        //包名
    protected List<Map<String, String>> parMap = new LinkedList<>();     //参数集合
    
    /**
     * 初始化运行面板
     * @param jdbcBean
     */
    public RunBaseForm(AbsFunMr funMr,JdbcBean jdbcBean,String packName) throws Exception {
        super(false);
        this.funMr = funMr;
        this.jdbcBean = jdbcBean;
        this.packName = packName;
        this.conn = ConnUtil.getConn(jdbcBean);
        this.type = funMr.treeNode.getType();

        HSplitPanel splitPane = new HSplitPanel(false);
        splitPane.setSplitWeight(0.5);
        JSplitPane jsp = splitPane.getComp();
        jsp.setLeftComponent(textArea.getComp());
        jsp.setRightComponent(getTabPane().getComp());
        textArea.getTextArea().setRows(15);

        setHead(getHBarPanel().getComp());
        set(splitPane.getComp());
        getParam();
    }

    /**
     * 获取对象参数信息
     * @return
     */
    protected abstract List<Map<String, String>> infoParam();

    /**
     * 获取运行sql
     * @param valMap    参数
     */
    protected abstract String getSql(Map<String, String> valMap) throws Exception;
    
    /**
     * 运行
     */
    protected abstract void runFun();


    protected void show() {
        HDialog dialog = new HDialog(StartUtil.parentFrame,1000, 800) {
            @Override
            protected void closeEvent() {
                finish();
                dispose();
            }
        };
        dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
        dialog.setWindowTitle(FunctionMgr.getLang("run"));
        HPanel hPanel = new HPanel();
        hPanel.setLastPanel(this);
        dialog.setRootPanel(hPanel);
        dialog.show();
    }
    
    protected void finish() {
        if (null != runTool) runTool.cancel();
        ConnUtil.close(conn);
    }
    
    private HBarPanel getHBarPanel() {
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel toolBarPane = new HBarPanel(l);
        //运行
        runBut = new HButton(FunctionMgr.getLang("run")) {
            @Override
            public void onClick() {
                try {
                    if (!ConnUtil.isConnected(conn)) conn = ConnUtil.getConn(jdbcBean);
                    stopBut.setEnabled(true);
                    runBut.setEnabled(false);
                    if (paTable.getComp().isEditing()) paTable.getComp().getCellEditor().stopCellEditing();
                    runFun();
                    hTabPane.selectPanel("information");
                }catch (Exception e){
                    e.printStackTrace();
                    messageText.setValue(e.getMessage());
                }
            }
        };
        runBut.setIcon(FunctionMgr.getIcon("formatsql"));
        //停止
        stopBut = new HButton(FunctionMgr.getLang("stop")) {
            @Override
            public void onClick() {
                stopBut.setEnabled(false);
                runBut.setEnabled(true);
                finish();
            }
        };
        stopBut.setIcon(FunctionMgr.getIcon("debugstop"));
        stopBut.setEnabled(false);
        toolBarPane.add(runBut, stopBut);

        return toolBarPane;
    }

    /**
     * 获取信息下方Tab
     * @return
     */
    private HTabPane getTabPane() throws Exception{
        hTabPane.setCloseBtn(false);
        //添加参数表格
        paTable = new HTable();
        paTable.setRowHeight(25);
        paTable.hideSeqCol();
        DataCol dc = new DataCol("parameter", FunctionMgr.getLang("parameter"));
        dc.setCellEditable(false);
        DataCol dcType = new DataCol("dbType",FunctionMgr.getLang("type"));
        dcType.setCellEditable(false);
        paTable.addCols(dc,dcType,new DataCol("value", FunctionMgr.getLang("value")));
        //根据用户输入参数值更新sql
        paTable.getComp().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                try {
                    if ("tableCellEditor".equalsIgnoreCase(evt.getPropertyName().trim())) {
                        Map<String, String> valMap = new HashMap<>();
                        for(int i=0;i<paTable.getComp().getRowCount();i++){
                            valMap.put(paTable.getComp().getValueAt(i, 0)+"",paTable.getComp().getValueAt(i,2)+"");
                        }
                        if (!valMap.isEmpty()) textArea.setText(getSql(valMap));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    PopPaneUtil.error(StartUtil.parentFrame .getWindow(), FunctionMgr.getLang("error")+":"+e.getMessage());
                }
            }
        });

        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setWithScroll(paTable.getComp());
        paTable.load(new LinkedList<>(), 1);
        hTabPane.addPanel("localVariable", FunctionMgr.getLang("parameter"), lastPanel.getComp(), true);

        //添加消息面板
        messageText.setLineWrap(true);
        LastPanel lasp = new LastPanel(false);
        lasp.set(messageText.getComp());
        hTabPane.addPanel("information", FunctionMgr.getLang("information"), lasp.getComp(), true);
        return hTabPane;
    }
    
    /**
     * 获取函数存储过程等对象参数信息
     * @throws Exception
     */
    protected void getParam() throws Exception{
        switch (DriverUtil.getDbType(jdbcBean)) {
            case hhdb:
                run = new RunHhPg(conn, conn.getSchema(), funMr.treeNode.getName(), HHdbPgsqlPrefixEnum.hh) {
                    @Override
                    public void next(List<HTabRowBean> lisi) {
                        parMap = getParamLists();
                        paTable.load(infoParam(), 1);
                    }
                };
                break;
            case pgsql:
                run = new RunHhPg(conn, conn.getSchema(), funMr.treeNode.getName(), HHdbPgsqlPrefixEnum.pg) {
                    @Override
                    public void next(List<HTabRowBean> lisi) {
                        parMap = getParamLists();
                        paTable.load(infoParam(), 1);
                    }
                };
                break;
            case oracle:
                run = new RunOracle(conn, conn.getSchema(), packName, funMr.treeNode.getName()) {
                    @Override
                    public void next(List<HTabRowBean> lisi) {
                        try {
                            parMap = getParamLists();
                            paTable.load(infoParam(), 1);
                            if (!lisi.isEmpty() && null != lisi.get(0)) {
                                if (lisi.get(0).getOldRow().get("value").startsWith("function")) {
                                    if (type != TreeMrType.FUNCTION) type = TreeMrType.FUNCTION;
                                } else {
                                    if (type != TreeMrType.PROCEDURE) type = TreeMrType.PROCEDURE;
                                }
                            }
                            textArea.setText(getSql(new HashMap<>()));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                break;
            case mysql:
                run = new RunMysql(conn, jdbcBean.getSchema(), funMr.treeNode.getName(), type.name());
                break;
            case sqlserver:
                run = new RunSqlServer(conn, conn.getSchema(), funMr.treeNode.getName(), type.name());
                break;
            case db2:
                run = new RunDb2(conn, "TEST", funMr.treeNode.getName(), type.name()) {
                    @Override
                    public void next(List<HTabRowBean> lisi) {
                        parMap = getParamLists();
                    }
                };
                break;
            case dm:
                run = new RunDm(conn, conn.getSchema(), packName, funMr.treeNode.getName()) {
                    @Override
                    public void next(List<HTabRowBean> lisi) {
                        try {
                            parMap = getParamLists();
    
                            if (!lisi.isEmpty() && null != lisi.get(0)) {
                                if (lisi.get(0).getOldRow().get("value").startsWith("function")) {
                                    if (type != TreeMrType.FUNCTION) type = TreeMrType.FUNCTION;
                                } else {
                                    if (type != TreeMrType.PROCEDURE) type = TreeMrType.PROCEDURE;
                                }
                            }
                            textArea.setText(getSql(new HashMap<>()));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                break;
            default:
                PopPaneUtil.info("暂不支持: " + DriverUtil.getDbType(jdbcBean).name());
                throw new IllegalStateException("Unexpected value: " + DriverUtil.getDbType(jdbcBean).name());
        }
        if (run.isOverWrite()) {
            run.show();
        } else {
            run.handParams();
            parMap = run.getParamLists();
            paTable.load(infoParam(), 1);
        }
    }
}
