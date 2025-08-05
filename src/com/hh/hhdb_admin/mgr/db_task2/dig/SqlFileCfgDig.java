package com.hh.hhdb_admin.mgr.db_task2.dig;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.dbtask2.bean.BaseTaskBean;
import com.hh.frame.dbtask2.sql_files_imp.AbsPkgSqlFilesImp;
import com.hh.frame.dbtask2.sql_files_imp.CommitType;
import com.hh.frame.dbtask2.sql_files_imp.FileImpBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.fc.FileChooserInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.with_label.LabelConf;
import com.hh.frame.swingui.view.with_label.WithLabelInput;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public class SqlFileCfgDig extends AbsCfgDig {
    private static HPanel panel;
    private FileChooserInput sqlFileChoose;
    private SelectBox encodeSelectBox;
    private CheckBoxInput isErrExit;

    
    public SqlFileCfgDig(BaseTaskBean bean) {
        super(bean);
        dialog.setWindowTitle(LangMgr.sLang(CsL.ADD_SQL_FILE));
        initSqlFilePanel();
        setJdbcPanel();
        rootPanel.add(panel);
        rootPanel.add(getToolBar());
        dialog.setRootPanel(rootPanel);
        setSize(rootPanel.getHeight());
        setTaskName(bean.getName());
        if (bean instanceof FileImpBean) sqlFileChoose.setValue(((FileImpBean)bean).getFilePathList());
    }

    @Override
    protected void resetForm() {
        super.resetForm();
        encodeSelectBox.setValue("");
        sqlFileChoose.setValue("");
    }

    @Override
    protected boolean anyEmpty() {
        if (super.anyEmpty()) {
            return true;
        }
        String v = encodeSelectBox.getValue();
        if (StringUtils.isBlank(v)) {
            PopDlgUtil.info(dialog, LangMgr.sLang(CsL.FILE_NOT_EMPTY));
            return true;
        }
        return false;
    }

    private void initSqlFilePanel() {
        panel = new HPanel();
        panel.setTitle(LangMgr.sLang(CsL.SQL_FILE_CONFIG));
        panel.add(new HeightComp(10));
    
        LabelConf labelConf = new LabelConf(GridSplitEnum.C3, AlignEnum.RIGHT,AlignEnum.LEFT);
        sqlFileChoose = new FileChooserInput();
        sqlFileChoose.setBtnText(LangMgr.lang(Word2.CHOOSE));
        WithLabelInput dirPathLine = new WithLabelInput(labelConf,new LabelInput( LangMgr.sLang(CsL.DIR_PATH) + "*："), sqlFileChoose);
    
        encodeSelectBox = new SelectBox();
        encodeSelectBox.addOption(LangMgr.lang(Word4.AUTOMATIC_DECISION), LangMgr.lang(Word4.AUTOMATIC_DECISION));
        encodeSelectBox.addOption("UTF-8", "UTF-8");
        encodeSelectBox.addOption("GB18030", "GB18030");
        HPanel encodeLine = new HPanel(new HDivLayout(GridSplitEnum.C3, GridSplitEnum.C6));
        encodeLine.add(new LabelInput(LangMgr.lang(Word4.TEXT_ENCODING) + "*："), encodeSelectBox, new LabelInput(""));
    
        isErrExit = new CheckBoxInput("isErrExit",LangMgr.sLang(FrameL.ERROR_TERMINATION));
        isErrExit.setValue(true);
        WithLabelInput isErrExitLine = new WithLabelInput( labelConf,new LabelInput(""), isErrExit);
    
        panel.add(dirPathLine,encodeLine,isErrExitLine);
    }

    @Override
    protected void setTask() throws Exception {
        FileImpBean bean = new FileImpBean(0,getTaskName(),getJdbc());
        bean.setFilePathList(sqlFileChoose.getValue());
        bean.setCommitType(CommitType.PER_FILE);
        bean.setErrExit(isErrExit.isChecked());
        bean.setCharset(encodeSelectBox.getValue().equals(LangMgr.lang(Word4.AUTOMATIC_DECISION)) ? null : Charset.forName(encodeSelectBox.getValue()));
    
        task = new AbsPkgSqlFilesImp(bean) {
            @Override
            protected InputStream path2Stream(String path) throws Exception {
                return new FileInputStream(path);
            }

            @Override
            protected void taskDone() {
                System.out.println("任务结束");
            }
        };
    }

}
