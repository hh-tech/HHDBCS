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
 * 网络实时监控
 * 
 */
public class NetSeries extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	private MonitorTablePanel netPanel = new MonitorTablePanel(true);
	private JButton reflash = new JButton("刷新");
	private Connection hc;
	private ServerBean serverbean;

	public NetSeries(DBMonitor dbMonitorp) {
		CmdEvent getsbEvent = new CmdEvent("com.hhdb.csadmin.plugin.monitor",
				"com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent revent = dbMonitorp.sendEvent(getsbEvent);
		serverbean = (ServerBean) revent.getObj();

		setBackground(Color.WHITE);
		netPanel.getViewport().setBackground(Color.WHITE);

		try {
			hc = ConnService.createConnection(serverbean);
			// 实时监控
			SqlBean sqlBean = HHSqlUtil.getSqlBean(ITEM_TYPE.DBSERVER,
					"eth_info");
			List<Map<String,Object>> dblist = SqlQueryUtil.select(hc, sqlBean.getSql());
			List<String> columnNames = new ArrayList<String>();
			List<Map<String, Object>> columnValueList = new ArrayList<Map<String, Object>>();
			SeriesUtil.getServerValueMap(dblist, columnNames,
					columnValueList);
			netPanel.setDataList(columnNames, columnValueList);
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
		add(jpl, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 10, 0, 0), 0, 0));
		add(netPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(10, 10, 0, 0), 0, 0));
	}

	@Override
	public void run() {
		try {
			// 实时监控
			SqlBean sqlBean = HHSqlUtil.getSqlBean(ITEM_TYPE.DBSERVER,
					"eth_info");

			List<Map<String,Object>> dblist = SqlQueryUtil.select(hc, sqlBean.getSql());
			List<String> columnNames = new ArrayList<String>();
			List<Map<String, Object>> columnValueList = new ArrayList<Map<String, Object>>();
			SeriesUtil.getServerValueMap(dblist, columnNames,
					columnValueList);
			netPanel.getBaseTable().removeAll();
			netPanel.setDataList(columnNames, columnValueList);
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
