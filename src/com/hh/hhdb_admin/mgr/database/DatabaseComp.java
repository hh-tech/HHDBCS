package com.hh.hhdb_admin.mgr.database;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.database.form.AbsForm;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author YuSai
 */
public abstract class DatabaseComp {

    private static final String LOG_NAME = DatabaseComp.class.getSimpleName();
    private static final String DOMAIN_NAME = DatabaseComp.class.getName();

    static {
        try {
            LangMgr2.loadMerge(DatabaseComp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HDialog dialog;
    private final DBTypeEnum dbTypeEnum;
    private final AbsForm databaseForm;

    public DatabaseComp(Connection conn, DBTypeEnum dbTypeEnum, String user) throws Exception {
        this.dbTypeEnum = dbTypeEnum;
        DatabaseUtil.initDatabase(conn, dbTypeEnum);
        this.databaseForm = AbsForm.getForm(conn, dbTypeEnum, user);
        dialog = new HDialog(StartUtil.parentFrame, 600, 500);
        dialog.setWindowTitle(getLang("addDatabase"));
        dialog.setIconImage(IconFileUtil.getLogo());
    }

    public HPanel getPanel() {
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(10);
        divLayout.setxBorderWidth(30);
        divLayout.setTopHeight(20);
        HPanel panel = new HPanel(divLayout);
        panel.add(getBarPanel());
        panel.add(databaseForm.getPanel());
        return panel;
    }

    private HBarPanel getBarPanel() {
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton saveBtn = new HButton(getLang("save")) {
            @Override
            public void onClick() {
                save();
            }
        };
        saveBtn.setIcon(getIcon("save"));
        HButton preViewBtn = new HButton(getLang("previewSql")) {
            @Override
            protected void onClick() {
                previewSql();
            }
        };
        preViewBtn.setIcon(getIcon("sql_view"));
        barPanel.add(saveBtn);
        barPanel.add(preViewBtn);
        return barPanel;
    }

    private void save() {
        JsonObject data = databaseForm.getData();
        if (DatabaseUtil.checkData(dialog, data, dbTypeEnum)) {
            try {
                DatabaseUtil.save(data);
                databaseForm.reset();
                dialog.dispose();
                refreshTree();
                PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("addSuccess"));
            } catch (SQLException e) {
                e.printStackTrace();
                logUtil.error(LOG_NAME, e);
                PopPaneUtil.error(dialog.getWindow(), e);
            }
        }
    }

    private void previewSql() {
        JsonObject data = databaseForm.getData();
        if (DatabaseUtil.checkData(dialog, data, dbTypeEnum)) {
            try {
                DatabaseUtil.previewSql(data);
            } catch (Exception e) {
                e.printStackTrace();
                logUtil.error(LOG_NAME, e);
                PopPaneUtil.error(dialog.getWindow(), e);
            }
        }
    }

    public void show() {
        dialog.setRootPanel(getPanel());
        dialog.show();
    }

    public static String getLang(String key) {
        LangMgr2.setDefaultLang(StartUtil.default_language);
        return LangMgr2.getValue(DOMAIN_NAME, key);
    }

    private ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.DATABASE.name(), name, IconSizeEnum.SIZE_16));
    }

    public abstract void refreshTree();

}
