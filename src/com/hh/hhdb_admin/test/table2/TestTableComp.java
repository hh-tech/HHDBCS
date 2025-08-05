package com.hh.hhdb_admin.test.table2;

import java.util.ArrayList;
import java.util.List;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.table2.comp.TableComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TestTableComp {

	public static void main(String[] args) throws Exception {
		MgrTestUtil.initConfLang();
		MgrTestUtil.initCsVarJdbc();
		List<String> dbtypes=new ArrayList<>();
		dbtypes.add(DBTypeEnum.oracle.name());
		dbtypes.add(DBTypeEnum.pgsql.name());
		SelectBox dbSel=new SelectBox(dbtypes) {
			@Override
			protected void onItemSelected(String value) {
				initCsvarJdbc(value);
			}
		};
		dbSel.setValue(DBTypeEnum.pgsql.name());
		initCsvarJdbc(dbSel.getValue());
		HBarPanel bar=new HBarPanel();
		bar.add(dbSel);
		bar.add(new HButton("建表") {
			public void onClick() {
				TableComp panelCreate = new TableComp(CsVarUtil.conn, CsVarUtil.jdbc,CsVarUtil.jdbc.getSchema());
				panelCreate.showDialog();
			}
		});
		
		CsVarUtil.parentFrame.setToolBar(bar);
		CsVarUtil.parentFrame.setWindowTitle("创建表测试");
		CsVarUtil.parentFrame.show();
	}
	
	private static void initCsvarJdbc(String value) {
		CsVarUtil.jdbc = new JdbcInfo(MgrTestUtil.getJdbcBean(DBTypeEnum.valueOf(value)).getJobj());
        try {
			CsVarUtil.conn= ConnUtil.getConn(CsVarUtil.jdbc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
