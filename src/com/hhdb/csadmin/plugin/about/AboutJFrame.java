package com.hhdb.csadmin.plugin.about;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;

import com.hhdb.csadmin.plugin.menu.util.UIUtils;

public class AboutJFrame extends Canvas {

	private static final long serialVersionUID = 1L;
	private Window window;

	public AboutJFrame() {
		AboutPanel jpanel = new AboutPanel(
				new Color(107, 148, 200),
				// 背景图、版本信息及文字介绍位置
				"etc/icon/backgroundImg/splash.jpg",
				"etc/icon/backgroundImg/splash2.jpg",
				"etc/icon/backgroundImg/splash3.jpg",
				"etc/icon/backgroundImg/splash4.jpg", 
				 new Color(107, 107, 107), 21, 165);
		Dimension size = new Dimension(jpanel.imgWidth, jpanel.imgHight);
		window = new Window(new Frame());
		window.setSize(size);
		window.setLayout(new BorderLayout());
		window.setVisible(true);
		window.setLocation(UIUtils.getPointToCenter(window, size));
		validate();
		ButtonPanel bp = new ButtonPanel(this,jpanel);
		jpanel.setLayout(new BorderLayout());
		jpanel.add(bp,BorderLayout.CENTER);
		window.add(jpanel);
		window.setVisible(true);
		
		
	}
	public static void main(String[] args) {
		 new AboutJFrame();
	}
	//关闭窗口
	public void close(){
		window.dispose();
	}
	
	
}
