package com.hh.hhdb_admin.common.lob_panel;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.mgr.table_open.common.EncodingDetect;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabDataUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.regex.Pattern;

public class LobViewer extends AbsHComp {
	private final LastPanel panel = new LastPanel(false);
	private final JLabel labelStatus = new JLabel();
	private byte[] data = null;
	private final Pattern pattern = Pattern.compile("[0-9a-zA-Z~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"\\s\\t\\r\\n]+");
	//	private
	protected HButton exportBtn;

	protected HBarPanel toolBar;

	public final static String UNKNOWN = "未知";
	public final static String NULL = "空";

	public final static String IMAGE = "IMAGE";
	public final static String TEXT = "text";


	public static final String IMAGE_JPG = "jpg";
	public static final String IMAGE_GIF = "gif";
	public static final String IMAGE_PNG = " png";
	public static final String IMAGE_BMP = "bmp";
	/**
	 * (xml)，文件头：3C3F786D6C
	 */
	public static final String TYPE_XML = "xml";
	/**
	 * HTML(html)，文件头：68746D6C3E
	 */
	public static final String TYPE_HTML = "html";
	/**
	 * ZIP Archive (zip)，文件头：504B0304
	 */
	public static final String TYPE_ZIP = "zip";

	/**
	 * RAR Archive (rar)，文件头：52617221
	 */
	public static final String TYPE_RAR = "rar";

	/**
	 * /**
	 * Adobe Acrobat (pdf)，文件头：255044462D312E
	 */
	public static final String TYPE_PDF = "pdf";


	protected HTextArea textArea;
	protected ImageIcon image;
	protected String code;

	protected boolean edit = false;
	protected boolean readOnly = false;


	public LobViewer() {
		this.comp = panel.getComp();
		textArea = new HTextArea(false, false);
		init();
	}

	private void init() {
		HBarLayout barLayout = new HBarLayout();
		barLayout.setAlign(AlignEnum.LEFT);
		barLayout.setTopHeight(5);
		barLayout.setBottomHeight(5);

		toolBar = new HBarPanel(barLayout);
		panel.setFoot(labelStatus);
		panel.setHead(toolBar.getComp());
		exportBtn = new HButton("导出");
		exportBtn.addActionListener(new ExportListener());
		toolBar.add(exportBtn);

	}

	public void loadData(byte[] data) throws IOException {
		this.data = data;
		exportBtn.setEnabled(true);
		String type = genType();
		load(data, type);
	}

	public void load(byte[] data, String type) throws IOException {
		this.data = data;
		String encode = data == null ? NULL : code;

		if (!type.equals(NULL) && data != null) {
			try (ByteArrayInputStream stream = new ByteArrayInputStream(data);
				 BufferedReader reader = new BufferedReader(new InputStreamReader(stream, EncodingDetect.getFileEncode(data)))) {
				textArea.read(reader);
			}
		} else {
			textArea.setText("");
		}
		if (data == null) {
			type = NULL;
		}
		changeType(type);
		labelStatus.setText("大小:" + ModifyTabDataUtil.formatSize(getSize(data)) + " | 类型(编码) :" + (encode == null ? type : encode));
		panel.updateUI();
	}

	public void changeType(String type) throws IOException {
		if (data == null || data.length == 0) {
//			type = NULL;
			edit = true;
		}
		switch (type) {
			case TEXT: {
				panel.set(textArea.getComp());
				break;
			}
			case IMAGE: {
				image = getIcon("error");
				BufferedImage read = data == null ? null : ImageIO.read(new ByteArrayInputStream(data));
				if (read != null) {
					image = new ImageIcon(read);
					panel.setWithScroll(getImgLabel(image));
				} else {
					panel.setWithScroll(getImgLabel(image, "解析图片失败!"));
				}

				break;
			}
			case NULL: {
				image = getIcon("error");
				panel.set(textArea.getComp());
				exportBtn.setEnabled(false);
				break;
			}
			case UNKNOWN: {
				ImageIcon icon = getIcon("unknown");
				panel.setWithScroll(getImgLabel(icon, "未知类型"));
				break;
			}
			default:
		}
		textArea.setEditable(!readOnly && edit);
		((JPanel) panel.getComp()).updateUI();
	}

	public LastPanel getPanel() {
		return panel;
	}

	public byte[] getData() {
		return data;
	}

	public String genType() {
		if (data == null || data.length <= 0) {
			return NULL;
		}

		boolean isUtf8 = new String(data).matches(String.valueOf(pattern));
		if (isUtf8) {
			code = EncodingDetect.UTF_8;
			return TEXT;
		}

		String type = genType(data);
		switch (type) {
			case IMAGE_JPG:
			case IMAGE_GIF:
			case IMAGE_PNG:
			case IMAGE_BMP:
				code = type;
				return IMAGE;
			default:
				break;
		}
		code = EncodingDetect.getFileEncode(data);
		boolean contains = EncodingDetect.getUsedCode().contains(code);
		if (contains) {
			edit = true;
			return TEXT;
		}

		return UNKNOWN;
	}

