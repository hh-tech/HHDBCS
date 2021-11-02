package com.hh.hhdb_admin.mgr.table_open.ui;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.lob.LobUtil;
import com.hh.frame.create_dbobj.table.base.AbsTableObjFun;
import com.hh.frame.create_dbobj.table.comm.CreateTableUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.RadioGroupInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.lob_panel.LobEditor;
import com.hh.hhdb_admin.common.lob_panel.LobViewer;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.table_open.common.LobViewListener;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabDataUtil;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabTool;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ItemEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author oyx
 * @description :大字段列 (暂不支持修改)
 * @date 2020-12-24 10:30:08
 */
public class LobJsonCol extends JsonCol {
	private HDialog dialog;
	private final Enum<?> type;
	private final DBTypeEnum dbTypeEnum;
	private final AbsTableObjFun tableObjFun;

	private LobViewListener saveListener;
	private LobViewListener cleanListener;

	private LobViewer viewer;

	private RadioGroupInput rInput;

	private boolean readOnly;
	private JsonObject tmpJson;
	private AtomicReference<JsonObject> resJsonObject;

	/**
	 * @param colId 列id
	 * @param value 列名
	 * @param type  类型
	 */
	public LobJsonCol(DBTypeEnum dbTypeEnum, String colId, String value, Enum<?> type) {
		super(colId, value);
		this.dbTypeEnum = dbTypeEnum;
		this.type = type;
		tableObjFun = CreateTableUtil.getDateType(dbTypeEnum);
	}

	@Override
	public JsonObject onClick(JsonObject json, int row, int column) {
		JsonObject object = cellOnClick(type, json);
		return object != null ? object : json;
	}

