package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.attribute.AttributeMgr;
import com.hh.hhdb_admin.mgr.tree.TreeUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: Jiang
 * @date: 2020/10/14
 */

public class AttributeHandler extends AbsHandler {

	@Override
	public void resolve(HTreeNode treeNode) throws Exception {
		sendMsg(CsMgrEnum.ATTRIBUTE, initJsonObject(treeNode));
	}

	protected JsonObject initJsonObject(HTreeNode treeNode) {
		JsonObject jsonObject = GuiJsonUtil.toJsonCmd(AttributeMgr.SHOW_ATTR);
		jsonObject.add("nodeType", treeNode.getType());
		jsonObject.add("name", treeNode.getName());
		jsonObject.add("oid", treeNode.getId() == null ? "" : treeNode.getId());

		String schemaName = getSchemaName();
		jsonObject.add("schemaName", StringUtils.isNoneBlank(schemaName) ? schemaName : "");

		String tableName = getTableName();
		if (StringUtils.isNotBlank(tableName)) {
			jsonObject.add("tableName", tableName);
		} else {
			tableName = TreeUtil.getPartitionTableName(treeNode, true);
			if (StringUtils.isNotBlank(tableName)) {
				jsonObject.add("tableName", tableName);
			}
		}
		return jsonObject;
	}

}
