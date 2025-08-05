package com.hh.hhdb_admin.mgr.workspace;

import com.hh.frame.lang2.LangMgr;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.io.FileUtils;

import java.io.File;
class WsTool {
	private File file = null;
	public WsTool(File wsDirFile) {
		file=new File(wsDirFile,"csadmin.pid");
	}
    synchronized File takeIt() throws Exception {
    	File wsDirFile=file.getParentFile();
    	try {
    		if(isTaken(file)) {
        		throw new Exception(LangMgr.sLang(CsL.WORKSPACE_OCCUPIED));
        	}else {
        		if (!wsDirFile.exists()) {
                	FileUtils.forceMkdir(wsDirFile);
                }
        	}
		} catch (Exception e) {
			throw e;
		}
    	
    	new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                    FileUtils.write(file, System.currentTimeMillis() + "", "utf-8");
                } catch (Exception ignored) {
                	break;
                }
            }
        }).start();
    	return wsDirFile;
    }
    
    static boolean isTaken(File wsDirFile) {
        File  file = new File(wsDirFile,"csadmin.pid");
        if (!file.exists()) {
            return false;
        }
        try {
            Thread.sleep(1000);
            String timekey = FileUtils.readFileToString(file, "utf-8");
            long oldtimekey;
            try {
                oldtimekey = Long.parseLong(timekey);
            } catch (Exception e) {
                return false;
            }
            return System.currentTimeMillis() - oldtimekey < 1000;
        } catch (Exception e) {
            return true;
        }
    }

}
