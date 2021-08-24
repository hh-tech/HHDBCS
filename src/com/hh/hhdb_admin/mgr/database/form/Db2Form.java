package com.hh.hhdb_admin.mgr.database.form;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.mgr.database.DatabaseComp;

/**
 * @author YuSai
 */
public class Db2Form extends AbsForm {

    private final HPanel formPanel;
    private final TextInput databaseInput;
    private final SelectBox autoStorage;
    private final TextInput autoStoragePath;
    private final TextInput dbPath;
    private final SelectBox codeSet;
    private final SelectBox territory;
    private final TextInput pageSize;


    public Db2Form() {
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(10);
        divLayout.setxBorderWidth(30);
        divLayout.setTopHeight(20);
        formPanel = new HPanel(divLayout);
        databaseInput = new TextInput("database");
        autoStorage = new SelectBox("autoStorage");
        autoStorage.addOption("", "");
        autoStorage.addOption("YES", "YES");
        autoStorage.addOption("NO", "NO");
        autoStoragePath = new TextInput("autoStoragePath");
        dbPath = new TextInput("dbPath");
        codeSet = new SelectBox("codeSet");
        codeSet.addOption("", "");
        codeSet.addOption("UTF-8", "UTF-8");
        codeSet.addOption("GBK", "GBK");
        territory = new SelectBox("territory");
        territory.addOption("", "");
        territory.addOption("CN", "CN");
        pageSize = new TextInput("pageSize");
    }

    @Override
    public HPanel getPanel() {
        formPanel.add(getWithLabelInput(DatabaseComp.getLang("databaseName"), databaseInput));
        formPanel.add(getWithLabelInput(DatabaseComp.getLang("autoStorage"), autoStorage));
        formPanel.add(getWithLabelInput(DatabaseComp.getLang("autoStoragePath"), autoStoragePath));
        formPanel.add(getWithLabelInput(DatabaseComp.getLang("dbPath"), dbPath));
        formPanel.add(getWithLabelInput(DatabaseComp.getLang("codeSet"), codeSet));
        formPanel.add(getWithLabelInput(DatabaseComp.getLang("territory"), territory));
        formPanel.add(getWithLabelInput(DatabaseComp.getLang("pageSize"), pageSize));
        return formPanel;
    }

    @Override
    public JsonObject getData() {
        JsonObject dataJson = new JsonObject();
        dataJson.add("dbName", databaseInput.getValue());
        dataJson.add("autoStorage", autoStorage.getValue());
        dataJson.add("autoStoragePath", autoStoragePath.getValue());
        dataJson.add("dbPath", dbPath.getValue());
        dataJson.add("codeSet", codeSet.getValue());
        dataJson.add("territory", territory.getValue());
        dataJson.add("pageSize", pageSize.getValue());
        return dataJson;
    }

    @Override
    public void reset() {
        databaseInput.setValue("");
        autoStorage.setValue("YES");
        autoStoragePath.setValue("");
        dbPath.setValue("");
        codeSet.setValue("UTF-8");
        territory.setValue("CN");
        pageSize.setValue("");
    }
}
