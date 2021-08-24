package com.hh.hhdb_admin.mgr.db_task;

import com.hh.frame.common.base.JobStatus;
import com.hh.frame.common.util.SleepUtil;
import com.hh.frame.dbtask.DbTask;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Jiang
 * @Date: 2021/8/12 10:34
 */
public class TaskRefreshThread extends Thread {

    private Map<String, JobStatus> statusMap = new HashMap<>();

    @Override
    public void run() {
        reloadMap();
        while (true) {
            for (DbTask task : TaskComp.tasks) {
                JobStatus cacheStatus = statusMap.get(task.getName());
                if (cacheStatus == null) {
                    statusMap.put(task.getName(), task.getStatus());
                } else {
                    if (cacheStatus != task.getStatus()) {
                        TaskComp.refresh();
                        reloadMap();
                        break;
                    }
                }
            }
            SleepUtil.sleep1000();
        }
    }

    private void reloadMap() {
        statusMap = new HashMap<>();
        TaskComp.tasks.forEach(item -> statusMap.put(item.getName(), item.getStatus()));
    }
}
