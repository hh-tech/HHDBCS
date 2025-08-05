package com.hh.hhdb_admin.mgr.function.ui.deBug_from;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.ErrorUtil;
import com.hh.frame.create_dbobj.function.debug.AbsDebug;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.function.util.DebugUtil;
import org.fife.ui.rtextarea.GutterIconInfo;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class DebugBaseForm extends LastPanel {
    protected JdbcBean jdbcBean;
    protected DBTypeEnum dbType;
    protected AbsDebug debug;
    protected AbsFunMr funMr;
    protected HTextArea qed;          //主编辑器
    protected HDialog dialog;
    protected DebugTab dparameter;            //调试结果集选项卡面板
    protected HButton xyddBut, xyhBut, enterBut, stopBut,tsBut;
    
    protected Map<String, HTextArea> editorMap;               //编辑器集合
    protected Map<String, Map<Integer, int[]>> linMap;                  //行号sql对应关系集合 key：编辑面板对象名称  map：函数内容与行号位置对应关系,大小代表函数行数
    protected Map<String, List<Integer>> pointMap;                      //断点集合
    protected List<Map<String,String>> stackList = new LinkedList<>();  //栈堆信息集合
    protected String debugTitle = "Script";                             //当前调试对象名称,默认Script
    protected String titleAt = "Script";                                //当前选择的Tab页名称,默认Script
    
    protected int currentline = 0;            //当前行
    
    /**
     * 初始化函数调试面板
     * @param treeNode
     * @param jdbcBean
     * @param pars
     */
    public DebugBaseForm(AbsFunMr funMr,JdbcBean jdbcBean) {
        super(false);
        this.funMr = funMr;
        this.jdbcBean = jdbcBean;
        dbType = DriverUtil.getDbType(jdbcBean);
        dparameter = new DebugTab(this);
        JSplitPane jsp = getHSplitPanel().getComp();
        jsp.setRightComponent(dparameter.getComp());
        setHead(getHBarPanel().getComp());
        set(jsp);
    }
    
    /**
     * 获取分割面板
     * @return
     */
    protected abstract HSplitPanel getHSplitPanel();
    
    /**
     * 启动调试
     */
    protected abstract void startDebug();
    
    /**
     * 执行调试操作
     */
    protected abstract void runDebug(String type);
    
    /**
     * 获取调试sql
     * @param valMap    参数
     */
    protected abstract String getSql(Map<String, List<String>> valMap) throws Exception;
    
    protected void show() {
        dialog = new HDialog(CsVarUtil.parentFrame,1000, 800) {
            @Override
            protected void closeEvent() {
                try {
                    if (null != debug) {
                        debug.stop();
                        debug.close();
                    }
                    dispose();
                }catch (Exception e){
                    Lg.error(DebugBaseForm.class, e);
                }
            }
        };
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setWindowTitle(LangMgr.lang(Word2.DEBUG));
        HPanel hPanel = new HPanel();
        hPanel.setLastPanel(this);
        dialog.setRootPanel(hPanel);
        dialog.setStatusBar(new HBarPanel());
        dialog.show();
    }
    
    /**
     * 调整调试sql，记录行与内容
     */
    protected String editText(String text,Map<Integer, int[]> map) {
        String[] ss = text.split("\n");
        int js = 0;
        for (int i = 0; i < ss.length; i++) {
            int s = text.indexOf(ss[i], js);
            js = s + ss[i].length();
            int[] posi = {s, js};
            map.put((i + 1), posi);   //保存行号，以及起始结束位置
        }
        return ("A" + text).trim().substring(1);
    }
    
    /**
     * 获取按钮栏
     * @return
     */
    private HBarPanel getHBarPanel() {
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel toolBarPane = new HBarPanel(l);
        //调试
        tsBut = new HButton(LangMgr.lang(Word2.DEBUG)) {
            @Override
            public void onClick() {
                if (null == funMr) {
                    startDebug();
                } else {
                    if (dparameter.getParameter().getComp().isEditing()) dparameter.getParameter().getComp().getCellEditor().stopCellEditing();
                    if (DebugUtil.verify(dparameter.getParameter().getRowBeans(null),dialog,dbType)) {
                        startDebug();
                    }
                }
            }
        };
        tsBut.setIcon(FaSolidEnum.PLAY,16);
        //下一行
        xyhBut = new HButton(LangMgr.lang(Word3.NEXT_LINE)) {
            @Override
            public void onClick() {
                runDebug("row");
            }
        };
        xyhBut.setIcon(FaSolidEnum.ARROWS_DOWN_TO_LINE,16);
        //下一断点
        xyddBut = new HButton(LangMgr.sLang(CsL.NEXT_BREAK_POINT)) {
            @Override
            public void onClick() {
                runDebug("dot");
            }
        };
        xyddBut.setIcon(FaSolidEnum.ARROWS_TURN_TO_DOTS,16);
        //进入
        enterBut = new HButton(LangMgr.lang(Word2.ENTER)) {
            @Override
            public void onClick() {
                runDebug("into");
            }
        };
        enterBut.setIcon(FaSolidEnum.RIGHT_TO_BRACKET,16);
        //停止
        stopBut = new HButton(LangMgr.lang(Word2.STOP)) {
            @Override
            public void onClick() {
                runDebug("stop");
            }
        };
        stopBut.setIcon(FaSolidEnum.STOP,16);
        toolBarPane.add(tsBut,xyhBut, xyddBut, enterBut, stopBut);
        return toolBarPane;
    }
    
    /**
     * 获取所有标上书签的行
     * @return
     */
    protected List<Integer> getbookmaskLines(HTextArea area) {
        List<Integer> lines = new ArrayList<Integer>();
        for (GutterIconInfo info : area.getGutter().getBookmarks()) {
            try {
                lines.add(area.getRSyntaxTextArea().getLineOfOffset(info.getMarkedOffset()) + 1);
            } catch (BadLocationException e) {
                Lg.error(getClass(), ErrorUtil.getFull(e));
            }
        }
        return lines;
    }
}