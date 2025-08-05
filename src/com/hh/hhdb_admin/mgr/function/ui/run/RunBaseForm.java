package com.hh.hhdb_admin.mgr.function.ui.run;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import com.hh.hhdb_admin.mgr.function.util.DebugUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.util.*;

public abstract class RunBaseForm extends LastPanel {
    private DBTypeEnum dbType;
    protected TreeMrType type;
    protected AbsFunMr funMr;
    protected JdbcBean jdbcBean;
    protected Connection conn;      //运行连接
    protected RunTool runTool;
    
    protected HDialog dialog;
    protected HButton stopBut, runBut;
    protected HTextArea textArea = new HTextArea();
    protected TextAreaInput messageText = new TextAreaInput("messageText");
    protected HTable paTable;
    protected HTabPanel hTabPane = new HTabPanel();
    
    protected List<Map<String, String>> parMap = new LinkedList<>();     //参数集合
    
    /**
     * 初始化运行面板
     */
    public RunBaseForm(AbsFunMr funMr,Connection conn,JdbcBean jdbcBean,List<Map<String, String>> list) {
        try {
            this.funMr = funMr;
            this.jdbcBean = jdbcBean;
            this.conn = conn;
            this.type = funMr.treeNode.getType();
            this.parMap = list;
            dbType = DriverUtil.getDbType(jdbcBean);
            
            if (dbType == DBTypeEnum.oracle) {
                String name = StringUtils.isNotBlank(funMr.treeNode.getPackageName()) ? funMr.treeNode.getPackageName() : funMr.treeNode.getName();
                String type = StringUtils.isNotBlank(funMr.treeNode.getPackageName()) ? "PACKAGE BODY" : this.type.name();
                if (!DebugUtil.funVerify(jdbcBean,name,type)) {
                    PopDlgUtil.info(CsVarUtil.parentFrame,(StringUtils.isNotBlank(funMr.treeNode.getPackageName()) ? funMr.treeNode.getPackageName() : funMr.treeNode.getName()) + "无效");
                    ConnUtil.close(conn);
                    return;
                }
            }
            
            HSplitPanel splitPane = new HSplitPanel(false);
            splitPane.setSplitWeight(0.5);
            JSplitPane jsp = splitPane.getComp();
            jsp.setLeftComponent(textArea.getComp());
            jsp.setRightComponent(getTabPane().getComp());
            textArea.getRSyntaxTextArea().setRows(15);
            textArea.setText(getSql(new HashMap<>()));
            HTAreaConf conf = new HTAreaConf();
            conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
            textArea.setConf(conf);
    
            setHead(getHBarPanel().getComp());
            set(splitPane.getComp());
            
            show();
        }catch (Exception e){
            Lg.error(RunBaseForm.class, e);
            PopDlgUtil.error(CsVarUtil.parentFrame,e);
        }
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
    protected abstract String getSql(Map<String, List<String>> valMap) throws Exception;
    
    /**
     * 运行
     */
    protected abstract void runFun();


    protected void show() {
        dialog = new HDialog(CsVarUtil.parentFrame,1000, 800) {
            @Override
            protected void closeEvent() {
                finish();
                dispose();
            }
        };
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setWindowTitle(LangMgr.lang(Word2.RUN));
        HPanel hPanel = new HPanel();
        hPanel.setLastPanel(this);
        dialog.setRootPanel(hPanel);
        dialog.setStatusBar(new HBarPanel());
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
        runBut = new HButton(LangMgr.lang(Word2.RUN)) {
            @Override
            public void onClick() {
                try {
                    if (paTable.getComp().isEditing()) paTable.getComp().getCellEditor().stopCellEditing();
                    if (DebugUtil.verify(paTable.getRowBeans(null),dialog,dbType)) {
                        stopBut.setEnabled(true);
                        runBut.setEnabled(false);
                        runFun();
                        hTabPane.selectPanel("information");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    messageText.setValue(e.getMessage());
                }
            }
        };
        runBut.setIcon(FaSolidEnum.PLAY,16);
        //停止
        stopBut = new HButton(LangMgr.lang(Word2.STOP)) {
            @Override
            public void onClick() {
                stopBut.setEnabled(false);
                runBut.setEnabled(true);
                finish();
            }
        };
        stopBut.setIcon(FaSolidEnum.STOP,16);
        stopBut.setEnabled(false);
        toolBarPane.add(runBut, stopBut);

        return toolBarPane;
    }

    /**
     * 获取信息下方Tab
     * @return
     */
    private HTabPanel getTabPane() throws Exception{
        //添加参数表格
        paTable = new HTable();
        paTable.setRowHeight(25);
        paTable.hideSeqCol();
        DataCol dc = new DataCol("name", LangMgr.lang(Word2.PARAM));
        dc.setCellEditable(false);
        DataCol dcType = new DataCol("type",LangMgr.lang(Word2.TYPE));
        dcType.setCellEditable(false);
        paTable.addCols(dc,dcType,new DataCol("value", LangMgr.lang(Word1.VALUE)));
        //根据用户输入参数值更新sql
        paTable.getComp().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                try {
                    if ("tableCellEditor".equalsIgnoreCase(evt.getPropertyName().trim())) {
                        Map<String, List<String>> valMap = new LinkedHashMap<>();
                        for(int i=0;i<paTable.getComp().getRowCount();i++){
                            List<String> list = new LinkedList<>();
                            list.add(paTable.getComp().getValueAt(i, 1)+"");
                            list.add(paTable.getComp().getValueAt(i, 2)+"");
                            valMap.put(paTable.getComp().getValueAt(i, 0)+"",list);
                        }
                        if (!valMap.isEmpty()) textArea.setText(getSql(valMap));
                    }
                }catch (Exception e){
                    Lg.error(RunBaseForm.class, e);
                    PopDlgUtil.error(CsVarUtil.parentFrame ,e);
                }
            }
        });

        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setWithScroll(paTable.getComp());
        paTable.load(infoParam(), 1);
        hTabPane.setPanel("localVariable", lastPanel, FunctionMgr.newHeaderConfig(LangMgr.lang(Word2.PARAM)));

        //添加消息面板
        messageText.setLineWrap(true);
        LastPanel lasp = new LastPanel(false);
        lasp.set(messageText.getComp());
        hTabPane.setPanel("information", lasp,FunctionMgr.newHeaderConfig(LangMgr.lang(Word2.INFORMATION)));
        return hTabPane;
    }
}
