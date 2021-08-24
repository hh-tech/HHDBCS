package com.hh.hhdb_admin.mgr.function.util;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunDebugComp;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.sql.Connection;
import java.util.*;

public class DebugUtil {

    /**
     * 获取函数sql
     *
     * @param conn
     * @return
     * @throws Exception
     */
    public static String getCreateSql(JdbcBean jdbcBean,String name,String schema) throws Exception {
        Connection conn = ConnUtil.getConn(jdbcBean);
        try {
            String result = "";
            String sql = "SELECT  A.TEXT FROM ALL_SOURCE  A WHERE  A.TYPE IN ( 'PROCEDURE', 'FUNCTION' ) AND A.name='"
                    + name + "' and A.owner ='" + schema + "' ORDER BY A.NAME, A.LINE";
            List<Map<String, Object>> mapList = SqlQueryUtil.select(true, conn, sql);
            if (mapList != null && mapList.size() > 0) {
                result = "CREATE OR REPLACE ";
                for (Map<String, Object> map : mapList) {
                    result = result + map.get("TEXT").toString();
                }
            }
            return result;
        }finally {
            ConnUtil.close(conn);
        }
    }

    /**
     * 验证函数是否异常
     * @param conn
     * @param name
     * @param type
     */
    public static boolean funVerify(JdbcBean jdbcBean,String name,String type){
        Connection conn = null;
        boolean bool = true;
        try {
            conn = ConnUtil.getConn(jdbcBean);
            String str = "select * from user_errors where name = '"+name+"' and type = '"+type+"'";
            bool = SqlQueryUtil.select(conn, str).size() > 0;
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame .getWindow(), e.getMessage());
        }finally {
            ConnUtil.close(conn);
        }
        return bool;
    }

    /**
     *解析栈堆信息
     */
    public static List<Map<String,String>> stackAnalysis(String val){
        //1: name=,type=CURSOR,line=6  2: name=FN_MYMONTH,type=FUNCTION,line=8  3: name=PKG_DBGD,type=PACKAGE_BODY,line=2
        List<Map<String,String>> liMap = new LinkedList<>();
        List<String> vl = Arrays.asList(val.split(":"));
        for (int i = 1; i < vl.size(); i++) {
            Map<String,String> map = new LinkedHashMap<>();
            String ss = vl.get(i).split(" {2}")[0].trim();
            String[] list = ss.split(",");
            String[] name = list[0].split("=");

            map.put("name",name.length > 1 ? name[1].trim() : "");
            map.put("type",list[1].split("=")[1].trim());
            map.put("line",list[2].split("=")[1].trim());
            liMap.add(map);
        }
        return liMap;
    }

    /**
     * 获取调试sql
     *
     * @param funMr
     * @param conn
     * @return
     * @throws Exception
     */
    public static String getSql(HTable hTable, AbsFunMr funMr, Connection conn) {
        String str = "";
        try {
            DBTypeEnum dbType = DriverUtil.getDbType(conn);
            JTable parmtable = hTable.getComp();
            if (parmtable.isEditing()) parmtable.getCellEditor().stopCellEditing();
            int rows = parmtable.getRowCount();

            if (dbType.equals(DBTypeEnum.hhdb) || dbType.equals(DBTypeEnum.pgsql)) {
                StringBuffer sql = new StringBuffer("\"" + funMr.treeNode.getSchemaName() + "\".\"" + funMr.treeNode.getName() + "\"(");
                for (int i = 0; i < rows; i++) {
                    sql.append(parmtable.getValueAt(i, 2) + "::" + parmtable.getValueAt(i, 1));
                    if (i != (rows - 1)) sql.append(",");
                }
                sql.append(")");
                str = sql.toString();
            } else if (dbType.equals(DBTypeEnum.oracle)) {
                Map<String, String> maps = new HashMap<String, String>();
                for (int i = 0; i < rows; i++) {
                    maps.put(parmtable.getValueAt(i, 0) + "", parmtable.getValueAt(i, 2) + "");
                }

                List<Map<String, String>> valList = infoParam(conn,null,funMr.treeNode.getName(),funMr.treeNode.getSchemaName());
                str = getORSql(valList,maps,funMr.treeNode.getName(),funMr.treeNode.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame .getWindow(), FunctionMgr.getLang("error") + ":" + e.getMessage());
        }
        return str;
    }

    /**
     * 获取函数存储过程等对象参数信息
     * @param con
     * @param packName  包名
     * @param objName   对象名
     * @param schema    模式
     * @return
     * @throws Exception
     */
    public static List<Map<String, String>> infoParam(Connection con,String packName,String objName,String schema) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ARGUMENT_NAME , DATA_TYPE, IN_OUT, DATA_LENGTH\n");
        sql.append("FROM ALL_ARGUMENTS\n");
        sql.append("WHERE OWNER = '"+ schema +"'\n");
        if (StringUtils.isNotBlank(packName)) {
            sql.append("AND PACKAGE_NAME = '"+ packName +"'\n");
        } else {
            sql.append("AND PACKAGE_NAME IS NULL\n");
        }
        sql.append("AND OBJECT_NAME = '"+ objName +"'\n");
        sql.append("AND DATA_TYPE IS NOT NULL\n");
        sql.append("AND DATA_LEVEL = 0\n");
        sql.append("ORDER BY SEQUENCE, IN_OUT");

        return SqlQueryUtil.selectStrMapList(con, sql.toString());
    }

    /**
     * 获取函数输入参数详细信息
     *
     * @return
     */
    public static List<Map<String, String>> getInPara(AbsFunMr funMr, Connection conn) throws Exception {
        DBTypeEnum dbType = DriverUtil.getDbType(conn);

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (dbType.equals(DBTypeEnum.hhdb) || dbType.equals(DBTypeEnum.pgsql)) {
            if (verifyFun(funMr.getFunParameter(conn).get(0).get("prosrc").toString()).length() < 1) {
                throw new Exception(FunctionMgr.getLang("debugHint") + "!");
            }
        } else if (dbType.equals(DBTypeEnum.oracle)) {

        }
        Map<String, String> parms = funMr.getFunInPar(conn);
        if (parms.isEmpty()) {
            return new ArrayList<Map<String, String>>();
        } else {
            for (String str : parms.keySet()) {
                Map<String, String> dparma = new HashMap<String, String>();
                dparma.put("parameter", str);
                dparma.put("dbType", parms.get(str));
                dparma.put("value", "");
                list.add(dparma);
            }
        }
        return list;
    }

    /**
     * 调试环境验证
     */
    public static void examine(String schemaName,JdbcBean jdbcBean) throws Exception{
        Connection conn = null;
        try {
            conn = ConnUtil.getConn(jdbcBean);
            if (DriverUtil.getDbType(jdbcBean) == DBTypeEnum.oracle){
                //安装调试所需包
                String str = "SELECT OWNER, OBJECT_NAME, OBJECT_TYPE FROM ALL_OBJECTS " +
                        "WHERE OBJECT_TYPE = 'PACKAGE'  AND OWNER = '"+ schemaName +"'" +
                        " AND OBJECT_NAME = 'ADP_DEBUG'";
                List<Map<String, String>> list = SqlQueryUtil.selectStrMapList(conn, str);
                if(null == list || list.isEmpty()) {
                    String sql= ClassLoadUtil.loadTextRes(FunDebugComp.class, "adp_debug.sql");
                    SqlExeUtil.batchExecute(conn, Arrays.asList(sql.split("//")));
                }
                //添加调试权限
                SqlExeUtil.executeUpdate(conn,"grant DEBUG CONNECT SESSION to " + jdbcBean.getUser());
                SqlExeUtil.executeUpdate(conn,"grant DEBUG ANY PROCEDURE to " + jdbcBean.getUser());
            }
        }finally {
            ConnUtil.close(conn);
        }
    }

    /**
     * 获取函数输出参数
     *
     * @return
     */
    public static List<String> getOutPara(TreeMrNode treeNode, JdbcBean jdbcBean) throws Exception {
        AbsFunMr funMr = AbsFunMr.genFunMr(DriverUtil.getDbType(jdbcBean),treeNode);
        Connection conn = ConnUtil.getConn(jdbcBean);
        try {
            List<String> list = new ArrayList<String>();
            Map<String, JsonObject> jmap = funMr.getFunAllPar(conn);
            if (null != jmap) {
                for (String key : jmap.keySet()) {
                    JsonObject jsb = jmap.get(key);
                    if (jsb != null){
                        if (jsb.getString("out_in").equals("OUT") || jsb.getString("out_in").equals("IN/OUT")) list.add(key);
                    }
                }
            }
            return list;
        }finally {
            ConnUtil.close(conn);
        }
    }

    /**
     * 验证函数是否有内容
     *
     * @param str
     * @return
     */
    private static String verifyFun(String str) {
        String result = "";
        str = str.trim().toUpperCase();
        if (str.contains("BEGIN")) {
            String[] s2 = str.split("BEGIN")[1].split("END");
            if (s2 != null) {
                result = s2[0];
            }
        }
        return result.trim();
    }

    /**
     * 获取or调试sql
     * @param valList   参数集合
     * @param valMap    参数值集合
     * @param objName   对象名
     * @param type      类型
     * @return
     * @throws Exception
     */
    private static String getORSql(List<Map<String, String>> valList,Map<String, String> valMap,String objName,TreeMrType type) throws Exception {
        String runsql = "";

        StringBuffer variate = new StringBuffer();
        StringBuffer param = new StringBuffer();
        for (Map<String, String> map : valList) {
            if (map.get("argument_name") == null) {
                //添加函数返回值
                if ( type == TreeMrType.FUNCTION ) variate.append("\tRESULT " + ( map.get("data_type").equals("VARCHAR2") ? "VARCHAR2 ( 4000 )" : map.get("data_type") )).append(";\n");
                continue;
            }

            //变量
            String str = map.get("data_type").equals("VARCHAR2") ? "VARCHAR2 ( 4000 )" : map.get("data_type");
            str = "\""+ map.get("argument_name") + "\" " + str;
            if ("IN".equals(map.get("in_out")) || "IN/OUT".equals(map.get("in_out"))) {
                if ( null != valMap && StringUtils.isNotBlank(valMap.get(map.get("argument_name"))) ) {
                    str += " := '" + valMap.get(map.get("argument_name"))+"'";
                }
            }
            variate.append("\t"+str+";\n");
            //参数
            param.append(param.length()>1 ? "," : "").append("\""+map.get("argument_name")+"\" => \""+map.get("argument_name")+"\"");
        }

        StringBuffer sql = new StringBuffer();
        if (variate.length() >0) {
            sql.append("DECLARE\n").append(variate);
        }
        sql.append("BEGIN\n");

        //调用函数
        sql.append( type == TreeMrType.FUNCTION ? "\tRESULT :=" : "");
        sql.append( type == TreeMrType.FUNCTION ? " " : "\t");
        sql.append("\""+ objName +"\"(");
        sql.append(param);
        sql.append(");\n");

        runsql = sql.append("END;\n").toString();
        return runsql;
    }
}
