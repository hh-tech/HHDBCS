package com.hh.hhdb_admin.mgr.delete;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;

/**
 * @Author: Jiang
 * @Date: 2021/9/8 14:43
 */
public class NodeInfo {

    private String id;
    private String name;
    private String schemaName;
    private String tableName;
    private TreeMrType treeMrType;

    public NodeInfo() {
    }

    public NodeInfo(String name, String schemaName, String tableName, TreeMrType treeMrType) {
        this.name = name;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.treeMrType = treeMrType;
    }

    public NodeInfo(String id, String name, String schemaName, String tableName, TreeMrType treeMrType) {
        this(name, schemaName, tableName, treeMrType);
        this.id = id;
    }

    @Override
    public String toString() {
        return "NodeInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", schemaName='" + schemaName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", treeMrType=" + treeMrType +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public TreeMrType getTreeMrType() {
        return treeMrType;
    }

    public void setTreeMrType(TreeMrType treeMrType) {
        this.treeMrType = treeMrType;
    }
}
