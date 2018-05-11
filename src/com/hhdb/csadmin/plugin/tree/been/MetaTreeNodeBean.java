package com.hhdb.csadmin.plugin.tree.been;

import java.util.HashMap;
import java.util.Map;

import com.hh.frame.common.log.LM;

public class MetaTreeNodeBean implements Cloneable{
	// private String connKey;
	private String openIcon;
	private String closeIcon;
	private String type = "";
	private String name;
	private int id;
	private boolean isUnique = false;
	private Map<String, String> attrMap = new HashMap<String, String>();

	public String getOpenIcon() {
		return this.openIcon;
	}

	public void setOpenIcon(String openIcon) {
		this.openIcon = openIcon;
	}

	public String getCloseIcon() {
		return this.closeIcon;
	}

	public void setCloseIcon(String closeIcon) {
		this.closeIcon = closeIcon;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getUnique() {
		return this.isUnique;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}

	public Map<String, String> getAttrMap() {
		return this.attrMap;
	}

	public void setAttrMap(Map<String, String> attrMap) {
		this.attrMap = attrMap;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public MetaTreeNodeBean clone(){
		MetaTreeNodeBean obj = null;
        try{
        	obj = (MetaTreeNodeBean)super.clone();
        }catch(CloneNotSupportedException e){
        	LM.error(LM.Model.CS.name(), e);
        }
        return obj;
    }
}
