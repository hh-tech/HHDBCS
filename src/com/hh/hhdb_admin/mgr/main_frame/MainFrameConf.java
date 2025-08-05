package com.hh.hhdb_admin.mgr.main_frame;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
public class MainFrameConf extends BaseJsonBean{
	public MainFrameConf() {
		setShowMenu(true);
		setShowToolbar(true);
		setShowStatus(true);
		setShowTree(true);
		setShowTabbedPane(true);
	}
	public MainFrameConf(JsonObject obj) {
		this.jobj=obj;
	}
	private final static String SHOWMENU="showMenu";
	private final static String SHOWTOOLBAR="showToolbar";
	private final static String SHOWSTATUS="showStatus";
	private final static String SHOWTREE="showTree";
	private final static String SHOWTABBEDPANE="showTabbedPane";
	public boolean getShowMenu() {
		return jobj.getBoolean(SHOWMENU);
	}
	public void setShowMenu(boolean showMenu) {
		jobj.set(SHOWMENU, showMenu);
	}
	public boolean getShowToolbar() {
		return jobj.getBoolean(SHOWTOOLBAR);
	}
	public void setShowToolbar(boolean showToolbar) {
		jobj.set(SHOWTOOLBAR, showToolbar);
	}
	public boolean getShowStatus() {
		return jobj.getBoolean(SHOWSTATUS);
	}
	public void setShowStatus(boolean showStatus) {
		jobj.set(SHOWSTATUS, showStatus);
	}
	public boolean getShowTree() {
		return jobj.getBoolean(SHOWTREE);
	}
	public void setShowTree(boolean showTree) {
		jobj.set(SHOWTREE, showTree);
	}
	public boolean getShowTabbedPane() {
		return jobj.getBoolean(SHOWTABBEDPANE);
	}
	public void setShowTabbedPane(boolean showTabbedPane) {
		jobj.set(SHOWTABBEDPANE, showTabbedPane);
	}
	public static void main(String[] args) {
		MainFrameConf conf=new MainFrameConf();
		System.out.print(conf);
	}
}
