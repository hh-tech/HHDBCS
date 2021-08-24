package com.hh.hhdb_admin.mgr.function.ui.run;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import com.hh.hhdb_admin.mgr.function.util.DebugUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrRunForm extends RunBaseForm {

    private RunTool runTool;

    private String packName;        //包名

    /**
     * 初始化调试面板
     * @param funMr
     * @param jdbcBean
     * @param packName  为包对象时的包名
     * @throws Exception
     */
    public OrRunForm(AbsFunMr funMr, JdbcBean jdbcBean,String packName) throws Exception {
        super(funMr,jdbcBean);
        this.packName = packName;
        try {
            if (DebugUtil.funVerify(jdbcBean,funMr.treeNode.getName(),funMr.treeNode.getType().name())) {
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
    protected void runFun() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuffer sb = new StringBuffer();
                try {
                    if (paTable.getComp().isEditing()) paTable.getComp().getCellEditor().stopCellEditing();
                    runTool =  new RunTool(conn,textArea.getText());
                    List<String> list = runTool.procRun();
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

    @Override
    protected void finish() {
        if (null != runTool) runTool.cancel();
        ConnUtil.close(conn);
    }

    @Override
    protected List<Map<String, String>> infoParam() {
        Connection con = null;
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            con = ConnUtil.getConn(jdbcBean);
            List<Map<String, String>> parms = DebugUtil.infoParam(con,packName,funMr.treeNode.getName(),jdbcBean.getSchema());
            if (parms.isEmpty()) {
                return list;
            } else {
                for (Map<String, String> map : parms) {
                    if ("IN".equals(map.get("in_out")) || "IN/OUT".equals(map.get("in_out"))) {
                        Map<String, String> dparma = new HashMap<String, String>();
                        dparma.put("parameter", map.get("argument_name"));
                        dparma.put("dbType", map.get("data_type"));
                        dparma.put("value", "");
                        list.add(dparma);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ConnUtil.close(con);
        }
        return list;
    }

    @Override
    protected String getSql(Map<String, String> valMap) throws Exception {
        Connection con = null;
        String runsql = "";
        try {
            con = ConnUtil.getConn(jdbcBean);
            List<Map<String, String>> list = DebugUtil.infoParam(con,packName,funMr.treeNode.getName(),jdbcBean.getSchema());

            StringBuffer variate = new StringBuffer();
            StringBuffer dbms_output = new StringBuffer();
            StringBuffer param = new StringBuffer();
            for (Map<String, String> map : list) {
                if (map.get("argument_name") == null) continue;

                //变量
                String type = map.get("data_type").equals("VARCHAR2") ? "VARCHAR2 ( 4000 )" : map.get("data_type");
                String str = "\""+ map.get("argument_name") + "\" " + type;
                if ("IN".equals(map.get("in_out")) || "IN/OUT".equals(map.get("in_out"))) {
                    if ( null != valMap && StringUtils.isNotBlank(valMap.get(map.get("argument_name"))) ) {
                        str += " := '" + valMap.get(map.get("argument_name"))+"'";
                    }
                }
                variate.append("\t"+str+";\n");
                //输出语句
                if ("OUT".equals(map.get("in_out")) || "IN/OUT".equals(map.get("in_out"))) {
                    dbms_output.append("\tDBMS_OUTPUT.PUT_LINE ( '"+map.get("argument_name")+"' || ' = ' || "+map.get("argument_name")+" );\n");
                }
                //参数
                param.append(param.length()>1 ? "," : "").append("\""+map.get("argument_name")+"\" => \""+map.get("argument_name")+"\"");
            }

            StringBuffer sql = new StringBuffer();
            if (variate.length() >0) {
                sql.append("DECLARE\n").append(variate);
            }
            sql.append("BEGIN\n");

            //调用函数
            StringBuffer jj = new StringBuffer();
            jj.append( (funMr.treeNode.getType() == TreeMrType.FUNCTION || funMr.treeNode.getType() == TreeMrType.PACKAGE_FUNCTION) ? " " : "\t");
            jj.append(StringUtils.isNotBlank(packName) ? "\""+packName+"\"." : "").append("\""+funMr.treeNode.getName()+"\"(");
            jj.append(param).append(")");
            if ( funMr.treeNode.getType() == TreeMrType.FUNCTION || funMr.treeNode.getType() == TreeMrType.PACKAGE_FUNCTION ) {
                sql.append("\tDBMS_OUTPUT.PUT_LINE ( 'Return' || ' = ' || "+ jj +" );\n");
            } else {
                sql.append(jj).append(";\n");
            }

            sql.append(dbms_output);
            runsql = sql.append("END;\n").toString();
        }finally {
            ConnUtil.close(con);
        }
        return runsql;
    }

}
