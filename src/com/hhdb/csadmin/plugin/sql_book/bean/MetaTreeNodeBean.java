package com.hhdb.csadmin.plugin.sql_book.bean;

import com.hh.frame.common.log.LM;

/**
 * 树结构数据Bean
 * @author hhxd
 *
 */
public class MetaTreeNodeBean implements Cloneable{
	private String openIcon;   	//打开图标
	private String closeIcon;	//关闭图标
	private String type = "";	//类型
	private String name;		//名称
	private Integer id;			//id
	private Integer parentId;	//上级id
	private Integer originalId;	//为快捷方式时原始的id
	private String txt;         //内容
	
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

	public String getTxt() {
		return txt;
	}

	public void setTxt(String txt) {
		this.txt = txt;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getOriginalId() {
		return originalId;
	}

	public void setOriginalId(Integer originalId) {
		this.originalId = originalId;
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
