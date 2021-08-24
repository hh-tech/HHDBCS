package com.hh.hhdb_admin.mgr.monitor;

import com.hh.frame.lang.LangMgr;
import com.hh.frame.lang.LangUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;

import javax.swing.*;

/**
 * @author YuSai
 */
public class MonitorComp {

    private static final String DOMAIN_NAME = MonitorComp.class.getName();

    static {
        LangMgr.merge(DOMAIN_NAME, LangUtil.loadLangRes(MonitorComp.class));
    }

    public String getLang(String key) {
        LangMgr.setDefaultLang(StartUtil.default_language);
        return LangMgr.getValue(DOMAIN_NAME, key);
    }

    public ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.MONITOR.name(), name, IconSizeEnum.SIZE_16));
    }

}
