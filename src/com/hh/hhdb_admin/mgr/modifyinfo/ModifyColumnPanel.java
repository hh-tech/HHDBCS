package com.hh.hhdb_admin.mgr.modifyinfo;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.db_sql.ColSqlUtil;
import com.hh.frame.db_sql.conn.ColConnUtil;
import com.hh.frame.db_sql.info.SqlColPropBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;

public class ModifyColumnPanel extends BaseModifyPanel {


    private final String columnName;
    public SqlColPropBean oldColBean;

    public ModifyColumnPanel(String schemaName, String tableName, String columnName) {
        super(schemaName, tableName);
        this.columnName = columnName;

        if (getClass() == ModifyColumnPanel.class)
            init();
    }
    
    @Override
    protected void initPanel() throws SQLException {
        super.initPanel();
        if (dbtype == DBTypeEnum.informix || dbtype == DBTypeEnum.derby) newCommentArea.setEnabled(false);
    }
    
    @Override
    protected void initValue() throws SQLException {
        super.initValue();
        newNameInput.setValue(columnName);
//        jdbc.setSchema(schemaName);
        oldColBean = ColConnUtil.selectColProp(dbtype, ver, conn, schemaName, tableName, columnName);
        oldComment = oldColBean.getComment();
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
            oldColBean.setComment(newComment);
            String commentSql = ColSqlUtil.getCommentSql(dbtype, ver, oldColBean);
            if (commentSql != null) sql.append(commentSql).append(NEW_LINE);
        }

        if (!StringUtils.equals(columnName, newName)) {
            String renameColSql = ColSqlUtil.getRenameColSql(dbtype, ver, oldColBean, newName);
            if (renameColSql == null) throw new Exception(LangMgr.lang(Word4.NOT_SUPPORT));
            sql.append(renameColSql).append(NEW_LINE);
        }

        return sql.toString();
    }
}
