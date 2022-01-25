package com.hh.hhdb_admin.mgr.monitor.linechart;

import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.StandardXYSeriesLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.*;
import org.jfree.ui.RectangleInsets;

import java.awt.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author YuSai
 */
public class LineChartPanel extends HPanel implements Runnable {

    private final String sql;

    private final Connection connection;

    private int section = 10;

    private final List<LineChart> seriesList = new ArrayList<>();

    private ScheduledExecutorService executor;

    private final TimeSeriesCollection dataSet = new TimeSeriesCollection();

    private List<TimeData> list = new ArrayList<>();

    public LineChartPanel(String sql, Connection connection, String title)throws Exception {
        this.sql = sql;
        this.connection = connection;
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "",
                "", dataSet, true, true, false);
        configFont(chart);
        ChartPanel chartPanel = new ChartPanel(chart);
        LastPanel lastPanel = new LastPanel();
        lastPanel.set(chartPanel);
        setLastPanel(lastPanel);
    }

    /**
     * 添加折线
     *
     * @param name name
     * @param value value
     */
    public void add(String name, String value) {
        LineChart series = new LineChart(name, value);
        seriesList.add(series);
        TimeSeries timeSeries = new TimeSeries(value);
        dataSet.addSeries(timeSeries);
    }

    /**
     * 设置横轴数
     *
     * @param section 横轴数
     */
    public void setSection(int section) {
        this.section = section;
    }

    private void configFont(JFreeChart chart)throws Exception {
        // 配置字体
        Font yFontY = new Font("宋体", Font.PLAIN, 12);// Y轴
        Font kFont = new Font("宋体", Font.PLAIN, 12);// 底部
        Font titleFont = new Font("宋体", Font.BOLD, 16); // 图片标题
        XYPlot plot = chart.getXYPlot();// 图形的绘制结构对象
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setTickUnit(new DateTickUnit(DateTickUnitType.SECOND, 1, new SimpleDateFormat("mm:ss")));
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
            renderer.setLegendItemToolTipGenerator(new StandardXYSeriesLabelGenerator("Tooltip {0}"));// 鼠标移到序列线上提示信息为“Tooltip + 序列线的名字”
        }
        NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
        numberAxis.setAutoTickUnitSelection(true);
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
        rangeAxis.setLabelFont(yFontY);
        rangeAxis.setLabelPaint(Color.BLUE); // 字体颜色
        rangeAxis.setTickLabelFont(yFontY);
    }

    /**
     * 调用执行
     */
    public void execute()throws Exception {
        run();
        executor = Executors.newScheduledThreadPool(seriesList.size());
        executor.scheduleAtFixedRate(this, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 调用中断
     */
    public void shutdown() {
        if (null != executor) {
            executor.shutdown();
        }
    }

    @Override
    public void run() {
        Calendar date = Calendar.getInstance();
        Day day = new Day(date.getTime());
        Hour hour = new Hour(date.get(Calendar.HOUR_OF_DAY), day);
        Minute minute = new Minute(date.get(Calendar.MINUTE), hour);
        int second = date.get(Calendar.SECOND);
        try {
            List<Map<String, Object>> maps = SqlQueryUtil.select(connection, sql);
            list.add(new TimeData(new Second(second, minute), maps.get(0)));
            if (list.size() >= this.section) {
                list = list.subList(list.size() - this.section, list.size());
            }
            for (int i = 0; i < dataSet.getSeriesCount(); i++) {
                dataSet.getSeries(i).clear();
            }
            for (TimeData d : list) {
                for (int i = 0; i < dataSet.getSeriesCount(); i++) {
                    TimeSeries timeSeries = dataSet.getSeries(i);
                    String name = seriesList.get(i).getName();
                    timeSeries.addOrUpdate(d.getSecond(), Double.parseDouble(d.getValue().get(name).toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (list.size() >= this.section) {
                list = list.subList(list.size() - this.section, list.size());
            }
            executor.shutdown();
        }
    }

}
