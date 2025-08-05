package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.db_sql.ora.OraSqlUtil;
import com.hh.frame.dbobj3.ora.OracleCompileTool;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.comp.MultiHandleDialog;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import org.apache.commons.lang3.StringUtils;

/**
 * @author YuSai
 */
public class CompileHandler extends AbsHandler {

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        OracleCompileTool tool = getOracleCompileTool(treeNode);
        tool.compile();
        String errorMsg = tool.getErrorMsg(false);
        if (CsVarUtil.getDbtype() == DBTypeEnum.dm || StringUtils.isEmpty(errorMsg))
            PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.COMPILE_SUCCESS));
        else
            PopDlgUtil.info(CsVarUtil.parentFrame, errorMsg);
    }

    @Override
    public void resolve(CsFolderBean[] treeNodes) throws Exception {
        OracleCompileTool tool = getOracleCompileTool(treeNodes[0]);

        MultiHandleDialog multiHandleDialog = new MultiHandleDialog(CsVarUtil.parentFrame, treeNodes, treeNode -> {
            try {
                tool.setObjName(treeNode.getName());
                tool.compile();
                return CsVarUtil.getDbtype() == DBTypeEnum.dm || StringUtils.isEmpty(tool.getErrorMsg(false));
            } catch (Exception e) {
                return false;
            }
        });
        multiHandleDialog.setTitle(LangMgr.lang(Word2.COMPILE));
        multiHandleDialog.show();
    }

    private OracleCompileTool getOracleCompileTool(CsFolderBean treeNode) {
        String schemaName = getSchemaName();
        TreeMrType nodeType = treeNode.getType();
        OraSqlUtil.CompileType sessionEnum;
        String objName = treeNode.getName();
        switch (nodeType) {
            case FUNCTION:
                sessionEnum = OraSqlUtil.CompileType.function;
                break;
            case PROCEDURE:
                sessionEnum = OraSqlUtil.CompileType.procedure;
                break;
            case PACKAGE_HEAD:
                sessionEnum = OraSqlUtil.CompileType.packhead;
                objName = treeNode.getFolder().getName();
                break;
            case PACKAGE_BODY:
                sessionEnum = OraSqlUtil.CompileType.packbody;
                objName = treeNode.getFolder().getName();
                break;
            case VIEW:
                sessionEnum = OraSqlUtil.CompileType.view;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + nodeType);
        }
        return new OracleCompileTool(CsVarUtil.jdbc.getCurrentVersion(), CsVarUtil.conn, schemaName, sessionEnum, objName);
    }

}
