package com.hh.hhdb_admin.mgr.function.ui.run;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;

import java.sql.Connection;
import java.util.*;

public class MysqlRunForm extends RunBaseForm {
    private HTable resTable;
    
    private List<String> parList;   //存储过程需要设置的参数集合
    private StringBuffer outSql;    //获取存储过程输出信息sql

    public MysqlRunForm(AbsFunMr funMr, JdbcBean jdbcBean) throws Exception {
        super(funMr,jdbcBean,"");
        try {
            textArea.setText(getSql(new HashMap<>()));
            show();
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), FunctionMgr.getLang("error")+":"+e.getMessage());
        }
    }

    @Override
    protected List<Map<String, String>> infoParam() {
        Connection con = null;
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            con = ConnUtil.getConn(jdbcBean);
            Map<String, String> parms = funMr.getFunInPar(con);
            if (parms.isEmpty()) {
                return list;
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
        }finally {
            ConnUtil.close(con);
        }
        return list;
    }

    @Override
    protected String getSql(Map<String, List<String>> valMap) throws Exception {
        Connection con = null;
        try {
            con = ConnUtil.getConn(jdbcBean);
            StringBuffer finalParms = new StringBuffer();
            if (type.name().equals("FUNCTION")) {
                for (String str : valMap.keySet()) {   //设置参数
                    if (null != str) finalParms.append(finalParms.length() == 0 ? "" : ",").append("'" + valMap.get(str).get(1) + "'");
                }
                return "select `"+funMr.treeNode.getSchemaName()+"`.`"+funMr.treeNode.getName()+"`("+finalParms+");";
            } else {
                outSql = new StringBuffer();
                parList = new LinkedList<>();
                for (String str : valMap.keySet()) {   //设置参数
                    if (null != str) parList.add("SET @`"+str+"` = '"+valMap.get(str).get(1)+"'");
                }
                Map<String, JsonObject> mapPar = funMr.getFunAllPar(con);
                for (String str : mapPar.keySet()) {
                    //获取输出参数
                    if (mapPar.get(str).getString("out_in").equals("OUT") || mapPar.get(str).getString("out_in").equals("INOUT")) {
                        outSql.append(outSql.length() == 0 ? "SELECT" : ",").append("@`"+str+"`");
                    }
                    finalParms.append(finalParms.length() == 0 ? "" : ",").append("@`"+str+"`");
                }
                return "CALL `" + funMr.treeNode.getSchemaName() + "`.`" + funMr.treeNode.getName() + "`(" + finalParms + ");";
            }
        }finally {
            ConnUtil.close(con);
        }
    }
    
    @Override
    protected void runFun() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuffer sb = new StringBuffer();
                try {
                    if (null != resTable) resTable.load(new ArrayList<>(), 1);
                    runTool =  new RunTool(conn,textArea.getText());
                    Map<String,String> map = runTool.mysqlRun(outSql,parList);
                    sb.append(FunctionMgr.getLang("run-succeed")+"！\n");
    
                    if (!map.isEmpty()) {
                        if (null == resTable) {
                            resTable = new HTable();
                            resTable.setRowHeight(25);
                            resTable.hideSeqCol();
                            map.keySet().forEach(a -> resTable.addCols(new DataCol(a, a)));
                            LastPanel lastPanel = new LastPanel(false);
                            lastPanel.setWithScroll(resTable.getComp());
                            hTabPane.addPanel("result", FunctionMgr.getLang("result"), lastPanel.getComp());
                        }
                        List<Map<String, String>> valist = new ArrayList<Map<String, String>>();
                        Map<String, String> dparma = new HashMap<String, String>();
                        map.keySet().forEach(a -> dparma.put(a, map.get(a)+""));
                        valist.add(dparma);
                        resTable.load(valist, 1);
                    }
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
