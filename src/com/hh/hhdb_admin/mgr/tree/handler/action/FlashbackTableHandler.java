package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.tree.TreeComp;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;

import javax.swing.*;

/**
 * 处理闪回表事件
 *
 * @author Yusai
 */

public class FlashbackTableHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        int res = JOptionPane.showConfirmDialog(null, TreeComp.getLang("sure_flashback"), getLang("hint"), JOptionPane.YES_NO_OPTION);
        if (res != 0) {
            return;
        }
        SqlExeUtil.executeUpdate(loginBean.getConn(), String.format("FLASHBACK TABLE %s TO BEFORE DROP", treeNode.getName()));
        PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("flashbackSuccess"));
//        DBTypeEnum dbTypeEnum = DriverUtil.getDbTypeOp(loginBean.getJdbc()).orElse(DBTypeEnum.hhdb);
        String schemaName = getSchemaName();
        sendMsg(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
                .add(StartUtil.PARAM_SCHEMA, schemaName)
                .add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.RECYCLE_BIN_GROUP.name()));
    }

}
