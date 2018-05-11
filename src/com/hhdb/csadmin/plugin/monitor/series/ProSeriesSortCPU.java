package com.hhdb.csadmin.plugin.monitor.series;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.common.bean.SqlBean;
import com.hhdb.csadmin.common.util.HHSqlUtil;
import com.hhdb.csadmin.common.util.HHSqlUtil.ITEM_TYPE;
import com.hhdb.csadmin.plugin.monitor.DBMonitor;
import com.hhdb.csadmin.plugin.monitor.ui.MonitorTablePanel;
import com.hhdb.csadmin.plugin.monitor.util.PluginEventUtil;
import com.hhdb.csadmin.plugin.monitor.util.SeriesUtil;

/**
 * 进程监控内存排序
 */
public class ProSeriesSortCPU extends JPanel {
	private static final long serialVersionUID = 1L;
	//服务器进程监控
	private MonitorTablePanel serverProPanel = new MonitorTablePanel(true);
	private JButton reflash=new JButton("刷新");
	private DBMonitor monitorPlugin;
	public ProSeriesSortCPU(final DBMonitor monitorPlugin) {
		this.monitorPlugin=monitorPlugin;
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		serverProPanel.getViewport().setBackground(Color.WHITE);
		loadData();
	    JPanel jpl=new JPanel();
	    jpl.setBackground(Color.WHITE);
	    jpl.add(reflash); 
	    reflash.addMouseListener(new MouseAdapter() {
	    	@Override
	    	public void mouseClicked(MouseEvent e) {
	    		loadData();
	    	}
		});
	    add(jpl, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 10, 0, 0), 0, 0));
	    add(serverProPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(20, 20, 20, 20), 0, 0));
	}
	
	private void loadData(){
		serverProPanel.getBaseTable().removeAll();
	    try{
	    	//查找事件
	        SqlBean sqlBean = HHSqlUtil.getSqlBean(ITEM_TYPE.DBSERVER,"show_ps_top_mem");
	        List<Map<String, Object>> dblist = PluginEventUtil.executeSqlListMapEvent(monitorPlugin, monitorPlugin.getClass().getPackage().getName(), sqlBean.getSql());	
		    
		    List<String> columnNames = new ArrayList<String>();
			List<Map<String, Object>> columnValueList = new ArrayList<Map<String, Object>>();
			SeriesUtil.getServerValueMap(dblist, columnNames, columnValueList);
		    serverProPanel.setDataList(columnNames, columnValueList);
		}catch(Exception ee){
			LM.error(LM.Model.CS.name(), ee);
		}
	}
	
}
