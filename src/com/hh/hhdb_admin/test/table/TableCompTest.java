package com.hh.hhdb_admin.test.table;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.table.TableComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Connection;

/**
 * @author oyx
 * @date 2020-9-25  15:02:07
 * @Description: 创建表
 */
public class TableCompTest {

	private static Connection conn;

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(() -> {
			HFrame hFrame = new HFrame(HFrame.LARGE_WIDTH);
			try {
				hFrame.getWindow().addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						ConnUtil.close(conn);
						super.windowClosing(e);
					}
				});

				JdbcBean jdbc = MgrTestUtil.getJdbcBean();
				HHSwingUi.init();
				IconFileUtil.setIconBaseDir(new File("etc/icon/"));

				conn = ConnUtil.getConn(jdbc);
				DBTypeEnum dbType = DriverUtil.getDbType(conn);
				TableComp panelCreate = new TableComp(conn, dbType);
				TableComp.jdbcBean = jdbc;
				TableComp.schemaName = jdbc.getSchema();
				LastPanel lastPanel = panelCreate.initComp();

				hFrame.setWindowTitle(dbType + "创建表测试");
				HPanel panel = new HPanel();
				panel.setLastPanel(lastPanel);
				hFrame.setRootPanel(panel);
				hFrame.show();
				panelCreate.genTableData();


			} catch (Exception e) {
				e.printStackTrace();
				PopPaneUtil.error(e.getMessage());
			}finally {

			}
		});
	}

	//临时使用
//		JdbcBean jdbcBean = new JdbcBean();
//		jdbcBean.setClazz("org.hhdbsql.Driver");
//		jdbcBean.setDbUrl("jdbc:hhdbsql://192.168.2.206:1432/hhdb");
//		jdbcBean.setSchema("test");
//		jdbcBean.setPassword("123456");
//		jdbcBean.setUser("oyx");

//		JdbcBean jdbcBean = new JdbcBean();
//		jdbcBean.setClazz("oracle.jdbc.driver.OracleDriver");
//		jdbcBean.setDbUrl("jdbc:oracle:thin:@192.168.2.191:1521:orcl");
//		jdbcBean.setSchema("OYX");
//		jdbcBean.setPassword("123456");
//		jdbcBean.setUser("OYX");

	//jdbc:mysql://47.113.191.24:3306/test?zeroDateTimeBehavior=CONVERT_TO_NULL&rewriteBatchedStatements=true&useCursorFetch=true&serverTimezone=Asia/Shanghai

//		JdbcBean jdbcBean = new JdbcBean();
//		jdbcBean.setClazz(DriverUtil.getDriverClass(DBTypeEnum.mysql));
//		jdbcBean.setDbUrl(DriverUtil.getDriverUrl(DBTypeEnum.mysql));
//		jdbcBean.setPassword("root");
//		jdbcBean.setUser("root");
//		jdbcBean.setSchema("test");

//		JdbcBean jdbcBean = new JdbcBean();
//		jdbcBean.setClazz(DriverUtil.getDriverClass(DBTypeEnum.pgsql));
//		jdbcBean.setDbUrl(DriverUtil.getDriverUrl(DBTypeEnum.pgsql));
//		jdbcBean.setPassword("123456");
//		jdbcBean.setUser("postgres");

//		StartUtil.db_type = DBTypeEnum.mysql;


}
