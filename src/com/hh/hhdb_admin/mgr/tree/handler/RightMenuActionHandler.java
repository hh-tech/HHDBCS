package com.hh.hhdb_admin.mgr.tree.handler;

import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.tree.CsTree;
import com.hh.hhdb_admin.mgr.tree.TreeUtil;
import com.hh.hhdb_admin.mgr.tree.handler.action.AbsHandler;
import com.hh.hhdb_admin.mgr.tree.handler.action.MultiChooseHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: Jiang
 * @date: 2020/9/14
 */

public class RightMenuActionHandler {

	protected static final Pattern LINE_PATTERN = Pattern.compile("_(\\w)");
	protected static final Map<String, AbsHandler> RESOLVE_MAP = new HashMap<>();

	public static AbsHandler getInstance(String actionCmd) {
		AbsHandler handler;
		if (RESOLVE_MAP.containsKey(actionCmd)) {
			handler = RESOLVE_MAP.get(actionCmd);
		} else {
			String targetClass = RightMenuActionHandler.class.getPackage().getName() + ".action." + lineToHump(actionCmd) + "Handler";
			try {
				handler = (AbsHandler) Class.forName(targetClass).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
			RESOLVE_MAP.put(actionCmd, handler);
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
	public void resolve(String actionCmd, LoginBean loginBean, CsTree tree, HTreeNode... treeNodes) {
		try {
			AbsHandler handler;
			String schemaName = TreeUtil.getSchemaName(treeNodes[0], DriverUtil.getDbType(loginBean.getConn()));
			String tableName = TreeUtil.getTableName(treeNodes[0]);

			if (treeNodes.length > 1) {
				handler = new MultiChooseHandler(treeNodes, actionCmd);
				handler.setLoginBean(loginBean).setCsTree(tree);
			} else {
				handler = getInstance(actionCmd);
				if (handler == null) {
					return;
				}
				handler.setLoginBean(loginBean);
				handler.setCsTree(tree);

			}
			handler.setSchemaName(schemaName);
			handler.setTableName(tableName);
			handler.resolve(treeNodes.length > 1 ? null : treeNodes[0]);
		} catch (Exception e) {
			e.printStackTrace();
			PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
		}
	}

	public static String lineToHump(String str) {
		Matcher matcher = LINE_PATTERN.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);
		return String.valueOf(sb.charAt(0)).toUpperCase() + sb.substring(1);
	}

}
