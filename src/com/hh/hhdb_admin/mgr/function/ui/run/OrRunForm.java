package com.hh.hhdb_admin.mgr.function.ui.run;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrRunForm extends RunBaseForm {

    public OrRunForm(AbsFunMr funMr, Connection conn, JdbcBean jdbcBean, List<Map<String, String>> list) {
        super(funMr,conn,jdbcBean,list);
    }

    @Override
    protected List<Map<String, String>> infoParam() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (parMap.isEmpty()) {
            return list;
        } else {
            for (Map<String, String> map : parMap) {
                if ("IN".equals(map.get("in_out")) || "IN/OUT".equals(map.get("in_out"))) {
                    if (map.get("parameter") == null) continue;
                    Map<String, String> dparma = new HashMap<String, String>();
                    dparma.put("name", map.get("parameter"));
                    dparma.put("type", map.get("type"));
                    dparma.put("value", "");
                    list.add(dparma);
                }
            }
        }
        return list;
    }

    @Override
    protected String getSql(Map<String, List<String>> valMap) throws Exception {
        String runsql = "";
        StringBuffer variate = new StringBuffer();
        StringBuffer dbms_output = new StringBuffer();
        StringBuffer param = new StringBuffer();
        for (Map<String, String> map : parMap) {
            if (map.get("parameter") == null) continue;

            //变量
            String type = map.get("type").equals("VARCHAR2") ? "VARCHAR2 ( 4000 )" : map.get("type");
            String str = "\""+ map.get("parameter") + "\" " + type;
            if ("IN".equals(map.get("in_out")) || "IN/OUT".equals(map.get("in_out"))) {
                if ( !valMap.isEmpty() && StringUtils.isNotBlank(valMap.get(map.get("parameter")).get(1)) ) {
                    str += " := '" + valMap.get(map.get("parameter")).get(1)+"'";
                }
            }
            variate.append("\t"+str+";\n");
            //输出语句
            if ("OUT".equals(map.get("in_out")) || "IN/OUT".equals(map.get("in_out"))) {
                dbms_output.append("\tDBMS_OUTPUT.PUT_LINE ( '"+map.get("parameter")+"' || ' = ' || "+map.get("parameter")+" );\n");
            }
            //参数
            param.append(param.length()>1 ? "," : "").append("\""+map.get("parameter")+"\" => "+map.get("parameter"));
        }

        StringBuffer sql = new StringBuffer();
        if (variate.length() >0) {
            sql.append("DECLARE\n").append(variate);
        }
        sql.append("BEGIN\n");

        //调用函数
        StringBuffer jj = new StringBuffer();
        jj.append( (type == TreeMrType.FUNCTION || type == TreeMrType.PACKAGE_FUNCTION) ? " " : "\t");
        jj.append(StringUtils.isNotBlank(funMr.treeNode.getPackageName()) ? "\""+funMr.treeNode.getPackageName()+"\"." : "").append("\""+funMr.treeNode.getName()+"\"(");
        jj.append(param).append(")");
        if ( type == TreeMrType.FUNCTION || type == TreeMrType.PACKAGE_FUNCTION ) {
            sql.append("\tDBMS_OUTPUT.PUT_LINE ( 'Return' || ' = ' || "+ jj +" );\n");
        } else {
            sql.append(jj).append(";\n");
        }

        sql.append(dbms_output);
        runsql = sql.append("END;\n").toString();
        return runsql;
    }
    
    @Override
    protected void runFun() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuffer sb = new StringBuffer();
                try {
                    runTool =  new RunTool(conn,textArea.getText());
                    List<String> list = runTool.oracleRun();
                    sb.append(LangMgr.lang(Word4.RUN_SUCCESSFULLY)+"！\n");
                    list.forEach(a -> sb.append(a+"\n"));
                }catch (Exception e){
                    Lg.error(OrRunForm.class, e);
                    sb.append(e.getMessage()+"\n");
                    finish();
                }finally {
                    messageText.setValue(sb.toString());
                    stopBut.setEnabled(false);
                    runBut.setEnabled(true);
                }
            }
        }).start();
    }
    
}
