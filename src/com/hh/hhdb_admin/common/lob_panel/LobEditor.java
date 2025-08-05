package com.hh.hhdb_admin.common.lob_panel;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fileChooser.HFileChooser;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * 从路径中选择文件，把文件展示在Viewer中
 * 支持从剪切板中加载文本或者图片
 *
 * @author Tony
 */
public class LobEditor extends LobViewer {
	private File localFile = null;
	
	public LobEditor(File file) throws IOException {
		this();
		byte[] bytes = FileUtils.readFileToByteArray(file);
		loadData(bytes);
		localFile = file;
	}
	
	public LobEditor(byte[] bytes) throws IOException {
		this();
		loadData(bytes);
	}
	
	public LobEditor() {
		textArea = new HTextArea();
		HButton openFileBtn = new HButton(LangMgr.lang(Word2.OPEN));
		openFileBtn.addActionListener(new OpenFileListener());
		toolBar.add(openFileBtn);
	}

	public void addToolBarBtn(HButton... buttons) {
		for (HButton button : buttons) {
			toolBar.add(button);
		}
	}

	protected class OpenFileListener implements ActionListener {
		private File file;
		@Override
		public void actionPerformed(ActionEvent e) {
			SwingUtilities.invokeLater(() -> {
				try {
					HFileChooser hfc = new HFileChooser(HFileChooser.FileType.BOTH);
					hfc.setDialogTitle(LangMgr.lang(Word2.CHOOSE));
					hfc.setApproveButtonText(LangMgr.lang(Word2.OK));
					if (fileType.equals(IMAGE)) {
						hfc.addFilter("Images (*.jpg,*.png,*.bmp,*.gif)", "jpg", "jpeg", "png", "bmp", "gif ");
					} else if (fileType.equals(TEXT)) {
						hfc.addFilter("脚本文件(txt|xml|html|sql)", "txt", "text", "sql", "xml", "htm", "html");
					}
					if ( hfc.showOpenDialog() == HFileChooser.APPROVE_OPTION){
						file = hfc.getSelectedFile();
						if (file != null && file.exists()) {
							byte[] bytes = FileUtils.readFileToByteArray(file);
							loadData(bytes);
							openCallback();
						}
					}
				} catch (IOException exception) {
					PopDlgUtil.error(exception);
				}
			});
		}
	}

	public File getLocalFile() {
		return localFile;
	}
	
	protected void openCallback() {
	
	}
}
