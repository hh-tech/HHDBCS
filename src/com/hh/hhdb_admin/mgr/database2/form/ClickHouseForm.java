package com.hh.hhdb_admin.mgr.database2.form;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.db_sql.DatabaseSqlUtil;
import com.hh.frame.db_sql.pg.info.PgDataBaseBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.List;

public class ClickHouseForm extends AbsForm {
    private final TextAreaInput descriptionInput;

    public ClickHouseForm(JdbcBean jdbcBean, Connection connection) {
        super(jdbcBean, connection);
        descriptionInput = new TextAreaInput("description", "", 5);
    }

    @Override
    public HPanel getPanel() {
        hPanel.add(new LabelInput(LangMgr.lang(Word2.NOTES) + ":"), descriptionInput);
        return hPanel;
    }

    @Override
    public String getSql() {
        PgDataBaseBean dataBaseBean = new PgDataBaseBean(nameInput.getValue());
        List<String> list = DatabaseSqlUtil.getCreateDbSqls(dbTypeEnum, version, dataBaseBean, descriptionInput.getValue());
        return StringUtils.join(list, " ");
    }
}
