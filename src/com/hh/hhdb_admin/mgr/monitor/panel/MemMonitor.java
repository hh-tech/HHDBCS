package com.hh.hhdb_admin.mgr.monitor.panel;

import com.hh.frame.common.util.db.SqlQueryUtil;
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
import org.jfree.data.time.*;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.util.List;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;

/**
 * @author YuSai
 */
public class MemMonitor extends MonitorComp implements Runnable {

    private static final String LOG_NAME = MemMonitor.class.getSimpleName();

    private final HPanel panel = new HPanel();

    private final HPanel sysPanel = new HPanel();

    private final HPanel memPanel = new HPanel();

    private int refreshTime = 5;

    private final int refreshTime2 = 5 * 1000;

    private SelectBox refreshRate;

    private final SystemMonitorComp sysComp;

    private final Connection conn;
    //数据库新老版本标记
    private Boolean bool = null;

    private final HTable sysTable = new HTable();

    private final HTable memTable = new HTable();

    private boolean initCol = true;

    private static int memory = 0;

    private final TimeSeries timeSeries = new TimeSeries(getLang("memory"));

    private final List<Map<Second, Integer>> list = new ArrayList<>();

    private final int kd = 12;

    private ScheduledExecutorService executor;

    private List<Map<String, String>> sysValueList;

