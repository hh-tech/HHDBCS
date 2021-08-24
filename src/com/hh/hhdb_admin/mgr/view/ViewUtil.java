package com.hh.hhdb_admin.mgr.view;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;

import java.sql.Connection;

public class ViewUtil {

    /**
     * 删除view
     *
     * @param conn     连接
     * @param viewName 视图名称
     * @throws Exception e
     */
    public static void delView(Connection conn, String schemaName, String viewName) throws Exception {
        DBTypeEnum typeEnum = DriverUtil.getDbType(conn);
        String name = DbCmdStrUtil.toDbCmdStr(viewName, typeEnum);
        String schema = DbCmdStrUtil.toDbCmdStr(schemaName, typeEnum);
        SqlExeUtil.executeUpdate(conn, String.format("DROP VIEW  %s.%s", schema, name));
    }


}
