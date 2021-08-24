package com.hh.hhdb_admin.mgr.gis;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.LM;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.gis.ui.MapContentPanel;
import com.hh.hhdb_admin.mgr.gis.ui.MapInset;
import com.hh.hhdb_admin.mgr.gis.util.GisUtil;
import com.hh.hhdb_admin.mgr.gis.util.ImpGis;
import org.geotools.swing.MapLayerTable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.sql.Connection;

public class GisComp extends AbsHComp {
    private static String logName = GisComp.class.getSimpleName();
    private Connection conn;
    private JdbcBean jdbcBean;
    private LastPanel lastPanel = new LastPanel(false);

    //左控制区域
    private LastPanel leftjp;
    //地图内容
    private MapContentPanel mapcontent;
    //图层列表面板
    private MapLayerTable maplayertable;
    
    private GisComp gismap;

    
    public GisComp(JdbcBean jdbcBean) throws Exception {
        this.jdbcBean = jdbcBean;
        this.conn = ConnUtil.getConn(jdbcBean);
    
        DBTypeEnum dbType = DriverUtil.getDbType(jdbcBean);
        if (dbType.equals(DBTypeEnum.hhdb) || dbType.equals(DBTypeEnum.pgsql)) {
            String str = DriverUtil.getDbType(conn).equals(DBTypeEnum.hhdb) ? "hh" : "pg";
            //从mapcontent类248行移出来的方法
            //此方法用户Pg数据库添加某些图层时出现的function "st_estimated_extent" statement 2 异常
            if (str.equals("pg")) {
                String sql = "create or replace function st_estimated_extent(text,text) "+
                        "returns box2d as 'select null::box2d' language sql;";
                String sql2 =" create or replace function st_estimated_extent(text,text,text)"+
                        " returns box2d as 'select null::box2d' language sql;";
                SqlExeUtil.executeUpdate(conn, sql);
                SqlExeUtil.executeUpdate(conn, sql2);
            }
        }
        
        lastPanel.set(initLeft().getComp());
        comp = lastPanel.getComp();
        gismap = this;
    }

    /**
     * 添加图层
     * @param str
     */
    public void addLayer(String str) {
        try {
            mapcontent.addLayer(str);
            maplayertable.repaint();
            //设置图层样式弹出窗
            //临时设置小字体,用于解决gis自带弹出窗口面板上字体过大，导致按钮挤出显示区域问题
            UIManager.put("Label.font", new Font("微软雅黑", Font.PLAIN, 12));
            UIManager.setLookAndFeel(HHSwingUi.getBeautyEyeLNFStrWindowsPlatform());
        } catch (Exception e) {
            LM.error(logName, e);
            JOptionPane.showMessageDialog(null, e, GisMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 右侧图层刷新，左侧的图层集合也要刷新
     */
    public void reload(MapLayerTable maplayertable) {
        ((JPanel)leftjp.getComp()).remove(1);
        this.maplayertable = maplayertable;
        maplayertable.setSize(240, 350);
        leftjp.set(maplayertable);
        leftjp.updateUI();
    }

    public LastPanel getLastPanel() {
        return lastPanel;
    }

    /**
     * 关闭数据连接，关闭插件时调用	
     */
    public void closeconn(){
        mapcontent.closedatastore();
    }
    
    private HSplitPanel initLeft() throws Exception {
        mapcontent = new MapContentPanel(jdbcBean, this);
        maplayertable = mapcontent.addMapLayerTable();

        HSplitPanel splitPane = new HSplitPanel(true);
        splitPane.setSplitWeight(0.35);
        
        //左边信息区域
        leftjp = new LastPanel();
        leftjp.setHead(initToolBar().getComp());    //工具栏
        leftjp.set(maplayertable);                  //图层显示区域
        splitPane.setLastComp4One(leftjp);
        //右图层区域 gis图片区域
        splitPane.setLastComp4Two(mapcontent);
        return splitPane;
    }
    
    private HBarPanel initToolBar()throws Exception {
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel hTool = new HBarPanel(l);

        //导入shp文件
        HButton input = new HButton(GisMgr.getLang("impshp")) {
            @Override
            public void onClick() {
                try {
                    GisUtil.getversion(conn);
                    new ImpGis(jdbcBean);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, GisMgr.getLang("noGIS"), GisMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        input.setIcon(GisMgr.getIcon("export"));
        hTool.add(input);
        //导出shp文件
        HButton output = new HButton(GisMgr.getLang("expshp")) {
            @Override
            public void onClick() {
                try {
                    new MapInset(jdbcBean,GisUtil.getTable(conn));
                } catch (Exception e) {
                    logUtil.error(logName, e);
                }
            }
        };
        output.setIcon(GisMgr.getIcon("import"));
        hTool.add(output);
        //添加图层
        HButton addmap = new HButton(GisMgr.getLang("AddLayer")) {
            @Override
            public void onClick() {
                try {
                    Object[] options = {GisMgr.getLang("AddDabs"), GisMgr.getLang("Addlocal")};
                    int m = JOptionPane.showOptionDialog(null, GisMgr.getLang("Selectpath"), GisMgr.getLang("AddLayer"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                    if (m == 0) {
                        DBTypeEnum dbType = DriverUtil.getDbType(jdbcBean);
                        if (dbType.equals(DBTypeEnum.hhdb) || dbType.equals(DBTypeEnum.pgsql)) {
                            new MapInset(jdbcBean,GisUtil.getTable(conn), gismap, mapcontent.getlayerlist());
                        }else {
                            JOptionPane.showMessageDialog(null,"当前数据库不支持添加！", GisMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (m == 1) {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setCurrentDirectory(chooser.getSelectedFile());
                        chooser.setMultiSelectionEnabled(true);
                        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);// 设置选择模式
                        FileNameExtensionFilter filter = new FileNameExtensionFilter("TIF&SHP", "tif", "shp");
                        chooser.setFileFilter(filter);
                        int resultFile = chooser.showSaveDialog(null);
                        if (resultFile == JFileChooser.APPROVE_OPTION) {
                            File[] files = chooser.getSelectedFiles();
                            for (File file : files) {
                                addLayer(file.toString());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, e, GisMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        addmap.setIcon(GisMgr.getIcon("addtrigger"));
        hTool.add(addmap);
        return hTool;
    }
}