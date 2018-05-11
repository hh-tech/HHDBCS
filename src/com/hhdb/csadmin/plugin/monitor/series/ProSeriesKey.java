package com.hhdb.csadmin.plugin.monitor.series;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.common.bean.SqlBean;
import com.hhdb.csadmin.common.util.HHSqlUtil;
import com.hhdb.csadmin.common.util.HHSqlUtil.ITEM_TYPE;
import com.hhdb.csadmin.plugin.monitor.DBMonitor;
import com.hhdb.csadmin.plugin.monitor.ui.MonitorTablePanel;
import com.hhdb.csadmin.plugin.monitor.util.PluginEventUtil;
import com.hhdb.csadmin.plugin.monitor.util.SeriesUtil;

/**
 * 关键字查找进程监控
 */
public class ProSeriesKey extends JPanel {
	private static final long serialVersionUID = 1L;
	//查询条件	包含(label, text, btn*2)
	private JPanel searchPanel = new JPanel();
	//服务器进程监控
	private MonitorTablePanel serverProPanel = new MonitorTablePanel(true);
	public ProSeriesKey(final DBMonitor monitorPlugin) {
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		serverProPanel.getViewport().setBackground(Color.WHITE);
		JLabel label = new JLabel("关键字");
		label.setPreferredSize(new Dimension(42, 25));
		final JTextField field = new JTextField();
		field.setPreferredSize(new Dimension(100, 22));
		JButton btnSubmit = new JButton("查找");
		btnSubmit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
			    try{
			    	//查找事件
			        SqlBean sqlBean = HHSqlUtil.getSqlBean(ITEM_TYPE.DBSERVER,field.getText());
			        List<Map<String,Object>> dblist = PluginEventUtil.executeSqlListMapEvent(monitorPlugin, monitorPlugin.getClass().getPackage().getName(), sqlBean.getSql());			        			        
			        List<String> columnNames = new ArrayList<String>();
					List<Map<String, Object>> columnValueList = new ArrayList<Map<String, Object>>();
					SeriesUtil.getServerValueMap(dblist, columnNames, columnValueList);
				    serverProPanel.setDataList(columnNames, columnValueList);
				}catch(Exception ee){
					LM.error(LM.Model.CS.name(), ee);
				}
			}
		});
		JButton btnReset = new JButton("重置");
		btnReset.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//重置事件
				field.setText("");
			}
		});
		searchPanel.setBackground(Color.WHITE);
		//搜索
		searchPanel.add(label);
		searchPanel.add(field);
		searchPanel.add(btnSubmit);
		searchPanel.add(btnReset);
		JPanel jp=new JPanel();
		jp.setBackground(Color.WHITE);
	    add(searchPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 20, 0, 0), 0, 0));
	    add(serverProPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 20, 20, 20), 0, 0));
	    add(jp, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 10, 0, 0), 0, 0));
	}
}
