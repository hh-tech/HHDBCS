package com.hh.hhdb_admin.common.util.textEditor.tooltip;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.dbobj2.TabObjEnum;
import com.hh.frame.dbobj2.TableTool;
import com.hh.frame.dbobj2.db2.Db2Schema;
import com.hh.frame.dbobj2.mysql.MysqlSchema;
import com.hh.frame.dbobj2.ora.OraSchema;
import com.hh.frame.dbobj2.ora.pack.OraPackObj;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditUtil;
import com.hh.hhdb_admin.common.util.textEditor.base.Keyword;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TipUtil {
    
    /**
     * 判断输入字符开始位置
     * @param sql
     * @return
     */
    public static String isCharacter(String sql) {
        String flag = "\n\t\r ";
        int w = sql.length()-1;
       a : while (w >= 0) {
            for (int i = 0; i < flag.length(); i++) {
                if (flag.charAt(i) == sql.charAt(w)) {
                    break a;
                }
            }
            w --;
        }
        return sql.substring(w + 1);
    }
    
    /**
     * 解析用户输入字符，主要解析是表别名时获取表的名称
     * @param val  编辑器内容
     * @param pos  光标位置
     * @param name 用户输入字符
     * @return
     */
    public static String analysisStr(String val, int pos,String name){
        List<String> list = new LinkedList<>();
        String[] split = getSql(val,pos).trim().split(" ");
        for (String string : split) {
            if (string.endsWith(")")) string = string.replace(")", "");
            if (string.startsWith("(")) string = string.replace("(", "");
            if (string.startsWith(")")) string = string.replace(")", "");
            
            String[] val2 =  string.split("\\n");
            if (val2.length>1) {
                for (String s : val2) {
                    if (!StringUtils.isNotBlank(s)) continue;
                    list.add(s);
                }
            } else {
                if (!StringUtils.isNotBlank(string)) continue;
                list.add(string);
            }
        }
        
        name = name.substring(0,name.length()-1).trim();
        for (int i = list.size()-1; i > -1; i--) {
            String string = list.get(i);
            if (string.equals(name)) {
                if (i-1 >-1) {
                    name = list.get(i-1);
                    if (name.toUpperCase().equals("FROM"))  name = string;
                } else {
                    name = string;
                }
                break;
            }
        }
        return name;
    }
    
    /**
     * 获取对象子元素提示名称集合,如模式下所有对象，表的字段,包里函数等等
     * @param conn
     * @param val   光标位置整句sql
     * @param name  用户输入字符
     * @param schemaSubMap  模式下对象集合
     * @return
     */
    public static List<Keyword> getSubList(Connection conn,String schema,String val, int pos, String name, Map<String,List<Keyword>> schemaSubMap) {
        List<Keyword> list = new LinkedList<>();
        if (name.equals(".")) return new LinkedList<>();
        try {
            DBTypeEnum dbtype = DriverUtil.getDbType(conn);
            String str = analysisStr(val,pos,name);
    
            //查询表,视图列
            TableTool tab = new TableTool(conn, schema, str);
            tab.getSubObjNames(TabObjEnum.COLUMN).forEach( a -> list.add(new Keyword(a, "column", QueryEditUtil.columnIcon)));
            if (list.size() > 0) return list;
            //查询包函数
            if (dbtype == DBTypeEnum.oracle) {
                OraPackObj pack = new OraPackObj(conn, schema, str);
                pack.getFunNames().forEach( a -> list.add(new Keyword(a, "pack", QueryEditUtil.functionIcon)));
                pack.getProcNames().forEach( a -> list.add(new Keyword(a, "pack", QueryEditUtil.functionIcon)));
                if (list.size() > 0) return list;
            }
            //查询为函数时子对象
            List<Keyword> st = schemaSub(conn,dbtype,str,false);    //添加部分对象用于显示
            if (!st.isEmpty()) {
                list.addAll(st);
                new Thread(new Runnable() {  //获取该模式下所有对象保存
                    @Override
                    public void run() {
                        schemaSubMap.put(str,schemaSub(conn,dbtype,str,true));
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * 获取模式下对象集合
     * @param jdbc
     * @param val   查询对象
     * @param bool  是否查询所有子对象
     * @return
     */
    private static List<Keyword> schemaSub(Connection conn,DBTypeEnum dbtype,String val,boolean bool){
        List<Keyword> list = new LinkedList<>();
        try {
            if (dbtype == DBTypeEnum.oracle || dbtype == DBTypeEnum.dm) {
                OraSchema os = new OraSchema(conn,val.toUpperCase());
                os.getAllObj(bool ? "" : Tooltip.tipMax+"").forEach( a -> list.add(new Keyword(a.get("name"), a.get("type"), getIcon(a.get("type")))));
            } else if (dbtype == DBTypeEnum.hhdb || dbtype == DBTypeEnum.pgsql) {
                HHdbPgsqlPrefixEnum prefix = dbtype == DBTypeEnum.pgsql ? HHdbPgsqlPrefixEnum.pg : HHdbPgsqlPrefixEnum.hh;
                String sql ="select cls.relname as name, \n" +
                        "    case cls.relkind\n" +
                        "        when 'r' then 'TABLE'\n" +
                        "        when 'p' then 'TABLE'\n" +
                        "        when 'm' then 'MATERIALIZED_VIEW'\n" +
                        "        when 'S' then 'SEQUENCE'\n" +
                        "        when 'v' then 'VIEW'\n" +
                        "        when 'c' then 'TYPE'\n" +
                        "        else cls.relkind::text\n" +
                        "    end as type\n" +
                        "from " + prefix + "_class cls join " + prefix + "_namespace nsp on nsp.oid = cls.relnamespace\n" +
                        "where cls.relkind in ('r','p','m','S','v','c')\n" +
                        "  and nsp.nspname = '%s'\n" +
                        "order by cls.relname " +
                        " %s";
                List<Map<String, String>> ls = SqlQueryUtil.selectStrMapList(conn, String.format(sql,val.toLowerCase(),bool ? "" : "LIMIT "+Tooltip.tipMax));
                ls.forEach( a -> list.add(new Keyword(a.get("name"), a.get("type"), getIcon(a.get("type")))));
                //查询所有函数
                if (bool || (!bool && list.size() < Tooltip.tipMax) ) {
                    sql = "SELECT p.proname as name,'function' as type \n" +
                            "FROM " + prefix + "_proc p JOIN " + prefix + "_namespace s ON (p.pronamespace = s.oid)\n" +
                            "WHERE s.nspname  = '%s' %s ";
                    ls = SqlQueryUtil.selectStrMapList(conn, String.format(sql,val.toLowerCase(),bool ? "" : "LIMIT 100"));
                    ls.forEach( a -> list.add(new Keyword(a.get("name"), a.get("type"), getIcon(a.get("type")))));
                }
            } else if (dbtype == DBTypeEnum.mysql) {
                String sql = "select table_name as name,\"table\" as type\n" +
                        "  from information_schema.TABLES\n" +
                        "  where TABLE_SCHEMA = \"%s\" AND TABLE_TYPE='BASE TABLE' %s;";
                List<Map<String, String>> ls = SqlQueryUtil.selectStrMapList(conn, String.format(sql,val,bool ? "" : "limit "+Tooltip.tipMax));
                ls.forEach( a -> list.add(new Keyword(a.get("name"), a.get("type"), getIcon(a.get("type")))));
                
                MysqlSchema mySchema = new MysqlSchema(conn,val);
                if (bool || (!bool && list.size() < Tooltip.tipMax)) mySchema.getAllView().forEach( a -> list.add(new Keyword(a.get("view_name"), "view", getIcon("view"))));
                if (bool || (!bool && list.size() < Tooltip.tipMax)) mySchema.getAllFunction().forEach( a -> list.add(new Keyword(a.get("name"), "function", getIcon("function"))));
                if (bool || (!bool && list.size() < Tooltip.tipMax)) mySchema.getAllProcedure().forEach( a -> list.add(new Keyword(a.get("name"), "function", getIcon("function"))));
            } else if (dbtype == DBTypeEnum.sqlserver) {
                String sql = "select %s o.NAME ,\n" +
                        "CASE \n" +
                        "     when o.type = 'V' then 'VIEW'\n" +
                        "     when o.type = 'U' then 'TABLE'\n" +
                        "     when o.type IN ('P', 'IF','FN','TF','AF') then 'FUNCTION'\n" +
                        "     when o.type = 'SN' then 'VIEW' \n" +
                        "     ELSE o.type END AS TYPE\n" +
                        "from sys.objects o LEFT JOIN sys.schemas s ON o.schema_id = s.schema_id\n" +
                        " WHERE o.type in ('V','U','P','IF','FN','TF','AF','SN') and s.name = '%s'";
                List<Map<String, String>> ls = SqlQueryUtil.selectStrMapList(conn, String.format(sql,bool ? "" : "TOP "+Tooltip.tipMax,val));
                ls.forEach( a -> list.add(new Keyword(a.get("name"), a.get("type"), getIcon(a.get("type")))));
            } else if (dbtype == DBTypeEnum.db2) {
                String sql = "select %s tabname as NAME,'table' as TYPE\n" +
                        "FROM SYSCAT.TABLES WHERE TYPE='T' AND tabschema = '%s' order by TBSPACEID, TABLEID  %s";
                List<Map<String, String>> ls = SqlQueryUtil.selectStrMapList(conn,
                        String.format(sql,bool ? "":"ROW_NUMBER() OVER(order by  TABLEID) AS ROWNUM,",val.toUpperCase(),bool ? "" : "fetch first "+Tooltip.tipMax+" rows only "));
                ls.forEach( a -> list.add(new Keyword(a.get("name"), a.get("type"), getIcon(a.get("type")))));
    
                Db2Schema db2Schema = new Db2Schema(conn,val.toUpperCase());
                if (bool || (!bool && list.size() < Tooltip.tipMax)) db2Schema.getAllView().forEach( a -> list.add(new Keyword(a.get("view_name"), "view", getIcon("view"))));
                if (bool || (!bool && list.size() < Tooltip.tipMax)) db2Schema.getAllFunction().forEach( a -> list.add(new Keyword(a.get("function_name"), "function", getIcon("function"))));
                if (bool || (!bool && list.size() < Tooltip.tipMax)) db2Schema.getAllProcedure().forEach( a -> list.add(new Keyword(a.get("procedure_name"), "function", getIcon("function"))));
                if (bool || (!bool && list.size() < Tooltip.tipMax)) db2Schema.getAllTrigger().forEach( a -> list.add(new Keyword(a.get("trigger_name"), "trigger", getIcon("trigger"))));
                if (bool || (!bool && list.size() < Tooltip.tipMax)) db2Schema.getAllSequence().forEach( a -> list.add(new Keyword(a.get("sequence_name"), "sequence", getIcon("sequence"))));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * 获取光标位置整句sql
     * @param val   编辑器内容
     * @param pos   光标位置
     * @return
     */
    private static String getSql(String val, int pos) {
        String flag = ";";
        int w = pos;
        while (w >-1 && flag.charAt(0) != val.charAt(w)) {
            w--;
        }
        int w2 = pos;
        while (w2 < val.length() && flag.charAt(0) != val.charAt(w2)) {
            w2++;
        }
        return val.substring(w+1,w2);
    }
    
    private static ImageIcon getIcon(String type){
        ImageIcon icon = null;
        if (!StringUtils.isNotBlank(type)) return null;
        switch (type.toLowerCase()) {
            case "table":
            case "table_group":
                icon = QueryEditUtil.tableIcon;
                break;
            case "view":
            case "mview":
            case "materialized view":
                icon = QueryEditUtil.viewIcon;
                break;
            case "sequence":
                icon = QueryEditUtil.sequenceIcon;
                break;
            case "function":
            case "procedure":
                icon = QueryEditUtil.functionIcon;
                break;
            case "trigger":
                icon = QueryEditUtil.triggerIcon;
                break;
            case "type":
                icon = QueryEditUtil.typeIcon;
                break;
            case "pack":
            case "package":
                icon = QueryEditUtil.packIcon;
                break;
            case "synonym":
                icon = QueryEditUtil.synonymIcon;
                break;
            default:
                break;
        }
        return icon;
    }
    
    public static void main(String[] args) {
        String s = "select * from  aaa jkl.t";
        System.out.println(TipUtil.analysisStr(s,0,"jkl."));
    }
}
