package com.hhdb.csadmin.plugin.menu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;



import org.apache.commons.lang3.StringUtils;

import com.hhdb.csadmin.plugin.cmd.console.CommonsHelper;
import com.hhdb.csadmin.plugin.cmd.console.EncodingDetector;

public class FileUtils {

	private FileUtils() {
	}

	public static File findFileOnClassPath(final String name) {
		final String classpath = CommonsHelper.getClassPath()+"/"+name;
		final String pathSeparator = System.getProperty("path.separator");
		final StringTokenizer tokenizer = new StringTokenizer(classpath, pathSeparator);
		while (tokenizer.hasMoreTokens()) {
			final String pathElement = tokenizer.nextToken();
			final File directoryOrJar = new File(pathElement);
			final File absoluteDirectoryOrJar = directoryOrJar.getAbsoluteFile();
			if (absoluteDirectoryOrJar.isFile()) {
				final File target = new File(absoluteDirectoryOrJar.getParent(), name);
				if (target.exists()) {
					return target;
				}
			} else {
				final File target = new File(directoryOrJar, name);
				if (target.exists()) {
					return target;
				}
			}
		}
		return null;
	}
	
	public static String loadFile(File file) throws IOException {
	    return loadFile(file, true);
	}
	
	 public static String loadFile(File file, boolean escapeLines) throws IOException {

	        FileReader fileReader = null;
	        BufferedReader reader = null;
	        
	        try {
	            fileReader = new FileReader(file);
	            reader = new BufferedReader(fileReader);
	            
	            String value = null;
	            StringBuilder sb = new StringBuilder();
	            
	            while ((value = reader.readLine()) != null) {
	                sb.append(value);
	                
	                if (escapeLines) {
	                    sb.append('\n');
	                }
	                
	            }

	            String charset = new EncodingDetector().detectCharset(file);
	            if (StringUtils.isNotBlank(charset)) {
	                
	                return new String(sb.toString().getBytes(), charset);
	            }
	            
	            return sb.toString();
	        
	        } finally {
	            if (reader != null) {
	                reader.close();
	            }
	            if (fileReader != null) {
	                fileReader.close();
	            }
	        }
	    }
}
