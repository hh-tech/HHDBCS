package com.hh.hhdb_admin;

import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lic.VerifyLicTool;
import com.hh.frame.swingui.engine.GuiEngine;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.splash.HSplash;
import com.hh.frame.swingui.view.splash.SplashTask;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.menubar.LicenseComp;
import com.hh.hhdb_admin.mgr.workspace.WorkSpaceComp;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HHdbAdmin {

	public static void main(String[] args) {
		try {
			StartUtil.setLocale(StartUtil.default_language);
			IconFileUtil.setIconBaseDir(new File(StartUtil.getEtcFile(), "icon"));
			SplashTask t1 = new SplashTask("加载配置···") {
				@Override
				public void todo() {
					try {
						File jsonFile = new File(StartUtil.getEtcFile(), "conf.json");
						String jStr = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
						JsonObject jObj = Json.parse(jStr).asObject();
						StartUtil.eng = new GuiEngine(CsMgrEnum.class, jObj);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			SplashTask t2 = new SplashTask("加载UI···") {
				@Override
				public void todo() {
					// 初始化自定义UI
					EventQueue.invokeLater(() -> {
						try {
							HHSwingUi.newSkin(StartUtil.getDefaultSkinClass());
						} catch (Exception e) {
							PopPaneUtil.error(e);
							e.printStackTrace();
						}
					});
				}
			};
			List<SplashTask> ts = new ArrayList<>();
			ImageIcon imgIcon = IconFileUtil.getIcon(new IconBean("", "splash.png", IconSizeEnum.OTHER));
			if (imgIcon == null) {
				return;
			}
			HSplash hsp = new HSplash(ts, imgIcon.getImage());
			SplashTask t3 = new SplashTask("加载界面···") {
				@Override
				public void todo() {
					try {
						VerifyLicTool vt = StartUtil.getVt();
						if (vt == null || vt.expired()) {
							hsp.dispose();
							new LicenseComp(true) {
								@Override
								protected void nextCallback() {
									showLogin(hsp);
								}
							};
						} else {
							showLogin(hsp);
						}

					} catch (Exception e) {
						e.printStackTrace();
						PopPaneUtil.error(e);
						hsp.dispose();
						new LicenseComp(true) {
							@Override
							protected void nextCallback() {
								showLogin(hsp);
							}
						};
					}
				}
			};

			ts.add(t1);
			ts.add(t2);
			ts.add(t3);

			hsp.show();
			hsp.dispose();

		} catch (Exception e1) {
			PopPaneUtil.error(e1);
			System.exit(0);
		}
	}

	private static void showLogin(HSplash hsp) {
		new WorkSpaceComp();
		hsp.dispose();
		StartUtil.eng.doPush(CsMgrEnum.LOGIN, GuiJsonUtil.toJsonCmd(LoginMgr.CMD_SHOW_LOGIN));
	}
}
