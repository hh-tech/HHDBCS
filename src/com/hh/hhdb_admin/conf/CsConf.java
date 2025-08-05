package com.hh.hhdb_admin.conf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lg.Lg;
import com.hh.frame.lg.LgBaseConf;
import com.hh.frame.lg.LgFileConf;
import com.hh.frame.swingui.cs_desk.BaseStyleBean;
import com.hh.frame.swingui.laf.LafIdEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;

public class CsConf extends BaseStyleBean{
	private static final String DEFAULT_DB_TYPE="default_db_type";
	private static final String SUPPORT_DB_TYPE="support_db_type";
	private static final String SQL_FORMAT_WIDTH="sql_format_width";
	private static final String AUTO_SAVE_CONN="auto_save_conf";
	
	private static final String SQL_PANEL_CONF="sql_panel_conf";
	private static final String CMD_PANEL_CONF="cmd_panel_conf";

	private static final String LOG_CONF="log_conf";
	private static final String SHOW_TOOL_BAR = "show_tool_bar";
	private static final String SHOW_STATUS_BAR = "show_status_bar";
	
	public CsConf() {
		setSkin(LafIdEnum.FAST);
		setDefDbType(DBTypeEnum.hhdb);
		setSqlFormatWith(50);
		setCmdPanelConf(new CmdPanelConf());
		setSqlPanelConf(new SqlPanelConf());
		setLogConf(new LgFileConf(new File("logs")));
		setAutoSaveConn(true);
		setSupportDbTypes(CsVarUtil.allSupportDbTypes);
	}
	
	public CsConf(JsonObject jobj) {
		super(jobj);
	}
	
	public DBTypeEnum getDefDbType() {
		return DBTypeEnum.valueOf(jobj.getString(DEFAULT_DB_TYPE));
	}
	
	public void setDefDbType(DBTypeEnum dbtype) {
		jobj.set(DEFAULT_DB_TYPE, dbtype.name());
	}
	
	public List<DBTypeEnum> getSupportDbTypes(){
		List<DBTypeEnum> l=new ArrayList<>();
		JsonArray arr=jobj.get(SUPPORT_DB_TYPE).asArray();
		for (int i = 0; i < arr.size(); i++) {
			String s= arr.get(i).asString();
			l.add(DBTypeEnum.valueOf(s));
		}
		return l;
	}
	
	public void setSupportDbTypes(List<DBTypeEnum> types){
		JsonArray arr=new JsonArray();
		for(DBTypeEnum type:types) {
			arr.add(type.name());
		}
		jobj.set(SUPPORT_DB_TYPE, arr);
	}
	
	public void setSqlFormatWith(int width) {
		jobj.set(SQL_FORMAT_WIDTH, width);
	}
	
	public int getSqlFormatWith() {
		return jobj.getInt(SQL_FORMAT_WIDTH);
	}
	
	public void setAutoSaveConn(boolean autoSave) {
		jobj.set(AUTO_SAVE_CONN, autoSave);
	}
	public boolean getAutoSaveConn() {
		return jobj.getBoolean(AUTO_SAVE_CONN);
	}
	
	public LgBaseConf getLogConf() {
		JsonObject o=jobj.get(LOG_CONF).asObject();		
		return Lg.readConf(o);
	}
	
	public void setLogConf(LgBaseConf lgConf) {
		jobj.set(LOG_CONF, lgConf.getJobj());
	}
	
	public void setSqlPanelConf(SqlPanelConf conf) {
		jobj.set(SQL_PANEL_CONF, conf.getJobj());
	}
	
	public SqlPanelConf getSqlPanelConf() {
		JsonObject o=jobj.get(SQL_PANEL_CONF).asObject();
		SqlPanelConf sqlPanel=new SqlPanelConf(o);
		return  sqlPanel;
	}
	
	
	public void setCmdPanelConf(CmdPanelConf conf) {
		jobj.set(CMD_PANEL_CONF, conf.getJobj());
	}
	
	public CmdPanelConf getCmdPanelConf() {
		JsonObject o=jobj.get(CMD_PANEL_CONF).asObject();
		CmdPanelConf cmdPanel=new CmdPanelConf(o);
		return  cmdPanel;
	}
	
	public void setShowToolBar(boolean show) {
		jobj.set(SHOW_TOOL_BAR, show);
	}
	
	public void setShowStatusBar(boolean show) {
		jobj.set(SHOW_STATUS_BAR, show);
	}
	
	public Boolean IsShowToolBar() {
		return jobj.getBoolean(SHOW_TOOL_BAR, true);
	}
	
	public Boolean isShowStatusBar() {
		return jobj.getBoolean(SHOW_STATUS_BAR, true);
	}
	public static void main(String[] args) {
		CsConf conf=new CsConf();
		System.out.println(conf);
	}

}
