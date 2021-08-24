package com.hh.hhdb_admin.test.toolbar;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.toolbar.ToolbarMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;

/**
 * @author: Jiang
 * @date: 2020/10/15
 */

public class ToolbarTestComp extends AbsMainTestComp {
    @Override
    public void init() {
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.CENTER);
        HBarPanel barPanel = new HBarPanel(barLayout);
        barPanel.add(new HButton("打开dba工具栏") {
            @Override
            protected void onClick() {
                try {
                    String id = StartUtil.eng.doCall(CsMgrEnum.TOOLBAR, GuiJsonUtil.toJsonCmd(ToolbarMgr.CMD_INIT)
                            .add("isDba", true)).getString("id");
                    HBarPanel toolbar = (HBarPanel) StartUtil.eng.getSharedObj(id);
                    getDialog().setSize(800, 200);
                    HPanel panel = new HPanel();
                    panel.add(toolbar);
                    getDialog().setRootPanel(panel);
                    getDialog().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        barPanel.add(new HButton("打开user工具栏") {
            @Override
            protected void onClick() {
                try {
                    String id = StartUtil.eng.doCall(CsMgrEnum.TOOLBAR, GuiJsonUtil.toJsonCmd(ToolbarMgr.CMD_INIT)
                            .add("isDba", false)).getString("id");
                    HBarPanel toolbar = (HBarPanel) StartUtil.eng.getSharedObj(id);
                    getDialog().setSize(800, 200);
                    HPanel panel = new HPanel();
                    panel.add(toolbar);
                    getDialog().setRootPanel(panel);
                    getDialog().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tFrame.setToolBar(barPanel);
    }
}
