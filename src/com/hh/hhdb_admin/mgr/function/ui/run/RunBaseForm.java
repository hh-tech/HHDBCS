package com.hh.hhdb_admin.mgr.function.ui.run;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditorTextArea;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RunBaseForm extends LastPanel {
    protected AbsFunMr funMr;
    protected JdbcBean jdbcBean;
    protected Connection conn;      //运行连接

    protected HButton stopBut, runBut;
    protected QueryEditorTextArea textArea = new QueryEditorTextArea(false);
    protected TextAreaInput messageText = new TextAreaInput("messageText");
    protected HTable paTable = new HTable();
    protected HTabPane hTabPane = new HTabPane();


    /**
     * 初始化运行面板
     * @param jdbcBean
     */
    public RunBaseForm(AbsFunMr funMr,JdbcBean jdbcBean) throws Exception {
        super(false);
        this.funMr = funMr;
        this.jdbcBean = jdbcBean;
        this.conn = ConnUtil.getConn(jdbcBean);

        HSplitPanel splitPane = new HSplitPanel(false);
        splitPane.setSplitWeight(0.5);
        JSplitPane jsp = splitPane.getComp();
        jsp.setLeftComponent(textArea.getComp());
        jsp.setRightComponent(getTabPane().getComp());
        textArea.getTextArea().setRows(15);

        setHead(getHBarPanel().getComp());
        set(splitPane.getComp());
    }

    /**
     * 运行
     */
    protected abstract void runFun();

    /**
     * 结束
     */
    protected abstract void finish();

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
        Connection con = null;
        try {
            con = ConnUtil.getConn(jdbcBean);

            hTabPane.setCloseBtn(false);
            //添加参数表格
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
                            if (!paTable.getComp().isEditing()) {
                                Map<String, String> valMap = new HashMap<>();
                                for(int i=0;i<paTable.getComp().getRowCount();i++){
                                    valMap.put(paTable.getComp().getValueAt(i, 0)+"",paTable.getComp().getValueAt(i,2)+"");
                                }
                                textArea.setText(getSql(valMap));
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        PopPaneUtil.error(StartUtil.parentFrame .getWindow(), FunctionMgr.getLang("error")+":"+e.getMessage());
                    }
                }
            });

            LastPanel lastPanel = new LastPanel(false);
            lastPanel.setWithScroll(paTable.getComp());
            paTable.load(infoParam(), 1);
            hTabPane.addPanel("localVariable", FunctionMgr.getLang("parameter"), lastPanel.getComp(), true);

            //添加消息面板
            messageText.setLineWrap(true);
            LastPanel lasp = new LastPanel(false);
            lasp.set(messageText.getComp());
            hTabPane.addPanel("information", FunctionMgr.getLang("information"), lasp.getComp(), true);
        }finally {
            ConnUtil.close(con);
        }
        return hTabPane;
    }

}
