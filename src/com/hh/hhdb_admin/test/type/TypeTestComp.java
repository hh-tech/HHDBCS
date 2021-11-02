package com.hh.hhdb_admin.test.type;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.dbobj2.ora.OraSessionEnum;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.type.TypeMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

/**
 * @author YuSai
 */
public class TypeTestComp extends AbsMainTestComp {

    @Override
    public void init() {
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        if (jdbcBean != null) {
            barPanel.add(new HButton("添加类型") {
                @Override
                public void onClick() {
                    StartUtil.eng.doPush(CsMgrEnum.TYPE, GuiJsonUtil.toJsonCmd(TypeMgr.CMD_ADD)
                            .add(StartUtil.PARAM_SCHEMA, jdbcBean.getUser())
                            .add(TypeMgr.PARAM_TEST, "test")
                            .add(TypeMgr.PARAM_NAME, "test")
                            .add(TypeMgr.PARAM_TYPE, OraSessionEnum.type.name()));
                }
            }, new HButton("添加类型体") {
                @Override
                public void onClick() {
                    StartUtil.eng.doPush(CsMgrEnum.TYPE, GuiJsonUtil.toJsonCmd(TypeMgr.CMD_ADD)
                            .add(StartUtil.PARAM_SCHEMA, jdbcBean.getUser())
                            .add(TypeMgr.PARAM_TEST, "test")
                            .add(TypeMgr.PARAM_NAME, "test")
                            .add(TypeMgr.PARAM_TYPE, OraSessionEnum.typebody.name()));
                }
            });
        }
        tFrame.setToolBar(barPanel);
    }

}
