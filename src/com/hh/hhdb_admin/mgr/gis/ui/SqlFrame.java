package com.hh.hhdb_admin.mgr.gis.ui;


import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
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

        textArea = new HTextArea();
        HTAreaConf conf = new HTAreaConf();
        conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
        textArea.setConf(conf);

        backgPanel.getComp().addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                backcolor = JColorChooser.showDialog(null, LangMgr.lang(Word4.SET_COLORS), backcolor);
                backgPanel.setBackground(backcolor);
            }
        });

        HPanel panel = new HPanel();
        panel.add(textArea);
        HPanel hPanel = new HPanel(new HDivLayout(GridSplitEnum.C3, GridSplitEnum.C1));
        hPanel.add(new LabelInput(LangMgr.lang(Word4.MARKER_COLOR)+":"));
        hPanel.add(backgPanel);
        panel.add(hPanel);
    
        dlog = new HDialog(CsVarUtil.parentFrame, 400);
        dlog.setSize(600, 550);
        dlog.setIconImage(IconFileUtil.getLogo());
        dlog.setWindowTitle(LangMgr.sLang(CsL.EXEC_SQL));
        dlog.setToolBar(initToolBar());
        dlog.setRootPanel(panel);
        dlog.show();
    }

    private HBarPanel initToolBar() {
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel hTool = new HBarPanel(l);

        //导入shp文件
        HButton input = new HButton(LangMgr.lang(Word2.EXECUTE)) {
            @Override
            public void onClick() {
                Connection conn = null;
                try {
                    conn = CsVarUtil.newConn(jdbcBean);
                    String sqlstr = textArea.getText().trim();
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
                    Lg.error(SqlFrame.class, e);
                    PopDlgUtil.error(e);
                }finally {
                    ConnUtil.close(conn);
                    dlog.hide();
                }
            }
        };
        input.setIcon(FaSolidEnum.PLAY,16);
        hTool.add(input);

        return hTool;
    }

}
