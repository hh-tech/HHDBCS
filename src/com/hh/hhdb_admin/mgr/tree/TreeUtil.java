package com.hh.hhdb_admin.mgr.tree;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.create_dbobj.treeMr.mr.AbsTreeMr;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Jiang
 * @date: 2020/9/17
 */

public class TreeUtil {

    public static final String ICON_CONTEXT = "tree";

    /**
     * 获取当前节点的上层节点，直到匹配目标节点类型
     *
     * @param treeNode   当前节点
     * @param targetType 目标节点类型
     * @return 目标节点
     */
    public static HTreeNode getTargetParentNode(HTreeNode treeNode, TreeMrType targetType) {
        if (treeNode.getType().equals(targetType.name())) {
            return treeNode;
        }
        HTreeNode parentNode = treeNode.getParentHTreeNode();
        if (parentNode == null) {
            return null;
        }
        if (parentNode.getType().equals(targetType.name())) {
            return parentNode;
        } else {
            return getTargetParentNode(parentNode, targetType);
        }
    }

    /**
     * 获取指定节点所在的模式，当不存在的模式时返回空串，避免json赋值错误
     *
     * @param treeNode 树结点
     * @return 模式名
     */
    public static String getSchemaName(HTreeNode treeNode, DBTypeEnum typeEnum) {
        TreeMrType type;
        switch (typeEnum) {
            case hhdb:
            case pgsql:
            case sqlserver:
            case db2:
            case dm:
                type = TreeMrType.SCHEMA;
                break;
            default:
                type = TreeMrType.ROOT_DATA_MODEL;
        }
        HTreeNode ownerNode = getTargetParentNode(treeNode, type);
        return ownerNode == null ? "" : ownerNode.getName();
    }

    /**
     * 获取指定节点所在的表，当不存在时返回空串
     *
     * @param treeNode 树结点
     * @return 表名
     */
    public static String getTableName(HTreeNode treeNode) {
        HTreeNode tableNode = getTargetParentNode(treeNode, TreeMrType.TABLE);
        return tableNode == null ? "" : tableNode.getName();
    }

    /**
     * 获取指定节点所在的包，当不存在时返回空串
     *
     * @param treeNode 树结点
     * @return 包名
     */
    public static String getPackageName(HTreeNode treeNode) {
        HTreeNode tableNode = getTargetParentNode(treeNode, TreeMrType.PACKAGE);
        return tableNode == null ? "" : tableNode.getName();
    }

    /**
     * 获取指定节点所在的分区表子表或主表，当不存在时返回空串
     *
     * @param treeNode 树结点
     * @param isChild  是子表还是主表 true-子表；false-主表
     * @return 表名
     */
    public static String getPartitionTableName(HTreeNode treeNode, boolean isChild) {
        TreeMrType nodeType = isChild ? TreeMrType.PARTITION_TABLE_CHILD : TreeMrType.PARTITION_TABLE;
        HTreeNode tableNode = getTargetParentNode(treeNode, nodeType);
        return tableNode == null ? "" : tableNode.getName();
    }

    public static String getDbItemType(TreeMrType type) {
        switch (type) {
            case TABLE:
                return "TABLE";
            case VIEW:
                return "VIEW";
            case M_VIEW:
                return "MATERIALIZED VIEW";
            case FUNCTION:
                return "FUNCTION";
            case PROCEDURE:
                return "PROCEDURE";
            case SEQUENCE:
                return "SEQUENCE";
            case TYPE:
                return "TYPE";
            case TRIGGER:
                return "TRIGGER";
            case RULE:
                return "RULE";
            case COLUMN:
                return "COLUMN";
            default:
                return "";
        }
    }

    public static void refreshSchemaTree(LoginBean loginBean, CsTree tree, HTreeNode treeNode) {
        AbsTreeMr.genTreeMr(loginBean.getJdbc()).ifPresent(treeMr -> {
            List<HTreeNode> treeNodes = treeMr.getSchemas(loginBean.getConn()).stream().map(item -> {
                String schemaName = item.get("schema_name").trim();
                String iconName = loginBean.getJdbc().getSchema().equals(schemaName) ? "schema_choose.png" : "schema.png";
                HTreeNode node = new HTreeNode();
                node.setName(schemaName);
                node.setOpenIcon(IconFileUtil.getIcon(new IconBean(TreeUtil.ICON_CONTEXT, iconName)));
                return node;
            }).collect(Collectors.toList());
            tree.addHTreeNode(treeNode, treeNodes, true);
        });
    }

}
