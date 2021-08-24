package com.hh.hhdb_admin.common.icon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hh.frame.common.base.JobStatus;

import javax.swing.ImageIcon;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.swingui.view.util.ImgUtil;

public class IconFileUtil {
	private static File iconBaseDir = null;
	private static final Map<IconBean, ImageIcon> iconMap = new HashMap<>();
	public static final String LOGO_CONTEXT="shared/logo";
	public static final String DB_TYPE_CONTEXT="shared/dbtype";
	public static final String STATUS_CONTEXT="shared/status";
	public static final String ERROR_CONTEXT="shared/has_error";
	public static void setIconBaseDir(File dir) {
		iconBaseDir = dir;
	}

	public static ImageIcon getIcon(IconBean iconBean) {
		// 如果map中已经存在直接从map中返回
		if (iconMap.containsKey(iconBean)) {
			return iconMap.get(iconBean);
		}
		File iconFile = new File(iconBaseDir, iconBean.toString());
		if (iconFile.exists()) {
			ImageIcon icon = ImgUtil.readImgIcon(iconFile);
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
	
	public static ImageIcon getLogo(IconSizeEnum size) {
		IconBean iconBean=new IconBean(LOGO_CONTEXT,"logo");
		iconBean.setSize(size);
		return getIcon(iconBean);
	}
	
	public static ImageIcon getDbIcon(DBTypeEnum dbtype,IconSizeEnum size) {
		IconBean iconBean=new IconBean(DB_TYPE_CONTEXT,dbtype.name());
		iconBean.setSize(size);
		return getIcon(iconBean);
	}
	
	public static ImageIcon getDbIcon(DBTypeEnum dbtype) {
		IconBean iconBean=new IconBean(DB_TYPE_CONTEXT,dbtype.name());
		return getIcon(iconBean);
	}
	
	public static ImageIcon getStatusIcon(JobStatus jobStatus) {
		IconBean iconBean=new IconBean(STATUS_CONTEXT,jobStatus.name().toLowerCase());
		return getIcon(iconBean);
	}
	
	public static ImageIcon hasError(boolean hasErr) {
		IconBean iconBean=new IconBean(ERROR_CONTEXT,String.valueOf(hasErr));
		return getIcon(iconBean);
	}
		
	public static ImageIcon getLogo() {
		return getLogo(IconSizeEnum.SIZE_16);
	}
	
	public static void main(String[] args) {
		iconBaseDir=new File("etc/icon");
		System.out.println(hasError(true));
	}


}
