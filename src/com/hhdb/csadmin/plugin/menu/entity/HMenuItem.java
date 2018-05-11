package com.hhdb.csadmin.plugin.menu.entity;

public class HMenuItem {
private String id;
private String name;
private String off;//0禁用；1启用
private String icon;
private String to;
private String key;//快捷键

public String getKey() {
	return key;
}
public void setKey(String key) {
	this.key = key;
}
public String getTo() {
	return to;
}
public void setTo(String to) {
	this.to = to;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}

public String getOff() {
	return off;
}
public void setOff(String off) {
	this.off = off;
}
public String getIcon() {
	return icon;
}
public void setIcon(String icon) {
	this.icon = icon;
}
public HMenuItem() {
	super();
	// TODO Auto-generated constructor stub
}
@Override
public String toString() {
	return "HMenuItem [id=" + id + ", name=" + name + ", off=" + off
			+ ", icon=" + icon + ", to=" + to + ", key=" + key + "]";
}





}
