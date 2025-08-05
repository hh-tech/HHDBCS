package com.hh.hhdb_admin.mgr.function2;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.DBTypeEnum.Version;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.comp.column.DataTypePanel;

import org.apache.commons.lang3.StringUtils;

class PkgTypeCol extends JsonCol{
	private DBTypeEnum dbtype=CsVarUtil.getDbtype();
	private Version ver=CsVarUtil.jdbc.getCurrentVersion();
	public PkgTypeCol(String name, String value) {
		super(name, value);
	}
	
	DataTypePanel panel = null;
	@Override
	public JsonObject onClick(JsonObject json, int row, int column) {

		HDivLayout layout = new HDivLayout();
		layout.setTopHeight(10);
		layout.setBottomHeight(10);
		
		if(StringUtils.isNotBlank(json.getString(__TEXT))) {
//            panel.setByFullType(json.getString(__TEXT));
			try {
				panel=new DataTypePanel(dbtype,ver,json.getString(__TEXT));
			} catch (Exception e) {
				GlobalWorkUtil.err(getClass(), e);
			}
		}else {
			panel=new DataTypePanel(dbtype,ver);
		}
		
		HDialog dialog = new HDialog(getTab().getComp(), 500,300,false) {
			@Override
			protected void onConfirm() {
				JsonObject obj = new JsonObject();
                obj.set(JsonCol.__TEXT, panel.getFullTypeDefine());
				htab.setValueAt(obj.toString(),row, column);
			}
		};
		dialog.setRootPanel(panel);
		dialog.show();
		return json;
	
	}

}
