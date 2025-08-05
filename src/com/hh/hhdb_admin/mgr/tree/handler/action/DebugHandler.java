package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.function2.FunctionBean;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;

/**
 * 调试函数
 * @author: Jiang
 * @date: 2020/9/15
 */

public class DebugHandler extends AbsHandler {
    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        String schemaName = getSchemaName();
        TreeMrType nodeType = treeNode.getType();
        if (nodeType == TreeMrType.FUNCTION || nodeType == TreeMrType.PROCEDURE) {
            FunctionBean functionBean = new FunctionBean(FunctionBean.OprType.DEBUG_FUNCTION,null);
            functionBean.setSchema(schemaName);
            functionBean.setName(treeNode.getName());
            functionBean.setId(treeNode.getObjectId());
            functionBean.setNodeType(nodeType);
            sendMsg(CsMgrEnum.FUNCTION, functionBean);
        }
        
    }
}
