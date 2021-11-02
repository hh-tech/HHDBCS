package com.hh.hhdb_admin.mgr.monitor.panel;

import static java.util.concurrent.Executors.newScheduledThreadPool;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYSeriesLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.lang.LangEnum;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.monitor.MonitorComp;
import com.hh.hhdb_admin.mgr.monitor.comp.SystemMonitorComp;
import com.hh.hhdb_admin.mgr.monitor.util.MonitorUtil;

/**
 * @author YuSai
 */
public class CpuMonitor extends MonitorComp implements Runnable {

    private static final String LOG_NAME = CpuMonitor.class.getSimpleName();

    private final HPanel panel = new HPanel();

    private final HPanel cpuPanel = new HPanel();

    private final HPanel cpuSumPanel = new HPanel();

    private int refreshTime = 5;

    private final int refreshTime2 = 5 * 1000;

    private final SelectBox refreshRate;

    private final SystemMonitorComp sysComp;

    private final Connection conn;
    //数据库新老版本标记
    private Boolean bool = null;

    private final HTable cpuTable = new HTable();

    private final HTable cpuSumTable = new HTable();

    private boolean initCol = true;

    private final TimeSeries userUsageSeries = new TimeSeries(getLang("userUsage"));

    private final TimeSeries systemUsageSeries = new TimeSeries(getLang("systemUsage"));

    private final TimeSeries idleSeries = new TimeSeries(getLang("idleRate"));

    private static final String USER_USAGE_RATE = LangMgr2.getDefaultLang().equals(LangEnum.EN) ? "USER_USAGE_RATE" : "用户使用率";

    private static final String SYS_USAGE_RATE = LangMgr2.getDefaultLang().equals(LangEnum.EN) ? "SYS_USAGE_RATE" : "系统使用率";

    private static final String IDLE_RATE = LangMgr2.getDefaultLang().equals(LangEnum.EN) ? "IDLE_RATE" : "空闲率";

    private final List<Map<Second, Map<String, String>>> list = new ArrayList<>();

    private final int kd = 12;

    private ScheduledExecutorService executor = newScheduledThreadPool(1);

    private List<Map<String, String>> cpuValueList;

    public CpuMonitor(SystemMonitorComp sysComp, Connection conn) {
        this.sysComp = sysComp;
        this.conn = conn;
        HPanel ratePanel = new HPanel(new HDivLayout(GridSplitEnum.C4, GridSplitEnum.C2, GridSplitEnum.C1));
        LabelInput labelInput = new LabelInput("", getLang("refreshRate"));
        refreshRate = new SelectBox("refreshRate") {
            @Override
            public void onItemChange(ItemEvent e) {
                runGenerateChart();
            }
        };
        refreshRate.addOption("5", "5");
        refreshRate.addOption("10", "10");
        refreshRate.addOption("30", "30");
        ratePanel.add(new LabelInput());
        ratePanel.add(labelInput);
        ratePanel.add(refreshRate);
        panel.add(ratePanel);
        // 初始化走势图
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(userUsageSeries);
        dataset.addSeries(systemUsageSeries);
        dataset.addSeries(idleSeries);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(getLang("cpuTrend"), getLang("systemTime"), getLang("cpuPercentage"), dataset, true, true, false);
        configFont(chart);
        ChartPanel jp = new ChartPanel(chart);
        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(jp);
        // 初始化下方列表
        initTabPanel();
        HSplitPanel splitPanel = new HSplitPanel();
        splitPanel.setDividerLocation(550);
        cpuPanel.setTitle(getLang("cpuInfo"));
        splitPanel.setPanelOne(cpuPanel);
        cpuSumPanel.setTitle(getLang("cpuConcise"));
        splitPanel.setPanelTwo(cpuSumPanel);
        lastPanel.set(splitPanel.getComp());
        panel.setLastPanel(lastPanel);
        initCpuSeries();
        executor.scheduleAtFixedRate(this, refreshTime2, refreshTime2, TimeUnit.MILLISECONDS);
    }

    public HPanel getPanel() {
        return panel;
    }

