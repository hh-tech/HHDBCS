package com.hh.hhdb_admin.common.lob_panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.hh.frame.chardet.ChardetUtil;
import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.filetype.FileTypeEnum;
import com.hh.frame.common.filetype.FileTypes;
import com.hh.frame.common.util.FileTypeUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fileChooser.HFileChooser;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;

public class LobViewer extends AbsHComp {
	private LastPanel panel;
	private final JLabel labelStatus = new JLabel();
	private byte[] data = null;
	// private
	protected HButton exportBtn;

	protected HBarPanel toolBar;

	public static final String UNKNOWN = "其它";
	public static final String NULL = "空";

	public static final String IMAGE = "IMAGE";
	public static final String TEXT = "TEXT";

	public String fileType = TEXT; // 选择的类型
	protected HTextArea textArea = new HTextArea();
	protected ImageIcon image;
	protected String code = "UTF-8";
	private String checkCode;
	private FileTypeEnum orignType;
	protected boolean edit = false;
	protected boolean readOnly = false;

	public LobViewer() {
		HBarLayout barLayout = new HBarLayout();
		barLayout.setAlign(AlignEnum.LEFT);
		barLayout.setTopHeight(5);
		barLayout.setBottomHeight(5);
		toolBar = new HBarPanel(barLayout);
		exportBtn = new HButton(LangMgr.lang(Word2.EXPORT));
		exportBtn.addActionListener(new ExportListener());
		toolBar.add(exportBtn);

		panel = new LastPanel(false);
		panel.setFoot(labelStatus);
		panel.setHead(toolBar.getComp());
		this.comp = panel.getComp();
	}

	public void loadData(byte[] data) throws IOException {
//		System.out.println("loadData2:" + Arrays.toString(data));
		this.data = data;
		exportBtn.setEnabled(true);
		orignType = FileTypeUtil.getFileType(data);
		
		String type = fileType = genType();
		load(data, type);
	}

	public void load(byte[] data, String type) throws IOException {
		this.data = data;

		
		orignType = FileTypeUtil.getFileType(data);
		if (!type.equals(NULL) && data != null) {
			try (ByteArrayInputStream stream = new ByteArrayInputStream(data);
					BufferedReader reader = new BufferedReader(new InputStreamReader(stream, code))) {
				edit = getUsedCode().contains(code);
				textArea.read(reader);
			}
		} else {
			edit = getUsedCode().contains(code);
			textArea.setText("");
		}
		if (data == null) {
			type = NULL;
		}

		changeType(type);

		String typeLabelValue = "";

		switch (type) {
		case TEXT:

			typeLabelValue = String.format("%s(%s)", type, code);
			break;
		case NULL:
			typeLabelValue = String.format("%s", type);
			break;

		default:
			typeLabelValue = String.format("%s(%s)", type, orignType);
			break;
		}
//		labelStatus.setText(LangMgr.lang(Word2.SIZES) + ":" + SelectTableSqlUtil.formatSize(getSize(data)) + " | "
		labelStatus.setText(LangMgr.lang(Word2.SIZES) + ":" + FileUtils.byteCountToDisplaySize(getSize(data)) + " | "
				+ LangMgr.lang(Word2.TYPE) + "(" + LangMgr.lang(Word2.CHARSET) + ") :" + typeLabelValue);
		panel.updateUI();
	}

