package com.hh.hhdb_admin.mgr.workspace;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.util.SleepUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.event.HHEventUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.fc.DirChooserInput;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author YuSai
 */
public class WorkSpaceComp {
    private static final String DOMAIN_NAME = WorkSpaceComp.class.getName();
    private static WsTool wsTool = new WsTool(StartUtil.workspace);

    static {
        try {
            LangMgr2.loadMerge(WorkSpaceComp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final String WORK_SPACE = "workspace";
    private final String NO_POP = "no_pop";
    private HFrame dialog;
    private File jsonFile;
    private JsonObject workSpaceData;
    private DirChooserInput workSpaceInput;
    private boolean isDone = false;
    private final CheckBoxInput popCheck = new CheckBoxInput(null, getLang(NO_POP));


    public WorkSpaceComp() {
        initWorkSpaceData();
    }

    private void initWorkSpaceData() {
        try {
            jsonFile = new File(StartUtil.getEtcFile(), "work_space.json");
            if (!jsonFile.exists()) {
                jsonFile.createNewFile();
                workSpaceData = new JsonObject();
                workSpaceData.set(WORK_SPACE, StartUtil.workspace.getAbsolutePath());
                workSpaceData.set(NO_POP, false);
                FileUtils.writeStringToFile(jsonFile, workSpaceData.toPrettyString(), StandardCharsets.UTF_8);
            } else {
                try {
                    workSpaceData = Json.parse(FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8)).asObject();
                    String wsPath = workSpaceData.getString(WORK_SPACE);
                    Boolean noPop = workSpaceData.getBoolean(NO_POP);
                    if (wsPath == null || noPop == null) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    jsonFile.delete();
                    initWorkSpaceData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(e);
        }
    }

    private void initDialog() {
        dialog = new HFrame(750, 250);
        dialog.setWindowTitle(getLang(WORK_SPACE));
        dialog.setIconImage(IconFileUtil.getLogo().getImage());
        dialog.setCloseType(true);
        dialog.getWindow().setLocationRelativeTo(null);

        workSpaceInput = new DirChooserInput(WORK_SPACE);
        workSpaceInput.setDesc(getLang("desc"));
        workSpaceInput.setValue(workSpaceData.getString(WORK_SPACE));
        workSpaceInput.setBtnText(getLang("btn_value"));

        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HButton applyBtn = new HButton(getLang("ok"));
        applyBtn.addActionListener(e -> click());
        HHEventUtil.addEnterEvent(e -> click(), workSpaceInput.getTextInput().getComp());
        HButton cancelBtn = new HButton(getLang("cancel")) {
            @Override
            public void onClick() {
                dialog.dispose();
                System.exit(0);
            }
        };

        HButton saveBtn = new HButton(getLang("save")) {
            @Override
            public void onClick() {
                try {
                    workSpaceData.set(NO_POP, popCheck.isChecked());
                    workSpaceData.set(WORK_SPACE, workSpaceInput.getValue());
                    FileUtils.writeStringToFile(jsonFile, workSpaceData.toPrettyString(), StandardCharsets.UTF_8);
                    PopPaneUtil.info(dialog.getWindow(), getLang("save_suc"));
                } catch (IOException e) {
                    PopPaneUtil.error(dialog.getWindow(), e);
                }

            }
        };

        HBarPanel barPanel = new HBarPanel();
        barPanel.add(popCheck);
        barPanel.add(saveBtn);
        barPanel.add(applyBtn);
        barPanel.add(cancelBtn);

        HDivLayout layout = new HDivLayout();
        layout.setTopHeight(30);
        layout.setyGap(30);
        HPanel rootPanel = new HPanel(layout);
        rootPanel.add(workSpaceInput);
        rootPanel.add(barPanel);
        rootPanel.setTitle(getLang(WORK_SPACE));
        dialog.setRootPanel(rootPanel);
        dialog.show();
    }

    public void show() {
        initDialog();
        dialog.show();
        while (!isDone) {
            SleepUtil.sleep100();
        }
    }


    public boolean noPop() {
        try {
            if (workSpaceData.getBoolean(NO_POP)) {
                String wsPath = workSpaceData.getString(WORK_SPACE);
                StartUtil.workspace = new File(wsPath);
                wsTool = new WsTool(new File(wsPath));
                wsTool.takeIt();
                return true;
            }
        } catch (Exception e) {
            PopPaneUtil.error(e);
        }
        return false;
    }

    private void click() {
        try {
            String licenseFile = workSpaceInput.getValue();
            if (StringUtils.isEmpty(licenseFile.trim())) {
                PopPaneUtil.info(dialog.getWindow(), getLang("workspaceNotNull"));
                return;
            }
            wsTool = new WsTool(new File(workSpaceInput.getValue()));
            StartUtil.workspace = wsTool.takeIt();
            dialog.dispose();
            isDone = true;
        } catch (Exception e) {
            PopPaneUtil.error(dialog.getWindow(), e);
        }
    }

    static String getLang(String key) {
        LangMgr2.setDefaultLang(StartUtil.default_language);
        return LangMgr2.getValue(DOMAIN_NAME, key);
    }

}
