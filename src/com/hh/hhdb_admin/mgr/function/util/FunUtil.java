package com.hh.hhdb_admin.mgr.function.util;


import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.create_dbobj.function.debug.AbsDebug;
import com.hh.frame.create_dbobj.function.debug.HHdbDebug;
import com.hh.frame.create_dbobj.function.debug.OraDebug;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.mgr.function2.FunctionBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.function.ui.deBug_from.HHDebugForm;
import com.hh.hhdb_admin.mgr.function.ui.deBug_from.OrDebugForm;
import com.hh.hhdb_admin.mgr.function.ui.from.*;
import com.hh.hhdb_admin.mgr.function.ui.run.*;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class FunUtil {

    public static FunBaseForm getFunBaseForm(AbsFunMr funMr, JdbcBean jdbcBean,Connection conn, boolean isEdit) throws Exception {
//        DBTypeEnum typeEnum = DriverUtil.getDbType(conn);
    	String user=jdbcBean.getUser();
    	DBTypeEnum typeEnum = DriverUtil.getDbType(jdbcBean);
    	//TODO感觉这边的用户是用户自己输入的，需要有大小写的问题，待测试
    	user=SqlStrUtil.rmQuote4Sel(user, typeEnum, jdbcBean.getCurrentVersion());

        if (typeEnum==DBTypeEnum.hhdb || typeEnum==DBTypeEnum.pgsql || typeEnum==DBTypeEnum.tdsql4pg){
            return  new HHFunForm(funMr,jdbcBean,conn,user,isEdit);
        }else if (typeEnum==DBTypeEnum.gaussdb) {
            return  new GaussdbFunForm(funMr,jdbcBean,conn,user,isEdit);
        }else if (typeEnum==DBTypeEnum.kingbase) {
            return new KingbaseFunForm(funMr,jdbcBean,conn,user,isEdit);
        }else if (typeEnum==DBTypeEnum.oracle || (typeEnum==DBTypeEnum.oceanbase && jdbcBean.getCurrentVersion()==null)) {
            return  new OrFunForm(funMr,jdbcBean,conn,isEdit);
        }else if (typeEnum==DBTypeEnum.mysql||typeEnum==DBTypeEnum.mariadb || typeEnum==DBTypeEnum.tdsql4mysql || (typeEnum==DBTypeEnum.oceanbase && jdbcBean.getCurrentVersion()==DBTypeEnum.Version.ob4ora)) {
            return  new MysqlFunForm(funMr,jdbcBean,conn,isEdit);
        }else if (typeEnum==DBTypeEnum.sqlserver) {
            return  new SqlsrverFunForm(funMr,jdbcBean,conn,isEdit);
        }else if (typeEnum==DBTypeEnum.db2) {
            return  new Db2FunForm(funMr,jdbcBean,conn,isEdit);
        }else if (typeEnum==DBTypeEnum.dm) {
            return  new DmFunForm(funMr,jdbcBean,conn,isEdit);
        }else if (typeEnum==DBTypeEnum.gbase) {
            return  new GBaseFunForm(funMr,jdbcBean,conn,isEdit);
        }
        return null;
    }
    
    
    public static void getDebugBaseForm(FunctionBean csGuiBean, JdbcBean jdbcBean) {
        try {    
            TreeMrNode treeNode = new TreeMrNode(csGuiBean.getName(),csGuiBean.getId(),csGuiBean.getNodeType() , "");
            treeNode.setSchemaName(csGuiBean.getSchema());
            DBTypeEnum typeEnum = DriverUtil.getDbType(jdbcBean);
            AbsFunMr funMr = AbsFunMr.genFunMr(typeEnum,jdbcBean.getCurrentVersion(),treeNode);
            if (typeEnum==DBTypeEnum.hhdb || typeEnum==DBTypeEnum.pgsql){
                new HHDebugForm(funMr,jdbcBean);
            }else if (typeEnum==DBTypeEnum.oracle) {
                new OrDebugForm(funMr,jdbcBean);
            }else if (typeEnum==DBTypeEnum.mysql) {

            }else if (typeEnum==DBTypeEnum.sqlserver) {
        
            }else if (typeEnum==DBTypeEnum.db2) {
        
            }
        }catch (Exception e){
            PopDlgUtil.error(CsVarUtil.parentFrame,e);
        }
    }
    
    public static RunBaseForm getRunFun(AbsFunMr funMr,Connection conn, JdbcBean jdbcBean, List<Map<String, String>> list) {
        DBTypeEnum typeEnum = DriverUtil.getDbType(jdbcBean);
        RunBaseForm runBaseForm = null;
        if (typeEnum==DBTypeEnum.hhdb || typeEnum==DBTypeEnum.pgsql || typeEnum==DBTypeEnum.tdsql4pg || typeEnum==DBTypeEnum.gaussdb){
            runBaseForm = new HHRunForm(funMr,conn,jdbcBean,list);
        }else if (typeEnum==DBTypeEnum.oracle) {
            runBaseForm = new OrRunForm(funMr,conn,jdbcBean,list);
        }else if (typeEnum==DBTypeEnum.mysql||typeEnum==DBTypeEnum.mariadb||typeEnum==DBTypeEnum.tdsql4mysql || typeEnum==DBTypeEnum.oceanbase) {
            runBaseForm = new MysqlRunForm(funMr,conn,jdbcBean,list);
        }else if (typeEnum==DBTypeEnum.sqlserver) {
            runBaseForm = new SqlsrverRunForm(funMr,conn,jdbcBean,list);
        }
        return runBaseForm;
    }
    
    public static AbsDebug getDebug(JdbcBean jdbcBean, String procId) throws Exception{
        DBTypeEnum dbType = DriverUtil.getDbType(jdbcBean);
        if (dbType == DBTypeEnum.hhdb || dbType == DBTypeEnum.pgsql) {
            return new HHdbDebug(jdbcBean, procId);
        } else if (dbType == DBTypeEnum.oracle) {
            return new OraDebug(jdbcBean);
        } else {
            throw new Exception("此类数据库暂时不支持调试功能");
        }
    }
}
