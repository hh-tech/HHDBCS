package com.hh.hhdb_admin.mgr.monitor.panel;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.monitor.MonitorComp;
import com.hh.hhdb_admin.mgr.monitor.comp.SystemMonitorComp;
import com.hh.hhdb_admin.mgr.monitor.util.MonitorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author YuSai
 */
public class DiskMonitor extends MonitorComp {

    private static final String LOG_NAME = DiskMonitor.class.getSimpleName();

    private final HPanel panel = new HPanel();

    private final HPanel usagePanel = new HPanel();

    private final HPanel perfPanel = new HPanel();

    private final SystemMonitorComp sysComp;

    private final Connection conn;

    private final HTable usageTable = new HTable();

    private final HTable perfTable = new HTable();

    private boolean initCol = true;
    //数据库新老版本标记
    private Boolean bool = null;

    List<Map<String, String>> usageValueLists;

    List<Map<String, String>> perfValueLists;

    public DiskMonitor(SystemMonitorComp sysComp, Connection conn) {
        this.sysComp = sysComp;
        this.conn = conn;
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.CENTER);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton refresh = new HButton(getLang("refresh")) {
            @Override
            protected void onClick() {
                getData();
                usageTable.load(usageValueLists, 1);
                perfTable.load(perfValueLists, 1);
            }
        };
        refresh.setIcon(getIcon("refresh"));
        barPanel.add(refresh);
        panel.add(barPanel);
        getData();
        HSplitPanel hPanel = new HSplitPanel();
        hPanel.setDividerLocation(600);
        hPanel.setPanelOne(perfPanel);
        hPanel.setPanelTwo(usagePanel);
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.set(hPanel.getComp());
        panel.setLastPanel(lastPanel);
        usageTable.load(usageValueLists, 1);
        perfTable.load(perfValueLists, 1);
    }

    public HPanel getPanel() {
        return panel;
    }

    /**
     * 获取数据
     *
     */
    private void getData() {
        try {
            List<String> usageColumns = new ArrayList<>();
            usageValueLists = new ArrayList<>();
            List<String> perfColumns = new ArrayList<>();
            perfValueLists = new ArrayList<>();
            //判断方式
            if ("SSH".equals(sysComp.rInput.getValue())) {
                sysComp.scriptRunner.run(getLang("disk_util_usage"), 0);
                MonitorUtil.getColumnAndData(false, sysComp.scriptRunner.getOutMsg(), usageColumns, usageValueLists, null);
                sysComp.scriptRunner.run(getLang("disk_util_perf"), 0);
                MonitorUtil.getColumnAndData(false, sysComp.scriptRunner.getOutMsg(), perfColumns, perfValueLists, null);
            } else {
                String sql = MonitorUtil.getDiskUsage(null == bool || bool);
                java.util.List<Map<String, Object>> dbList = SqlQueryUtil.select(conn, sql);
                if (!StringUtils.isNotBlank(dbList.get(0).get("show_disk_usage") + "")) { //判断是否是新版本数据库不需要加后缀
                    sql = MonitorUtil.getDiskUsage(false);
                    dbList = SqlQueryUtil.select(conn, sql);
                    bool = false;
                } else {
                    bool = true;
                }
                // 硬盘详细信息
                MonitorUtil.getColumnAndData(true, dbList.toString(), usageColumns, usageValueLists, null);
                // 表格简单信息
                List<Map<String, Object>> dbList2 = SqlQueryUtil.select(conn, MonitorUtil.getDiskPerf(bool));
                MonitorUtil.getColumnAndData(true, dbList2.toString(), perfColumns, perfValueLists, null);
            }
            if (initCol) {
                usageTable.setRowHeight(25);
                for (String column : usageColumns) {
                    usageTable.addCols(new DataCol(column, column));
                }
                LastPanel usageLast = new LastPanel(false);
                usageLast.setWithScroll(perfTable.getComp());
                usagePanel.setLastPanel(usageLast);
                perfTable.setRowHeight(25);
                for (String column : perfColumns) {
                    perfTable.addCols(new DataCol(column, column));
                }
                LastPanel perfLast = new LastPanel(false);
                perfLast.setWithScroll(usageTable.getComp());
                perfPanel.setLastPanel(perfLast);
                initCol = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logUtil.error(LOG_NAME, e);
            JOptionPane.showMessageDialog(null, e.getMessage(), getLang("mistake"), JOptionPane.ERROR_MESSAGE);
        }
    }

}
