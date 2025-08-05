package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.function2.FunctionBean;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;

/**
 * 添加函数调试信息
 * @author: Jiang
 * @date: 2021/06/29
 * <p>
 */

public class AddDebugInfoHandler extends AbsHandler {

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        FunctionBean functionBean = new FunctionBean(FunctionBean.OprType.ADD_DEBUG_INFO,null);
        functionBean.setSchema(getSchemaName());
        functionBean.setName(treeNode.getName());
        functionBean.setNodeType(treeNode.getType());
        sendMsg(CsMgrEnum.FUNCTION, functionBean);
    }

}
