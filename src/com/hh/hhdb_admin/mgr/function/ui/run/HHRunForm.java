package com.hh.hhdb_admin.mgr.function.ui.run;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.table.hh.HhDataTypeEnum;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HHRunForm extends RunBaseForm {
    private HTable resTable;

    public HHRunForm(AbsFunMr funMr, JdbcBean jdbcBean) throws Exception {
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
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (parMap.isEmpty()) {
            return list;
        } else {
            for (Map<String, String> map : parMap) {
                if ("IN".equals(map.get("in_out")) || "INOUT".equals(map.get("in_out"))) {
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
        StringBuffer sql = new StringBuffer();
        valMap.keySet().forEach(a -> {
            String type = valMap.get(a).get(0);
            String val = valMap.get(a).get(1);
            if (type.toUpperCase().equals(HhDataTypeEnum.CHAR.name()) || type.toUpperCase().equals(HhDataTypeEnum.VARCHAR.name())
                    ||type.toUpperCase().equals(HhDataTypeEnum.CHARACTER_VARYING.name())||type.toUpperCase().equals("CHARACTER VARYING")) {
                val = "'"+val+"'";
            }
            sql.append(sql.length()>0 ? "," : "").append(val + "::" + type);
        });
        sql.append(");");
        String st = funMr.treeNode.getType() == TreeMrType.FUNCTION ? "select " : "CALL ";
        return st + "\"" + funMr.treeNode.getSchemaName() + "\".\"" + funMr.treeNode.getName() + "\"(" + sql;
    }
    
    @Override
    protected void runFun() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuffer sb = new StringBuffer();
                try {
                    if (null != resTable) resTable.load(new ArrayList<>(), 1);
                    StringBuffer outStr = new StringBuffer();
                    runTool =  new RunTool(conn,textArea.getText());
                    Map<String,String> map = runTool.hhPgRun(outStr);
                    sb.append(FunctionMgr.getLang("run-succeed")+"！\n");
                    sb.append(outStr);
    
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
