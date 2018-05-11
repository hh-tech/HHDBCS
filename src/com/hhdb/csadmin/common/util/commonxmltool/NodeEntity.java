package com.hhdb.csadmin.common.util.commonxmltool;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class NodeEntity {
	private String name;
	private String text;
	private String path;

	private NodeEntity parentNode;
	private Map<String, String> attrMap = new HashMap<String, String>();
	private List<NodeEntity> childList = new ArrayList<NodeEntity>();

	public NodeEntity getParentNode() {
		return parentNode;
	}

	public void setParentNode(NodeEntity parentNode) {
		this.parentNode = parentNode;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Map<String, String> getAttrMap() {
		return attrMap;
	}

	public void setAttrMap(Map<String, String> attrMap) {
		this.attrMap = attrMap;
	}

	public List<NodeEntity> getChildList() {
		return childList;
	}

	public void setChildList(List<NodeEntity> childList) {
		this.childList = childList;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("name:" + name + ",");
		sb.append("text:" + text + ",");
		sb.append("path:" + path + ",");
		Set<String> keySet = attrMap.keySet();
		sb.append("attr:");
		for (String k : keySet) {
			sb.append(k + ":" + attrMap.get(k) + ",");
		}
		//sb.append(childList.toString() + "\r\n");
		return sb.toString();
	}

	public void addAttri(String key, String value) {
		attrMap.put(key, value);
	}

	public String getAttri(String key) {
		return XmlTools.getString(attrMap, key);
	}

	public String getAttri(String key, String defaultValue) {
		return XmlTools.getString(attrMap, key, defaultValue);
	}

	public void addNode(NodeEntity node) {
		childList.add(node);
	}
}
