package com.hh.hhdb_admin.test.database;

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
import com.hh.hhdb_admin.mgr.database.DatabaseMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

/**
 * @author YuSai
 */
public class DatabaseTestComp extends AbsMainTestComp {

    @Override
    public void init() {
        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        if (jdbcBean != null) {
            DBTypeEnum dbTypeEnum = DriverUtil.getDbType(jdbcBean);
            HBarLayout barLayout = new HBarLayout();
            barLayout.setAlign(AlignEnum.LEFT);
            HBarPanel barPanel = new HBarPanel(barLayout);
            barPanel.add(new HButton("添加数据库") {
                @Override
                public void onClick() {
                    StartUtil.eng.doPush(CsMgrEnum.DATABASE, GuiJsonUtil.toJsonCmd(DatabaseMgr.CMD_SHOW_ADD_DATABASE));
                }
            });
            if (dbTypeEnum != null) {
                tFrame.setWindowTitle(dbTypeEnum.name() + "--数据库新增测试");
            }
            tFrame.setToolBar(barPanel);
        }
    }

}
