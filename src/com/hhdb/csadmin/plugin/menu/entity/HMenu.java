package com.hhdb.csadmin.plugin.menu.entity;


import java.util.List;

public class HMenu{
private String id;

private String name;
private List<Object> menuItems;

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


public List<Object> getMenuItems() {
	return menuItems;
}
public void setMenuItems(List<Object> menuItems) {
	this.menuItems = menuItems;
}

@Override
public String toString() {
	return "HMenu [id=" + id + ", name=" + name + ", menuItems=" + menuItems
			+ "]";
}


}
