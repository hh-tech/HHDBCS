package com.hh.hhdb_admin.mgr.login.comp;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import javax.swing.ImageIcon;

import com.hh.hhdb_admin.comp.DBTypeComboBox;
import org.apache.commons.lang3.StringUtils;

import com.hh.frame.swingui.view.ctrl.SVGIcon;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.util.ImgUtil;
import com.hh.hhdb_admin.common.util.ExtDbTypeUtil;

class PkgDbTypeUtil {
	public static void addExtTypes(DBTypeComboBox box) {
		for(Entry<String, DBTypeBean> b:ExtDbTypeUtil.selfDefinedDbTypeMaps.entrySet()) {
			addTypeOption(b.getValue(),box);
		}
	}
	
	
	
	public static void  addTypeOption(DBTypeBean bean,DBTypeComboBox box) {
		ImageIcon icon = null;
		File iconFile = new File(ExtDbTypeUtil.ICON_FILE,bean.getLogo());
		if(!StringUtils.isBlank(bean.getLogo())) {
			if(bean.getLogo().endsWith("svg")) {
				try {
					if(iconFile.exists()) {
						icon = new SVGIcon(iconFile,null,16,16);
					}
				} catch (IOException e) {
				}
			}else {
				if(iconFile.exists()) {
					icon = ImgUtil.readImgIcon(iconFile);
				}
			}
		}
		
		if(icon!=null) {
			box.addOption(bean.getName(), bean.getName(),icon);
		}else {
			box.addOption(bean.getName(), bean.getName(),FaSolidEnum.DATABASE);
		}
		
	}
}
