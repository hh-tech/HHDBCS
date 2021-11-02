package com.hh.hhdb_admin.mgr.function.ui.run;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.sqlwin.rs.MultiRsBean;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlsrverRunForm extends RunBaseForm {

    public SqlsrverRunForm(AbsFunMr funMr, JdbcBean jdbcBean) throws Exception {
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
                    dparma.put("parameter", str);
                    dparma.put("dbType", parms.get(str));
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
    protected String getSql(Map<String, String> valMap) throws Exception {
        String runsql = "";
        Connection con = null;
        try {
            con = ConnUtil.getConn(jdbcBean);
            if (funMr.ftype.equals("TF") || funMr.ftype.equals("IF")) {     //表函数
                StringBuffer finalParms = new StringBuffer();
                //设置参数
                for (String str : valMap.keySet()) {
                    if (null != str) finalParms.append(finalParms.length() == 0 ? "" : ",").append("'"+valMap.get(str)+"'");
                }
                runsql = "select * from "+funMr.treeNode.getSchemaName()+"."+funMr.treeNode.getName()+"("+finalParms+");";
            }else if (type == TreeMrType.PROCEDURE || funMr.ftype.equals("FN")){     //普通函数或者存储过程
                StringBuffer variate = new StringBuffer();
                StringBuffer param = new StringBuffer();
                StringBuffer outParam = new StringBuffer();
                List<Map<String, Object>> list = funMr.getFunParameter(con);
                for (Map<String, Object> map : list) {
                    String type = null == map.get("length") ? map.get("type").toString() : map.get("type").toString()+ "("+map.get("length").toString()+")";
                    if (!StringUtils.isNotBlank(map.get("name").toString())) {
                        //添加函数的返回值
                        if ( this.type == TreeMrType.FUNCTION ) {
                            variate.append("\t@RESULT " + type);
                            outParam.append("\tSELECT @RESULT AS 'Return Result'\n");
                        }
                        continue;
                    }
                    //变量
                    String str = map.get("name") + " " + type;
                    if ( null != valMap && StringUtils.isNotBlank(valMap.get(map.get("name").toString())) ) {
                        str += " = '" + valMap.get(map.get("name").toString())+"'";
                    }
                    variate.append(variate.length()>1 ? ",\n" : "").append("\t"+str+"");
                    //参数
                    if ( this.type == TreeMrType.FUNCTION ) {
                        param.append(param.length()>1 ? "," : "").append(map.get("name"));
                    } else {
                        param.append(param.length()>1 ? "," : "").append(map.get("name"));
                        if (map.get("in_out").equals("INOUT") || map.get("in_out").equals("OUT")) {
                            param.append("="+map.get("name")+ " OUTPUT");
                            outParam.append("\tSELECT "+map.get("name")+" AS '"+map.get("name")+"'\n");
                        }
                    }
                }

                StringBuffer sql = new StringBuffer();
                if (variate.length() >0) {
                    sql.append("DECLARE\n").append(variate+"\n");
                }
                sql.append("EXEC\n");
                //调用函数
                sql.append( type == TreeMrType.FUNCTION ? "\t@RESULT = " : "\t");
                sql.append(funMr.treeNode.getSchemaName()+"."+funMr.treeNode.getName()).append(" "+param+"\n");
                //查询输出参数
                sql.append(outParam);

                runsql = sql.toString();
            }
        }finally {
            ConnUtil.close(con);
        }
        return runsql;
    }
    
    @Override
    protected void runFun() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuffer sb = new StringBuffer();
                try {
                    JTabbedPane jtp = (JTabbedPane)hTabPane.getComp();
                    while (jtp.getTabCount() > 2) {
                        hTabPane.closeTabPanel("result" + (jtp.getTabCount()-2));
                    }
                    jtp.updateUI();
                    
                    runTool =  new RunTool(conn,textArea.getText());
                    MultiRsBean mr = runTool.sqlsrverRun();
                    sb.append(FunctionMgr.getLang("run-succeed")+"！\n");
                    
                    if (null != mr) {
                        Map<String, List<List<String>>> rsMap = mr.getRsMap();
                        for (String s : rsMap.keySet()) {
                            List<List<String>> list = rsMap.get(s);
    
                            HTable resTable = new HTable();
                            resTable.setRowHeight(25);
                            resTable.hideSeqCol();
                            list.get(0).forEach(a -> resTable.addCols(new DataCol(a, a)));
                            LastPanel lastPanel = new LastPanel(false);
                            lastPanel.setWithScroll(resTable.getComp());
                            hTabPane.addPanel("result" + s, FunctionMgr.getLang("result") + s, lastPanel.getComp(), true);
    
                            List<Map<String, String>> valist = new ArrayList<Map<String, String>>();
                            for (int i = 1; i < list.size(); i++) {
                                List<String> val = list.get(i);
                                Map<String, String> dparma = new HashMap<String, String>();
                                for (int j = 0; j < val.size(); j++) {
                                    dparma.put(list.get(0).get(j), val.get(j));
                                }
                                valist.add(dparma);
                            }
                            resTable.load(valist, 1);
                        }
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
