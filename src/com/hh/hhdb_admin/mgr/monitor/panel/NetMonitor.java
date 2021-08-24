package com.hh.hhdb_admin.mgr.monitor.panel;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
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
 * 网络监控
 */
public class NetMonitor extends MonitorComp {

    private static final String LOG_NAME = NetMonitor.class.getSimpleName();

    private final HPanel panel = new HPanel();

    private final SystemMonitorComp sysComp;

    private final Connection conn;

    private final HTable netTable = new HTable();
    //数据库新老版本标记
    private Boolean bool = null;

    private boolean initCol = true;

    private List<Map<String, String>> columnValueList;

    public NetMonitor(SystemMonitorComp sysComp, Connection conn) {
        this.sysComp = sysComp;
        this.conn = conn;
        LastPanel lastPanel = new LastPanel(false);
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.CENTER);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton refresh = new HButton(getLang("refresh")) {
            @Override
            protected void onClick() {
                getData();
                netTable.load(columnValueList, 1);
            }
        };
        refresh.setIcon(getIcon("refresh"));
        barPanel.add(refresh);
        panel.add(barPanel);
        getData();
        lastPanel.setWithScroll(netTable.getComp());
        panel.setLastPanel(lastPanel);
        netTable.load(columnValueList, 1);
    }

    public HPanel getPanel() {
        return panel;
    }

    /**
     * 获取数据
     */
    private void getData() {
        try {
            List<String> columnNames = new ArrayList<>();
            columnValueList = new ArrayList<>();
            //判断方式
            if ("SSH".equals(sysComp.rInput.getValue())) {
                sysComp.scriptRunner.run(getLang("eth_util"), 0);
                MonitorUtil.getColumnAndData(false, sysComp.scriptRunner.getOutMsg(), columnNames, columnValueList, null);
            } else {
                String sql = MonitorUtil.getNetInfo(null == bool || bool);
                List<Map<String, Object>> dbList = SqlQueryUtil.select(conn, sql);
                //判断是否是新版本数据库不需要加后缀
                if (!StringUtils.isNotBlank(dbList.get(0).get("eth_list") + "")) {
                    sql = MonitorUtil.getNetInfo(false);
                    dbList = SqlQueryUtil.select(conn, sql);
                    bool = false;
                } else {
                    bool = true;
                }
                MonitorUtil.getColumnAndData(true, dbList.toString(), columnNames, columnValueList, null);
            }
            if (initCol) {
                netTable.setRowHeight(25);
                for (String columnName : columnNames) {
                    netTable.addCols(new DataCol(columnName, columnName));
                }
                initCol = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logUtil.error(LOG_NAME, e);
            JOptionPane.showMessageDialog(null, e.getMessage(), getLang("mistake"), JOptionPane.ERROR_MESSAGE);
        }
    }

}
