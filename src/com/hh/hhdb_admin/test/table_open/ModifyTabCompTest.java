package com.hh.hhdb_admin.test.table_open;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.table.base.AbsTableObjFun;
import com.hh.frame.create_dbobj.table.comm.CreateTableUtil;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.ComboTextInput;
import com.hh.frame.swingui.view.input.WithLabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.table_open.ModifyTabDataComp;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Connection;
import java.util.List;

public class ModifyTabCompTest {

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new OpenTable() {
			@Override
			protected void show(String tab, JdbcBean jdbcBean, File f, HFrame frame) throws Exception {
				super.show(tab, jdbcBean, f, frame);
			}
		});

	}

}

class OpenTable implements Runnable {
	Connection connection;
	ModifyTabDataComp mcomp = null;
	HPanel headPanel;

	@Override
	public void run() {
		try {
			JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
			connection = ConnUtil.getConn(jdbcBean);
			try {
				HHSwingUi.init();
				IconFileUtil.setIconBaseDir(new File("etc/icon/"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			HFrame frame = new HFrame(HFrame.LARGE_WIDTH);
			headPanel = new HPanel(new HDivLayout(GridSplitEnum.C10));
			((JPanel) headPanel.getComp()).setBorder(BorderFactory.createEmptyBorder(10, 2, 10, 2));
			File f = new File("tmp");
			FileUtils.deleteQuietly(f);
			FileUtils.forceMkdir(f);

			ComboTextInput txtInput = new ComboTextInput("tab_name");
			AbsTableObjFun tableObjFun = CreateTableUtil.getDateType(DriverUtil.getDbType(connection));
			List<String> allTables = tableObjFun.getAllTables(connection, jdbcBean.getSchema());
			txtInput.addItem(allTables);
			HButton btn = new HButton("查询") {
				@Override
				public void onClick() {
					try {
						String tab = txtInput.getValue();
						show(tab, jdbcBean, f, frame);
						((JPanel) frame.getRootPanel().getComp()).updateUI();

					} catch (Exception e) {
						e.printStackTrace();
						PopPaneUtil.error(e.getMessage());
					}

				}
			};

			WithLabelInput input = new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C1)), "表名", txtInput);
			headPanel.add(input);
			headPanel.add(btn);

			frame.setRootPanel(headPanel);
			frame.show();
			frame.getWindow().addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					if (mcomp != null) {
						mcomp.close();
					}
					ConnUtil.close(connection);
					super.windowClosing(e);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			PopPaneUtil.error(e);
		}
	}

	protected void show(String tab, JdbcBean jdbcBean, File f, HFrame frame) throws Exception {
		if (mcomp != null) {
			mcomp.refreshTab(tab);
		} else {
			mcomp = new ModifyTabDataComp(jdbcBean, tab, f);
			mcomp.refreshTab();
			HPanel panel = new HPanel();
			panel.add(headPanel);
			panel.setLastPanel(mcomp);
			frame.setRootPanel(panel);
			frame.show();
		}
	}
}


