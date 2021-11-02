package com.hh.hhdb_admin.mgr.tree;

import com.alipay.oceanbase.jdbc.StringUtils;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.engine.GuiMsgType;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;


/**
 * @author: Jiang
 * @date: 2020/9/10
 */

public class TreeMgr extends AbsGuiMgr {

    public static final String CMD_INIT = "init";
    public static final String CMD_INIT_TEST = "initTest";
    public static final String CMD_REFRESH = "refresh";
    public static final String CMD_REFRESH_WITH_NODE = "refreshWithNode";
    public static final String CMD_DELETE_NODE = "deleteNode";

    public static final String PARAM_NODE_TYPE = "treeNodeType";
    public static final String PARAM_NODE_OBJ_ID = "nodeObjId";

    private TreeComp treeComp;

    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public String getHelp() {
        return GuiJsonUtil.genCmdHelp(CMD_REFRESH, "刷新指定节点的父节点，需要参数TreeMgr.PARAM_NODE_TYPE(参考TreeNodeType类)," +
                "StartUtil.PARAM_SCHEMA(用户视图必须传), StartUtil.PARAM_TABLE(当节点在表节点下时需要传)", GuiMsgType.RECE);
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.TREE;
    }

    @Override
    public void doPush(JsonObject msg) {
        if (treeComp == null) {
            return;
        }
        switch (GuiJsonUtil.toStrCmd(msg)) {
            case CMD_REFRESH:
                String schemaName = msg.getString(StartUtil.PARAM_SCHEMA);
                String tableName = msg.getString(StartUtil.PARAM_TABLE);
                TreeMrType nodeType = TreeMrType.valueOf(msg.getString(TreeMgr.PARAM_NODE_TYPE));
                Boolean isParent = msg.getBoolean("isParent");
                treeComp.refreshNode(schemaName, tableName, nodeType, isParent != null && isParent);
                break;
            case CMD_REFRESH_WITH_NODE:
                String nodeObjId = msg.getString(PARAM_NODE_OBJ_ID);
                HTreeNode treeNode = (HTreeNode) StartUtil.eng.getSharedObj(nodeObjId);
                treeComp.getTree().getLeftDoubleHandler().refreshNode(treeNode);
                return;
            case CMD_DELETE_NODE:
                String objId = msg.getString(PARAM_NODE_OBJ_ID);
                if (StringUtils.isNotBlank(objId)) {
                    treeComp.getTree().removeHTreeNode((HTreeNode) StartUtil.eng.getSharedObj(objId));
                }
                break;
            default:
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) throws Exception {
        JsonObject res = new JsonObject();
        LoginBean loginBean = StartUtil.getLoginBean();
        if (CMD_INIT.equals(GuiJsonUtil.toStrCmd(msg))) {
            treeComp = TreeComp.newTreeInstance(loginBean);
            String compId = StartUtil.eng.push2SharedMap(treeComp);
            res.set("id", compId);
        } else {
            unknowMsg(msg.toPrettyString());
        }
        return res;
    }

}
