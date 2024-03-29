package com.hh.hhdb_admin.mgr.db_task.dig;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.dbtask.CleanTask;
import com.hh.frame.dbtask.DbTask;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author: Jiang
 * @date: 2021/1/28
 */

public class CleanCfgDig extends AbsCfgDig {

    public CleanCfgDig(JdbcBean jdbc, Map<String, String> config) {
        super(jdbc);
        setJdbcPanel();
        boolean isSchema = false;
        if (config != null) {
            isSchema = Boolean.parseBoolean(config.get("isSchema"));
            if (!StringUtils.isBlank(config.get("schema"))) {
                setSchema(config.get("schema"));
            }
            if (config.get("taskName") != null) {
                setTaskName(config.get("taskName"));
            }
        }
        dialog.setWindowTitle(isSchema ? getLang("clean_schema") : getLang("clean_db"));
        rootPanel.add(getToolBar());
        dialog.setRootPanel(rootPanel);
        setSize(rootPanel.getHeight());
    }

    @Override
    public DbTask show() {
        dialog.show();
        return task;
    }

    @Override
    protected void setTask() {
        String taskName = getTaskName();
        JdbcBean jdbc = getJdbc();
        task = new CleanTask(taskName, jdbc);
    }
}
