package com.hh.hhdb_admin.mgr.tree.handler;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.RedisObjGroupNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.mr.AbsTreeMr;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.tree2.HTree2;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.CsTreeUtil;
import org.apache.commons.lang3.StringUtils;


import java.util.List;

public class LeftDoubleClickHandler {
    private final HTree2 tree;
   	private JdbcBean jdbcBean = CsVarUtil.jdbcCloneWithSsh();
    private DBTypeEnum typeEnum=DriverUtil.getDbType(jdbcBean);
    private AbsTreeMr treeMr=null;
    public LeftDoubleClickHandler(HTree2 tree) {
        this.tree = tree;
    }

    /**
     * 刷新指定节点
     *
     * @param csFolder 节点
     * @throws Exception 
     */
    public void refreshNode(String nodeId) throws Exception {
    	CsFolderBean csNode=(CsFolderBean)tree.get(nodeId);
    	if(csNode!=null) {
    		tree.cleanFolder(nodeId);
            onClick(csNode);
    	}
    }

    /**
     * 加载指定节点的所有子节点
     *
     * @param csFolder 节点
     * @throws Exception e
     */
	public void onClick(CsFolderBean csFolder) throws Exception {
		
		if (csFolder.getNodeBeans().size() > 0)
			return;
		CsVarUtil.setWaitingCursor();
		try {
			if(treeMr==null)treeMr=AbsTreeMr.genTreeMgr(jdbcBean, LangMgr.langType);
			tree.cleanFolder(csFolder.getId());
			TreeMrNode treeMrNode=csFolder.getMrNode();
			
			
			if(treeMrNode==null) {
				treeMrNode = new TreeMrNode(csFolder.getName(), csFolder.getType());
				csFolder.setMrNode(treeMrNode);
			}
			if(StringUtils.isBlank(treeMrNode.getSchemaName()))treeMrNode.setSchemaName(CsTreeUtil.getSchemaName(csFolder, typeEnum));
			if(StringUtils.isBlank(treeMrNode.getTableName()))treeMrNode.setTableName(CsTreeUtil.getTableName(csFolder));
			if(StringUtils.isBlank(treeMrNode.getPackageName()))treeMrNode.setPackageName(CsTreeUtil.getPackageName(csFolder));
			
			List<TreeMrNode> allList  = treeMr.getChildNodeList(treeMrNode, CsVarUtil.conn);
			//redis暂时不支持这种过滤
			if(typeEnum != DBTypeEnum.redis && treeMrNode.getType().name().endsWith("GROUP")) {
				String nodeName=StringUtils.substringBefore(csFolder.getName(), "(");
				if(treeMrNode.getPageBean()==null) 
					nodeName=nodeName+String.format("(%d)", allList.size());
				else
					nodeName=nodeName+String.format("(%d)", treeMrNode.getPageBean().getTotalNum());
				if(StringUtils.isNotBlank(treeMrNode.getFilter())) {
					nodeName=nodeName+String.format("[%s]", treeMrNode.getFilter());
				}
				csFolder.setName(nodeName);
			}
			
			if(treeMrNode instanceof RedisObjGroupNode) {
				PkgRedisObjUtil.loadNodes4Redis(tree,csFolder, allList);
			}else {
				for (TreeMrNode t : allList) {
					CsFolderBean newNode = new CsFolderBean(t.getName());
					newNode.setFontType(CsTreeUtil.getFontType(t));
					if(StringUtils.isNoneBlank(t.getToolTip()))
						newNode.setTip(t.getToolTip());
					newNode.setMrNode(t);
					tree.add(csFolder.getId(), newNode);
				}
				//查询完成后设置提示
				if(StringUtils.isNoneBlank(treeMrNode.getToolTip()))
					csFolder.setTip(treeMrNode.getToolTip());
				tree.reload(csFolder.getId());
				tree.expandFolder(csFolder.getId());
			}
			
		} catch (Exception ex) {
			Lg.error(getClass(), ex);
			throw ex;
		} finally {
			CsVarUtil.setDefaultCursor();
		}
	}
	

}
