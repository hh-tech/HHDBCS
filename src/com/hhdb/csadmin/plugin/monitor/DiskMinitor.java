package com.hhdb.csadmin.plugin.monitor;

import java.awt.Color;

import javax.swing.JPanel;

import com.hhdb.csadmin.plugin.monitor.series.DiskSeries;

/**
 * 硬盘监控
 */
public class DiskMinitor extends JPanel {
	private static final long serialVersionUID = 1L;

	//硬盘详细使用信息
	private DiskSeries diskSeries;
	
//	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);  
	
	public DiskMinitor(DBMonitor dbMonitorp){
//		setLayout(new GridBagLayout());
		diskSeries=new DiskSeries(dbMonitorp);
		setBackground(Color.WHITE);
		diskSeries.setBackground(Color.WHITE);
		//add(diskSeries, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 10, 0, 0), 0, 0));
		add(diskSeries);
		//	    executor.scheduleAtFixedRate(diskSeries, 10000,10000,TimeUnit.MILLISECONDS); 
	}
	public void closeRunable(){
//		executor.shutdown();
	}
}
