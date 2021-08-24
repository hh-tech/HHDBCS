package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.mgr.tree.handler.RightMenuActionHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author: Jiang
 * @date: 2020/9/15
 */

public class AddPartitionHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws SQLException {
        //获取主分区表的类型
        DBTypeEnum dbTypeEnum = DriverUtil.getDbType(loginBean.getConn());
        String sql = "select " + (dbTypeEnum == DBTypeEnum.hhdb ? HHdbPgsqlPrefixEnum.hh : HHdbPgsqlPrefixEnum.pg) + "_get_partkeydef(%s) as type";
        List<Map<String, String>> list = SqlQueryUtil.selectStrMapList(loginBean.getConn(), String.format(sql, treeNode.getId()));
        String partitionTypeStr = list.get(0).get("type");
        String partitionType;
        if (partitionTypeStr.contains("LIST")) {
            partitionType = "LIST";
        } else if (partitionTypeStr.contains("RANGE")) {
            partitionType = "RANGE";
        } else if (partitionTypeStr.contains("HASH")) {
            partitionType = "HASH";
        } else {
            return;
        }
        AddToPartitionHandler addToPartitionHandler = (AddToPartitionHandler) RightMenuActionHandler.getInstance("AddToPartition");
        if (addToPartitionHandler == null) {
            return;
        }
        addToPartitionHandler.resolve(treeNode, partitionType);
    }

}
