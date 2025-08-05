package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JobStatus;
import com.hh.frame.common.util.SleepUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.db_sql.ora.OraSqlUtil;
import com.hh.frame.db_sql.ora.info.OraSnapshotInfo;
import com.hh.frame.dbobj3.ora.awr.OraAwrBean;
import com.hh.frame.dbobj3.ora.awr.OraAwrGen;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.BigDecimalSpinner;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.fc.FileChooserInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.with_label.LabelConf;
import com.hh.frame.swingui.view.with_label.WithLabelInput;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Map;

/**
 * 性能报告
 */
public class PerformanceReportHandler extends AbsHandler {
    private HDialog dialog;
    private BigDecimalSpinner numInput;
    
    private String instNum;
    private String dbId;
    
    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        if (treeNode.getType() == TreeMrType.ROOT_SCHEMA_OR_DB) {
            SelectBox typeSelectBox = new SelectBox();
            typeSelectBox.addOption("html", "html");
            typeSelectBox.addOption("text", "text");
            
            numInput = new BigDecimalSpinner();
            numInput.setValue(30);
            
            TextInput beginInput = new TextInput("begin");
            TextInput endInput = new TextInput("end");
            
            FileChooserInput sqlFileChoose = new FileChooserInput();
            sqlFileChoose.setBtnText(LangMgr.lang(Word2.CHOOSE));
            sqlFileChoose.setDefaultName("awr.html");
            
            HDivLayout layout = new HDivLayout(0, 10, GridSplitEnum.C12);
            layout.setRightWidth(100);
            HPanel panel = new HPanel(layout);
            panel.add(new HeightComp(10));
            panel.add(getLabelInput(LangMgr.lang(Word4.REPORT_TYPE) + " *：", typeSelectBox));
            panel.add(getLabelInput(LangMgr.lang(Word4.QUERY_DAYS) + " *：", numInput));
            panel.add(getSearchPanel(LangMgr.lang(Word4.START_SNAPSHOT), beginInput), getSearchPanel(LangMgr.lang(Word4.END_SNAPSHOT), endInput));
            panel.add(getLabelInput(LangMgr.lang(Word4.SAVE_CONTENTS) + " *：", sqlFileChoose));
            panel.add(new HeightComp(5));
            
            dialog = new HDialog(CsVarUtil.parentFrame, 600, 300, true) {
                @Override
                protected void onConfirm() {
                    if (StringUtils.isBlank(beginInput.getValue()) || StringUtils.isBlank(endInput.getValue())) {
                        PopDlgUtil.info(dialog, "请选择开始/结束快照");
                        return;
                    }
                    if (StringUtils.isBlank(sqlFileChoose.getValue())) {
                        PopDlgUtil.info(dialog, "请选择保存路径");
                        return;
                    }
                    
                    OraAwrBean bean = new OraAwrBean();
                    bean.setDbId(dbId);
                    bean.setInstNum(instNum);
                    bean.setHtml(typeSelectBox.getValue().equals("html"));
                    bean.setBeginSnapId(beginInput.getValue());
                    bean.setEndSnapId(endInput.getValue());
                    
                    OraAwrGen gen = new OraAwrGen(CsVarUtil.conn, bean, new File(sqlFileChoose.getValue()));
                    gen.doGen();
                    while (gen.getStaus() == JobStatus.RUNNING) {
                        SleepUtil.sleep1000();
                    }
                    if (StringUtils.isBlank(gen.getErrorMsg())) {
                        PopDlgUtil.info(dialog, LangMgr.lang(Word4.EXECUTED_SUCCESSFULLY));
                        dialog.dispose();
                    } else {
                        PopDlgUtil.error(dialog, gen.getErrorMsg());
                    }
                }
            };
            dialog.setDisposeOnHide(false);
            dialog.removeCancleBtn();
            dialog.setWindowTitle("awr报告导出");
            dialog.setRootPanel(panel);
            dialog.show();
        }
    }
    
    private HPanel getSearchPanel(String txt, TextInput input) {
        HButton searchBtn = new HButton(LangMgr.lang(Word2.QUERY), FaSolidEnum.MAGNIFYING_GLASS_CHART) {
            @Override
            protected void onClick() {
                HDialog tDialog = new HDialog(dialog, 700, 500, true);
                tDialog.setWindowTitle(LangMgr.lang(Word2.CHOOSE));
                try {
                    HTable table = new HTable();
                    table.setCellEditable(false);
                    table.setSingleSelection(true);
                    table.getComp().addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (e.getClickCount() >= 2) {
                                Map<String, String> map = table.getSelectedRowBeans().get(0).getOldRow();
                                if (null != map) {
                                    dbId = map.get(OraSnapshotInfo.DBID.name().toLowerCase());
                                    instNum = map.get(OraSnapshotInfo.INST_NUM.name().toLowerCase());
                                    input.setValue(map.get(OraSnapshotInfo.SNAP_ID.name().toLowerCase()));
                                    tDialog.dispose();
                                }
                            }
                        }
                    });
                    table.addCols(new DataCol(OraSnapshotInfo.SNAP_ID.name().toLowerCase(), OraSnapshotInfo.SNAP_ID.name().toLowerCase()),
                            new DataCol(OraSnapshotInfo.INST_NAME.name().toLowerCase(), OraSnapshotInfo.INST_NAME.name().toLowerCase()));
                    table.addCols(new DataCol(OraSnapshotInfo.DB_NAME.name().toLowerCase(), OraSnapshotInfo.DB_NAME.name().toLowerCase()),
                            new DataCol(OraSnapshotInfo.INSTART_FMT.name().toLowerCase(), OraSnapshotInfo.INSTART_FMT.name().toLowerCase()));
                    table.addCols(new DataCol(OraSnapshotInfo.SNAPDAT.name().toLowerCase(), OraSnapshotInfo.SNAPDAT.name().toLowerCase()),
                            new DataCol(OraSnapshotInfo.LVL.name().toLowerCase(), OraSnapshotInfo.LVL.name().toLowerCase()));
                    table.addCols(new DataCol(OraSnapshotInfo.DBID.name().toLowerCase(), OraSnapshotInfo.DBID.name().toLowerCase()),
                            new DataCol(OraSnapshotInfo.INST_NUM.name().toLowerCase(), OraSnapshotInfo.INST_NUM.name().toLowerCase()));
                    
                    SearchToolBar toolBar = new SearchToolBar(table);
                    table.load(SqlQueryUtil.selectStrMapList(CsVarUtil.conn, OraSqlUtil.getSnapshotList(Integer.parseInt(numInput.getValue()), OraSnapshotInfo.class)), 1);
                    LastPanel lastPanel = new LastPanel();
                    lastPanel.setHead(toolBar.getComp());
                    lastPanel.setWithScroll(table.getComp());
                    tDialog.setRootPanel(lastPanel);
                    tDialog.setStatusBar(new HBarPanel());
                    tDialog.show();
                } catch (Exception e) {
                    GlobalWorkUtil.err(getClass(), e, dialog.getWindow());
                }
            }
        };
        
        HPanel panel = new HPanel(new HDivLayout(GridSplitEnum.C4, GridSplitEnum.C6));
        panel.add(new LabelInput(txt + " *："), input, searchBtn);
        return panel;
    }
    
    protected WithLabelInput getLabelInput(String label, AbsInput input) {
        LabelConf labelConf = new LabelConf(GridSplitEnum.C4, AlignEnum.RIGHT, AlignEnum.LEFT);
        return new WithLabelInput(labelConf, new LabelInput(label), input);
    }
}

