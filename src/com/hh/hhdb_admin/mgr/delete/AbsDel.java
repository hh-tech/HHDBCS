package com.hh.hhdb_admin.mgr.delete;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author: Jiang
 * @Date: 2021/9/8 14:38
 */
public abstract class AbsDel {

    public Connection conn;
    public DBTypeEnum dbType;
    public JdbcBean jdbcBean;

    public void init(LoginBean loginBean){
        try {
            if (loginBean == null) {
                return;
            }
            this.jdbcBean = loginBean.getJdbc();
            conn = ConnUtil.getConn(jdbcBean);
            dbType = DriverUtil.getDbType(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public abstract void del(NodeInfo nodeInfo) throws Exception;

}
