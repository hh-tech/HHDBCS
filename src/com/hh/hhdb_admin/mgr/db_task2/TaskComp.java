package com.hh.hhdb_admin.mgr.db_task2;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.SleepUtil;
import com.hh.frame.dbtask2.TaskType;
import com.hh.frame.dbtask2.bean.BaseTaskBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HWindow;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.job_panel.GuiJob;
import com.hh.frame.swingui.view.job_panel.JobLastPanel;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.mgr.db_task2.dig.AbsCfgDig;
import com.hh.hhdb_admin.mgr.db_task2.dig.TaskJob;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;


public class TaskComp {
    public static HWindow window;
    private static JobLastPanel lastPanel;
    private static LastPanel taskPanel = new LastPanel(false);
    private static HBarPanel toolBar;
    private static ReentrantLock lock = new ReentrantLock();
    private static JdbcBean jdbc;
    private static SelectBox typeBox = null;
    private static Map<TaskJob, AbsCfgDig> taskDigMap = new HashMap<>();

    protected static List<TaskJob> tasks = new ArrayList<>();

    public static LastPanel getLastPanel(JdbcBean jdbc, HWindow window) {
        TaskComp.taskDigMap = new HashMap<>();
        TaskComp.tasks = new ArrayList<>();
        TaskComp.jdbc = jdbc;
        TaskComp.window = window;
        
        initToolbar();
        HPanel headPanel = new HPanel(new HDivLayout(GridSplitEnum.C8));
        headPanel.add(toolBar,lastPanel.getBarPanel());
        
        taskPanel.setHead(headPanel.getComp());
        taskPanel.setWithScroll(lastPanel.getComp());
        return taskPanel;
    }

    /**
     * 添加任务
     * @param taskJob
     * @param task
     * @param cfgDig
     * @param isReStart     是否重启
     * @param isAutoStart   是否自动执行
     * @throws Exception
     */
    public static void addTask(TaskJob taskJob, AbsCfgDig cfgDig, boolean isReStart, boolean isAutoStart) throws Exception {
        try {
            lock.lock();
            if (StringUtils.isBlank(taskJob.getName())) {
                throw new Exception(LangMgr.sLang(CsL.TASK_NAME_NOT_NULL));
            }
            TaskJob oldTask = getTask(taskJob.getName());
            if (isReStart) {
                tasks.set(tasks.indexOf(oldTask), taskJob);
                taskDigMap.remove(oldTask);
            } else {
                if (oldTask != null) {
                    throw new Exception(taskJob.getName() + LangMgr.lang(Word4.EXISTED));
                }
                tasks.add(taskJob);
            }
        } finally {
            lock.unlock();
        }
        taskDigMap.put(taskJob, cfgDig);
        if (isAutoStart) {
            taskJob.start();
        }
        SleepUtil.sleep(200);
        if (!isReStart) lastPanel.add(taskJob);
    }

    private static TaskJob getTask(String name) {
        try {
            lock.lock();
            for (TaskJob t : tasks) {
                if (t.getName().equalsIgnoreCase(name)) {
                    return t;
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    private static void initToolbar() {
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        toolBar = new HBarPanel(l);
        
        lastPanel = new JobLastPanel(1200) {
            @Override
            public void delInform(GuiJob job) {
                for (TaskJob t : tasks) {
                    if (t.getName().equalsIgnoreCase(job.getName())) {
                        tasks.remove(t);
                        taskDigMap.remove(t);
                        return;
                    }
                }
            }
        };
        
        HButton addTaskBtn = new HButton(LangMgr.lang(Word2.ADD)) {
            @Override
            public void onClick() {
                if (typeBox.getValue().equals("CHOOSE_TYPE")) return;
                BaseTaskBean bean = new BaseTaskBean(0,TaskUtil.generateTaskName(TaskType.valueOf(typeBox.getValue())),jdbc);
                AbsCfgDig dig = TaskUtil.getConfigDig(TaskType.valueOf(typeBox.getValue()), bean, true);
                if (dig == null) return;
                dig.show();
            }
        };

        typeBox = new SelectBox() {
            protected void onItemChange(ItemEvent e) {
                addTaskBtn.setEnabled(!typeBox.getValue().equals("CHOOSE_TYPE"));
            }
        };
        typeBox.addOption(LangMgr.sLang(CsL.CHOOSE_TASK_TYPE), "CHOOSE_TYPE");
        Map<String, String> taskMap = TaskUtil.getTaskMap();
        taskMap.keySet().forEach(key -> typeBox.addOption(taskMap.get(key), key));

        toolBar.add(typeBox);
        toolBar.add(addTaskBtn);
    }
}
