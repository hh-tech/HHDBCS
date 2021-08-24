package com.hh.hhdb_admin.mgr.monitor.comp;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.monitor.AbsDbMonitor;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.hhdb_admin.mgr.monitor.MonitorComp;
import com.hh.hhdb_admin.mgr.monitor.linechart.LineChartPanel;

import java.sql.Connection;

/**
 * @author YuSai
 */
public class DbChartMonitorComp extends MonitorComp {

    private final HPanel panel = new HPanel();

    private LineChartPanel sessionChart;

    private LineChartPanel transChart;

    private LineChartPanel writeChart;

    private LineChartPanel readChart;

    private LineChartPanel ioChart;

    public DbChartMonitorComp(Connection conn, DBTypeEnum dbTypeEnum) {
        AbsDbMonitor chartMonitor = AbsDbMonitor.getDbMonitor(dbTypeEnum);
        if (null != chartMonitor) {
            HSplitPanel topPanel = new HSplitPanel(true);
            String dbSessionSql = chartMonitor.getQuerySessionSql();
            sessionChart = new LineChartPanel(dbSessionSql, conn, getLang("dbSession"));
            sessionChart.add("active", getLang("active"));
            sessionChart.add("idle", getLang("idle"));
            sessionChart.add("total", getLang("total"));
            sessionChart.setSection(16);
            sessionChart.execute();
            String transactionSql = chartMonitor.getQueryTransactionSql();
            transChart = new LineChartPanel(transactionSql, conn, getLang("transPerSec"));
            transChart.add("commits", getLang("commits"));
            transChart.add("rollbacks", getLang("rollbacks"));
            transChart.add("transactions", getLang("transactions"));
            transChart.setSection(16);
            transChart.execute();
            topPanel.setPanelOne(sessionChart);
            topPanel.setPanelTwo(transChart);
            topPanel.setDividerLocation(750);

            HSplitPanel botPanel = new HSplitPanel();
            String writeSql = chartMonitor.getQueryWriteSql();
            writeChart = new LineChartPanel(writeSql, conn, getLang("writeTuple"));
            writeChart.add("inserts", getLang("inserts"));
            writeChart.add("updates", getLang("updates"));
            writeChart.add("deletes", getLang("deletes"));
            writeChart.setSection(10);
            writeChart.execute();

            botPanel.setDividerLocation(500);
            botPanel.setPanelOne(writeChart);

            HSplitPanel botPanel2 = new HSplitPanel();
            String readSql = chartMonitor.getQueryReadSql();
            readChart = new LineChartPanel(readSql, conn, getLang("readTuple"));
            readChart.add("fetched", getLang("fetched"));
            readChart.add("returned", getLang("returned"));
            readChart.setSection(10);
            readChart.execute();
            String ioSql = chartMonitor.getQueryIoSql();
            ioChart = new LineChartPanel(ioSql, conn, getLang("modularIo"));
            ioChart.add("reads", getLang("reads"));
            ioChart.add("hits", getLang("hits"));
            ioChart.setSection(10);
            ioChart.execute();

            botPanel2.setDividerLocation(500);
            botPanel2.setPanelOne(readChart);
            botPanel2.setPanelTwo(ioChart);
            botPanel.setPanelTwo(botPanel2);

            HSplitPanel mainPanel = new HSplitPanel(false);
            mainPanel.setDividerLocation(400);
            mainPanel.setPanelOne(topPanel);
            mainPanel.setPanelTwo(botPanel);

            LastPanel lastPanel = new LastPanel();
            lastPanel.set(mainPanel.getComp());
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

    public void closeRunnable() {
        if (null != sessionChart) {
            sessionChart.shutdown();
        }
        if (null != transChart) {
            transChart.shutdown();
        }
        if (null != writeChart) {
            writeChart.shutdown();
        }
        if (null != readChart) {
            readChart.shutdown();
        }
        if (null != ioChart) {
            ioChart.shutdown();
        }
    }

}
