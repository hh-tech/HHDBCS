package com.hh.hhdb_admin.mgr.function.ui.run;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import com.hh.hhdb_admin.mgr.function.util.DebugUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrRunForm extends RunBaseForm {

    /**
     * 初始化运行面板
     * @param funMr
     * @param jdbcBean
     * @param packName  为包对象时的包名
     * @throws Exception
     */
    public OrRunForm(AbsFunMr funMr, JdbcBean jdbcBean,String packName) throws Exception {
        super(funMr,jdbcBean,packName);
        this.packName = packName;
        try {
            String name = StringUtils.isNotBlank(packName) ? packName : funMr.treeNode.getName();
            String type = StringUtils.isNotBlank(packName) ? "PACKAGE BODY" : this.type.name();
            if (DebugUtil.funVerify(jdbcBean,name,type)) {
                PopPaneUtil.error(StartUtil.parentFrame .getWindow(), funMr.treeNode.getName() + "无效");
                return;
            }
            textArea.setText(getSql(new HashMap<>()));
            show();
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame .getWindow(), FunctionMgr.getLang("error")+":"+e.getMessage());
        }
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
        jj.append(StringUtils.isNotBlank(packName) ? "\""+packName+"\"." : "").append("\""+funMr.treeNode.getName()+"\"(");
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
                    sb.append(FunctionMgr.getLang("run-succeed")+"！\n");
                    list.forEach(a -> sb.append(a+"\n"));
                }catch (Exception e){
                    e.printStackTrace();
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
