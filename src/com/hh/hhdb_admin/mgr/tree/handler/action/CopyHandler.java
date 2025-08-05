package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.column.ColumnBean;
import com.hh.hhdb_admin.mgr.table2.TableBean;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;

public class CopyHandler  extends AbsHandler{
    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        switch (treeNode.getType()) {
            case TABLE:
                TableBean tableBean = new TableBean(TableBean.OprType.COPY,treeNode.getFolder().getId());
                tableBean.setSchema(schemaName);
                tableBean.setTableName(tableName);
                sendMsg(CsMgrEnum.TABLE, tableBean);
                break;
            case COLUMN:
                ColumnBean columnBean = new ColumnBean(ColumnBean.ColOprType.COPY,treeNode.getFolder().getId());
                columnBean.setSchema(schemaName);
                columnBean.setTableName(tableName);
                columnBean.setColumnName(treeNode.getName());
                sendMsg(CsMgrEnum.COLUMN, columnBean);
            default:
                break;
        }
    }
}
