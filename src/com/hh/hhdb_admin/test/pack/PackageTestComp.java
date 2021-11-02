package com.hh.hhdb_admin.test.pack;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.pack.PackageMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

/**
 * @author YuSai
 */
public class PackageTestComp extends AbsMainTestComp {

    @Override
    public void init() {
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        if (jdbcBean != null) {
            barPanel.add(new HButton("添加包") {
                @Override
                public void onClick() {
                    StartUtil.eng.doPush(CsMgrEnum.PACKAGE, GuiJsonUtil.toJsonCmd(PackageMgr.CMD_ADD)
                            .add(StartUtil.PARAM_SCHEMA, jdbcBean.getUser())
                            .add(PackageMgr.PARAM_TEST, "test")
                            .add(PackageMgr.PARAM_NAME, "test"));
                }
            });
        }
        tFrame.setToolBar(barPanel);
    }

}
