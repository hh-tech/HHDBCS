package com.hh.hhdb_admin.mgr.function.util;


import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import com.hh.hhdb_admin.mgr.function.ui.deBug_from.HHDebugForm;
import com.hh.hhdb_admin.mgr.function.ui.deBug_from.OrDebugForm;
import com.hh.hhdb_admin.mgr.function.ui.from.*;
import com.hh.hhdb_admin.mgr.function.ui.run.HHRunForm;
import com.hh.hhdb_admin.mgr.function.ui.run.MysqlRunForm;
import com.hh.hhdb_admin.mgr.function.ui.run.OrRunForm;
import com.hh.hhdb_admin.mgr.function.ui.run.SqlsrverRunForm;

import java.sql.Connection;

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
    
    
    public static void getDebugBaseForm(JsonObject msg,JdbcBean jdbcBean) {
        Connection con = null;
        try {
            con = ConnUtil.getConn(jdbcBean);
    
            TreeMrNode treeNode = new TreeMrNode(GuiJsonUtil.toPropValue(msg, FunctionMgr.PARAM_FUNC_NAME),GuiJsonUtil.toPropValue(msg, FunctionMgr.PARAM_FUNC_ID),
                    TreeMrType.valueOf(GuiJsonUtil.toPropValue(msg,FunctionMgr.TYPE)) , "");
            treeNode.setSchemaName(GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_SCHEMA));
            AbsFunMr funMr = AbsFunMr.genFunMr(DriverUtil.getDbType(con),treeNode);
            DBTypeEnum typeEnum = DriverUtil.getDbType(jdbcBean);
            if (typeEnum==DBTypeEnum.hhdb || typeEnum==DBTypeEnum.pgsql){
                new HHDebugForm(funMr,jdbcBean);
            }else if (typeEnum==DBTypeEnum.oracle) {
                new OrDebugForm(funMr,jdbcBean);
            }else if (typeEnum==DBTypeEnum.mysql) {
        
            }else if (typeEnum==DBTypeEnum.sqlserver) {
        
            }else if (typeEnum==DBTypeEnum.db2) {
        
            }
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),e.getMessage());
        }finally {
            ConnUtil.close(con);
        }
    }
    
    public static void getRunFun(AbsFunMr funMr, JdbcBean jdbcBean, String packName) throws Exception {
        DBTypeEnum typeEnum = DriverUtil.getDbType(jdbcBean);
        if (typeEnum==DBTypeEnum.hhdb || typeEnum==DBTypeEnum.pgsql){
            new HHRunForm(funMr,jdbcBean);
        }else if (typeEnum==DBTypeEnum.oracle) {
            new OrRunForm(funMr,jdbcBean,packName);
        }else if (typeEnum==DBTypeEnum.mysql) {
            new MysqlRunForm(funMr,jdbcBean);
        }else if (typeEnum==DBTypeEnum.sqlserver) {
            new SqlsrverRunForm(funMr,jdbcBean);
        }else if (typeEnum==DBTypeEnum.db2) {
        
        }
    }
}
