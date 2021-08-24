package com.hh.hhdb_admin.mgr.function.ui.deBug_from;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.function.debug.DebugTool;
import com.hh.frame.create_dbobj.function.debug.HHdbDebug;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditorTextArea;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import org.hhdbsql.util.HHSQLException;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class HHDebugForm extends DebugBaseForm {
    private static String logName = HHDebugForm.class.getSimpleName();
    
    private HButton xyddBut, xyhBut, stopBut;
    private QueryEditorTextArea qed;        //编辑器
    
    private Highlighter highLighter;        //高亮显示对象
    private Map<Integer, int[]> lineattr = new HashMap<Integer, int[]>();    //函数内容与行号位置对应关系,大小代表函数行数
    private List<Integer> pointList = new ArrayList<>();                //断点集合
    
    /**
     * 初始化调试页面
     *
     * @param treeNode
     * @param jdbcBean
     * @param sql
     * @param pars
     */
    public HHDebugForm(TreeMrNode treeNode, JdbcBean jdbcBean, String sql, List<Map<String, String>> pars) {
        super(treeNode, jdbcBean, pars);
        try {
            //启动函数调试
            debug = new DebugTool(jdbcBean, treeNode.getId());
            if (treeNode.getType().name().equals("FUNCTION")) {
                debug.runProc("select " + sql + ";");
            } else {
                debug.runProc("CALL " + sql + ";");
            }
            
            qed.setText(editText(debug.getSource(), lineattr));
            
            getCurrentLine();
            show();
        } catch (Exception e) {
            e.printStackTrace();
            if (null != debug) debug.close();
            debug = null;
            
            if (e instanceof SQLException) return;
            JOptionPane.showMessageDialog(null, e.getMessage(), FunctionMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
            logUtil.error(logName, e);
        }
    }
    
    @Override
    protected HBarPanel getHBarPanel() {
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel toolBarPane = new HBarPanel(l);
        //下一断点
        xyddBut = new HButton(FunctionMgr.getLang("xydd")) {
            @Override
            public void onClick() {
                run(false);
            }
        };
        xyddBut.setIcon(FunctionMgr.getIcon("debugstart"));
        //下一行
        xyhBut = new HButton(FunctionMgr.getLang("xyh")) {
            @Override
            public void onClick() {
                run(true);
            }
        };
        xyhBut.setIcon(FunctionMgr.getIcon("dnext"));
        //停止
        stopBut = new HButton(FunctionMgr.getLang("stop")) {
            @Override
            public void onClick() {
                try {
                    if (currentline != -1 && currentline != 0) debug.stop();
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
        toolBarPane.add(xyddBut, xyhBut, stopBut);
        
        return toolBarPane;
    }
    
    @Override
    protected HSplitPanel getHSplitPanel() {
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
                    JOptionPane.showMessageDialog(null, FunctionMgr.getLang("Errorbreakpoint"), FunctionMgr.getLang("hint"), JOptionPane.ERROR_MESSAGE);
                } finally {
                    if (null != debug) pointList = qed.getbookmaskLines();
                }
            }
        };
        qed.hTextArea.showBookMask(true);
        //给绘色组件添加对象
        highLighter = qed.getTextArea().getHighlighter();
        
        HSplitPanel splitPane = new HSplitPanel(false);
        splitPane.setPanelOne(qed);
        splitPane.setSplitWeight(0.5);
        
        JSplitPane jsp = splitPane.getComp();
        jsp.setRightComponent(dparameter.lastPanel.getComp());
        
        return splitPane;
    }
    
    /**
     * 执行操作
     *
     * @param bool
     */
    synchronized private void run(boolean bool) {
        try {
            if (currentline == -1) return;
            new Thread(() -> {
                try {
                    System.out.println(bool ? debug.stepOver() : debug.contineGo());
                } catch (HHSQLException e) {
                    System.out.println("调试完成，关闭调试连接");
                } catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage(), FunctionMgr.getLang("hint"), JOptionPane.ERROR_MESSAGE);
                }
            }).start();
            Thread.sleep(300);
            
            dparameter.setTabMsg(((HHdbDebug) debug.getDebug()).getInfo());
            if (((HHdbDebug) debug.getDebug()).isEnd) {
				finish();
            } else {
				getCurrentLine();
                dparameter.setTabVariables();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), FunctionMgr.getLang("hint"), JOptionPane.ERROR_MESSAGE);
        }
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
    }
}
