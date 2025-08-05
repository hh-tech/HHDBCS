package com.hh.hhdb_admin.mgr.tree;

import org.apache.commons.lang3.StringUtils;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.tree2.FolderBean;


public class CsFolderBean extends FolderBean {
	//节点类型
	private TreeMrNode mrNode;
	
	/**
	 * 初始化节点
	 * @param name	节点显示名称
	 */
	public CsFolderBean(String name) {
		super(name);
	}
	
	public String getObjectId() {
		return mrNode.getId();
	}
	
	public TreeMrType getType() {
		if(mrNode==null) return null;
		return mrNode.getType();
	}
	
	
	@Override
	public void setTip(String tip) {
		if(StringUtils.isNotBlank(tip)) {
			if(tip.indexOf('\n')<0) {
				super.setTip(tip);
			}else {
				super.setTip("<html><pre>"+tip+"</html></pre>");
			}
		}else
			super.setTip(null);
	}

	public TreeMrNode getMrNode() {
		return mrNode;
	}

	public void setMrNode(TreeMrNode mrNode) {
		this.mrNode = mrNode;
	}
	
}
