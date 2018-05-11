package com.hhdb.csadmin;

import java.awt.Color;
import java.io.IOException;

import javax.swing.UnsupportedLookAndFeelException;

import org.apache.logging.log4j.Logger;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.base.IEventRoute;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.HHEventRoute;
import com.hhdb.csadmin.common.bean.VersionBean;
import com.hhdb.csadmin.common.util.UiUtil;
import com.hhdb.csadmin.common.util.VersionUtil;

public class HHAdmin {
	public static void main(String[] args){
		Logger csadminlogger = LM.newLogger("csadmin");
		LM.setLogger(LM.Model.CS.name(), csadminlogger);
		Logger dbobjlogger = LM.newLogger("dbobj");
		LM.setLogger(LM.Model.DB_OBJ.name(), dbobjlogger);
		Logger sqlutillogger = LM.newLogger("sqlutil");
		LM.setLogger(LM.Model.SQL_UTIL.name(), sqlutillogger);
		SplashPanel splash = createSplashPanel();
		for(int i=0;i<9;i++){
			splash.advance();
			try {
				Thread.sleep(110);
			} catch (InterruptedException e) {
				LM.error(LM.Model.CS.name(), e);
			}
		}
		splash.dispose();
		try {
			UiUtil.setLookAndFeel();
		} catch (ClassNotFoundException e) {
			LM.error(LM.Model.CS.name(), e);
		} catch (InstantiationException e) {
			LM.error(LM.Model.CS.name(), e);
		} catch (IllegalAccessException e) {
			LM.error(LM.Model.CS.name(), e);
		} catch (UnsupportedLookAndFeelException e) {
			LM.error(LM.Model.CS.name(), e);
		}
		IEventRoute eventRoute= new HHEventRoute();

		HHEvent loginEvent=new HHEvent("begin","com.hhdb.csadmin.plugin.login",EventTypeEnum.COMMON.name());
		eventRoute.processEvent(loginEvent);		
	}
	private static SplashPanel createSplashPanel() {
		VersionBean vb = new VersionBean();
		try {
			vb = VersionUtil.readVersionFile();
		} catch (IOException e) {
			LM.error(LM.Model.CS.name(), e);
		}
		return new SplashPanel(new Color(107, 148, 200), "/icon/splash.png", "产品："+vb.getName(),"版本："+vb.getVersion(),"", new Color(107, 107, 107), 70, 210);
	}
}
