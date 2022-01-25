package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

/**
 * @Author: Jiang
 * @Date: 2021/9/9 16:30
 */
public class TypeBodyDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String nodeName = nodeInfo.getName();
        String schemaName = nodeInfo.getSchemaName();
        if (dbType == DBTypeEnum.oracle) {
            String sql = String.format("DROP TYPE body %s.%s", SqlStrUtil.dealDoubleQuote(dbType, schemaName),
                    SqlStrUtil.dealDoubleQuote(dbType, nodeName));
            execute(sql);
        }

    }
}
