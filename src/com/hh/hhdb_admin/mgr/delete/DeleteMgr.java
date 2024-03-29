package com.hh.hhdb_admin.mgr.delete;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jiang
 * @Date: 2021/9/7 13:54
 */
public class DeleteMgr extends AbsGuiMgr {

    public static final String SHOW = "show";
    public static final String TRUNCATE = "truncate";   //清空表数据

    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.DELETE;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        TreeMrType treeMrType = TreeMrType.valueOf(msg.getString("nodeType"));
        if (SHOW.equals(GuiJsonUtil.toStrCmd(msg))) {
            List<NodeInfo> nodeInfoList = new ArrayList<>();
            for (JsonValue value : msg.get("names").asArray()) {
                NodeInfo nodeInfo = new NodeInfo();
                nodeInfo.setName(value.asObject().getString("name"));
                switch (treeMrType) {
                    case FUNCTION:
                    case PROCEDURE:
                    case TRIGGER_FUNCTION:
                        nodeInfo.setId(value.asObject().getString("id"));
                        break;
                    case COLUMN:
                    case INDEX:
                    case CHECK_KEY:
                    case PRIMARY_KEY:
                    case UNIQUE_KEY:
                    case FOREIGN_KEY:
                        nodeInfo.setTableName(msg.getString("tableName"));
                        break;
					default:
						break;
                }
                nodeInfo.setSchemaName(msg.getString("schemaName"));
                nodeInfo.setTreeMrType(treeMrType);
                nodeInfo.setOperType(OperateType.DEL);
                nodeInfoList.add(nodeInfo);
            }
            new DeleteComp(nodeInfoList, StartUtil.getLoginBean(), StartUtil.parentFrame);
        } else  if (TRUNCATE.equals(GuiJsonUtil.toStrCmd(msg))) {
            List<NodeInfo> nodeInfoList = new ArrayList<>();
            NodeInfo nodeInfo = new NodeInfo();
            nodeInfo.setName(msg.getString("tableName"));
            nodeInfo.setSchemaName(msg.getString("schemaName"));
            nodeInfo.setTreeMrType(treeMrType);
            nodeInfo.setOperType(OperateType.TRUN);
            nodeInfoList.add(nodeInfo);
    
            new DeleteComp(nodeInfoList, StartUtil.getLoginBean(), StartUtil.parentFrame);
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) throws Exception {
        return null;
    }
}
