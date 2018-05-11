package com.hhdb.csadmin.plugin.monitor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.hhdb.csadmin.plugin.monitor.series.CpuSeries;

/**
 * CPU监控图
 *
 */
public class CPUMinitor extends JScrollPane{
	private static final long serialVersionUID = 1L;
	private CpuSeries cpuseris;
	private ScheduledExecutorService executor =null;
	private JComboBox<String> sj=null;
	private int reflashTime=5;
	
	public CPUMinitor(DBMonitor monitor){
		JPanel picHeader=new JPanel();
		JPanel pic=new JPanel();
		JPanel cpu=new JPanel();
		sj=new JComboBox<String>();
		sj.addItem("5");
		sj.addItem("10");
		sj.addItem("30");
		
		picHeader.add(new JLabel("刷新频率:"));
		picHeader.add(sj);
		picHeader.add(new JLabel(" 秒"));
		picHeader.setBackground(Color.white);
		
		pic.setBackground(Color.WHITE);
		pic.add(picHeader);
		cpuseris=new CpuSeries(reflashTime,monitor);
		pic.setPreferredSize(new Dimension(cpuseris.getPreferredSize().width, cpuseris.getPreferredSize().height+50));
	    pic.add(cpuseris);
	   
	    cpu.setBackground(Color.white);
	    cpu.add(pic);
	  
	    this.setViewportView(cpu);
	    this.setBorder(null);
	    
	    executor = Executors.newScheduledThreadPool(1); 
	    executor.scheduleAtFixedRate(cpuseris, reflashTime*1000,reflashTime*1000,TimeUnit.MILLISECONDS); 
	    
	    sj.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				switch (e.getStateChange())
                {
                	case ItemEvent.SELECTED: 
	                runGenerateChart();
	                break;
                }
			}
		});
	}
	
	private void runGenerateChart(){
		if(executor!=null&&!executor.isShutdown()){
			executor.shutdown();
			cpuseris.closeConnection();
		}
		reflashTime=Integer.parseInt(sj.getSelectedItem().toString());
	    executor = Executors.newScheduledThreadPool(1); 
	    cpuseris.setTime(reflashTime);
	    executor.scheduleAtFixedRate(cpuseris, reflashTime*1000,reflashTime*1000,TimeUnit.MILLISECONDS); 
	}
	
	public void closeRunable(){
		executor.shutdown();
		cpuseris.closeConnection();
	}
}
