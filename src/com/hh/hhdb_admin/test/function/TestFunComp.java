package com.hh.hhdb_admin.test.function;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.function.FunctionComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TestFunComp {

	public static void main(String[] args) throws Exception {
		MgrTestUtil.initConfLang();
		MgrTestUtil.initCsVarJdbc();
		String schema=CsVarUtil.jdbc.getSchema();
		
		HButton createBtn = new HButton("添加函数") {
			@Override
			protected void onClick() {
				try {
					FunctionComp fun = new FunctionComp(CsVarUtil.jdbc,CsVarUtil.conn,schema,"","",true);
					fun.addFun();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		HButton modifyBtn = new HButton("修改函数") {
			@Override
			protected void onClick() {
				try {
					String sql = null;
					FunctionComp fun = new FunctionComp(CsVarUtil.jdbc,CsVarUtil.conn,schema,"","",true);
					if (DriverUtil.getDbType(MgrTestUtil.getJdbcBean()) == DBTypeEnum.hhdb || DriverUtil.getDbType(MgrTestUtil.getJdbcBean()) == DBTypeEnum.pgsql
							|| DriverUtil.getDbType(MgrTestUtil.getJdbcBean()) == DBTypeEnum.db2) {
						sql=PopDlgUtil.nameInput("请输入函数名称与id或别名（,分隔）：", "");
					}else {
						sql=PopDlgUtil.nameInput("请输入函数名称：","");
					}
					if(sql==null) return;
					if (sql.contains(",")) {
						fun = new FunctionComp(CsVarUtil.jdbc,CsVarUtil.conn,schema,sql.split(",")[0],sql.split(",")[1],true);
						
					}else {
						fun = new FunctionComp(CsVarUtil.jdbc,CsVarUtil.conn,schema,sql,"",true);
					}
					fun.updFun();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		HBarPanel toolbar=new HBarPanel();
		toolbar.add(createBtn,modifyBtn);
		CsVarUtil.parentFrame.setToolBar(toolbar);
		CsVarUtil.parentFrame.show();

	}
}
