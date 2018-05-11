package com.hhdb.csadmin.plugin.menu.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;

public class UIUtils {
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
