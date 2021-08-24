package com.hh.hhdb_admin.mgr.trigger.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.triggerMr.mr.AbsTriggerMr;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.lang.LangUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.table.comp.SqlViewDialog;
import com.hh.hhdb_admin.mgr.trigger.TriggerUtil;
import com.hh.hhdb_admin.mgr.trigger.comp.form.TriggerForm;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class TriggerComp {

    private TriggerForm triggerForm;
    private HPanel hPanel;
    private HDialog dialog;
    private DBTypeEnum dbType;
    private Connection conn;
    private String schemaName;
    private String tableName;
    private AbsTriggerMr triggerMr;
    private final SqlViewDialog sqlViewComp = new SqlViewDialog();
    private static final String domainName = TriggerComp.class.getName();

    static {
        LangMgr.merge(domainName, LangUtil.loadLangRes(TriggerComp.class));
    }

    /**
     * 触发器
     *
     * @param schemaName 模式名
     * @param tableName  表名
     */
    public TriggerComp(Connection conn, JdbcBean jdbc, String schemaName, String tableName) {
        try {
            this.dialog = new HDialog(StartUtil.parentFrame, 800);
            this.conn = conn;
            this.schemaName = schemaName;
            this.tableName = tableName;
            this.dbType = DriverUtil.getDbType(conn);
            this.triggerMr = AbsTriggerMr.genTriggerMr(dbType);
            this.triggerForm = TriggerUtil.getTriggerComp(dialog, conn, jdbc, schemaName, tableName);
            LastPanel lastPanel = new LastPanel(false);
            HPanel panel = triggerForm.getPanel();
            lastPanel.set(panel.getComp());
            HBarLayout l = new HBarLayout();
            l.setAlign(AlignEnum.LEFT);
            l.setxGap(2);
            HBarPanel toolBar = new HBarPanel(l);
            if (TriggerUtil.isSqlEditModal(conn)) {
                toolBar.add(getSqlEditBtn());
            } else {
                toolBar.add(getSaveBtn());
                toolBar.add(getSqlViewBtn());
            }
            hPanel = new HPanel();
            lastPanel.setHead(toolBar.getComp());
            hPanel.setLastPanel(lastPanel);
            initDialog(panel.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
    }

    /**
     * 组件显示
     */
    public void show() {
        try {
            if (triggerMr == null) {
                throw new Exception(getLang("notSupport"));
            }
            if (StringUtils.isBlank(schemaName)) {
                throw new Exception(getLang("schemaIsNull"));
            }
            dialog.setWindowTitle(getLang("CreateTitle"));
            dialog.setRootPanel(hPanel);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
    }

    /**
     * 组件修改
     */
    public void show(String triggerName) {
        try {
            if (triggerMr == null) {
                throw new Exception(getLang("notSupport"));
            }
            if (StringUtils.isBlank(schemaName)) {
                throw new Exception(getLang("schemaIsNull"));
            }
            if (StringUtils.isBlank(triggerName)) {
                throw new Exception(getLang("triggerNameIsNull"));
            }
            if (TriggerUtil.isSqlEditModal(conn)) {
                String updateSql = TriggerUtil.getTriggerSqlForUpdate(conn, triggerName, schemaName);
                if (StringUtils.isBlank(updateSql)) {
                    throw new Exception(getLang("notSupportEdit"));
                }
                TriggerEditDialog triggerEditDialog = new TriggerEditDialog(updateSql);
                addSqlRunBtn(triggerEditDialog);
                triggerEditDialog.show();
            } else {
                triggerForm.initFormData(triggerName);
                dialog.setWindowTitle(getLang("UpdateTitle"));
                dialog.setRootPanel(hPanel);
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
    }

    public static String getLang(String key) {
        return LangMgr.getDefaultValue(domainName, key);
    }

    /**
     * 刷新树方式
     */
    protected abstract void refreshTreeData(String schemaName, String tabName);

    /**
     * 初始化dialog
     */
    private void initDialog(int height) {
        TriggerUtil.setDialogSize(dbType, dialog, height);
        dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
    }

    /**
     * sql预览butn
     */
    private HButton getSqlViewBtn() {
        HButton sqlBtn = new HButton() {
            @Override
            protected void onClick() {
                if (triggerForm.checkForm()) {
                    sqlViewComp.setSql(triggerForm.getSql());
                    sqlViewComp.show();
                }
            }
        };
        sqlBtn.setIcon(IconFileUtil.getIcon(new IconBean(CsMgrEnum.USR.name(), "viewsql", IconSizeEnum.SIZE_16)));
        sqlBtn.setText(getLang("PREVIEW_SQL"));
        return sqlBtn;
    }

    /**
     * 保存按钮
     */
    private HButton getSaveBtn() {
        HButton saveBtn = new HButton(getLang("save")) {
            @Override
            protected void onClick() {
                try {
                    if (triggerForm.checkForm()) {
                        String sql = triggerForm.getSql();
                        if (StringUtils.isBlank(sql)) {
                            return;
                        }
                        SqlExeUtil.executeUpdate(conn, sql);
                        refreshTreeData(schemaName, tableName);
                        dialog.dispose();
                        PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("CreateSuccess"));
                    }
                } catch (Exception e) {
                    PopPaneUtil.error(dialog.getWindow(), e);
                }
            }
        };
        saveBtn.setIcon(IconFileUtil.getIcon(new IconBean(CsMgrEnum.TRIGGER.name(), "save", IconSizeEnum.SIZE_16)));
        return saveBtn;
    }

    /**
     * sql编辑butn
     */
    private HButton getSqlEditBtn() {
        HButton editBtn = new HButton() {
            @Override
            protected void onClick() {
                if (triggerForm.checkForm()) {
                    TriggerEditDialog triggerEditDialog = new TriggerEditDialog(triggerForm.getSql());
                    addPreStepBtn(triggerEditDialog);
                    addSqlRunBtn(triggerEditDialog);
                    dialog.hide();
                    triggerEditDialog.show();
                }
            }
        };
        editBtn.setIcon(IconFileUtil.getIcon(new IconBean(CsMgrEnum.TRIGGER.name(), "next", IconSizeEnum.OTHER)));
        editBtn.setText(getLang("nextEditSql"));
        return editBtn;
    }

    /**
     * 添加执行按钮
     */
    private void addSqlRunBtn(TriggerEditDialog triggerEditDialog) {
        HButton button = new HButton(getLang("CREATE"));
        button.setIcon(IconFileUtil.getIcon(new IconBean(CsMgrEnum.TRIGGER.name(), "trigger", IconSizeEnum.OTHER)));
        triggerEditDialog.getToolBar().add(button);
        button.addActionListener(e -> {
            try {
                ConnUtil.setCurrentSchema(conn, DbCmdStrUtil.toDbCmdStr(schemaName, dbType));
                SqlExeUtil.executeUpdate(conn, triggerEditDialog.getTriggerSql());
                triggerEditDialog.close();
                dialog.dispose();
                refreshTreeData(schemaName, tableName);
                PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("CreateSuccess"));
            } catch (SQLException ex) {
                PopPaneUtil.error(dialog.getWindow(), ex);
            }
        });
    }

    /**
     * 添加上一步按钮
     */
    private void addPreStepBtn(TriggerEditDialog triggerEditDialog) {
        HButton preButton = new HButton(getLang("PRE")) {
            @Override
            protected void onClick() {
                triggerEditDialog.close();
                dialog.show();
            }
        };
        preButton.setIcon(IconFileUtil.getIcon(new IconBean(CsMgrEnum.TRIGGER.name(), "pre", IconSizeEnum.OTHER)));
        triggerEditDialog.getToolBar().add(preButton);
    }

}
