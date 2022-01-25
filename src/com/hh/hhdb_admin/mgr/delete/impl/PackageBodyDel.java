package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

/**
 * @Author: Jiang
 * @Date: 2021/9/15 16:10
 */
public class PackageBodyDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String name = nodeInfo.getName();
        execute(String.format("DROP PACKAGE BODY \"%s\"", name));
    }
}
