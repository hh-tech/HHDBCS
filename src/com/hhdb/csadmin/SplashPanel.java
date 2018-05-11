/*
 * SplashPanel.java
 *
 * Copyright (C) 2002-2013 Takis Diakoumis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.hhdb.csadmin;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;

/**
 * This class creates a splash panel to the size of the image to be displayed.
 * The panel is displayed for as long as is required to load required classes
 * and build the application frame and associated components.
 * 
 * @author Takis Diakoumis
 * @version $Revision: 160 $
 * @date $Date: 2013-02-08 21:15:04 +0800 (Fri, 08 Feb 2013) $
 */
public class SplashPanel extends Canvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** This object's font metrics */
	private FontMetrics fontMetrics;

	/** The window displayed */
	private Window window;

	/** The splash image */
	private Image image;

	/** The off-screen image */
	private Image offscreenImg;

	/** The off-screen graphics */
	private Graphics offscreenGfx;

	/** The startup progress posiiton */
	private int progress;

	/** The version info string */
	private String version;
	private String message1;
	private String message2;
	/** The progress bar's colour */
	private final Color progressColour;

	/** the light gradient colour */
	private final Color gradientColour;

	/** the x-coord of the version string */
	private int versionLabelX;

	/** the y-coord of the version string */
	private int versionLabelY;

	/** The progress bar height */
	private static final int PROGRESS_HEIGHT = 5;

	private final Color versionTextColour;

	/** Creates a new instance of the splash panel. */
	public SplashPanel(Color progressBarColour, String imageResourcePath, String versionNumber,String msg1,String msg2) {

		this(progressBarColour, imageResourcePath, versionNumber, msg1,msg2, -2, -2);
	}

	public SplashPanel(Color progressBarColour, String imageResourcePath, String versionNumber,String msg1,String msg2, int versionLabelX, int versionLabelY) {

		this(progressBarColour, imageResourcePath, versionNumber, msg1,msg2, Color.WHITE, versionLabelX, versionLabelY);
	}

	public SplashPanel(Color progressBarColour, String imageResourcePath, String versionNumber,String msg1,String msg2, Color versionTextColour, int versionLabelX, int versionLabelY) {

		this.versionTextColour = versionTextColour;
		this.versionLabelX = versionLabelX;
		this.versionLabelY = versionLabelY;
		this.message1=msg1;
		this.message2=msg2;
		progressColour = progressBarColour;
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		setBackground(Color.white);

		gradientColour = getBrighter(progressBarColour, 0.75);

		Font font = new Font("SimSun", Font.BOLD, 15);
		
		setFont(font);
		fontMetrics = getFontMetrics(font);
		image = getToolkit().getImage(getClass().getResource(imageResourcePath));

		MediaTracker tracker = new MediaTracker(this);
		tracker.addImage(image, 0);

		if (versionNumber != null) {
			version = versionNumber;
		}
		try {
			tracker.waitForAll();
		} catch (Exception e) {
			e.printStackTrace();
		}

		window = new Window(new Frame());

		Dimension size = new Dimension(image.getWidth(this), image.getHeight(this));
		window.setSize(size);
		window.setLayout(new BorderLayout());
		window.add(BorderLayout.CENTER, this);

		window.setLocation(getPointToCenter(window, size));

		window.validate();
		window.setVisible(true);
	}

	public synchronized void paint(Graphics g) {

		Dimension size = getSize();

		if (offscreenImg == null) {
			offscreenImg = createImage(size.width, size.height);
			offscreenGfx = offscreenImg.getGraphics();
			offscreenGfx.setFont(getFont());
		}

		offscreenGfx.drawImage(image, 0, 0, this);

		offscreenGfx.setColor(progressColour);

		Graphics2D offscreenGfx2d = (Graphics2D) offscreenGfx;

		offscreenGfx2d.setPaint(new GradientPaint(0, image.getHeight(this) - PROGRESS_HEIGHT, gradientColour,// new
																												// Color(95,95,190),
				0, image.getHeight(this), progressColour));

		offscreenGfx.fillRect(0, image.getHeight(this) - PROGRESS_HEIGHT, (window.getWidth() * progress) / 9, PROGRESS_HEIGHT);

		if (version != null) {

			if (versionLabelX == -1) {
				versionLabelX = (getWidth() - fontMetrics.stringWidth(version)) / 2;
			}

			if (versionLabelY == -1) {
				// if no y value - set just above progress bar
				versionLabelY = image.getHeight(this) - PROGRESS_HEIGHT - fontMetrics.getHeight();
			}

			offscreenGfx2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			offscreenGfx2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

			offscreenGfx2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			offscreenGfx.setColor(versionTextColour);
			offscreenGfx.drawString(version, versionLabelX, versionLabelY);
			offscreenGfx.drawString(message1, versionLabelX, versionLabelY+25);
			
			offscreenGfx.drawString(message2, versionLabelX, versionLabelY+50);
		}

		g.drawImage(offscreenImg, 0, 0, this);
		
		notify();
	}

	public void dispose() {
		window.dispose();
	}

	public void update(Graphics g) {
		paint(g);
	}

	public synchronized void advance() {

		progress++;
		repaint();

		try {
			wait();
		} catch (InterruptedException ie) {
		}

	}
	
	public static Color getBrighter(Color color, double factor) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		int i = (int) (1.0 / (1.0 - factor));
		if (r == 0 && g == 0 && b == 0) {
			return new Color(i, i, i);
		}
		if (r > 0 && r < i)
			r = i;
		if (g > 0 && g < i)
			g = i;
		if (b > 0 && b < i)
			b = i;
		return new Color(Math.min((int) (r / factor), 255), Math.min((int) (g / factor), 255), Math.min((int) (b / factor), 255));
	}
	
	public static Point getPointToCenter(Component component, Dimension dimension) {
		Dimension screenSize = getDefaultDeviceScreenSize();
		if (component == null) {
			if (dimension.height > screenSize.height) {
				dimension.height = screenSize.height;
			}
			if (dimension.width > screenSize.width) {
				dimension.width = screenSize.width;
			}
			return new Point((screenSize.width - dimension.width) / 2, (screenSize.height - dimension.height) / 2);
		}
		Dimension frameDim = component.getSize();
		Rectangle dRec = new Rectangle(component.getX(), component.getY(), (int) frameDim.getWidth(), (int) frameDim.getHeight());
		int dialogX = dRec.x + ((dRec.width - dimension.width) / 2);
		int dialogY = dRec.y + ((dRec.height - dimension.height) / 2);
		if (dialogX <= 0 || dialogY <= 0) {
			if (dimension.height > screenSize.height) {
				dimension.height = screenSize.height;
			}
			if (dimension.width > screenSize.width) {
				dimension.width = screenSize.width;
			}
			dialogX = (screenSize.width - dimension.width) / 2;
			dialogY = (screenSize.height - dimension.height) / 2;
		}
		return new Point(dialogX, dialogY);
	}
	
	public static Dimension getDefaultDeviceScreenSize() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getScreenDevices()[0];
		Dimension screenSize = gs.getDefaultConfiguration().getBounds().getSize();
		return screenSize;
	}
}
