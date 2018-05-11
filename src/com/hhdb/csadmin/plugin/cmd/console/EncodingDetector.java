package com.hhdb.csadmin.plugin.cmd.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.mozilla.universalchardet.UniversalDetector;

public class EncodingDetector {

    public String detectCharset(File file) throws IOException {

        byte[] buf = new byte[4096];
        FileInputStream fis = new FileInputStream(file);        
        UniversalDetector detector = new UniversalDetector(null);

        try {
            
            int read = 0;
            while ((read = fis.read(buf)) > 0 && !detector.isDone()) {

                detector.handleData(buf, 0, read);
            }
            detector.dataEnd();

            String encoding = detector.getDetectedCharset();
            return encoding;
            
        } finally {

            detector.reset();
            if (fis != null) {

                fis.close();
            }
            
        }
    }

}


