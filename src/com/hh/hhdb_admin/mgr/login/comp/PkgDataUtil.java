package com.hh.hhdb_admin.mgr.login.comp;

import com.hh.frame.common.base.BaseConnBean;
class PkgDataUtil {
	static boolean toBool(String bStr) {
		if (bStr != null && bStr.equals("是")) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	static void decryptPass(BaseConnBean bean) {
		if (!bean.isLockPass()) {
			bean.setPassword(bean.getPassword());
			bean.setLockPass(false);
		}else {
			bean.setPassword("");
			bean.setLockPass(false);
		}
	}
	
	
	static  String toShowBool(Boolean b){
		if(b==null) {
			return "否";
		}
		return b?"是":"否";
	}
}
