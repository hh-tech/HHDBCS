package com.hh.hhdb_admin.conf;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.textarea2.base.TAreaThemeType;

public class SqlPanelConf extends BaseJsonBean {
	private static final String ROWS_PER_PAGE="rows_per_page";
	private static final String FNOT_SIZE="font_size";
	private static final String AUTO_POP="auto_pop";
	private static final String TAREA_THEME="tarea_theme";
	
	public SqlPanelConf() {
		setRowPerPage(30);
		setFontSize(13);
		setAutoPop(true);
		setTAreaThemeType(TAreaThemeType.auto);
	}
	
	public SqlPanelConf(JsonObject jobj) {
		this.jobj=jobj;
	}
	
	public void setRowPerPage(int rows) {
		jobj.set(ROWS_PER_PAGE, rows);
	}
	
	public int getRowPerPage() {
		return jobj.getInt(ROWS_PER_PAGE);
	}
	
	public void setFontSize(int size) {
		jobj.set(FNOT_SIZE, size);
	}
	
	public int getFontSize() {
		return jobj.getInt(FNOT_SIZE);
	}
	
	public TAreaThemeType getTareaTheme() {
		return TAreaThemeType.valueOf(jobj.getString(TAREA_THEME));
	}
	
	public void setTAreaThemeType(TAreaThemeType theme) {
		jobj.set(TAREA_THEME, theme.name());
	}
	
	
	
	public boolean isAutoPop() {
		return jobj.getBoolean(AUTO_POP);
	}
	public void setAutoPop(boolean autoPop) {
		jobj.set(AUTO_POP, autoPop);
	}
	
}
