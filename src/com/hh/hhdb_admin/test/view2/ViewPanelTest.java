package com.hh.hhdb_admin.test.view2;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.view2.ViewPanel;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.io.IOException;
import java.sql.SQLException;

public class ViewPanelTest {
	public static void main(String[] args) throws Exception {
		try {
			LangMgr.addLang(CsL.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		MgrTestUtil.initCsVarJdbc();
		HFrame frame = new HFrame(1000,600) {
			@Override
			public void dispose() {
				try {
					CsVarUtil.conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				super.dispose();
			}
		};
		ViewPanel p = new ViewPanel(CsVarUtil.jdbc,CsVarUtil.jdbc.getUser(),true);
		frame.setRootPanel(p);
		frame.show();
	}

}
