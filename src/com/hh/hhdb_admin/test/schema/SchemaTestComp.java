package com.hh.hhdb_admin.test.schema;

import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.schema.SchemaMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;

/**
 * @author: Jiang
 * @date: 2020/11/12
 */

public class SchemaTestComp extends AbsMainTestComp {

    @Override
    public void init() {
        HBarPanel toolBar = new HBarPanel();
        toolBar.add(new HButton("新建模式") {
            @Override
            protected void onClick() {
                StartUtil.eng.doPush(CsMgrEnum.SCHEMA, GuiJsonUtil.toJsonCmd(SchemaMgr.CMD_ADD));
            }
        });
        tFrame.setToolBar(toolBar);
    }
}
