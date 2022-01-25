package com.hh.hhdb_admin.mgr.db_task.dig;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.dbtask.DbTask;
import com.hh.frame.dbtask.GenTestDataTask;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
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
 * @author YuSai
 */
public class GenTestDataCfgDig extends AbsCfgDig {

    private static final String SCHEMA = "schema";
    private static HPanel panel = null;
    private final DirChooserInput dirPath = new DirChooserInput("dirPath");
    private final TextInput whNum = new TextInput("whNum");
    private final TextInput threadNum = new TextInput("threadNum");
    private final TextInput lineNum = new TextInput("lineNum");
    private final TextInput maxLinePerFile = new TextInput("maxLinePerFile");

    public GenTestDataCfgDig(JdbcBean jdbc, Map<String, String> config) {
        super(jdbc);
        dirPath.setBtnText(getLang("choose"));
        setJdbcPanel();
        if (config != null) {
            if (!StringUtils.isBlank(config.get(SCHEMA))) {
                super.setSchema(config.get(SCHEMA));
            }
            if (config.get("taskName") != null) {
                setTaskName(config.get("taskName"));
            }
            dirPath.setValue(config.get("dirPath"));
            threadNum.setValue(config.get("threadNum"));
        }
        dialog.setWindowTitle(getLang("gen_test_data"));
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
        JdbcBean jdbc = super.getJdbc();
        String url = dirPath.getValue();
        String whNumber = whNum.getValue();
        String lineNumber = lineNum.getValue();
        String maxLine = maxLinePerFile.getValue();
        String threadNumber = threadNum.getValue();
        if (StringUtils.isAnyEmpty(url, whNumber, lineNumber, maxLine, threadNumber)) {
            throw new Exception(getLang("notNull"));
        }
        Map<String, String> config = new HashMap<String, String>(5) {
            private static final long serialVersionUID = 1L;
            {
                put("whNum", whNumber);
                put("lineNum", lineNumber);
                put("maxLinePerFile", maxLine);
                put("dirPath", url);
                put("threadNum", threadNumber);
            }
        };
        DBTypeEnum dbtype = DriverUtil.getDbType(jdbc);
        if (dbtype != null) {
            if (dbtype != DBTypeEnum.oracle
                    && dbtype != DBTypeEnum.hhdb
                    && dbtype != DBTypeEnum.pgsql
                    && dbtype != DBTypeEnum.mysql
                    && dbtype != DBTypeEnum.sqlserver
                    && dbtype != DBTypeEnum.db2
                    && dbtype != DBTypeEnum.dm) {
                PopPaneUtil.info(dialog.getWindow(), "暂不支持" + dbtype.name() + "数据库的敏感数据生成。");
            } else {
                task = new GenTestDataTask(getTaskName(), jdbc, config);
            }
        }
    }

    private void initPanel() {
        panel = new HPanel();
        panel.setTitle(getLang("dataGenerateConfig"));
        HGridPanel lineOne = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
        lineOne.setComp(1, new LabelInput(getLang("warehouses")));
        lineOne.setComp(2, whNum);
        whNum.setValue("10");
        HGridPanel lineTwo = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
        lineTwo.setComp(1, new LabelInput(getLang("data_row")));
        lineTwo.setComp(2, lineNum);
        lineNum.setValue("100000");
        HGridPanel lineThree = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
        lineThree.setComp(1, new LabelInput(getLang("maxLinePerFile")));
        lineThree.setComp(2, maxLinePerFile);
        maxLinePerFile.setValue("1000");
        HGridPanel lineFour = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
        lineFour.setComp(1, new LabelInput(getLang("dir_path")));
        lineFour.setComp(2, dirPath);
        File defaultDir = new File("task");
        if (!defaultDir.exists()) {
            defaultDir.mkdir();
        }
        dirPath.setValue(defaultDir.getAbsolutePath());
        HGridPanel lineFive = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
        lineFive.setComp(1, new LabelInput(getLang("work_thread")));
        lineFive.setComp(2, threadNum);
        threadNum.setValue("5");
        panel.add(lineOne, lineTwo, lineThree, lineFour, lineFive);
    }

}
