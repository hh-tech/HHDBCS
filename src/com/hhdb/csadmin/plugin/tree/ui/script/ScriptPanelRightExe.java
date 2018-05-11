package com.hhdb.csadmin.plugin.tree.ui.script;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ScriptPanelRightExe extends JPanel {
	private static final long serialVersionUID = 1L;
	public JTextArea infoArea = new JTextArea("");
	public ScriptPanelRightExe(){
		//填充检查
		setBackground(Color.WHITE);
		setLayout(new GridBagLayout());
		JLabel label=new JLabel("结果输出:");
		label.setPreferredSize(new Dimension(100,20));
		infoArea.setEditable(false);
		
		
		add(label, new GridBagConstraints(1 , 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
//		add(scrollpane, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
		add(infoArea, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));

		JPanel jpl = new JPanel();
		jpl.setBackground(Color.WHITE);
		add(jpl, new GridBagConstraints(0, 10, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
	}
	
	
}
