package com.hh.hhdb_admin.test.synonym;

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
import com.hh.hhdb_admin.mgr.synonym.SynonymMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

/**
 * @author YuSai
 */
public class SynonymTestComp extends AbsMainTestComp {

    @Override
    public void init() {
        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        if (jdbcBean != null) {
            DBTypeEnum dbTypeEnum = DriverUtil.getDbType(jdbcBean);
            HBarLayout barLayout = new HBarLayout();
            barLayout.setAlign(AlignEnum.LEFT);
            HBarPanel barPanel = new HBarPanel(barLayout);
            barPanel.add(new HButton("添加Synonym") {
                @Override
                public void onClick() {
                    StartUtil.eng.doPush(CsMgrEnum.SYNONYM, GuiJsonUtil.toJsonCmd(SynonymMgr.CMD_SHOW)
                            .add(SynonymMgr.PARAM_NAME, ""));
                }
            });
            if (dbTypeEnum != null) {
                tFrame.setWindowTitle(dbTypeEnum.name() + "--Synonym新增测试");
            }
            tFrame.setToolBar(barPanel);
        }
    }

}
