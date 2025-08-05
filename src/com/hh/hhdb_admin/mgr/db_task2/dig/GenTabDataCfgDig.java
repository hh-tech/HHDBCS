package com.hh.hhdb_admin.mgr.db_task2.dig;

import java.sql.Connection;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.dbobj3.TabObjUtil;
import com.hh.frame.dbobj3.util.DataUtil;
import com.hh.frame.dbtask2.bean.BaseTaskBean;
import com.hh.frame.dbtask2.gen_tab_data.GenTabDataBean;
import com.hh.frame.dbtask2.gen_tab_data.GenTabDataTask;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.util.VerifyUtil;
import com.hh.frame.swingui.view.with_label.LabelConf;
import com.hh.frame.swingui.view.with_label.WithLabelInput;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;


/**
 * 生成表格数据
 */
public class GenTabDataCfgDig extends AbsCfgDig {
    private HPanel panel;
    private final TextInput schemaInput = new TextInput();
    private final TextInput tableInput = new TextInput();
    private final TextInput dataRowInput = new TextInput();
    private final TextAreaInput templateInput = new TextAreaInput();
    
    public GenTabDataCfgDig(BaseTaskBean bean) {
        super(bean);
        templateInput.setEnabled(false);
        dataRowInput.setInputVerifier(VerifyUtil.getTextIntVerifier(LangMgr.sLang(CsL.DATA_ROW) + "*：", 1, 2147483647));
        templateInput.setRows(4);
        templateInput.setLineWrap(true);
        initPanel();
        setJdbcPanel();
        rootPanel.add(panel);
        rootPanel.add(getToolBar());
        dialog.setWindowTitle(LangMgr.sLang(CsL.TABLE_DATA_GENERATE));
        dialog.setRootPanel(rootPanel);
        setSize(rootPanel.getHeight());
        setTaskName(bean.getName());
        
        if (bean instanceof GenTabDataBean) {
            String schemaName = ((GenTabDataBean) bean).getSCHEMA_NAME();
            String tableName = ((GenTabDataBean) bean).getTABLE_NAME();
            schemaInput.setValue(schemaName);
            tableInput.setValue(tableName);
            if (StringUtils.isNotBlank(schemaName) && StringUtils.isNotBlank(tableName)) {
                templateInput.setValue(geneTemplate(schemaName, tableName));
            }
        }
    }
    
    @Override
    protected void resetForm() {
        super.resetForm();
        if (schemaInput.isEnabled()) {
            schemaInput.setValue("");
        }
        if (tableInput.isEnabled()) {
            tableInput.setValue("");
        }
        boolean isSetTemp = !tableInput.isEnabled() && !schemaInput.isEnabled();
        templateInput.setValue(isSetTemp ? geneTemplate(schemaInput.getValue(), tableInput.getValue()) : "");
        dataRowInput.setValue("10");
    }
    
    @Override
    protected boolean anyEmpty() {
        if (super.anyEmpty()) {
            return true;
        }
        if (StringUtils.isBlank(schemaInput.getValue())) {
            PopDlgUtil.info(dialog, LangMgr.sLang(CsL.SCHEMA_NOT_NULL));
            return true;
        }
        if (StringUtils.isBlank(tableInput.getValue())) {
            PopDlgUtil.info(dialog, LangMgr.sLang(CsL.TABLE_NOT_NULL));
            return true;
        }
        if (StringUtils.isBlank(dataRowInput.getValue())) {
            PopDlgUtil.info(dialog, LangMgr.sLang(CsL.ROW_NOT_NULL));
            return true;
        }
        if (StringUtils.isBlank(templateInput.getValue())) {
            PopDlgUtil.info(dialog, LangMgr.sLang(CsL.TEMPLATE_NOT_NULL));
            return true;
        }
        
        return false;
    }
    
    @Override
    protected void setTask() throws Exception {
        GenTabDataBean bean = new GenTabDataBean(0, getTaskName(), getJdbc());
        bean.setSCHEMA_NAME(schemaInput.getValue());
        bean.setTABLE_NAME(tableInput.getValue());
        bean.setINSERT_VM(templateInput.getValue());
        bean.setDATA_ROW(dataRowInput.getValue());
        task = new GenTabDataTask(bean);
    }
    
    private String geneTemplate(String schemaName, String tableName) {
        String res = "";
        if (StringUtils.isAnyBlank(schemaName, tableName)) {
            PopDlgUtil.info(dialog, LangMgr.sLang(CsL.SCHEMA_TABLE_FILLPARAM));
            return res;
        }
        Connection conn = null;
        try {
            conn = CsVarUtil.newConn(jdbc);
            Map<String, String> defMap = TabObjUtil.getTabObj(DriverUtil.getDbType(jdbc), jdbc.getCurrentVersion(), conn, schemaName, tableName).getColDefMap();
            Map<String, String> map = defMap.keySet().stream().collect(Collectors.toMap(String::trim,String::trim));
            res = StringUtils.replace(DataUtil.getInsertVm(map, DbCmdStrUtil.toDbCmdStr(schemaName, DriverUtil.getDbType(jdbc)), tableName, DriverUtil.getDbType(jdbc) == DBTypeEnum.tdsql4pg), "${ID}", "${__N}");
        } catch (Exception e) {
            Lg.error(GenTabDataCfgDig.class, e);
            PopDlgUtil.error(dialog, e);
        } finally {
            ConnUtil.close(conn);
        }
        return res;
    }
    
    private void initPanel() {
        LabelConf labelConf = new LabelConf(GridSplitEnum.C3, AlignEnum.RIGHT, AlignEnum.LEFT);
        panel = new HPanel();
        panel.setTitle(LangMgr.sLang(CsL.DATA_GENERATE_CONFIG));
        panel.add(new WithLabelInput(labelConf, new LabelInput(LangMgr.lang(Word2.SCHEMA) + "*："), schemaInput));
        panel.add(new WithLabelInput(labelConf, new LabelInput(LangMgr.lang(Word1.TABLE) + "*："), tableInput));
        panel.add(new WithLabelInput(labelConf, new LabelInput(LangMgr.sLang(CsL.DATA_ROW) + "*："), dataRowInput));
        HPanel templateLine = new HPanel(new HDivLayout(GridSplitEnum.C3, GridSplitEnum.C7));
        templateLine.add(new LabelInput(LangMgr.lang(Word4.DATA_TEMPLATE) + "*："), templateInput, new HButton(LangMgr.lang(Word2.MODIFY)) {
            @Override
            protected void onClick() {
                String template = geneTemplate(schemaInput.getValue(), tableInput.getValue());
                if (StringUtils.isBlank(template)) {
                    return;
                }
                
                new TempDlog(templateInput.getValue(), template, LangMgr.sLang(CsL.NOTE) + "set( ${__N} = ${__N}+ 100 )", dialog) {
                    public void save(String sql) {
                        templateInput.setValue(sql);
                    }
                };
            }
        });
        panel.add(templateLine);
    }
}
