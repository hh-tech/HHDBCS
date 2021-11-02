package com.hh.hhdb_admin.common.util.textEditor.tooltip;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbobj2.TabObjEnum;
import com.hh.frame.dbobj2.TableTool;
import com.hh.frame.dbobj2.db2.Db2Schema;
import com.hh.frame.dbobj2.dm.DmSchema;
import com.hh.frame.dbobj2.dm.DmSessionEnum;
import com.hh.frame.dbobj2.hhdb.HHdbSchema;
import com.hh.frame.dbobj2.hhdb.HHdbSessionEnum;
import com.hh.frame.dbobj2.mysql.MysqlSchema;
import com.hh.frame.dbobj2.ora.OraSchema;
import com.hh.frame.dbobj2.ora.OraSessionEnum;
import com.hh.frame.dbobj2.ora.pack.OraPackObj;
import com.hh.frame.dbobj2.sqlserver.SqlServerSchema;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditUtil;
import com.hh.hhdb_admin.common.util.textEditor.base.Keyword;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.sql.Connection;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TipUtil {
    
    /**
     * 判断是否与上次提示词相同
     * @param array
     * @return
     */
    public static boolean isListChange(JList<Object> list,List<Keyword> array) {
        DefaultListModel<Object> model = (DefaultListModel<Object>) list.getModel();
        if (array.size() != model.getSize()) return true;
        
        for (int i = 0; i < array.size(); i++) {
            if (!array.get(i).getName().equals(model.get(i))) return true;
        }
        return false;
    }
    
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
     * 获取对象子元素提示名称集合,如表的字段,包里函数等等
     * @param pos   光标位置
     * @param sql   编辑器内容
     * @param jdbc
     * @param name  用户输入字符
     * @return
     */
    public static List<Keyword> getSubList(int pos, String sql, JdbcBean jdbc, String name) {
        List<Keyword> list = new LinkedList<>();
        Connection conn = null;
        try {
            conn = ConnUtil.getConn(jdbc);
            DBTypeEnum dbtype = DriverUtil.getDbType(conn);
            String str = getTabName(getSql(sql,pos), name);
            while (true) {
                //查询表,视图列
                TableTool tab = new TableTool(conn, jdbc.getSchema(), str);
                tab.getSubObjNames(TabObjEnum.COLUMN).forEach( a -> list.add(new Keyword(a, "column", QueryEditUtil.columnIcon)));
                if (list.size() > 0) break;
                //查询包函数
                if (dbtype == DBTypeEnum.oracle) {
                    OraPackObj pack = new OraPackObj(conn, jdbc.getSchema(), str);
                    pack.getFunNames().forEach( a -> list.add(new Keyword(a, "pack", QueryEditUtil.functionIcon)));
                    pack.getProcNames().forEach( a -> list.add(new Keyword(a, "pack", QueryEditUtil.functionIcon)));
                    if (list.size() > 0) break;
                }
                //查询模式下的所有对象
                if (dbtype == DBTypeEnum.oracle) {
                    List<OraSessionEnum> trypList = Arrays.asList(OraSessionEnum.table,OraSessionEnum.view,OraSessionEnum.mview,
                            OraSessionEnum.sequence,OraSessionEnum.function,OraSessionEnum.trigger,OraSessionEnum.type,OraSessionEnum.pack);
                    OraSchema oraSchema = new OraSchema(conn, str);
                    for (OraSessionEnum ty : trypList) {
                        oraSchema.getChildList(ty).forEach(a -> {
                            String typeName = OraSessionEnum.pack == ty ? "package_name" : ty.name()+"_name";
                            list.add(new Keyword(a.get(typeName), ty.name(), getIcon(ty.name())));
                        });
                    }
                } else if (dbtype == DBTypeEnum.hhdb || dbtype == DBTypeEnum.pgsql) {
                    HHdbPgsqlPrefixEnum prefix = dbtype == DBTypeEnum.pgsql ? HHdbPgsqlPrefixEnum.pg : HHdbPgsqlPrefixEnum.hh;
                    List<HHdbSessionEnum> trypList = Arrays.asList(HHdbSessionEnum.table,HHdbSessionEnum.view,HHdbSessionEnum.mview,
                            HHdbSessionEnum.sequence,HHdbSessionEnum.function,HHdbSessionEnum.type);
                    //分别查询大小写模式下的内容
                    HHdbSchema hhSchema = new HHdbSchema(conn,str.toLowerCase(), prefix);
                    for (HHdbSessionEnum ty : trypList) {
                        hhSchema.getChildList(ty).forEach( a -> list.add(new Keyword(a.get(ty.name()+"_name"), ty.name(), getIcon(ty.name()))));
                    }
                    hhSchema = new HHdbSchema(conn,str.toUpperCase(), prefix);
                    for (HHdbSessionEnum ty : trypList) {
                        hhSchema.getChildList(ty).forEach( a -> list.add(new Keyword(a.get(ty.name()+"_name"), ty.name(), getIcon(ty.name()))));
                    }
                } else if (dbtype == DBTypeEnum.mysql) {
                    MysqlSchema mySchema = new MysqlSchema(conn,str);
                    mySchema.getAllTable().forEach( a -> list.add(new Keyword(a.get("table_name"), "table", getIcon("table"))));
                    mySchema.getAllView().forEach( a -> list.add(new Keyword(a.get("view_name"), "view", getIcon("view"))));
                    mySchema.getAllFunction().forEach( a -> list.add(new Keyword(a.get("name"), "function", getIcon("function"))));
                    mySchema.getAllProcedure().forEach( a -> list.add(new Keyword(a.get("name"), "function", getIcon("function"))));
                } else if (dbtype == DBTypeEnum.sqlserver) {
                    List<TreeMrType> trypList = Arrays.asList(TreeMrType.TABLE_GROUP,TreeMrType.VIEW,TreeMrType.FUNCTION
                            ,TreeMrType.PROCEDURE,TreeMrType.TYPE,TreeMrType.TRIGGER,TreeMrType.RULE,TreeMrType.SYNONYM);
                    SqlServerSchema ssSchema = new SqlServerSchema(conn,str);
                    for (TreeMrType ty : trypList) {
                        ssSchema.getAllChild(ty).forEach( a -> list.add(new Keyword(a.get("name"), ty.name(), getIcon(ty.name()))));
                    }
                } else if (dbtype == DBTypeEnum.dm) {
                    List<DmSessionEnum> trypList = Arrays.asList(DmSessionEnum.table,DmSessionEnum.view,DmSessionEnum.mview,
                            DmSessionEnum.sequence,DmSessionEnum.function,DmSessionEnum.trigger,DmSessionEnum.type,DmSessionEnum.pack);
                    DmSchema dmSchema = new DmSchema(conn, str);
                    for (DmSessionEnum ty : trypList) {
                        dmSchema.getChildList(ty).forEach(a -> {
                            String typeName = DmSessionEnum.pack == ty ? "package_name" : ty.name()+"_name";
                            list.add(new Keyword(a.get(typeName), ty.name(), getIcon(ty.name())));
                        });
                    }
                } else if (dbtype == DBTypeEnum.db2) {
                    Db2Schema db2Schema = new Db2Schema(conn,str);
                    db2Schema.getAllTable().forEach( a -> list.add(new Keyword(a.get("table_name"), "table", getIcon("table"))));
                    db2Schema.getAllView().forEach( a -> list.add(new Keyword(a.get("view_name"), "view", getIcon("view"))));
                    db2Schema.getAllFunction().forEach( a -> list.add(new Keyword(a.get("function_name"), "function", getIcon("function"))));
                    db2Schema.getAllProcedure().forEach( a -> list.add(new Keyword(a.get("procedure_name"), "function", getIcon("function"))));
                    db2Schema.getAllTrigger().forEach( a -> list.add(new Keyword(a.get("trigger_name"), "trigger", getIcon("trigger"))));
                    db2Schema.getAllSequence().forEach( a -> list.add(new Keyword(a.get("sequence_name"), "sequence", getIcon("sequence"))));
                }
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            ConnUtil.close(conn);
        }
        return list;
    }
    
    private static ImageIcon getIcon(String type){
        ImageIcon icon = null;
        switch (type.toLowerCase()) {
            case "table":
            case "table_group":
                icon = QueryEditUtil.tableIcon;
                break;
            case "view":
            case "mview":
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
                icon = QueryEditUtil.packIcon;
                break;
            default:
                break;
        }
        return icon;
    }
    
    /**
     * 获取光标位置整句sql
     * @param sql
     * @param pos
     * @return
     */
    private static String getSql(String sql, int pos) {
        String flag = ";";
        
        int w = pos;
        while (w >-1 && flag.charAt(0) != sql.charAt(w)) {
            w--;
        }
        
        int w2 = pos;
        while (w2 < sql.length() && flag.charAt(0) != sql.charAt(w2)) {
            w2++;
        }
        
        return sql.substring(w+1,w2);
    }
    
    /**
     * 根据输入解析表名
     * @param sql
     * @param name 需要提示的字符
     * @return
     */
    private static String getTabName(String sql,String name){
        List<String> list = new LinkedList<>();
        String[] val = sql.trim().split(" ");
        for (String string : val) {
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
//        System.out.println(list);
    
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
    
    
    public static void main(String[] args) {
        String s = "select * from  aaa a  bbbb b ccc c";
        System.out.println(TipUtil.getTabName(s,"a."));
    }
}
