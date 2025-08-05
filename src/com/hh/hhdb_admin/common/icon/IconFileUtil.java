package com.hh.hhdb_admin.common.icon;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JobStatus;
import com.hh.frame.swingui.laf.LafIdEnum;
import com.hh.frame.swingui.laf.LafMgr;
import com.hh.frame.swingui.view.util.ImgUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IconFileUtil {
	public static File iconBaseDir = new File(CsVarUtil.getEtcFile(), "icon");
	private static Map<IconBean, ImageIcon> iconMap = new HashMap<>();
	public static final String DB_TYPE_CONTEXT = "shared/dbtype";
	public static final String STATUS_CONTEXT = "shared/status";
	public static final String ERROR_CONTEXT = "shared/has_error";
	public static final String PRODUCT_ICON = new File(iconBaseDir, "product_icon").getAbsolutePath();

//	public static void setIconBaseDir(File dir) {
//		iconBaseDir = dir;
//	}


	public static ImageIcon getIcon(IconBean iconBean) {
		// 如果map中已经存在直接从map中返回
		if (iconMap.containsKey(iconBean)) {
			return iconMap.get(iconBean);
		}
		File iconFile = new File(iconBaseDir, iconBean.toString());
		if (!iconFile.exists()) {
			iconBean.setIconType(ImgUtil.ImgType.PNG);
			iconFile = new File(iconBaseDir, iconBean.toString());
		}
		if (LafMgr.getCurrent() == LafIdEnum.DARK) {
			iconBean.setDark(true);
			iconFile = new File(iconBaseDir, iconBean.toString());
			if (!iconFile.exists()) {
				iconBean.setDark(false);
				iconFile = new File(iconBaseDir, iconBean.toString());
			}
		}
		if (iconFile.exists()) {
			ImageIcon icon;
			if (iconFile.getName().endsWith(ImgUtil.ImgType.SVG.name().toLowerCase())) {
				icon = ImgUtil.readSvgIcon(iconFile);
			} else {
				icon = ImgUtil.readImgIcon(iconFile);
			}
			iconMap.put(iconBean, icon);
			return icon;
		}
		return null;
	}
	
	public static List<String> getContextList() {
		List<String> names = new ArrayList<>();
		File[] files = iconBaseDir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					names.add(file.getName());
				}
			}
		}
		return names;
	}

	public static boolean existContext(IconBean iconBean) {
		File contextFile = new File(iconBaseDir, iconBean.getContext());
		return contextFile.exists() && contextFile.isDirectory();
	}

	public static ImageIcon getDbIcon(DBTypeEnum dbtype, IconSizeEnum size) {
		IconBean iconBean = new IconBean(DB_TYPE_CONTEXT, dbtype.name());
		iconBean.setSize(size);
		return getIcon(iconBean);
	}

	public static ImageIcon getDbIcon(DBTypeEnum dbtype) {
		IconBean iconBean = new IconBean(DB_TYPE_CONTEXT, dbtype.name());
		return getIcon(iconBean);
	}

	public static ImageIcon getStatusIcon(JobStatus jobStatus) {
		IconBean iconBean = new IconBean(STATUS_CONTEXT, jobStatus.name().toLowerCase());
		return getIcon(iconBean);
	}

	public static ImageIcon hasError(boolean hasErr) {
		IconBean iconBean = new IconBean(ERROR_CONTEXT, String.valueOf(hasErr));
		return getIcon(iconBean);
	}

	public static ImageIcon getLogo() {
		return ImgUtil.readImgIcon(new File(PRODUCT_ICON,"logo.png"));
	}
	
	public static ImageIcon getTheme() {
		File iconFile = new File("");
		File[] subFiles = new File(PRODUCT_ICON).listFiles();
		if (null!=subFiles){
			for (File subFile : subFiles) {
				if(subFile.isFile() && subFile.getName().startsWith("theme.")) iconFile = subFile;
			}
		}
		return ImgUtil.readImgIcon(iconFile);
	}
	
	public static void reset() {
		iconMap = new HashMap<>();
	}

	public static void main(String[] args) {
		iconBaseDir = new File("etc/icon");
		System.out.println(hasError(true));
	}


}
