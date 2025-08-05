package com.hh.hhdb_admin.mgr.tree.handler;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.EventType;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.CsTree;
import com.hh.hhdb_admin.mgr.tree.CsTreeUtil;
import com.hh.hhdb_admin.mgr.tree.handler.action.*;

import java.util.HashMap;
import java.util.Map;


public class RightMenuUtil {

	private static final  Map<EventType, AbsHandler> ACTION_MAP = new HashMap<>();
	static {
		try {
			ACTION_MAP.put(EventType.ADD, AddHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.ATTRIBUTE, AttributeHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.FILTER, FilterHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.REFRESH, RefreshHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.RUN_SCRIPT, RunScriptHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.DELETE, DeleteHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.REMOVE_PARTITION, RemovePartitionHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.BACKUP, BackupHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.RESTORE, RestoreHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.OPEN, OpenHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.MULTI_VIEW, MultiViewHandler.class.getDeclaredConstructor().newInstance());
//			ACTION_MAP.put(EventType.EDIT, EditHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.DESIGN, DesignHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.TRUNCATE, TruncateHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.CLEAN_DB, CleanDbHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.CASCADE_TRUNCATE, CascadeTruncateHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.CASCADE_DELETE, CascadeDeleteHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.IMPORT_DATA, ImportDataHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.EXPORT_DATA, ExportDataHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.RENAME, RenameHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.AUTH, AuthHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.DEBUG, DebugHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.RUN, RunHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.AUTHORIZE_MANAGEMENT, AuthorizeManagementHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.INSTALL, InstallHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.UNINSTALL, UninstallHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.REFRESH_DATA, RefreshDataHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.PURGE_RECYCLE_BIN, PurgeRecycleBinHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.PURGE_DBA_RECYCLE_BIN, PurgeDbaRecycleBinHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.FLASHBACK_TABLE, FlashbackTableHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.FLASHBACK_RENAME, FlashbackRenameHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.PURGE_OBJECT, PurgeObjectHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.ADD_DEBUG_INFO, AddDebugInfoHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.COMPILE, CompileHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.CONNECT, ConnectHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.DISCONNECT, DisconnectHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.MOVE, MoveHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.MODIFY, ModifyHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.PAGE_FILTER, PageFilterHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.COPY, CopyHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.COPY_NAME, CopyNameHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.REDIS_FILTER, RedisFilterHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.PERFORMANCE_REPORT, PerformanceReportHandler.class.getDeclaredConstructor().newInstance());
			ACTION_MAP.put(EventType.GEN_DATA, GenDataHandler.class.getDeclaredConstructor().newInstance());
//			ACTION_MAP.put(EventType.MONGO_FILTER, MongoFilterHandler.class.getDeclaredConstructor().newInstance());

		} catch (Exception e) {
			GlobalWorkUtil.err(RightMenuUtil.class,e);
		} 
	}
	

	public static AbsHandler getHandler(EventType actionCmd) {
		return ACTION_MAP.get(actionCmd);
	}

	public static void doRMenuAction(EventType actionCmd, JdbcBean jdbc, CsTree tree, CsFolderBean... treeNodes) {
		try {
			if (treeNodes.length == 0)
				return;
			CsVarUtil.setWaitingCursor();
			AbsHandler handler;
			String schemaName = CsTreeUtil.getSchemaName( treeNodes[0], DriverUtil.getDbType(jdbc));
			String tableName = CsTreeUtil.getTableName( treeNodes[0]);

			handler = getHandler(actionCmd);
			handler.setJdbcBean(jdbc).setCsTree(tree);
			handler.setSchemaName(schemaName);
			handler.setTableName(tableName);

			if (treeNodes.length == 1)
				handler.resolve(treeNodes[0]);
			else
				handler.resolve(treeNodes);
		} catch (Exception e) {
			GlobalWorkUtil.err(RightMenuUtil.class,e);
		}finally {
			CsVarUtil.setDefaultCursor();
		}
	}


}
