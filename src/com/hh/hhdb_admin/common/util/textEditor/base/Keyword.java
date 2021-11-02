package com.hh.hhdb_admin.common.util.textEditor.base;

import javax.swing.*;

/**
 * 关键字对象
 * @author hhxd
 */
public class Keyword {
	private String name;   					//显示的内容
	private String value;   				//值
	private String type;					//类型
	private ImageIcon icon;					//图片
	
	/**
	 * 构造关键字对象
	 * @param name   名称
	 * @param type	类型，表：t，视图:v，其他：k
	 * @param icon   图片
	 */
	public Keyword(String name,String value, String type,ImageIcon icon) {
		setName(name);
		setValue(value);
		setType(type);
		this.icon = icon;
	}
	
	public Keyword(String name, String type,ImageIcon icon) {
		this(name,null,type,icon);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ImageIcon getIcon() {
		return icon;
	}
}
