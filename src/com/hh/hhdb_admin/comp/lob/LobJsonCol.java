package com.hh.hhdb_admin.comp.lob;

import java.io.File;

import com.hh.frame.json.JsonObject;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;

public class LobJsonCol extends JsonCol {
	private boolean isBlob;
	private String dataType;
	private File f;

	public LobJsonCol(String name, String value, boolean isBlob, String type,File dataSaveDir) {
		super(name, value);
		this.isBlob = isBlob;
		this.dataType = type;
		this.f = dataSaveDir;
	}

	@Override
	public JsonObject onClick(JsonObject json, int row, int column) {

		try {
			File saveFile = new File(f,row + "_" + column+".dat");
			LobEditorDlg editor = new LobEditorDlg(getTab().getComp(),json,isBlob, dataType,saveFile);
			editor.show();
			return editor.getRetObj();
		} catch (Exception e) {
			Lg.error(getClass(), e);
			PopDlgUtil.error(getTab().getComp(), e);
		}
		return json;
	}

}
