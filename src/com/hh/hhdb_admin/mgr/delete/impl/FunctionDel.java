package com.hh.hhdb_admin.mgr.delete.impl;

import java.sql.Connection;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;
import com.hh.hhdb_admin.mgr.function.ui.from.FunBaseForm;
import com.hh.hhdb_admin.mgr.function.util.FunUtil;

/**
 * @Author: Jiang
 * @Date: 2021/9/9 12:19
 */
public class FunctionDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String nodeName = nodeInfo.getName();
        String schemaName = nodeInfo.getSchemaName();
        String id = nodeInfo.getId();
        TreeMrNode treeNode = new TreeMrNode(nodeName, id, TreeMrType.FUNCTION, "");
        treeNode.setSchemaName(schemaName);
        AbsFunMr funMr = AbsFunMr.genFunMr(dbType, treeNode);
        Connection conn = ConnUtil.getConn(jdbcBean);
        FunBaseForm funForm = FunUtil.getFunBaseForm(funMr, conn, jdbcBean, true);
        if (funForm == null) {
            throw new Exception("删除失败");
        }
        String sql = funForm.getDelSql();
        conn.close();
        execute(sql);
    }
}