	public int getSize(byte[] data) {
		if (data != null) {
			return data.length;
		}
		return 0;
	}

	public HBarPanel getToolBar() {
		return toolBar;
	}

	private JLabel getImgLabel(ImageIcon icon) {
		return getImgLabel(icon, null);
	}

	private JLabel getImgLabel(ImageIcon icon, String text) {
		JLabel imgLabel = new JLabel();
		imgLabel.setVerticalAlignment(JLabel.CENTER);
		imgLabel.setHorizontalAlignment(JLabel.CENTER);
		imgLabel.setIcon(icon);
		imgLabel.setSize(icon.getIconWidth(), icon.getIconHeight());
		if (StringUtils.isNoneBlank(text)) {
			imgLabel.setText(text);
		}
		return imgLabel;
	}

	/**
	 * 根据文件流判断文件类型
	 *
	 * @return jpg/png/gif/bmp
	 */
	public static String genType(byte[] data) {
		//读取文件的前几个字节来判断图片格式
		if (data == null) {
			return UNKNOWN;
		}
		int min = Math.min(data.length, 6);
		byte[] b = new byte[min];
		System.arraycopy(data, 0, b, 0, min);
		String s = ModifyTabDataUtil.bytesToHexString(b);
		if (StringUtils.isBlank(s)) {
			return UNKNOWN;
		}
		String type = s.toUpperCase();
		if (type.contains("FFD8FF")) {
			return IMAGE_JPG;
		} else if (type.contains("89504E47")) {
			return IMAGE_PNG;
		} else if (type.contains("47494638")) {
			return IMAGE_GIF;
		} else if (type.contains("424D")) {
			return IMAGE_BMP;
		} else if (type.contains("3C3F786D6C")) {
			return TYPE_XML;
		} else if (type.contains("68746D6C3E")) {
			return TYPE_HTML;
		} else if (type.contains("504B0304")) {
			return TYPE_ZIP;
		} else if (type.contains("52617221")) {
			return TYPE_RAR;
		} else if (type.contains("504446")) {
			return TYPE_PDF;
		} else {
			return UNKNOWN;
		}
	}


	public static String checkImageType(byte[] imageBytes) {
		if (imageBytes == null) {
			return NULL;
		}
		ByteArrayInputStream bais;
		MemoryCacheImageInputStream mcis;
		bais = new ByteArrayInputStream(imageBytes);
		mcis = new MemoryCacheImageInputStream(bais);
		Iterator<ImageReader> itr = ImageIO.getImageReaders(mcis);
		while (itr.hasNext()) {
			ImageReader reader = itr.next();
			if (reader != null) {
				return IMAGE;
			}
		}
		return UNKNOWN;
	}

	private class ExportListener implements ActionListener {
		private File file;

		@Override
		public void actionPerformed(ActionEvent e) {
			SwingUtilities.invokeLater(() -> {
				try {
					JFileChooser fileChooser = getjFileChooser(file);
					fileChooser.setDialogTitle("导出文件");
					int value = fileChooser.showSaveDialog(getComp());
					if (value == JFileChooser.APPROVE_OPTION) {
						file = fileChooser.getSelectedFile();
						if (file != null) {
							if (!file.exists()) {
								file.createNewFile();
								FileUtils.writeByteArrayToFile(file, data, false);
								PopPaneUtil.info("导出成功");
							} else {
								PopPaneUtil.error(String.format("文件 : %s 已存在!", file.getAbsolutePath()));
							}
						}
					}
				} catch (IOException exception) {
					PopPaneUtil.error(exception);
					exception.printStackTrace();
				}
			});
		}


	}


	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public HButton getExportBtn() {
		return exportBtn;
	}

	public HTextArea getTextArea() {
		return textArea;
	}

	public String getCode() {
		return code;
	}

	/**
	 * @param name
	 * @return
	 */
	private ImageIcon getIcon(String name) {
		return IconFileUtil.getIcon(new IconBean("lob", name, IconSizeEnum.SIZE_128));
	}

	protected JFileChooser getjFileChooser(File file) {
		JFileChooser fileChooser = new JFileChooser() {
			private static final long serialVersionUID = 1L;

			@Override
			public JDialog createDialog(Component parent) {
				JDialog dialog = super.createDialog(parent);
				dialog.setMinimumSize(new Dimension(850, 750));
				return dialog;
			}
		};
		if (file != null) {
			fileChooser.setCurrentDirectory(file.getParentFile());
		} else {
			FileSystemView fsv = FileSystemView.getFileSystemView();
			fileChooser.setCurrentDirectory(fsv.getDefaultDirectory());
		}
		return fileChooser;
	}
}
