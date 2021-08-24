package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.treeMr.base.EventType;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.tree.handler.RightMenuActionHandler;

import java.sql.SQLException;

public class MultiChooseHandler extends AbsHandler {

    private final HTreeNode[] treeNodes;
    private final EventType actionCmd;

    public MultiChooseHandler(HTreeNode[] treeNodes, String actionCmd) {
        this.treeNodes = treeNodes;
        this.actionCmd = EventType.valueOf(actionCmd.toUpperCase());
    }

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        if (treeNodes.length == 0) {
            return;
        }
        boolean isSuccess = true;
        switch (actionCmd) {
            case DELETE:
                if (!PopPaneUtil.confirm(getLang("sure_delete"))) {
                    return;
                }
                isSuccess = true;
                for (HTreeNode item : treeNodes) {
                    AbsHandler handler = RightMenuActionHandler.getInstance("delete");
                    if (handler == null) {
                        return;
                    }
                    DeleteHandler deleteHandler = (DeleteHandler) handler;
                    deleteHandler.setLoginBean(loginBean);
                    deleteHandler.setSchemaName(schemaName);
                    try {
                        deleteHandler.resolveMulti(item);
                    } catch (Exception exception) {
                        isSuccess = false;
                        deleteHandler.isMulti = false;
                        exception.printStackTrace();
                        PopPaneUtil.error(StartUtil.parentFrame.getWindow(), item.getName() + "--" + exception);
                        break;
                    }
                }
                break;
            case CASCADE_DELETE:
                if (!PopPaneUtil.confirm(getLang("sure_delete"))) {
                    return;
                }
                isSuccess = true;
                for (HTreeNode item : treeNodes) {
                    try {
                        AbsHandler handler = RightMenuActionHandler.getInstance("cascadeDelete");
                        if (handler == null) {
                            return;
                        }
                        CascadeDeleteHandler deleteHandler = (CascadeDeleteHandler) handler;
                        deleteHandler.setLoginBean(loginBean);
                        deleteHandler.resolveMulti(item);
                    } catch (Exception exception) {
                        isSuccess = false;;
                        exception.printStackTrace();
                        PopPaneUtil.error(StartUtil.parentFrame.getWindow(), item.getName() + "--" + exception);
                        break;
                    }
                }
                break;
            default:
        }
        if (isSuccess) {
            PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("delete_success"));
        }
    }

    public void update(String sql, String... params) {
        try {
            SqlExeUtil.executeUpdate(loginBean.getConn(), String.format(sql, (Object[]) params));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
