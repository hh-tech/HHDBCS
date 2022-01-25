package com.hh.hhdb_admin.test.query;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import org.apache.commons.io.FileUtils;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.dbquery.QueryTool;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.parser.PosBean;
import com.hh.frame.sqlwin.SqlWin;
import com.hh.frame.sqlwin.WinMgr;
import com.hh.frame.sqlwin.rs.WinRs;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.query.QueryComp;
import com.hh.hhdb_admin.mgr.query.QueryMgr;
import com.hh.hhdb_admin.mgr.query.ui.DataTab;
import com.hh.hhdb_admin.mgr.query.ui.OutputTabPanel;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class QuerCompTest {

	public static void main(String[] args) throws Exception {
		try {
            LangMgr2.loadMerge(QueryMgr.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
		IconFileUtil.setIconBaseDir(new File("etc/icon/"));
//		HHSwingUi.init();
		HHSwingUi.newSkin();

		HFrame frame = new HFrame();
		HDivLayout layout = new HDivLayout(20, 30, GridSplitEnum.C12);
		layout.setxBorderWidth(20);

		HPanel panel = new HPanel(layout);
		HButton createUsrPanel = new HButton() {
			@Override
			protected void onClick() {
				try {
					QueryComp que = new QueryComp(MgrTestUtil.getJdbcBean(),null);
					HDialog dialog = new HDialog(new HFrame(), 1000, 800) {
						@Override
						public void closeEvent() {
							que.close();
						}
					};
					HPanel hPanel = new HPanel();
					hPanel.setLastPanel(que.getLastPanel());
					dialog.setRootPanel(hPanel);
					dialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		createUsrPanel.setText("打开查询器");
		panel.add(createUsrPanel);
		HButton tabPanel = new HButton() {
			@Override
			protected void onClick() {
				try {
					String sql = JOptionPane.showInputDialog(null, "请输入sql：", "");
					if (sql == null) {
						return;
					}

					String id = "OQ_" + new Date().getTime();
					JdbcBean jdbc=MgrTestUtil.getJdbcBean();
					SqlWin sqlwin = WinMgr.newWin(jdbc, id);

					OutputTabPanel outputTab = new OutputTabPanel(sqlwin.getJdbc()) {
						@Override
						protected void highlighted(String title, Map<String, List<Integer>> resultMap) {
							System.out.println("显示对应行sql");
						}
					};
					HDialog dialog = new HDialog(new HFrame(), 1000, 800) {
						@Override
						public void closeEvent() {
							try {
								WinMgr.closeWin(id);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					HSplitPanel splitPane = new HSplitPanel(false);
					JSplitPane jsp = splitPane.getComp();
					jsp.setRightComponent(outputTab.getHTabPane().getComp());

					PosBean pos = new PosBean();
					pos.setBeginColumn(0);
					pos.setBeginLine(0);
					pos.setEndColumn(0);
					pos.setEndLine(0);
					WinRs rs = sqlwin.runSql(sql, pos);

					outputTab.showRs(rs.getRsMap(),30,"<NULL>");

					dialog.setRootPanel(splitPane);
					dialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		tabPanel.setText("打开选项卡面板");
		panel.add(tabPanel);
		HButton dataPanel = new HButton() {
			@Override
			protected void onClick() {
				try {
					File fele = new File(WinMgr.workDir, "OQ_" + new Date().getTime());
					String sql = JOptionPane.showInputDialog(null, "请输入单条查询sql：", "");
					if (sql == null) {
						return;
					}
					JdbcBean jdbc = MgrTestUtil.getJdbcBean();

					QueryTool queryTool = new QueryTool(ConnUtil.getConn(jdbc), sql, fele, 30);
					long start = System.currentTimeMillis();
					queryTool.first();

					HDialog dialog = new HDialog(new HFrame(), 1000, 800) {
						@Override
						public void closeEvent() {
							try {
								FileUtils.forceDelete(fele);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					HPanel hPanel = new HPanel();
					hPanel.setLastPanel(new DataTab(jdbc, queryTool,30,"<NULL>",System.currentTimeMillis()-start));
					dialog.setRootPanel(hPanel);
					dialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		dataPanel.setText("打开结果显示页");
		panel.add(dataPanel);

		frame.setRootPanel(panel);
		frame.show();
	}
}
