package com.hh.hhdb_admin.mgr.db_task.dig;


import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.dbtask.DbTask;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.ctrl.HImage;
import com.hh.frame.swingui.view.input.*;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.db_task.TaskComp;
import com.hh.hhdb_admin.mgr.db_task.TaskMgr;
import com.hh.hhdb_admin.mgr.login.LoginUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.ItemEvent;
import java.util.LinkedHashMap;
import java.util.Map;

abstract public class AbsCfgDig {
    protected static final Map<String, String> jdbcMap = new LinkedHashMap<>();
    protected HPanel rootPanel = null;
    protected JdbcBean jdbc;
    protected DbTask task = null;
    protected boolean jdbcEditable;
    protected HDialog dialog;

    private static final String TASK_NAME = "taskName";
    private static final String JDBC_DBTYPE = "db_Type";
    private static final String JDBC_URL = "db_Url";
    private static final String JDBC_USER = "db_User";
    private static final String JDBC_PASS = "db_Pass";
    private static final String JDBC_SCHEMA = "db_schema";
    private static HImage jdbcIcon;
    private final TextInput taskNameInput = new TextInput();
    private HPanel jdbcPanel = null;
    private HButton okBtn;
    private HButton resetBtn;
    private CheckBoxInput isAutoStartCb;
    /**
     * 是否重新运行
     */
    private boolean isReStart = false;


    static {
        jdbcMap.put(TASK_NAME, "任务名称*");
        jdbcMap.put(JDBC_DBTYPE, "数据库类型*:");
        jdbcMap.put(JDBC_URL, "连接*:");
        jdbcMap.put(JDBC_USER, "用户*:");
        jdbcMap.put(JDBC_PASS, "密码*:");
        jdbcMap.put(JDBC_SCHEMA, "模式*:");
    }

    public AbsCfgDig(JdbcBean jdbc) {
        this.jdbc = jdbc;
        dialog = new HDialog(StartUtil.parentFrame, 800) {
            @Override
            protected void closeEvent() {
                task = null;
                super.closeEvent();
            }
        };
        dialog.setIconImage(IconFileUtil.getLogo());
    }

    public AbsCfgDig setReStart(boolean reStart) {
        isReStart = reStart;
        return this;
    }

    public abstract DbTask show();

    protected abstract void setTask() throws Exception;

    protected void setTaskName(String taskName) {
        this.taskNameInput.setValue(taskName);
    }

    protected void setJdbcPanel() {
        rootPanel = new HPanel();
        HGridPanel gridPanel = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
        HPanel lPanel = new HPanel();
        jdbcPanel = new HPanel();
        DBTypeEnum dbtype = DriverUtil.getDbType(jdbc);
        if (dbtype == null) {
            return;
        }
        jdbcIcon = new HImage(IconFileUtil.getDbIcon(dbtype, IconSizeEnum.SIZE_128));
        jdbcIcon.setHAlign(AlignEnum.CENTER);
        lPanel.add(jdbcIcon);
        gridPanel.setComp(1, lPanel);
        gridPanel.setComp(2, jdbcPanel);
        gridPanel.setTitle("数据库配置");
        SelectBox typeBox = new SelectBox(JDBC_DBTYPE) {
            @Override
            protected void onItemChange(ItemEvent e) {
                DBTypeEnum chooseType = DBTypeEnum.valueOf(getValue());
                jdbcIcon.setImg(IconFileUtil.getDbIcon(chooseType, IconSizeEnum.SIZE_128));
                WithLabelInput urlInput = (WithLabelInput) jdbcPanel.getHComp(JDBC_URL);
                if (urlInput != null) {
                    urlInput.setValue(DriverUtil.getDriverUrl(chooseType));
                }
            }
        };
        typeBox.addOption("恒辉数据库", DBTypeEnum.hhdb.name());
        typeBox.addOption("PostgreSQL", DBTypeEnum.pgsql.name());
        typeBox.addOption("Oracle", DBTypeEnum.oracle.name());
        typeBox.addOption("Mysql", DBTypeEnum.mysql.name());
        typeBox.addOption("DB2", DBTypeEnum.db2.name());
        typeBox.addOption("SqlServer", DBTypeEnum.sqlserver.name());
        typeBox.addOption("DM", DBTypeEnum.dm.name());
        DBTypeEnum dbTypeEnum = DriverUtil.getDbType(jdbc);
        if (dbTypeEnum != null) {
            typeBox.setValue(dbTypeEnum.name());
        }

        jdbcPanel.add(getWithLabelInput(TASK_NAME, taskNameInput));
        jdbcPanel.add(getWithLabelInput(JDBC_DBTYPE, typeBox));
        jdbcPanel.add(getWithLabelInput(JDBC_USER, new TextInput(null, jdbc.getUser())));
        jdbcPanel.add(getWithLabelInput(JDBC_PASS, new PasswordInput(null, jdbc.getPassword())));
        jdbcPanel.add(getWithLabelInput(JDBC_URL, new TextInput(null, jdbc.getDbUrl())));
        jdbcPanel.add(getWithLabelInput(JDBC_SCHEMA, new TextInput(null, jdbc.getSchema().replaceAll("`", ""))));

        HDivLayout layout = new HDivLayout(GridSplitEnum.C3);
        HPanel hPanel = new HPanel(layout);
        isAutoStartCb = new CheckBoxInput("auto");
        isAutoStartCb.setValue("true");
        WithLabelInput auto = new WithLabelInput(hPanel, new LabelInput(getLang("is_auto_start")), isAutoStartCb);

        jdbcPanel.add(auto);
        rootPanel.add(gridPanel);
    }

