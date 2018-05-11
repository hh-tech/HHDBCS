package com.hhdb.csadmin.common.util;

import java.io.File;
import java.net.URISyntaxException;

public class StringUtil {
	public static String getRootPath() throws URISyntaxException{
		return ClassLoader.getSystemResource("").toURI().getPath();
	}
	
	public static String getIconPath(String iconName) throws URISyntaxException{
		return getRootPath()+File.pathSeparator+"icon"+File.pathSeparator+iconName;
	}
	
	public static String getXmlPath(String xmlName){
		return System.getProperty("user.dir")+"/etc/xml/"+xmlName;
	}
	public static String getProIcoPath(String icoName){
		return System.getProperty("user.dir")+"/etc/icon/"+icoName;
	}
}
