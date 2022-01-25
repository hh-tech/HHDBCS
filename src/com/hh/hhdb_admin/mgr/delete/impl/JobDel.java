package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

public class JobDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String nodeName = nodeInfo.getName();
        String schema = nodeInfo.getSchemaName();
        if (dbType == DBTypeEnum.oracle) {
            execute(String.format("call dbms_scheduler.drop_job('%s.%s')", schema, nodeName));
        }
    }
}

