package com.hh.hhdb_admin.mgr.db_task2.dig;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.create_dbobj.obj_query.AbsObjQuery;
import com.hh.frame.create_dbobj.obj_query.ObjQueryUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbobj3.findobj.FindObjType;
import com.hh.frame.dbtask2.bean.BaseTaskBean;
import com.hh.frame.dbtask2.ddl_exp.AbsDDLExpTask;
import com.hh.frame.dbtask2.ddl_exp.DDLExptBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SearchSelectBox;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.fc.DirChooserInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;
import com.hh.frame.swingui.view.with_label.LabelConf;
import com.hh.frame.swingui.view.with_label.WithLabelInput;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.obj_query.comp.AbsQueryComp;
import com.hh.hhdb_admin.mgr.obj_query.comp.ObjQueryComp;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.sql.Connection;
import java.util.*;

public class DdlExpCfgDig extends AbsCfgDig {
    private TextInput keyWordInput = new TextInput();
    private DirChooserInput savePathChoose = new DirChooserInput();
    private SearchSelectBox schemaSelectBox;
    private SelectBox typeSelectBox = new SelectBox();
    private HButton querStopBut, selectBut;
    private HTable resTable;
    private LastPanel tablePanel = new LastPanel();
    
    private AbsObjQuery query;
    private SwingWorker<String, List<Map<String, String>>> worker;
    private Map<FindObjType,List<String>> selectName = new HashMap<>();
    private Map<FindObjType,List<String>> selectId = new HashMap<>();
    private List<TreeMrType> typeList = new LinkedList<>();
    
    private boolean isRunning,selectAll = false;
    
    public DdlExpCfgDig(BaseTaskBean bean) {
        super(bean);
        savePathChoose.setBtnText(LangMgr.lang(Word2.CHOOSE));
        setBox();
        setJdbcPanel();
        rootPanel.add(initPanel());
        rootPanel.add(getToolBar());
        dialog.setWindowTitle(LangMgr.sLang(CsL.OBJECT_EXPORT));
        dialog.setRootPanel(rootPanel);
        setSize(rootPanel.getHeight()+20);
        setTaskName(bean.getName());
        search();
    }
    
    @Override
    protected void resetForm() {
        super.resetForm();
        savePathChoose.setValue("");
        typeSelectBox.setValue(FindObjType.TABLE.name());
        keyWordInput.setValue("");
        resTable.load(new ArrayList<>(), 0);
    }
    
    @Override
    protected boolean anyEmpty() {
        if (super.anyEmpty()) {
            return true;
        }
        if (StringUtils.isBlank(savePathChoose.getValue())) {
            PopDlgUtil.info(dialog, LangMgr.sLang(CsL.FILE_NOT_EMPTY));
            return true;
        }
        if (resTable.getRowBeans(null).size() <= 0 ) {
            PopDlgUtil.info(dialog, LangMgr.sLang(CsL.RESULT_IS_NULL));
            return true;
        }
        selectName = new HashMap<>();
        selectId = new HashMap<>();
        for (HTabRowBean bean : resTable.getRowBeans(RowStatus.UPDATE)) {
            if (bean.getCurrRow().get("select").equalsIgnoreCase(Boolean.TRUE.toString())) {
                String ty = bean.getOldRow().get("type");
                if (typeList.contains(TreeMrType.valueOf(ty))) {
                    List<String> name = selectName.get(FindObjType.valueOf(ty)) == null ? new ArrayList<>() : selectName.get(FindObjType.valueOf(ty));
                    name.add(bean.getOldRow().get("name").trim());
                    selectName.put(FindObjType.valueOf(ty),name);
                    List<String> id = selectId.get(FindObjType.valueOf(ty)) == null ? new ArrayList<>() : selectId.get(FindObjType.valueOf(ty));
                    id.add(StringUtils.isNotBlank(bean.getOldRow().get("id")) ? bean.getOldRow().get("id") : "");
                    selectId.put(FindObjType.valueOf(ty),id);
                }
            }
        }
        if (selectName.size() <= 0 ) {
            PopDlgUtil.info(dialog, LangMgr.sLang(CsL.SELECT_EXPORT_OBJ));
            return true;
        }
        return false;
    }
    
