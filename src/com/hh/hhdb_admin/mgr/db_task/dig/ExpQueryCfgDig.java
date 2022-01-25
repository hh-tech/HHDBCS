package com.hh.hhdb_admin.mgr.db_task.dig;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.dbmg.exp.data.ExpEnum;
import com.hh.frame.dbobj2.DataUtil;
import com.hh.frame.dbquery.QueryTool;
import com.hh.frame.dbtask.DbTask;
import com.hh.frame.dbtask.ExpQueryAsCsvOrXlsTask;
import com.hh.frame.dbtask.ExpQueryAsInsertTask;
import com.hh.frame.sqlwin.WinMgr;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.*;
import com.hh.frame.swingui.view.input.fc.DirChooserInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.frame.swingui.view.util.VerifyUtil;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.query.util.TempEdit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 数据导出
 */
public class ExpQueryCfgDig extends AbsCfgDig {
    public static final String SQL = "sql";

    private final TextInput schemaInput = new TextInput();
    private final TextInput sqlInput = new TextInput();
    private final TextInput perFileLinesInput = new TextInput();
    private final TextAreaInput templateInput = new TextAreaInput();
    private final DirChooserInput dirPath = new DirChooserInput("dirPath");
    private final HPanel templateLine = new HPanel(new HDivLayout(GridSplitEnum.C3, GridSplitEnum.C7));

    private String type;

    public ExpQueryCfgDig(JdbcBean jdbc, Map<String, String> config) {
        super(jdbc);
        perFileLinesInput.setValue("10000");
        perFileLinesInput.setInputVerifier(VerifyUtil.getTextIntVerifier(getLang("data_row"), 1, 1000000));

        templateInput.setRows(2);
        templateInput.setLineWrap(true);
        templateInput.setEnabled(false);

        dirPath.setBtnText(getLang("choose"));
        dialog.setWindowTitle(getLang("query_export"));
        setJdbcPanel();
        rootPanel.add(initPanel());
        rootPanel.add(getToolBar());
        dialog.setRootPanel(rootPanel);
        setSize(rootPanel.getHeight() + 70);
        if (config != null) {
            String schemaName = config.get(StartUtil.PARAM_SCHEMA);
            String sql = config.get(SQL);
            schemaInput.setValue(schemaName);
            sqlInput.setValue(sql);
            if (StringUtils.isNotBlank(schemaName) && StringUtils.isNotBlank(sql)) {
                templateInput.setValue(geneTemplate(schemaName, sql));
            }
            if (config.get("taskName") != null) {
                setTaskName(config.get("taskName"));
            }
        }

    }

    @Override
    public DbTask show() {
        dialog.show();
        return task;
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
    }

    @Override
    protected void setTask() throws Exception {
        String sqlName = sqlInput.getValue();
        String schemaName = schemaInput.getValue();
        String rowStr = perFileLinesInput.getValue();
        String template = templateInput.getValue();
        String url = dirPath.getValue();
        if (StringUtils.isBlank(schemaName)) {
            throw new Exception(getLang("schema_not_null"));
        }
        if (StringUtils.isBlank(sqlName)) {
            throw new Exception(getLang("sql_not_null"));
        }
        if (StringUtils.isBlank(rowStr)) {
            throw new Exception(getLang("row_not_null"));
        }

        if (StringUtils.isBlank(url)) {
            throw new Exception(getLang("url_not_null"));
        }

        if (type.equals("sql") && StringUtils.isBlank(template)) {
            throw new Exception(getLang("template_not_null"));
        }

        Map<String, String> config = new HashMap<>();
        config.put("sql", sqlName);
        config.put("dir", url);
        config.put("perFileLines", rowStr);
        config.put("schema", schemaName);
        if (type.equals("sql")) {
            config.put("insertVm", template);
            task = new ExpQueryAsInsertTask(getTaskName(), jdbc, config);
        } else if (type.equals("csv")) {
            config.put("expEnum", ExpEnum.csv.name());
            task = new ExpQueryAsCsvOrXlsTask(getTaskName(), jdbc, config);
        } else {
            config.put("expEnum", ExpEnum.xlsx.name());
            task = new ExpQueryAsCsvOrXlsTask(getTaskName(), jdbc, config);
        }
    }

    private String geneTemplate(String schemaName, String sql) {
        String res = "";
        String sqlName = sqlInput.getValue();
        if (StringUtils.isBlank(schemaName)) {
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), getLang("schema_not_null"));
            return res;
        }
        if (StringUtils.isBlank(sqlName)) {
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), getLang("sql_not_null"));
            return res;
        }

        String exportFile = WinMgr.workDir + File.separator + new Date().getTime() + File.separator;  //数据临时文件夹
        Connection conn = null;
        try {
            conn = ConnUtil.getConn(jdbc);
            if (!schemaName.equals(jdbc.getSchema()))
                ConnUtil.setCurrentSchema(conn, DbCmdStrUtil.toDbCmdStr(schemaName, DriverUtil.getDbType(jdbc)));
            QueryTool expdata = new QueryTool(conn, sql, new File(exportFile), 1);
            expdata.first();
            Map<String, String> defMap = new LinkedHashMap<>();
            expdata.getColNames().forEach(a -> defMap.put(a, a));
            res = DataUtil.getInsertVm(defMap, jdbc.getSchema(), "new_table");
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(dialog.getWindow(), e.getMessage());
        } finally {
            try {
                if (new File(exportFile).exists()) FileUtils.forceDelete(new File(exportFile));
                ConnUtil.close(conn);
            } catch (Exception e) {
                PopPaneUtil.error(dialog.getWindow(), e);
            }
        }
        return res;
    }

    private HPanel initPanel() {
        HPanel panel = new HPanel();
        panel.setTitle(getLang("query_config"));
        WithLabelInput schemaLine = new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C3)), getLang("schema_name"), schemaInput);
        WithLabelInput sqlLine = new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C3)), "sql*：", sqlInput);
        WithLabelInput dataRowLine = new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C3)), getLang("file_row"), perFileLinesInput);
        WithLabelInput dirPathLine = new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C3)), getLang("dir_path"), dirPath);

        RadioGroupInput rInput = new RadioGroupInput("type", new HPanel(new HDivLayout(GridSplitEnum.C4, GridSplitEnum.C4))) {
            @Override
            protected void stateChange(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED) return;
                type = ((JRadioButton) e.getItem()).getName();
                if (type.equals("sql")) {
                    templateLine.add(new LabelInput(getLang("data_template")), templateInput, new HButton(getLang("template")) {
                        @Override
                        protected void onClick() {
                            String template = geneTemplate(schemaInput.getValue(), sqlInput.getValue());
                            if (StringUtils.isBlank(template)) {
                                return;
                            }

                            new TempEdit(templateInput.getValue(), template, getLang("modifyParam"), dialog) {
                                public void save(String sql) {
                                    templateInput.setValue(sql);
                                }
                            };
                        }
                    });
                } else {
                    templateLine.removeAll();
                }
                templateLine.updateUI();
            }
        };
        rInput.setId("type");
        rInput.add("csv", "CSV");
        rInput.add("sql", "INSERT");
        rInput.setSelected("csv");
        WithLabelInput typeLine = new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C3)), getLang("derived_type"), rInput);

        panel.add(schemaLine, sqlLine, dataRowLine, dirPathLine, typeLine, templateLine);
        return panel;
    }
}
