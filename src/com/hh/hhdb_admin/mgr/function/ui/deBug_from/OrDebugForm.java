package com.hh.hhdb_admin.mgr.function.ui.deBug_from;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.function.debug.OraDebug;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditorTextArea;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import com.hh.hhdb_admin.mgr.function.util.DebugUtil;
import com.hh.hhdb_admin.mgr.function.util.FunUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class OrDebugForm extends DebugBaseForm {
    private HTabPanel hTabPane;

    private Map<String,String> resMap = new LinkedHashMap<>(); //调试返回值和输出参数名称集合

    //调试模版sql
    private String titleSql = "declare \n" +
            "  -- Local variables here\n" +
            "  i integer;\n" +
            "begin\n" +
            "  -- Test statements here\n" +
            "  \n" +
            "end;";
    
    /**
     * 初始化调试面板
     * @param jdbcBean
     */
    public OrDebugForm(JdbcBean jdbcBean) {
        this(null,jdbcBean);
    }
    
    /**
     * 初始化函数调试页面
     * @param funMr
     * @param jdbcBean
     */
    public OrDebugForm(AbsFunMr funMr, JdbcBean jdbcBean) {
        super(funMr, jdbcBean);
        try {
            if (null != funMr && DebugUtil.funVerify(jdbcBean,funMr.treeNode.getName(),funMr.treeNode.getType().name())) {
                PopPaneUtil.error(StartUtil.parentFrame.getWindow(), funMr.treeNode.getName() + "无效");
                return;
            }
            if (null != funMr) dparameter.setTabParas(DebugUtil.getParam(funMr,jdbcBean));
            editorMap.get(debugTitle).setText(null != funMr ? getSql(new HashMap<>()) : titleSql );
            editorMap.get(debugTitle).getTextArea().setEditable(null == funMr);
            xyhBut.setEnabled(false);
            stopBut.setEnabled(false);
            xyddBut.setEnabled(false);
            enterBut.setEnabled(false);
            show();
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame .getWindow(), FunctionMgr.getLang("error")+":"+e.getMessage());
        }
        
    }

    @Override
    protected HSplitPanel getHSplitPanel() {
        editorMap = new LinkedHashMap<>();
        linMap  = new LinkedHashMap<>();
        pointMap = new LinkedHashMap<>();
        hTabPane = new HTabPanel() {
            @Override
            public void onSelected(String id) {
                JTabbedPane tab = (JTabbedPane)getComp();
                titleAt = tab.getTitleAt(tab.getSelectedIndex());
            }
        };
        addTextArea(debugTitle = "Script");
        qed = editorMap.get("Script");

        LastPanel lastPanel = new LastPanel(false);
        lastPanel.set(hTabPane.getComp());

        HSplitPanel splitPane = new HSplitPanel(false);
        splitPane.setSplitWeight(0.5);
        JSplitPane jsp = splitPane.getComp();
        jsp.setLeftComponent(lastPanel.getComp());
        return splitPane;
    }
    
    @Override
    protected void startDebug() {
        try {
            //初始化页面信息
            if (currentline == -1) {
                //从新开始
                finish();
                Iterator<Map.Entry<String, QueryEditorTextArea>> it = editorMap.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry<String, QueryEditorTextArea> entry = it.next();
                    if(!entry.getKey().equals("Script")) {
                        hTabPane.close(entry.getKey());
                        it.remove();
                    }
                }
                linMap  = new LinkedHashMap<>();
                pointMap.put("Script",new LinkedList<>());
                currentline = 0;
                debugTitle = "Script";
                titleAt = "Script";
            }
            //删除编辑器上所有书签
            editorMap.get(debugTitle).hTextArea.getArea().getScrollPane().getGutter().removeAllTrackingIcons();
        
            if (null != funMr) {
                //函数调试时关闭参数输入，获取调试函数的输出值
                dparameter.setParamEdit(false);
                resMap = DebugUtil.getOutPara(funMr.treeNode,jdbcBean);
                if (funMr.treeNode.getType() == TreeMrType.FUNCTION) resMap.put("RESULT","");
            }
        
            String sql = editorMap.get(debugTitle).getText();
            if (StringUtils.isBlank(sql) || sql.equals(titleSql)) {
                PopPaneUtil.info(StartUtil.parentFrame.getWindow(),FunctionMgr.getLang("correctStatement"));
                return;
            }
            editorMap.get(debugTitle).getTextArea().setEditable(false);
            xyhBut.setEnabled(true);
            stopBut.setEnabled(true);
            xyddBut.setEnabled(true);
            enterBut.setEnabled(true);
            tsBut.setEnabled(false);
    
            debug = FunUtil.getDebug(jdbcBean, null == funMr ? null : funMr.treeNode.getId());
            debug.runProc(sql);
    
            Map<Integer, int[]> map = new LinkedHashMap<>();
            editorMap.get(debugTitle).setText(editText(debug.getSource(),map));
            linMap.put(debugTitle,map);
            currentline = debug.stepInto();
    
            getCurrentLine();
            dparameter.setTabVariables();
        }catch (Exception e){
            e.printStackTrace();
            finish();
            if (e instanceof SQLException) return;
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : FunctionMgr.getLang("invalid"));
        }
    }
    
    @Override
    protected synchronized void runDebug(String type) {
        if (currentline == -1) return;
        try {
            if (type.equals("stop")) {
                try {
                    if (currentline != 0) debug.stop();
                    if (null == funMr) editorMap.get(debugTitle).getTextArea().setEditable(true);
                    return;
                }finally {
                    finish();
                }
            } else if (type.equals("dot")) {
                currentline = debug.contineGo();
            } else if (type.equals("row")) {
                currentline = debug.stepOver();
            } else {
                currentline = debug.stepInto();
            }
            Thread.sleep(300);
        
            if (currentline == 0) {    //当返回为0时表示调试结束
                finish();
            } else {
                List<Map<String,String>> vl = DebugUtil.stackAnalysis(((OraDebug) debug).showStack());
                String name  = vl.get(vl.size() - 1).get("name");
                debugTitle = StringUtils.isNotBlank(name) ? name:"Script";
            
                if (type.equals("into") && !editorMap.containsKey(debugTitle)) {
                    //新建一个编辑页面
                    addTextArea(debugTitle);
                    //获取进入对象的sql在编辑页面显示
                    Map<Integer, int[]> map = new LinkedHashMap<>();
                    String sql = DebugUtil.getCreateSql(jdbcBean, debugTitle, null != funMr ? funMr.treeNode.getSchemaName() : jdbcBean.getSchema());
                    sql = editText(sql,map);
                    linMap.put(debugTitle,map);
                    editorMap.get(debugTitle).setText(sql);
                
                    stackList = vl;
                }
            
                getCurrentLine();
                dparameter.setTabVariables();
                dparameter.setTabResult(resMap);
            }
            hTabPane.selectPanel(debugTitle);
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),e.getMessage());
        }
    }
    
    @Override
    protected String getSql(Map<String, List<String>> valMap) throws Exception {
        Connection con = null;
        String runsql = "";
        try {
            con = ConnUtil.getConn(jdbcBean);
            StringBuffer variate = new StringBuffer();
            StringBuffer param = new StringBuffer();
            List<Map<String, Object>> jsList = funMr.getFunParameter(con);
            if (null != jsList) {
                for (Map<String, Object> map : jsList) {
                    String type = map.get("PLS_TYPE")+"";
                    String name = map.get("ARGUMENT_NAME")+"";
                    String in_out = map.get("IN_OUT")+"";
                    if (map.get("ARGUMENT_NAME") == null) {
                        //添加函数返回值
                        if ( funMr.treeNode.getType() == TreeMrType.FUNCTION ) variate.append("\tRESULT " + ( type.equals("VARCHAR2") ? "VARCHAR2 ( 4000 )" : type)).append(";\n");
                        continue;
                    }
                    
                    //变量
                    String str = type.equals("VARCHAR2") ? "VARCHAR2 ( 4000 )" : type;
                    str = "\""+ name + "\" " + str;
                    if (!valMap.isEmpty() && ("IN".equals(in_out) || "IN/OUT".equals(in_out))) {
                        List<String> li = valMap.get(name);
                        if (StringUtils.isNotBlank(li.get(1)))  str += " := '" + li.get(1) +"'";
                    }
                    variate.append("\t"+str+";\n");
                    //参数
                    param.append(param.length()>1 ? "," : "").append("\""+name+"\" => \""+name+"\"");
                }
            }
        
            StringBuffer sql = new StringBuffer();
            if (variate.length() >0) {
                sql.append("DECLARE\n").append(variate);
            }
            sql.append("BEGIN\n");
        
            //调用函数
            sql.append( funMr.treeNode.getType() == TreeMrType.FUNCTION ? "\tRESULT :=" : "");
            sql.append( funMr.treeNode.getType() == TreeMrType.FUNCTION ? " " : "\t");
            sql.append("\""+ funMr.treeNode.getName() +"\"(");
            sql.append(param).append(");\n");
            runsql = sql.append("END;\n").toString();
        }finally {
            ConnUtil.close(con);
        }
        return runsql;
    }
    
    /**
     * 获取当前堆栈信息
     * @throws Exception
     */
    private void getCurrentLine() throws Exception {
        if (currentline != -1) {
            List<Map<String, String>> list = new LinkedList<>();

            String val = ((OraDebug) debug).showStack();
            List<Map<String,String>> vl = DebugUtil.stackAnalysis(val);
            for (Map<String, String> map1 : vl) {
                Map<String, String> map = new HashMap<>();
                map.put("row", map1.get("line"));
                map.put("name", map1.get("name"));
                map.put("value", map1.toString().replace("{", "").replace("}", ""));
                list.add(map);
            }
            dparameter.setTabStack(list);
    
            //清除所有页面编辑器高亮显示
            editorMap.keySet().forEach(a -> editorMap.get(a).getTextArea().getHighlighter().removeAllHighlights());
            //设置当前调试行高亮显示
            int[] attr = linMap.get(debugTitle).get(currentline);
            if (attr != null && attr.length > 0) {
                editorMap.get(debugTitle).getTextArea().getHighlighter().addHighlight(attr[0], attr[1], new DefaultHighlighter.DefaultHighlightPainter(new Color(172, 192, 253)));
            }
        }
    }

    /**
     * 添加编辑面板
     */
    private void addTextArea(String name) {
        QueryEditorTextArea qed = new QueryEditorTextArea(false) {
            @Override
            public synchronized void bookmarksAc() {
                //添加或取消断点
                List<Integer> list = getbookmaskLines();
                try {
                    if (null != debug) {
                        //判断是主页还是子对象打断点
                        if (titleAt.equals("Script")) {
                            List<Integer> reduce1 = pointMap.get(titleAt).stream().filter(item -> !list.contains(item)).collect(Collectors.toList());
                            if (!reduce1.isEmpty()) debug.rmBreakPoints(reduce1);
                            List<Integer> reduce2 = list.stream().filter(item -> !pointMap.get(titleAt).contains(item)).collect(Collectors.toList());
                            if (!reduce2.isEmpty()) debug.setBreakPoints(reduce2);
                        } else {
                            Map<String,String> map = new LinkedHashMap<>();
                            for (Map<String, String> strMap : stackList) {
                                if (strMap.get("name").equals(titleAt)) {
                                    map = strMap;
                                    break;
                                }
                            }

                            List<Integer> reduce1 = pointMap.get(titleAt).stream().filter(item -> !list.contains(item)).collect(Collectors.toList());
                            if (!reduce1.isEmpty()) ((OraDebug) debug).rmBreakPoints(titleAt,reduce1.get(0));

                            List<Integer> reduce2 = list.stream().filter(item -> !pointMap.get(titleAt).contains(item)).collect(Collectors.toList());
                            if (!reduce2.isEmpty()) {
                                String type = map.get("type");
                                String ns =  type.contains("PACKAGE") ?  "PACKAGE_BODY" : "TOP_LEVEL";
                                ((OraDebug) debug).setBreakPoints(reduce2,titleAt,jdbcBean.getUser(),type,ns);
                            }
                        }
                    }
                } catch (Exception e) {
                    try {
                        //删除错误的书签
                        List<Integer> reduce2 = list.stream().filter(item -> !pointMap.get(titleAt).contains(item)).collect(Collectors.toList());
                        for (Integer integer : reduce2) {
                            hTextArea.getArea().getScrollPane().getGutter().toggleBookmark(Math.max((integer - 1), 0));
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(),FunctionMgr.getLang("Errorbreakpoint"));
                } finally {
                    if (null != debug) pointMap.put(titleAt,getbookmaskLines());
                }
            }
        };
        qed.getTextArea().setRows(15);
        qed.hTextArea.showBookMask(true);
    
        HeaderConfig hec = new HeaderConfig(name);
        hec.setFixTab(true);
        hec.setTitleEditable(false);
        hTabPane.addPanel(name, qed, hec);
        editorMap.put(name, qed);
        pointMap.put(name,new LinkedList<>());
    }

    /**
     * 调试结束后续操作
     */
    private void finish() {
        try {
            //清除编辑器高亮显示
            editorMap.get(debugTitle).getTextArea().getHighlighter().removeAllHighlights();
            currentline = -1;
            if (null != debug) debug.close();
            debug = null;
            //禁用按钮
            xyhBut.setEnabled(false);
            stopBut.setEnabled(false);
            xyddBut.setEnabled(false);
            enterBut.setEnabled(false);
            tsBut.setEnabled(true);
            if (null != funMr) dparameter.setParamEdit(true);
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),e.getMessage());
        }
    }
}
