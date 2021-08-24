package com.hh.hhdb_admin.mgr.tablespace.form;

import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.util.logUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author YuSai
 */
public class Db2Form extends AbsForm {

    private static final String LOG_NAME = Db2Form.class.getSimpleName();

    private final Connection conn;

    private final HPanel formPanel;

    private final TextInput spaceName;
    private final TextInput pageSize;
    private final SelectBox managedBy;
    private final SelectBox type;
    private final TextInput filePath;
    private final TextInput fileSize;
    private final TextInput extentSize;
    private final TextInput prefetchSize;
    private final SelectBox bufferPool;
    private final TextInput overhead;
    private final TextInput transferRate;
    private final SelectBox fileSystem;
    private final SelectBox recovery;

    public Db2Form(Connection conn) {
        this.conn = conn;
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(10);
        divLayout.setxBorderWidth(30);
        divLayout.setTopHeight(20);
        formPanel = new HPanel(divLayout);
        spaceName = new TextInput("spaceName");
        pageSize = new TextInput("pageSize");
        managedBy = new SelectBox("spaceName");
        type = new SelectBox("type");
        filePath = new TextInput("filePath");
        fileSize = new TextInput("spaceSize");
        extentSize = new TextInput("extentSize");
        prefetchSize = new TextInput("prefetchSize");
        bufferPool = new SelectBox("bufferPool");
        overhead = new TextInput("overHead");
        transferRate = new TextInput("transferRate");
        fileSystem = new SelectBox("fileSystem");
        recovery = new SelectBox("recovery");
        initSelectBox();
    }

    private void initSelectBox() {
        try {
            managedBy.addOption("DATABASE", "DATABASE");
            managedBy.addOption("SYSTEM", "SYSTEM");
            managedBy.addOption("AUTOMATIC STORAGE", "AUTOMATIC STORAGE");
            type.addOption("file", "file");
            type.addOption("device", "device");
            List<Map<String, String>> maps = SqlQueryUtil.selectStrMapList(conn, "select bp_name from sysibmadm.snapbp");
            for (Map<String, String> map : maps) {
                bufferPool.addOption(map.get("bp_name"), map.get("bp_name"));
            }
            fileSystem.addOption("CACHING", "CACHING");
            recovery.addOption("ON", "ON");
            recovery.addOption("OFF", "OFF");
        } catch (SQLException e) {
            e.printStackTrace();
            logUtil.error(LOG_NAME, e);
        }
    }

    @Override
    public HPanel getPanel() {
        formPanel.add(getGridPanel(getLang("tableSpaceName"), spaceName));
        formPanel.add(getGridPanel(getLang("pageSize"), pageSize));
        formPanel.add(getGridPanel(getLang("manageBy"), managedBy));
        formPanel.add(getGridPanel(getLang("type"), type));
        formPanel.add(getGridPanel(getLang("filePath"), filePath));
        formPanel.add(getGridPanel(getLang("fileSize"), fileSize));
        formPanel.add(getGridPanel(getLang("extentSize"), extentSize));
        formPanel.add(getGridPanel(getLang("prefetchSize"), prefetchSize));
        formPanel.add(getGridPanel(getLang("bufferPool"), bufferPool));
        formPanel.add(getGridPanel(getLang("overhead"), overhead));
        formPanel.add(getGridPanel(getLang("transferRate"), transferRate));
        formPanel.add(getGridPanel(getLang("fileSystem"), fileSystem));
        formPanel.add(getGridPanel(getLang("recovery"), recovery));
        return formPanel;
    }

    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.add("spaceName", spaceName.getValue());
        data.add("pageSize", pageSize.getValue());
        data.add("managedBy", managedBy.getValue());
        data.add("type", type.getValue());
        data.add("filePath", filePath.getValue());
        data.add("fileSize", fileSize.getValue());
        data.add("extentSize", extentSize.getValue());
        data.add("prefetchSize", prefetchSize.getValue());
        data.add("bufferPool", bufferPool.getValue());
        data.add("overhead", overhead.getValue());
        data.add("transferRate", transferRate.getValue());
        data.add("fileSystem", fileSystem.getValue());
        data.add("recovery", recovery.getValue());
        return data;
    }

    @Override
    public void reset() {
        spaceName.setValue("");
        pageSize.setValue("");
        managedBy.setValue("DATABASE");
        type.setValue("file");
        filePath.setValue("");
        fileSize.setValue("");
        extentSize.setValue("");
        prefetchSize.setValue("");
        bufferPool.setValue("");
        overhead.setValue("");
        transferRate.setValue("");
        fileSystem.setValue("CACHING");
        recovery.setValue("ON");
    }
}
