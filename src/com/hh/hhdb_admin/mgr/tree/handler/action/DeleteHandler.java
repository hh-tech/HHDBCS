package com.hh.hhdb_admin.mgr.tree.handler.action;


import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.DBTypeEnum.Version;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.db_sql.mysql.MysqlUserUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.delete.DeleteBean;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.CsTreeUtil;

/**
 * 右键单节点删除处理类
 */

public class DeleteHandler extends AbsHandler {

    protected boolean isCascade = false;
    protected boolean isMulti = false;
    private DBTypeEnum dbtype=CsVarUtil.getDbtype();
    private Version ver=CsVarUtil.jdbc.getCurrentVersion();

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        resolve(new CsFolderBean[]{treeNode});
    }

    @Override
    public void resolve(CsFolderBean[] treeNodes) throws Exception {
        if (!PopDlgUtil.confirm(LangMgr.lang(Word4.SURE_DELETE))) return;
        dbtype=CsVarUtil.getDbtype();
        CsFolderBean treeNode = treeNodes[0];
        TreeMrType treeNodeType = treeNode.getType();

        DeleteBean deleteBean = new DeleteBean();
        for (CsFolderBean item : treeNodes) {
            if ((DBTypeEnum.isPgSeries(dbtype, ver) || dbtype == DBTypeEnum.db2 || DBTypeEnum.isInformixSeries(dbtype, ver))
                    && (treeNode.getType() == TreeMrType.FUNCTION || treeNode.getType() == TreeMrType.PROCEDURE)) {
                deleteBean.addObjName(item.getTip());
            } else if (DBTypeEnum.isMysqlSeries(dbtype, ver) && treeNode.getType() == TreeMrType.USER) {
                deleteBean.addObjName(MysqlUserUtil.toMysqlUsrHost(item.getName()));
            } else if (dbtype==DBTypeEnum.redis){
            	deleteBean.addObjName(item.getMrNode().getName());
            }else {
                deleteBean.addObjName(item.getName());
            }
        }
        deleteBean.setNodeType(treeNodeType);

        if(dbtype==DBTypeEnum.redis) {
        	CsFolderBean rootSchema=CsTreeUtil.getTargetParentNode(treeNode, TreeMrType.ROOT_SCHEMA_OR_DB);
        	deleteBean.setRefreshNodeId(rootSchema.getId());
        }else {
        	deleteBean.setSchema(getSchemaName());
            deleteBean.setTableName(getTableName());
            deleteBean.setRefreshNodeId(treeNode.getFolder().getId());
        }
        

        sendMsg(CsMgrEnum.DELETE, deleteBean);
    }

    public void cascadeResolve(CsFolderBean treeNode) throws Exception {
        this.isCascade = true;
        resolve(treeNode);
        this.isCascade = false;
    }

    public void resolveMulti(CsFolderBean treeNode) throws Exception {
        this.isMulti = true;
        resolve(treeNode);
        this.isMulti = false;
    }

    public void cascadeMultiResolve(CsFolderBean treeNode) throws Exception {
        this.isMulti = this.isCascade = true;
        resolve(treeNode);
        this.isCascade = this.isMulti = false;
    }
}
