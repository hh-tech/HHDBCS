package com.hhdb.csadmin.plugin.about;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ButtonPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private ImageIcon iconlast;
	private ImageIcon iconnext;
	public ButtonPanel(final AboutJFrame abf, final AboutPanel abp) {
		setLayout(new GridBagLayout());
		//取消透明
		setOpaque(false); 
		// 上一张下一张按钮
		iconlast = new ImageIcon("etc/icon/backgroundImg/last.png");
		iconnext = new ImageIcon("etc/icon/backgroundImg/next.png");
		final JButton btn_last = new JButton();
		final JButton btn_next = new JButton();
		btn_last.setBounds(0, 0, 16, 22);
		btn_next.setBounds(0, 0, 16, 22);
		Image imglast=iconlast.getImage().getScaledInstance(btn_last.getWidth(),btn_last.getHeight(),Image.SCALE_DEFAULT);
		iconlast = new ImageIcon(imglast);
		btn_last.setIcon(iconlast);
		Image next=iconnext.getImage().getScaledInstance(btn_next.getWidth(),btn_next.getHeight(),Image.SCALE_DEFAULT);
		iconnext = new ImageIcon(next);
		btn_next.setIcon(iconnext);
//		btn_last.setFont(new Font("宋体",Font.BOLD,18));
//		btn_next.setFont(new Font("宋体",Font.BOLD,18));
//		btn_last.setForeground(Color.GRAY);
//		btn_next.setForeground(Color.GRAY);
		btn_last.setMargin(new java.awt.Insets(0, 0, 0, 0));
		btn_next.setMargin(new java.awt.Insets(0, 0, 0, 0));
		final JButton btn_close = new JButton("关闭");
		btn_close.setForeground(Color.GRAY);
		btn_close.setFont(new Font("宋体",Font.PLAIN,13));
		//删除按钮空白部分
		btn_close.setMargin(new Insets(0,0,0,0));
		add(btn_last,new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.CENTER,new Insets(80, 00, 170, 215), 0, 0));
		add(btn_next,new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.CENTER,new Insets(80, 230, 170, 00), 0, 0));
		add(btn_close,new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.BOTH,new Insets(0, 230, 45, 00), 0, 0));
		//隐藏按钮边框
		btn_last.setContentAreaFilled(false);
		btn_next.setContentAreaFilled(false);
		btn_close.setContentAreaFilled(false);
		btn_last.setBorderPainted(false);
		btn_next.setBorderPainted(false);
		btn_close.setBorderPainted(false);
		btn_last.setToolTipText("上一页");
		btn_next.setToolTipText("下一页");
		btn_last.addMouseListener(new MouseAdapter() {
			 //鼠标移入
			   public void mouseEntered(MouseEvent e) {
				   btn_last.repaint();
				   ImageIcon iconlast = new ImageIcon("etc/icon/backgroundImg/last2.png");
			       Image imglast=iconlast.getImage().getScaledInstance(btn_last.getWidth(),btn_last.getHeight(),Image.SCALE_DEFAULT);
			       iconlast = new ImageIcon(imglast);
					btn_last.setIcon(iconlast);
				  
			   }
			   public void mouseExited(MouseEvent m){
				   btn_last.repaint();
				   ImageIcon iconlast = new ImageIcon("etc/icon/backgroundImg/last.png");
			       Image imglast=iconlast.getImage().getScaledInstance(btn_last.getWidth(),btn_last.getHeight(),Image.SCALE_DEFAULT);
			       iconlast = new ImageIcon(imglast);
				   btn_last.setIcon(iconlast);
			   }
		});
		btn_next.addMouseListener(new MouseAdapter() {
			 //鼠标移入
			   public void mouseEntered(MouseEvent e) {
				   btn_next.repaint();
				   ImageIcon iconlast = new ImageIcon("etc/icon/backgroundImg/next2.png");
			       Image imglast=iconlast.getImage().getScaledInstance(btn_next.getWidth(),btn_next.getHeight(),Image.SCALE_DEFAULT);
			       iconlast = new ImageIcon(imglast);
			       btn_next.setIcon(iconlast);
			   }
			   public void mouseExited(MouseEvent m){
				   btn_next.repaint();
				   ImageIcon iconlast = new ImageIcon("etc/icon/backgroundImg/next.png");
			       Image imglast=iconlast.getImage().getScaledInstance(btn_next.getWidth(),btn_next.getHeight(),Image.SCALE_DEFAULT);
			       iconlast = new ImageIcon(imglast);
			       btn_next.setIcon(iconlast);
			   }
		});
		btn_close.addMouseListener(new MouseAdapter() {
			   //鼠标移入
			   public void mouseEntered(MouseEvent e) {
				   btn_close.setForeground(Color.RED);
			   }
			   public void mouseExited(MouseEvent m){
				   btn_close.setForeground(Color.GRAY);
			   }
		});
		
		// 监听按钮换图
		btn_last.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				abp.index--;
				if (abp.index < 0) {
					abp.index = 3;
				}
				abp.repaint();
			}
		});

		btn_next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				abp.index++;
				abp.repaint();

			}
		});

		btn_close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				abf.close();
			}
		});
	}
}
