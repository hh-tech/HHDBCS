package com.hh.hhdb_admin.mgr.modifyinfo;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.ViewSqlUtil;
import com.hh.frame.db_sql.hive.HiveTabViewSqlUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.List;

public class ModifyViewPanel extends BaseModifyPanel {
    private final String viewName;

    public ModifyViewPanel(String schemaName, String viewName) {
        super(schemaName, null);
        this.viewName = viewName;

        if (getClass() == ModifyViewPanel.class)
            init();
    }

    @Override
    protected void initPanel() throws SQLException {
        super.initPanel();
        if (DBTypeEnum.isOraSeries(dbtype, ver) || DBTypeEnum.isInformixSeries(dbtype, ver) || dbtype == DBTypeEnum.db2
                || dbtype == DBTypeEnum.sybase || dbtype == DBTypeEnum.hana)
            newNameInput.setEnabled(false);
    }

    @Override
    protected void initValue() throws SQLException {
        super.initValue();
        newNameInput.setValue(viewName);
        if (dbtype == DBTypeEnum.hive) {
            oldComment = HiveTabViewSqlUtil.getComment(conn, schemaName, viewName);
        } else {
            String sql = ViewSqlUtil.getCommentSql(dbtype, ver, schemaName, viewName);
            oldComment = SqlQueryUtil.selectValue(conn, sql);
        }
        if (oldComment == null)
            oldComment = "";
        newCommentArea.setValue(oldComment);
    }

    @Override
    public String genSql() throws Exception {
        StringBuilder sql = new StringBuilder();
        String newComment = newCommentArea.getValue();
        String newName = newNameInput.getValue();

        if (!StringUtils.equals(oldComment, newComment)) {
            String commentSql = ViewSqlUtil.getUpdateCommentSql(dbtype, ver, schemaName, viewName, newComment);
            if (commentSql == null) throw new Exception(LangMgr.lang(Word4.NOT_SUPPORT));
            sql.append(commentSql).append(NEW_LINE);
        }

        if (!StringUtils.equals(viewName, newName)) {
            List<String> renameColSql = ViewSqlUtil.getRenameViewSql(dbtype, ver, schemaName, viewName, newName);
            if (renameColSql.isEmpty()) throw new Exception(LangMgr.lang(Word4.NOT_SUPPORT));
            for (String rename : renameColSql) {
                sql.append(rename).append(NEW_LINE);
            }
        }

        return sql.toString();
    }
}
