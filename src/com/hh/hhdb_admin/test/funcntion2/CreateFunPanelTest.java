package com.hh.hhdb_admin.test.funcntion2;

import java.io.IOException;
import java.sql.SQLException;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.function2.CreatePanel;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class CreateFunPanelTest {
	public static void main(String[] args) throws Exception {
		
		try {
			LangMgr.addLang(CsL.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		MgrTestUtil.initCsVarJdbc();
		
		HFrame frame = new HFrame() {
			@Override
			public void dispose() {
				
				try {
					CsVarUtil.conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.dispose();
			}
		};
		CreatePanel p = new CreatePanel(true,FunctionMgrTest.SCHEMA) {
			@Override
			public void doNext() {
				PopDlgUtil.info(getComp(), genSql());
			}
		};
		frame.setRootPanel(p);
		frame.show();
	}

}
