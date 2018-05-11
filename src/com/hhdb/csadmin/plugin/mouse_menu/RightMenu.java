package com.hhdb.csadmin.plugin.mouse_menu;

import java.util.Map;


public class RightMenu {
	private String parentMenu;
	private Boolean isAvailable;
	private Boolean haveChildren;
	private Map<String, Boolean> childrenMap;
	public RightMenu(){}
	public  RightMenu(String parentMenu,Boolean isAvailable,Boolean haveChildren,Map<String, Boolean> childrenMap) {
		this.setParentMenu(parentMenu);
		this.setIsAvailable(isAvailable);
		this.setHaveChildren(haveChildren);
		this.setChildrenMap(childrenMap);
	}
	
	public Boolean getIsAvailable() {
		return isAvailable;
	}
	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	public Boolean getHaveChildren() {
		return haveChildren;
	}
	public void setHaveChildren(Boolean haveChildren) {
		this.haveChildren = haveChildren;
	}
	public Map<String, Boolean> getChildrenMap() {
		return childrenMap;
	}
	public void setChildrenMap(Map<String, Boolean> childrenMap) {
		this.childrenMap = childrenMap;
	}

	public String getParentMenu() {
		return parentMenu;
	}

	public void setParentMenu(String parentMenu) {
		this.parentMenu = parentMenu;
	}
}
