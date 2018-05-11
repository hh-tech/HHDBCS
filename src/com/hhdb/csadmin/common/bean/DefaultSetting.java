package com.hhdb.csadmin.common.bean;

import java.awt.Color;
import java.io.File;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.common.util.StringUtil;
import com.hhdb.csadmin.common.util.commonxmltool.NodeEntity;
import com.hhdb.csadmin.common.util.commonxmltool.XmlEnger;


public class DefaultSetting {
	/**
	 * textpane设置
	 * 
	 * @param background
	 *            背景颜色
	 * @param fontsize
	 *            字体大小
	 */
	public static void updateSettings(DefaultSet set) {
		try {
			XmlEnger e = new XmlEnger();
			e.setXmlFile(new File(StringUtil.getXmlPath("default.xml")));
			NodeEntity root = e.getRootEntity();
			NodeEntity fs = e.getNode("default.textpane.fontsize");
			NodeEntity bg = e.getNode("default.textpane.background");
			NodeEntity ln = e.getNode("default.textpane.linenumber");
			
			NodeEntity cmdbackcolor = e.getNode("default.cmdpane.backgroud");
			NodeEntity cmdfontcolor = e.getNode("default.cmdpane.fontcolor");
			if(set.getFontSize()!=null&&!"".equals(set.getFontSize())){
				fs.setText(set.getFontSize());
			}
			if(set.getBackground()!=null&&!"".equals(set.getBackground())){
				bg.setText(set.getBackground());
			}
			if(set.getLinunumber()!=null&&!"".equals(set.getLinunumber())){
				ln.setText(set.getLinunumber());
			}
			if(set.getCmdbackcolor()!=null&&!"".equals(set.getCmdbackcolor())){
				cmdbackcolor.setText(set.getCmdbackcolor());
				cmdfontcolor.setText(set.getCmdfontcolor());
			}
			e.generateXml(root,StringUtil.getXmlPath("default.xml"));
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		} 
	}
	
	public static void updateQkeySetting(DefaultSet set){
		try {
			XmlEnger e = new XmlEnger();
			e.setXmlFile(new File(StringUtil.getXmlPath("default.xml")));
			NodeEntity root = e.getRootEntity();
			NodeEntity guanjian = e.getNode("default.qkeys.guanjian");
			NodeEntity tablename = e.getNode("default.qkeys.tablename");
			NodeEntity viewname = e.getNode("default.qkeys.viewname");
			if(set.getQkeyguanjian()!=null&&!"".equals(set.getQkeyguanjian())){
				guanjian.setText(set.getQkeyguanjian());
			}
			if(set.getQkeytablename()!=null&&!"".equals(set.getQkeytablename())){
				tablename.setText(set.getQkeytablename());
			}
			if(set.getQkeyviewname()!=null&&!"".equals(set.getQkeyviewname())){
				viewname.setText(set.getQkeyviewname());
			}
			e.generateXml(root,StringUtil.getXmlPath("default.xml"));
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		} 
	}

	/**
	 * 加载 textpane 设置
	 * 
	 * @return
	 */
	public static DefaultSet loadFontSettings() {
		DefaultSet settings = new DefaultSet();
		try {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			XmlEnger e = new XmlEnger();
			File file=new File(URLDecoder.decode(StringUtil.getXmlPath("default.xml"),"utf-8"));
			e.setXmlFile(file);
			settings.setSqlkeys(p.matcher(e.getNode("default.sqlkeys").getText()).replaceAll(""));
			settings.setBackground(e.getNode("default.textpane.background").getText());
			settings.setLinunumber(e.getNode("default.textpane.linenumber").getText());
			settings.setFontSize(e.getNode("default.textpane.fontsize").getText());
			settings.setCmdbackcolor(e.getNode("default.cmdpane.backgroud").getText());
			settings.setCmdfontcolor(e.getNode("default.cmdpane.fontcolor").getText());
			
			settings.setQkeyguanjian(e.getNode("default.qkeys.guanjian").getText());
			settings.setQkeytablename(e.getNode("default.qkeys.tablename").getText());
			settings.setQkeyviewname(e.getNode("default.qkeys.viewname").getText());
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		} 
		return settings;
	}

	// 颜色格式化
	public static Color strToColor(String color) {
		String[] colors = color.split(",");
		return new Color(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]));
	}
}
