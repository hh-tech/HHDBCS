package com.hh.hhdb_admin.mgr.table_open.comp;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SelectTableSqlUtil;
import com.hh.frame.create_dbobj.table.base.AbsTableObjFun;
import com.hh.frame.create_dbobj.table.comm.CreateTableUtil;
import com.hh.frame.dbobj3.edit.EditTableUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.RadioGroupInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.lob_panel.LobEditor;
import com.hh.hhdb_admin.common.lob_panel.LobViewer;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.table_open.common.LobViewListener;
import com.hh.hhdb_admin.mgr.table_open.common.SaveLobListener;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.io.IOException;
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
	private AtomicReference<JsonObject> resJsonObject;
	
	private DBTypeEnum.Version ver;

	/**
	 * @param colId 列id
	 * @param value 列名
	 * @param type  类型
	 */
	public LobJsonCol(DBTypeEnum dbtype, DBTypeEnum.Version ver,String colId, String value, Enum<?> type) {
		super(colId, value);
		this.dbTypeEnum = dbtype;
		this.ver=ver;
		this.type = type;
		tableObjFun = CreateTableUtil.getDateType(dbtype,ver);
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
			if (SelectTableSqlUtil.isLob(dbTypeEnum, ver,type)) {
				boolean isBlob = SelectTableSqlUtil.isBlob(dbTypeEnum,ver, type, tableObjFun);

				byte[] bytes = EditTableUtil.getBytesForJson(json, isBlob);
				
				json = showLobView(bytes);
			}
		} catch (IOException e) {
			PopDlgUtil.error(e);
		}

		return json;
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
			dialog = new HDialog(CsVarUtil.parentFrame, 900, 900 / 4 * 3);
			dialog.setRootPanel(dPanel);
			dialog.setTitle(LangMgr.sLang(CsL.DISPLAY_LARGE_FIELD_DATA));
			dialog.setWindowTitle(LangMgr.sLang(CsL.BINARY_DISPLAY));
			dialog.setStatusBar(new HBarPanel());
			JDialog jDialog = ((JDialog) dialog.getWindow());
			jDialog.setResizable(true);
			//setAlwaysOnTop至于窗口最顶部
//			jDialog.setAlwaysOnTop(dialog.getWindow().isAlwaysOnTopSupported());
			jDialog.setModal(true);
			dialog.show();
			viewer.getTextArea().getRSyntaxTextArea().requestFocus();
			panel.updateUI();
		} catch (IOException e) {
			PopDlgUtil.error(e);
		}
		return resJsonObject != null ? resJsonObject.get() : null;
	}


	private LastPanel getLastPanel(byte[] bytes) throws IOException {
		HPanel radioPanel = new HPanel(new HDivLayout(GridSplitEnum.C1, GridSplitEnum.C1, GridSplitEnum.C1));
		rInput = new RadioGroupInput("RADIO_GROUP", radioPanel);
		rInput.add(LobViewer.TEXT, LangMgr.lang(Word2.TEXT));
		rInput.add(LobViewer.IMAGE, LangMgr.lang(Word2.PICTURE));
		rInput.add(LobViewer.UNKNOWN, LangMgr.lang(Word2.OTHER));
		rInput.setTitle(LangMgr.lang(Word2.TYPE));
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
			((LobEditor) viewer).addToolBarBtn(initBtn());
		} else {
			rInput.setTitle(LangMgr.lang(Word2.TYPE)+"("+LangMgr.lang(Word2.READONLY)+")");
			viewer = new LobViewer();
		}
		viewer.setReadOnly(readOnly);
		viewer.loadData(bytes);
		String genType = viewer.genType();
		rInput.setSelected(genType.equalsIgnoreCase(LobEditor.NULL) ? LobViewer.TEXT : genType);
		viewer.getExportBtn().setEnabled(true);
		rInput.addItemListener(e -> {
			if (e.getStateChange() != ItemEvent.SELECTED) {
				return;
			}
			try {
				String type = ((JRadioButton) e.getItem()).getName();
				viewer.changeType(type);
				viewer.fileType = type;
			} catch (IOException ioException) {
				PopDlgUtil.error(ioException);
			}

		});
		LastPanel panel = new LastPanel(false);
		panel.set(viewer.getPanel().getComp());
		panel.setHead(radioPanel.getComp());
		return panel;
	}

	private HButton[] initBtn() {
		HButton cleanBtn = new HButton(LangMgr.lang(Word2.ELIMINATE));
		cleanBtn.addActionListener(getCleanListener());
		HButton saveBtn = new HButton(LangMgr.lang(Word2.SAVE));
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
		JsonObject lobJson = ((SaveLobListener) saveListener).getLobJson();
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
