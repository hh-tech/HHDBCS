package com.hh.hhdb_admin.mgr.function.ui.deBug_from;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.function.debug.DebugTool;
import com.hh.frame.create_dbobj.function.debug.HHdbDebug;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditorTextArea;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import org.hhdbsql.util.HHSQLException;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class HHDebugForm extends DebugBaseForm {
    private Highlighter highLighter;        //高亮显示对象
    private Map<Integer, int[]> lineattr;    //函数内容与行号位置对应关系,大小代表函数行数
    private List<Integer> pointList;                //断点集合
    
    private String debugSql;       //调试原始sql
    
    /**
     * 初始化调试页面
     * @param jdbcBean
     */
    public HHDebugForm(AbsFunMr funMr, JdbcBean jdbcBean) {
        super(funMr, jdbcBean);
        try {
            qed.setText(debugSql = getSql(new HashMap<>()));
            xyhBut.setEnabled(false);
            stopBut.setEnabled(false);
            xyddBut.setEnabled(false);
            show();
        } catch (Exception e) {
            e.printStackTrace();
            if (null != debug) debug.close();
            debug = null;
            
            if (e instanceof SQLException) return;
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),e.getMessage());
        }
    }
    
    @Override
    protected HSplitPanel getHSplitPanel() {
        lineattr = new LinkedHashMap<>();
        pointList = new ArrayList<>();
        qed = new QueryEditorTextArea(false) {
            @Override
            synchronized public void bookmarksAc() {
                List<Integer> list = qed.getbookmaskLines();
                //添加或取消断点
                try {
                    if (null != debug) {
                        if (pointList.isEmpty()) {
                            debug.setBreakPoints(list);
                        } else {
                            List<Integer> reduce1 = pointList.stream().filter(item -> !list.contains(item)).collect(Collectors.toList());
                            if (!reduce1.isEmpty()) debug.rmBreakPoints(reduce1);
                            List<Integer> reduce2 = list.stream().filter(item -> !pointList.contains(item)).collect(Collectors.toList());
                            if (!reduce2.isEmpty()) debug.setBreakPoints(reduce2);
                        }
                    }
                } catch (Exception e) {
                    try {
                        //删除错误的书签
                        List<Integer> reduce2 = list.stream().filter(item -> !pointList.contains(item)).collect(Collectors.toList());
                        for (Integer integer : reduce2) {
                            hTextArea.getArea().getScrollPane().getGutter().toggleBookmark(Math.max((integer - 1), 0));
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(),FunctionMgr.getLang("Errorbreakpoint"));
                } finally {
                    if (null != debug) pointList = qed.getbookmaskLines();
                }
            }
        };
        qed.getTextArea().setRows(15);
        qed.hTextArea.showBookMask(true);
        //给绘色组件添加对象
        highLighter = qed.getTextArea().getHighlighter();
        
        HSplitPanel splitPane = new HSplitPanel(false);
        splitPane.setPanelOne(qed);
        splitPane.setSplitWeight(0.5);
        return splitPane;
    }
    
    @Override
    protected void startDebug() {
        try {
            //初始化页面信息
            if (currentline == -1) {
                //从新开始
                finish();
                currentline = 0;
                lineattr = new HashMap<Integer, int[]>();
                pointList = new ArrayList<>();
            }
            //删除编辑器上所有书签
            qed.hTextArea.getArea().getScrollPane().getGutter().removeAllTrackingIcons();
            dparameter.setParamEdit(false);
            xyhBut.setEnabled(true);
            stopBut.setEnabled(true);
            xyddBut.setEnabled(true);
            tsBut.setEnabled(false);
        
            //启动函数调试
            debug = new DebugTool(jdbcBean, funMr.treeNode.getId());
            debug.runProc(debugSql);
            qed.setText(editText(debug.getSource(), lineattr));
            getCurrentLine();
        }catch (Exception e){
            e.printStackTrace();
            finish();
            if (e instanceof SQLException) return;
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),e.getMessage());
        }
    }
    
    @Override
    protected void runDebug(String type) {
        try {
            if (currentline == -1) return;
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
                    }
                } catch (HHSQLException e) {
                    System.out.println("调试完成，关闭调试连接");
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(),e.getMessage());
                }
            }).start();
            Thread.sleep(300);
        
            //显示调试信息
            StringBuffer str = new StringBuffer();
            Map<String,String> map = ((HHdbDebug) debug.getDebug()).getInfo(str);
            dparameter.setTabMsg(str.toString());
            if (!map.isEmpty()) dparameter.setTabResult(map);
            
            if (((HHdbDebug) debug.getDebug()).isEnd) {
                finish();
            } else {
                getCurrentLine();
                dparameter.setTabVariables();
            }
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),e.getMessage());
        }
    }
    
    @Override
    protected String getSql(Map<String, List<String>> valMap) throws Exception {
        StringBuffer sql = new StringBuffer();
        valMap.keySet().forEach(a -> {
            List<String> li = valMap.get(a);
            sql.append(sql.length()>0 ? "," : "").append(li.get(1) + "::" + li.get(0));
        });
        sql.append(");");
        String st = funMr.treeNode.getType() == TreeMrType.FUNCTION ? "select " : "CALL ";
        debugSql = st + "\"" + funMr.treeNode.getSchemaName() + "\".\"" + funMr.treeNode.getName() + "\"(" + sql.toString();
        return debugSql;
    }
	
	/**
	 * 获取当前堆栈信息
	 *
	 * @throws Exception
	 */
	private void getCurrentLine() throws Exception {
		if (currentline != -1) {
			List<Map<String, String>> list = new LinkedList<>();
			
			list = debug.getStack();
			currentline = Integer.parseInt(list.get(0).get("row"));
			dparameter.setTabStack(list);
			
			//设置高亮显示调试行
			highLighter.removeAllHighlights();
			int[] attr = lineattr.get(currentline);
			if (attr != null && attr.length > 0) {
				highLighter.addHighlight(attr[0], attr[1], new DefaultHighlighter.DefaultHighlightPainter(new Color(172, 192, 253)));
			}
		}
	}
    
    /**
     * 调试结束后续操作
     */
    private void finish() {
        highLighter.removeAllHighlights();
        currentline = -1;
        if (null != debug) debug.close();
        debug = null;
        xyhBut.setEnabled(false);
        stopBut.setEnabled(false);
        xyddBut.setEnabled(false);
        tsBut.setEnabled(true);
        dparameter.setParamEdit(true);
    }
}
