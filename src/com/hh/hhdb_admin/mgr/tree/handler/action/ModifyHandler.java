package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.column.ColumnBean;
import com.hh.hhdb_admin.mgr.table2.TableBean;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.view2.ViewBean;

public class ModifyHandler extends AbsHandler{

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {

        String schemaName = getSchemaName();
        String tableName = getTableName();
        TreeMrType nodeType = treeNode.getType();

        switch (nodeType) {
            case TABLE:
            case PAR_TABLE:
                TableBean tableBean = new TableBean(TableBean.OprType.MODIFY,treeNode.getFolder().getId());
                tableBean.setSchema(schemaName);
                tableBean.setTableName(tableName);
                sendMsg(CsMgrEnum.TABLE, tableBean);
                break;
            case COLUMN:
                ColumnBean columnBean = new ColumnBean(ColumnBean.ColOprType.MODIFY,treeNode.getFolder().getId());
                columnBean.setSchema(schemaName);
                columnBean.setTableName(tableName);
                columnBean.setColumnName(treeNode.getName());
                sendMsg(CsMgrEnum.COLUMN, columnBean);
                break;
            case VIEW:
                ViewBean viewBean = new ViewBean(ViewBean.OprType.MODIFY,treeNode.getFolder().getId());
                viewBean.setSchema(schemaName);
                viewBean.setName(treeNode.getName());
                sendMsg(CsMgrEnum.VIEW, viewBean);
            default:
                break;
        }
    }
}
