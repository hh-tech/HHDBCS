package com.hh.hhdb_admin.mgr.delete.impl;

import java.sql.Connection;

import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

/**
 * @Author: Jiang
 * @Date: 2021/9/15 15:46
 */
public class PackageDel extends AbsDel {

	@Override
	public void del(NodeInfo nodeInfo) throws Exception {
		String schemaName = nodeInfo.getSchemaName();
		String name = nodeInfo.getName();
		String sql = String.format("select object_name from all_objects where object_type = 'PACKAGE' "
				+ "and owner = '%s' and object_name = '%s'", schemaName, name);
		Connection conn = ConnUtil.getConn(jdbcBean);
		try {
			if (SqlQueryUtil.existData(conn, sql)) {
				execute(String.format("DROP PACKAGE \"%s\".\"%s\"", schemaName, name));
			} else {
				execute(String.format("DROP PACKAGE BODY \"%s\"", name));
			}
		} finally {
			conn.close();
		}
	}
}
