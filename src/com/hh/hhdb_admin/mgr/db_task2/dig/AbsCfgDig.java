package com.hh.hhdb_admin.mgr.db_task2.dig;


import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.EnumUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.dbtask2.AbsDbTask;
import com.hh.frame.dbtask2.bean.BaseTaskBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.ctrl.HImage;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.PasswordInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.comp.DBTypeComboBox;
import com.hh.hhdb_admin.mgr.db_task2.TaskComp;
import com.hh.hhdb_admin.mgr.db_task2.TaskUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.Objects;

public abstract class AbsCfgDig {
    protected HPanel rootPanel = null;
    protected BaseTaskBean bean;
    protected JdbcBean jdbc;
    protected AbsDbTask task = null;
    protected TaskJob taskJob = null;
    protected boolean jdbcEditable;
    protected HDialog dialog;

    private final TextInput taskNameInput = new TextInput();
    private DBTypeComboBox typeInput;
    private TextInput usetInput;
    private TextInput urlInput;
    private TextInput schemalInput;
    private PasswordInput passwordInput;
    private CheckBoxInput isAutoStartCb;
    protected HButton okBtn;
    protected HButton resetBtn;
    
    private boolean isReStart = false;  //是否重新运行


    public AbsCfgDig(BaseTaskBean bean) {
        this.jdbc = bean.getJdbc();
        dialog = new HDialog(CsVarUtil.parentFrame, 800) {
            @Override
            protected void closeEvent() {
                task = null;
                taskJob = null;
            }
        };
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setStatusBar(new HBarPanel());
    }

    public void show(){
        dialog.show();
    }
    
    public boolean isAutoStart() {
        return Boolean.parseBoolean(isAutoStartCb.getValue());
    }
    
    public void setSchema(String schema) {
        schemalInput.setValue(schema);
    }
    
    public void setJdbcEditable(boolean jdbcEditable) {
        this.jdbcEditable = jdbcEditable;
        typeInput.setEnabled(jdbcEditable);
        usetInput.setEnabled(jdbcEditable);
        passwordInput.setEnabled(jdbcEditable);
        urlInput.setEnabled(jdbcEditable);
        schemalInput.setEnabled(jdbcEditable);
    }

    protected abstract void setTask() throws Exception;

    protected void setTaskName(String taskName) {
        this.taskNameInput.setValue(taskName);
    }

    protected void setJdbcPanel() {
        if (DriverUtil.getDbType(jdbc) == null) return;
    
        HPanel jdbcPanel = new HPanel(new HDivLayout(GridSplitEnum.C3));
        HImage jdbcIcon = new HImage(IconFileUtil.getDbIcon(DriverUtil.getDbType(jdbc), IconSizeEnum.SIZE_128));
        jdbcIcon.setHAlign(AlignEnum.CENTER);
        
        passwordInput = new PasswordInput(null, jdbc.getPassword());
        passwordInput.setEyeVisible(false);
    
        typeInput = new DBTypeComboBox(){
            @Override
            protected void dbTypeItemChange(String value) {
                jdbcIcon.setImg(IconFileUtil.getDbIcon(DBTypeEnum.valueOf(getDbTypeValue()), IconSizeEnum.SIZE_128));
                if (urlInput != null) {
                    DBTypeEnum.Version ver = StringUtils.isNoneBlank(getVersValue()) ? DBTypeEnum.Version.valueOf(typeInput.getVersValue()) : null;
                    urlInput.setValue(DriverUtil.getDriverUrl(DBTypeEnum.valueOf(getDbTypeValue()), ver));
                }
            }
    
            @Override
            protected void versItemChange(ItemEvent e) {
                if (urlInput != null && e.getStateChange() == ItemEvent.SELECTED) {
                    DBTypeEnum.Version ver = StringUtils.isNoneBlank(getVersValue()) ? DBTypeEnum.Version.valueOf(getVersValue()) : null;
                    urlInput.setValue(DriverUtil.getDriverUrl(DBTypeEnum.valueOf(getDbTypeValue()), ver));
                }
            }
        };
        typeInput.setDataList(TaskUtil.getDbTypes());
        typeInput.setDbTypeValue(DBTypeEnum.hhdb.name());
        if (DriverUtil.getDbType(jdbc) != null) typeInput.setDbTypeValue(DriverUtil.getDbType(jdbc).name());
        if (null != jdbc.getCurrentVersion()) typeInput.setVersValue(jdbc.getCurrentVersion().name());
        
    
        isAutoStartCb = new CheckBoxInput("auto");
        isAutoStartCb.setValue("true");
        
        jdbcPanel.add(new LabelInput(LangMgr.lang(Word4.TASK_NAME)+"* : "),taskNameInput);
        jdbcPanel.add(new LabelInput(LangMgr.lang(Word3.DATABASE)+"* : "),typeInput.getHpanel());
        jdbcPanel.add(new LabelInput(LangMgr.lang(Word2.USERNAME)+"* : "),usetInput = new TextInput(null, jdbc.getUser()));
        jdbcPanel.add(new LabelInput(LangMgr.lang(Word2.PASSWORD)+"* : "),passwordInput);
        jdbcPanel.add(new LabelInput(LangMgr.lang(Word2.CONNECT)+"* : "),urlInput = new TextInput(null, jdbc.getDbUrl()));
        jdbcPanel.add(new LabelInput(LangMgr.lang(Word2.MODE)+" : "),schemalInput = new TextInput(null, StringUtils.isNotBlank(jdbc.getSchema()) ?jdbc.getSchema().replaceAll("`", "") : ""));
        jdbcPanel.add(new LabelInput(LangMgr.lang(Word4.AUTO_START)+" : "),isAutoStartCb);
    
        HGridPanel gridPanel = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
        gridPanel.setComp(1, jdbcIcon);
        gridPanel.setComp(2, jdbcPanel);
        gridPanel.setTitle(LangMgr.sLang(CsL.DATABASE_CONFIG));
    
        rootPanel = new HPanel();
        rootPanel.add(gridPanel);
    }

