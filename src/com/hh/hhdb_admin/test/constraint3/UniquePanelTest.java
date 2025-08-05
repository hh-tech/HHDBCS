package com.hh.hhdb_admin.test.constraint3;

import java.io.IOException;
import java.sql.SQLException;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.constraint3.UniqueKeyPanel;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class UniquePanelTest {
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
		
		
		UniqueKeyPanel p = new UniqueKeyPanel(ConstraintMgrTest.SCHEMA, ConstraintMgrTest.TABLE);
		
		frame.setRootPanel(p);
		frame.show();
	}

}
