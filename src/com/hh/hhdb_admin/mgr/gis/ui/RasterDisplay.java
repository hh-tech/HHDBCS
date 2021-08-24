package com.hh.hhdb_admin.mgr.gis.ui;

import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.hmenu.HPopMenu;
import com.hh.hhdb_admin.mgr.gis.GisMgr;
import org.geotools.map.Layer;
import org.geotools.map.RasterLayer;

import javax.swing.*;
import java.util.List;

/**
 * 栅格显示操作菜单
 */
public class RasterDisplay extends HPopMenu {
    public RasterDisplay(MapContentPanel mapcontent, List<Layer> list) {
        boolean flag = false;
        //图层内有栅格数据才可以进行此操作
        for (Layer layer : list) {
            if (layer instanceof RasterLayer) {
                flag = true;
                break;
            }
        }
        if (flag) {
            addItem(new HMenuItem(GisMgr.getLang("Grayscale")+" 1") {
                protected void onAction() {
                    mapcontent.changGrayStyle(1);
                }
            });
            addItem(new HMenuItem(GisMgr.getLang("Grayscale")+" 2") {
                protected void onAction() {
                    mapcontent.changGrayStyle(2);
                }
            });
            addItem(new HMenuItem(GisMgr.getLang("Grayscale")+" 3") {
                protected void onAction() {
                    mapcontent.changGrayStyle(3);
                }
            });
            addItem(new HMenuItem(GisMgr.getLang("RGB")) {
                protected void onAction() {
                    mapcontent.changGrayStyle(0);
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, GisMgr.getLang("noRaster"), GisMgr.getLang("error"), JOptionPane.WARNING_MESSAGE);
        }
    }
}