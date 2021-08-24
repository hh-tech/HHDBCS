package com.hh.hhdb_admin.mgr.function.ui.deBug_from;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.function.debug.DebugTool;
import com.hh.frame.create_dbobj.function.debug.OraDebug;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.HTabPane;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditorTextArea;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import com.hh.hhdb_admin.mgr.function.util.DebugUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class OrDebugForm extends DebugBaseForm {
    private static String logName = OrDebugForm.class.getSimpleName();

    private HButton xyddBut, xyhBut, enterBut, stopBut,tsBut,restartBut;
    private HTabPane hTabPane;

    private Map<String, QueryEditorTextArea> editorMap;     //编辑器集合
    private Map<String, Highlighter> highMap;               //高亮显示对象集合
    private Map<String, Map<Integer, int[]>> linMap;        //行号sql对应关系集合 key：编辑面板对象名称  map：函数内容与行号位置对应关系,大小代表函数行数
    private Map<String, List<Integer>> pointMap;            //断点集合

    private String debugTitle = "Script";                   //当前调试对象名称,默认Script
    private String titleAt = "Script";                      //当前选择的Tab页名称,默认Script

    private List<String> resList = new LinkedList<>();      //调试返回值和输出参数名称集合
    private List<Map<String,String>> stackList = new LinkedList<>();  //保存所有出现过的对象信息集合

    private String titleSql = "declare \n" +
            "  -- Local variables here\n" +
            "  i integer;\n" +
            "begin\n" +
            "  -- Test statements here\n" +
            "  \n" +
            "end;";

    /**
     * 初始化函数调试页面
     *
     * @param treeNode
     * @param jdbcBean
     * @param sql
     * @param pars
     */
    public OrDebugForm(TreeMrNode treeNode, JdbcBean jdbcBean, String sql, List<Map<String, String>> pars) {
        super(treeNode, jdbcBean, pars);
        if (DebugUtil.funVerify(jdbcBean,treeNode.getName(),treeNode.getType().name())) {
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), treeNode.getName() + "无效");
            return;
        }
        startDebug(sql);
    }

    /**
     * 初始化调试面板
     *
     * @param jdbcBean
     * @param pars
     */
    public OrDebugForm(JdbcBean jdbcBean,String sql) {
        super(jdbcBean);
        editorMap.get(debugTitle).setText(StringUtils.isNotEmpty(sql) ? sql : titleSql);
        editorMap.get(debugTitle).getTextArea().setEditable(true);
        editorMap.get(debugTitle).hTextArea.showBookMask(false);
        restartBut.setEnabled(false);
        finish();
        show();
    }

    /**
     *  启动调试程序
     * @param sql
     */
    private void startDebug(String sql){
        try {
            debug = new DebugTool(jdbcBean, null == treeNode ? null : treeNode.getId());
            debug.runProc(sql);

            Map<Integer, int[]> map = new LinkedHashMap<>();
            String str = editText(debug.getSource(),map);
            linMap.put(debugTitle,map);
            editorMap.get(debugTitle).setText(str);
            currentline = ((OraDebug) debug.getDebug()).stepInto();

            if (null == treeNode) {
                editorMap.get(debugTitle).getTextArea().setEditable(false);
                editorMap.get(debugTitle).hTextArea.showBookMask(true);
                xyhBut.setEnabled(true);
                stopBut.setEnabled(true);
                xyddBut.setEnabled(true);
                enterBut.setEnabled(true);
                tsBut.setEnabled(false);
                restartBut.setEnabled(true);
            } else {
                resList = DebugUtil.getOutPara(treeNode,jdbcBean);
                if (treeNode.getType() == TreeMrType.FUNCTION) resList.add("RESULT");
                show();
            }
            getCurrentLine();
        } catch (Exception e) {
            e.printStackTrace();
            if (null != debug) debug.close();
            debug = null;

            if (e instanceof SQLException) return;
            JOptionPane.showMessageDialog(null,StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : FunctionMgr.getLang("invalid"), FunctionMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
            logUtil.error(logName, e);
        }
    }

    @Override
    protected HBarPanel getHBarPanel() {
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel toolBarPane = new HBarPanel(l);
        //调试
        if (null == treeNode) {
            tsBut = new HButton(FunctionMgr.getLang("debug")) {
                @Override
                public void onClick() {
                    String sql = editorMap.get(debugTitle).getText();
                    if (StringUtils.isBlank(sql) || sql.equals(titleSql)) {
                        JOptionPane.showMessageDialog(null,"请输入正确语句！", FunctionMgr.getLang("hint"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    startDebug(sql);
                }
            };
            tsBut.setIcon(FunctionMgr.getIcon("formatsql"));
            //重新开始
            restartBut = new HButton("重新开始") {
                @Override
                public void onClick() {
                    try {
                        if (currentline != -1 && currentline != 0) debug.stop();
                        finish();
                        dialog.dispose();
                        new OrDebugForm(jdbcBean,editorMap.get("Script").getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                        logUtil.error(logName, e);
                        JOptionPane.showMessageDialog(null, e.getMessage(), FunctionMgr.getLang("hint"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            restartBut.setIcon(FunctionMgr.getIcon("reflash"));
            toolBarPane.add(tsBut,restartBut);
        }
        //下一行
        xyhBut = new HButton(FunctionMgr.getLang("xyh")) {
            @Override
            public void onClick() {
                run("row");
            }
        };
        xyhBut.setIcon(FunctionMgr.getIcon("dnext"));
        //下一断点
        xyddBut = new HButton(FunctionMgr.getLang("xydd")) {
            @Override
            public void onClick() {
                run("dot");
            }
        };
        xyddBut.setIcon(FunctionMgr.getIcon("debugstart"));
        //进入
        enterBut = new HButton(FunctionMgr.getLang("enter")) {
            @Override
            public void onClick() {
                run("enter");
            }
        };
        enterBut.setIcon(FunctionMgr.getIcon("load"));
        //停止
        stopBut = new HButton(FunctionMgr.getLang("stop")) {
            @Override
            public void onClick() {
                try {
                    if (currentline != -1 && currentline != 0) debug.stop();
                    if (null == treeNode) editorMap.get(debugTitle).getTextArea().setEditable(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    logUtil.error(logName, e);
                    JOptionPane.showMessageDialog(null, e.getMessage(), FunctionMgr.getLang("hint"), JOptionPane.ERROR_MESSAGE);
                } finally {
                    finish();
                }
            }
        };
        stopBut.setIcon(FunctionMgr.getIcon("debugstop"));
        toolBarPane.add(xyhBut, xyddBut, enterBut, stopBut);

        return toolBarPane;
    }

    @Override
    protected HSplitPanel getHSplitPanel() {
        editorMap = new LinkedHashMap<>();
        highMap = new LinkedHashMap<>();
        linMap  = new LinkedHashMap<>();
        pointMap = new LinkedHashMap<>();
        hTabPane = new HTabPane() {
            @Override
            public void onTabChange(String id) {
                JTabbedPane tab = (JTabbedPane)getComp();
                titleAt = tab.getTitleAt(tab.getSelectedIndex());
            }
        };
        debugTitle = "Script";

        addTextArea(debugTitle);

        LastPanel lastPanel = new LastPanel(false);
        lastPanel.set(hTabPane.getComp());

        HSplitPanel splitPane = new HSplitPanel(false);
        splitPane.setSplitWeight(0.5);
        JSplitPane jsp = splitPane.getComp();
        jsp.setLeftComponent(lastPanel.getComp());
        jsp.setRightComponent(dparameter.lastPanel.getComp());
        return splitPane;
    }

    /**
     * 获取当前堆栈信息
     * @throws Exception
     */
    private void getCurrentLine() throws Exception {
        if (currentline != -1) {
            List<Map<String, String>> list = new LinkedList<>();

            String val = ((OraDebug) debug.getDebug()).showStack();
            List<Map<String,String>> vl = DebugUtil.stackAnalysis(val);
            for (int i = 0; i < vl.size(); i++) {
                Map<String, String> map = new HashMap<>();
                map.put("row", i+1+"");
                map.put("value", vl.get(i).toString().replace("{","").replace("}",""));
                list.add(map);
            }

            dparameter.setTabStack(list);

            //设置高亮显示调试行
            highMap.get(debugTitle).removeAllHighlights();
            int[] attr = linMap.get(debugTitle).get(currentline);
            if (attr != null && attr.length > 0) {
                highMap.get(debugTitle).addHighlight(attr[0], attr[1], new DefaultHighlighter.DefaultHighlightPainter(new Color(172, 192, 253)));
            }
        }
    }

    /**
     * 添加编辑面板
     */
    private void addTextArea(String name) {
        QueryEditorTextArea qed = new QueryEditorTextArea(false) {
            @Override
            synchronized public void bookmarksAc() {
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
                            if (!reduce1.isEmpty()) ((OraDebug) debug.getDebug()).rmBreakPoints(titleAt,reduce1.get(0));

                            List<Integer> reduce2 = list.stream().filter(item -> !pointMap.get(titleAt).contains(item)).collect(Collectors.toList());
                            if (!reduce2.isEmpty()) {
                                String type = map.get("type");
                                String ns =  type.contains("PACKAGE") ?  "PACKAGE_BODY" : "TOP_LEVEL";
                                ((OraDebug) debug.getDebug()).setBreakPoints(reduce2,titleAt,jdbcBean.getUser(),type,ns);
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
                    JOptionPane.showMessageDialog(null, FunctionMgr.getLang("Errorbreakpoint"), FunctionMgr.getLang("hint"), JOptionPane.ERROR_MESSAGE);
                } finally {
                    if (null != debug) pointMap.put(titleAt,getbookmaskLines());
                }
            }
        };
        qed.getTextArea().setRows(15);
        qed.hTextArea.showBookMask(true);

        hTabPane.addPanel(name, name, qed.getComp(), false);
        editorMap.put(name, qed);
        highMap.put(name, qed.getTextArea().getHighlighter());
        pointMap.put(name,new LinkedList<>());
    }

    /**
     * 执行操作
     *
     * @param bool
     */
    synchronized private void run(String type) {
        if (currentline == -1) return;
        try {
            if (type.equals("dot")) {
                currentline = debug.contineGo();
            } else if (type.equals("row")) {
                currentline = debug.stepOver();
            } else {
                currentline = ((OraDebug) debug.getDebug()).stepInto();
            }
            Thread.sleep(300);

            if (currentline == 0) {    //当返回为0时表示调试结束
                finish();
            } else {
                List<Map<String,String>> vl = DebugUtil.stackAnalysis(((OraDebug) debug.getDebug()).showStack());
                String name  = vl.get(vl.size() - 1).get("name");
                debugTitle = StringUtils.isNotBlank(name) ? name:"Script";
                if (!titleAt.equals(debugTitle))  highMap.get(titleAt).removeAllHighlights();  //清除编辑器高亮显示


                if (type.equals("enter") && !editorMap.containsKey(debugTitle)) {
                    //新建一个编辑页面
                    addTextArea(debugTitle);
                    //获取进入对象的sql在编辑页面显示
                    Map<Integer, int[]> map = new LinkedHashMap<>();
                    String sql = DebugUtil.getCreateSql(jdbcBean, debugTitle, null != treeNode ? treeNode.getSchemaName() : jdbcBean.getSchema());
                    sql = editText(sql,map);
                    linMap.put(debugTitle,map);
                    editorMap.get(debugTitle).setText(sql);

                    stackList = vl;
                }

                getCurrentLine();
                dparameter.setTabVariables();
                dparameter.setTabResult(resList);
            }
            hTabPane.selectPanel(debugTitle);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), FunctionMgr.getLang("hint"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 调试结束后续操作
     */
    private void finish() {
        //清除编辑器高亮显示
        highMap.get(debugTitle).removeAllHighlights();
        currentline = -1;
        if (null != debug) debug.close();
        debug = null;
        //禁用按钮
        xyhBut.setEnabled(false);
        stopBut.setEnabled(false);
        xyddBut.setEnabled(false);
        enterBut.setEnabled(false);
    }
}