    protected HBarPanel getToolBar() {
        okBtn = new HButton(LangMgr.lang(Word2.CONFIRM)) {
            @Override
            public void onClick() {
                okClick();
            }
        };
        resetBtn = new HButton(LangMgr.lang(Word2.REFILL)) {
            @Override
            public void onClick() {
                resetForm();
            }
        };
        HBarPanel toolBar = new HBarPanel();
        toolBar.add(okBtn, resetBtn);
        return toolBar;
    }

    private void okClick() {
        if (anyEmpty()) return;
        try {
            setTask();
            if (null == taskJob) {
                taskJob = new TaskJob() {
                    @Override
                    protected void showDlg(Boolean isEdit) {
                        isReStart = isEdit;
                        resetBtn.setEnabled(isEdit);
                        okBtn.setEnabled(isEdit);
                        show();
                    }
                };
            }
            taskJob.setTask(task);
            dialog.hide();
    
            if (CsVarUtil.eng2 != null) {
                CsVarUtil.eng2.doCall(CsMgrEnum.DB_TASK, new BaseOpBean(BaseOpEnum.INIT));
            }
            TaskComp.addTask(taskJob, this, isReStart, isAutoStart());
        } catch (Exception e) {
            Lg.error(AbsCfgDig.class, e);
            PopDlgUtil.error(CsVarUtil.parentFrame, e);
        }
    }

    protected JdbcBean getJdbc() {
        JdbcBean jdbc = new JdbcBean();
        
        DBTypeEnum type = DBTypeEnum.valueOf(typeInput.getDbTypeValue());
        DBTypeEnum.Version ver = null;
        String str = typeInput.getVersValue();
        if (StringUtils.isNotBlank(str) && EnumUtil.contain(DBTypeEnum.Version.class, str)) {
            ver = DBTypeEnum.Version.valueOf(str);
            jdbc.setCurrentVersion(ver);
        }
        
        jdbc.setClazz(DriverUtil.getDriverClass(type));
        jdbc.setDbUrl(urlInput.getValue().trim());
        jdbc.setPassword(passwordInput.getValue().trim());
        
        String inputUser = usetInput.getValue().trim();
        jdbc.setUser(jdbcEditable ? SqlStrUtil.rmQuote4Sel(inputUser, type,ver) : inputUser);

        String inputSchema = schemalInput.getValue().trim();
        jdbc.setSchema(jdbcEditable ? SqlStrUtil.rmQuote4Sel(inputSchema, type,ver) : inputSchema);
        return jdbc;
    }

    protected String getTaskName() {
        return taskNameInput.getValue().trim();
    }

    protected void resetForm() {
        taskNameInput.setValue("");
        typeInput.setDbTypeValue(Objects.requireNonNull(DriverUtil.getDbType(jdbc)).name());
        typeInput.setVersValue(Objects.requireNonNull(jdbc.getCurrentVersion()).name());
        urlInput.setValue(jdbc.getDbUrl());
        passwordInput.setValue(jdbcEditable ? "" : jdbc.getPassword());
        usetInput.setValue(jdbcEditable ? "" : jdbc.getUser());
        schemalInput.setValue(jdbcEditable ? "" : jdbc.getSchema());
    }


    protected boolean anyEmpty() {
        String str = null;
        if (StringUtils.isBlank(taskNameInput.getValue()))  str = LangMgr.lang(Word4.TASK_NAME);
        if (!typeInput.getDbTypeValue().equals(DBTypeEnum.redis.name())) {
            if (StringUtils.isBlank(usetInput.getValue())) str = LangMgr.lang(Word2.USERNAME);
            if (StringUtils.isBlank(passwordInput.getValue()))  str = LangMgr.lang(Word2.PASSWORD);
        }
        if (StringUtils.isBlank(urlInput.getValue())) str = LangMgr.lang(Word2.CONNECT);
        if (null != str) {
            PopDlgUtil.error(dialog, new Exception(str + LangMgr.sLang(CsL.CANNOT_BE_EMPTY)));
            return true;
        }
        return false;
    }

    protected void setSize(int height) {
        dialog.setSize(800, height + 60);
        ((JDialog) dialog.getWindow()).setResizable(true);
    }

}
