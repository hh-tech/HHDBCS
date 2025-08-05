package com.hh.hhdb_admin.mgr.modifyinfo;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.db_sql.TableSqlUtil;
import com.hh.frame.db_sql.hive.HiveTabViewSqlUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;

public class ModifyTablePanel extends BaseModifyPanel {
    public ModifyTablePanel(String schemaName, String tableName) {
        super(schemaName, tableName);

        if (getClass() == ModifyTablePanel.class)
            init();
    }

    @Override
    protected void initPanel() throws SQLException {
        super.initPanel();
        if (dbtype == DBTypeEnum.informix || dbtype == DBTypeEnum.derby || dbtype == DBTypeEnum.sqlite) newCommentArea.setEnabled(false);
    }

    @Override
    protected void initValue() throws SQLException {
        super.initValue();
        newNameInput.setValue(tableName);
        if (dbtype == DBTypeEnum.hive) {
            oldComment = HiveTabViewSqlUtil.getComment(conn, schemaName, tableName);
        } else {
            String sql = TableSqlUtil.getTabCommentSql(dbtype, ver, schemaName, SqlStrUtil.rmQuote4Sel(tableName, dbtype, ver));
            if (sql != null)
                oldComment = SqlQueryUtil.selectValue(conn, sql);
        }
        if (oldComment == null)
            oldComment = "";
        newCommentArea.setValue(oldComment);
    }

    @Override
    public String genSql() throws Exception {
        String sql = "";

        String newComment = newCommentArea.getValue();
        if (!StringUtils.equals(oldComment, newComment)) {
            String addTabCommentSql = TableSqlUtil.getAddTabCommentSql(dbtype, ver, schemaName, tableName, newComment);
            if (addTabCommentSql == null) throw new Exception(LangMgr.lang(Word4.NOT_SUPPORT));
            sql += addTabCommentSql;
            sql += NEW_LINE;
        }

        String newTableName = newNameInput.getValue();
        if (!StringUtils.equals(tableName, newTableName)) {
            String renameTabSql = TableSqlUtil.getRenameTabSql(dbtype, ver, schemaName, tableName, newTableName);
            if (renameTabSql == null) throw new Exception(LangMgr.lang(Word4.NOT_SUPPORT));
            sql += renameTabSql;
            sql += NEW_LINE;
        }

        return sql;
    }
}
