package com.hhdb.csadmin.plugin.flow_editor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.hhdb.csadmin.common.ui.BaseFrame;

/**
 * 对话框面板
 */
public class JFramePanel extends JFrame  {
	
	private static final long serialVersionUID = 1L;
	
	private BaseFrame bf;
	
	public JFramePanel(String title,int width, int height,JComponent comp,BaseFrame baf){
		super(title);
		this.bf = baf;
		add(comp);
		setSize(width, height);
		setLocationRelativeTo(null);
		setVisible(true);
		//父窗口不可用
		bf.setEnabled(false);
		
		//关闭窗口使父窗口可用
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					bf.setEnabled(true);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
