package com.hh.hhdb_admin.mgr.obj_query.handler;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.treeMr.base.EventType;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.hhdb_admin.mgr.obj_query.comp.AbsQueryComp;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.CsTree;
import com.hh.hhdb_admin.mgr.tree.handler.RightMenuUtil;
import com.hh.hhdb_admin.mgr.tree.handler.action.AbsHandler;
import com.hh.hhdb_admin.mgr.tree.handler.action.RunHandler;

/**
 * @author ouyangxu
 * @date 2021-07-28 0028 16:24:14
 */
public class ObjRightMenuActionHandler {
	protected String schemaName;
	protected String tableName;
	protected AbsQueryComp queryComp;

	private final HDialog parent;
	private final HTable table;

	public ObjRightMenuActionHandler(HDialog parent, HTable table) {
		this.parent = parent;
		this.table = table;
	}

	/**
	 * 处理单选
	 *
	 * @param treeNodes 节点
	 * @param actionCmd 事件
	 * @param loginBean 连接信息
	 */
	public void resolve(EventType actionCmd, JdbcBean jdbc, CsTree tree, CsFolderBean... treeNodes) {
		try {
			if (queryComp.dbTypeEnum == DBTypeEnum.redis && treeNodes.length > 1)
				return;
			AbsHandler handler = getObjInstance(actionCmd);
			if (handler == null) {
				return;
			}
			handler.setJdbcBean(jdbc);
			handler.setCsTree(tree);
			handler.setSchemaName(schemaName);
			handler.setTableName(getTableName());
			if (handler instanceof RunHandler) {
				((RunHandler) handler).initPack(treeNodes[0].getName());
			}

			TreeMrType type = treeNodes[0].getType();
			if (queryComp.dbTypeEnum == DBTypeEnum.redis && actionCmd == EventType.DESIGN && (type == TreeMrType.HASH
					|| type == TreeMrType.ZSET || type == TreeMrType.SET || type == TreeMrType.LIST)) {
				treeNodes[0].setId(null); // 防止误操作不同树对象中同id的节点
			}
			handler.resolve(treeNodes[0]);
			if (queryComp != null) {
				if (actionCmd == EventType.REMOVE_PARTITION
						|| (queryComp.dbTypeEnum == DBTypeEnum.redis && actionCmd == EventType.MOVE)) {
					queryComp.search();
				}
			}
		} catch (Exception e) {
			Lg.error(ObjRightMenuActionHandler.class, e);
			PopDlgUtil.error(e);
		}
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setQueryComp(AbsQueryComp queryComp) {
		this.queryComp = queryComp;
	}

	private AbsHandler getObjInstance(EventType actionCmd) {
		switch (actionCmd) {
		case DELETE:
			return new ObjDeleteHandler(queryComp, table, parent);
		case REFRESH:
			return new ObjRefreshHandler(queryComp);
		case ATTRIBUTE:
			return new ObjAttributeHandler();
		case RENAME:
			return new ObjRenameHandler(queryComp);
		case DESIGN:
			return new ObjDesignHandler(queryComp);
		default:
			return RightMenuUtil.getHandler(actionCmd);
		}
	}

}
