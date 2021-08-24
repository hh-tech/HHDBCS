package com.hh.hhdb_admin.common.lob_panel;

import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 从路径中选择文件，把文件展示在Viewer中
 * 支持从剪切板中加载文本或者图片
 *
 * @author Tony
 */
public class LobEditor extends LobViewer {
	private File localFile = null;
	/**
	 * 文件选择过滤器
	 */
	private List<FileFilter> extFilters;


	public LobEditor() {
		HButton openFileBtn = new HButton("打开");
		openFileBtn.addActionListener(new OpenFileListener());
		super.toolBar.add(openFileBtn);
	}

	public void addToolBarBtn(HButton... buttons) {
		for (HButton button : buttons) {
			super.toolBar.add(button);
		}
	}

	protected class OpenFileListener implements ActionListener {
		private File file;

		@Override
		public void actionPerformed(ActionEvent e) {
			SwingUtilities.invokeLater(() -> {
				try {
					JFileChooser fileChooser = getjFileChooser(file);
					if (extFilters != null && extFilters.size() > 0) {
						fileChooser.setFileFilter(null);
						fileChooser.setAcceptAllFileFilterUsed(false);
						for (FileFilter extFilter : extFilters) {
							fileChooser.addChoosableFileFilter(extFilter);
						}
						fileChooser.setAcceptAllFileFilterUsed(true);
					}
					fileChooser.setDialogTitle("选择文件");
					int value = fileChooser.showOpenDialog(getComp());
					if (value == JFileChooser.APPROVE_OPTION) {
						file = fileChooser.getSelectedFile();
						if (file != null && file.exists()) {
							byte[] bytes = FileUtils.readFileToByteArray(file);
							loadData(bytes);
							openCallback();
						}
					}
				} catch (IOException exception) {
					PopPaneUtil.error(exception);
					exception.printStackTrace();
				}
			});
		}
	}

	protected void openCallback() {

	}

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

	public File getLocalFile() {
		return localFile;
	}

	public void setExtFilters(List<FileFilter> extFilters) {
		this.extFilters = extFilters;
	}

	public void addFilters(FileFilter... filters) {
		if (extFilters == null) {
			extFilters = new ArrayList<>();
		}
		extFilters.addAll(Arrays.asList(filters));
	}
}
