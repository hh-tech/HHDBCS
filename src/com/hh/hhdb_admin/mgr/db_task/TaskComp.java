package com.hh.hhdb_admin.mgr.db_task;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.base.JobStatus;
import com.hh.frame.common.util.DateUtil;
import com.hh.frame.common.util.EnumUtil;
import com.hh.frame.common.util.SleepUtil;
import com.hh.frame.dbtask.DbTask;
import com.hh.frame.dbtask.TaskType;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.bar.BarCol;
import com.hh.frame.swingui.view.tab.col.bar.ColButton;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;
import com.hh.frame.swingui.view.tab.col.icon.IconCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.db_task.dig.*;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.ItemEvent;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


public class TaskComp {
    private static final String CHOOSE_TYPE = "CHOOSE_TYPE";
    private static final String COL_NAME = "name";
    private static final String COL_TASK_DES = "task_des";
    private static final String COL_STATUS = "status";
    private static final String COL_ERROR = "error";
    private static final String COL_DBTYPE = "dbtype";
    private static final String COL_BEGIN = "begin";
    private static final String COL_SPEND = "spend";
    private static final String COL_END = "end";
    private static final String COL_BAR = "bar";

    private static final LastPanel TASK_PANEL = new LastPanel(false);
    private static final HBarPanel TOOL_BAR = new HBarPanel();
    private static final HTable TASK_TABLE = new HTable();
    private static final Map<DbTask, AbsCfgDig> TASK_DIG_MAP = new HashMap<>();
    private static final ReentrantLock lock = new ReentrantLock();
    private static JdbcBean jdbc;
    private static SelectBox typeBox = null;

    protected static final List<DbTask> tasks = new ArrayList<>();

    static {
        initToolbar();
        initTab();
        TASK_PANEL.setHead(TOOL_BAR.getComp());
        TASK_PANEL.setWithScroll(TASK_TABLE.getComp());
    }

    public static LastPanel getLastPanel(JdbcBean jdbc) {
        TaskComp.jdbc = jdbc;
        return TASK_PANEL;
    }

    public static void setJdbc(JdbcBean jdbc) {
        TaskComp.jdbc = jdbc;
    }

    public static void addTask(DbTask task, AbsCfgDig cfgDig, boolean isReStart, boolean isAutoStart) throws Exception {
        try {
            lock.lock();
            if (StringUtils.isBlank(task.getName())) {
                throw new Exception(TaskMgr.getLang("taskNameNotNull"));
            }
            DbTask oldTask = getTask(task.getName());
            if (isReStart) {
                tasks.set(tasks.indexOf(oldTask), task);
                TASK_DIG_MAP.remove(oldTask);
            } else {
                if (oldTask != null) {
                    throw new Exception(task.getName() + TaskMgr.getLang("existed"));
                }
                tasks.add(task);
            }
        } finally {
            lock.unlock();
        }
        TASK_DIG_MAP.put(task, cfgDig);
        if (isAutoStart) {
            task.startTask();
        }
        SleepUtil.sleep(200);
        refresh();
    }

    public static void refresh() {
        TASK_TABLE.load(toData(), 1);
    }

    public static AbsCfgDig getConfigDig(JdbcBean jdbc, TaskType taskType, Map<String, String> config, String taskName, boolean editable) {
        AbsCfgDig absCfgDig;
        config.put("taskName", taskName);
        switch (taskType) {
            case SQL_FILE:
                absCfgDig = new SqlFileCfgDig(jdbc, config);
                break;
            case BACKUP:
            case RESTORE:
                config.put("isBackup", String.valueOf(taskType == TaskType.BACKUP));
                absCfgDig = new BackupRestoreCfgDig(jdbc, config);
                break;
            case CLEAN:
                absCfgDig = new CleanCfgDig(jdbc, config);
                break;
            case GEN_TAB_DATA:
                absCfgDig = new GenTabDataCfgDig(jdbc, config);
                break;
            case EXP_QUERY_AS_INSERT:
            case EXP_QUERY_AS_XLS:
                absCfgDig = new ExpQueryCfgDig(jdbc, config);
                break;
            case GEN_TEST_DATA:
                absCfgDig = new GenTestDataCfgDig(jdbc, config);
                break;
            default:
                return null;
        }
        return absCfgDig.setJdbcEditable(editable);
    }

    /**
     * 根据任务类型获取任务配置面板
     *
     * @param jdbc     jdbc
     * @param taskType 任务类型
     * @param config   任务额外配置
     * @param editable jdbc是否可修改
     * @return 任务配置面板
     */
    public static AbsCfgDig getConfigDig(JdbcBean jdbc, TaskType taskType, Map<String, String> config, boolean editable) {
        return getConfigDig(jdbc, taskType, config, TaskUtil.generateTaskName(taskType), editable);
    }