    public MemMonitor(SystemMonitorComp sysComp, Connection conn) {
        this.sysComp = sysComp;
        this.conn = conn;
        try {
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
            initPanel();
            //走势图
            TimeSeriesCollection dataset = new TimeSeriesCollection();
            dataset.addSeries(timeSeries);
            JFreeChart chart = ChartFactory.createTimeSeriesChart(getLang("memoryTrend"), getLang("systemTime"), getLang("memorySize"), dataset, true, true, false);
            configFont(chart);
            ChartPanel jp = new ChartPanel(chart);
            LastPanel lastPanel = new LastPanel();
            lastPanel.setHead(jp);
            HSplitPanel splitPanel = new HSplitPanel();
            splitPanel.setDividerLocation(730);
            sysPanel.setTitle(getLang("systemInfo"));
            splitPanel.setPanelOne(sysPanel);
            memPanel.setTitle(getLang("memoryInfo"));
            splitPanel.setPanelTwo(memPanel);
            lastPanel.set(splitPanel.getComp());
            panel.setLastPanel(lastPanel);
            initMemSeries();
        } catch (Exception e) {
            closeRunnable();
            e.printStackTrace();
            logUtil.error(LOG_NAME, e);
            JOptionPane.showMessageDialog(null, e.getMessage(), getLang("mistake"), JOptionPane.ERROR_MESSAGE);
        }
        executor = newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this, refreshTime2, refreshTime2, TimeUnit.MILLISECONDS);
    }

    public HPanel getPanel() {
        return panel;
    }

    private void initPanel() throws Exception {
        List<String> sysColumns = new ArrayList<>();
        sysValueList = new ArrayList<>();
        List<String> memColumns = new ArrayList<>();
        List<Map<String, String>> memValueList = new ArrayList<>();
        Map<String, Integer> data = new HashMap<>();
        if ("SSH".equals(sysComp.rInput.getValue())) {
            // 操作系统信息
            sysComp.scriptRunner.run(getLang("sys_util"), 0);
            MonitorUtil.getColumnAndData(false, sysComp.scriptRunner.getOutMsg(), sysColumns, sysValueList, null);
        } else {
            // 操作系统信息
            String sql = MonitorUtil.getOsInfo(true);
            List<Map<String, Object>> dbList = SqlQueryUtil.select(conn, sql);
            if (!StringUtils.isNotBlank(dbList.get(0).get("os_info") + "")) { //判断是否是新版本数据库不需要加后缀
                sql = MonitorUtil.getOsInfo(false);
                dbList = SqlQueryUtil.select(conn, sql);
                bool = false;
            } else {
                bool = true;
            }
            MonitorUtil.getColumnAndData(true, dbList.toString(), sysColumns, sysValueList, null);
        }
        getData(memColumns, memValueList, data);
        if (initCol) {
            sysTable.setRowHeight(25);
            for (String columnName : sysColumns) {
                sysTable.addCols(new DataCol(columnName, columnName));
            }
            LastPanel sysLast = new LastPanel(false);
            sysLast.setWithScroll(sysTable.getComp());
            sysPanel.setLastPanel(sysLast);
            memTable.setRowHeight(25);
            for (String columnName : memColumns) {
                memTable.addCols(new DataCol(columnName, columnName));
            }
            LastPanel memLast = new LastPanel(false);
            memLast.setWithScroll(memTable.getComp());
            memPanel.setLastPanel(memLast);
            initCol = false;
        }
        memory = Integer.parseInt(data.get("memTotal").toString());
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
        int value = memory;
        plot.getRangeAxis().setRange(0, value);
        NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
        numberAxis.setAutoTickUnitSelection(false);
        double unit = value >> 2;// 刻度的长
        NumberTickUnit ntu = new NumberTickUnit(unit);
        numberAxis.setTickUnit(ntu);
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

    private void initMemSeries() {
        timeSeries.clear();
        Calendar date = Calendar.getInstance();
        Day day = new Day(date.getTime());
        date.add(Calendar.SECOND, -refreshTime * kd);
        list.clear();
        for (int i = 0; i < kd; i++) {
            date.add(Calendar.SECOND, refreshTime);
            int ss = date.get(Calendar.SECOND);
            Second second = new Second(ss, new Minute(date.get(Calendar.MINUTE), new Hour(date.get(Calendar.HOUR_OF_DAY), day)));
            Map<Second, Integer> data = new HashMap<>();
            data.put(second, null);
            list.add(data);
        }
        for (Map<Second, Integer> data : list) {
            timeSeries.addOrUpdate(data.keySet().iterator().next(), 0);
        }
    }

    private void setTime(int refreshTime) {
        this.refreshTime = refreshTime;
        initMemSeries();
    }

    /**
     * 获取数据
     *
     * @param columnNames 列集合
     * @param columnValueList 数据集合
     * @param data 数据
     */
    private void getData(List<String> columnNames, List<Map<String, String>> columnValueList, Map<String, Integer> data) throws Exception {
        if ("SSH".equals(sysComp.rInput.getValue())) {  //判断方式
            sysComp.scriptRunner.run(getLang("mem_util"), 0);
            MonitorUtil.getColumnAndData(false, sysComp.scriptRunner.getOutMsg(), columnNames, columnValueList, data);
        } else {
            String sql = MonitorUtil.getMem(bool);
            List<Map<String, Object>> dbList = SqlQueryUtil.select(conn, sql);
            MonitorUtil.getColumnAndData(true, dbList.toString(), columnNames, columnValueList, data);
        }
    }

    @Override
    public void run() {
        if (executor.isShutdown()) {
            return;
        }
        Calendar date = Calendar.getInstance();
        Day day = new Day(date.getTime());
        Hour hour = new Hour(date.get(Calendar.HOUR_OF_DAY), day);
        Minute minute = new Minute(date.get(Calendar.MINUTE), hour);
        int second = date.get(Calendar.SECOND);
        try {
            List<String> columnNames = new ArrayList<>();
            List<Map<String, String>> columnValueList = new ArrayList<>();
            Map<String, Integer> data = new HashMap<>();
            // 获取实时内存信息
            getData(columnNames, columnValueList, data);
            memory = Integer.parseInt(data.get("memTotal").toString());
            synchronized (this) {
                sysTable.load(sysValueList, 1);
                memTable.load(columnValueList, 1);
            }
            int value = Integer.parseInt(data.get("userMem").toString());
            if (second % refreshTime != 0) {
                try {
                    int n = (refreshTime - second % refreshTime) * 1000;
                    Thread.sleep(n);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    logUtil.error(LOG_NAME, e);
                    JOptionPane.showMessageDialog(null, e.getMessage(), getLang("mistake"), JOptionPane.ERROR_MESSAGE);
                }
            }
            second = second + (refreshTime - second % refreshTime);
            Second key = new Second(second, minute);
            Map<Second, Integer> d = new HashMap<>();
            d.put(key, value);
            list.add(d);
            if (list.size() >= kd) {
                list.remove(0);
            }
            timeSeries.clear();
            for (Map<Second, Integer> dd : list) {
                timeSeries.addOrUpdate(dd.keySet().iterator().next(), dd.values().iterator().next());
            }
        } catch (Exception e) {
            closeRunnable();
            e.printStackTrace();
            logUtil.error(LOG_NAME, e);
            JOptionPane.showMessageDialog(null, e.getMessage(), getLang("mistake"), JOptionPane.ERROR_MESSAGE);
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
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

}
