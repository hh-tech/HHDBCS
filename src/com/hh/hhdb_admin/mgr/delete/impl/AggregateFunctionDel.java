package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @Author: Jiang
 * @Date: 2021/9/15 14:04
 */
public class AggregateFunctionDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String id = nodeInfo.getId();
        String schemaName = nodeInfo.getSchemaName();
        String name = nodeInfo.getName();
        String sql = "DROP AGGREGATE \"%s\".\"%s\"(%s)";
        String getParamTypeSql = "SELECT COALESCE(hh_catalog.hh_get_function_identity_arguments(oid)) as arguments " +
                "FROM hh_proc " +
                "WHERE oid = %s";
        Map<String, String> paramTypeData = SqlQueryUtil.selectOneStrMap(conn, String.format(getParamTypeSql, id));
        if (StringUtils.isBlank(paramTypeData.get("arguments"))) {
            return;
        }
        SqlExeUtil.executeUpdate(conn, String.format(sql, schemaName, name, paramTypeData.get("arguments")));
    }
}
