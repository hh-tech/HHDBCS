package com.hhdb.csadmin.plugin.sql_book.attribute.tree;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.tree.DefaultMutableTreeNode;

import com.hhdb.csadmin.plugin.sql_book.bean.MetaTreeNodeBean;

/**
 * 节点
 * @author hhxd
 *
 */
public class BaseTreeNode extends DefaultMutableTreeNode implements Cloneable{
	private static final long serialVersionUID = 1L;
	private MetaTreeNodeBean nodeBean;									//数据
	private Map<String, Object> attrMap = new HashMap<String, Object>();
	protected boolean isSelected; 
	protected boolean isEnabled=true;
	private List<BaseTreeNode> nodes = new ArrayList<BaseTreeNode>();   //子节点集合
	private BaseTreeNode parentBaseTreeNode = null;     				//父节点
	Connection conn = null;
	
	public BaseTreeNode() {
	}
	
	public BaseTreeNode(MetaTreeNodeBean nodeBean) {
		this.setUserObject(new JLabel());
		this.nodeBean = nodeBean;
	}
	
	public BaseTreeNode getParentBaseTreeNode() {
		return parentBaseTreeNode;
	}

	public void setParentBaseTreeNode(BaseTreeNode parentBaseTreeNode) {
		this.parentBaseTreeNode = parentBaseTreeNode;
	}

	public MetaTreeNodeBean getMetaTreeNodeBean() {
		return nodeBean;
	}

	public void setMetaTreeNodeBean(MetaTreeNodeBean nodeBean) {
		this.nodeBean = nodeBean;
	}

	public void addAttr(String key, String value) {
		attrMap.put(key, value);
	}

	public void addAllAttr(Map<String, Object> aMap) {
		attrMap.putAll(aMap);
	}

	public Map<String, Object> getAttrMap() {
		return attrMap;
	}

	public void setAttrMap(Map<String, Object> attrMap) {
		this.attrMap = attrMap;
	}

	public void addChildNode(BaseTreeNode childNode) {
		nodes.add(childNode);
		this.add(childNode);
	}

	public List<BaseTreeNode> getChildNode() {
		return nodes;
	}

	public String getType() {
		return this.getMetaTreeNodeBean().getType();
	}

	@Override
	public String toString(){
		return nodeBean.getName();
	}
	
 
    public boolean isSelected()  
    {  
        return isSelected;  
    }  
      
    public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
}
