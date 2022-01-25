package com.hh.hhdb_admin.mgr.delete;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @Author: Jiang
 * @Date: 2021/9/8 14:38
 */
public abstract class AbsDel {

    Statement stat;
    public DBTypeEnum dbType;
    protected JdbcBean jdbcBean;

    public void init(LoginBean loginBean,Statement stat){
        this .stat = stat;
        this.jdbcBean = loginBean.getJdbc();
        dbType = DriverUtil.getDbType(jdbcBean);
    }
    
    public abstract void del(NodeInfo nodeInfo) throws Exception;

    public void execute(String formatSql) throws SQLException{
		stat.executeUpdate(formatSql);		
    }
    protected void batchExecute(List<String> sqlList) throws SQLException {
    	for (String sql : sqlList) {
			stat.addBatch(sql);
		}
    	stat.executeBatch();
    }
}
