package com.hh.hhdb_admin.mgr.database;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.database.Database;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.mgr.table.comp.SqlViewDialog;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author YuSai
 */
public class DatabaseUtil {

    private static Database database;

    static void initDatabase(Connection conn, DBTypeEnum dbTypeEnum) {
        database = new Database(dbTypeEnum, conn);
    }

    static boolean checkData(HDialog parent, JsonObject data, DBTypeEnum dbTypeEnum) {
        if (StringUtils.isEmpty(data.getString("dbName"))) {
            PopPaneUtil.info(parent.getWindow(), DatabaseComp.getLang("pleEnterDbName"));
            return false;
        }
        if (DBTypeEnum.hhdb.equals(dbTypeEnum) || DBTypeEnum.pgsql.equals(dbTypeEnum)) {
            if (StringUtils.isEmpty(data.getString("dbName"))) {
                PopPaneUtil.info(parent.getWindow(), DatabaseComp.getLang("pleSelDbOwner"));
                return false;
            }
            if (StringUtils.isEmpty(data.getString("dbName"))) {
                PopPaneUtil.info(parent.getWindow(), DatabaseComp.getLang("pleSelDbSpace"));
                return false;
            }
        } else if (DBTypeEnum.db2.equals(dbTypeEnum)) {
            if (StringUtils.isNotEmpty(data.getString("autoStorage"))) {
                if (StringUtils.isEmpty(data.getString("autoStoragePath"))) {
                    PopPaneUtil.info(parent.getWindow(), DatabaseComp.getLang("pleEntPath"));
                    return false;
                }
            }
        }
        return true;
    }

    public static void save(JsonObject data) throws SQLException {
        database.addDatabase(data);
    }

    public static void delete(String spaceName) throws Exception {
        database.deleteDatabase(spaceName);
    }

    static void previewSql(JsonObject data) {
        SqlViewDialog dialog = new SqlViewDialog();
        dialog.setSql(database.getSql(data));
        dialog.show();
    }

}
