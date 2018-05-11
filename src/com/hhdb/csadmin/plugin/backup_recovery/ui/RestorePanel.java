package com.hhdb.csadmin.plugin.backup_recovery.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * 恢复面板
 * @author hhxd
 *
 */
public abstract class RestorePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	protected BaseButton ok, cancle;
	protected JPanel tab1;
	protected JScrollPane scroll;
	protected JProgressBar progressBar ;
	protected JTextField textfd;
	protected JTextPane msg;
	protected JPanel tab;
	private JLabel tlabel11,tlabel21,tlabel41,tlabel61;
	public RestorePanel(int width,int height){
		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(width,height));
		//选择备份路径控件
        textfd=new JTextField();
		textfd.setEditable(false);
		textfd.setPreferredSize(new Dimension(240,20));
		BaseButton btn=new BaseButton("...");
		btn.setActionCommand("jz");
		btn.addActionListener(this);
		JPanel jp=new JPanel();
		jp.setLayout(new GridBagLayout());
		jp.add(new BaseLabel("文件名"),new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 10, 0,0), 0, 0));
		jp.add(textfd,new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(4, 5, 0,0), 0, 0));
		jp.add(btn,new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 5, 0,5), 0, 0));
		
		tab=new JPanel();
		tab.setLayout(new GridBagLayout());
		tab.add(jp,new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3, 5, 10,7), 0, 0));
		
		tab1=new JPanel();
		tab1.setLayout(new GridBagLayout());
		Border b = BorderFactory.createTitledBorder("  信息日志 ");
		JLabel tlabel1 = new JLabel("服务器：");
		JLabel tlabel2 = new JLabel("数据库：");
		JLabel tlabel4 = new JLabel("已处理对象：");
		JLabel tlabel6 = new JLabel("时间：");
		
		tlabel11 = new JLabel("localhost");
		tlabel21 = new JLabel("hhdb ");
		tlabel41 = new JLabel("");
		tlabel61 = new JLabel("");
		
		tab1.setBorder(b);
		tab1.add(tlabel1, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 45, 0,0), 0, 0));
		tab1.add(tlabel2, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 45, 0,0), 0, 0));
		tab1.add(tlabel4, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 45, 0,0), 0, 0));
		tab1.add(tlabel6, new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 45, 0,0), 0, 0));
		tab1.add(tlabel11, new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 70, 0,60), 0, 0));
		tab1.add(tlabel21, new GridBagConstraints(2, 2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 70, 0,0), 0, 0));
		tab1.add(tlabel41, new GridBagConstraints(2, 3, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 70, 0,0), 0, 0));
		tab1.add(tlabel61, new GridBagConstraints(2, 4, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 70, 3,0), 0, 0));
		
		msg=new JTextPane();
		scroll = new JScrollPane(msg); 
		msg.setEditable(false);
		
		progressBar = new JProgressBar();
		progressBar.setOrientation(JProgressBar.HORIZONTAL);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setValue(0);
		progressBar.setStringPainted(false);
		progressBar.setPreferredSize(new Dimension(50, 15));
	}	
	
	
	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBarMaxandMin(int max,int min) {
		progressBar.setMaximum(max);
		progressBar.setMinimum(min);
	}
	public void setProgressBarValue(int value) {
		progressBar.setValue(value);
	}
	public JTextPane getMsg() {
		return msg;
	}

	public void addMsg(String txt) {
		Document docs = msg.getDocument();//获得文本对象
		int start=docs.getLength();
		try {
			docs.insertString(start, txt, msg.getCharacterAttributes());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public JLabel getTlabel11() {
		return tlabel11;
	}

	public void setTlabel11(String tlabel11) {
		this.tlabel11.setText(tlabel11);
	}

	public JLabel getTlabel21() {
		return tlabel21;
	}

	public void setTlabel21(String tlabel21) {
		this.tlabel21.setText(tlabel21);
	}

	public JLabel getTlabel41() {
		return tlabel41;
	}

	public void setTlabel41(String tlabel41) {
		this.tlabel41.setText(tlabel41);
	}

	public JLabel getTlabel61() {
		return tlabel61;
	}

	public void setTlabel61(String tlabel61) {
		this.tlabel61.setText(tlabel61);
	}

	public String getTextfd() {
		return textfd.getText();
	}
	public BaseButton getCancle() {
		return cancle;
	}
}
