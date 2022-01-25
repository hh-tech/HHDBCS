package com.hh.hhdb_admin.mgr.obj_query.handler;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.mgr.delete.DeleteComp;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;
import com.hh.hhdb_admin.mgr.delete.OperateType;
import com.hh.hhdb_admin.mgr.obj_query.ObjQueryComp;
import com.hh.hhdb_admin.mgr.tree.handler.action.MultiChooseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ouyangxu
 * @date 2021-07-29 0029 14:17:36
 */
public class ObjDeleteHandler extends MultiChooseHandler {
    protected ObjQueryComp queryComp;
    protected HTreeNode[] treeNodes;
    protected HTable table;
    protected HDialog parent;

    public ObjDeleteHandler(ObjQueryComp queryComp, HTable table, HDialog parent, HTreeNode... treeNodes) {
        super(treeNodes);
        this.queryComp = queryComp;
        this.table = table;
        this.parent = parent;
        this.treeNodes = treeNodes;
    }

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        if (PopPaneUtil.confirm(parent.getWindow(), ObjQueryComp.getLang("sure_delete"))) {
            List<NodeInfo> nodeInfos = new ArrayList<>();
            for (HTabRowBean rowBean : table.getSelectedRowBeans()) {
                Map<String, String> map = rowBean.getOldRow();
                NodeInfo nodeInfo = new NodeInfo();
                nodeInfo.setName(map.get("name"));
                nodeInfo.setId(map.get("object_name"));
                nodeInfo.setTableName(map.get("object_name"));
                nodeInfo.setSchemaName(schemaName);
                nodeInfo.setTreeMrType(TreeMrType.valueOf(map.get("type")));
                nodeInfo.setOperType(OperateType.DEL);
                nodeInfos.add(nodeInfo);
            }
            new DeleteComp(nodeInfos, loginBean, parent) {
                @Override
                public void refresh() {
                    queryComp.search();
                }
            };
        }
    }

    public ObjQueryComp getQueryComp() {
        return queryComp;
    }

    public void setQueryComp(ObjQueryComp queryComp) {
        this.queryComp = queryComp;
    }
}
