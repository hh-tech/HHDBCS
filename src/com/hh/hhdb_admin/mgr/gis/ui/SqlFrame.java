package com.hh.hhdb_admin.mgr.gis.ui;


import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.textEditor.base.ConstantsEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.gis.GisMgr;
import com.hh.hhdb_admin.mgr.gis.util.CreateGeometry;
import com.hh.hhdb_admin.mgr.gis.util.GisUtil;
import com.vividsolutions.jts.geom.Geometry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * sql查询弹出面板
 */
public class SqlFrame {
    private final JdbcBean jdbcBean;

    private final HDialog dlog;
    private final HPanel backgPanel = new HPanel();

    private static Color backcolor = Color.YELLOW;
    private final HTextArea textArea;
    private final MapContentPanel mc;

    public SqlFrame(MapContentPanel mc, JdbcBean jdbcBean) {
        this.jdbcBean = jdbcBean;
        this.mc = mc;

        backgPanel.setBackground(backcolor);
        backgPanel.getComp().setBorder(BorderFactory.createEtchedBorder());
        backgPanel.getComp().setPreferredSize(new Dimension(20, 20));

        textArea = new HTextArea(false, true);
        textArea.showBookMask(false);
        textArea.setConstants(ConstantsEnum.SYNTAX_STYLE_NONE);

        backgPanel.getComp().addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                backcolor = JColorChooser.showDialog(null, GisMgr.getLang("setColors"), backcolor);
                backgPanel.setBackground(backcolor);
            }
        });

        HPanel panel = new HPanel();
        panel.add(textArea);
        HPanel hPanel = new HPanel(new HDivLayout(GridSplitEnum.C3, GridSplitEnum.C1));
        hPanel.add(new LabelInput(GisMgr.getLang("markerColor")));
        hPanel.add(backgPanel);
        panel.add(hPanel);

        dlog = StartUtil.getMainDialog();
        dlog.setSize(600, 550);
        dlog.setIconImage(GisMgr.getIcon("sql").getImage());
        dlog.setWindowTitle(GisMgr.getLang("execSQL"));
        dlog.setToolBar(initToolBar());
        dlog.setRootPanel(panel);
        dlog.show();
    }

    private HBarPanel initToolBar() {
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel hTool = new HBarPanel(l);

        //导入shp文件
        HButton input = new HButton(GisMgr.getLang("execute")) {
            @Override
            public void onClick() {
                Connection conn = null;
                try {
                    conn = ConnUtil.getConn(jdbcBean);
                    String sqlstr = textArea.getArea().getTextArea().getText().trim();
                    String tableName;
                    //截取表名
                    int i = sqlstr.indexOf(" from ");
                    int x = sqlstr.indexOf(" where ");
                    if (x > 0) {
                        tableName = sqlstr.substring(i + 6, x);
                    } else {
                        tableName = sqlstr.substring(i + 6);
                    }

                    ArrayList<Geometry> geoarr = new ArrayList<Geometry>();
                    ArrayList<String> arr = GisUtil.selectSql(conn, sqlstr);
                    for (Object str : arr) {
                        if (str.toString().contains("POINT") && !str.toString().contains("MULTIPOINT") && !str.toString().contains("GEOMETRYCOLLECTION")) {
                            geoarr.add(CreateGeometry.createPointByWKT(str.toString()));
                        } else if (str.toString().contains("MULTIPOINT") && !str.toString().contains("GEOMETRYCOLLECTION")) {
                            geoarr.add(CreateGeometry.createMulPointByWKT(str.toString()));
                        } else if (str.toString().contains("LINESTRING") && !str.toString().contains("MULTILINESTRING") && !str.toString().contains("GEOMETRYCOLLECTION")) {
                            geoarr.add(CreateGeometry.createLineByWKT(str.toString()));
                        } else if (str.toString().contains("POLYGON") && !str.toString().contains("MULTIPOLYGON") && !str.toString().contains("GEOMETRYCOLLECTION")) {
                            geoarr.add(CreateGeometry.createPolygonByWKT(str.toString()));
                        } else if (str.toString().contains("MULTILINESTRING") && !str.toString().contains("GEOMETRYCOLLECTION")) {
                            geoarr.add(CreateGeometry.createMultiLineByWKT(str.toString()));
                        } else if (str.toString().contains("MULTIPOLYGON") && !str.toString().contains("GEOMETRYCOLLECTION")) {
                            geoarr.add(CreateGeometry.createMulPolygonByWKT(str.toString()));
                        } else if (str.toString().contains("GEOMETRYCOLLECTION")) {
                            geoarr.add(CreateGeometry.createGeometryCollectionByWKT(str.toString()));
                        }
                    }
                    mc.selectFeatures(geoarr, tableName, backgPanel.getComp().getBackground());
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, e.getMessage(), GisMgr.getLang("error"), JOptionPane.WARNING_MESSAGE);
                }finally {
                    ConnUtil.close(conn);
                    dlog.hide();
                }
            }
        };
        input.setIcon(GisMgr.getIcon("export"));
        hTool.add(input);

        return hTool;
    }

}
