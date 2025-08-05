package com.hh.hhdb_admin.mgr.table2.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.create_dbobj.table2.CreateNewTabTool;
import com.hh.frame.create_dbobj.table2.db.ClickHouseCreateNewTab;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.LangType;
import com.hh.frame.lang2.conf.*;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.frame.swingui.view.with_label.LabelConf;
import com.hh.frame.swingui.view.with_label.WithLabelInput;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.mgr.table2.TableUtil;
import com.hh.hhdb_admin.mgr.table2.panel.*;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


public class TableComp {
    private TextInput nameInput;
    private TextAreaInput annotateInput;
    private SelectBox engineBox;
    private HTextArea sqlArea;
    private HDialog dialog;
    
    private ColPanel colPanel;
    private UKPanel ukPanel;
    private FKPanel fkPanel;
    private AbsCodePanel parPanel;
    
    private GridSplitEnum splitEnum = CsVarUtil.CONF.getLang() == LangType.zh ? GridSplitEnum.C1 : GridSplitEnum.C2;
    
    public static Connection conn;
    public static JdbcBean jdbcBean;
    public static DBTypeEnum dbType;
    public static DBTypeEnum.Version ver;
    public static String schemaName;
   
    
    
    public TableComp(Connection conn, JdbcBean jdbc,String schemaName) {
        TableComp.conn = conn;
        TableComp.jdbcBean = jdbc;
		TableComp.dbType = DriverUtil.getDbType(jdbc);
		TableComp.ver = jdbc.getCurrentVersion();
        TableComp.schemaName = schemaName;
    
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setHead(initNamePanel().getComp());
        lastPanel.set(initSplitPanel().getComp());
    
        dialog = new HDialog(CsVarUtil.parentFrame, HFrame.LARGE_WIDTH, HFrame.LARGE_WIDTH / 4 * 3);
        dialog.setRootPanel(lastPanel);
    }
    
    public void showDialog() {
        dialog.setWindowTitle(LangMgr.lang(Word3.CREATE_TABLE));
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.getWindow().setLocationRelativeTo(dialog.getWindow().getParent());
        dialog.setStatusBar(new HBarPanel());
        dialog.show();
    }
    
    private HPanel initNamePanel() {
        HDivLayout divLayout = new HDivLayout(5, 10, GridSplitEnum.C12);
        divLayout.setTopHeight(5);
        HPanel panel =new HPanel(divLayout);
        
        HBarPanel saveInput = new HBarPanel();
        saveInput.add(new HButton(LangMgr.lang(Word2.CODE),FaSolidEnum.MAGNIFYING_GLASS) {
            @Override
            protected void onClick() {viewSql();}
        },new HButton(LangMgr.lang(Word2.SAVE),FaSolidEnum.FLOPPY_DISK) {
            @Override
            protected void onClick() {
                try {
                    if (StringUtils.isBlank(sqlArea.getText())) viewSql();
                    if (GlobalWorkUtil.exeSql(jdbcBean, conn, sqlArea.getText())) {
                        PopDlgUtil.info(dialog.getWindow(), LangMgr.sLang(FrameL.TIP_SAVE_SUCCESS));
                        refresh();
                        dialog.dispose();
                    }
                } catch (Exception e) {
                    GlobalWorkUtil.err(getClass(), e,dialog.getWindow());
                }
            }
        });
        nameInput = new TextInput("table_name", "new_table");
        saveInput.setLastHComp(nameInput);
        panel.add(new WithLabelInput(LangMgr.lang(Word2.TABLE_NAME)+"：", saveInput,splitEnum));
    
        if (dbType == DBTypeEnum.clickhouse) {
            engineBox = new SelectBox("engine");
            List<String> engineList = new ArrayList<>();
            for (ClickHouseCreateNewTab.CkhEngine value : ClickHouseCreateNewTab.CkhEngine.values()) {
                engineList.add(value.name());
            }
            engineList.forEach(a -> engineBox.addOption(a,a));
            panel.add(new WithLabelInput(new LabelConf(splitEnum, AlignEnum.LEFT,AlignEnum.LEFT),new LabelInput(LangMgr.lang(Word2.ENGINE)+"："), engineBox));
        }
        
        if (dbType != DBTypeEnum.informix && dbType != DBTypeEnum.sqlite && dbType != DBTypeEnum.derby) {
            annotateInput = new TextAreaInput("annotateArea", "", 2);
            panel.add(new WithLabelInput(new LabelConf(splitEnum, AlignEnum.LEFT,AlignEnum.LEFT),new LabelInput(LangMgr.lang(Word2.COMMENT)+"："), annotateInput));
        }
        return panel;
    }
    
