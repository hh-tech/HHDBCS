package com.hh.hhdb_admin.test.index2;

import java.io.IOException;
import java.sql.SQLException;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.index2.MongodbIndexPanel;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class MangoIndexPanelTest {
	public static void main(String[] args) throws Exception {
		
		try {
			LangMgr.addLang(CsL.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		CsVarUtil.jdbc=new JdbcInfo(MgrTestUtil.getJdbcBean(DBTypeEnum.mongodb).getJobj());
		CsVarUtil.conn=ConnUtil.getConn(CsVarUtil.jdbc);
		
		
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
		MongodbIndexPanel p = new MongodbIndexPanel("admin", "test");
		frame.setRootPanel(p);
		frame.show();
	}

}
