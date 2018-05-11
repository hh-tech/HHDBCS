package com.hhdb.csadmin.plugin.menu.ui;

import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

public class ServerToolPanel extends JToolBar{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3028638752411429026L;
	private CurrentConnectPanel currentConnectPanel;
	private JButton reflashbtn = null, endbtn = null;

	public ServerToolPanel(CurrentConnectPanel currentConnectPanel) {
		this.setCurrentConnectPanel(currentConnectPanel);
		setFloatable(false);
		setMargin(new Insets(0, 0, 0, 0));
		reflashbtn = new JButton("刷新", new ImageIcon(
				LockInfoPanel.class.getResource("/icon/reflash.png")));
		endbtn = new JButton("结束进程", new ImageIcon(
				LockInfoPanel.class.getResource("/icon/end.png")));
		add(reflashbtn);
		add(endbtn);
		/*reflashbtn.addActionListener(this);
		endbtn.addActionListener(this);*/
	}

	public CurrentConnectPanel getCurrentConnectPanel() {
		return currentConnectPanel;
	}

	public void setCurrentConnectPanel(CurrentConnectPanel currentConnectPanel) {
		this.currentConnectPanel = currentConnectPanel;
	}

	

}
