package com.hh.hhdb_admin.mgr.workspace;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;


public class WorkSpaceUtil {
    
    public static void initWorkSpaceData() {
        try {
            File f = CsVarUtil.workspace;
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
                CsVarUtil.workspace =  wsTool.takeIt();
            } else {
                initWorkSpaceData();
            }
        } catch (Exception e) {
        	Lg.error(WorkSpaceUtil.class, e);
            PopDlgUtil.error(new Exception(LangMgr.sLang(CsL.CREATION_FAILED)));
            System.exit(0);     //创建空间失败则停止启动
        }
    }
}
