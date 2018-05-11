package com.hhdb.csadmin.plugin.menu.ui;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;


public class ServerPanel extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8991292491545266563L;

	public ServerPanel(CurrentConnectPanel currentConnectPanel, CurrentDealPanel currentDealPanel){
		setLayout(new GridBagLayout());
		JTabbedPane jTabbedPane=new JTabbedPane();
		jTabbedPane.addTab("当前连接监控", currentConnectPanel);
		jTabbedPane.addTab("当前交易监控", currentDealPanel);
	
		add(jTabbedPane, new GridBagConstraints(0, 0, 1, 1,1.0,1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
	}
}
