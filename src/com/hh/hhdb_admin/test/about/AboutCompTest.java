package com.hh.hhdb_admin.test.about;

import java.io.File;

import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.about.AboutComp;

public class AboutCompTest {
    public static void main(String[] args) {
    	IconFileUtil.setIconBaseDir(new File("etc/icon/"));
        AboutComp aboutComp = new AboutComp();
        aboutComp.show();

    }
}
