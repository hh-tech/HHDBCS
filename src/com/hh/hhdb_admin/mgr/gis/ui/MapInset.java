package com.hh.hhdb_admin.mgr.gis.ui;


import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.CheckGroupInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.gis.GisComp;
import com.hh.hhdb_admin.mgr.gis.GisMgr;
import com.hh.hhdb_admin.mgr.gis.util.ExpGis;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 地图图层数据操作
 */
public class MapInset {
    private HScrollPane scroll = new HScrollPane();
    private HDialog dlog;

    public MapInset(JdbcBean jdbcBean, List<Map<String, String>> tablelist) {
        new MapInset(jdbcBean, tablelist, null, new ArrayList<>());
    }

    /**
     * 展示数据库中图层数据
     * @param jdbcBean
     * @param tablelist     存在的表
     * @param gm
     * @param exclude       需要排除的
     */
    public MapInset(JdbcBean jdbcBean, List<Map<String, String>> tablelist, final GisComp gm, List<String> exclude) {
        if (tablelist.isEmpty()) {
            JOptionPane.showMessageDialog(null, GisMgr.getLang("noActionable"), GisMgr.getLang("hint"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        HPanel tablejp = new HPanel();
        int o = 0;
        CheckGroupInput cInput = new CheckGroupInput("check", new HPanel(new HDivLayout(GridSplitEnum.C4, GridSplitEnum.C4)));
        for (Map<String, String> map : tablelist) {
            String str = map.get("table_schema") + "." + map.get("table_name");
            if (!exclude.contains(map.get("table_name"))) {
                cInput.add(str, str);
                o++;
            }
        }
        if (o == 0) {
            JOptionPane.showMessageDialog(null, GisMgr.getLang("noActionable"), GisMgr.getLang("hint"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        tablejp.add(cInput);

        HButton jbutton = new HButton(GisMgr.getLang("confirm")) {
            @Override
            public void onClick() {
                Set<String> name = cInput.getValues();

                if (name.size() == 0) {
                    JOptionPane.showMessageDialog(null, GisMgr.getLang("noTable"), GisMgr.getLang("hint"), JOptionPane.INFORMATION_MESSAGE);
                    return;
                } else if (name.size() >= 5) {
                    int response = JOptionPane.showConfirmDialog(null, GisMgr.getLang("continue"), GisMgr.getLang("hint"), JOptionPane.YES_NO_OPTION);
                    if (response != 0) return;
                }

                if (null == gm) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 设置选择模式
                    int resultFile = chooser.showSaveDialog(null);
                    if (resultFile == JFileChooser.APPROVE_OPTION) new ExpGis(jdbcBean, name, chooser.getSelectedFile());
                }else {
                    //遍及图层集合
                    for (String str : name) {
                        gm.addLayer(str);
                    }
                }
                dlog.hide();
            }
        };
        jbutton.setIcon(GisMgr.getIcon("import"));
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel tbar = new HBarPanel(barLayout);
        tbar.add(jbutton);

        dlog = StartUtil.getMainDialog();
        dlog.setSize(600, 300);
        dlog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
        dlog.setWindowTitle(GisMgr.getLang("selectTable"));
        scroll.setPanel(tablejp);
        dlog.setRootPanel(scroll);
        dlog.setToolBar(tbar);
        dlog.show();
    }
}
