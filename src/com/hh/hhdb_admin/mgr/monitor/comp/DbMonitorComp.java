package com.hh.hhdb_admin.mgr.monitor.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.monitor.AbsDbMonitor;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.monitor.MonitorComp;
import com.hh.hhdb_admin.mgr.monitor.util.MonitorUtil;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YuSai
 */
public class DbMonitorComp extends MonitorComp {

    private static final String LOG_NAME = DbMonitorComp.class.getSimpleName();

    private final HPanel panel = new HPanel();

    private final AbsDbMonitor dbMonitor;

    private final Connection conn;

    private final JdbcBean jdbcBean;

    private DbChartMonitorComp chartMonitorComp;

    public DbMonitorComp(Connection conn, JdbcBean jdbcBean, DBTypeEnum dbTypeEnum) {
        this.conn = conn;
        this.jdbcBean = jdbcBean;
        dbMonitor = AbsDbMonitor.getDbMonitor(dbTypeEnum);
        if (null != dbMonitor) {
            HTabPane tabPane = new HTabPane();
            tabPane.setCloseBtn(false);
            tabPane.addPanel("connMonitor", getLang("connMonitor"), initDbMonitor(true, true, dbMonitor.getColumns(true)).getComp(), false);
            if (MonitorUtil.isHhOrPg(dbTypeEnum)) {
                tabPane.addPanel("transMonitor", getLang("transMonitor"), initDbMonitor(false, true, dbMonitor.getColumns(false)).getComp(), false);
            }
            tabPane.addPanel("lockMonitor", getLang("lockMonitor"), initDbMonitor(true, false, dbMonitor.getLockColumns()).getComp(), false);
            if (MonitorUtil.isHhOrPg(dbTypeEnum)) {
                chartMonitorComp = new DbChartMonitorComp(conn, dbTypeEnum);
                tabPane.addPanel("chartMonitor", getLang("chartMonitor"), chartMonitorComp.getPanel().getComp(), false);
            }
            tabPane.selectPanel("connMonitor");
            LastPanel lastPanel = new LastPanel();
            lastPanel.set(tabPane.getComp());
            panel.setLastPanel(lastPanel);
        }

    }

    public HPanel getPanel() {
        return panel;
    }

    public String getTitle() {
        return getLang("dbMonitor");
    }

    public void show(HDialog dialog) {
        dialog.setRootPanel(panel);
        dialog.setWindowTitle(getLang("dbMonitor"));
        dialog.show();
    }

    private HPanel initDbMonitor(boolean bool, boolean flag, LinkedHashMap<String, String> columns) {
        HTable table = new HTable();
        table.setRowHeight(25);
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            table.addCols(new DataCol(entry.getKey(), getLang(entry.getValue())));
        }
        HPanel tablePanel = new HPanel();
        LastPanel lastPanel = new LastPanel(false);
        HButton refBtn = new HButton(getLang("refresh")) {
            @Override
            protected void onClick() {
                table.load(getData(bool, flag), 1);
            }
        };
        refBtn.setIcon(getIcon("refresh"));
        HButton delBtn = new HButton(getLang("endProcess")) {
            @Override
            protected void onClick() {
                List<HTabRowBean> rowBeans = table.getSelectedRowBeans();
                if (rowBeans.size() == 0) {
                    JOptionPane.showMessageDialog(null, getLang("pleaseSelectData"), getLang("hint"), JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int result = JOptionPane.showConfirmDialog(null, getLang("isKill"), getLang("hint"), JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        dbMonitor.endProcess(conn, rowBeans);
                        JOptionPane.showMessageDialog(null, getLang("executeSuccess"), getLang("hint"), JOptionPane.INFORMATION_MESSAGE);
                        table.load(getData(bool, flag),1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        logUtil.error(LOG_NAME, e);
                        PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
                    }
                }
            }
        };
        delBtn.setIcon(getIcon("end"));
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
        table.load(getData(bool, flag), 1);
        return tablePanel;
    }

    private List<Map<String, String>> getData(boolean bool, boolean flag) {
        if (flag) {
            return dbMonitor.getData(conn, jdbcBean, bool);
        } else {
            return dbMonitor.getLockData(conn);
        }
    }

    public void closeRunnable() {
        if (null != chartMonitorComp) {
            chartMonitorComp.closeRunnable();
        }
    }

}
