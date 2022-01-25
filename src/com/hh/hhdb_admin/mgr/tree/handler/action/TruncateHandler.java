package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.delete.DeleteMgr;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: Jiang
 * @date: 2020/9/15
 * <p>
 * 处理清空事件
 */

public class TruncateHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        if (PopPaneUtil.confirm(StartUtil.parentFrame.getWindow(), getLang("cleanTableData"))) {
            sendMsg(CsMgrEnum.DELETE, GuiJsonUtil.toJsonCmd(DeleteMgr.TRUNCATE)
                    .add("schemaName", getSchemaName())
                    .add("nodeType", treeNode.getType())
                    .add("tableName", getTableName())
                    .add("id", StringUtils.isBlank(treeNode.getId()) ? "" : treeNode.getId()));
        }
    }

}
