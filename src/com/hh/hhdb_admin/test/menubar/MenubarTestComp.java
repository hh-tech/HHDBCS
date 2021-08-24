package com.hh.hhdb_admin.test.menubar;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.menubar.MenubarComp;
import com.hh.hhdb_admin.mgr.menubar.MenubarMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;

/**
 * @author: Jiang
 * @date: 2020/10/15
 */

public class MenubarTestComp extends AbsMainTestComp {
    @Override
    public void init() {
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.CENTER);
        HBarPanel barPanel = new HBarPanel(barLayout);
        barPanel.add(new HButton("打开菜单") {
            @Override
            protected void onClick() {
                try {
                    JsonObject res = StartUtil.eng.doCall(CsMgrEnum.MENUBAR, GuiJsonUtil.toJsonCmd(MenubarMgr.CMD_INIT));
                    String objId = res.getString("id");
                    MenubarComp menubarComp = (MenubarComp) StartUtil.eng.getSharedObj(objId);
                    getDialog().setSize(800, 100);
                    HPanel panel = new HPanel();
                    panel.add(menubarComp);
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
