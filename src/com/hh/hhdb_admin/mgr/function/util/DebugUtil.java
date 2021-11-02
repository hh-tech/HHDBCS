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
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;

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
                    String sql= ClassLoadUtil.loadTextRes(FunctionMgr.class, "adp_debug.sql");
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
     * 验证函数是否编译通过
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
     * 获取函数参数信息
     * @return
     */
    public static List<Map<String, String>> getParam(AbsFunMr funMr, JdbcBean jdbcBean) {
        Connection con = null;
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            con = ConnUtil.getConn(jdbcBean);
            DBTypeEnum dbType = DriverUtil.getDbType(con);
            if (dbType.equals(DBTypeEnum.hhdb) || dbType.equals(DBTypeEnum.pgsql)) {
                if (verifyFun(funMr.getFunParameter(con).get(0).get("prosrc").toString()).length() < 1) {
                    throw new Exception(FunctionMgr.getLang("debugHint") + "!");
                }
            }
            Map<String, String> parms = funMr.getFunInPar(con);
            if (parms.isEmpty()) {
                return new ArrayList<Map<String, String>>();
            } else {
                for (String str : parms.keySet()) {
                    Map<String, String> dparma = new HashMap<String, String>();
                    dparma.put("name", str);
                    dparma.put("type", parms.get(str));
                    dparma.put("value", "");
                    list.add(dparma);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame .getWindow(),e.getMessage());
        }finally {
            ConnUtil.close(con);
        }
        return list;
    }

    /**
     * 获取函数输出参数
     *
     * @return
     */
    public static Map<String,String> getOutPara(TreeMrNode treeNode, JdbcBean jdbcBean) throws Exception {
        Map<String,String> map = new LinkedHashMap<>();
        AbsFunMr funMr = AbsFunMr.genFunMr(DriverUtil.getDbType(jdbcBean),treeNode);
        Connection conn = ConnUtil.getConn(jdbcBean);
        try {
            Map<String, JsonObject> jmap = funMr.getFunAllPar(conn);
            if (null != jmap) {
                for (String key : jmap.keySet()) {
                    JsonObject jsb = jmap.get(key);
                    if (jsb != null){
                        if (jsb.getString("out_in").equals("OUT") || jsb.getString("out_in").equals("IN/OUT")) map.put(key,"");
                    }
                }
            }
            return map;
        }finally {
            ConnUtil.close(conn);
        }
    }

    /**
     * 验证函数是否有内容
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
}