    private static DbTask getTask(String name) {
        try {
            lock.lock();
            for (DbTask t : tasks) {
                if (t.getName().equalsIgnoreCase(name)) {
                    return t;
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    private static List<Map<String, String>> toData() {
        List<Map<String, String>> data = new ArrayList<>();
        try {
            lock.lock();
            for (DbTask t : tasks) {
                Map<String, String> line = toLine(t);
                data.add(line);
            }
            return data;
        } finally {
            lock.unlock();
        }
    }

    private static Map<String, String> toLine(DbTask t) {
        Map<String, String> map = new HashMap<>();
        map.put(COL_NAME, t.getName());
        map.put(COL_TASK_DES, TaskUtil.getTypeMap().get(TaskUtil.getTypeLabel(t).name()));
        map.put(COL_STATUS, t.getStatus().name());
        if (StringUtils.isNotBlank(t.getErrMsg())) {
            map.put(COL_ERROR, String.valueOf(true));
        } else {
            map.put(COL_ERROR, String.valueOf(false));
        }
        map.put(COL_DBTYPE, t.getDbType().name());
        map.put(COL_BEGIN, (t.getBegin() == null) ? null : DateUtil.dateToStr(t.getBegin(), false));
        map.put(COL_END, (t.getEnd() == null) ? null : DateUtil.dateToStr(t.getEnd(), false));
        if (t.getBegin() == null) {
            map.put(COL_SPEND, "");
        } else {
            if (t.getEnd() == null) {
                map.put(COL_SPEND, (System.currentTimeMillis() - t.getBegin().getTime()) / 1000 + TaskMgr.getLang("second"));
            } else {
                map.put(COL_SPEND, (t.getEnd().getTime() - t.getBegin().getTime()) / 1000 + TaskMgr.getLang("second"));
            }
        }
        map.put(COL_BAR, t.getStatus().name());
        return map;
    }

    private static void initToolbar() {
        HButton addTaskBtn = new HButton(TaskMgr.getLang("add")) {
            @Override
            public void onClick() {
                addTaskFromToolbar();
            }
        };

        HButton delTaskBtn = new HButton(TaskMgr.getLang("remove")) {
            @Override
            public void onClick() {
                List<HTabRowBean> selectRows = TASK_TABLE.getSelectedRowBeans();
                if (selectRows.size() <= 0) {
                    return;
                }
                if (!PopPaneUtil.confirm(TaskMgr.getLang("confirmRemove"))) {
                    return;
                }
                TASK_TABLE.getSelectedRowBeans().forEach(row -> delTask(row.getOldRow().get(COL_NAME)));
            }
        };

        HButton refreshBtn = new HButton(TaskMgr.getLang("refresh")) {
            @Override
            public void onClick() {
                refresh();
            }
        };

        typeBox = new SelectBox() {
            protected void onItemChange(ItemEvent e) {
                addTaskBtn.setEnabled(!typeBox.getValue().equals(CHOOSE_TYPE));
            }
        };
        typeBox.addOption(TaskMgr.getLang("chooseTaskType"), CHOOSE_TYPE);
        TaskUtil.getTypeMap().keySet().forEach(key -> typeBox.addOption(TaskUtil.getTypeMap().get(key), key));

        TOOL_BAR.add(typeBox);
        TOOL_BAR.add(addTaskBtn);
        TOOL_BAR.add(delTaskBtn);
        TOOL_BAR.add(refreshBtn);
    }

    private static void addTaskFromToolbar() {
        if (typeBox.getValue().equals(CHOOSE_TYPE)) {
            return;
        }
        AbsCfgDig dig = getConfigDig(jdbc, TaskType.valueOf(typeBox.getValue()), new HashMap<>(), true);
        if (dig == null) {
            return;
        }
        dig.show();
    }

    private static void initTab() {
        DataCol name = new DataCol(COL_NAME, TaskMgr.getLang("name"));
        DataCol taskDesc = new DataCol(COL_TASK_DES, TaskMgr.getLang("desc"));
        IconCol status = new IconCol(COL_STATUS, TaskMgr.getLang("status"));
        for (String st : EnumUtil.toArray(JobStatus.class)) {
            status.putIcon(st, IconFileUtil.getStatusIcon(JobStatus.valueOf(st)));
            if (st.equals(JobStatus.DONE.name())) {
                status.putTip(st, TaskMgr.getLang("done"));
            }
            if (st.equals(JobStatus.PREPARE.name())) {
                status.putTip(st, TaskMgr.getLang("prepare"));
            }
            if (st.equals(JobStatus.RUNNING.name())) {
                status.putTip(st, TaskMgr.getLang("running"));
            }
        }
        BoolCol bool = new BoolCol(COL_ERROR, TaskMgr.getLang("error"));
        bool.setFalseRenderIcon(IconFileUtil.hasError(false));
        bool.setFalseTip(TaskMgr.getLang("noError"));
        bool.setTrueRenderIcon(IconFileUtil.hasError(true));
        bool.setTrueTip(TaskMgr.getLang("hasError"));
        IconCol dbtype = new IconCol(COL_DBTYPE, TaskMgr.getLang("taskType"));
        for (String iconName : EnumUtil.toArray(DBTypeEnum.class)) {
            dbtype.putIcon(iconName, IconFileUtil.getDbIcon(DBTypeEnum.valueOf(iconName)));
            dbtype.putTip(iconName, iconName);
        }

        DataCol begin = new DataCol(COL_BEGIN, TaskMgr.getLang("startTime"));
        DataCol spend = new DataCol(COL_SPEND, TaskMgr.getLang("duration"));
        DataCol end = new DataCol(COL_END, TaskMgr.getLang("endTime"));

        ColButton optionBtn = new ColButton() {
            @Override
            protected void init(String value) {
                if (value.equals(JobStatus.PREPARE.name())) {
                    this.setText(TaskMgr.getLang("start"));
                } else if (value.equals(JobStatus.RUNNING.name())) {
                    this.setText(TaskMgr.getLang("stop"));
                } else {
                    this.setText(TaskMgr.getLang("restart"));
                }
            }

            @Override
            protected void onClick(HTabRowBean rowBean) {
                DbTask task = getTask(rowBean);
                if (task.getStatus() == JobStatus.PREPARE) {
                    task.startTask();
                    setText(TaskMgr.getLang("stop"));
                } else if (task.getStatus() == JobStatus.RUNNING) {
                    task.stopTask();
                    setText(TaskMgr.getLang("restart"));
                } else if (task.getStatus() == JobStatus.DONE) {
                    try {
                        AbsCfgDig dig = TASK_DIG_MAP.get(task);
                        dig.setReStart(true).enableOptionBar().show();
                        Thread.sleep(500);
                        setText(TaskMgr.getLang("stop"));
                    } catch (Exception e) {
                        PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
                    }
                }
                SleepUtil.sleep100();
                refresh();
            }
        };

        ColButton configBtn = new ColButton(TaskMgr.getLang("view")) {
            @Override
            protected void init(String value) {

            }

            @Override
            protected void onClick(HTabRowBean rowBean) {
                DbTask task = getTask(rowBean);
                AbsCfgDig cfgDig = TASK_DIG_MAP.get(task);
                cfgDig.disableOptionBar();
                cfgDig.show();
            }
        };

        ColButton logBtn = new ColButton(TaskMgr.getLang("log")) {
            @Override
            protected void init(String value) {
            }

            @Override
            protected void onClick(HTabRowBean rowBean) {
                TaskUtil.showTaskInfo(getTask(rowBean));
            }
        };

        BarCol toolBarCol = new BarCol(COL_BAR, TaskMgr.getLang("toolbar"), optionBtn, configBtn, logBtn);
        toolBarCol.setWidth(200);
        TASK_TABLE.addCols(name, taskDesc, status, bool, dbtype, begin, spend, end, toolBarCol);
        TASK_TABLE.setNullSymbol("");
        //todo 临时解决设置表格不可编辑之后单独设置某列可编辑也无法编辑的bug，等待table完善
        name.setCellEditable(false);
        taskDesc.setCellEditable(false);
        status.setCellEditable(false);
        bool.setCellEditable(false);
        dbtype.setCellEditable(false);
        begin.setCellEditable(false);
        spend.setCellEditable(false);
        end.setCellEditable(false);
        toolBarCol.setCellEditable(true);
        refresh();
    }

    /**
     * 根据表格点击行获取对应任务
     *
     * @param rowBean 表格行
     * @return 任务
     */
    private static DbTask getTask(HTabRowBean rowBean) {
        boolean isCurrentRow = rowBean.getCurrRow() != null && rowBean.getCurrRow().containsKey(COL_NAME);
        String taskName = isCurrentRow ? rowBean.getCurrRow().get(COL_NAME) : rowBean.getOldRow().get(COL_NAME);
        return getTask(taskName);
    }

    /**
     * 删除任务
     *
     * @param name 任务名
     */
    private static void delTask(String name) {
        try {
            lock.lock();
            for (DbTask t : tasks) {
                if (t.getName().equalsIgnoreCase(name)) {
                    if (t.getStatus() == JobStatus.RUNNING) {
                        PopPaneUtil.error(StartUtil.parentFrame.getWindow(), name + TaskMgr.getLang("stillRunning"));
                    } else {
                        tasks.remove(t);
                    }
                    TASK_DIG_MAP.remove(t);
                    return;
                }
            }
        } finally {
            lock.unlock();
            refresh();
        }
    }

}
