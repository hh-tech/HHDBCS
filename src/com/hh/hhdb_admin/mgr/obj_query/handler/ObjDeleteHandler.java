package com.hh.hhdb_admin.mgr.obj_query.handler;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.redis.base.RedisUtil;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tree2.FolderBean;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.obj_query.comp.AbsQueryComp;
import com.hh.hhdb_admin.mgr.redis_obj.RedisObjUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;
import com.hh.hhdb_admin.mgr.tree.handler.action.AbsHandler;

import java.sql.Connection;

/**
 * @author ouyangxu
 * @date 2021-07-29 0029 14:17:36
 */
public class ObjDeleteHandler extends AbsHandler {
    protected AbsQueryComp queryComp;
    protected HTable table;
    protected HDialog parent;

    public ObjDeleteHandler(AbsQueryComp queryComp, HTable table, HDialog parent) {
        this.queryComp = queryComp;
        this.table = table;
        this.parent = parent;
    }

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        if (PopDlgUtil.confirm(parent, LangMgr.sLang(CsL.DEFINITIVE_DELETION))) {
            if (queryComp.dbTypeEnum == DBTypeEnum.redis) {
                Connection conn = null;
                try {
                    conn = CsVarUtil.newConn(jdbc);
                    RedisUtil.switch2Db(conn,schemaName);
                    RedisObjUtil.getRedisObj(conn,treeNode.getName()).delete();
                    FolderBean folder=treeNode.getFolder();
                    if(folder!=null)
                    	CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(folder.getId()));
                    queryComp.search();
                } finally {
                    if (conn != null) ConnUtil.close(conn);
                }
            } else {
            	System.out.println("需要重新编写");
//                List<NodeInfo> nodeInfos = new ArrayList<>();
//                for (HTabRowBean rowBean : table.getSelectedRowBeans()) {
//                    Map<String, String> map = rowBean.getOldRow();
//                    NodeInfo nodeInfo = new NodeInfo();
//                    nodeInfo.setName(map.get("name"));
//                    nodeInfo.setId(map.get("object_name"));
//                    nodeInfo.setTableName(map.get("object_name"));
//                    nodeInfo.setSchemaName(schemaName);
//                    nodeInfo.setTreeMrType(TreeMrType.valueOf(map.get("type")));
//                    nodeInfo.setOperType(OperateType.DEL);
//                    nodeInfos.add(nodeInfo);
//                }
//                new DeleteComp(nodeInfos, jdbc, parent) {
//                    @Override
//                    public void refresh() {
//                        queryComp.search();
//                    }
//                };
            }
        }
    }
}
