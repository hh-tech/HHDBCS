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
 */
public class CpuSeries extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	private TimeSeries userssSeries = new TimeSeries("用户使用率");
	private TimeSeries sysuseSeries = new TimeSeries("系统使用率");
	private TimeSeries idleSeries = new TimeSeries("空闲率");
	private List<Map<Second, Map<String, Object>>> list = new ArrayList<Map<Second, Map<String, Object>>>();
	private int kd = 12;
	private int reflashTime;
	private Connection hc;

	private MonitorTablePanel cpujyPanel = new MonitorTablePanel(true);
	private MonitorTablePanel cpuPanel = new MonitorTablePanel(true);

	private ServerBean serverbean;

	public CpuSeries(int reflashTime, DBMonitor monitor) {
		this.reflashTime = reflashTime;
		CmdEvent getsbEvent = new CmdEvent("com.hhdb.csadmin.plugin.monitor",
				"com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent revent = monitor.sendEvent(getsbEvent);
		serverbean = (ServerBean) revent.getObj();

		setLayout(new GridBagLayout());
		setBackground(Color.white);
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(userssSeries);
		dataset.addSeries(sysuseSeries);
		dataset.addSeries(idleSeries);
		JFreeChart chart = ChartFactory.createTimeSeriesChart("CPU实时监控走势图",
				"系统时间", "CPU百分比", dataset, true, true, false);
		configFont(chart, this.reflashTime);
		ChartPanel jp = new ChartPanel(chart);
		try {
			hc = ConnService.createConnection(serverbean);
			SqlBean sqlBean = HHSqlUtil.getSqlBean(ITEM_TYPE.DBSERVER,
					"cpu_info");
			List<Map<String,Object>> dblist = SqlQueryUtil.select(hc, sqlBean.getSql());
			List<String> columnNames = new ArrayList<String>();
			List<Map<String, Object>> columnValueList = new ArrayList<Map<String, Object>>();
			SeriesUtil.getServerValueMap(dblist, columnNames,
					columnValueList);

			cpuPanel.setDataList(columnNames, columnValueList);
			cpuPanel.setPreferredSize(new Dimension(450, 200));
			cpuPanel.getViewport().setBackground(Color.WHITE);

			cpujyPanel.setPreferredSize(new Dimension(450, 200));
			cpujyPanel.getViewport().setBackground(Color.WHITE);

			Font font = new Font("SumSan", Font.BOLD, 13);
			JLabel cpuinfo = new JLabel("CPU信息");
			cpuinfo.setFont(font);
			JLabel cpujyinfo = new JLabel("CPU简要信息");
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
			add(cpuPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					new Insets(10, 10, 0, 0), 0, 0));
			add(cpujyPanel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					new Insets(10, 10, 0, 0), 0, 0));
			initCpuSeries();
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);

			String fromID = monitor.getClass().getPackage().getName();
			String toID = "com.hhdb.csadmin.plugin.tabpane";
			HHEvent removePanelEvent = new HHEvent(fromID, toID,
					"RemovePanelEvent");
			removePanelEvent.addProp("COMPONENT_ID", "CPUMinitor");
			monitor.sendEvent(removePanelEvent);

			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	private void initCpuSeries() {
		userssSeries.clear();
		sysuseSeries.clear();
		idleSeries.clear();
		list.clear();
		Calendar date = Calendar.getInstance();
		Day day = new Day(date.getTime());
		date.add(Calendar.SECOND, -reflashTime * kd);
		Map<String, Object> init = new HashMap<String, Object>();
		init.put("用户使用率", "0");
		init.put("系统使用率", "0");
		init.put("空闲率", "0");
		for (int i = 0; i < kd; i++) {
			date.add(Calendar.SECOND, reflashTime);
			int ss = date.get(Calendar.SECOND);
			Second key = new Second(ss, new Minute(date.get(Calendar.MINUTE),
					new Hour(date.get(Calendar.HOUR_OF_DAY), day)));
			Map<Second, Map<String, Object>> data = new HashMap<Second, Map<String, Object>>();
			data.put(key, init);
			list.add(data);
		}
		for (Map<Second, Map<String, Object>> data : list) {
			Second second = data.keySet().iterator().next();
			userssSeries.addOrUpdate(second, 0);
			sysuseSeries.addOrUpdate(second, 0);
			idleSeries.addOrUpdate(second, 0);
		}
	}

	public void setTime(int reflashTime) {
		this.reflashTime = reflashTime;
		cpujyPanel.getTableDataModel().setRowCount(0);
		initCpuSeries();
	}

	@Override
	public void run() {
		try {
			Calendar date = Calendar.getInstance();
			Day day = new Day(date.getTime());
			Hour hour = new Hour(date.get(Calendar.HOUR_OF_DAY), day);
			Minute minute = new Minute(date.get(Calendar.MINUTE), hour);
			int second = date.get(Calendar.SECOND);
			SqlBean sqlBean = HHSqlUtil.getSqlBean(ITEM_TYPE.DBSERVER,
					"cpu_perf");

			List<Map<String,Object>> dblist = SqlQueryUtil.select(hc, sqlBean.getSql());

			List<String> columnNames = new ArrayList<String>();
			List<Map<String, Object>> columnValueList = new ArrayList<Map<String, Object>>();
			SeriesUtil.getServerCpuPerfMap(dblist, columnNames,
					columnValueList);

			cpujyPanel.getTableDataModel().setRowCount(0);
			cpujyPanel.setDataList(columnNames, columnValueList);

			if (second % reflashTime != 0) {
				try {
					Thread.sleep((reflashTime - second % reflashTime) * 1000);
				} catch (InterruptedException e) {
					LM.error(LM.Model.CS.name(), e);
				}
			}
			second = second + (reflashTime - second % reflashTime);
			Map<String, Object> seriesValue = columnValueList.get(0);

			Second key = new Second(second, minute);
			Map<Second, Map<String, Object>> data = new HashMap<Second, Map<String, Object>>();
			data.put(key, seriesValue);
			list.add(data);
			if (list.size() >= kd) {
				list.remove(0);
			}
			userssSeries.clear();
			sysuseSeries.clear();
			idleSeries.clear();
			for (Map<Second, Map<String, Object>> dd : list) {
				Second sc = dd.keySet().iterator().next();
				Map<String, Object> d = dd.values().iterator().next();
				userssSeries.addOrUpdate(sc,
						Double.parseDouble(d.get("用户使用率").toString()));
				sysuseSeries.addOrUpdate(sc,
						Double.parseDouble(d.get("系统使用率").toString()));
				idleSeries.addOrUpdate(sc,
						Double.parseDouble(d.get("空闲率").toString()));
			}
		} catch (Exception e) {
			// e.printStackTrace();
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(this, e.getMessage());
		}

	}

	private static void configFont(JFreeChart chart, Integer seconds) {
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

		plot.getRangeAxis().setRange(0, 100);
		NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
		numberAxis.setAutoTickUnitSelection(false);
		NumberTickUnit ntu = new NumberTickUnit(20);
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
//				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
	}
}
