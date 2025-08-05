package com.hh.hhdb_admin.test.table_open3;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.hhdb_admin.mgr.table_open3.gui.OpenTableComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TableOpenCompTest {
	public static void main(String[] args) throws IOException {
		MgrTestUtil.initConfLang();
		HFrame frame = new HFrame();
		
		JdbcBean b = new JdbcBean();
		b.setDbUrl("jdbc:oracle:thin:@192.168.2.253:1521/orcl");
		b.setClazz("oracle.jdbc.driver.OracleDriver");
		b.setUser("HU");
		b.setPassword("123456");
		b.setSchema(b.getUser());
		OpenTableComp c = new OpenTableComp(b,"PERSON",new File("E:\\tmp"),false);
		frame.getWindow().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				
				super.windowClosing(e);
				c.close();
			}
		});
		frame.setRootPanel(c);
		frame.show();
		c.loadData();
		
		
//		c.loadSql("select id,name from person");
		
		
		
	}
}
