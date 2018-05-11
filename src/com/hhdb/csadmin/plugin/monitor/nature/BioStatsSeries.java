package com.hhdb.csadmin.plugin.monitor.nature;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
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
import com.hhdb.csadmin.common.dao.ConnService;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.monitor.DBMonitor;

/**
 * database session
 * 
 */
public class BioStatsSeries extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	private TimeSeries activeSeries = new TimeSeries("读取");
	private TimeSeries idleSeries = new TimeSeries("匹配");
	private List<TimeData> list = new ArrayList<TimeData>();
	private Connection hc;
	private ScheduledExecutorService executor;
	private ServerBean serverbean;

	public BioStatsSeries(ScheduledExecutorService executor,
			DBMonitor dbMonitorp) {
		this.executor = executor;
		
		CmdEvent getsbEvent = new CmdEvent("com.hhdb.csadmin.plugin.monitor",
				"com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent revent = dbMonitorp.sendEvent(getsbEvent);
		serverbean = (ServerBean) revent.getObj();
		
		try {
			hc = ConnService.createConnection(serverbean);
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		}

		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(activeSeries);
		dataset.addSeries(idleSeries);
		JFreeChart chart = ChartFactory.createTimeSeriesChart("块 IO", "", "",
				dataset, true, true, false);
		configFont(chart);
		ChartPanel jp = new ChartPanel(chart);
		// database session
		add(jp, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(10, 0, 0, 0), 0, 0));
	}

	@Override
	public void run() {
		Calendar date = Calendar.getInstance();
		Day day = new Day(date.getTime());
		Hour hour = new Hour(date.get(Calendar.HOUR_OF_DAY), day);
		Minute minute = new Minute(date.get(Calendar.MINUTE), hour);
		int second = date.get(Calendar.SECOND);

		String sql = "SELECT "
				+ "(SELECT sum(blks_read) FROM "+StartUtil.prefix+"_stat_database WHERE datname ='"
				+ serverbean.getDBName()
				+ "') AS \"reads\", "
				+ "(SELECT sum(blks_hit) FROM "+StartUtil.prefix+"_stat_database WHERE datname ='"
				+ serverbean.getDBName() + "') AS \"hits\"; ";

		try {
			
			List<Map<String, Object>> dablist = SqlQueryUtil.select(hc, sql);

			list.add(new TimeData(new Second(second, minute), dablist.get(0)));
			if (list.size() >= 10) {
				list = list.subList(list.size() - 10, list.size());
			}
			activeSeries.clear();
			idleSeries.clear();
			for (TimeData d : list) {
				activeSeries.addOrUpdate(d.getSecond(),
						Integer.parseInt(d.getValue().get("reads").toString()));
				idleSeries.addOrUpdate(d.getSecond(),
						Integer.parseInt(d.getValue().get("hits").toString()));
			}
		} catch (Exception ee) {
			LM.error(LM.Model.CS.name(), ee);
			if (list.size() >= 10) {
				list = list.subList(list.size() - 10, list.size());
			}
			executor.shutdown();
			JOptionPane.showMessageDialog(this, ee.getMessage());

		}

	}

	private static void configFont(JFreeChart chart) {
		// 配置字体
		Font yfont = new Font("宋体", Font.PLAIN, 12);// Y轴
		Font kfont = new Font("宋体", Font.PLAIN, 12);// 底部
		Font titleFont = new Font("宋体", Font.BOLD, 16); // 图片标题
		XYPlot plot = chart.getXYPlot();// 图形的绘制结构对象
		DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
		domainAxis.setTickUnit(new DateTickUnit(DateTickUnitType.SECOND, 1,
				new SimpleDateFormat("mm:ss")));
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

		NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
		numberAxis.setAutoTickUnitSelection(true);

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
				//JOptionPane.showMessageDialog(this, e.getMessage());
				LM.error(LM.Model.CS.name(), e);
			}
		}
	}
}
