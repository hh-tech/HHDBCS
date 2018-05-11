package com.hhdb.csadmin.plugin.function.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import com.hh.frame.swingui.swingcontrol.displayTable.basis.BaseTable;


public class MessagePanel extends JTabbedPane{

	private static final long serialVersionUID = 1L;
	private JTextArea message = new JTextArea();
	private JPanel messagePanel=null;
	
	public MessagePanel() {
		super(JTabbedPane.TOP);
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
	}

	public class OutputPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public OutputPanel(Component comp) {
			setLayout(new BorderLayout());
			if (comp instanceof JTextArea) {
				((JTextArea) comp).setEditable(false);
			}
			setBorder(null);
			JScrollPane scroll = new JScrollPane(comp);
			scroll.getViewport().setBackground(Color.WHITE);
			scroll.setBorder(null);
			add(scroll);
		}
	}
	
	/**
	 * 异常
	 * @param text
	 */
	public void setMessage(String text) {
		messagePanel = new OutputPanel(message);
		add(messagePanel,"消息",0);
		
		message.setText(text);
	}

	/**
	 * 数据填充表格
	 * @param data0
	 * @param colNames
	 */
	public void showData(Vector<Object> data0, Vector<String> colNames) {
		BaseTable table = new BaseTable();
		table.setModel(new DefaultTableModel(data0, colNames));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setRowHeight(22);
		JPanel grid = new OutputPanel(table);
		add(grid,"结果集",0);
	}
	
	public void cleanTab(){
		removeAll();
	}
}
