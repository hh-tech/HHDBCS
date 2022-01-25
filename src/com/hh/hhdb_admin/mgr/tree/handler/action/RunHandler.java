package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: Jiang
 * @date: 2020/9/15
 */

public class RunHandler extends AbsHandler {

    private String packageName;
    private String nodeName;

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        String schemaName = getSchemaName();
        TreeMrType nodeType = TreeMrType.valueOf(treeNode.getType().toUpperCase());
        switch (nodeType) {
            case FUNCTION:
            case PROCEDURE:
                sendMsg(CsMgrEnum.FUNCTION, GuiJsonUtil.toJsonCmd(FunctionMgr.RUN_FUNCTION)
                        .add(FunctionMgr.PARAM_FUNC_NAME, treeNode.getName())
                        .add(FunctionMgr.PARAM_FUNC_ID, StringUtils.isBlank(treeNode.getId()) ? "" : treeNode.getId())
                        .add(StartUtil.PARAM_SCHEMA, schemaName).add(FunctionMgr.TYPE, nodeType.toString()));
                break;
            case PACKAGE_FUNCTION:
            case PACKAGE_PROCEDURE:
                nodeName = treeNode.getName();
                if (nodeName.contains(".")) {
                    packageName = treeNode.getName().split("\\.")[0];
                    nodeName = treeNode.getName().split("\\.")[1];
                } else {
                    packageName = treeNode.getParentHTreeNode().getParentHTreeNode().getParentHTreeNode().getName();
                }
                sendMsg(CsMgrEnum.FUNCTION, GuiJsonUtil.toJsonCmd(FunctionMgr.RUN_FUNCTION)
                        .add(FunctionMgr.PARAM_FUNC_NAME, nodeName)
                        .add(FunctionMgr.PARAM_FUNC_ID, StringUtils.isBlank(treeNode.getId()) ? "" : treeNode.getId())
                        .add(StartUtil.PARAM_SCHEMA, schemaName).add(FunctionMgr.TYPE, nodeType.toString())
                        .add(FunctionMgr.PARAM_PACKNAME, packageName));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + nodeType);
        }
    }

    public void initPack(String nodeName) {
        if (StringUtils.isBlank(nodeName)) {
            return;
        }
        String[] strArr = nodeName.split("\\.");
        if (strArr.length != 2) {
            return;
        }
        this.packageName = strArr[0];
        this.nodeName = strArr[1];
    }
}
