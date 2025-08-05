package com.hh.hhdb_admin.mgr.tablespace;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.create_dbobj.tablespace.TableSpace;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.comp.SqlViewDialog;
import org.apache.commons.lang3.StringUtils;

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
            case tdsql4pg:
//                String prefix = dbTypeEnum == DBTypeEnum.hhdb ? HHdbPgsqlPrefixEnum.hh.name() : HHdbPgsqlPrefixEnum.pg.name();
                String prefix = HHdbPgsqlPrefixEnum.pg.name();

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
        PopDlgUtil.window = parent;
        if (StringUtils.isEmpty(data.getString("spaceName"))) {
            PopDlgUtil.info(parent, LangMgr.sLang(CsL.PLEASE_ENTER_NAME));
            return false;
        }
        if (DBTypeEnum.hhdb == dbTypeEnum || DBTypeEnum.pgsql == dbTypeEnum || DBTypeEnum.tdsql4pg == dbTypeEnum || DBTypeEnum.oracle == dbTypeEnum) {
            if (StringUtils.isEmpty(data.getString("location"))) {
                PopDlgUtil.info(parent, LangMgr.sLang(CsL.PLEASE_ENTER_LOCATION));
                return false;
            }
        }
        if (DBTypeEnum.oracle == dbTypeEnum) {
            if (StringUtils.isEmpty(data.getString("size"))) {
                PopDlgUtil.info(parent, LangMgr.sLang(CsL.PLEASE_ENTER_THE_SIZE));
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
}