	public void changeType(String type) throws IOException {
		if (data == null || data.length == 0) {
			edit = true;
		}

		
		switch (type) {
		case TEXT: {

			panel.set(textArea.getComp());

			break;
		}
		case IMAGE: {
			code = null;
			image = getIcon("error");
			BufferedImage read = data == null ? null : ImageIO.read(new ByteArrayInputStream(data));
			if (read != null) {
				image = new ImageIcon(read);
				panel.setWithScroll(getImgLabel(image));
			} else {
				panel.setWithScroll(getImgLabel(image, LangMgr.sLang(CsL.PICTURE_PARSING_FAILED)));
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
			code = null;
			ImageIcon icon = getIcon("unknown");
			panel.setWithScroll(getImgLabel(icon, LangMgr.lang(Word2.OTHER) + LangMgr.lang(Word2.TYPE)));
			break;
		}
		default:
		}

		textArea.getRSyntaxTextArea().setEditable(!readOnly && edit);
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

		try {
			code = checkCode = ChardetUtil.detectCharset(data);
		} catch (Exception e) {
			code = StandardCharsets.UTF_8.name();
		}
		
		if (orignType == null) {
			orignType = FileTypeUtil.getFileType(data);
		}
		
		return toShowType(orignType);

	}

	private String toShowType(FileTypeEnum types) {
		switch (types) {
		case GIF:
		case JPEG:
		case PNG:
			return IMAGE;
		case TEXT:
		case BOM_TEXT:
			return TEXT;
			
		case ZIP:
		case RAR:
		case PDF:
		case MP3:
		case PPTX:
		case PPT:
		case EXCEL:
		case XLS:
		case XLSX:
		case DOC:
		case DOCX:
			return UNKNOWN;	
		default:
			if(checkCode!=null&&getUsedCode().contains(code)) {
				return TEXT;
			}
			
			return UNKNOWN;
		}
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

//	/**
//	 * 根据文件流判断文件类型
//	 *
//	 * @return jpg/png/gif/bmp
//	 */
//	public static String genType(byte[] data) {
//		//读取文件的前几个字节来判断图片格式
//		if (data == null) {
//			return UNKNOWN;
//		}
//		int min = Math.min(data.length, 6);
//		byte[] b = new byte[min];
//		System.arraycopy(data, 0, b, 0, min);
//		String s = SelectTableSqlUtil.bytesToHexString(b);
//		if (StringUtils.isBlank(s)) {
//			return UNKNOWN;
//		}
//		String type = s.toUpperCase();
//		if (type.contains("FFD8FF")) {
//			return IMAGE_JPG;
//		} else if (type.contains("89504E47")) {
//			return IMAGE_PNG;
//		} else if (type.contains("47494638")) {
//			return IMAGE_GIF;
//		} else if (type.contains("424D")) {
//			return IMAGE_BMP;
//		} else if (type.contains("3C3F786D6C")) {
//			return TYPE_XML;
//		} else if (type.contains("68746D6C3E")) {
//			return TYPE_HTML;
//		} else if (type.contains("504B0304")) {
//			return TYPE_ZIP;
//		} else if (type.contains("52617221")) {
//			return TYPE_RAR;
//		} else if (type.contains("504446")) {
//			return TYPE_PDF;
//		} else {
//			return UNKNOWN;
//		}
//	}

	private class ExportListener implements ActionListener {
		private File file;

		@Override
		public void actionPerformed(ActionEvent e) {
			SwingUtilities.invokeLater(() -> {
				try {
					HFileChooser hfc = new HFileChooser(HFileChooser.FileType.BOTH);
					hfc.setDialogTitle(LangMgr.lang(Word2.EXPORT));
					hfc.setApproveButtonText(LangMgr.lang(Word2.OK));
					String defaultName = "";
					FileTypeEnum type = FileTypeUtil.getFileType(data);
					
					if(FileTypes.getTypeEnums().contains(type)) {
						defaultName = System.currentTimeMillis()  + FileTypes.getFtfBytes(type);
					};
					
					hfc.setDefaultName(defaultName);
					if (hfc.showOpenDialog() == HFileChooser.APPROVE_OPTION) {
						file = hfc.getSelectedFile();
						if (file != null) {
							if (!file.exists()) {
								file.createNewFile();

								FileUtils.writeByteArrayToFile(file, data, false);
								PopDlgUtil.info(LangMgr.lang(Word4.EXPORT_SUCCESS));
							} else {
								PopDlgUtil.info(String.format("文件 : %s 已存在!", file.getAbsolutePath()));
							}
						}
					}
				} catch (IOException exception) {
					PopDlgUtil.error(exception);
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
		return IconFileUtil.getIcon(new IconBean(IconFileUtil.STATUS_CONTEXT, name, IconSizeEnum.SIZE_128));
	}
	
	private Set<String> getUsedCode() {
		return new LinkedHashSet<>(Arrays.asList("UTF-8", "GB2312", "GBK", "GB18030"));
	}
}
