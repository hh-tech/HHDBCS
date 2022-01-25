package com.hh.hhdb_admin.mgr.delete.impl;

import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.delete.AbsDel;
import com.hh.hhdb_admin.mgr.delete.NodeInfo;
import com.hh.hhdb_admin.mgr.schema.SchemaComp;

/**
 * @Author: Jiang
 * @Date: 2021/9/13 10:42
 */
public class SchemaDel extends AbsDel {

    @Override
    public void del(NodeInfo nodeInfo) throws Exception {
        String name = SqlStrUtil.dealDoubleQuote(dbType, nodeInfo.getName());
        switch (dbType) {
            case hhdb:
            case pgsql:
                if (name.equalsIgnoreCase("\"public\"")) {
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(), "Public模式无法删除");
                    return;
                }
                execute(String.format("DROP SCHEMA %s cascade", name));
                break;
            case sqlserver:
                execute(String.format("DROP SCHEMA %s", name));
                break;
            case db2:
                try {
                    execute(String.format("drop schema %s restrict", name));
                } catch (Exception e) {
                    if (e.getMessage().toUpperCase().contains("SQLCODE=-478, SQLSTATE=42893, SQLERRMC=SCHEMA")) {
                        throw new Exception(SchemaComp.getLang("del_exp"));
                    } else {
                        throw e;
                    }
                }
                break;
            case dm:
                execute(String.format("DROP SCHEMA %s CASCADE", name));
                break;
            default:
        }
    }
}
