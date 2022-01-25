package com.hh.hhdb_admin.test.cmd;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.cmd.CmdComp;
import com.hh.hhdb_admin.mgr.cmd.CmdMgr;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.io.File;

public class TestCmdComp {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(() -> {
			try {
				LangMgr2.loadMerge(CmdMgr.class);
				IconFileUtil.setIconBaseDir(new File("etc/icon/"));
				// 初始化自定义UI
//				HHSwingUi.init();
				HHSwingUi.newSkin();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			HFrame hFrame = new HFrame(HFrame.MIDDLE_WIDTH);
			LastPanel lp = new LastPanel();
			lp.set(new CmdComp(MgrTestUtil.getJdbcBean(DBTypeEnum.oracle)).getComp());
			HPanel hp = new HPanel();
			hp.setLastPanel(lp);
			hFrame.setRootPanel(hp);
			hFrame.setTitle("测试窗口");
			hFrame.setWindowTitle("测试");
			hFrame.show();
		});
	}
}
