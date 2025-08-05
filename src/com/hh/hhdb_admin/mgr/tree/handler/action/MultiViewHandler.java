package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.multi_view.MultiViewBean;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;

/**
 * 处理批量查看事件
 *
 */

public class MultiViewHandler extends AbsHandler {

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        switch (treeNode.getType()) {
            case COLUMN_GROUP:
                resolve(new CsFolderBean[] {});
                break;
            default:
                resolve(new CsFolderBean[] {treeNode});
        }
    }

    @Override
    public void resolve(CsFolderBean[] treeNodes) throws Exception {
        MultiViewBean bean = new MultiViewBean();
        bean.setSchema(getSchemaName());
        bean.setTableName(getTableName());
        for (CsFolderBean treeNode : treeNodes) {
            bean.addId(treeNode.getName());
        }
        sendMsg(CsMgrEnum.MULTI_VIEW, bean);
    }
}
