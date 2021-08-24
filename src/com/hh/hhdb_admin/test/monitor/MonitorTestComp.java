package com.hh.hhdb_admin.test.monitor;

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
import com.hh.hhdb_admin.mgr.monitor.MonitorMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

import javax.swing.*;

/**
 * @author YuSai
 */
public class MonitorTestComp extends AbsMainTestComp {

    @Override
    public void init() {
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        if (jdbcBean != null) {
            DBTypeEnum dbType = DriverUtil.getDbType(jdbcBean);
            if (dbType != null) {
                if (dbType.equals(DBTypeEnum.hhdb) || dbType.equals(DBTypeEnum.pgsql)
                        || dbType.equals(DBTypeEnum.mysql) || dbType.equals(DBTypeEnum.oracle)
                        || dbType.equals(DBTypeEnum.sqlserver)) {
                    barPanel.add(new HButton("数据库监控") {
                        @Override
                        public void onClick() {
                            StartUtil.eng.doPush(CsMgrEnum.MONITOR, GuiJsonUtil.toJsonCmd(MonitorMgr.CMD_SHOW_DB_MONITOR)
                                    .add(MonitorMgr.IS_TEST, true));
                        }
                    });
                    if (dbType.equals(DBTypeEnum.hhdb) || dbType.equals(DBTypeEnum.pgsql)) {
                        if (dbType.equals(DBTypeEnum.hhdb)) {
                            barPanel.add(new HButton("系统监控") {
                                @Override
                                public void onClick() {
                                    StartUtil.eng.doPush(CsMgrEnum.MONITOR, GuiJsonUtil.toJsonCmd(MonitorMgr.CMD_SHOW_SYSTEM_MONITOR)
                                            .add(MonitorMgr.IS_TEST, true));
                                }
                            });
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "目前只支持hhdb、pg、mysql、oracle!");
                }
            }
        }
        tFrame.setToolBar(barPanel);
    }

}
