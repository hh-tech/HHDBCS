package com.hh.hhdb_admin.mgr.database2.form;

import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.DatabaseSqlUtil;
import com.hh.frame.db_sql.mysql.MysqlDatabaseUtil;
import com.hh.frame.db_sql.mysql.info.MysqlDataBaseBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;


public class MysqlForm extends AbsForm {
    private final SelectBox charSetBox;
    private final SelectBox collateBox;

    public MysqlForm(JdbcBean jdbc, Connection conn) throws Exception {
        super(jdbc,conn);
        collateBox = new SelectBox("collateRule");
        collateBox.addOption("", "");
        charSetBox = new SelectBox("charSet") {
            @Override
            protected void onItemChange(ItemEvent e) {
                collateBox.removeAllItems();
                collateBox.addOption("", "");
                if (StringUtils.isNotBlank(getValue())) {
                    try {
                        List<String> maps = SqlQueryUtil.selectOneColumn(conn, MysqlDatabaseUtil.getCollationSqls(version,getValue()));
                        for (String str : maps) {
                            collateBox.addOption(str, str);
                        }
                    } catch (SQLException e1) {
                        Lg.error(MysqlForm.class, e1);
                    }
                }
            }
        };
        charSetBox.addOption("", "");
        
        List<Map<String, Object>> maps = SqlQueryUtil.select(conn, "show character set;");
        for (Map<String, Object> map : maps) {
            charSetBox.addOption(map.get("charset") + "", map.get("charset") + "");
        }
    }

    @Override
    public HPanel getPanel() {
        hPanel.add(new LabelInput(LangMgr.lang(Word3.CHARACTER_SET)+":"), charSetBox);
        hPanel.add(new LabelInput(LangMgr.lang(Word4.COLLATE_RULE)+":"), collateBox);
        return hPanel;
    }
    
    @Override
    public String getSql() {
        MysqlDataBaseBean dataBaseBean = new MysqlDataBaseBean(nameInput.getValue());
        dataBaseBean.setCharset(charSetBox.getValue());
        dataBaseBean.setCollate(collateBox.getValue());
        return DatabaseSqlUtil.getCreateDbSql(dbTypeEnum,version,dataBaseBean);
    }
}
