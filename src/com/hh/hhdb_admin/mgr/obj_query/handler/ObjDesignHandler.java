package com.hh.hhdb_admin.mgr.obj_query.handler;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;

import com.hh.hhdb_admin.mgr.obj_query.comp.AbsQueryComp;
import com.hh.hhdb_admin.mgr.redis_obj.AddUpdRedisObjComp;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.handler.action.DesignHandler;


/**
 * @author: Jiang
 * @date: 2020/9/15
 */

public class ObjDesignHandler extends DesignHandler {
    private final AbsQueryComp queryComp;
    
    public ObjDesignHandler(AbsQueryComp queryComp) {
        this.queryComp = queryComp;
    }
    
    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        if (queryComp.dbTypeEnum == DBTypeEnum.redis && (TreeMrType.UNSUPPORT != treeNode.getType() )) {
//            new AddUpdRedisObjComp(schemaName, null){
//                public boolean updateRefresh() {
//                    Connection conn = null;
//                    try {
//                        conn = CsVarUtil.newConn(jdbc);
//                        RedisUtil.switch2Db(conn, schemaName);
//                        if (!RedisObjUtil.getRedisObj(conn, treeNode.getName()).exist()) {
////                            CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(null));
//                            queryComp.search();
//                        }
//                    }catch (Exception e){
//                        Lg.error(ObjDesignHandler.class, e);
//                    } finally {
//                        ConnUtil.close(conn);
//                    }
//                    return false;
//                }
//            }.showDialog(treeNode.getName());
        	new AddUpdRedisObjComp(schemaName, null).showDialog(treeNode.getName());
        } else {
            super.resolve(treeNode);
        }
    }
}
