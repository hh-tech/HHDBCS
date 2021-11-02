package com.hh.hhdb_admin.mgr.tree.handler.action;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.delete.DeleteMgr;
import com.hh.hhdb_admin.mgr.tree.TreeComp;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;

/**
 * 删除事件处理
 *
 * @author Jiang
 * @date 2020/9/15
 */

public class DeleteHandler extends AbsHandler {

    protected boolean isCascade = false;
    protected boolean isMulti = false;


    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        boolean isDel = PopPaneUtil.confirm(null, TreeComp.getLang("sure_delete"));
        if (isDel){
            doDel(treeNode);
        }
    }

    protected void doDel(HTreeNode treeNode) throws SQLException {
        String nodeName = treeNode.getName();
        String tableName = getTableName();
        String schemaName = getSchemaName();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(nodeName);
        sendMsg(CsMgrEnum.DELETE, GuiJsonUtil.toJsonCmd(DeleteMgr.SHOW)
                .add("names", jsonArray)
                .add("schemaName", schemaName)
                .add("nodeType", treeNode.getType())
                .add("tableName", tableName)
                .add("id", StringUtils.isBlank(treeNode.getId()) ? "" : treeNode.getId()));

    }

    public void delNode(HTreeNode treeNode) {
        String objId = StartUtil.eng.push2SharedMap(treeNode);
        JsonObject msg = GuiJsonUtil.toJsonCmd(TreeMgr.CMD_DELETE_NODE);
        msg.add(TreeMgr.PARAM_NODE_OBJ_ID, objId);
        sendMsg(CsMgrEnum.TREE, msg);
    }

    public void cascadeResolve(HTreeNode treeNode) throws Exception {
        this.isCascade = true;
        resolve(treeNode);
        this.isCascade = false;
    }

    public void resolveMulti(HTreeNode treeNode) throws Exception {
        this.isMulti = true;
        resolve(treeNode);
        this.isMulti = false;
    }

    public void cascadeMultiResolve(HTreeNode treeNode) throws Exception {
        this.isMulti = this.isCascade = true;
        resolve(treeNode);
        this.isCascade = this.isMulti = false;
    }
}
