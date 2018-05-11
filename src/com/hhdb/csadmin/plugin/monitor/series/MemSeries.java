package com.hhdb.csadmin.plugin.monitor.series;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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

import com.hh.frame.common.log.LM;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.bean.SqlBean;
import com.hhdb.csadmin.common.dao.ConnService;
import com.hhdb.csadmin.common.util.HHSqlUtil;
import com.hhdb.csadmin.common.util.HHSqlUtil.ITEM_TYPE;
import com.hhdb.csadmin.plugin.monitor.DBMonitor;
import com.hhdb.csadmin.plugin.monitor.ui.MonitorTablePanel;
import com.hhdb.csadmin.plugin.monitor.util.SeriesUtil;

/**
 * CPU实时监控折线图
 * 
 */
public class MemSeries extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	private TimeSeries timeSeries = new TimeSeries("内存");
	private List<Map<Second, Integer>> list = new ArrayList<Map<Second, Integer>>();
	private static int memery = 0;
	private int kd = 12;
	private int reflashTime;
	private Connection hc;
	private MonitorTablePanel memjyPanel = new MonitorTablePanel(true);
	private MonitorTablePanel memPanel = new MonitorTablePanel(true);

	private ServerBean serverbean;

	public MemSeries(int reflashTime, DBMonitor dbMonitorp) {
		CmdEvent getsbEvent = new CmdEvent("com.hhdb.csadmin.plugin.monitor",
				"com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent revent = dbMonitorp.sendEvent(getsbEvent);
		serverbean = (ServerBean) revent.getObj();

		this.reflashTime = reflashTime;
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(timeSeries);

		try {

			// 获取操作系统信息
			SqlBean sqlBean = HHSqlUtil.getSqlBean(ITEM_TYPE.DBSERVER,
					"os_info");
			hc = ConnService.createConnection(serverbean);
			List<Map<String,Object>> dblist = SqlQueryUtil.select(hc, sqlBean.getSql());

			List<String> columnNames = new ArrayList<String>();
			List<Map<String, Object>> columnValueList = new ArrayList<Map<String, Object>>();

			SeriesUtil.getServerValueMap(dblist, columnNames,
					columnValueList);
			memPanel.setDataList(columnNames, columnValueList);
			// 获取实时内存信息
			sqlBean = HHSqlUtil.getSqlBean(ITEM_TYPE.DBSERVER, "show_mem");

			dblist = SqlQueryUtil.select(hc, sqlBean.getSql());
			Map<String, Integer> data = new HashMap<String, Integer>();
			columnNames = new ArrayList<String>();
			columnValueList = new ArrayList<Map<String, Object>>();
			SeriesUtil.getServerShowMemMap(dblist, columnNames,
					columnValueList, data);
			memery = Integer.parseInt(data.get("memTotal").toString());
			memjyPanel.setDataList(columnNames, columnValueList);

			memjyPanel.setPreferredSize(new Dimension(450, 200));
			memPanel.setPreferredSize(new Dimension(450, 200));
			memjyPanel.getViewport().setBackground(Color.WHITE);
			memPanel.getViewport().setBackground(Color.WHITE);

			Font font = new Font("SumSan", Font.BOLD, 13);
			JFreeChart chart = ChartFactory.createTimeSeriesChart("实时内存监控走势图",
					"系统时间", "内存大小(单位M)", dataset, true, true, false);
			configFont(chart);
			ChartPanel jp = new ChartPanel(chart);

			JLabel cpuinfo = new JLabel("获取操作系统信息");
			cpuinfo.setFont(font);
			JLabel cpujyinfo = new JLabel("获取实时内存信息");
			cpujyinfo.setFont(font);
			add(jp, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
					new Insets(10, 0, 0, 0), 0, 0));
			add(cpuinfo, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					new Insets(10, 10, 0, 0), 0, 0));
			add(cpujyinfo, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					new Insets(10, 10, 0, 0), 0, 0));
			add(memPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					new Insets(10, 10, 0, 0), 0, 0));
			add(memjyPanel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					new Insets(10, 10, 0, 0), 0, 0));

			initMemSeries();
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(this, e.getMessage());
		}

	}

	private void initMemSeries() {
		timeSeries.clear();
		Calendar date = Calendar.getInstance();
		Day day = new Day(date.getTime());
		date.add(Calendar.SECOND, -reflashTime * kd);
		list.clear();
		for (int i = 0; i < kd; i++) {
			date.add(Calendar.SECOND, reflashTime);
			int ss = date.get(Calendar.SECOND);
			Second second = new Second(ss, new Minute(
					date.get(Calendar.MINUTE), new Hour(
							date.get(Calendar.HOUR_OF_DAY), day)));
			Map<Second, Integer> data = new HashMap<Second, Integer>();
			data.put(second, null);
			list.add(data);
		}
		for (Map<Second, Integer> data : list) {
			timeSeries.addOrUpdate(data.keySet().iterator().next(), 0);
		}
	}

	public void setTime(int reflashTime) {
		this.reflashTime = reflashTime;
		memjyPanel.getTableDataModel().setRowCount(0);
		initMemSeries();
	}

	@Override
	public void run() {
		Calendar date = Calendar.getInstance();
		Day day = new Day(date.getTime());
		Hour hour = new Hour(date.get(Calendar.HOUR_OF_DAY), day);
		Minute minute = new Minute(date.get(Calendar.MINUTE), hour);
		int second = date.get(Calendar.SECOND);

		try {
			// 获取实时内存信息
			SqlBean sqlBean = HHSqlUtil.getSqlBean(ITEM_TYPE.DBSERVER,
					"show_mem");

			List<Map<String,Object>> dblist = SqlQueryUtil.select(hc, sqlBean.getSql());

			List<String> columnNames = new ArrayList<String>();
			List<Map<String, Object>> columnValueList = new ArrayList<Map<String, Object>>();
			Map<String, Integer> data = new HashMap<String, Integer>();
			SeriesUtil.getServerShowMemMap(dblist, columnNames,
					columnValueList, data);
			memery = Integer.parseInt(data.get("memTotal").toString());

			memjyPanel.getTableDataModel().setRowCount(0);
			memjyPanel.setDataList(columnNames, columnValueList);

			int value = Integer.parseInt(data.get("userMem").toString());
			if (second % reflashTime != 0) {
				try {
					Thread.sleep((reflashTime - second % reflashTime) * 1000);
				} catch (InterruptedException e) {
					LM.error(LM.Model.CS.name(), e);
				}
			}
			second = second + (reflashTime - second % reflashTime);

			Second key = new Second(second, minute);
			Map<Second, Integer> d = new HashMap<Second, Integer>();
			d.put(key, value);
			list.add(d);

			if (list.size() >= kd) {
				list.remove(0);
			}
			timeSeries.clear();
			for (Map<Second, Integer> dd : list) {
				timeSeries.addOrUpdate(dd.keySet().iterator().next(), dd
						.values().iterator().next());
			}
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(this, e.getMessage());
		}

	}

	private static void configFont(JFreeChart chart) {
		// 配置字体
		Font yfont = new Font("宋体", Font.PLAIN, 12);// Y轴
		Font kfont = new Font("宋体", Font.PLAIN, 12);// 底部
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
			renderer.setLegendItemToolTipGenerator(new StandardXYSeriesLabelGenerator(
					"Tooltip {0}"));// 鼠标移到序列线上提示信息为“Toolop
									// +
									// 序列线的名字”
		}

		int value = memery;
		plot.getRangeAxis().setRange(0, value);
		NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
		numberAxis.setAutoTickUnitSelection(false);
		double unit = value / 4;// 刻度的长
		NumberTickUnit ntu = new NumberTickUnit(unit);
		numberAxis.setTickUnit(ntu);
		XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) plot
				.getRenderer();
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
		chart.getLegend().setItemFont(kfont);
		// 横轴列表字体
		plot.getDomainAxis().setTickLabelFont(kfont);
		// 横轴小标题字体
		plot.getDomainAxis().setLabelFont(kfont);
		// Y 轴
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setLabelFont(yfont);
		rangeAxis.setLabelPaint(Color.BLUE); // 字体颜色
		rangeAxis.setTickLabelFont(yfont);
	}

	public void closeConnection() {
		if (this.hc != null) {
			try {
				if (!hc.isClosed()) {
					hc.close();
				}
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				//JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
	}
}
