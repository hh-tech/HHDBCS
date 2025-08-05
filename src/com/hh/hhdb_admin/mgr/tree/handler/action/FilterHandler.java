package com.hh.hhdb_admin.mgr.tree.handler.action;

import java.util.List;
import org.apache.commons.lang3.StringUtils;

import com.hh.frame.create_dbobj.treeMr.base.MongoDocGroupNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tree2.LeafBean;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;

public class FilterHandler extends AbsHandler {

	@Override
	public void resolve(CsFolderBean csFolder) throws Exception {
		if(doMongoDocFilter(csFolder)) {
			return;
		}
		String filter = csFolder.getMrNode().getFilter();
		String value = PopDlgUtil.nameInput("筛选", filter);
		
		if (value == null)
			//点击取消
			return;
		else
			value = value.trim();
		if (StringUtils.isNotBlank(value)) {
			// redis的TreeMrType.ROOT_DATA_MODEL的过滤需要支持统配符
			if (csFolder.getType() == TreeMrType.ROOT_SCHEMA_OR_DB && value.indexOf('*') < 0)
				value = "*" + value + "*";
			else
				csFolder.getMrNode().setToolTip("筛选:" + value);

		} else
			csFolder.getMrNode().setToolTip("筛选:无");
		csFolder.getMrNode().setFilter(value);
		csFolder.setTip(csFolder.getMrNode().getToolTip());
		// redis的TreeMrType.ROOT_DATA_MODEL不支持前台过滤
		if (csFolder.getType() == TreeMrType.ROOT_SCHEMA_OR_DB) {
			CsVarUtil.eng2.doCall(CsMgrEnum.TREE, new RefreshNodeBean(csFolder.getId(), false));
			return;
		}
		List<LeafBean> beans = csFolder.getNodeBeans();
		for (LeafBean l : beans) {
			if (StringUtils.isBlank(value)) {
				l.setHidden(false);
			} else if (StringUtils.containsIgnoreCase(l.getName(), value)) {
				l.setHidden(false);
			} else {
				l.setHidden(true);
			}
		}
		CsVarUtil.eng2.doCall(CsMgrEnum.TREE, new RefreshNodeBean(csFolder.getId(), true));
	}
	
	private boolean doMongoDocFilter(CsFolderBean csFolder) {
		TreeMrNode treeMrNode=csFolder.getMrNode();
		if(treeMrNode instanceof MongoDocGroupNode) {
			MongoDocGroupNode objNode=(MongoDocGroupNode)csFolder.getMrNode();
			objNode.setRefreshId(csFolder.getId());
			CsVarUtil.eng2.doShare(CsMgrEnum.MONGO_MGR, objNode);
			return true;
		}
		return false;
	}
	
}
