package com.hh.hhdb_admin.test.menubar;

import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.tool.comp.SqlConversionComp;

import javax.swing.*;
import java.io.File;

/**
 * @author ouyangxu
 * @date 2021-12-09 0009 14:33:02
 */
public class SqkConversionTest {
	public static void main(String[] args) throws Exception {
		SwingUtilities.invokeLater(() -> {
			try {
				HHSwingUi.newSkin();
				IconFileUtil.setIconBaseDir(new File("etc/icon/"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			SqlConversionComp sqlConversionComp = new SqlConversionComp();

			HFrame frame = new HFrame(HFrame.LARGE_WIDTH);
			HPanel rootPanel = new HPanel();
			rootPanel.setLastPanel(sqlConversionComp);
			frame.setRootPanel(rootPanel);
			frame.show();
		});
	}

}
