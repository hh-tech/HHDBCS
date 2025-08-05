package com.hh.hhdb_admin.test.lob_panel;

import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.hhdb_admin.common.lob_panel.LobEditor;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.io.IOException;

public class TestLobEditor {

	public static void main(String[] args) throws IOException {

		MgrTestUtil.initConfLang();
		MgrTestUtil.showComp(getPanel());

	}

	private static HPanel getPanel() throws IOException {
		HPanel panel = new HPanel();
		byte[] d = ClassLoadUtil.load2Bytes(TestLobViewer.class, "big.png");
		LobEditor viewer = new LobEditor(d);
		panel.setLastPanel(viewer.getPanel());
		return panel;
	}

}
