package com.hh.hhdb_admin.mgr.delete.impl;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

/**
 * @Author: Jiang
 * @Date: 2021/9/15 16:12
 */
public class SynonymDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String schemaName = nodeInfo.getSchemaName();
        String name = nodeInfo.getName();
        execute(String.format("drop synonym %s.%s", schemaName, name));
    }
}
