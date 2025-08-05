package com.hh.hhdb_admin.test.lob_panel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.common.util.FileTypeUtil;
import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.lob_panel.LobViewer;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TestLobViewer {
	public static void main(String[] args) throws IOException {
		MgrTestUtil.initConfLang();
		CsVarUtil.parentFrame.setTitle("显示测试");
		CsVarUtil.parentFrame.setWindowTitle("二进制显示");
		MgrTestUtil.showComp(getPanel());
	}

	private static HPanel getPanel() throws IOException {
		HPanel panel = new HPanel();
		LastPanel lPanel = new LastPanel(false);
		LobViewer viewer = new LobViewer();
		lPanel.set(viewer.getComp());
		panel.setLastPanel(lPanel);
		HBarPanel tbar = new HBarPanel();
		HButton loadUtfText = new HButton("加载UTF-8文本") {
			@Override
			public void onClick() {
				byte[] data;
				try {
					data = genText().getBytes(StandardCharsets.UTF_8);
					viewer.loadData(data);
				} catch (Exception e) {
					PopDlgUtil.error(e);
				}
			}
		};

		HButton loadGbText = new HButton("加载GBK文本") {
			@Override
			public void onClick() {
				byte[] data;
				try {
					data = genText().getBytes("GBK");
					System.out.println(FileTypeUtil.getFileType(data));
					viewer.loadData(data);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		HButton loadJPGImgBtn = new HButton("加载JPG图片") {
			@Override
			public void onClick() {
				try {
					byte[] d = ClassLoadUtil.load2Bytes(TestLobViewer.class, "about1.jpg");
					viewer.loadData(d);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		HButton loadPngImgBtn = new HButton("加载PNG图片") {
			@Override
			public void onClick() {
				try {
					byte[] d = ClassLoadUtil.load2Bytes(TestLobViewer.class, "big.png");
					viewer.loadData(d);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		HButton loadPDFBtn = new HButton("加载PDF文件") {
			@Override
			public void onClick() {
				try {
					byte[] d = ClassLoadUtil.load2Bytes(TestLobViewer.class, "test.pdf");
					viewer.loadData(d);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		
		HButton loadWordBtn = new HButton("加载WORD文件") {
			@Override
			public void onClick() {
				try {
					byte[] d = ClassLoadUtil.load2Bytes(TestLobViewer.class, "test.docx");
					viewer.loadData(d);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		HButton loadUnknowBtn = new HButton("加载未知文件") {
			@Override
			public void onClick() {
				try {
					byte[] d = new byte[1024];
					Arrays.fill(d, (byte) -1);
					viewer.loadData(d);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		tbar.add(loadUtfText, loadGbText, loadJPGImgBtn, loadPngImgBtn, loadPDFBtn,loadWordBtn,loadUnknowBtn);

		panel.add(tbar);
		return panel;
	}

	private static String genText() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 100; i++) {
			if (i % 17 == 0) {
				sb.append(RandomUtil.getStr(getDict(), 700, 1000));
			} else {
				sb.append(RandomUtil.getStr(getDict(), 10, 100));
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	private static char[] getDict() {
		return "qwertyuiopasdfghjklzxcvbnm1234567890方法开始就调用编程十万个怎么办".toCharArray();
	}

}
