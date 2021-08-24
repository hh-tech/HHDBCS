package com.hh.hhdb_admin.mgr.function.ui.deBug_from;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.function.debug.DebugTool;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.swingui.view.container.*;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;

import java.util.List;
import java.util.Map;

public abstract class DebugBaseForm extends LastPanel {
    protected JdbcBean jdbcBean;
    protected DBTypeEnum dbType;
    protected DebugTool debug;
    protected TreeMrNode treeNode;
    
    protected HDialog dialog;
    protected DebugTab dparameter;            //调试结果集选项卡面板
    
    protected int currentline = 0;            //当前行
    
    /**
     * 初始化函数调试面板
     * @param treeNode
     * @param jdbcBean
     * @param pars
     */
    public DebugBaseForm(TreeMrNode treeNode,JdbcBean jdbcBean, List<Map<String, String>> pars) {
        super(false);
        this.jdbcBean = jdbcBean;
        this.treeNode = treeNode;
        dbType = DriverUtil.getDbType(jdbcBean);
        dparameter = new DebugTab(this);
        dparameter.setTabParas(pars);
        
        setHead(getHBarPanel().getComp());
        set(getHSplitPanel().getComp());
    }
    
    /**
     * 初始化调试面板
     * @param jdbcBean
     */
    public DebugBaseForm(JdbcBean jdbcBean) {
        super(false);
        this.jdbcBean = jdbcBean;
        dbType = DriverUtil.getDbType(jdbcBean);
        dparameter = new DebugTab(this);
        
        setHead(getHBarPanel().getComp());
        set(getHSplitPanel().getComp());
    }
    
    protected void show() {
        dialog = new HDialog(StartUtil.parentFrame,1000, 800) {
            @Override
            protected void closeEvent() {
                try {
                    if (null != debug) debug.close();
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
    protected abstract HBarPanel getHBarPanel();
    
    /**
     * 获取分割面板
     * @return
     */
    protected abstract HSplitPanel getHSplitPanel();
}