package com.hh.hhdb_admin.mgr.function.ui.from;


import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.ListCol;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditorTextArea;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class FunBaseForm extends AbsHComp {
    public AbsFunMr funMr;
    protected DBTypeEnum dbType;
    protected Connection conn;
    protected JdbcBean jdbcBean;
    protected boolean isEdit;
    
    
    protected HTable hTable;
    public TextAreaInput messageText;
    public QueryEditorTextArea queryUi;

    public FunBaseForm(AbsFunMr funMr,Connection conn,JdbcBean jdbcBean) throws Exception {
        this.funMr = funMr;
        this.conn = conn;
        this.jdbcBean = jdbcBean;
        this.dbType = DriverUtil.getDbType(conn);
    }

    /**
     * 获取输入参数面板
     * @return
     * @throws Exception
     */
    public abstract LastPanel getParaPanel() throws Exception;

    /**
     * 获取sql
     */
    public abstract String getSql() throws Exception;

    /**
     * 保存
     * @return
     * @throws Exception
     */
    public abstract void save() throws Exception;

    /**
     * 删除
     * @return
     * @throws Exception
     */
    public abstract void delete() throws Exception;
    
    /**
     * 检查函数
     */
    public abstract void examineFun();
    
    /**
     * 获取编辑sql面板
     * @return
     * @throws Exception
     */
    public LastPanel getSqlPanel() throws Exception {
        LastPanel lastPanel = new LastPanel(false);
        queryUi = QueryEditUtil.getQueryEditor(true);
        queryUi.setText(getSql());
    
        if (isEdit) {
            messageText = new TextAreaInput("messageText");
            messageText.setLineWrap(true);
            messageText.setEnabled(false);
            LastPanel lasp = new LastPanel(false);
            lasp.set(messageText.getComp());
    
            HSplitPanel splitPane = new HSplitPanel(false);
            splitPane.setSplitWeight(0.8);
            JSplitPane jsp = splitPane.getComp();
            jsp.setLeftComponent(queryUi.getComp());
            jsp.setRightComponent(lasp.getComp());
            lastPanel.set(splitPane.getComp());
        } else {
            lastPanel.set(queryUi.getComp());
        }
        return lastPanel;
    }
    
    /**
     * 获取参数输入表格
     * @return
     * @throws Exception
     */
    protected HPanel getTablePanel()throws Exception{
        hTable = new HTable();
        hTable.hideSeqCol();
        hTable.addCols(new DataCol("name",FunctionMgr.getLang("name")));
        if (dbType.equals(DBTypeEnum.mysql)){
            if(funMr.treeNode.getType() == TreeMrType.PROCEDURE) hTable.addCols(new ListCol("schema", FunctionMgr.getLang("schema"), Arrays.asList("IN", "OUT", "INOUT")));
        }else if (dbType.equals(DBTypeEnum.sqlserver)){
            if(funMr.treeNode.getType() == TreeMrType.PROCEDURE) hTable.addCols(new ListCol("schema", FunctionMgr.getLang("schema"), Arrays.asList("IN","output")));
        }else if (dbType.equals(DBTypeEnum.db2) || dbType.equals(DBTypeEnum.dm)){
            if(funMr.treeNode.getType() == TreeMrType.PROCEDURE) hTable.addCols(new ListCol("schema", FunctionMgr.getLang("schema"), Arrays.asList("IN","OUT")));
        }else {
            hTable.addCols(new ListCol("schema",FunctionMgr.getLang("schema"), Arrays.asList("IN", "OUT", "INOUT")));
        }
        hTable.addCols(new ListCol("type",FunctionMgr.getLang("type"), funMr.getDataType()));
        hTable.setRowHeight(25);
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setWithScroll(hTable.getComp());

        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel toolBarPane = new HBarPanel(l);
        //添加参数
        HButton parameterBut = new HButton(FunctionMgr.getLang("addparameter")) {
            @Override
            public void onClick() {
                Map<String, String> line = new HashMap<>();
                line.put("name","");
                if (dbType.equals(DBTypeEnum.mysql) || dbType.equals(DBTypeEnum.db2) || dbType.equals(DBTypeEnum.dm)){
                    if(funMr.treeNode.getType() == TreeMrType.PROCEDURE) line.put("schema","");
                }else {
                    line.put("schema","IN");
                }
                line.put("type","");
                hTable.add(line);
            }
        };
        parameterBut.setIcon(FunctionMgr.getIcon("addparkey"));
        toolBarPane.add(parameterBut);
        //删除参数
        HButton deleteBut = new HButton(FunctionMgr.getLang("deleteparameter")) {
            @Override
            public void onClick() {
                hTable.deleteSelectRow();
            }
        };
        deleteBut.setIcon(FunctionMgr.getIcon("delparkey"));
        toolBarPane.add(deleteBut);

        HPanel hPanel = new HPanel();
        hPanel.setTitle(FunctionMgr.getLang("parameter"));
        hPanel.getComp().setPreferredSize(new Dimension(hPanel.getComp().getWidth(), 270));
        hPanel.setLastPanel(lastPanel);
        hPanel.add(toolBarPane);
        return hPanel;
    }

    protected HPanel getPanel(String txt, AbsHComp abs, HDivLayout layout){
        HPanel language = new HPanel(layout);
        LabelInput label = new LabelInput(txt);
        label.setAlign(AlignEnum.RIGHT);
        language.add(label);
        language.add(abs);
        return language;
    }
}
