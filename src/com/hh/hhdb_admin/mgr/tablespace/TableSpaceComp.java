package com.hh.hhdb_admin.mgr.tablespace;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.tablespace.form.AbsForm;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author YuSai
 */
public abstract class TableSpaceComp {

    private static final String DOMAIN_NAME = TableSpaceComp.class.getName();
    private static final String LOG_NAME = TableSpaceComp.class.getSimpleName();
    static {
        try {
            LangMgr2.loadMerge(TableSpaceComp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final HDialog dialog;
    private final DBTypeEnum dbTypeEnum;
    private final AbsForm spaceForm;

    public TableSpaceComp(Connection conn, DBTypeEnum dbTypeEnum) {
        this.dbTypeEnum = dbTypeEnum;
        TableSpaceUtil.initTableSpace(conn, dbTypeEnum);
        spaceForm = AbsForm.getForm(conn, dbTypeEnum);
        dialog = new HDialog(StartUtil.parentFrame, 800);
        dialog.setRootPanel(getPanel());
        dialog.setWindowTitle(getLang("addTableSpace"));
        dialog.setIconImage(TableSpaceUtil.getIcon("manage"));
        TableSpaceUtil.setDialogSize(dbTypeEnum, dialog);
    }

    public HPanel getPanel() {
        HPanel panel = new HPanel();
        if (spaceForm == null) {
            panel.add(new LabelInput(getLang("notSupport"), AlignEnum.CENTER));
        } else {
            panel.add(initBtnPanel());
            LastPanel lastPanel = new LastPanel();
            lastPanel.set(spaceForm.getPanel().getComp());
            panel.setLastPanel(lastPanel);
        }
        return panel;
    }

    private HPanel initBtnPanel() {
        HPanel btnPanel = new HPanel();
        btnPanel.add(new HeightComp(20));
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton saveBtn = new HButton(getLang("save")) {
            @Override
            public void onClick() {
                save();
            }
        };
        saveBtn.setIcon(TableSpaceUtil.getIcon("save"));
        HButton preViewBtn = new HButton(getLang("previewSql")) {
            @Override
            protected void onClick() {
                previewSql();
            }
        };
        preViewBtn.setIcon(TableSpaceUtil.getIcon("sql_view"));
        barPanel.add(saveBtn);
        if (dbTypeEnum != DBTypeEnum.oracle) {
            barPanel.add(preViewBtn);
        }
        btnPanel.add(barPanel);
        return btnPanel;
    }

    private void save() {
        try {
            if (dbTypeEnum == DBTypeEnum.oracle) {
                TableSpaceUtil.save(spaceForm.getSql());
            } else {
                JsonObject data = spaceForm.getData();
                if (TableSpaceUtil.checkData(dialog, data, dbTypeEnum)) {
                    TableSpaceUtil.save(data);
                    spaceForm.reset();
                }
            }
            dialog.dispose();
            refreshTree();
            PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("addSuccess"));
        } catch (SQLException e) {
            e.printStackTrace();
            logUtil.error(LOG_NAME, e);
            PopPaneUtil.error(dialog.getWindow(), e);
        }
    }

    private void previewSql() {
        try {
            if (dbTypeEnum == DBTypeEnum.oracle) {
                TableSpaceUtil.previewSql(spaceForm.getSql());
            } else {
                JsonObject data = spaceForm.getData();
                if (TableSpaceUtil.checkData(dialog, data, dbTypeEnum)) {
                    TableSpaceUtil.previewSql(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logUtil.error(LOG_NAME, e);
            PopPaneUtil.error(dialog.getWindow(), e);
        }
    }

    public void show() {
        dialog.show();
    }

    public static String getLang(String key) {
        LangMgr2.setDefaultLang(StartUtil.default_language);
        return LangMgr2.getValue(DOMAIN_NAME, key);
    }

    public abstract void refreshTree();

}
