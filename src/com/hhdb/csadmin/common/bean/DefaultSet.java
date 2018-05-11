package com.hhdb.csadmin.common.bean;

import java.util.ArrayList;
import java.util.List;

public class DefaultSet {
	private String sqlkeys;
	private String background;
	private String fontSize;
	private String linunumber;
	private String cmdbackcolor;
	private String cmdfontcolor;
	private List<String> keylist = new ArrayList<String>();
	private String qkeyguanjian = "";
	private String qkeytablename = "";
	private String qkeyviewname = "";

	
	public String getQkeyguanjian() {
		return qkeyguanjian;
	}

	public void setQkeyguanjian(String qkeyguanjian) {
		this.qkeyguanjian = qkeyguanjian;
	}

	public String getQkeytablename() {
		return qkeytablename;
	}

	public void setQkeytablename(String qkeytablename) {
		this.qkeytablename = qkeytablename;
	}

	public String getQkeyviewname() {
		return qkeyviewname;
	}

	public void setQkeyviewname(String qkeyviewname) {
		this.qkeyviewname = qkeyviewname;
	}

	public String getSqlkeys() {
		return sqlkeys;
	}

	public void setSqlkeys(String sqlkeys) {
		this.sqlkeys = sqlkeys;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	public String getLinunumber() {
		return linunumber;
	}

	public void setLinunumber(String linunumber) {
		this.linunumber = linunumber;
	}

	public List<String> getKeylist() {
		for (String key : getSqlkeys().split(",")) {
			keylist.add(key);
		}

		return keylist;
	}

	public String getCmdbackcolor() {
		return cmdbackcolor;
	}

	public void setCmdbackcolor(String cmdbackcolor) {
		this.cmdbackcolor = cmdbackcolor;
	}

	public String getCmdfontcolor() {
		return cmdfontcolor;
	}

	public void setCmdfontcolor(String cmdfontcolor) {
		this.cmdfontcolor = cmdfontcolor;
	}
}
