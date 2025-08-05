package com.hh.hhdb_admin.mgr.obj_query.handler;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.hhdb_admin.mgr.obj_query.comp.AbsQueryComp;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.handler.action.RenameHandler;

public class ObjRenameHandler extends RenameHandler {
    private final AbsQueryComp queryComp;

    public ObjRenameHandler(AbsQueryComp queryComp) {
       this.queryComp = queryComp;
    }

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        if (TreeMrType.TABLE == treeNode.getType() || TreeMrType.SEQUENCE == treeNode.getType() || queryComp.dbTypeEnum == DBTypeEnum.redis) {
            super.resolve(treeNode);
            queryComp.search();
        }
    }
}
