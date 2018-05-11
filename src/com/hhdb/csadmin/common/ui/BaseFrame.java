package com.hhdb.csadmin.common.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.hh.frame.common.log.LM;

//import com.hh.frame.swingui.util.LogDefault;


public class BaseFrame extends JFrame {
//	private static LogDefault logger=LogDefault.getDefaultLogger("com.hhdb.csadmin.logger");

	private static final long serialVersionUID = 1L;
	public static final String TITLE = "恒辉关系数据库--CS管理工具";
	private static final String APPLICATION_ICON = "manage.png";
	private int lastX;
	private int lastY;
	private int lastWidth;
	private int lastHeight;

	public BaseFrame() {
		super(TITLE);
		Image icon;
		try {
			icon = new ImageIcon(System.getProperty("user.dir")+"/etc/icon/"+APPLICATION_ICON).getImage();
			setIconImage(icon);
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			// setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension size = new Dimension(dim.width * 2 / 3, dim.height * 2 / 3);
			setSize(size);
			lastWidth = getSize().width;
			lastHeight = getSize().height;
			lastX = (dim.width - lastWidth) / 2;
			lastY = (dim.height - lastHeight) / 2;
			setBounds(lastX, lastY, lastWidth, lastHeight);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					int option = JOptionPane.showConfirmDialog(BaseFrame.this, "确定要退出吗？", " 提示", JOptionPane.OK_CANCEL_OPTION);
					if (JOptionPane.OK_OPTION == option) {
						// 点击了确定按钮
						System.exit(0);
					} else {
						BaseFrame.this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					}
				}
			});
			setLayout(new BorderLayout());
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			//logger.error(e, null);
		}
		
	}

	public int getLastX() {
		return lastX;
	}

	public int getLastY() {
		return lastY;
	}

	public int getLastWidth() {
		return lastWidth;
	}

	public int getLastHeight() {
		return lastHeight;
	}
}