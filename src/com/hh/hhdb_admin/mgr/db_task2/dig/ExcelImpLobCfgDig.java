package com.hh.hhdb_admin.mgr.db_task2.dig;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.dbtask2.bean.BaseTaskBean;
import com.hh.frame.dbtask2.xls_lob_imp.AbsCsXlsImpLobTask;
import com.hh.frame.dbtask2.xls_lob_imp.XlsImpBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fileChooser.HFileChooser;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.RadioGroupInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.fc.FileChooserInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.with_label.LabelConf;
import com.hh.frame.swingui.view.with_label.WithLabelInput;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.io.InputStream;
import java.nio.charset.Charset;

public class ExcelImpLobCfgDig extends AbsCfgDig {
    private FileChooserInput sqlFileChoose;
    private SelectBox encodeSelectBox;
    private RadioGroupInput rInput;
    private final HPanel encodelateLine = new HPanel(new HDivLayout(GridSplitEnum.C3, GridSplitEnum.C7));
    
    
    public ExcelImpLobCfgDig(BaseTaskBean bean) {
        super(bean);
        dialog.setWindowTitle(LangMgr.sLang(CsL.IMPORT_LOB_DATA));
        setJdbcPanel();
        rootPanel.add(initPanel());
        rootPanel.add(getToolBar());
        dialog.setRootPanel(rootPanel);
        setSize(rootPanel.getHeight());
        setTaskName(bean.getName());
    }
    
    @Override
    protected void resetForm() {
        super.resetForm();
        if (null != encodeSelectBox) encodeSelectBox.setValue("");
        sqlFileChoose.setValue("");
        rInput.setSelected("blob");
    }
    
    @Override
    protected boolean anyEmpty() {
        if (super.anyEmpty()) {
            return true;
        }
        if (StringUtils.isBlank(sqlFileChoose.getValue())) {
            PopDlgUtil.info(dialog, LangMgr.sLang(CsL.FILE_NOT_EMPTY));
            return true;
        }
        return false;
    }
    
    private HPanel initPanel() {
        HPanel panel = new HPanel();
        panel.setTitle(LangMgr.sLang(CsL.EXCEL_FILE_CONFIG));
    
        sqlFileChoose = new FileChooserInput();
        sqlFileChoose.setBtnText(LangMgr.lang(Word2.CHOOSE));
        WithLabelInput dirPathLine = new WithLabelInput(new LabelConf(GridSplitEnum.C3, AlignEnum.RIGHT,AlignEnum.LEFT),new LabelInput(LangMgr.lang(Word4.TASK_TEMPLATE) + "*："), sqlFileChoose);
        rInput = new RadioGroupInput("type", new HPanel(new HDivLayout(GridSplitEnum.C6))) {
            @Override
            protected void stateChange(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED) return;
                if (((JRadioButton) e.getItem()).getName().equals("clob")) {
                    encodelateLine.removeAll();
                    encodeSelectBox = new SelectBox();
                    encodeSelectBox.addOption(LangMgr.lang(Word4.AUTOMATIC_DECISION), LangMgr.lang(Word4.AUTOMATIC_DECISION));
                    encodeSelectBox.addOption("UTF-8", "UTF-8");
                    encodeSelectBox.addOption("GB18030", "GB18030");
                    encodelateLine.add(new LabelInput(LangMgr.lang(Word4.TEXT_ENCODING) + "*："), encodeSelectBox);
                } else {
                    encodelateLine.removeAll();
                }
                encodelateLine.updateUI();
            }
        };
        rInput.setId("type");
        rInput.add("blob", "BLOB");
        rInput.add("clob", "CLOB");
        rInput.setSelected("blob");
        
        HPanel typelateLine = new HPanel(new HDivLayout(GridSplitEnum.C3, GridSplitEnum.C6, GridSplitEnum.C2));
        typelateLine.add(new LabelInput(LangMgr.lang(Word2.TYPE) + "*："), rInput, new HButton(LangMgr.lang(Word4.ACQUIRE_TEMPLATE)) {
            @Override
            protected void onClick() {
                InputStream is = null;
                try {
                    HFileChooser hfc = new HFileChooser(HFileChooser.FileType.BOTH);
                    hfc.addFilter("EXCL文件(*.xlsx)", "xlsx");
                    hfc.setDialogTitle(LangMgr.lang(Word2.SAVE));
                    hfc.setApproveButtonText(LangMgr.lang(Word2.SAVE));
                    hfc.setDefaultName("imp_lob_sample.xlsx");
                    if (hfc.showOpenDialog() == HFileChooser.APPROVE_OPTION) {
                        is = ExcelImpLobCfgDig.class.getResourceAsStream("imp_lob_sample.xlsx");
                        FileUtils.copyToFile(is, hfc.getSelectedFile());
                        PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.SAVED_SUCCESSFULLY));
                    }
                } catch (Exception e) {
                    Lg.error(ExcelImpLobCfgDig.class, e);
                    PopDlgUtil.error(dialog, e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        }, new LabelInput(""));
        
        panel.add(dirPathLine, typelateLine, encodelateLine);
        return panel;
    }
    
    @Override
    protected void setTask() throws Exception {
        XlsImpBean bean = new XlsImpBean(0,getTaskName(),getJdbc());
        bean.setPath(sqlFileChoose.getValue());
        bean.setCLob(rInput.getValue().equals("clob"));
        if (null != encodeSelectBox) {
            bean.setCharset(encodeSelectBox.getValue().equals(LangMgr.lang(Word4.AUTOMATIC_DECISION)) ? null : Charset.forName(encodeSelectBox.getValue()));
        }
    
        task = new AbsCsXlsImpLobTask(bean) {
            @Override
            protected void taskDone() {
                System.out.println("任务结束");
            }
        };
    }
}
