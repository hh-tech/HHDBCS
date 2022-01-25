package com.hh.hhdb_admin.mgr.delete.impl;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;

/**
 * @Author: Jiang
 * @Date: 2021/9/13 10:38
 */
public class TableSpaceDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String name = nodeInfo.getName();
        switch (dbType) {
            case hhdb:
            case pgsql:
            case mysql:
            case db2:
                execute(String.format("DROP TABLESPACE \"%s\"", name));
                break;
            case oracle:
                execute(String.format("drop tablespace \"%s\" including contents and datafiles" +
                        " cascade constraint", name));
                break;
            default:
        }
    }
}
