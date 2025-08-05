package com.hh.hhdb_admin.comp.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.laf.LafIdEnum;
import com.hh.frame.swingui.laf.LafMgr;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.comp.colsel.ColSelPanel;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TestColSelPanel {
	public static void main(String[] args) throws Exception {
		LangMgr.addLang(CsL.class);
		LafMgr.setLaf(LafIdEnum.FAST);
		ColSelPanel panel = new ColSelPanel();
		
		JdbcBean b = MgrTestUtil.getJdbcBean(DBTypeEnum.oracle);
        Connection conn= ConnUtil.getConn(b);
		
		
        Set<String> selCols = new HashSet<>();
        selCols.add("ID");
        selCols.add("NAME");
        
		panel.loadData(b, conn, "HU", "PERSON",selCols);
		
		HDialog frame   = new HDialog() {
			@Override
			protected void onConfirm() {
				List<String> ss = panel.getSelected();
				PopDlgUtil.info(getWindow(), String.join(",", ss));
			}
			@Override
			protected void onCancel() {
				super.onCancel();
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				dispose();
			}
			
		};
		frame.setDisposeOnHide(false);
		frame.setRootPanel(panel);
		frame.show();
		
		
		
	}
}
