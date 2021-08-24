package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbobj2.ora.OraSessionEnum;
import com.hh.frame.dbobj2.ora.pack.OracleCompileTool;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.common.util.StartUtil;

import java.sql.Connection;

/**
 * @author YuSai
 */
public class CompileHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        Connection conn = loginBean.getConn();
        String schemaName = getSchemaName();
        TreeMrType nodeType = TreeMrType.valueOf(treeNode.getType().toUpperCase());
        OraSessionEnum sessionEnum;
        OracleCompileTool tool;
        String objName = treeNode.getName();
        switch (nodeType) {
            case FUNCTION:
                sessionEnum = OraSessionEnum.function;
                break;
            case PROCEDURE:
                sessionEnum = OraSessionEnum.procedure;
                break;
            case PACKAGE_HEAD:
                sessionEnum = OraSessionEnum.packhead;
                objName = treeNode.getParentHTreeNode().getName();
                break;
            case PACKAGE_BODY:
                sessionEnum = OraSessionEnum.packbody;
                objName = treeNode.getParentHTreeNode().getName();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + nodeType);
        }
        tool = new OracleCompileTool(conn, schemaName, sessionEnum, objName);
        tool.compile(StartUtil.parentFrame);
    }

}
