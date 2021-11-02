package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.json.JsonArray;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.delete.DeleteMgr;
import org.apache.commons.lang3.StringUtils;

public class MultiChooseHandler extends AbsHandler {

    private final HTreeNode[] treeNodes;

    public MultiChooseHandler(HTreeNode[] treeNodes, String actionCmd) {
        this.treeNodes = treeNodes;
    }

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        if (treeNodes.length == 0) {
            return;
        }
        if (!PopPaneUtil.confirm(getLang("sure_delete"))) {
            return;
        }
        JsonArray jsonArray = new JsonArray();
        for (HTreeNode item : treeNodes) {
            jsonArray.add(item.getName());
        }
        String tableName = getTableName();
        String schemaName = getSchemaName();

        sendMsg(CsMgrEnum.DELETE, GuiJsonUtil.toJsonCmd(DeleteMgr.SHOW)
                .add("names", jsonArray)
                .add("schemaName", schemaName)
                .add("nodeType", treeNodes[0].getType())
                .add("tableName", tableName)
                .add("isParent", true)
                .add("id", StringUtils.isBlank(treeNodes[0].getId()) ? "" : treeNodes[0].getId()));

    }


}
