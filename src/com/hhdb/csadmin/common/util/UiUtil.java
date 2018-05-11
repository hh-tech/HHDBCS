package com.hhdb.csadmin.common.util;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class UiUtil {
	
	public static void setLookAndFeel() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
		String style = "";
//		if (System.getProperty("os.name").toUpperCase().startsWith("LINUX")) {
			//style = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
		style = "com.pagosoft.plaf.PgsLookAndFeel";
//		}else{
//			//style = UIManager.getSystemLookAndFeelClassName();
//			style = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
//		}
		UIManager.setLookAndFeel(style);
//		UIManager.setLookAndFeel(new PgsLookAndFeel());
	}
}
