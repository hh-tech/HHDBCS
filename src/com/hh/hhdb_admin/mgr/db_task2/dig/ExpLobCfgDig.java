package com.hh.hhdb_admin.mgr.db_task2.dig;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.dbtask2.bean.BaseTaskBean;
import com.hh.frame.dbtask2.lob_exp.AbsLobExpTask;
import com.hh.frame.dbtask2.lob_exp.LobExpBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.fc.DirChooserInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.with_label.LabelConf;
import com.hh.frame.swingui.view.with_label.WithLabelInput;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class ExpLobCfgDig extends AbsCfgDig {
    private final TextAreaInput sqlInput = new TextAreaInput();
    private final TextInput lobNameInput = new TextInput();
    private final TextInput fileNameInput = new TextInput();
    private final DirChooserInput dirPath = new DirChooserInput("dirPath");
    
    
    public ExpLobCfgDig(BaseTaskBean bean) {
        super(bean);
        sqlInput.setRows(2);
        sqlInput.setLineWrap(true);
        dirPath.setBtnText(LangMgr.lang(Word2.CHOOSE));
        dialog.setWindowTitle(LangMgr.lang(Word4.EXPORT_CONFIGURATION));
        setJdbcPanel();
        rootPanel.add(initPanel());
        rootPanel.add(getToolBar());
        dialog.setRootPanel(rootPanel);
        setSize(rootPanel.getHeight() + 70);
        setTaskName(bean.getName());
    }
    
    @Override
    protected void resetForm() {
        super.resetForm();
        sqlInput.setValue("");
        lobNameInput.setValue("");
        fileNameInput.setValue("");
        dirPath.setValue("");
    }
    
    @Override
    protected void setTask() throws Exception {
        String sqlName = sqlInput.getValue();
        String lobName = lobNameInput.getValue();
        String fileName = fileNameInput.getValue();
        String url = dirPath.getValue();
        if (StringUtils.isBlank(sqlName)) {
            throw new Exception(LangMgr.sLang(CsL.SQL_NOT_NULL));
        }
        if (StringUtils.isBlank(lobName)) {
            throw new Exception(LangMgr.sLang(CsL.LOB_COLUMN_NAME_NOT_NULL));
        }
        if (StringUtils.isBlank(fileName)) {
            throw new Exception(LangMgr.sLang(CsL.FILE_COLUMN_NAME_NOT_NULL));
        }
        if (StringUtils.isBlank(url)) {
            throw new Exception(LangMgr.sLang(CsL.URL_NOT_NULL));
        }
    
    
        LobExpBean bean = new LobExpBean(0,getTaskName(),getJdbc());
        bean.setSql(sqlName);
        bean.setLobColName(lobName);
        bean.setLobColFileName(fileName);
        bean.setOutputDir(new File(url));
        task = new AbsLobExpTask(bean) {
            @Override
            protected void taskDone() {
                System.out.println("任务结束");
            }
        };
    }
    
    private HPanel initPanel() {
        LabelConf labelConf = new LabelConf(GridSplitEnum.C3, AlignEnum.RIGHT,AlignEnum.LEFT);
        HPanel panel = new HPanel();
        panel.setTitle(LangMgr.sLang(CsL.QUERY_DATA_EXPORT));
        WithLabelInput sqlLine = new WithLabelInput(labelConf,new LabelInput("sql*："), sqlInput);
        WithLabelInput lobNameLine = new WithLabelInput(labelConf,new LabelInput(LangMgr.sLang(CsL.LOB_COLUMN_NAME) + "*："), lobNameInput);
        WithLabelInput fileNameLine = new WithLabelInput(labelConf,new LabelInput(LangMgr.sLang(CsL.FILE_COLUMN_NAME) + "*："), fileNameInput);
        WithLabelInput dirPathLine = new WithLabelInput(labelConf,new LabelInput(LangMgr.lang(Word4.SAVE_CONTENTS) + "*："), dirPath);
        
        panel.add(sqlLine, lobNameLine, fileNameLine, dirPathLine);
        return panel;
    }
}
