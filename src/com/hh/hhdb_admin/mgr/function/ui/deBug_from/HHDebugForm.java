package com.hh.hhdb_admin.mgr.function.ui.deBug_from;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.function.debug.HHdbDebug;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.table.hh.HhDataTypeEnum;
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
import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class HHDebugForm extends DebugBaseForm {
    private HTabPanel hTabPane;
    
    private String debugSql;       //调试原始sql
    
    /**
     * 初始化调试页面
     * @param jdbcBean
     */
    public HHDebugForm(AbsFunMr funMr, JdbcBean jdbcBean) {
        super(funMr, jdbcBean);
        try {
            dparameter.setTabParas(DebugUtil.getParam(funMr,jdbcBean));
            editorMap.get(debugTitle).setText(debugSql = getSql(new HashMap<>()));
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
            if (currentline == -1) {     //从新开始
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
            dparameter.setParamEdit(false);
            xyhBut.setEnabled(true);
            stopBut.setEnabled(true);
            xyddBut.setEnabled(true);
            enterBut.setEnabled(true);
            tsBut.setEnabled(false);
        
            debug = FunUtil.getDebug(jdbcBean, funMr.treeNode.getId());
            debug.runProc(debugSql);
        
            Map<Integer, int[]> map = new LinkedHashMap<>();
            editorMap.get(debugTitle).setText(editText(debug.getSource(),map));
            linMap.put(debugTitle,map);
        
            getCurrentLine();
            dparameter.setTabVariables();
        }catch (Exception e){
            e.printStackTrace();
            finish();
            if (e instanceof SQLException) return;
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : FunctionMgr.getLang("invalid"));
        }
    }
    
    @Override
    protected void runDebug(String type) {
        if (currentline == -1) return;
        try {
            if (type.equals("stop")) {
                try {
                    if (currentline != 0) debug.stop();
                    return;
                }finally {
                    finish();
                }
            }
            new Thread(() -> {
                try {
                    if (type.equals("row")) {
                        System.out.println(debug.stepOver());
                    } else if (type.equals("dot")) {
                        System.out.println(debug.contineGo());
                    } else {
                        System.out.println(debug.stepInto());
                    }
                } catch (SQLException e) {
                    System.out.println("调试完成，关闭调试连接");
                } catch (Exception e) {
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(),e.getMessage());
                }
            }).start();
            Thread.sleep(300);
        
            //显示调试信息
            StringBuffer str = new StringBuffer();
            Map<String,String> info = ((HHdbDebug)debug).getInfo(str);
            dparameter.setTabMsg(str.toString());
            if (!info.isEmpty()) dparameter.setTabResult(info);
        
            if (((HHdbDebug)debug).isEnd) {
                finish();
            } else {
                List<Map<String,String>> vl = debug.getStack();
                Map<String,String> stack = vl.get(0);
                debugTitle = vl.size() > 1 ? stack.get("name"):"Script";
                if (type.equals("into") && !editorMap.containsKey(debugTitle)) {     //新建一个编辑页面
                    addTextArea(debugTitle);
                    Map<Integer, int[]> map = new LinkedHashMap<>();
                    editorMap.get(debugTitle).setText(editText(((HHdbDebug) debug).getSource(stack.get("oid")),map));  //获取进入对象的sql在编辑页面显示
                    linMap.put(debugTitle,map);
                    stackList = vl;
                }
            
                getCurrentLine();
                dparameter.setTabVariables();
            }
            hTabPane.selectPanel(debugTitle);
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),e.getMessage());
        }
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
        debugSql = st + "\"" + funMr.treeNode.getSchemaName() + "\".\"" + funMr.treeNode.getName() + "\"(" + sql;
        return debugSql;
    }
	
	/**
	 * 获取当前堆栈信息
	 *
	 * @throws Exception
	 */
	private void getCurrentLine() throws Exception {
        if (currentline != -1) {
            List<Map<String,String>> vl = debug.getStack();
            dparameter.setTabStack(vl);
            currentline = Integer.parseInt(vl.get(0).get("row"));
            
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
                            if (!reduce1.isEmpty()) ((HHdbDebug) debug).rmBreakPoints(reduce1.get(0),map.get("oid"));
                            
                            List<Integer> reduce2 = list.stream().filter(item -> !pointMap.get(titleAt).contains(item)).collect(Collectors.toList());
                            if (!reduce2.isEmpty()) {
                                ((HHdbDebug) debug).setBreakPoints(reduce2,map.get("oid"));
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
            dparameter.setParamEdit(true);
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),e.getMessage());
        }
    }
}