    public boolean isAutoStart() {
        return Boolean.parseBoolean(isAutoStartCb.getValue());
    }

    protected HBarPanel getToolBar() {
        okBtn = new HButton("确定") {
            @Override
            public void onClick() {
                okClick();
            }
        };
        resetBtn = new HButton("重填") {
            @Override
            public void onClick() {
                resetForm();
            }
        };
        HBarPanel toolBar = new HBarPanel();
        toolBar.add(okBtn, resetBtn);
        return toolBar;
    }

    private void okClick() {
        if (anyEmpty()) {
            return;
        }
        try {
            setTask();
            dialog.hide();
            TaskComp.addTask(task, this, isReStart, isAutoStart());
            if (StartUtil.eng != null) {
                StartUtil.eng.doPush(CsMgrEnum.DB_TASK, GuiJsonUtil.toJsonCmd(TaskMgr.CMD_SHOW_TASK_PANEL));
            }
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
        }
    }

    protected JdbcBean getJdbc() {
        JdbcBean jdbc = new JdbcBean();
        String clazz = DriverUtil.getDriverClass(DBTypeEnum.valueOf(jdbcPanel.getInputValue(JDBC_DBTYPE)));
        jdbc.setClazz(clazz);
        jdbc.setDbUrl(jdbcPanel.getInputValue(JDBC_URL).trim());
        jdbc.setPassword(jdbcPanel.getInputValue(JDBC_PASS).trim());

        DriverUtil.getDbTypeOp(jdbc).ifPresent(item -> {
            String inputUser = jdbcPanel.getInputValue(JDBC_USER).trim();
            jdbc.setUser(jdbcEditable ? LoginUtil.getRealName(inputUser, item.name()) : inputUser);
            String inputSchema = jdbcPanel.getInputValue(JDBC_SCHEMA).trim();
            jdbc.setSchema(jdbcEditable ? LoginUtil.getRealName(inputSchema, item.name()) : inputSchema);
        });
        return jdbc;
    }

    protected String getTaskName() {
        return jdbcPanel.getInputValue(TASK_NAME).trim();
    }

    protected void resetForm() {
        ((WithLabelInput) jdbcPanel.getHComp(TASK_NAME)).setValue("");
        ((WithLabelInput) jdbcPanel.getHComp(JDBC_DBTYPE)).setValue(DriverUtil.getDbType(jdbc).name());
        ((WithLabelInput) jdbcPanel.getHComp(JDBC_URL)).setValue(jdbc.getDbUrl());
        ((WithLabelInput) jdbcPanel.getHComp(JDBC_PASS)).setValue(jdbcEditable ? "" : jdbc.getPassword());
        ((WithLabelInput) jdbcPanel.getHComp(JDBC_USER)).setValue(jdbcEditable ? "" : jdbc.getUser());
        ((WithLabelInput) jdbcPanel.getHComp(JDBC_SCHEMA)).setValue(jdbcEditable ? "" : jdbc.getSchema());
    }


    protected boolean anyEmpty() {
        for (String key : jdbcMap.keySet()) {
            String v = jdbcMap.get(key);
            if (v.contains("*")) {
                String value = jdbcPanel.getInputValue(key);
                if (StringUtils.isBlank(value)) {
                    PopPaneUtil.error(dialog.getWindow(), v + "不能为空");
                    return true;
                }
            }
        }
        return false;
    }

    private WithLabelInput getWithLabelInput(String id, AbsInput i) {
        HDivLayout layout = new HDivLayout(GridSplitEnum.C3);
        HPanel hPanel = new HPanel(layout);
        WithLabelInput input = new WithLabelInput(hPanel, jdbcMap.get(id), i);
        input.setId(id);
        return input;
    }

    /**
     *
     */
    public void setJdbcUnEditable() {
        ((WithLabelInput) jdbcPanel.getHComp(JDBC_DBTYPE)).setEnabled(false);
        ((WithLabelInput) jdbcPanel.getHComp(JDBC_USER)).setEnabled(false);
        ((WithLabelInput) jdbcPanel.getHComp(JDBC_PASS)).setEnabled(false);
        ((WithLabelInput) jdbcPanel.getHComp(JDBC_URL)).setEnabled(false);
        ((WithLabelInput) jdbcPanel.getHComp(JDBC_URL)).setEnabled(false);
        ((WithLabelInput) jdbcPanel.getHComp(JDBC_SCHEMA)).setEnabled(false);
    }

    public void setSchema(String schema) {
        ((WithLabelInput) jdbcPanel.getHComp(JDBC_SCHEMA)).setValue(schema);
    }

    public AbsCfgDig setJdbcEditable(boolean jdbcEditable) {
        this.jdbcEditable = jdbcEditable;
        setJdbcUnEditable();
        return this;
    }

    protected static String getLang(String key) {
        return LangMgr.getValue(TaskMgr.class.getName(), key);
    }

    public void disableOptionBar() {
        this.resetBtn.setEnabled(false);
        this.okBtn.setEnabled(false);
    }

    public AbsCfgDig enableOptionBar() {
        this.resetBtn.setEnabled(true);
        this.okBtn.setEnabled(true);
        return this;
    }

    protected void setSize(int height) {
        dialog.setSize(800, height + 40);
    }

}
