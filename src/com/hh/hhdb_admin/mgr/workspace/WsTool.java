package com.hh.hhdb_admin.mgr.workspace;

import java.io.File;
import org.apache.commons.io.FileUtils;
public class WsTool {
	private File file = null;
	public WsTool(File wsDirFile) {
		file=new File(wsDirFile,"csadmin.pid");
	}
    public synchronized File takeIt() throws Exception {
    	File wsDirFile=file.getParentFile();
    	try {
    		if(isTaken(file)) {
        		throw new Exception(WorkSpaceComp.getLang("workspaceOccupied"));
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
    
    public static boolean isTaken(File wsDirFile) {
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
