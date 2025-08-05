package com.hh.hhdb_admin.test.schema2;

import java.io.IOException;
import java.sql.SQLException;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.schema2.SchemaPanel;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class SchemaPanelTest {
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
					e.printStackTrace();
				}
				super.dispose();
			}
		};
		SchemaPanel p = new SchemaPanel();
		p.init(MgrTestUtil.getJdbcBean().getUser());
		frame.setRootPanel(p);
		frame.show();
	}

}
