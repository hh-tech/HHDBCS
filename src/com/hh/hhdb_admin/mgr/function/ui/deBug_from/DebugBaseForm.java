package com.hh.hhdb_admin.mgr.function.ui.deBug_from;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.function.debug.DebugTool;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditorTextArea;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import com.hh.hhdb_admin.mgr.function.util.DebugUtil;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public abstract class DebugBaseForm extends LastPanel {
    protected JdbcBean jdbcBean;
    protected DBTypeEnum dbType;
    protected DebugTool debug;
    protected AbsFunMr funMr;
    
    protected QueryEditorTextArea qed;          //主编辑器
    protected HDialog dialog;
    protected DebugTab dparameter;            //调试结果集选项卡面板
    protected HButton xyddBut, xyhBut, enterBut, stopBut,tsBut;
    
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
        if (null != funMr) dparameter.setTabParas(DebugUtil.getParam(funMr,jdbcBean));
    
        JSplitPane jsp = getHSplitPanel().getComp();
        jsp.setRightComponent(dparameter.getLastPanel().getComp());
        
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
        dialog = new HDialog(StartUtil.parentFrame,1000, 800) {
            @Override
            protected void closeEvent() {
                try {
                    if (null != debug) {
                        debug.stop();
                        debug.close();
                    }
                    dispose();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
        dialog.setWindowTitle(FunctionMgr.getLang("debug"));
        HPanel hPanel = new HPanel();
        hPanel.setLastPanel(this);
        dialog.setRootPanel(hPanel);
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
            int[] posi = new int[]{s, js};
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
        tsBut = new HButton(FunctionMgr.getLang("debug")) {
            @Override
            public void onClick() {
                startDebug();
            }
        };
        tsBut.setIcon(FunctionMgr.getIcon("formatsql"));
        //下一行
        xyhBut = new HButton(FunctionMgr.getLang("xyh")) {
            @Override
            public void onClick() {
                runDebug("row");
            }
        };
        xyhBut.setIcon(FunctionMgr.getIcon("dnext"));
        //下一断点
        xyddBut = new HButton(FunctionMgr.getLang("xydd")) {
            @Override
            public void onClick() {
                runDebug("dot");
            }
        };
        xyddBut.setIcon(FunctionMgr.getIcon("debugstart"));
        //进入
        enterBut = new HButton(FunctionMgr.getLang("enter")) {
            @Override
            public void onClick() {
                runDebug("enter");
            }
        };
        enterBut.setIcon(FunctionMgr.getIcon("load"));
        //停止
        stopBut = new HButton(FunctionMgr.getLang("stop")) {
            @Override
            public void onClick() {
                runDebug("stop");
            }
        };
        stopBut.setIcon(FunctionMgr.getIcon("debugstop"));
        if (dbType == DBTypeEnum.oracle) {
            toolBarPane.add(tsBut,xyhBut, xyddBut, enterBut, stopBut);
        } else {
            toolBarPane.add(tsBut,xyhBut, xyddBut, stopBut);
        }
        return toolBarPane;
    }
}