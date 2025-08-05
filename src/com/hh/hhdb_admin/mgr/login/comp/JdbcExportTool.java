package com.hh.hhdb_admin.mgr.login.comp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.hh.frame.common.base.BaseConnBean;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.swingui.bg.conn_mgr.ConnFileMgrTool;
import com.hh.frame.swingui.bg.conn_mgr.data.ExportTool;

public class JdbcExportTool extends ExportTool {

	
	public JdbcExportTool(ConnFileMgrTool tool) {
		super(tool);
	}

	@Override
	public List<String> getHeaders() {
		List<String> fHeader = new ArrayList<String>();
		fHeader.add("全路径");
		fHeader.add("名称");
		fHeader.add("类型");
		fHeader.add("连接");
		fHeader.add("class");
		fHeader.add("用户");
		fHeader.add("密码");
		fHeader.add("模式");
		fHeader.add("是否SYS用户");
		fHeader.add("登录视图");
		fHeader.add("是否加密");
		fHeader.add("是否开启SSH通道");
		fHeader.add("SSH IP");
		fHeader.add("SSH 端口");
		fHeader.add("SSH 用户");
		fHeader.add("SSH 密码");
		fHeader.add("SSH 验证方式");
		fHeader.add("SSH 秘钥");
		fHeader.add("SSH 是否加密");
		return fHeader;
	}

	@Override
	public List<String> toStringList(String path, BaseConnBean bean) {
		List<String> d = new ArrayList<String>();
		d.add(path);
		JdbcInfo jBean = new JdbcInfo(bean.getJobj());
		d.add(jBean.getName());
		d.add(DriverUtil.getDbType(jBean).name());
		d.add(jBean.getDbUrl());
		d.add(jBean.getClazz());
		d.add(jBean.getUser());
		d.add(jBean.isLockPass()?"":jBean.getPassword());
		d.add(jBean.getSchema());
		d.add(PkgDataUtil.toShowBool(jBean.isSys()));
		d.add(jBean.getView().name());
		d.add(PkgDataUtil.toShowBool(jBean.isLockPass() ));
		d.add(PkgDataUtil.toShowBool(jBean.isUseSshTunnel()));
		
		if(jBean.isUseSshTunnel()) {
			d.add(jBean.getSshBean().getIp());
			d.add(jBean.getSshBean().getPort() + "");
			d.add(jBean.getSshBean().getUser());
			String pass = jBean.getSshBean().isLockPass()?"":jBean.getSshBean().getPassword();
			d.add(pass);
			d.add(jBean.getSshBean().getAuthMode().name());
			d.add(jBean.getSshBean().getKey());
			d.add(PkgDataUtil.toShowBool(jBean.getSshBean().isLockPass()));
		}else {
			d.add("");
			d.add("");
			d.add("");
			d.add("");
			d.add("");
			d.add("");
		}
		
		return d;
	}
	
	
	@Override
	protected LinkedHashMap<Integer, Integer> getSetColoumMap() {
		LinkedHashMap<Integer, Integer> setColumnMap = new LinkedHashMap<Integer, Integer>();
		setColumnMap.put(1, 30);
		setColumnMap.put(2, 20);
		setColumnMap.put(3, 20);
		setColumnMap.put(4, 20);
		setColumnMap.put(5, 20);
		setColumnMap.put(6, 20);
		setColumnMap.put(7, 20);
		setColumnMap.put(8, 20);
		setColumnMap.put(9, 20);
		setColumnMap.put(10, 20);
		setColumnMap.put(11, 20);
		setColumnMap.put(12, 20);
		setColumnMap.put(13,20);
		setColumnMap.put(14, 20);
		setColumnMap.put(15, 20);
		setColumnMap.put(16, 20);
		setColumnMap.put(17, 20);
		setColumnMap.put(18, 20);
		setColumnMap.put(19, 20);
		return setColumnMap;
	}
	
	


}