    private HSplitPanel initSplitPanel() {
		colPanel = new ColPanel();
        
        HTabPanel tabPane = new HTabPanel();
        if (TableUtil.showPartition(dbType)) {
            switch (dbType) {
                case db2:
                    parPanel = new Db2PartitionPanel(colPanel.getTable());
                    break;
                case gaussdb:
                    parPanel = new GaussPartitionPanel(colPanel.getTable());
                    break;
                case greenplum:
                    parPanel = new GreenPlumPartitionPanel(colPanel.getTable());
                    break;
                default:
                    parPanel = new PgPartitionPanel(colPanel.getTable());
                    break;
            }
            tabPane.setPanel("parPanel", parPanel.gethPanel(), TableUtil.newHeaderConfig(LangMgr.lang(Word4.PARTITION_TYPE)).setFixTab(true));
        }
        if (dbType != DBTypeEnum.clickhouse && dbType != DBTypeEnum.hive && dbType != DBTypeEnum.gbase8a) {
            ukPanel = new UKPanel(colPanel.getTable());
            fkPanel = new FKPanel(colPanel.getTable(),new CreateNewTabTool(dbType,ver,"",new ArrayList<>()).getNewTab());
            tabPane.setPanel("fkPanel", fkPanel.gethPanel(), TableUtil.newHeaderConfig(LangMgr.lang(Word2.FOREIGN_KEY)));
            if (dbType != DBTypeEnum.databricks) tabPane.setPanel("ukPanel", ukPanel.gethPanel(), TableUtil.newHeaderConfig(LangMgr.lang(Word3.UNIQUE_KEY)));
        }
        tabPane.setPanel("colPanel", colPanel.gethPanel(), TableUtil.newHeaderConfig(LangMgr.lang(Word1.COLUMN)));
        tabPane.selectPanel("colPanel");
    
        HTAreaConf conf = new HTAreaConf();
        conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
        sqlArea = new HTextArea(conf);
        LastPanel sqlPanel = new LastPanel();
        sqlPanel.set(sqlArea.getComp());
        
        HSplitPanel hSplitPanel = new HSplitPanel(false);
        hSplitPanel.setPanelOne(tabPane);
        hSplitPanel.setLastComp4Two(sqlPanel);
        hSplitPanel.setDividerLocation(400);
        return hSplitPanel;
    }
    
    /**
     * 预览sql
     */
    private void viewSql() {
        if (StringUtils.isBlank(nameInput.getValue().trim())) {
            PopDlgUtil.info(dialog, LangMgr.sLang(CsL.INPUT_NEW_TABLE_NAME));
            return ;
        }
        sqlArea.setText(build(nameInput.getValue()));
    }
    
    private String build(String tableName) {
        tableName = SqlStrUtil.rmQuote4Sel(tableName, dbType, ver);
        String tabFullName = SqlStrUtil.getQuotedFullName(dbType,ver,schemaName,tableName);
        CreateNewTabTool tabTool = new CreateNewTabTool(dbType,ver,tabFullName,new ArrayList<>());
        colPanel.build(tabTool);
        if (null != ukPanel) ukPanel.build(tabTool);
        if (null != fkPanel) fkPanel.build(tabTool);
        if (null != parPanel) parPanel.build(tabTool);
        if (null != annotateInput && StringUtils.isNotBlank(annotateInput.getValue())) tabTool.setComment(annotateInput.getValue());
        if (null != engineBox) {
            ((ClickHouseCreateNewTab) tabTool.getNewTab()).setEngine(ClickHouseCreateNewTab.CkhEngine.valueOf(engineBox.getValue()));
        }
        return tabTool.toString();
    }
    
    protected void refresh() {
    }
}
