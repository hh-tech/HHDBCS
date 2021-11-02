package com.hh.hhdb_admin.mgr.dblink;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.engine.GuiMsgType;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;

/**
 * @author YuSai
 */
public class DblinkMgr extends AbsGuiMgr {

    public static final String CMD_SHOW = "CMD_SHOW";
    public static final String PARAM_NAME = "name";

    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public String getHelp() {
        return GuiJsonUtil.genCmdHelp(CMD_SHOW, "显示Dblink", GuiMsgType.RECE);
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.DBLINK;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        String cmd = GuiJsonUtil.toStrCmd(msg);
        String name = GuiJsonUtil.toPropValue(msg, PARAM_NAME);
        LoginBean loginBean = getLoginBean();
        DblinkComp dblinkComp = new DblinkComp(loginBean.getConn(), loginBean.getJdbc().getUser(), name) {
            @Override
            public void refreshTree() {
                // 刷新树节点
                StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH).
                        add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.DBLINK_GROUP.name()));
            }
        };
        if (CMD_SHOW.equals(cmd)) {
            dblinkComp.show();
        } else {
            unknowMsg(msg.toPrettyString());
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) {
        return null;
    }

    public LoginBean getLoginBean() throws Exception {
        JsonObject sharedIdObj = StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(LoginMgr.ObjType.LOGIN_BEAN));
        return (LoginBean) StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(sharedIdObj));
    }

}
