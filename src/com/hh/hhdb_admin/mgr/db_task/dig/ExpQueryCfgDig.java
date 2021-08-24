package com.hh.hhdb_admin.mgr.db_task.dig;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.dbmg.exp.data.ExpEnum;
import com.hh.frame.dbtask.DbTask;
import com.hh.frame.dbtask.ExpQueryAsCsvOrXlsTask;
import com.hh.frame.dbtask.ExpQueryAsInsertTask;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.*;
import com.hh.frame.swingui.view.input.fc.DirChooserInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.util.VerifyUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.query.util.TempEdit;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.HashMap;
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
        rootPanel.add(super.getToolBar());
        dialog.setRootPanel(rootPanel);
        super.setSize(rootPanel.getHeight() + 70);
        if (config != null) {
            String schemaName = config.get(StartUtil.PARAM_SCHEMA);
            String sql = config.get(SQL);
            schemaInput.setValue(schemaName);
            sqlInput.setValue(sql);
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

        templateInput.setValue("");
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
            config.put("expEnum", ExpEnum.xls.name());
            task = new ExpQueryAsCsvOrXlsTask(getTaskName(), jdbc, config);
        }
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
                            new TempEdit(templateInput.getValue(), jdbc, sqlInput.getValue(), "*请根据选择的表修改值参数", dialog) {
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
