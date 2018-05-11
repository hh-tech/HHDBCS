package com.hhdb.csadmin.plugin.about;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.common.bean.VersionBean;
import com.hhdb.csadmin.common.util.VersionUtil;
import com.hhdb.csadmin.plugin.menu.util.UIUtils;

public class AboutPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private FontMetrics fontMetrics;
	private Image offscreenImg;
	private Graphics offscreenGfx;
	private Color versionTextColour;
	//产品名字
	private String versionname;
//	private String message2;
	private Color progressColour;
	private Color gradientColour;
	//版本号
	private String version;
	private int PROGRESS_HEIGHT=5;
	private int progress;	
	//背景图
	private ImageIcon[] imgs;
	private int versionLabelXa;
	private int versionLabelYa;
	public int index;
	public int imgWidth;
	public int imgHight;
	
	public AboutPanel(Color progressBarColour, String imageResourcePath,String SecondImage, String thirdImage,String ForthImage,
			final Color versionTextColour, int versionLabelX, int versionLabelY){
		this.versionTextColour = versionTextColour;
		try {
			VersionBean versionbean =VersionUtil.readVersionFile();
			versionname="产品:"+versionbean.getName();
			version="当前版本:"+versionbean.getVersion();
		} catch (IOException e) {
			LM.error(LM.Model.CS.name(), e);
		}
		this.versionLabelXa=versionLabelX;
		this.versionLabelYa=versionLabelY;
		progressColour = progressBarColour;
		//指针转圈
//		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		setBackground(Color.white);

		gradientColour = UIUtils.getBrighter(progressBarColour, 0.75);

		Font font = new Font("SimSun", Font.BOLD, 15);
		
		setFont(font);
		fontMetrics = getFontMetrics(font);
		final ImageIcon[] imgs = {
                new ImageIcon(imageResourcePath),
                new ImageIcon(SecondImage),
                new ImageIcon(thirdImage),
                new ImageIcon(ForthImage),
            };
		
//		MediaTracker tracker = new MediaTracker(this);
		Image image =imgs[index%imgs.length].getImage();
		imgWidth = image.getWidth(this);
		imgHight = image.getHeight(this);
//		tracker.addImage(image, 0);
		
//		try {
//			tracker.waitForAll();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}		
		this.imgs=imgs;
	
	}
	@Override
	public  void paintComponent(Graphics g) {
		//添加背景图及背景文字
		Dimension size = getSize();
		offscreenImg =  createImage(size.width, size.height);
		offscreenGfx = offscreenImg.getGraphics();
		offscreenGfx.setFont(getFont());
		
		offscreenGfx.drawImage(imgs[index%imgs.length].getImage(), 0, 0, this);
		offscreenGfx.setColor(progressColour);
		Graphics2D offscreenGfx2d = (Graphics2D) offscreenGfx;
		offscreenGfx2d.setPaint(new GradientPaint(0, imgs[index%imgs.length].getImage().getHeight(this) - PROGRESS_HEIGHT, gradientColour,// new
				0, imgs[index%imgs.length].getImage().getHeight(this), progressColour));
		offscreenGfx.fillRect(0, imgs[index%imgs.length].getImage().getHeight(this) - PROGRESS_HEIGHT, (imgs[index%imgs.length].getImage().getWidth(this) * progress) / 9, PROGRESS_HEIGHT);
	
		if (version != null) {
	
			if (versionLabelXa == -1) {
				versionLabelXa = (getWidth() - fontMetrics.stringWidth(version)) / 2;
			}
	
			if (versionLabelYa == -1) {
				// if no y value - set just above progress bar
				versionLabelYa = imgs[index%imgs.length].getImage().getHeight(this) - PROGRESS_HEIGHT - fontMetrics.getHeight();
			}
	
			offscreenGfx2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	
			offscreenGfx2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	
			offscreenGfx2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	
			offscreenGfx.setColor(versionTextColour);
			offscreenGfx.drawString(versionname, versionLabelXa+5, versionLabelYa+17);
			offscreenGfx.drawString(version, versionLabelXa+5, versionLabelYa+42);
//			offscreenGfx.drawString(message2, versionLabelXa+5, versionLabelYa+67);
		}
		g.drawImage(offscreenImg, 0, 0, this);		
	}
}
