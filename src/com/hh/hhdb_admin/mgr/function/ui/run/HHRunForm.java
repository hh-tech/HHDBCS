package com.hh.hhdb_admin.mgr.function.ui.run;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HHRunForm extends RunBaseForm {

    protected RunTool runTool;

    /**
     * 初始化调试面板
     *
     * @param jdbcBean
     * @param pars
     */
    public HHRunForm(AbsFunMr funMr, JdbcBean jdbcBean) throws Exception {
        super(funMr,jdbcBean);
        try {
            textArea.setText(getSql(new HashMap<>()));
            show();
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), FunctionMgr.getLang("error")+":"+e.getMessage());
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
        StringBuffer finalParms = new StringBuffer();
        for (String str : valMap.keySet()) {   //设置参数
            if (null != str)
                finalParms.append(finalParms.length() == 0 ? "" : ",").append("'" + valMap.get(str) + "'");
        }
        
        if (funMr.treeNode.getType().name().equals("FUNCTION")) {
            return "select \"" + funMr.treeNode.getSchemaName() + "\".\"" + funMr.treeNode.getName() + "\"(" + finalParms + ");";
        } else {
            return "CALL \"" + funMr.treeNode.getSchemaName() + "\".\"" + funMr.treeNode.getName() + "\"(" + finalParms + ");";
        }
    }
}
