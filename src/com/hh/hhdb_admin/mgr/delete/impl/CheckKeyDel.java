package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.DelUtil;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

/**
 * @Author: Jiang
 * @Date: 2021/9/15 15:45
 */
public class CheckKeyDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        DelUtil.constraintDel(this, nodeInfo);
    }
}
