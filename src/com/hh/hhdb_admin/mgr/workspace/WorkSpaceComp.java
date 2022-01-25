package com.hh.hhdb_admin.mgr.workspace;

import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author YuSai
 */
public class WorkSpaceComp {
    private static final String DOMAIN_NAME = WorkSpaceComp.class.getName();
    
    static {
        try {
            LangMgr2.loadMerge(WorkSpaceComp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public WorkSpaceComp() {
        initWorkSpaceData();
    }
    
    private void initWorkSpaceData() {
        try {
            File f = StartUtil.workspace;
            if (!f.exists()) { //判断工作空间父目录是否存在
                FileUtils.forceMkdir(f);
            } else {    //清除未使用的工作空间
                File[] files = f == null ? File.listRoots() : f.listFiles();
                for(File ff: files) {
                    if (ff.isDirectory()) {
                        if ( !WsTool.isTaken(ff) ) FileUtils.forceDelete(ff);
                    }
                }
            }
            
            //创建当前客户端的工作空间
            File file = new File(f, System.currentTimeMillis()+"");
            FileUtils.forceMkdir(file);
            if (file.exists()){
                //占用并保存当前客户端的工作空间目录
                WsTool wsTool = new WsTool(file);
                StartUtil.workspace =  wsTool.takeIt();
            } else {
                initWorkSpaceData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(e);
        }
    }
    
    static String getLang(String key) {
        LangMgr2.setDefaultLang(StartUtil.default_language);
        return LangMgr2.getValue(DOMAIN_NAME, key);
    }
}
