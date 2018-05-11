package com.hhdb.csadmin.common.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class IconUtilities {
	private static final String ICON_PATH = "/icon/";
	private static Map<String, ImageIcon> icons = new HashMap<String, ImageIcon>();

	public static ImageIcon loadImage(String name) {
		return new ImageIcon(IconUtilities.class.getResource(name));
	}

	public static ImageIcon loadIcon(String name) {
		return loadIcon(name, false);
	}

	public static ImageIcon loadIcon(String name, boolean store) {
		ImageIcon icon = null;
		if (icons.containsKey(name)) {
			icon = icons.get(name);
		} else {
			URL url = IconUtilities.class.getResource(name);
			if (url != null) {
				icon = new ImageIcon(url);
			} else {
				icon = loadDefaultIconResource(name, false);
			}
			if (store) {
				icons.put(name, icon);
			}
		}
		return icon;
	}

	public static ImageIcon loadDefaultIconResource(String name, boolean store) {
		ImageIcon icon = null;
		name = ICON_PATH + name;
		if (icons.containsKey(name)) {
			icon = icons.get(name);
		} else {
			URL url = IconUtilities.class.getResource(name);
			if (url == null) {
				throw new RuntimeException("Icon at resource path not found: " + name);
			}
			icon = new ImageIcon(url);
			if (store) {
				icons.put(name, icon);
			}
		}
		return icon;
	}

	private IconUtilities() {
	}
}
