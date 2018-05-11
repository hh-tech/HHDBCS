package com.hhdb.csadmin.plugin.menu.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hhdb.csadmin.common.bean.DefaultSet;
import com.hhdb.csadmin.common.bean.DefaultSetting;


public class CmdSetPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4968949824009267661L;
	private JPanel backcolor;
	private JPanel fontcolor;
	private DefaultSet tpset;
		public CmdSetPanel(){
		tpset = DefaultSetting.loadFontSettings();
		setLayout(new GridBagLayout());
		backcolor = new JPanel();
		backcolor.setBackground(DefaultSetting.strToColor(tpset.getCmdbackcolor()));
		
		backcolor.setPreferredSize(new Dimension(50, 18));
		fontcolor = new JPanel();
		fontcolor.setBackground(DefaultSetting.strToColor(tpset.getCmdfontcolor()));
		
		fontcolor.setPreferredSize(new Dimension(50, 18));
		backcolor.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				Color c = JColorChooser.showDialog(null, "设置", Color.blue);
				backcolor.setBackground(c);
			}
		});
		fontcolor.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				Color c = JColorChooser.showDialog(null, "设置", Color.blue);
				fontcolor.setBackground(c);
			}
		});
		add(new JLabel("背景颜色："), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(32, 30, 0, 0), 0, 0));
		add(backcolor, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(35, 10, 0, 0), 0, 0));
		add(new JLabel("字体颜色："), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(12, 30, 0, 0), 0, 0));
		add(fontcolor, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 10, 0, 0), 0, 0));
		add(new JPanel(), new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(15, 10, 0, 0), 0, 0));
	}
	
	public String getBackcolor(){
		return (backcolor.getBackground().getRed() + "," + backcolor.getBackground().getGreen() + "," + backcolor.getBackground().getBlue());
	}
	public String getFontcolor(){
		return (fontcolor.getBackground().getRed() + "," + fontcolor.getBackground().getGreen() + "," + fontcolor.getBackground().getBlue());
	}


}
