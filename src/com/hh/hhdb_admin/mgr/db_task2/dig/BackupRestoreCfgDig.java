package com.hh.hhdb_admin.mgr.db_task2.dig;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.dbtask2.bean.BaseTaskBean;
import com.hh.frame.dbtask2.schema.AbsSchemaExp;
import com.hh.frame.dbtask2.schema.AbsSchemaImp;
import com.hh.frame.dbtask2.schema.SchemaBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.fc.DirChooserInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.util.VerifyUtil;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * 模式备份/恢复
 */
public class BackupRestoreCfgDig extends AbsCfgDig {
    private static HPanel panel = null;
    private final DirChooserInput dirPath = new DirChooserInput("dirPath");
    private final TextInput threadNum = new TextInput("threadNum");
    private final CheckBoxInput isContinue = new CheckBoxInput("threadNum", LangMgr.sLang(CsL.IS_CONTINUE));
    private String title = "";
    private boolean isBackup = false;
    
    
    public BackupRestoreCfgDig(BaseTaskBean bean, boolean isBackup) {
        super(bean);
        this.isBackup = isBackup;
        dirPath.setBtnText(LangMgr.lang(Word2.CHOOSE));
        setJdbcPanel();
        boolean isDb = false;
        if (bean instanceof BackupRestoreBean) {
            isDb = ((BackupRestoreBean) bean).isDb;
            setSchema(((BackupRestoreBean) bean).schema);
//            dirPath.setValue(((BackupRestoreBean) bean).getOutputDir().getAbsolutePath());
//            threadNum.setValue(String.valueOf(((BackupRestoreBean) bean).getThreadNum()));
//            isContinue.setValue(((BackupRestoreBean) bean).isCleanMode());
        }
        if (isBackup) {
            this.title = isDb ? LangMgr.sLang(CsL.DB_BACKUP) : LangMgr.sLang(CsL.SCHEMA_BACKUP);
        } else {
            this.title = isDb ? LangMgr.sLang(CsL.DB_RESTORE) : LangMgr.sLang(CsL.SCHEMA_RESTORE);
        }
        setTaskName(bean.getName());
        threadNum.setInputVerifier(VerifyUtil.getTextIntVerifier(LangMgr.sLang(CsL.WORK_THREAD) + "*：", 1, 10));
        dialog.setWindowTitle(title);
        initPanel();
        rootPanel.add(panel);
        rootPanel.add(getToolBar());
        dialog.setRootPanel(rootPanel);
        setSize(rootPanel.getHeight());
    }
    
    @Override
    protected void setTask() throws Exception {
        String url = dirPath.getValue();
        if (StringUtils.isBlank(url)) {
            throw new Exception(LangMgr.sLang(CsL.URL_NOT_NULL));
        }
        BackupRestoreBean backupRestoreBean = new BackupRestoreBean(0,getTaskName(),getJdbc());
        backupRestoreBean.setThreadNum(Integer.parseInt(threadNum.getValue()));
        backupRestoreBean.setCleanMode(isContinue.isChecked());
        backupRestoreBean.setOutputDir(new File(url));
        if (isBackup) {
            task = new AbsSchemaExp(backupRestoreBean) {
                @Override
                protected void taskDone() {
                    System.out.println("任务结束");
                }
            };
        } else {
            task = new AbsSchemaImp(backupRestoreBean) {
                @Override
                protected void taskDone() {
                    System.out.println("任务结束");
                }
            };
        }
    }
    
    private void initPanel() {
        panel = new HPanel();
        panel.setTitle(title + LangMgr.lang(Word2.CONFIGURE));

        HGridPanel lineOne = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
        lineOne.setComp(1, new LabelInput(LangMgr.sLang(CsL.DIR_PATH) + "*："));
        lineOne.setComp(2, dirPath);
        File defaultDir = new File("task");
        if (!defaultDir.exists()) {
            defaultDir.mkdir();
        }
        dirPath.setValue(defaultDir.getAbsolutePath());

        HGridPanel lineTwo = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
        lineTwo.setComp(1, new LabelInput(LangMgr.sLang(CsL.WORK_THREAD) + "："));
        lineTwo.setComp(2, threadNum);
        threadNum.setValue("5");

        HGridPanel lineThree = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
        lineThree.setComp(1, new HPanel());
        lineThree.setComp(2, isContinue);
       // isContinue.setValue("true");

        panel.add(lineOne, lineTwo, lineThree);
    }
    
    
    public static class BackupRestoreBean extends SchemaBean {
        private boolean isDb = false;
        private String schema;
        
        public BackupRestoreBean(int id, String name, JdbcBean jdbc) {
            super(id, name, jdbc);
        }
    
        public void setisDb(boolean isDb) {
            this.isDb = isDb;
        }
    
        public void setSchema(String schema) {
            this.schema = schema;
        }
    }
}
