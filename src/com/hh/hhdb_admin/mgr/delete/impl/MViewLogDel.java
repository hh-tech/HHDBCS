package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

import java.util.Map;

public class MViewLogDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String nodeName = nodeInfo.getName();
        String schemaName = nodeInfo.getSchemaName();
        String sql;
        if (dbType == DBTypeEnum.oracle) {
            sql = String.format("select master from all_mview_logs where log_owner = '%s' and log_table = '%s'", schemaName, nodeName);
            Map<String, String> map = SqlQueryUtil.selectOneStrMap(conn, sql);
            if (map.containsKey("master")) {
                SqlExeUtil.execute(conn, String.format("drop materialized view log on %s", map.get("master")));
            }
        }
    }

}
