package com.hh.hhdb_admin.test.dblink;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.dblink.DblinkMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

/**
 * @author YuSai
 */
public class DblinkTestComp extends AbsMainTestComp {

    @Override
    public void init() {
        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        if (jdbcBean != null) {
            DBTypeEnum dbTypeEnum = DriverUtil.getDbType(jdbcBean);
            HBarLayout barLayout = new HBarLayout();
            barLayout.setAlign(AlignEnum.LEFT);
            HBarPanel barPanel = new HBarPanel(barLayout);
            barPanel.add(new HButton("添加Dblink") {
                @Override
                public void onClick() {
                    StartUtil.eng.doPush(CsMgrEnum.DBLINK, GuiJsonUtil.toJsonCmd(DblinkMgr.CMD_SHOW)
                            .add(DblinkMgr.PARAM_NAME, ""));
                }
            });
            if (dbTypeEnum != null) {
                tFrame.setWindowTitle(dbTypeEnum.name() + "--Dblink新增测试");
            }
            tFrame.setToolBar(barPanel);
        }
    }

}
