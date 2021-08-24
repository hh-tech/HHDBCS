package com.hh.hhdb_admin.mgr.tablespace;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.create_dbobj.tablespace.TableSpace;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.mgr.table.comp.SqlViewDialog;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author YuSai
 */
public class TableSpaceUtil {

    private static TableSpace tableSpace;

    static void initTableSpace(Connection conn, DBTypeEnum dbTypeEnum) {
        tableSpace = new TableSpace(dbTypeEnum, conn);
    }

    public static void setDialogSize(DBTypeEnum typeEnum, HDialog dialog)  {
        if (typeEnum == DBTypeEnum.oracle) {
            dialog.setSize(1010, 700);
        } else if (typeEnum == DBTypeEnum.db2) {
            dialog.setSize(600, 700);
        } else {
            dialog.setSize(600, 400);
        }

    }

    public static List<Map<String, String>> getUsers(Connection conn, DBTypeEnum dbTypeEnum) throws SQLException {
        List<Map<String, String>> userLists = new ArrayList<>();
        String querySql;
        switch (dbTypeEnum) {
            case hhdb:
            case pgsql:
                String prefix = dbTypeEnum.name().substring(0, 2);
                querySql = "select usename username from " + prefix + "_user";
                userLists = SqlQueryUtil.selectStrMapList(conn, querySql);
                break;
            case oracle:
                querySql = "SELECT username FROM DBA_USERS WHERE account_status = 'OPEN'";
                userLists = SqlQueryUtil.selectStrMapList(conn, querySql);
            default:
        }
        return userLists;
    }

    static boolean checkData(HDialog parent, JsonObject data, DBTypeEnum dbTypeEnum) {
        if (StringUtils.isEmpty(data.getString("spaceName"))) {
            PopPaneUtil.info(parent.getWindow(), TableSpaceComp.getLang("pleEnterTsName"));
            return false;
        }
        if (DBTypeEnum.hhdb.equals(dbTypeEnum) || DBTypeEnum.pgsql.equals(dbTypeEnum) || DBTypeEnum.oracle.equals(dbTypeEnum)) {
            if (StringUtils.isEmpty(data.getString("location"))) {
                PopPaneUtil.info(parent.getWindow(), TableSpaceComp.getLang("pleEnterTsLocation"));
                return false;
            }
        }
        if (DBTypeEnum.oracle.equals(dbTypeEnum)) {
            if (StringUtils.isEmpty(data.getString("size"))) {
                PopPaneUtil.info(parent.getWindow(), TableSpaceComp.getLang("pleEnterSize"));
                return false;
            }
        }
        if (DBTypeEnum.hhdb.equals(dbTypeEnum) || DBTypeEnum.pgsql.equals(dbTypeEnum) ) {
            if (StringUtils.isEmpty(data.getString("owner"))) {
                PopPaneUtil.info(parent.getWindow(), TableSpaceComp.getLang("pleSelectUser"));
                return false;
            }
        }
        return true;
    }

    public static void save(JsonObject data) throws SQLException {
        tableSpace.addTableSpace(data);
    }

    public static void save(String sql) throws SQLException {
        tableSpace.addTableSpace(sql);
    }

    public static void delete(String spaceName) throws SQLException {
        tableSpace.delTabSpace(spaceName);
    }

    static void previewSql(JsonObject data) throws SQLException {
        SqlViewDialog dialog = new SqlViewDialog();
        dialog.setSql(tableSpace.getSql(data));
        dialog.show();
    }

    static void previewSql(String sql) {
        SqlViewDialog dialog = new SqlViewDialog();
        dialog.setSql(sql);
        dialog.show();
    }

    public static ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.TABLE_SPACE.name(), name, IconSizeEnum.SIZE_16));
    }

}
