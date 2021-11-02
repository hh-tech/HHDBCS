package com.hh.hhdb_admin.mgr.monitor;

import com.hh.frame.lang.LangMgr2;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;

import javax.swing.*;
import java.io.IOException;

/**
 * @author YuSai
 */
public class MonitorComp {

    private static final String DOMAIN_NAME = MonitorComp.class.getName();

    static {
        try {
            LangMgr2.loadMerge(MonitorComp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLang(String key) {
        LangMgr2.setDefaultLang(StartUtil.default_language);
        return LangMgr2.getValue(DOMAIN_NAME, key);
    }

    public ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.MONITOR.name(), name, IconSizeEnum.SIZE_16));
    }

}
