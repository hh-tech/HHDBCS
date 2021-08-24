package com.hh.hhdb_admin.mgr.function.util;


import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.hhdb_admin.mgr.function.ui.deBug_from.HHDebugForm;
import com.hh.hhdb_admin.mgr.function.ui.deBug_from.OrDebugForm;
import com.hh.hhdb_admin.mgr.function.ui.from.*;
import com.hh.hhdb_admin.mgr.function.ui.run.HHRunForm;
import com.hh.hhdb_admin.mgr.function.ui.run.OrRunForm;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class FunUtil {

    public static FunBaseForm getFunBaseForm(AbsFunMr funMr, Connection conn, JdbcBean jdbcBean, boolean isEdit) throws Exception {
        DBTypeEnum typeEnum = DriverUtil.getDbType(conn);
        if (typeEnum==DBTypeEnum.hhdb || typeEnum==DBTypeEnum.pgsql){
            return  new HHFunForm(funMr,conn,jdbcBean,isEdit);
        }else if (typeEnum==DBTypeEnum.oracle) {
            return  new OrFunForm(funMr,conn,jdbcBean,isEdit);
        }else if (typeEnum==DBTypeEnum.mysql) {
            return  new MysqlFunForm(funMr,conn,jdbcBean,isEdit);
        }else if (typeEnum==DBTypeEnum.sqlserver) {
            return  new SqlsrverFunForm(funMr,conn,jdbcBean,isEdit);
        }else if (typeEnum==DBTypeEnum.db2) {
            return  new Db2FunForm(funMr,conn,jdbcBean,isEdit);
        }else if (typeEnum==DBTypeEnum.dm) {
            return  new DmFunForm(funMr,conn,jdbcBean,isEdit);
        }
        return null;
    }
    
    
    public static void getDebugBaseForm(TreeMrNode treeNode, JdbcBean jdbcBean, String sql, List<Map<String,String>> pars) {
        DBTypeEnum typeEnum = DriverUtil.getDbType(jdbcBean);
        if (typeEnum==DBTypeEnum.hhdb || typeEnum==DBTypeEnum.pgsql){
            new HHDebugForm(treeNode,jdbcBean,sql,pars);
        }else if (typeEnum==DBTypeEnum.oracle) {
            new OrDebugForm(treeNode,jdbcBean,sql,pars);
        }else if (typeEnum==DBTypeEnum.mysql) {
        
        }else if (typeEnum==DBTypeEnum.sqlserver) {
        
        }else if (typeEnum==DBTypeEnum.db2) {
        
        }
    }
    
    public static void getRunFun(AbsFunMr funMr, JdbcBean jdbcBean, String packName) throws Exception {
        DBTypeEnum typeEnum = DriverUtil.getDbType(jdbcBean);
        if (typeEnum==DBTypeEnum.hhdb || typeEnum==DBTypeEnum.pgsql){
            new HHRunForm(funMr,jdbcBean);
        }else if (typeEnum==DBTypeEnum.oracle) {
            new OrRunForm(funMr,jdbcBean,packName);
        }else if (typeEnum==DBTypeEnum.mysql) {
        
        }else if (typeEnum==DBTypeEnum.sqlserver) {
        
        }else if (typeEnum==DBTypeEnum.db2) {
        
        }
    }
}
