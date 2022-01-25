package com.hh.hhdb_admin.mgr.db_task.dig;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.dbtask.DbTask;
import com.hh.frame.dbtask.sql_file_imp.SqlFileImpTool;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.fc.FileChooserInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.ItemEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SqlFileCfgDig extends AbsCfgDig {

    private static HPanel panel;
    private static TextInput splitInput;
    private FileChooserInput sqlFileChoose;
    private SelectBox encodeSelectBox;

    public static final String ENCODING_TEXT_ID = "encoding_id";
    public static final String SQL_FILE_TEXT_ID = "sql_file_id";

    public SqlFileCfgDig(JdbcBean jdbc, Map<String, String> config) {
        super(jdbc);
        dialog.setWindowTitle(getLang("addSqlFile"));
        initSqlFilePanel();
        setJdbcPanel();
        rootPanel.add(panel);
        rootPanel.add(super.getToolBar());
        dialog.setRootPanel(rootPanel);
        super.setSize(rootPanel.getHeight());
        if (config != null) {
            encodeSelectBox.setValue(config.get(ENCODING_TEXT_ID));
            sqlFileChoose.setValue(config.get(SQL_FILE_TEXT_ID));
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
        encodeSelectBox.setValue("");
        sqlFileChoose.setValue("");
    }

    @Override
    protected boolean anyEmpty() {
        if (super.anyEmpty()) {
            return true;
        }
        String v = encodeSelectBox.getValue();
        if (StringUtils.isBlank(v)) {
            PopPaneUtil.error(dialog.getWindow(), getLang("sqlFileNotEmpty"));
            return true;
        }
        return false;
    }

    private void initSqlFilePanel() {
        panel = new HPanel();
        panel.setTitle(getLang("sqlFileConfig"));
        HGridPanel gridPanel = new HGridPanel(new HGridLayout(GridSplitEnum.C3, GridSplitEnum.C8));
        splitInput = new TextInput();
        HGridPanel gPanel = getWithLabelInput(splitInput);
        SelectBox selectBox = new SelectBox() {
            @Override
            protected void onItemChange(ItemEvent e) {
                splitInput.setValue("");
                gPanel.getComp().setVisible("custom".equals(this.getValue()));
            }
        };
        selectBox.addOption(getLang("regular"), "regular");
        selectBox.addOption(getLang("custom"), "custom");
        gridPanel.setComp(1, selectBox);
        gridPanel.setComp(2, gPanel);
        gridPanel.setComp(3, new LabelInput());
        gPanel.getComp().setVisible(false);
        panel.add(gridPanel);

        panel.add(new HeightComp(10));
        HGridPanel sqlFilePanel = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
        encodeSelectBox = new SelectBox();
        encodeSelectBox.setId(ENCODING_TEXT_ID);
        encodeSelectBox.addOption("UTF-8", "UTF-8");
        encodeSelectBox.addOption("GBK", "GBK");

        sqlFileChoose = new FileChooserInput();
        sqlFileChoose.setBtnText(getLang("choose"));

        sqlFilePanel.setComp(1, encodeSelectBox);
        sqlFilePanel.setComp(2, sqlFileChoose);
        panel.add(sqlFilePanel);
    }

    HGridPanel getWithLabelInput(AbsInput input) {
        HGridLayout gridLayout = new HGridLayout(GridSplitEnum.C3);
        HGridPanel gridPanel = new HGridPanel(gridLayout);
        LabelInput labelInput = new LabelInput(getLang("split_str"));
        gridPanel.setComp(1, labelInput);
        gridPanel.setComp(2, input);
        return gridPanel;
    }

    @Override
    protected void setTask() throws Exception {
        String taskName = super.getTaskName();
        JdbcBean jdbc = super.getJdbc();
        File sqlFile = new File(sqlFileChoose.getValue());
        String splitStr = splitInput.getValue();
        String encoding = encodeSelectBox.getValue();
        boolean flag = StringUtils.isNotEmpty(splitStr);
        Map<String, String> config = new HashMap<>();
        config.put("sqlFile", sqlFile.getAbsolutePath());
        config.put("split", String.valueOf(flag));
        config.put("encoding", encoding);
        task = new SqlFileImpTool(taskName, jdbc, config);
        if (!flag) {
            return;
        }
        ((SqlFileImpTool) task).setSplitStr(splitStr);
    }

}
