package com.hh.hhdb_admin.mgr.table_open.common;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SelectTableSqlUtil;
import com.hh.frame.common.util.db.lob.LobUtil;
import com.hh.frame.create_dbobj.table.base.AbsTableObjFun;
import com.hh.frame.create_dbobj.table.comm.CreateTableUtil;
import com.hh.frame.dbobj3.edit.EditTableUtil;
import com.hh.frame.dbquery.QueryTool;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.hhdb_admin.common.lob_panel.LobViewer;
import com.hh.hhdb_admin.mgr.table_open.comp.LobJsonCol;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.awt.event.ActionEvent;
import java.io.*;

/**
 * @author ouyangxu
 * @date 2022-01-04 0004 14:06:16
 */
public class SaveLobListener extends LobViewListener {
	private Enum<?> type;

//	private LobJsonCol jsonCol;
	private File tmpAddDir;
	private JsonObject lobJson;

	private HTable table;
	private DBTypeEnum dbtype;
	private DBTypeEnum.Version ver;
	private AbsTableObjFun tableObjFun;

	public SaveLobListener(QueryTool queryTool, LobJsonCol jsonCol, String tableName, Enum<?> type) {
		this.type = type;
		this.dbtype = queryTool.getDbType();
		this.ver=queryTool.getDbVer();
//		this.jsonCol = jsonCol;
		File baseDir = queryTool.getBaseDir();
		this.tableObjFun = CreateTableUtil.getDateType(dbtype,queryTool.getDbVer());
		tmpAddDir = new File(baseDir, "addLob" + File.separator + tableName);
		tmpAddDir.mkdirs();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		byte[] data = null;
		ByteArrayOutputStream os = null;
		try {
			os = new ByteArrayOutputStream();
			String code = viewer.getCode();
			String ct = viewer.genType();
			boolean isGet = (ct.equals(LobViewer.TEXT) || ct.equals(LobViewer.NULL));
			if (isGet) {
				viewer.getTextArea().getRSyntaxTextArea().write(new OutputStreamWriter(os));
				data = os.toByteArray();
			} else {
				data = viewer.getData();
			}

			boolean isBlob = SelectTableSqlUtil.isBlob(dbtype, ver,type, tableObjFun);
			int selectedColumn = table.getComp().getSelectedColumn();
			int selectedRow = table.getComp().getSelectedRow();
			//					JsonColEditor cellEditor = (JsonColEditor) ((JTable) table.getComp()).getColumnModel().getColumn(selectedColumn).getCellEditor();
			if (data != null && data.length > 0) {
				File tmpData = new File(tmpAddDir, selectedRow + "_" + selectedColumn + ".dat");
				if (tmpData.exists()) {
					FileUtils.deleteQuietly(tmpData);
				}
				tmpData.createNewFile();
				try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
					String length = "0";
					if (isBlob) {
						length = String.valueOf(LobUtil.writeStream2File(is, tmpData, 0));
					} else {
						Reader reader = new InputStreamReader(is);
						length = LobUtil.reader2File(reader, tmpData, 0).split("_")[1];
					}
					lobJson = EditTableUtil.getLobJsonObj(type, 0 + "_" + length, tmpData.getAbsolutePath());
					if(code==null) {
						lobJson.set(EditTableUtil.ENCODING, EncodingDetect.UTF_8);
					}else {
						lobJson.set(EditTableUtil.ENCODING, code);
					}
					
				}
			}
			callBack();
		} catch (Exception e2) {
			PopDlgUtil.error(e2);
		} finally {
			IOUtils.closeQuietly(os);
		}
	}


	protected void callBack() throws Exception {
	}

	public HTable getTable() {
		return table;
	}

	public void setTable(HTable table) {
		this.table = table;
	}

	public JsonObject getLobJson() {
		return lobJson;
	}

	public void setLobJson(JsonObject lobJson) {
		this.lobJson = lobJson;
	}
}
