package com.hhdb.csadmin.common.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class VmUtil {
	public static Set<String> getProNameByVmStr(String str){
		Set<String> proNames = new HashSet<String>();
		String[] flagstrs = StringUtils.splitByWholeSeparator(str,"${");
		if(flagstrs.length>=2){
			for(int i=1;i<flagstrs.length;i++){
				String fstr = flagstrs[i];
				if(fstr.indexOf("}")>0){
					String proname = fstr.substring(0,fstr.indexOf("}"));
					proNames.add(proname);
				}
			}
		}
		return proNames;
	}
}
