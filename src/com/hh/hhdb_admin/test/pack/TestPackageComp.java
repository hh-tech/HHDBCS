package com.hh.hhdb_admin.test.pack;

import com.hh.hhdb_admin.mgr.pack.PackageBean;
import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.pack.PackageComp;
import com.hh.hhdb_admin.test.MgrTestUtil;


public class TestPackageComp {

	public static void main(String[] args) throws Exception {

		MgrTestUtil.initConfLang();
		MgrTestUtil.initCsVarJdbc();

		if (CsVarUtil.getDbtype() != DBTypeEnum.oracle && CsVarUtil.getDbtype() != DBTypeEnum.dm) {
			PopDlgUtil.info("Package不支持" + CsVarUtil.getDbtype());
		}
		String schema=StringUtils.isBlank(CsVarUtil.jdbc.getSchema())?CsVarUtil.jdbc.getUser():CsVarUtil.jdbc.getSchema();
		schema=SqlStrUtil.rmQuote4Sel(schema, CsVarUtil.getDbtype(),null);
		HFrame frame = new HFrame();
		PackageBean packageBean = new PackageBean(PackageBean.OprType.ADD,null);
		packageBean.setSchema(schema);
		frame.setRootPanel(new PackageComp(CsVarUtil.jdbc,CsVarUtil.conn, packageBean).getAddPanel("test_package"));
		frame.show();
		frame.maximize();

	}
}
