package com.hhdb.csadmin.common.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import com.hhdb.csadmin.common.bean.VersionBean;
/**
 *获取所有版本信息
 * @author Administrator
 *
 */
public class VersionUtil {
	 public  static VersionBean readVersionFile() throws IOException{
		VersionBean versionbean = new VersionBean();
		versionbean.setName("恒辉数据库C/S管理工具");
		String encoding="UTF-8";
		//获取版本Id
        File file=new File("etc/version.txt");
        if(file.isFile() && file.exists()){ //判断文件是否存在
            InputStreamReader read = new InputStreamReader(
            new FileInputStream(file),encoding);//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
	            while((lineTxt = bufferedReader.readLine()) != null) {
	            	versionbean.setVersion(lineTxt);
	            }
	            read.close();
	    }
        return versionbean;
	 } 
}