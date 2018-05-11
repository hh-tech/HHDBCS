package com.hhdb.csadmin.plugin.menu.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hhdb.csadmin.common.bean.DefaultSet;
import com.hhdb.csadmin.common.bean.DefaultSetting;

public class QuerySetPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5705797500269917475L;
	private DefaultSet tpset;
	JPanel jfld=null;
	JComboBox<String> jcomb=null;
	JCheckBox linebox=null;

	
	public QuerySetPanel(){
		tpset = DefaultSetting.loadFontSettings();
		setLayout(new GridBagLayout());
		JLabel backlab = new JLabel("背景颜色:");
		backlab.setFont(getSetFont(13));
		// 背景颜色选择
		jfld = new JPanel();
		jfld.setBackground(DefaultSetting.strToColor(tpset.getBackground()));
		
		jfld.setPreferredSize(new Dimension(50, 18));
		// 字体大小选择
		jcomb = getFontSize(tpset.getFontSize());
		JLabel fontlab = new JLabel("字体大小:");
		fontlab.setFont(getSetFont(13));
		JLabel linelab = new JLabel("显示行号:");
		linelab.setFont(getSetFont(13));
		boolean bline = false;
		if ("true".equals(tpset.getLinunumber())) {
			bline = true;
		}
		jfld.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				Color c = JColorChooser.showDialog(null, "设置", Color.blue);
				jfld.setBackground(c);
			}
		});
		linebox = new JCheckBox("", bline);
		add(backlab, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(35, 30, 0, 0), 0, 0));
		add(jfld, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(35, 10, 0, 0), 0, 0));
		add(fontlab, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 30, 0, 0), 0, 0));
		add(jcomb, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 10, 0, 0), 0, 0));
//		add(linelab, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 30, 0, 0), 0, 0));
//		add(linebox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 10, 0, 0), 0, 0));
		add(new JPanel(), new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(15, 10, 0, 0), 0, 0));

	}
	
	private Font getSetFont(int fontsize) {
		return new Font("SimSun", Font.PLAIN, fontsize);
	}
	
	// 字体combobox
	public JComboBox<String> getFontSize(String fontsize) {
		String[] strfont = new String[] { "12", "14", "16", "18", "20", "24" };
		JComboBox<String> fbox = new JComboBox<String>();
		fbox.setPreferredSize(new Dimension(60, 20));
		for (String str : strfont) {
			fbox.addItem(str);
		}
		fbox.setSelectedItem(fontsize);
		return fbox;
	}
	
	public String getFontsize(){
		return jcomb.getSelectedItem().toString();
	}
	public String getBackColor(){
		return (jfld.getBackground().getRed() + "," + jfld.getBackground().getGreen() + "," + jfld.getBackground().getBlue());
	}
	public boolean getIsLine(){
		return linebox.isSelected();
	}
}
