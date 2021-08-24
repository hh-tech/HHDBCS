package com.hh.hhdb_admin.mgr.monitor.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.monitor.AbsDbMonitor;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
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

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author YuSai
 */
public class DbLockMonitorComp extends MonitorComp {

    private static final String LOG_NAME = DbLockMonitorComp.class.getSimpleName();

    private final HPanel panel = new HPanel();

    private final Connection conn;

    private final AbsDbMonitor dbMonitor;

    public DbLockMonitorComp(Connection conn, DBTypeEnum dbTypeEnum) {
        this.conn = conn;
        dbMonitor = AbsDbMonitor.getDbMonitor(dbTypeEnum);
        if (null != dbMonitor) {
            HTable table = new HTable();
            table.setRowHeight(25);
            for (Map.Entry<String, String> entry : dbMonitor.getLockColumns().entrySet()) {
                table.addCols(new DataCol(entry.getKey(), getLang(entry.getValue())));
            }
            HButton refBtn = new HButton(getLang("refresh")) {
                @Override
                protected void onClick() {
                    table.load(getData(), 1);
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
                            table.load(getData(), 1);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            logUtil.error(LOG_NAME, e);
                            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
                        }
                    }
                }
            };
            delBtn.setIcon(getIcon("end"));
            HBarLayout barLayout = new HBarLayout();
            barLayout.setAlign(AlignEnum.LEFT);
            HBarPanel barPanel = new HBarPanel(barLayout);
            barPanel.add(refBtn);
            barPanel.add(delBtn);
            SearchToolBar sToolbar = new SearchToolBar(table);
            HPanel panel1 = new HPanel(new HDivLayout(GridSplitEnum.C6));
            panel1.add(barPanel);
            panel1.add(sToolbar);
            panel.add(panel1);
            LastPanel lastPanel = new LastPanel(false);
            lastPanel.setWithScroll(table.getComp());
            table.load(getData(), 1);
            panel.setLastPanel(lastPanel);
        }

    }

    public HPanel getPanel() {
        return panel;
    }

    public String getTitle() {
        return getLang("dbLockMonitor");
    }

    public void show(HDialog dialog) {
        dialog.setRootPanel(panel);
        dialog.setWindowTitle(getLang("dbLockMonitor"));
        dialog.show();
    }

    private List<Map<String, String>> getData() {
        return dbMonitor.getLockData(conn);
    }
}
