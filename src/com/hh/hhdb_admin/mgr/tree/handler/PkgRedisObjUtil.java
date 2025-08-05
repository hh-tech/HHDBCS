package com.hh.hhdb_admin.mgr.tree.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.hh.frame.create_dbobj.treeMr.base.RedisObjGroupNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.redis.base.RedisUtil;
import com.hh.frame.swingui.view.tree2.HTree2;
import com.hh.frame.swingui.view.tree2.LeafBean;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.CsTreeUtil;

/*
* Author:Zhang Dagang
* Date:2024年5月2日
*/
class PkgRedisObjUtil {
	public static void loadNodes4Redis(HTree2 tree,CsFolderBean csFolder,List<TreeMrNode> allNodes) {
		TreeMrNode treeMrNode=csFolder.getMrNode();
		RedisObjGroupNode robjNode=(RedisObjGroupNode)treeMrNode;
		String parNodeName=StringUtils.substringBefore(csFolder.getName(), "(");
		parNodeName=String.format("%s(%d)[%s]", parNodeName,allNodes.size(),robjNode.getKeyPattern());
		csFolder.setName(parNodeName);
		List<TreeMrNode> noFolderList=new ArrayList<>();
		String spliter=robjNode.getKeySpliter();
		for(TreeMrNode node:allNodes) {
			String nodeName=node.getName();
			if(StringUtils.isNotBlank(spliter) && nodeName.contains(spliter)) {
				String[] parts=StringUtils.split(nodeName, robjNode.getKeySpliter());
				doLoad(tree,csFolder,node,robjNode.getKeySpliter(),parts);
			}else
				noFolderList.add(node);
		}
		for(TreeMrNode node:noFolderList) {
			CsFolderBean fBean=new CsFolderBean(node.getName());
			fBean.setMrNode(node);
			fBean.setFontType(CsTreeUtil.getFontType(node));
			tree.add(csFolder.getId(), fBean);
		}
		
		// 刷新添加或者更新节点
		String refreshName=null;
		int size=robjNode.getPreLoadKeys().size();
		if(size>0) {
			refreshName=robjNode.getPreLoadKeys().get(size-1);
		}
		tree.reload(csFolder.getId());
		if(refreshName==null) {
			tree.expandFolder(csFolder.getId());
		}else {
			CsFolderBean f=getCsFolderBean(csFolder,refreshName);
			tree.expandFolder(f.getFolder().getId());
			tree.setSelected(f.getId());
		}
	}
	
	private static CsFolderBean getCsFolderBean(CsFolderBean parent,String nodeName){
		 List<LeafBean>  nodes=parent.getNodeBeans();
		 CsFolderBean f=null;
		 for(LeafBean l:nodes) {
			 f=(CsFolderBean)l;
			 TreeMrNode node= f.getMrNode();
			 if(node!=null && node.getName().equals(nodeName))
				 return f;
			 f=getCsFolderBean(f,nodeName);
			 if(f!=null)
				 return f;
		 }
		 return f;
	}
	
	private static void doLoad(HTree2 tree,CsFolderBean parent, TreeMrNode robjNode,String spliter,String[] parts){
		List<LeafBean> subNodes=parent.getNodeBeans();
		if(parts.length==1) {
			CsFolderBean csFolderBean=new CsFolderBean(RedisUtil.shortName(robjNode.getName(),spliter));
			csFolderBean.setMrNode(robjNode);
			csFolderBean.setFontType(CsTreeUtil.getFontType(robjNode));
			tree.add(parent.getId(), csFolderBean);
			return;
		}
		if(parts.length>1) {
			boolean exist=false;
			String name=parts[0];
			CsFolderBean f=null;
			for(LeafBean l:subNodes) {
				f=(CsFolderBean)l;
				if(name.equals(f.getName())) {
					exist=true;
					break;
				}
			}
			if(!exist) {
				f=new CsFolderBean(name);
				tree.add(parent.getId(), f);
			}
			doLoad(tree,f,robjNode,spliter,ArrayUtils.remove(parts, 0));
		}
	}
}