	/**
	 * 点击查看
	 *
	 * @param type 类型
	 * @param json json数据
	 * @return
	 */
	protected JsonObject cellOnClick(Enum<?> type, JsonObject json) {
		resJsonObject = new AtomicReference<>();
		try {
			if (ModifyTabDataUtil.isLob(dbTypeEnum, type)) {
				if (tmpJson != null) {
					json = tmpJson;
				}
				byte[] bytes = null;
				if (json != null) {
					JsonValue filePath = json.get(ModifyTabTool.FILE_PATH);
					JsonValue offset = json.get(ModifyTabTool.OFFSET);
					JsonValue len = json.get(ModifyTabTool.LEN);
					if (filePath != null && offset != null && len != null) {
						File dataFile = new File(filePath.asString().trim());
						boolean isBlob = ModifyTabDataUtil.isBlob(dbTypeEnum, type, tableObjFun);
						bytes = readLobData(isBlob, dataFile, offset.asString().trim(), len.asString().trim());
					}
				}
				json = showLobView(bytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
			PopPaneUtil.error(e.getMessage());
		}

		return json;
	}

	/**
	 * 读取data文件数据
	 *
	 * @param isBlob 是blob还是clob
	 * @param offset 开始位置
	 * @param len    长度
	 * @return
	 * @throws IOException
	 */
	private byte[] readLobData(boolean isBlob, File dataFile, String offset, String len) throws IOException {
		byte[] bytes = null;
		if (isBlob) {
			try (InputStream stream = LobUtil.getStreamFromFile(dataFile, Long.parseLong(offset), Long.parseLong(len))) {
				bytes = IOUtils.toByteArray(stream);
			}
		} else {
			try (Reader reader = LobUtil.getReaderFromFile(dataFile, Long.parseLong(offset), Integer.parseInt(len));
				 BufferedReader bufferedReader = new BufferedReader(reader)) {
				bytes = IOUtils.toByteArray(bufferedReader, StandardCharsets.UTF_8);
			}
		}
		return bytes;
	}

	/**
	 * 显示大字段弹框
	 *
	 * @param bytes 二进制数组
	 */
	private JsonObject showLobView(byte[] bytes) {
		try {
			LastPanel panel = getLastPanel(bytes);
			HPanel dPanel = new HPanel();
			dPanel.setLastPanel(panel);
			dialog = new HDialog(StartUtil.getMainDialog(), 900, 900 / 4 * 3);
			dialog.setRootPanel(dPanel);
			dialog.setTitle("显示大字段数据");
			dialog.setWindowTitle("二进制显示");
			JDialog jDialog = ((JDialog) dialog.getWindow());
			jDialog.setResizable(true);
			//setAlwaysOnTop至于窗口最顶部
			jDialog.setAlwaysOnTop(dialog.getWindow().isAlwaysOnTopSupported());
			jDialog.setModal(true);
			dialog.show();
			viewer.getTextArea().getArea().getTextArea().requestFocus();
			panel.updateUI();
		} catch (IOException e) {
			PopPaneUtil.error(e);
			e.printStackTrace();
		}
		return resJsonObject != null ? resJsonObject.get() : null;
	}


	private LastPanel getLastPanel(byte[] bytes) throws IOException {
		HPanel radioPanel = new HPanel(new HDivLayout(GridSplitEnum.C1, GridSplitEnum.C1, GridSplitEnum.C1));
		rInput = new RadioGroupInput("RADIO_GROUP", radioPanel);
		rInput.add(LobViewer.TEXT, "文本");
		rInput.add(LobViewer.IMAGE, "图片");
		rInput.add(LobViewer.UNKNOWN, "其他");
		rInput.setTitle("显示类型");
		if (!readOnly) {
			viewer = new LobEditor() {
				@Override
				protected void openCallback() {
					rInput.setSelected(genType());
				}
			};
			if (saveListener != null) {
				saveListener.setViewer((LobEditor) viewer);
			}
			if (cleanListener != null) {
				cleanListener.setViewer((LobEditor) viewer);
			}
			addFilter(LobViewer.TEXT);
			((LobEditor) viewer).addToolBarBtn(initBtn());
		} else {
			rInput.setTitle("显示类型(只读)");
			viewer = new LobViewer();
		}
		viewer.setReadOnly(readOnly);

		LastPanel panel = new LastPanel();

		viewer.loadData(bytes);
		String genType = viewer.genType();
		rInput.setSelected(genType.equalsIgnoreCase(LobEditor.NULL) ? LobViewer.TEXT : genType);
		viewer.getExportBtn().setEnabled(true);
		rInput.addItemListener(e -> {
			if (e.getStateChange() != ItemEvent.SELECTED) {
				return;
			}
			try {
				((LobEditor) viewer).setExtFilters(null);
				String type = ((JRadioButton) e.getItem()).getName();
				viewer.changeType(type);
				addFilter(type);
			} catch (IOException ioException) {
				ioException.printStackTrace();
				PopPaneUtil.error(ioException);
			}

		});
		panel.setWithScroll(viewer.getComp());
		panel.setHead(radioPanel.getComp());
		return panel;
	}

	private void addFilter(String type) {
		if (type.equalsIgnoreCase(LobViewer.IMAGE)) {
			FileFilter imageFilter = new FileNameExtensionFilter("Images (*.jpg,*.png,*.bmp,*.gif)", "jpg", "jpeg", "png", "bmp", "gif ");
			((LobEditor) viewer).addFilters(imageFilter);
		} else if (type.equalsIgnoreCase(LobViewer.TEXT)) {
			FileFilter txtFilter = new FileNameExtensionFilter("Text (*.txt,*.text,*.rtf)", "txt", "text", "rtf");
			FileFilter xmlFilter = new FileNameExtensionFilter("XML (*.xml)", "xml");
			FileFilter htmlFilter = new FileNameExtensionFilter("HTML (*.html)", "htm", "html");
			FileFilter sqlFilter = new FileNameExtensionFilter("SQL (*.sql)", "sql");
			((LobEditor) viewer).addFilters(txtFilter, xmlFilter, htmlFilter, sqlFilter);
		}
	}

	private HButton[] initBtn() {
		HButton cleanBtn = new HButton("清除");
		cleanBtn.addActionListener(getCleanListener());
		HButton saveBtn = new HButton("保存");
		saveBtn.addActionListener(getSaveListener());
		return new HButton[]{cleanBtn, saveBtn};
	}

	public LobViewListener getSaveListener() {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();//二进制OutputStream
//		viewer.getTextArea().getArea().getTextArea().write(new OutputStreamWriter(baos));
//		ByteArrayInputStream in = new ByteArrayInputStream(baos.toByteArray());
		return saveListener;
	}


	public void setSaveListener(LobViewListener saveListener) {
		this.saveListener = saveListener;
	}


	public LobViewListener getCleanListener() {
		return cleanListener;
	}

	public void setCleanListener(LobViewListener cleanListener) {
		this.cleanListener = cleanListener;
	}

	public Enum<?> getType() {
		return type;
	}

	public LobViewer getViewer() {
		return viewer;
	}

	public void dispose() {
		JsonObject lobJson = ((ModifyTabTool.SaveLobListener) saveListener).getLobJson();
		resJsonObject.set(lobJson);
//		jsonColEditor.setValue(lobJson);
		dialog.dispose();
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public JsonObject getTmpJson() {
		return tmpJson;
	}

	public void setTmpJson(JsonObject tmpJson) {
		this.tmpJson = tmpJson;
	}

	public void setDialog(HTable table) {
		this.htab = table;
	}

	//	@Override
//	public TableCellEditor newColEditor() {
//		jsonColEditor=new LobJsonColEditor(this);
//		return jsonColEditor;
//	}
	@Override
	public HTable getTab() {
		return htab;
	}

}