    @Override
    protected void setTask() throws Exception {
        DDLExptBean ddlExptBean = new DDLExptBean(0, getTaskName(), jdbc);
        for (FindObjType findObjType : selectName.keySet()) {
            ddlExptBean.put(findObjType, selectName.get(findObjType),selectId.get(findObjType));
        }
        task = new AbsDDLExpTask(ddlExptBean) {
            @Override
            protected void taskDone() {
                System.out.println("任务结束");
            }
            @Override
            public void moveFile(File f) throws Exception {
                FileUtils.copyToDirectory(f,new File(savePathChoose.getValue()));
            }
        };
    }
    
    private HPanel initPanel() {
        LabelConf labelConf = new LabelConf(GridSplitEnum.C3, AlignEnum.RIGHT,AlignEnum.LEFT);
        HPanel panel = new HPanel();
        panel.setTitle(LangMgr.sLang(CsL.EXPORT_CONFIGURATION));
        panel.add(new WithLabelInput(labelConf, new LabelInput(LangMgr.sLang(CsL.DIR_PATH) + "*："), savePathChoose));
        panel.add(new WithLabelInput(labelConf, new LabelInput(LangMgr.lang(Word2.MODE) + "*："), schemaSelectBox));
        panel.add(new WithLabelInput(labelConf, new LabelInput(LangMgr.lang(Word2.TYPE) + "*："), typeSelectBox));
        
        HPanel typelateLine = new HPanel(new HDivLayout(GridSplitEnum.C3, GridSplitEnum.C6, GridSplitEnum.C2));
        typelateLine.add(new LabelInput(LangMgr.lang(Word3.KEYWORD) + "："), keyWordInput, querStopBut = new HButton(LangMgr.lang(Word2.QUERY),FaSolidEnum.MAGNIFYING_GLASS) {
            @Override
            protected void onClick() {
                if (isRunning) {
                    doStop();
                } else {
                    search();
                }
                selectBut.setText(LangMgr.lang(Word2.SELECT_ALL));
                selectAll = false;
            }
        }, selectBut = new HButton(LangMgr.lang(Word2.SELECT_ALL)) {
            @Override
            protected void onClick() {
                if (resTable.getRowCount() == 0) return;
                selectBut.setText(LangMgr.lang(selectAll ? Word2.SELECT_ALL:Word2.CANCEL));
                selectAll = !selectAll;
                new Thread(new SwingWorker<String, String>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        selectBut.setEnabled(false);
                        for (int i = 0; i < resTable.getRowCount(); i++) {
                            resTable.setValueAt(selectAll,i,resTable.getColumn("select").getColIndex()-1);
                        }
                        selectBut.setEnabled(true);
                        return "";
                    }
                }).start();
            }
        });
        panel.add(typelateLine);
    
        resTable = new HTable();
        resTable.setRowHeight(25);
        BoolCol boolCol = new BoolCol("select", LangMgr.lang(Word2.SELECT));
        boolCol.setWidth(200);
        DataCol idCol = new DataCol("id", "id");
        idCol.setShow(false);
        resTable.addCols(idCol,new DataCol("name", LangMgr.lang(Word2.NAME)),new DataCol("type", LangMgr.lang(Word2.TYPE)), boolCol);
        resTable.load(new ArrayList<>(), 0);
        tablePanel.setTitle(LangMgr.lang(Word2.RESULT));
        tablePanel.setWithScroll(resTable.getComp());
        panel.add(tablePanel);
        return panel;
    }
    
    /**
     * 查询
     */
    public void search() {
        try {
            isRunning = true;
            setStatus();
            jdbc = getJdbc();
            String keyWord = keyWordInput.getValue();
            if (StringUtils.isBlank(keyWord)) keyWord = "%";
            String schemaName = schemaSelectBox.getValue();
            if (StringUtils.isBlank(schemaName)) {
                PopDlgUtil.info(dialog, LangMgr.sLang(CsL.SELECT_SCHEMA));
                return;
            }
            
            if (worker != null) {
                if (query != null) query.stop();
                if (worker != null) worker.cancel(true);
                worker = null;
            }
            querStopBut.setIcon(FaSolidEnum.STOP,16);
            querStopBut.setText(LangMgr.lang(Word2.STOP));
            resTable.load(new ArrayList<>(), 0);
            jdbc.setSchema(schemaName);
//            jdbc.setCurSessionSchema(schemaName);
            if (typeSelectBox.getValue().equals("ALL")) {
                query = AbsObjQuery.getInstance( jdbc, schemaName, typeList, keyWord,jdbc.getCurrentVersion());
            } else {
                query = AbsObjQuery.getInstance( jdbc, schemaName, Arrays.asList(TreeMrType.valueOf(typeSelectBox.getValue())), keyWord,jdbc.getCurrentVersion());
            }
            query.setIgnoreCase(true);
            query.doQuery();
            worker = new AbsQueryComp.BackgroundTask(query) {
                @Override
                protected void process(List<List<Map<String, String>>> listList) {
                    boolean flag = listList.get(0).size() > 0 && listList.get(0).size() > resTable.getRowCount();
                    if (!flag) {
                        return;
                    }
                    for (List<Map<String, String>> maps : listList) {
                        resTable.load(maps, 0);
                    }
                }
                
                @Override
                protected void done() {
                    isRunning = false;
                    resTable.load(query.getResList(), 0);
                    querStopBut.setText(LangMgr.lang(Word2.QUERY));
                    querStopBut.setIcon(FaSolidEnum.MAGNIFYING_GLASS,16);
                    setStatus();
                    PopDlgUtil.info(dialog, LangMgr.lang(Word4.QUERY_COMPLETE));
                }
            };
            worker.execute();
        } catch (Exception e) {
            Lg.error(ObjQueryComp.class, e);
            PopDlgUtil.error(dialog,e);
        }
    }
    
    /**
     * 停止查询
     */
    protected void doStop() {
        if (query != null) {
            query.stop();
        }
        if (worker != null) {
            worker.cancel(true);
        }
        worker = null;
        isRunning = false;
        querStopBut.setText(LangMgr.lang(Word2.QUERY));
        querStopBut.setIcon(FaSolidEnum.MAGNIFYING_GLASS,16);
        setStatus();
    }
    
    private void setStatus(){
        if (isRunning) {
            resetBtn.setEnabled(false);
            okBtn.setEnabled(false);
            savePathChoose.setEnabled(false);
            schemaSelectBox.setEnabled(false);
            typeSelectBox.setEnabled(false);
            keyWordInput.setEnabled(false);
            selectBut.setEnabled(false);
            tablePanel.setTitle("查询中......");
        } else {
            resetBtn.setEnabled(true);
            okBtn.setEnabled(true);
            savePathChoose.setEnabled(true);
            schemaSelectBox.setEnabled(true);
            typeSelectBox.setEnabled(true);
            keyWordInput.setEnabled(true);
            selectBut.setEnabled(true);
            tablePanel.setTitle(LangMgr.lang(Word2.RESULT));
        }
    }
    
    private void setBox(){
        typeSelectBox.getComp().addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (null != resetBtn) search();
                }
            }
        });
        typeSelectBox.addOption("ALL","ALL");
        List<TreeMrType> type = ObjQueryUtil.getFullTypeList(DriverUtil.getDbType(jdbc));
        for (FindObjType value : FindObjType.values()) {
            if (value != FindObjType.INDEX && value != FindObjType.JAVA_SOURCE && value != FindObjType.JAVA_CLASS && value != FindObjType.RULE) {
                for (TreeMrType treeMrType : type) {
                    if (treeMrType.name().equals(value.name())) {
                        typeSelectBox.addOption(value.name(),value.name());
                        typeList.add(treeMrType);
                    }
                }
            }
        }
    
        schemaSelectBox = new SearchSelectBox() {
            @Override
            protected void onValueChanged(String value) {
                if (null != resetBtn) search();
            }
        };
        Connection conn = null;
        try {
            conn = CsVarUtil.newConn(jdbc);
            List<String> list = SqlWinUtil.getSchemaNameList(DriverUtil.getDbType(jdbc), jdbc.getCurrentVersion(), conn);
            for (String name : list) {
                schemaSelectBox.addOption(name, name);
            }
    
            String currSchema;
            if(StringUtils.isNoneBlank(jdbc.getSchema())) {
                currSchema= SqlStrUtil.rmQuote4Sel(jdbc.getSchema(), DriverUtil.getDbType(jdbc), jdbc.getCurrentVersion());
            }else {
                currSchema=SqlWinUtil.getCurrSchema(DriverUtil.getDbType(jdbc), jdbc.getCurrentVersion(), conn);
            }
            schemaSelectBox.setValue(currSchema);
        } catch (Exception e) {
            Lg.error(ObjQueryComp.class, e);
        } finally {
            ConnUtil.close(conn);
        }
    }
    
}
