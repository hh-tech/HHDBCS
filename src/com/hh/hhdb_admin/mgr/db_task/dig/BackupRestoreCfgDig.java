package com.hh.hhdb_admin.mgr.db_task.dig;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.dbtask.BackupTask;
import com.hh.frame.dbtask.DbTask;
import com.hh.frame.dbtask.RestoreTask;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.fc.DirChooserInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Jiang
 * @date: 2021/1/20
 */

public class BackupRestoreCfgDig extends AbsCfgDig {

    private static final String SCHEMA = "schema";
    private static HPanel panel = null;
    private final DirChooserInput dirPath = new DirChooserInput("dirPath");
    private final TextInput threadNum = new TextInput("threadNum");
    private final CheckBoxInput isContinue = new CheckBoxInput("threadNum", getLang("is_continue"));
    private String title = "";
    private boolean isBackup = false;

    public BackupRestoreCfgDig(JdbcBean jdbc, Map<String, String> config) {
        super(jdbc);
        isContinue.setValue("true");
        dirPath.setBtnText(getLang("choose"));
        setJdbcPanel();
        if (config != null) {
            isBackup = Boolean.parseBoolean(config.get("isBackup"));
            this.title = getTitle(config);
            if (!StringUtils.isBlank(config.get(SCHEMA))) {
                super.setSchema(config.get(SCHEMA));
            }
            if (config.get("taskName") != null) {
                setTaskName(config.get("taskName"));
            }
            dirPath.setValue(config.get("dirPath"));
            threadNum.setValue(config.get("threadNum"));
            isContinue.setValue(config.get("isContinue"));
        }
        dialog.setWindowTitle(title);
        initPanel();
        rootPanel.add(panel);
        rootPanel.add(super.getToolBar());
        dialog.setRootPanel(rootPanel);
        super.setSize(rootPanel.getHeight());
    }

    @Override
    public DbTask show() {
        dialog.show();
        return task;
    }

    @Override
    protected void setTask() throws Exception {
        String taskName = super.getTaskName();
        JdbcBean jdbc = super.getJdbc();

        String url = dirPath.getValue();
        if (StringUtils.isBlank(url)) {
            throw new Exception(getLang("url_not_null"));
        }


        Map<String, String> config = new HashMap<String, String>(3) {
            private static final long serialVersionUID = 1L;

            {
                put("dirPath", dirPath.getValue());
                put("threadNum", threadNum.getValue());
                put("isContinue", isContinue.getValue());
            }
        };
        try {
            task = isBackup ? new BackupTask(taskName, jdbc, config) : new RestoreTask(taskName, jdbc, config);
        } catch (Exception e) {
            PopPaneUtil.error(dialog.getWindow(), e);
        }
    }

    private String getTitle(Map<String, String> config) {
        boolean isDb = Boolean.parseBoolean(config.get("isDb"));
        if (isBackup) {
            return isDb ? getLang("db_backup") : getLang("schema_backup");
        } else {
            return isDb ? getLang("db_restore") : getLang("schema_restore");
        }
    }

    private void initPanel() {
        panel = new HPanel();
        panel.setTitle(title + getLang("config"));

        HGridPanel lineOne = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
        lineOne.setComp(1, new LabelInput(getLang("dir_path")));
        lineOne.setComp(2, dirPath);
        File defaultDir = new File("task");
        if (!defaultDir.exists()) {
            defaultDir.mkdir();
        }
        dirPath.setValue(defaultDir.getAbsolutePath());

        HGridPanel lineTwo = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
        lineTwo.setComp(1, new LabelInput(getLang("work_thread")));
        lineTwo.setComp(2, threadNum);
        threadNum.setValue("5");

        HGridPanel lineThree = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
        lineThree.setComp(1, new HPanel());
        lineThree.setComp(2, isContinue);
       // isContinue.setValue("true");

        panel.add(lineOne, lineTwo, lineThree);
    }
}
