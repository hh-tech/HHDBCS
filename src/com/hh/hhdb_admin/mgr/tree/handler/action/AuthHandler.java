package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.function2.FunctionBean;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import org.apache.commons.lang3.StringUtils;

/**
 * 检查函数
 * @author: Jiang
 * @date: 2020/9/15
 */

public class AuthHandler extends AbsHandler {
    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        String schemaName =getSchemaName();
        TreeMrType nodeType = treeNode.getType();
    
        switch (nodeType) {
            case FUNCTION:
                FunctionBean functionBean = new FunctionBean(FunctionBean.OprType.EXAMINE,null);
                functionBean.setSchema(schemaName);
                functionBean.setName(treeNode.getName());
                functionBean.setId(StringUtils.isBlank(treeNode.getObjectId()) ? "" : treeNode.getObjectId());
                functionBean.setNodeType(nodeType);
                sendMsg(CsMgrEnum.FUNCTION, functionBean);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + nodeType);
        }
    }
}
