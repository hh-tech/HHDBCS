package com.hh.hhdb_admin.mgr.db_task2.dig;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.dbobj3.util.DataUtil;
import com.hh.frame.dbquery.QueryTool;
import com.hh.frame.dbtask2.bean.BaseTaskBean;
import com.hh.frame.dbtask2.query_exp.AbsQueryTask;
import com.hh.frame.dbtask2.query_exp.ExpFileType;
import com.hh.frame.dbtask2.query_exp.QueryExpBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.sqlwin.WinMgr;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.RadioGroupInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.fc.DirChooserInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.util.VerifyUtil;
import com.hh.frame.swingui.view.with_label.LabelConf;
import com.hh.frame.swingui.view.with_label.WithLabelInput;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.sql.Connection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 查询数据导出
 */
public class ExpQueryCfgDig extends AbsCfgDig {
    public static final String SQL = "sql";

    private final TextInput schemaInput = new TextInput();
    private final TextAreaInput sqlInput = new TextAreaInput();
    private final TextInput perFileLinesInput = new TextInput();
    private final TextAreaInput templateInput = new TextAreaInput();
    private final DirChooserInput dirPath = new DirChooserInput("dirPath");
    private final TextInput nullFlagInput = new TextInput();
    private final HPanel templateLine = new HPanel(new HDivLayout(GridSplitEnum.C3, GridSplitEnum.C7));

    private String type;

    public ExpQueryCfgDig(BaseTaskBean bean) {
        super(bean);
        sqlInput.setRows(2);
        sqlInput.setLineWrap(true);
        perFileLinesInput.setValue("10000");
        perFileLinesInput.setInputVerifier(VerifyUtil.getTextIntVerifier(LangMgr.sLang(CsL.DATA_ROW)+"*：", 1, 1000000));
        templateInput.setRows(2);
        templateInput.setLineWrap(true);
        templateInput.setEnabled(false);
        nullFlagInput.setValue(CsVarUtil.CONF.getNullValue());

        dirPath.setBtnText(LangMgr.lang(Word2.CHOOSE));
        dialog.setWindowTitle(LangMgr.lang(Word4.EXPORT_CONFIGURATION));
        setJdbcPanel();
        rootPanel.add(initPanel());
        rootPanel.add(getToolBar());
        dialog.setRootPanel(rootPanel);
        setSize(rootPanel.getHeight() + 70);
        setTaskName(bean.getName());
        if (bean instanceof QueryExpBean) {
            String schemaName = bean.getJdbc().getSchema();
            String sql = ((QueryExpBean) bean).getQuerySql();
            schemaInput.setValue(schemaName);
            sqlInput.setValue(sql);
            if (StringUtils.isNotBlank(schemaName) && StringUtils.isNotBlank(sql)) {
                templateInput.setValue(geneTemplate(schemaName, sql));
            }
        }
    }

    @Override
    protected void resetForm() {
        super.resetForm();
        if (schemaInput.isEnabled()) {
            schemaInput.setValue("");
        }
        if (sqlInput.isEnabled()) {
            sqlInput.setValue("");
        }
        boolean isSetTemp = !sqlInput.isEnabled() && !schemaInput.isEnabled();
        templateInput.setValue(isSetTemp ? geneTemplate(schemaInput.getValue(), sqlInput.getValue()) : "");
        perFileLinesInput.setValue("");
        dirPath.setValue("");
        nullFlagInput.setValue("");
    }

    @Override
    protected void setTask() throws Exception {
        String sqlName = sqlInput.getValue();
        String schemaName = schemaInput.getValue();
        String rowStr = perFileLinesInput.getValue();
        String template = templateInput.getValue();
        String url = dirPath.getValue();
        if (StringUtils.isBlank(schemaName)) {
            throw new Exception(LangMgr.sLang(CsL.SCHEMA_NOT_NULL));
        }
        if (StringUtils.isBlank(sqlName)) {
            throw new Exception(LangMgr.sLang(CsL.SQL_NOT_NULL));
        }
        if (StringUtils.isBlank(rowStr)) {
            throw new Exception(LangMgr.sLang(CsL.ROW_NOT_NULL));
        }

        if (StringUtils.isBlank(url)) {
            throw new Exception(LangMgr.sLang(CsL.URL_NOT_NULL));
        }

        if (type.equals("sql") && StringUtils.isBlank(template)) {
            throw new Exception(LangMgr.sLang(CsL.TEMPLATE_NOT_NULL));
        }

        QueryExpBean bean = new QueryExpBean(0,getTaskName(),getJdbc());
        bean.getJdbc().setSchema(schemaName);
        bean.setQuerySql(sqlName);
        bean.setLinePerFile(Integer.parseInt(rowStr));
        bean.setOutputDir(new File(url));
        bean.setExpFileType(ExpFileType.valueOf(type.toUpperCase()));
        if (type.equals("sql")) {
            bean.setSqlTemplate(template);
        } else {
            bean.setNullFlag(nullFlagInput.getValue());
        }
        task = new AbsQueryTask(bean) {
            @Override
            protected void taskDone() {
                System.out.println("任务结束");
            }
        };
    }

