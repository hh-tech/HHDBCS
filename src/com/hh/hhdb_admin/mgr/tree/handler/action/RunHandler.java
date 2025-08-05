package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.function2.FunctionBean;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import org.apache.commons.lang3.StringUtils;

/**
 * 运行函数
 */

public class RunHandler extends AbsHandler {

    private String packageName;
    private String nodeName;

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        String schemaName = getSchemaName();
        TreeMrType nodeType = treeNode.getType();
        switch (nodeType) {
            case FUNCTION:
            case PROCEDURE:
                FunctionBean functionBean = new FunctionBean(FunctionBean.OprType.RUN,null);
                functionBean.setSchema(schemaName);
                functionBean.setName(treeNode.getName());
                functionBean.setId(StringUtils.isBlank(treeNode.getObjectId()) ? "" : treeNode.getObjectId());
                functionBean.setNodeType(nodeType);
                sendMsg(CsMgrEnum.FUNCTION, functionBean);
                break;
            case PACKAGE_FUNCTION:
            case PACKAGE_PROCEDURE:
                nodeName = treeNode.getName();
                if (nodeName.contains(".")) {
                    packageName = treeNode.getName().split("\\.")[0];
                    nodeName = treeNode.getName().split("\\.")[1];
                } else {
                    packageName = treeNode.getFolder().getFolder().getFolder().getName();
                }
                functionBean = new FunctionBean(FunctionBean.OprType.RUN,null);
                functionBean.setSchema(schemaName);
                functionBean.setName(nodeName);
                functionBean.setId(StringUtils.isBlank(treeNode.getObjectId()) ? "" : treeNode.getObjectId());
                functionBean.setNodeType(nodeType);
                functionBean.setPackName(packageName);
                sendMsg(CsMgrEnum.FUNCTION, functionBean);
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