    private void initTabPanel() {
        try {
            //CPU信息
            List<String> cpuColumns = new ArrayList<>();
            cpuValueList = new ArrayList<>();
            if ("SSH".equals(sysComp.rInput.getValue())) {
                sysComp.scriptRunner.run(getLang("cpu_info"), 0);
                MonitorUtil.getColumnAndData(false, sysComp.scriptRunner.getOutMsg(), cpuColumns, cpuValueList, null);
            } else {
                String sql = MonitorUtil.getCpuInfo(true);
                List<Map<String, Object>> lists = SqlQueryUtil.select(conn, sql);
                if (!StringUtils.isNotBlank(lists.get(0).get("cpu_info") + "")) { //判断是否是新版本数据库不需要加后缀
                    sql = MonitorUtil.getCpuInfo(false);
                    lists = SqlQueryUtil.select(conn, sql);
                    bool = false;
                } else {
                    bool = true;
                }
                MonitorUtil.getColumnAndData(true, lists.toString(), cpuColumns, cpuValueList, null);
            }
            List<String> columnNames = new ArrayList<>();
            List<Map<String, String>> columnValueList = new ArrayList<>();
            getData(columnNames, columnValueList);
            if (initCol) {
                cpuTable.setRowHeight(25);
                for (String columnName : cpuColumns) {
                    cpuTable.addCols(new DataCol(columnName, columnName));
                }
                LastPanel cpuLast = new LastPanel(false);
                cpuLast.setWithScroll(cpuTable.getComp());
                cpuPanel.setLastPanel(cpuLast);
                cpuSumTable.setRowHeight(25);
                for (String columnName : columnNames) {
                    cpuSumTable.addCols(new DataCol(columnName, columnName));
                }
                LastPanel cpuSumLast = new LastPanel(false);
                cpuSumLast.setWithScroll(cpuSumTable.getComp());
                cpuSumPanel.setLastPanel(cpuSumLast);
                initCol = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logUtil.error(LOG_NAME, e);
            JOptionPane.showMessageDialog(null, e.getMessage(), getLang("mistake"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configFont(JFreeChart chart) {
        // 配置字体
        Font yFont = new Font("宋体", Font.PLAIN, 12);// Y轴
        Font kFont = new Font("宋体", Font.PLAIN, 12);// 底部
        Font titleFont = new Font("宋体", Font.BOLD, 16); // 图片标题
        XYPlot plot = chart.getXYPlot();// 图形的绘制结构对象
        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            // 数据点样式设置
            renderer.setBaseShapesVisible(true); // 数据点显示外框
            renderer.setBaseShapesFilled(true); // 数据点外框内是否填充
            renderer.setSeriesFillPaint(0, Color.BLUE); // 第一条序列线上数据点外框内填充颜色为橘黄色
            renderer.setUseFillPaint(true); // 如果要在数据点外框内填充自定义的颜色，这个标志位必须为真
            // 序列线样式设置
            renderer.setSeriesPaint(0, Color.GREEN); // 设置第一条序列线为绿色
            renderer.setLegendItemToolTipGenerator(new StandardXYSeriesLabelGenerator("Tooltip {0}"));// 鼠标移到序列线上提示信息为“Tooltip+序列线的名字”
        }
        plot.getRangeAxis().setRange(0, 100);
        NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
        numberAxis.setAutoTickUnitSelection(false);
        numberAxis.setTickUnit(new NumberTickUnit(20));
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) plot.getRenderer();
        plot.setDomainGridlinesVisible(true);// 显示横坐标格线
        plot.setRangeGridlinesVisible(true);// 显示纵坐标格线
        // 设置网格背景颜色
        plot.setBackgroundPaint(Color.white);
        // 设置网格竖线颜色
        plot.setDomainGridlinePaint(Color.pink);
        // 设置网格横线颜色
        plot.setRangeGridlinePaint(Color.pink);
        // 设置曲线图与xy轴的距离
        plot.setAxisOffset(new RectangleInsets(0D, 0D, 0D, 5D));
        // 设置曲线是否显示数据点
        xylineandshaperenderer.setBaseShapesVisible(true);
        // 图片标题
        chart.setTitle(new TextTitle(chart.getTitle().getText(), titleFont));
        // 横轴框里的标题字体
        chart.getLegend().setItemFont(kFont);
        // 横轴列表字体
        plot.getDomainAxis().setTickLabelFont(kFont);
        // 横轴小标题字体
        plot.getDomainAxis().setLabelFont(kFont);
        // Y 轴
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelFont(yFont);
        rangeAxis.setLabelPaint(Color.BLUE); // 字体颜色
        rangeAxis.setTickLabelFont(yFont);
    }

    private void initCpuSeries() {
        userUsageSeries.clear();
        systemUsageSeries.clear();
        idleSeries.clear();
        list.clear();
        Calendar date = Calendar.getInstance();
        Day day = new Day(date.getTime());
        date.add(Calendar.SECOND, -refreshTime * kd);
        Map<String, String> init = new HashMap<>();
        init.put(USER_USAGE_RATE, "0");
        init.put(SYS_USAGE_RATE, "0");
        init.put(IDLE_RATE, "0");
        for (int i = 0; i < kd; i++) {
            date.add(Calendar.SECOND, refreshTime);
            int ss = date.get(Calendar.SECOND);
            Second key = new Second(ss, new Minute(date.get(Calendar.MINUTE), new Hour(date.get(Calendar.HOUR_OF_DAY), day)));
            Map<Second, Map<String, String>> data = new HashMap<>();
            data.put(key, init);
            list.add(data);
        }
        for (Map<Second, Map<String, String>> data : list) {
            Second second = data.keySet().iterator().next();
            userUsageSeries.addOrUpdate(second, 0);
            systemUsageSeries.addOrUpdate(second, 0);
            idleSeries.addOrUpdate(second, 0);
        }
    }

    private void setTime(int refreshTime) {
        this.refreshTime = refreshTime;
        initCpuSeries();
    }

    private void getData(List<String> columnNames, List<Map<String, String>> columnValueList) {
        try {
            //CPU实时信息
            if ("SSH".equals(sysComp.rInput.getValue())) {
                sysComp.scriptRunner.run(getLang("cpu_util"), 0);
                MonitorUtil.getColumnAndData(false, sysComp.scriptRunner.getOutMsg(), columnNames, columnValueList, null);
            } else {
                String sql = MonitorUtil.getCpuPerf(bool);
                List<Map<String, Object>> lists = SqlQueryUtil.select(conn, sql);
                MonitorUtil.getColumnAndData(true, lists.toString(), columnNames, columnValueList, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logUtil.error(LOG_NAME, e);
            JOptionPane.showMessageDialog(null, e.getMessage(), getLang("mistake"), JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void run() {
        if (executor.isShutdown()) {
            return;
        }
        List<String> columnNames = new ArrayList<>();
        List<Map<String, String>> columnValueList = new ArrayList<>();
        Calendar date = Calendar.getInstance();
        Day day = new Day(date.getTime());
        Hour hour = new Hour(date.get(Calendar.HOUR_OF_DAY), day);
        Minute minute = new Minute(date.get(Calendar.MINUTE), hour);
        int second = date.get(Calendar.SECOND);
        getData(columnNames, columnValueList);
        synchronized (this) {
            cpuTable.load(cpuValueList, 1);
            cpuSumTable.load(columnValueList, 1);
        }
        if (second % refreshTime != 0) {
            try {
                int n = (refreshTime - second % refreshTime) * 1000;
                Thread.sleep(n);
            } catch (InterruptedException e) {
                e.printStackTrace();
                logUtil.error(LOG_NAME, e);
            }
        }
        second = second + (refreshTime - second % refreshTime);
        if (columnValueList.size() > 0) {
            Map<String, String> seriesValue = columnValueList.get(0);
            Second key = new Second(second, minute);
            Map<Second, Map<String, String>> data = new HashMap<>();
            data.put(key, seriesValue);
            list.add(data);
            if (list.size() >= kd) {
                list.remove(0);
            }
            userUsageSeries.clear();
            systemUsageSeries.clear();
            idleSeries.clear();
            for (Map<Second, Map<String, String>> dd : list) {
                Second sc = dd.keySet().iterator().next();
                Map<String, String> d = dd.values().iterator().next();
                userUsageSeries.addOrUpdate(sc, Double.parseDouble(d.get(USER_USAGE_RATE)));
                systemUsageSeries.addOrUpdate(sc, Double.parseDouble(d.get(SYS_USAGE_RATE)));
                idleSeries.addOrUpdate(sc, Double.parseDouble(d.get(IDLE_RATE)));
            }
        }
    }

    private void runGenerateChart() {
        closeRunnable();
        refreshTime = Integer.parseInt(refreshRate.getValue());
        executor = newScheduledThreadPool(1);
        setTime(refreshTime);
        executor.scheduleAtFixedRate(this, refreshTime2, refreshTime2, TimeUnit.MILLISECONDS);
    }

    public void closeRunnable() {
        if (null != executor && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

}
