package com.hh.hhdb_admin.mgr.login.comp;

import java.util.List;

import com.hh.frame.common.base.BaseConnBean;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.base.SshBean;
import com.hh.frame.ssh.jsch.SshAuthType;
import com.hh.frame.swingui.bg.conn_mgr.ConnFileMgrTool;
import com.hh.frame.swingui.bg.conn_mgr.data.ImportTool;

public class JdbcImportTool extends ImportTool {

	
	public JdbcImportTool(ConnFileMgrTool tool) {
		super(tool);
	}

	@Override
	protected BaseConnBean toBaseConnBean(List<String> list) {
		JdbcInfo info = new JdbcInfo();
		info.setName(list.get(1));
		info.setDbUrl(list.get(3));
		info.setClazz(list.get(4));
		info.setUser(list.get(5));
		info.setLockPass(PkgDataUtil.toBool(list.get(10)));
		info.setSys(PkgDataUtil.toBool(list.get(8)));
		info.setUseSshTunnel(PkgDataUtil.toBool(list.get(11)));
		info.setPassword(list.get(6));
		info.setSchema(list.get(7));
		info.setView(list.get(9)!=null?JdbcInfo.View.valueOf(list.get(9)):JdbcInfo.View.USER);
		if(info.isUseSshTunnel()) {
			SshBean bean = new SshBean();
			bean.setIp(list.get(12));
			bean.setPort(Integer.parseInt(list.get(13)));
			bean.setUser(list.get(14));
			bean.setAuthMode(SshAuthType.valueOf(list.get(16)));
			bean.setLockPass(PkgDataUtil.toBool(list.get(18)));
			if(bean.getAuthMode() == SshAuthType.KEY) {
				bean.setKey(list.get(17));
				bean.setKeyPass(list.get(15));
				bean.setKeyPass(bean.getKeyPass());
			}else {
				bean.setPassword(list.get(15));
				PkgDataUtil.decryptPass(bean);
			}
			
			info.setSshBean(bean);
			
			
		}
		PkgDataUtil.decryptPass(info);
		return info;
		
	}
	
}
