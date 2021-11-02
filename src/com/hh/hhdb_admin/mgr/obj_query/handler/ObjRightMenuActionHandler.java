package com.hh.hhdb_admin.mgr.obj_query.handler;

import com.hh.frame.create_dbobj.treeMr.base.EventType;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.obj_query.ObjQueryComp;
import com.hh.hhdb_admin.mgr.tree.CsTree;
import com.hh.hhdb_admin.mgr.tree.handler.RightMenuActionHandler;
import com.hh.hhdb_admin.mgr.tree.handler.action.AbsHandler;
import com.hh.hhdb_admin.mgr.tree.handler.action.RunHandler;
import org.apache.commons.lang3.EnumUtils;

import java.util.Locale;

/**
 * @author ouyangxu
 * @date 2021-07-28 0028 16:24:14
 */
public class ObjRightMenuActionHandler extends RightMenuActionHandler {
	protected String schemaName;
	protected String tableName;
	protected ObjQueryComp queryComp;

	public AbsHandler getObjInstance(String actionCmd) {
		AbsHandler handler = null;
		EventType eventType = EventType.valueOf(actionCmd.toUpperCase(Locale.ROOT));
		switch (eventType) {
			case DELETE:
				handler = new ObjDeleteHandler(queryComp);
				break;
			case REFRESH:
				handler = new ObjRefreshHandler(queryComp);
				break;
			case ATTRIBUTE:
				handler = new ObjAttributeHandler();
				break;
			default:
				handler = RightMenuActionHandler.getInstance(actionCmd);
		}
		return handler;
	}

	/**
	 * 处理单选
	 *
	 * @param treeNodes 节点
	 * @param actionCmd 事件
	 * @param loginBean 连接信息
	 */
	@Override
	public void resolve(String actionCmd, LoginBean loginBean, CsTree tree, HTreeNode... treeNodes) {
		try {
			AbsHandler handler = getObjInstance(actionCmd);
			if (handler == null) {
				return;
			}
			handler.setLoginBean(loginBean);
			handler.setCsTree(tree);

			handler.setSchemaName(schemaName);
			handler.setTableName(tableName);
			if (handler instanceof RunHandler){
				((RunHandler) handler).initPack(treeNodes[0].getName());
			}
			handler.resolve(treeNodes[0]);


			if (EnumUtils.isValidEnum(EventType.class, actionCmd.toUpperCase(Locale.ROOT)) && queryComp != null) {
				switch (EventType.valueOf(actionCmd.toUpperCase(Locale.ROOT))) {
					case RENAME:
					case REMOVE_PARTITION:
						queryComp.search();
					default:
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			PopPaneUtil.error(e.getMessage());
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

	public ObjQueryComp getQueryComp() {
		return queryComp;
	}

	public void setQueryComp(ObjQueryComp queryComp) {
		this.queryComp = queryComp;
	}
}
