package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

/**
 * 处理闪回表重命名事件
 *
 * @author Yusai
 */

public class FlashbackRenameHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        String newName = JOptionPane.showInputDialog(null, getLang("enterTableName"), getLang("rename"), JOptionPane.PLAIN_MESSAGE);
//        DBTypeEnum dbTypeEnum = DriverUtil.getDbTypeOp(loginBean.getJdbc()).orElse(DBTypeEnum.hhdb);
        String schemaName =getSchemaName();
        if (StringUtils.isNotBlank(newName)) {
            SqlExeUtil.executeUpdate(loginBean.getConn(), String.format("FLASHBACK TABLE %s TO BEFORE DROP RENAME TO %s", treeNode.getName(), newName));
            PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("flashbackSuccess"));
            sendMsg(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
                    .add(StartUtil.PARAM_SCHEMA, schemaName)
                    .add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.RECYCLE_BIN_GROUP.name()));
        }
    }

}
