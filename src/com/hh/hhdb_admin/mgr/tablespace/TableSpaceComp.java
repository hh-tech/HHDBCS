package com.hh.hhdb_admin.mgr.tablespace;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tablespace.form.AbsForm;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author YuSai
 */
public abstract class TableSpaceComp {
    private final HDialog dialog;
    private final DBTypeEnum dbTypeEnum;
    private final AbsForm spaceForm;

    public TableSpaceComp(Connection conn, DBTypeEnum dbTypeEnum) {
        this.dbTypeEnum = dbTypeEnum;
        TableSpaceUtil.initTableSpace(conn, dbTypeEnum);
        spaceForm = AbsForm.getForm(conn, dbTypeEnum);
        dialog = new HDialog(CsVarUtil.parentFrame, 800);
        dialog.setRootPanel(getPanel());
        dialog.setWindowTitle(LangMgr.lang(Word2.ADD));
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setStatusBar(new HBarPanel());
        TableSpaceUtil.setDialogSize(dbTypeEnum, dialog);
    }

    public HPanel getPanel() {
        HPanel panel = new HPanel();
        if (spaceForm == null) {
            panel.add(new LabelInput(LangMgr.lang(Word4.NOT_SUPPORT), AlignEnum.CENTER));
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
        HButton saveBtn = new HButton(LangMgr.lang(Word2.SAVE)) {
            @Override
            public void onClick() {
                save();
            }
        };
        saveBtn.setIcon(FaSolidEnum.FLOPPY_DISK,16);
        HButton preViewBtn = new HButton(LangMgr.lang(Word2.PREVIEW)) {
            @Override
            protected void onClick() {
                previewSql();
            }
        };
        preViewBtn.setIcon(FaSolidEnum.MAGNIFYING_GLASS,16);
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
            PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.SUCCESSFULLY_ADDED));
        } catch (SQLException e) {
            Lg.error(TableSpaceComp.class, e);
            PopDlgUtil.error(dialog, e);
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
            Lg.error(TableSpaceComp.class, e);
            PopDlgUtil.error(dialog, e);
        }
    }

    public void show() {
        dialog.show();
    }

    public abstract void refreshTree();

}