    private String geneTemplate(String schemaName, String sql) {
        jdbc = getJdbc();
        String res = "";
        String sqlName = sqlInput.getValue();
        if (StringUtils.isBlank(schemaName)) {
            PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.sLang(CsL.SCHEMA_NOT_NULL));
            return res;
        }
        if (StringUtils.isBlank(sqlName)) {
            PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.sLang(CsL.SQL_NOT_NULL));
            return res;
        }

        String exportFile = WinMgr.workDir + File.separator + new Date().getTime() + File.separator;  //数据临时文件夹
        Connection conn = null;
        try {
        	conn=CsVarUtil.newConn(jdbc);
        	DBTypeEnum dbtype=DriverUtil.getDbType(jdbc);
            if (!schemaName.equals(jdbc.getSchema()))
                ConnUtil.setCurrentSchema(dbtype,jdbc.getCurrentVersion(),conn, DbCmdStrUtil.toDbCmdStr(schemaName, dbtype));
            QueryTool expdata = new QueryTool(dbtype,jdbc.getCurrentVersion(),conn, sql, new File(exportFile), 1);
            expdata.first();
            Map<String, String> defMap = new LinkedHashMap<>();
            expdata.getColNames().forEach(a -> defMap.put(a, a));
            if (dbtype == DBTypeEnum.tdsql4pg) {
                res = DataUtil.getInsertVm(defMap, DbCmdStrUtil.toDbCmdStr(schemaName, DriverUtil.getDbType(jdbc)), "new_table",true);
            } else if (DBTypeEnum.isInformixSeries(DriverUtil.getDbType(jdbc),jdbc.getCurrentVersion())) {
                res = DataUtil.getInsertVm(defMap, "", "new_table",false);
            } else {
                res = DataUtil.getInsertVm(defMap, DbCmdStrUtil.toDbCmdStr(schemaName, DriverUtil.getDbType(jdbc)), "new_table",false);
            }
        } catch (Exception e) {
            Lg.error(ExpQueryCfgDig.class, e);
            PopDlgUtil.error(dialog, e);
        } finally {
            try {
                if (new File(exportFile).exists()) FileUtils.forceDelete(new File(exportFile));
                ConnUtil.close(conn);
            } catch (Exception e) {
                Lg.error(ExpQueryCfgDig.class, e);
                PopDlgUtil.error(dialog, e);
            }
        }
        return res;
    }

    private HPanel initPanel() {
        LabelConf labelConf = new LabelConf(GridSplitEnum.C3, AlignEnum.RIGHT,AlignEnum.LEFT);
        HPanel panel = new HPanel();
        panel.setTitle(LangMgr.sLang(CsL.QUERY_DATA_EXPORT));
        WithLabelInput schemaLine = new WithLabelInput(labelConf,new LabelInput(LangMgr.lang(Word2.MODE)+"*："), schemaInput);
        WithLabelInput sqlLine = new WithLabelInput(labelConf,new LabelInput("sql*："), sqlInput);
        WithLabelInput dataRowLine = new WithLabelInput(labelConf,new LabelInput(LangMgr.sLang(CsL.FILE_ROW)+"*："), perFileLinesInput);
        WithLabelInput dirPathLine = new WithLabelInput(labelConf,new LabelInput(LangMgr.sLang(CsL.DIR_PATH)+"*："), dirPath);

        RadioGroupInput rInput = new RadioGroupInput("type", new HPanel(new HDivLayout(GridSplitEnum.C4, GridSplitEnum.C4))) {
            @Override
            protected void stateChange(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED) return;
                type = ((JRadioButton) e.getItem()).getName();
                if (type.equals("sql")) {
                    templateLine.removeAll();
                    templateLine.add(new LabelInput(LangMgr.lang(Word4.DATA_TEMPLATE)+"*："), templateInput, new HButton(LangMgr.lang(Word2.TEMPLATE)) {
                        @Override
                        protected void onClick() {
                            String template = geneTemplate(schemaInput.getValue(), sqlInput.getValue());
                            if (StringUtils.isBlank(template)) {
                                return;
                            }

                            new TempDlog(templateInput.getValue(), template, LangMgr.sLang(CsL.MODIFY_PARAM), dialog) {
                                public void save(String sql) {
                                    templateInput.setValue(sql);
                                }
                            };
                        }
                    });
                } else {
                    templateLine.removeAll();
                    templateLine.add(new LabelInput(LangMgr.lang(Word4.NULL_DISPLAYED)+"："), nullFlagInput);
                }
                templateLine.updateUI();
            }
        };
        rInput.setId("type");
        rInput.add("csv", "CSV");
        rInput.add("xls", "EXCEL");
        rInput.add("sql", "INSERT");
        rInput.setSelected("csv");
        WithLabelInput typeLine = new WithLabelInput(labelConf,new LabelInput(LangMgr.lang(Word2.TYPE)+"："), rInput);

        panel.add(schemaLine, sqlLine, dataRowLine, dirPathLine, typeLine, templateLine);
        return panel;
    }
}
