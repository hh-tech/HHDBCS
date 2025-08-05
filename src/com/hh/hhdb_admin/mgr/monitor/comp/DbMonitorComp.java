package com.hh.hhdb_admin.mgr.monitor.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.monitor.AbsDbMonitor;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DbMonitorComp {
    private final HPanel panel = new HPanel();

    private final AbsDbMonitor dbMonitor;

    private Connection conn;

    private DbChartMonitorComp chartMonitorComp;

//    private SystemMonitorComp monitorComp;

    private HWindow window;
    
    private DBTypeEnum dbtype;
    private DBTypeEnum.Version ver;

    public DbMonitorComp(DBTypeEnum dbTypeEnum, DBTypeEnum.Version ver,Connection conn,  HWindow window) {
        this.conn = conn;
        this.dbtype=dbTypeEnum;
        this.ver=ver;
        this.window = window;
        dbMonitor = AbsDbMonitor.getDbMonitor(dbTypeEnum,ver,LangMgr.langType);
        if (null != dbMonitor) {
            LastPanel lastPanel = new LastPanel();
            panel.setLastPanel(lastPanel);

            if (dbTypeEnum == DBTypeEnum.oracle || dbTypeEnum == DBTypeEnum.gbase8a || dbTypeEnum == DBTypeEnum.redis || ver == DBTypeEnum.Version.tidb
                    || dbTypeEnum == DBTypeEnum.hana) {
                lastPanel.set(getConnMonitorPanel(dbMonitor.getColumns(true)).getComp());
            } else {
                HTabPanel tabPane = new HTabPanel();
//                    monitorComp = new SystemMonitorComp();
//                    tabPane.setPanel("sysMonitor", monitorComp.getPanel(), getHeader(LangMgr.lang(Word4.SYSTEM_MONITOR)));
                if (dbTypeEnum != DBTypeEnum.oceanbase && dbTypeEnum != DBTypeEnum.sybase )
                    tabPane.setPanel("lockMonitor", getLockMonitorPanel(dbMonitor.getLockColumns()), getHeader(LangMgr.lang(Word3.LOCK_MONITOR)));
                if (DBTypeEnum.hhdb == dbTypeEnum || DBTypeEnum.pgsql == dbTypeEnum) {
                    chartMonitorComp = new DbChartMonitorComp(conn, dbTypeEnum,ver);
                    tabPane.setPanel("chartMonitor", chartMonitorComp.getPanel(), getHeader(LangMgr.lang(Word4.CHART_MONITOR)));
                    tabPane.setPanel("transMonitor", getTransMonitorPanel(dbMonitor.getColumns(false)), getHeader(LangMgr.lang(Word4.TRANS_MONITOR)));
                }
                tabPane.setPanel("connMonitor", getConnMonitorPanel(dbMonitor.getColumns(true)), getHeader(LangMgr.lang(Word4.CONNECTION_MONITOR)));
                tabPane.selectPanel("connMonitor");

                lastPanel.set(tabPane.getComp());
            }
        }
    }

    public HPanel getPanel() {
        return panel;
    }

    public String getTitle() {
        return LangMgr.lang(Word4.MONITORING_MANAGEMENT);
    }

    public void show(HDialog dialog) {
        dialog.setRootPanel(panel);
        dialog.setWindowTitle(LangMgr.lang(Word4.MONITORING_MANAGEMENT));
        dialog.show();
    }

    public void closeRunnable() {
        if (null != chartMonitorComp) {
            chartMonitorComp.closeRunnable();
        }
//        if (null != monitorComp) {
//            monitorComp.closeRunnable();
//        }
        ConnUtil.close(conn);
    }

    private HPanel getLockMonitorPanel(LinkedHashMap<String, String> columnMaps) {
        HTable table = getTable(columnMaps);
        HPanel tablePanel = new HPanel();
        LastPanel lastPanel = new LastPanel(false);
        HButton refBtn = new HButton(LangMgr.lang(Word2.REFRESH)) {
            @Override
            protected void onClick() {
                if (dbMonitor != null) {
                    table.load(getLockData(), 1);
                }
            }
        };
        refBtn.setIcon(FaSolidEnum.ARROW_ROTATE_RIGHT, 16);
        SearchToolBar sToolbar = new SearchToolBar(table);
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        barPanel.add(refBtn);
        HPanel panel = new HPanel(new HDivLayout(GridSplitEnum.C6));
        panel.add(barPanel);
        panel.add(sToolbar);
        tablePanel.add(panel);
        lastPanel.setWithScroll(table.getComp());
        tablePanel.setLastPanel(lastPanel);
        table.load(getLockData(), 1);
        return tablePanel;
    }

    private HPanel getTransMonitorPanel(LinkedHashMap<String, String> columnMaps) {
        HTable table = getTable(columnMaps);
        HPanel tablePanel = new HPanel();
        LastPanel lastPanel = new LastPanel(false);
        HButton refBtn = new HButton(LangMgr.lang(Word2.REFRESH)) {
            @Override
            protected void onClick() {
                if (dbMonitor != null) {
                    table.load(getTransData(), 1);
                }
            }
        };
        refBtn.setIcon(FaSolidEnum.ARROW_ROTATE_RIGHT, 16);
        HButton delBtn = new HButton(LangMgr.lang(Word4.END_PROCESS)) {
            @Override
            protected void onClick() {
                List<HTabRowBean> rowBeans = table.getSelectedRowBeans();
                if (rowBeans.size() == 0) {
                    PopDlgUtil.info(window, LangMgr.sLang(CsL.PLEASE_SELECT_DATA));
                    return;
                }
                if (PopDlgUtil.confirm(window, LangMgr.sLang(CsL.IS_KILL))) {
                    try {
                        if (dbMonitor != null) {
                            dbMonitor.endProcess(conn, rowBeans.stream().map(HTabRowBean::getOldRow).collect(Collectors.toList()));
                            PopDlgUtil.info(window, LangMgr.lang(Word4.EXECUTED_SUCCESSFULLY));
                            table.load(getTransData(), 1);
                        }
                    } catch (SQLException e) {
                        Lg.error(DbMonitorComp.class, e);
                        PopDlgUtil.error(window, e);
                    }
                }
            }
        };
        delBtn.setIcon(FaSolidEnum.CIRCLE_XMARK, 16);
        SearchToolBar sToolbar = new SearchToolBar(table);
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        barPanel.add(refBtn);
        barPanel.add(delBtn);
        HPanel panel = new HPanel(new HDivLayout(GridSplitEnum.C6));
        panel.add(barPanel);
        panel.add(sToolbar);
        tablePanel.add(panel);
        lastPanel.setWithScroll(table.getComp());
        tablePanel.setLastPanel(lastPanel);
        table.load(getTransData(), 1);
        return tablePanel;
    }

    private HPanel getConnMonitorPanel(LinkedHashMap<String, String> columnMaps) {
        HTable table = getTable(columnMaps);
        HPanel tablePanel = new HPanel();
        LastPanel lastPanel = new LastPanel(false);
        HButton refBtn = new HButton(LangMgr.lang(Word2.REFRESH)) {
            @Override
            protected void onClick() {
                if (dbMonitor != null) {
                    table.load(getConnData(), 1);
                }
            }
        };
        refBtn.setIcon(FaSolidEnum.ARROW_ROTATE_RIGHT, 16);
        HButton delBtn = new HButton(LangMgr.lang(Word4.END_PROCESS)) {
            @Override
            protected void onClick() {
                List<HTabRowBean> rowBeans = table.getSelectedRowBeans();
                if (rowBeans.size() == 0) {
                    PopDlgUtil.info(window, LangMgr.sLang(CsL.PLEASE_SELECT_DATA));
                    return;
                }
                if (PopDlgUtil.confirm(window, LangMgr.sLang(CsL.IS_KILL))) {
                    try {
                        if (dbMonitor != null) {
                            dbMonitor.endProcess(conn, rowBeans.stream().map(HTabRowBean::getOldRow).collect(Collectors.toList()));
                            PopDlgUtil.info(window, LangMgr.lang(Word4.EXECUTED_SUCCESSFULLY));
                            table.load(getConnData(), 1);
                        }
                    } catch (SQLException e) {
                        Lg.error(DbMonitorComp.class, e);
                        PopDlgUtil.error(window, e);
                    }
                }
            }
        };
        delBtn.setIcon(FaSolidEnum.CIRCLE_XMARK, 16);
        SearchToolBar sToolbar = new SearchToolBar(table);
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        barPanel.add(refBtn);
        barPanel.add(delBtn);
        HPanel panel = new HPanel(new HDivLayout(GridSplitEnum.C6));
        panel.add(barPanel);
        panel.add(sToolbar);
        tablePanel.add(panel);
        lastPanel.setWithScroll(table.getComp());
        tablePanel.setLastPanel(lastPanel);
        table.load(getConnData(), 1);
        return tablePanel;
    }

    private HTable getTable(LinkedHashMap<String, String> columnMaps) {
        HTable table = new HTable();
        table.setRowHeight(25);
        columnMaps.forEach((key, value) -> table.addCols(new DataCol(key, value)));
        return table;
    }

    private List<Map<String, String>> getTransData() {
        try {
            resetConn();
            return dbMonitor.getTransData(conn);
        } catch (Exception e) {
            PopDlgUtil.error(window, e);
        }
        return new ArrayList<>();
    }

    private List<Map<String, String>> getLockData() {
        try {
            resetConn();
            return dbMonitor.getLockData(conn);
        } catch (Exception e) {
            PopDlgUtil.error(window, e);
        }
        return new ArrayList<>();
    }

    private List<Map<String, String>> getConnData() {
        try {
            resetConn();
            return dbMonitor.getData(conn);
        } catch (Exception e) {
            PopDlgUtil.error(window, e);
        }
        return new ArrayList<>();
    }

    private void resetConn() throws Exception {
        if (!ConnUtil.isConnected(dbtype,ver,conn)) {
            ConnUtil.close(conn);
            conn = CsVarUtil.newConn(CsVarUtil.jdbc);
        }
    }

    public HeaderConfig getHeader(String name) {
        HeaderConfig hc = new HeaderConfig(name);
        hc.setFixTab(true);
        hc.setTitleEditable(false);
        return hc;
    }

}
