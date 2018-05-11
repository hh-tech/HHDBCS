package com.hhdb.csadmin.plugin.monitor;

import java.awt.Color;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JPanel;

import com.hhdb.csadmin.plugin.monitor.series.NetSeries;

/**
 * 网络监控
 */
public class NetMonitor extends JPanel {
	private static final long serialVersionUID = 1L;

	//硬盘详细使用信息
	private NetSeries netSeries;
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);  
	
	public NetMonitor(DBMonitor dbMonitorp){
		//setLayout(new GridBagLayout());
		netSeries=new NetSeries(dbMonitorp);
		setBackground(Color.WHITE);
		//add(netSeries, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
	    add(netSeries);
	}
	
	public void closeRunable(){
		executor.shutdown();
	}
}
