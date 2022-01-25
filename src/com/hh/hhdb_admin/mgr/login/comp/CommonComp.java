package com.hh.hhdb_admin.mgr.login.comp;

import com.hh.frame.lang.LangMgr2;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;

import javax.swing.*;
import java.io.IOException;

public class CommonComp {

    public static final String NAME = "name";
    public static final String VIEW = "view";
    public static final String TYPE = "type";
    public static final String CONNECT = "connect";
    public static final String USERNAME = "username";
    public static final String ENCRYPTED = "encrypted";
    public static final String PASSWORD = "password";
    public static final String SCHEMA = "schema";
    public static final String USER_TYPE = "userType";

    public static final String ENABLED = "enabled";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String SSH_USERNAME = "sshUsername";
    public static final String SSH_TYPE = "sshType";
    public static final String SSH_ENCRYPTED = "sshEncrypted";
    public static final String SSH_PASSWORD = "sshPassword";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PRIVATE_PASSWORD = "privatePassword";

    static {
        try {
            LangMgr2.loadMerge(CommonComp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final String DOMAIN_NAME = CommonComp.class.getName();

    public static String getLang(String key) {
        LangMgr2.setDefaultLang(StartUtil.default_language);
        return LangMgr2.getValue(DOMAIN_NAME, key);
    }

    public static ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.LOGIN.name(), name, IconSizeEnum.SIZE_16));
    }

}
