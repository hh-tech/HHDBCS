package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.lg.Lg;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.CsTree;



public abstract class AbsHandler {

    protected JdbcBean jdbc;
    protected CsTree tree;

    protected String schemaName;
    protected String tableName;

    public abstract void resolve(CsFolderBean treeNode) throws Exception;

    public void resolve(CsFolderBean[] treeNodes) throws Exception {
    }

    public AbsHandler setJdbcBean(JdbcBean jdbc) {
        this.jdbc = jdbc;
        return this;
    }

    public void setCsTree(CsTree tree) {
        this.tree = tree;
    }

    /**
     * 发送消息到对应插件
     *
     * @param mgr    目标插件
     * @param resMsg 消息
     */
    public void sendMsg(CsMgrEnum mgr, BaseJsonBean bean) {
        Lg.debug(getClass(), mgr + "---" + bean.getJobj().toPrettyString());
        if (CsVarUtil.eng2 != null) {
            CsVarUtil.eng2.doCall(mgr, bean);
        }
    }
    
    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getTableName() {
        return tableName;
    }
}
