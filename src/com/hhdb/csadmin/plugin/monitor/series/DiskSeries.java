package com.hhdb.csadmin.plugin.monitor.series;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
 * 硬盘实时监控
 */
public class DiskSeries extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	private MonitorTablePanel xxPanel = new MonitorTablePanel(true);
	private MonitorTablePanel jdPanel = new MonitorTablePanel(true);
	private JButton reflash = new JButton("刷新");
	private Connection hc;
	private ServerBean serverbean;

	public DiskSeries(DBMonitor dbMonitorp) {
		CmdEvent getsbEvent = new CmdEvent("com.hhdb.csadmin.plugin.monitor",
				"com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent revent = dbMonitorp.sendEvent(getsbEvent);
		serverbean = (ServerBean) revent.getObj();

		xxPanel.getViewport().setBackground(Color.WHITE);
		jdPanel.getViewport().setBackground(Color.WHITE);

		try {
			hc = ConnService.createConnection(serverbean);
			// 表格详细信息
			SqlBean sqlBean = HHSqlUtil.getSqlBean(ITEM_TYPE.DBSERVER,
					"show_disk_usage");
			List<Map<String,Object>> dblist = SqlQueryUtil.select(hc, sqlBean.getSql());

			List<String> columnNames = new ArrayList<String>();
			List<Map<String, Object>> columnValueList = new ArrayList<Map<String, Object>>();

			SeriesUtil.getServerValueMap(dblist, columnNames,
					columnValueList);
			xxPanel.setDataList(columnNames, columnValueList);
			// 表格简单信息
			sqlBean = HHSqlUtil
					.getSqlBean(ITEM_TYPE.DBSERVER, "show_disk_perf");
			columnNames = new ArrayList<String>();
			columnValueList = new ArrayList<Map<String, Object>>();

			dblist = SqlQueryUtil.select(hc, sqlBean.getSql());
			SeriesUtil.getServerValueMap(dblist, columnNames,
					columnValueList);
			jdPanel.setDataList(columnNames, columnValueList);
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
		JPanel jpl = new JPanel();
		jpl.setBackground(Color.WHITE);
		jpl.add(reflash);
		reflash.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				run();
			}
		});
		setLayout(new GridBagLayout());
		add(jpl, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 10, 0, 0), 0, 0));
		add(xxPanel, new GridBagConstraints(0, 1, 1, 1, 1, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(10, 10, 0, 0), 0, 0));
		add(jdPanel, new GridBagConstraints(1, 1, 1, 1, 1, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(10, 10, 0, 0), 0, 0));
	}

	@Override
	public void run() {
		try {
			// 硬盘详细信息
			SqlBean sqlBean = HHSqlUtil.getSqlBean(ITEM_TYPE.DBSERVER,
					"show_disk_usage");

			List<Map<String,Object>> dblist = SqlQueryUtil.select(hc, sqlBean.getSql());
			List<String> columnNames = new ArrayList<String>();
			List<Map<String, Object>> columnValueList = new ArrayList<Map<String, Object>>();

			SeriesUtil.getServerValueMap(dblist, columnNames,
					columnValueList);
			xxPanel.getTableDataModel().setRowCount(0);
			xxPanel.setDataList(columnNames, columnValueList);
			// 表格简单信息
			sqlBean = HHSqlUtil
					.getSqlBean(ITEM_TYPE.DBSERVER, "show_disk_perf");

			columnNames = new ArrayList<String>();
			columnValueList = new ArrayList<Map<String, Object>>();
			dblist = SqlQueryUtil.select(hc, sqlBean.getSql());
			SeriesUtil.getServerValueMap(dblist, columnNames,
					columnValueList);
			jdPanel.getBaseTable().removeAll();
			jdPanel.setDataList(columnNames, columnValueList);
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
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
