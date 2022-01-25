package com.hh.hhdb_admin.test.query;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.mgr.query.QueryMgr;
import com.hh.hhdb_admin.mgr.query.util.QuerUtil;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class QuerUtilTest {

	public static void main(String[] args) throws Exception {
		try {
            LangMgr2.loadMerge(QueryMgr.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
//		HHSwingUi.init();
		HHSwingUi.newSkin();

		HFrame frame = new HFrame();
		HDivLayout layout = new HDivLayout(20, 30, GridSplitEnum.C12);
		layout.setxBorderWidth(20);

		HPanel panel = new HPanel(layout);
		HButton button2 = new HButton() {
			@Override
			protected void onClick() {
				try {
					JdbcBean jdbc=MgrTestUtil.getJdbcBean();
					String sql = JOptionPane.showInputDialog(null,"输入sql（例如\\copy tabName to 'url' csv header;）：","");
					if(sql==null){
						return;
					}
					QuerUtil.copyStream(jdbc, sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		button2.setText("执行\\copy元子命令导入导出");
		panel.add(button2);

		frame.setRootPanel(panel);
		frame.show();
	}
}
