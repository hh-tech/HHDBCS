package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

public class DblinkDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String nodeName = nodeInfo.getName();
        String schema = nodeInfo.getSchemaName();
        if (dbType == DBTypeEnum.oracle) {
            if (nodeName.contains(".")) {
                schema = nodeName.split("\\.")[0];
                nodeName = nodeName.split("\\.")[1];
            }
            if ("PUBLIC".equals(schema)) {
            	execute(String.format("drop public database link %s", nodeName));
            } else {
            	execute(String.format("drop database link %s", nodeName));
            }
        }
    }
}
