package com.hhdb.csadmin.plugin.status_bar;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;


public class StatusBar extends JPanel {

	/**
	 * 
	 */
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	JLabel label = new JLabel("", JLabel.LEFT);
	static JLabel label1 = new JLabel("");
	JLabel label2 = new JLabel("北京恒辉信达技术有限公司",JLabel.RIGHT);
	private static final long serialVersionUID = 1L;
	Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

	public StatusBar() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel comp = new JPanel(new GridLayout(1, 1), false);
		TimePanel timePanel = new TimePanel();
		Font font=new Font("宋体", Font.PLAIN,14);
		timePanel.setFont(font);
		label.setFont(font);
		comp.add(label);
		comp.add(label1);
		comp.add(label2);
		//comp.add(timePanel,BorderLayout.EAST);
		comp.setBorder(loweredetched);
		this.add(comp);
//		Timer timer = new Timer();
//		timer.schedule(new ShowTime(), new Date(), 1000);
	}

	public void setText(String statusText) {
		this.label.setText(statusText);
	}
	
	class ShowTime extends TimerTask {
		public void run() {
			repaint();
		}
	}
	
	class TimePanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public void paint(Graphics g) {
			super.paint(g);
			g.drawString(sdf.format(new Date()), 150, 12);
		}
	}
	
}

