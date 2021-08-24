package com.hh.hhdb_admin.test.lob_panel;

import java.io.IOException;

import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.lob_panel.LobEditor;

public class TestLobEditor {

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(() -> {
			try {
				// 初始化自定义UI
				HHSwingUi.init();
			} catch (Exception e) {
				e.printStackTrace();
			}
			HFrame hFrame = new HFrame(HFrame.MIDDLE_WIDTH);
			try {
				hFrame.setRootPanel(getPanel());
			} catch (IOException e) {
				e.printStackTrace();
			}
			hFrame.setTitle("显示测试");
			hFrame.setWindowTitle("二进制显示");
			hFrame.show();
		});
	}

	private static HPanel getPanel() throws IOException {
		HPanel panel = new HPanel();
		byte[] d = ClassLoadUtil.load2Bytes(TestLobViewer.class, "big.png");
		LobEditor viewer = new LobEditor(d);
		panel.setLastPanel(viewer.getPanel());
		return panel;
	}

}
