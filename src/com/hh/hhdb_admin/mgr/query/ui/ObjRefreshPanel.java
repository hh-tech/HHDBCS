package com.hh.hhdb_admin.mgr.query.ui;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.dbobj2.kword.KeyWordUtil;
import com.hh.frame.json.JsonArray;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.util.ImgUtil;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.query.QueryMgr;

import javax.swing.*;
import java.io.File;
import java.sql.Connection;
import java.util.*;

/**
 * 对象刷新页面
 *
 * @author hexu
 */
public class ObjRefreshPanel {
    private HDialog dialog;
    
    private JdbcBean jdbc;
    
    private HButton refBut,refreshbtn;
    private HTable objTable;
    
    private boolean isEnd = false;      //是否刷新完成
    private JsonArray jsonValues;
    
    /**
     * 对象刷新
     * @param refBut
     * @param jdbc
     * @param schame
     */
    public ObjRefreshPanel(HButton refBut, JdbcBean jdbc) {
        this.refBut = refBut;
        this.jdbc = jdbc;
        dialog = new HDialog(StartUtil.parentFrame,800, 600){
            @Override
            protected void closeEvent() {
                if (isEnd) refBut.setIcon(QueryMgr.getIcon("refresh"));
                dialog.dispose();
            }
        };
        dialog.setRootPanel(init());
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setWindowTitle(QueryMgr.getLang("objectRefresh"));
        
        refresh();
    }
    
    public void show(){
        if (isEnd) dialog.show();
    }
    
    /**
     * 刷新
     */
    private void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection conns = null;
                isEnd = false;
                try {
                    conns = ConnUtil.getConn(jdbc);
                    refBut.setIcon(ImgUtil.readImgIcon(new File("etc\\icon\\query\\refresh2_16.gif")));
                    refreshbtn.setIcon(ImgUtil.readImgIcon(new File("etc\\icon\\query\\refresh2_16.gif")));
                    
                    //刷新内容显示
                    jsonValues = KeyWordUtil.getKeyWordJson(conns);
                    conns = ConnUtil.getConn(jdbc);
                    KeyWordUtil.getDbObjectJson(jsonValues,conns, jdbc.getSchema(),"table"); //先设置表提示
                    KeyWordUtil.getDbObjectJson(jsonValues,conns, jdbc.getSchema(),"view");
                    KeyWordUtil.getDbObjectJson(jsonValues,conns, jdbc.getSchema(),"function");
                    DBTypeEnum dbtype = DriverUtil.getDbType(conns);
                    if (dbtype == DBTypeEnum.oracle || dbtype == DBTypeEnum.dm) {
                        KeyWordUtil.getDbObjectJson(jsonValues,conns, jdbc.getSchema(),"synonym");
                    }
    
                    List<Map<String, String>> data = new LinkedList<>();
                    jsonValues.forEach(a-> {
                        String name = a.asObject().getString("caption");
                        String type = a.asObject().getString("meta");
                        if (!type.equals("reserve") && !type.equals("key") && !type.equals("sys_view") && !type.equals("user_view")) {
                            Map<String, String> map = new LinkedHashMap<>();
                            map.put("name",name);
                            map.put("type",type);
                            data.add(map);
                        }
                    });
//                    Thread.sleep(1000);
                    objTable.load(data, 1);
                    
                    refBut.setIcon(QueryMgr.getIcon("refresh3"));
                    refreshbtn.setIcon(QueryMgr.getIcon("refresh3"));
                    if (null != dialog && dialog.isVisible()) {
                        JOptionPane.showMessageDialog(StartUtil.parentFrame.getWindow(), QueryMgr.getLang("refresh—success"),QueryMgr.getLang("hint"), JOptionPane.INFORMATION_MESSAGE);
                    }
                    update(jsonValues);
                } catch (Exception e) {
                    e.printStackTrace();
                    jsonValues = new JsonArray();
                    refBut.setIcon(QueryMgr.getIcon("refresh"));
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(),e);
                } finally {
                    ConnUtil.close(conns);
                    isEnd = true;
                }
            }
        }).start();
    }
    
    private HPanel init() {
        objTable = new HTable();
        DataCol nameCol = new DataCol("name", QueryMgr.getLang("name"));
        nameCol.setCellEditable(false);
        objTable.addCols(nameCol);
        DataCol typeCol = new DataCol("type", QueryMgr.getLang("type"));
        typeCol.setCellEditable(false);
        objTable.addCols(typeCol);
    
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setHead(new SearchToolBar(objTable).getComp());
        lastPanel.setWithScroll(objTable.getComp());
    
        LastPanel las = new LastPanel(false);
        las.set(lastPanel.getComp());
        las.setHead(initHButton().getComp());
        objTable.load(new ArrayList<>(), 1);
    
        HDivLayout hdiv = new HDivLayout();
        hdiv.setBottomHeight(15);
        HPanel hPanel = new HPanel(hdiv);
        hPanel.setLastPanel(las);
        return hPanel;
    }
    
    private HBarPanel initHButton() {
        //关闭
        HButton closebtn = new HButton(QueryMgr.getLang("close")) {
            @Override
            public void onClick() {
                if (isEnd) {
                    refBut.setIcon(QueryMgr.getIcon("refresh"));
                    refreshbtn.setIcon(QueryMgr.getIcon("refresh"));
                }
                dialog.dispose();
            }
        };
        closebtn.setIcon(QueryMgr.getIcon("cancel"));
        //刷新
        refreshbtn = new HButton(QueryMgr.getLang("objectRefresh")) {
            @Override
            public void onClick() {
                if (isEnd) refresh();
            }
        };
        refreshbtn.setIcon(QueryMgr.getIcon("refresh"));
    
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel toolBarPane = new HBarPanel(l);
        toolBarPane.add(closebtn,refreshbtn);
        return toolBarPane;
    }
    
    protected void update(JsonArray jsonValues) {
    
    }
}
