package com.hh.hhdb_admin.mgr.obj_query.handler;

import com.hh.frame.create_dbobj.treeMr.base.EventType;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.tab.HTable;
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
    @Override
    public void resolve(String actionCmd, LoginBean loginBean, CsTree tree, HTreeNode... treeNodes) {
        try {
            AbsHandler handler = getObjInstance(actionCmd, treeNodes);
            if (handler == null) {
                return;
            }
            handler.setLoginBean(loginBean);
            handler.setCsTree(tree);
            handler.setSchemaName(schemaName);
            handler.setTableName(treeNodes[0].getName());
            if (handler instanceof RunHandler) {
                ((RunHandler) handler).initPack(treeNodes[0].getName());
            }
            handler.resolve(treeNodes[0]);
            if (EnumUtils.isValidEnum(EventType.class, actionCmd.toUpperCase(Locale.ROOT)) && queryComp != null) {
                if (EventType.valueOf(actionCmd.toUpperCase(Locale.ROOT)) == EventType.REMOVE_PARTITION) {
                    queryComp.search();
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

    private AbsHandler getObjInstance(String actionCmd, HTreeNode... treeNodes) {
        switch (EventType.valueOf(actionCmd.toUpperCase(Locale.ROOT))) {
            case DELETE:
                return new ObjDeleteHandler(queryComp, table, parent, treeNodes);
            case REFRESH:
                return new ObjRefreshHandler(queryComp);
            case ATTRIBUTE:
                return new ObjAttributeHandler();
            case RENAME:
                return new ObjRenameHandler(parent, queryComp);
            default:
                return RightMenuActionHandler.getInstance(actionCmd);
        }
    }

}
