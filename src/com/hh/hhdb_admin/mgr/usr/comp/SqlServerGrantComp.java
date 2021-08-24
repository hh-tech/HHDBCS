package com.hh.hhdb_admin.mgr.usr.comp;


import com.hh.frame.lang.LangMgr;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;
import com.hh.hhdb_admin.mgr.usr.util.ServiceUtil;
import com.hh.hhdb_admin.mgr.usr.util.UsrUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 权限表格
 */
public class SqlServerGrantComp extends GrantTableComp {
//    private static HDialog dialog;
//    private Map<String, List<HTabRowBean>> schemaDatas;
//    private Map<String, List<HTabRowBean>> roleDatas;
//    private HTable dbRoleTable;
//    private HTable schemaTable;
//    private String currentDb;
//    private String oldDb;

    public SqlServerGrantComp(PrivsType type, Connection conn) {
        super(type, conn);
//        schemaDatas = new HashMap<>();
//        roleDatas = new HashMap<>();

    }

    public String getSql() throws Exception {
        StringBuffer buffer = new StringBuffer();
        List<HTabRowBean> beans = hTable.getRowBeans(RowStatus.UPDATE);

        if (!permUpdate) {
            for (HTabRowBean bean : beans) {
                if (bean.getCurrRow().get("grant").equalsIgnoreCase(Boolean.TRUE.toString())) {
                    buffer.append(absUsrMr.getGrantSql(bean.getOldRow().get(this.columns), this.permUsr));
                }
            }
        } else {
            for (HTabRowBean bean : beans) {
                if (!bean.getCurrRow().get("grant").equals(bean.getOldRow().get("grant"))) {
                    if (bean.getCurrRow().get("grant").equalsIgnoreCase(Boolean.TRUE.toString())) {
                        buffer.append(absUsrMr.getGrantSql(bean.getOldRow().get(this.columns), this.permUsr));
                    } else {
                        buffer.append(getRevokeSql(bean.getOldRow().get(this.columns), this.permUsr));
                    }
                }
            }
        }

        return buffer.toString();
    }


    private String getRevokeSql(String privsName, String usr) throws SQLException {
        StringBuffer buffer = new StringBuffer();
        buffer.append(String.format("USE [%s];", privsName));
        buffer.append(System.lineSeparator());
        buffer.append(String.format("EXEC sp_dropuser '%s';", ServiceUtil.getDbUsrName(conn, privsName, usr)));
        buffer.append(System.lineSeparator());
        return buffer.toString();
    }


    @Override
    protected void addColumn() {

        BoolCol boolCol = new BoolCol("grant", LangMgr.getValue(domainName, "GRANT"));
        boolCol.setWidth(150);
        hTable.addCols(new DataCol(columns, LangMgr.getValue(domainName, UsrUtil.getPermTitleKey(this.typeEnum))));
        hTable.setSingleSelection(true);

//
//        HButton lineDataBtn = new HButton("设置") {
//
//
//            @Override
//            protected void onClick() {
//                List<HTabRowBean> beans = hTable.getSelectedRowBeans();
//                if(beans.isEmpty()){
//                    return;
//
//                }
//                if(!StringUtils.isBlank(currentDb)){
//                    oldDb = currentDb;
//                }
//                currentDb = beans.get(0).getOldRow().get(columns);
//                showDetail();
//            }
//
//
//        };
//
//        ToolBarCol toolBarCol = new ToolBarCol("toolbar", "详细", lineDataBtn);
//        toolBarCol.setWidth(160);
//        hTable.addCol(toolBarCol);
        hTable.addCols(boolCol);
    }

//    private void showDetail() {
//        dialog = new HDialog(new HFrame(), 800);
//        dialog.setOnClickClose(true);
//
//        HPanel panel = new HPanel();
//        initDbRoleTable();
//        initSchemaTable();
//        HPanel dbRoleTablePanel = new HPanel();
//        HPanel schemaTablePanel = new HPanel();
//        LastPanel dbRoleTableLast = new LastPanel();
//        LastPanel schemaTableLast = new LastPanel();
//        dbRoleTableLast.setWithScroll(dbRoleTable.getComp());
//        schemaTableLast.setWithScroll(schemaTable.getComp());
//        dbRoleTablePanel.setLastPanel(dbRoleTableLast);
//        schemaTablePanel.setLastPanel(schemaTableLast);
//        panel.add(schemaTablePanel);
//        panel.add(dbRoleTableLast);
//        dialog.setRootPanel(panel);
//        loadData();
//        dialog.show();
//    }


//    private void loadData() {
//        try {
//            if (dbRoleTable.getRowCount() > 0) {
//                roleDatas.put(oldDb, dbRoleTable.getRowBeans(null));
//            }
//            if (schemaTable.getRowCount() > 0) {
//                schemaDatas.put(oldDb, schemaTable.getRowBeans(null));
//            }
//
//            if (roleDatas.get(currentDb) != null) {
//                dbRoleTable.load(toLoadMap(roleDatas.get(currentDb)), 1);
//            } else {
//                dbRoleTable.load(ServiceUtil.getSqlServerDbRole(conn, currentDb), 1);
//            }
//
//            if (schemaDatas.get(currentDb) != null) {
//                schemaTable.load(toLoadMap(schemaDatas.get(currentDb)), 1);
//            } else {
//                schemaTable.load(ServiceUtil.getSqlServerSchema(conn, currentDb), 1);
//            }
//        } catch (Exception e) {
//            PopPaneUtil.error(e);
//        }
//    }


//    private List<Map<String, String>> toLoadMap(List<HTabRowBean> rowBeans) {
//        Map<String, String> map;
//        List<Map<String, String>> values = new ArrayList<>();
//        for (HTabRowBean row : rowBeans) {
//            map = new HashMap<>();
//            for (String str : row.getOldRow().keySet()) {
//                map.put(str, row.getOldRow().get(str));
//            }
//            if (row.getCurrRow() != null) {
//                for (String str : row.getCurrRow().keySet()) {
//                    map.put(str, row.getCurrRow().get(str));
//                }
//            }
//
//            values.add(map);
//
//        }
//        return values;
//    }

//    private void initDbRoleTable() {
//        dbRoleTable = new HTable();
//        dbRoleTable.addCol(new BoolCol("check", "选择"));
//        dbRoleTable.addCol(new DataCol("name", "数据库角色"));
//        dbRoleTable.setRowHeight(25);
//    }
//
//    private void initSchemaTable() {
//        schemaTable = new HTable();
//        schemaTable.addCol(new BoolCol("check", "选择"));
//        schemaTable.addCol(new DataCol("name", "拥有的架构"));
//        schemaTable.setRowHeight(25);
//    }

}
