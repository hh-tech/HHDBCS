package com.hh.hhdb_admin.test.tablespace;

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
import com.hh.hhdb_admin.mgr.tablespace.TableSpaceMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

/**
 * @author YuSai
 */
public class TableSpaceTestComp extends AbsMainTestComp {

    @Override
    public void init() {
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        if (jdbcBean != null) {
            DBTypeEnum dbTypeEnum = DriverUtil.getDbType(jdbcBean);
            barPanel.add(new HButton("添加表空间") {
                @Override
                public void onClick() {
                    StartUtil.eng.doPush(CsMgrEnum.TABLE_SPACE, GuiJsonUtil.toJsonCmd(TableSpaceMgr.CMD_SHOW_ADD_TABLE_SPACE));
                }
            });
            if (dbTypeEnum != null) {
                tFrame.setWindowTitle(dbTypeEnum.name() + "数据库：" + "--新增表空间测试");
            }
        }
        tFrame.setToolBar(barPanel);
    }

}
