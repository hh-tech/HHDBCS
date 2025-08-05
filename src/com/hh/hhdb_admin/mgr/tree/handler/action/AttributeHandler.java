package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.attribute.AttributeBean;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.CsTreeUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: Jiang
 * @date: 2020/10/14
 */

public class AttributeHandler extends AbsHandler {

	@Override
	public void resolve(CsFolderBean treeNode) throws Exception {
		sendMsg(CsMgrEnum.ATTRIBUTE, initJsonObject(treeNode));
	}

	protected AttributeBean initJsonObject(CsFolderBean folderBean) {
		AttributeBean bean = new AttributeBean();
		bean.setNodeType(folderBean.getType());
		if(DriverUtil.getDbType(jdbc)==DBTypeEnum.redis) 
			bean.setName(folderBean.getMrNode().getName());
		else
			bean.setName(folderBean.getName());
		bean.setOid(folderBean.getObjectId() == null ? "" : folderBean.getObjectId());
		
		String schemaName = getSchemaName();
		bean.setSchema(StringUtils.isNoneBlank(schemaName) ? schemaName : "");

		String tableName = getTableName();
		if (StringUtils.isNotBlank(tableName)) {
			bean.setTableName(tableName);
		} else {
			tableName = CsTreeUtil.getPartitionTableName(folderBean, true);
			if (StringUtils.isNotBlank(tableName)) {
				bean.setTableName(tableName);
			}
		}
		return bean;
	}

}
