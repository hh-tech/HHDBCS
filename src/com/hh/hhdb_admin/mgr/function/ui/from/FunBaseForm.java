package com.hh.hhdb_admin.mgr.function.ui.from;


import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.table.CreateTableTool;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.ListCol;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;

import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class FunBaseForm extends AbsHComp {
    public AbsFunMr funMr;
    public static CreateTableTool createTabTool;
    protected DBTypeEnum dbType;
    protected Connection conn;
    protected JdbcBean jdbcBean;
    protected boolean isEdit;
    protected String user;
    
    protected HTable hTable;
    public HTextArea queryUi;

    public FunBaseForm(AbsFunMr funMr,JdbcBean jdbcBean,Connection conn,String user) throws Exception {
        this.funMr = funMr;
        this.conn = conn;
        this.jdbcBean = jdbcBean;
        this.user=user;
        this.dbType = DriverUtil.getDbType(jdbcBean);
        createTabTool = new CreateTableTool(dbType,jdbcBean.getCurrentVersion());
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
        queryUi = new HTextArea();
        HTAreaConf conf = new HTAreaConf();
        conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
        queryUi.setConf(conf);
        queryUi.setText(getSql());
        lastPanel.set(queryUi.getComp());
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
        hTable.addCols(new DataCol("name", LangMgr.lang(Word2.NAME)));
        if (dbType == DBTypeEnum.mysql||dbType==DBTypeEnum.mariadb||dbType==DBTypeEnum.tdsql4mysql || dbType==DBTypeEnum.oceanbase){
            if(funMr.treeNode.getType() == TreeMrType.PROCEDURE) hTable.addCols(new ListCol("schema", LangMgr.lang(Word2.SCHEMA), Arrays.asList("IN", "OUT", "INOUT")));
        }else if (dbType == DBTypeEnum.sqlserver){
            if(funMr.treeNode.getType() == TreeMrType.PROCEDURE) hTable.addCols(new ListCol("schema", LangMgr.lang(Word2.SCHEMA), Arrays.asList("IN","output")));
        }else if (dbType == DBTypeEnum.db2){
            if(funMr.treeNode.getType() == TreeMrType.PROCEDURE) hTable.addCols(new ListCol("schema", LangMgr.lang(Word2.SCHEMA), Arrays.asList("IN","OUT")));
        }else if (dbType == DBTypeEnum.gbase){
            hTable.addCols(new ListCol("schema", LangMgr.lang(Word2.SCHEMA), Arrays.asList("INOUT","OUT")));
        }else if (DBTypeEnum.isOraSeries(dbType, jdbcBean.getCurrentVersion())){
            hTable.addCols(new ListCol("schema",LangMgr.lang(Word2.SCHEMA), Arrays.asList("IN", "OUT", "IN OUT")));
        }else {
            hTable.addCols(new ListCol("schema",LangMgr.lang(Word2.SCHEMA), Arrays.asList("IN", "OUT", "INOUT")));
        }
        if (dbType == DBTypeEnum.mysql || dbType==DBTypeEnum.mariadb||dbType == DBTypeEnum.sqlserver||dbType==DBTypeEnum.tdsql4mysql || dbType==DBTypeEnum.oceanbase){
            TypeColumn typeCol = new TypeColumn("type", LangMgr.lang(Word2.TYPE));
            typeCol.setWidth(140);
            hTable.addCols(typeCol);
        } else {
            hTable.addCols(new ListCol("type",LangMgr.lang(Word2.TYPE), funMr.getDataType()));
        }
        hTable.setRowHeight(30);
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setWithScroll(hTable.getComp());
        hTable.load(new ArrayList<>(),1);

        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel toolBarPane = new HBarPanel(l);
        //添加参数
        HButton parameterBut = new HButton(LangMgr.lang(Word2.ADD)) {
            @Override
            public void onClick() {
                Map<String, String> line = new HashMap<>();
                line.put("name","");
                if (dbType == DBTypeEnum.mysql ||dbType==DBTypeEnum.mariadb|| dbType == DBTypeEnum.db2||dbType==DBTypeEnum.tdsql4mysql || dbType==DBTypeEnum.oceanbase){
                    if(funMr.treeNode.getType() == TreeMrType.PROCEDURE) line.put("schema","");
                }else if (dbType==DBTypeEnum.gbase){
                    line.put("schema","INOUT");
                } else {
                    line.put("schema","IN");
                }
    
                if (dbType == DBTypeEnum.mysql||dbType==DBTypeEnum.mariadb || dbType == DBTypeEnum.sqlserver||dbType==DBTypeEnum.tdsql4mysql || dbType==DBTypeEnum.oceanbase){
                    line.put("type", TypeColumn.getDefType().toString());
                } else {
                    line.put("type","");
                }
                
                hTable.add(line);
            }
        };
        parameterBut.setIcon(FaSolidEnum.PLUS,16);
        toolBarPane.add(parameterBut);
        //删除参数
        HButton deleteBut = new HButton(LangMgr.lang(Word2.DELETE)) {
            @Override
            public void onClick() {
                hTable.deleteSelectRow();
            }
        };
        deleteBut.setIcon(FaSolidEnum.MINUS,16);
        toolBarPane.add(deleteBut);

        HPanel hPanel = new HPanel();
        hPanel.setTitle(LangMgr.lang(Word2.PARAM));
        hPanel.getComp().setPreferredSize(new Dimension(hPanel.getComp().getWidth(), 270));
        hPanel.setLastPanel(lastPanel);
        hPanel.add(toolBarPane);
        return hPanel;
    }

    protected HPanel getPanel(String txt, AbsHComp abs){
        HPanel language = new HPanel(new HDivLayout(10, 15, GridSplitEnum.C2,GridSplitEnum.C5));
        LabelInput label = new LabelInput(txt);
        label.setAlign(AlignEnum.RIGHT);
        language.add(label);
        language.add(abs);
        return language;
    }
}
