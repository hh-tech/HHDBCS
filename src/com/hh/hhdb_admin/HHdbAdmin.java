package com.hh.hhdb_admin;

import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lic.VerifyLicTool;
import com.hh.frame.swingui.engine.GuiEngine;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.splash.HSplash;
import com.hh.frame.swingui.view.splash.SplashTask;
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
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HHdbAdmin {

    public static void main(String[] args) {
        try {
            StartUtil.setLocale(StartUtil.default_language);
            IconFileUtil.setIconBaseDir(new File(StartUtil.getEtcFile(),"icon"));
            VerifyLicTool vt = StartUtil.getVt();
            if (vt == null || vt.expired()) {
                new LicenseComp(true);
            }
            WorkSpaceComp wscomp = new WorkSpaceComp();
            if (!wscomp.noPop()) {
                wscomp.show();
            }
            SplashTask t1 = new SplashTask("加载中···") {
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
            List<SplashTask> ts = new ArrayList<>();
            ts.add(t1);
            ImageIcon imgIcon = IconFileUtil.getIcon(new IconBean("", "splash.png", IconSizeEnum.OTHER));
            if (imgIcon == null) {
                return;
            }
            HSplash hsp = new HSplash(ts, imgIcon.getImage());
            hsp.show();
            hsp.dispose();
            StartUtil.eng.doPush(CsMgrEnum.LOGIN, GuiJsonUtil.toJsonCmd(LoginMgr.CMD_SHOW_LOGIN));
        } catch (Exception e1) {
            e1.printStackTrace();
            PopPaneUtil.error(e1);
        }
    }
}
