package com.hhdb.csadmin.plugin.sequence.panel;

import java.awt.Color;

import java.awt.Font;

import javax.swing.JTextPane;


/**
 * x新建序列注释
 * @author gd
 *
 */
public class SequenceAnnotationPannel extends JTextPane{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 构造方法
	 * @param node
	 * @param isEdit
	 */
	public SequenceAnnotationPannel() {
		setBackground(Color.WHITE);	
		Font font = new Font("宋体", Font.PLAIN, 16);
		setFont(font);
		
	}

}
