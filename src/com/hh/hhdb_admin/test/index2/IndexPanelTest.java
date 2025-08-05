package com.hh.hhdb_admin.test.index2;

import java.io.IOException;
import java.sql.SQLException;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.index2.IndexPanel;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class IndexPanelTest {
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
		IndexPanel p = new IndexPanel();
		p.init("MYTABLE", "HU");
		frame.setRootPanel(p);
		frame.show();
	}

}
