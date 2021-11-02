package com.hh.hhdb_admin.test.db_task;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.dbtask.DbTask;
import com.hh.frame.dbtask.TaskType;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.db_task.TaskComp;
import com.hh.hhdb_admin.mgr.db_task.TaskMgr;
import com.hh.hhdb_admin.mgr.db_task.dig.AbsCfgDig;
import com.hh.hhdb_admin.mgr.db_task.dig.SqlFileCfgDig;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TaskCompTest {

    private static final HPanel rootPanel = new HPanel();

    public static void main(String[] args) throws Exception {
        HHSwingUi.init();
        try {
            LangMgr2.loadMerge(TaskMgr.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JdbcBean jdbc = MgrTestUtil.getJdbcBean(DBTypeEnum.oracle);
        IconFileUtil.setIconBaseDir(new File("etc/icon/"));
        HFrame f = new HFrame(1024, HFrame.fromW2H(1024));
        HBarPanel toolBar = new HBarPanel();
        HButton btn1 = new HButton("面板添加任务") {
            public void onClick() {
                LastPanel lastPanel = TaskComp.getLastPanel(jdbc);
                rootPanel.setLastPanel(lastPanel);
            }
        };
        TextInput fileEncoding = new TextInput();
        fileEncoding.setValue("UTF-8");
        TextInput fileAdd = new TextInput();
        fileAdd.setValue("D:\\oracle.sql");

        Map<String, String> map = new HashMap<>();
        map.put(SqlFileCfgDig.ENCODING_TEXT_ID, fileEncoding.getValue());
        map.put(SqlFileCfgDig.SQL_FILE_TEXT_ID, fileAdd.getValue());
        SelectBox selectBox = new SelectBox();
        selectBox.addOption("备份数据库", TaskType.BACKUP.name());
        selectBox.addOption("恢复数据库", TaskType.RESTORE.name());
        selectBox.addOption("清除数据库", TaskType.CLEAN.name());
        selectBox.addOption("运行SQL文件", TaskType.SQL_FILE.name());
        selectBox.addOption("生成表格数据", TaskType.GEN_TAB_DATA.name());
        HButton btn2 = new HButton("通知添加任务") {
            public void onClick() {
                newTask(jdbc, map, TaskType.valueOf(selectBox.getValue()));
            }
        };
        toolBar.add(btn1, selectBox, btn2);
        f.setToolBar(toolBar);
        f.setRootPanel(rootPanel);
        f.show();
    }

    private static void newTask(JdbcBean jdbc, Map<String, String> map, TaskType type) {
        AbsCfgDig dig = TaskComp.getConfigDig(jdbc, type, map, true);
        if (dig == null) {
            return;
        }
        DbTask task = dig.show();
        if (task != null) {
            try {
                TaskComp.addTask(task, dig, false, true);
                TaskComp.refresh();
            } catch (Exception e) {
                PopPaneUtil.error(e.getMessage());
            }
        }
    }

}
