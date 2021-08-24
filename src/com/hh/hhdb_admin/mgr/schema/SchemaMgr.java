package com.hh.hhdb_admin.mgr.schema;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;

/**
 * @author: Jiang
 * @date: 2020/11/11
 */

public class SchemaMgr extends AbsGuiMgr {

    public static final String CMD_ADD = "add";
    public static final String CMD_DESIGN = "design";
    public static final String CMD_DELETE = "delete";

    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public Enum<?> getType() {
        return null;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        LoginBean loginBean = StartUtil.getLoginBean();
        String schemaName = msg.getString(StartUtil.PARAM_SCHEMA);
        SchemaComp schemaComp = new SchemaComp() {
            @Override
            public void refresh() {
                StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
                        .add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.DATA_MODEL_SCHEMA_GROUP.name()));
            }
        };
        switch (GuiJsonUtil.toStrCmd(msg)) {
            case CMD_ADD:
                schemaComp.add(loginBean.getConn(), loginBean.getJdbc());
                break;
            case CMD_DESIGN:
                schemaComp.update(loginBean.getConn(), schemaName);
                break;
            case CMD_DELETE:
                schemaComp.delete(loginBean.getConn(), schemaName);
                break;
            default:
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) throws Exception {
        return null;
    }

}
