package com.hh.hhdb_admin.test.table_open;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.hhdb_admin.mgr.table_open.ModifyTabDataComp;

import java.io.File;

/**
 * @author ouyangxu
 * @date 2021-01-05 0005 9:50:44
 */
public class ReadOnlyTableTest extends ModifyTabCompTest {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new OpenTable() {
			@Override
			protected void show(String tab, JdbcBean jdbcBean, File f, HFrame frame) throws Exception {
				String sql = String.format("select * from %s", tab);
				if (mcomp != null) {
					mcomp.loadReadOnlyTable(sql);
				} else {
					mcomp = new ModifyTabDataComp(jdbcBean, null, f);
					mcomp.loadReadOnlyTable(sql);
					HPanel panel = new HPanel();
					panel.setLastPanel(mcomp);
					frame.setRootPanel(panel);
					frame.show();
				}
			}
		});

	}
}
