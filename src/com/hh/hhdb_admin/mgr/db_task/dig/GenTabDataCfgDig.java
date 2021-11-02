package com.hh.hhdb_admin.mgr.db_task.dig;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.dbobj2.ColumTool;
import com.hh.frame.dbobj2.DataUtil;
import com.hh.frame.dbtask.DbTask;
import com.hh.frame.dbtask.GenTabDataTask;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.WithLabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.frame.swingui.view.util.VerifyUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.query.util.TempEdit;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Jiang
 * @date: 2021/2/20
 */

public class GenTabDataCfgDig extends AbsCfgDig {

    private HPanel panel;
    private final TextInput schemaInput = new TextInput();
    private final TextInput tableInput = new TextInput();
    private final TextInput dataRowInput = new TextInput();
    private final TextAreaInput templateInput = new TextAreaInput();

    public GenTabDataCfgDig(JdbcBean jdbc, Map<String, String> config) {
        super(jdbc);
        templateInput.setEnabled(false);
        dataRowInput.setInputVerifier(VerifyUtil.getTextIntVerifier(getLang("data_row"), 1, 2147483647));
        templateInput.setRows(4);
        templateInput.setLineWrap(true);
        dialog.setWindowTitle(getLang("gen_table_data"));
        initPanel();
        setJdbcPanel();
        rootPanel.add(panel);
        rootPanel.add(getToolBar());
        dialog.setRootPanel(rootPanel);
        setSize(rootPanel.getHeight());
        if (config != null) {
            String schemaName = config.get(StartUtil.PARAM_SCHEMA);
            String tableName = config.get(StartUtil.PARAM_TABLE);
            schemaInput.setValue(schemaName);
            tableInput.setValue(tableName);
            if (StringUtils.isNotBlank(schemaName) && StringUtils.isNotBlank(tableName)) {
                templateInput.setValue(geneTemplate(schemaName, tableName));
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
        if (tableInput.isEnabled()) {
            tableInput.setValue("");
        }
        boolean isSetTemp = !tableInput.isEnabled() && !schemaInput.isEnabled();
        templateInput.setValue(isSetTemp ? geneTemplate(schemaInput.getValue(), tableInput.getValue()) : "");
        dataRowInput.setValue("10");
    }

    @Override
    protected void setTask() throws Exception {
        String tableName = tableInput.getValue();
        String schemaName = schemaInput.getValue();
        String dataRowStr = dataRowInput.getValue();
        String template = templateInput.getValue();
        if (StringUtils.isBlank(schemaName)) {
            throw new Exception(getLang("schema_not_null"));
        }
        if (StringUtils.isBlank(tableName)) {
            throw new Exception(getLang("table_not_null"));
        }
        if (StringUtils.isBlank(dataRowStr)) {
            throw new Exception(getLang("row_not_null"));
        }
        if (StringUtils.isBlank(template)) {
            template = geneTemplate(schemaName, tableName);
        }
        int dataRow = Integer.parseInt(dataRowStr);
        Map<String, String> config = new HashMap<>();
        config.put("schemaName", schemaName);
        config.put("tableName", tableName);
        config.put("insertVm", template);
        config.put("dataRow", String.valueOf(dataRow));
        task = new GenTabDataTask(getTaskName(), jdbc, config);
    }

    private String geneTemplate(String schemaName, String tableName) {
        String res = "";
        if (StringUtils.isAnyBlank(schemaName, tableName)) {
            PopPaneUtil.error(dialog.getWindow(), "请先填写模式名和表名!");
            return res;
        }
        Connection conn = null;
        try {
            conn = ConnUtil.getConn(jdbc);
            ColumTool columTool = new ColumTool(conn, schemaName, tableName);
            Map<String, String> defMap = columTool.getDefMap();
            res = StringUtils.replace(DataUtil.getInsertVm(defMap, schemaName, tableName), "${ID}", "${__N}");
        } catch (Exception e) {
            PopPaneUtil.error(dialog.getWindow(), e);
        } finally {
            if (conn != null) {
                ConnUtil.close(conn);
            }
        }
        return res;
    }

    private void initPanel() {
        panel = new HPanel();
        panel.setTitle("数据生成配置");
        WithLabelInput schemaLine = new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C3)), getLang("schema_name"), schemaInput);
        WithLabelInput tableLine = new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C3)), getLang("table_name"), tableInput);
        WithLabelInput dataRowLine = new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C3)), getLang("data_row"), dataRowInput);
        HPanel templateLine = new HPanel(new HDivLayout(GridSplitEnum.C3, GridSplitEnum.C7));
        templateLine.add(new LabelInput(getLang("data_template")), templateInput, new HButton(getLang("edit")) {
            @Override
            protected void onClick() {
                String template = geneTemplate(schemaInput.getValue(), tableInput.getValue());
                if (StringUtils.isBlank(template)) {
                    return;
                }
                
                new TempEdit(templateInput.getValue(),template,"注：可以使用表达式改变__N的值,如从100开始计数set( ${__N} = ${__N}+ 100 )",dialog){
                    public void save(String sql) {
                        templateInput.setValue(sql);
                    }
                };
            }
        });
        panel.add(schemaLine, tableLine, dataRowLine, templateLine);
    }

}
