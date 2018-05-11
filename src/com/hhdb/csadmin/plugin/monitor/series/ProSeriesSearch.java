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

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.common.bean.SqlBean;
import com.hhdb.csadmin.common.ui.BaseTextField;
import com.hhdb.csadmin.common.util.HHSqlUtil;
import com.hhdb.csadmin.common.util.HHSqlUtil.ITEM_TYPE;
import com.hhdb.csadmin.plugin.monitor.DBMonitor;
import com.hhdb.csadmin.plugin.monitor.ui.MonitorTablePanel;
import com.hhdb.csadmin.plugin.monitor.util.PluginEventUtil;
import com.hhdb.csadmin.plugin.monitor.util.SeriesUtil;

/**
 * 进程监控查询
 * 
 */
public class ProSeriesSearch extends JPanel {
	private static final long serialVersionUID = 1L;
	//查询条件	包含(label, text, btn*2)
	private JPanel searchPanel = new JPanel();
	//服务器进程监控
	private MonitorTablePanel serverProPanel = new MonitorTablePanel(true);
	//动态库监控
	private MonitorTablePanel dynamicPanel = new MonitorTablePanel(true);
	//端口监控
	private MonitorTablePanel portPanel = new MonitorTablePanel(true);
	public ProSeriesSearch(final DBMonitor monitorPlugin) {
		setLayout(new GridBagLayout());
		serverProPanel.getViewport().setBackground(Color.WHITE);
		dynamicPanel.getViewport().setBackground(Color.WHITE);
		portPanel.getViewport().setBackground(Color.WHITE);
		setBackground(Color.WHITE);
		JLabel label = new JLabel("PID");
		label.setPreferredSize(new Dimension(35, 25));
		final BaseTextField field = new BaseTextField(true);
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
					
					List<String> columnNameDcLy = new ArrayList<String>();
					List<Map<String, Object>> columnValueDcLy = new ArrayList<Map<String, Object>>();
					
					List<String> columnNameAt = new ArrayList<String>();
					List<Map<String, Object>> columnValueAt = new ArrayList<Map<String, Object>>();
					
					SeriesUtil.getServerValueMap(dblist, columnNames, columnValueList);
				    serverProPanel.setDataList(columnNames, columnValueList);
				    
			        SeriesUtil.getServerPsValueMap(dblist, columnNames, columnValueList,columnNameDcLy,columnValueDcLy,columnNameAt,columnValueAt);
				    serverProPanel.setDataList(columnNames, columnValueList);
				    dynamicPanel.setDataList(columnNameDcLy, columnValueDcLy);
				    portPanel.setDataList(columnNameAt,columnValueAt);
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
	    add(searchPanel, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
	    add(serverProPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
	    add(dynamicPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
	    add(portPanel, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
	    add(jp, new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 10, 0, 0), 0, 0));
	}
}
