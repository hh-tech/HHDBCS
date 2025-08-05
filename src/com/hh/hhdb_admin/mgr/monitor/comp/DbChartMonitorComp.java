package com.hh.hhdb_admin.mgr.monitor.comp;

import com.hh.frame.chart.option.SeriesOption;
import com.hh.frame.chart.option.em.ChartType;
import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.monitor.AbsDbMonitor;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.mgr.monitor.base.LineChartPanel;

import java.sql.Connection;

/**
 * @author YuSai
 */
public class DbChartMonitorComp {
    private final HPanel panel = new HPanel();

    private LineChartPanel sessionChart;

    private LineChartPanel transChart;

    private LineChartPanel writeChart;

    private LineChartPanel readChart;

    private LineChartPanel ioChart;

    public DbChartMonitorComp(Connection conn, DBTypeEnum dbTypeEnum, DBTypeEnum.Version ver) {
        AbsDbMonitor chartMonitor = AbsDbMonitor.getDbMonitor(dbTypeEnum,ver,LangMgr.langType);
        try {
            if (null != chartMonitor) {
                String dbSessionSql = chartMonitor.getQuerySessionSql();
                sessionChart = new LineChartPanel(dbSessionSql, conn, LangMgr.sLang(CsL.DB_SESSION));
                sessionChart.addSeriesOption("active", new SeriesOption(LangMgr.lang(Word2.ACTIVE), ChartType.line));
                sessionChart.addSeriesOption("idle", new SeriesOption(LangMgr.lang(Word2.LEISURE), ChartType.line));
                sessionChart.addSeriesOption("total", new SeriesOption(LangMgr.lang(Word2.TOTAL), ChartType.line));
                sessionChart.setSection(10);
                sessionChart.execute();

                String transactionSql = chartMonitor.getQueryTransactionSql();
                transChart = new LineChartPanel(transactionSql, conn, LangMgr.lang(Word3.TRANSACTIONS));
                transChart.addSeriesOption("commits", new SeriesOption(LangMgr.lang(Word3.COMMITS), ChartType.line));
                transChart.addSeriesOption("rollbacks", new SeriesOption(LangMgr.lang(Word3.ROLLBACKS), ChartType.line));
                transChart.addSeriesOption("transactions", new SeriesOption(LangMgr.lang(Word3.TRANSACTIONS), ChartType.line));
                transChart.setSection(10);
                transChart.execute();

                String writeSql = chartMonitor.getQueryWriteSql();
                writeChart = new LineChartPanel(writeSql, conn, LangMgr.lang(Word4.WRITE_TUPLE));
                writeChart.addSeriesOption("inserts", new SeriesOption(LangMgr.lang(Word2.INSERTS), ChartType.line));
                writeChart.addSeriesOption("updates", new SeriesOption(LangMgr.lang(Word2.MODIFY), ChartType.line));
                writeChart.addSeriesOption("deletes", new SeriesOption(LangMgr.lang(Word2.DELETE), ChartType.line));
                writeChart.setSection(8);
                writeChart.execute();

                String readSql = chartMonitor.getQueryReadSql();
                readChart = new LineChartPanel(readSql, conn, LangMgr.lang(Word4.READ_TUPLE));
                readChart.addSeriesOption("fetched", new SeriesOption(LangMgr.lang(Word2.OBTAIN), ChartType.line));
                readChart.addSeriesOption("returned", new SeriesOption(LangMgr.lang(Word2.RETURN), ChartType.line));
                readChart.setSection(8);
                readChart.execute();

                String ioSql = chartMonitor.getQueryIoSql();
                ioChart = new LineChartPanel(ioSql, conn, LangMgr.sLang(CsL.MODULAR_IO));
                ioChart.addSeriesOption("reads", new SeriesOption(LangMgr.lang(Word2.READ), ChartType.line));
                ioChart.addSeriesOption("hits", new SeriesOption(LangMgr.lang(Word2.MATCHING), ChartType.line));
                ioChart.setSection(8);
                ioChart.execute();

                HSplitPanel topPanel = new HSplitPanel(true);
                topPanel.setSplitWeight(0.5);
                topPanel.setPanelOne(sessionChart);
                topPanel.setPanelTwo(transChart);

                HSplitPanel botPanel = new HSplitPanel();
                botPanel.setSplitWeight(0.33);
                botPanel.setPanelOne(writeChart);

                HSplitPanel botPanel2 = new HSplitPanel();
                botPanel2.setSplitWeight(0.5);
                botPanel2.setPanelOne(readChart);
                botPanel2.setPanelTwo(ioChart);
                botPanel.setPanelTwo(botPanel2);

                HSplitPanel mainPanel = new HSplitPanel(false);
                mainPanel.setSplitWeight(0.5);
                mainPanel.setPanelOne(topPanel);
                mainPanel.setPanelTwo(botPanel);

                LastPanel lastPanel = new LastPanel();
                lastPanel.set(mainPanel.getComp());
                panel.setLastPanel(lastPanel);
            }
        } catch (Exception e) {
            HGridLayout gridLayout = new HGridLayout();
            gridLayout.setTopHeight(100);
            HGridPanel hp = new HGridPanel(gridLayout);
            hp.setComp(1, new LabelInput(LangMgr.sLang(CsL.DATA_ERROR), AlignEnum.CENTER));
            panel.add(hp);
            Lg.error(DbChartMonitorComp.class, e);
            closeRunnable();
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
