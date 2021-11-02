package com.hh.hhdb_admin.mgr.usr.comp;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.userMr.mr.AbsUsrMr;
import com.hh.frame.dbobj2.version.VersionUtil;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.usr.util.UsrUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 权限表格
 */
public class GrantTableComp extends AbsHComp {
    private LastPanel panel;
    protected HTable hTable;
    protected String permUsr;
    protected String columns;
    protected boolean permUpdate;
    protected AbsUsrMr absUsrMr;
    private JScrollPane scrollPane;
    private PrivsType type;
    protected Connection conn;
    protected DBTypeEnum typeEnum;
    private SearchToolBar searchToolBar;

    public enum PrivsType {
        role, sys
    }

    protected static final String domainName = GrantTableComp.class.getName();

    static {
        try {
			LangMgr2.loadMerge(GrantTableComp.class);
		} catch (IOException e) {
			PopPaneUtil.error(e);
		}
    }

    public GrantTableComp(PrivsType type, Connection conn) {
        try {
            this.type = type;
            this.conn = conn;
            this.typeEnum = DriverUtil.getDbType(conn);
            absUsrMr = AbsUsrMr.genUsrSqlMr(this.typeEnum, VersionUtil.getDbVersion(conn));
            panel = new LastPanel(false);
            hTable = new HTable();

            hTable.setRowHeight(25);
            scrollPane = new JScrollPane(hTable.getComp());
            panel.set(scrollPane);

//            if (type == PrivsType.sys) {
                searchToolBar = new SearchToolBar(hTable);
                panel.setHead(searchToolBar.getComp());
//            }

            if (type == PrivsType.role) {
                this.columns = "role";
            } else {
                this.columns = "perm";
            }
            setTableColumn();
            hTable.load(new ArrayList<>(), 1);
            this.comp = panel.getComp();
        } catch (Exception e) {
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
    }

    public void refreshTable() throws SQLException {
        hTable.load(getData(), 1);
    }

    public void setPermUsr(String permUsr) {

        this.permUsr = permUsr;
    }

    public void setPermUpdate(boolean permUpdate) {
        this.permUpdate = permUpdate;
    }

    public void setHeight(int height) {
        scrollPane.setPreferredSize(new Dimension(scrollPane.getWidth(), height));
        scrollPane.repaint();
    }

    public String getSql() throws Exception {
        List<HTabRowBean> beans = hTable.getRowBeans(RowStatus.UPDATE);
        StringBuffer buffer = new StringBuffer();
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
                        buffer.append(absUsrMr.getGrantSql(bean.getOldRow().get(this.columns), UsrUtil.formatName(this.typeEnum, this.permUsr)));
                    } else {
                        buffer.append(absUsrMr.getRevokeSql(bean.getOldRow().get(this.columns), UsrUtil.formatName(this.typeEnum, this.permUsr)));
                    }
                }
            }
        }

        return buffer.toString();
    }


    /**
     * 设置表格列
     */
    private void setTableColumn() {
        if (scrollPane.getViewport().getView() != null) {
            scrollPane.getViewport().removeAll();
        }
        hTable = new HTable();
        hTable.setRowHeight(25);
        scrollPane.getViewport().add(hTable.getComp());
        if (searchToolBar != null ) {
            ((JPanel) panel.getComp()).remove(searchToolBar.getComp());
            searchToolBar = new SearchToolBar(hTable);
            panel.setHead(searchToolBar.getComp());
            ((JPanel) panel.getComp()).updateUI();
        }
        addColumn();
    }


    protected void addColumn() {
        if (type == PrivsType.role) {
            hTable.addCols(new DataCol(columns, LangMgr2.getValue(domainName, "ROLE")));
            BoolCol boolCol = new BoolCol("grant", LangMgr2.getValue(domainName, "GRANT"));
            boolCol.setWidth(200);
            hTable.addCols(boolCol);


        } else {
            hTable.addCols(new DataCol(columns, LangMgr2.getValue(domainName, UsrUtil.getPermTitleKey(typeEnum))));


            BoolCol boolCol = new BoolCol("grant", LangMgr2.getValue(domainName, "GRANT"));
            boolCol.setWidth(200);
            hTable.addCols(boolCol);
        }
    }

    /**
     * 获取用户权限的数据
     */
    private List<Map<String, String>> getData() throws SQLException {
        List<Map<String, String>> list = new ArrayList<>();
        if (conn == null) {
            return list;
        }
        List<Map<String, String>> usrServerPrivs;
        if (type == PrivsType.role) {
            usrServerPrivs = absUsrMr.getUsrRoles(conn, this.permUsr);
        } else {
            usrServerPrivs = absUsrMr.getUsrServerPrivs(conn, this.permUsr);
        }
        return usrServerPrivs;

    }

}
