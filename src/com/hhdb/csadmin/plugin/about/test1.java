package com.hhdb.csadmin.plugin.about;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UnsupportedLookAndFeelException;

import com.hhdb.csadmin.common.util.UiUtil;
import com.hhdb.csadmin.plugin.menu.util.UIUtils;

public class test1 {
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UiUtil.setLookAndFeel();
		final AboutPanel jpanel = new AboutPanel(new Color(107, 148, 200),
				//背景图、版本信息及文字介绍位置
				"etc/icon/backgroundImg/splash.png","etc/icon/backgroundImg/splash2.png",
				"etc/icon/backgroundImg/splash3.png","etc/icon/backgroundImg/splash4.png",
				new Color(107, 107, 107), 21, 165);
		//dbp.jpanel.repaint();
		//实例化界面
				final JFrame jframe = new JFrame();

				Dimension size = new Dimension(jpanel.imgWidth,jpanel.imgHight);
				jframe.setSize(size);
				jframe.setLayout(new BorderLayout());
			//	jframe.add(BorderLayout.CENTER, this);
				
				
				//上一张下一张按钮
				JPanel panel_down = new JPanel();
				panel_down.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
				JButton btn_last = new JButton("上一张");
				panel_down.add(btn_last);
				JButton btn_next = new JButton("下一张");
				panel_down.add(btn_next);
				panel_down.setBackground(null);  
				panel_down.setOpaque(false); 
				//将按钮添加至图片上
				jpanel.add(panel_down, BorderLayout.SOUTH);
				//关系窗口按钮
				JPanel panel_close=new JPanel();
				panel_close.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
				JButton btn_close = new JButton("关闭");
				panel_close.add(btn_close);
				jpanel.add(btn_close,BorderLayout.SOUTH);
				
				jframe.add(jpanel);

				jframe.setLocation(UIUtils.getPointToCenter(jframe, size));
				jframe.setUndecorated(true);
				jframe.validate();
				jframe.setVisible(true);
				//监听按钮换图
				btn_last.addActionListener(
						new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								jpanel.index--;
								if(jpanel.index<0){
									jpanel.index=3;
								}
								jpanel.repaint();
							}
						}
				);
				
				btn_next.addActionListener(
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								jpanel.index++;
								jpanel.repaint();
								
							}
						}
				);
				
				btn_close.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						//关闭
						//jframe.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
						jframe.setVisible(false);
					}
				});
		
	}
}
