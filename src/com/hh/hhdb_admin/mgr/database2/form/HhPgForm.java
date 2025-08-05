package com.hh.hhdb_admin.mgr.database2.form;

import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.create_dbobj.attributeMr.AttrHeardType;
import com.hh.frame.db_sql.DatabaseSqlUtil;
import com.hh.frame.db_sql.pg.PgRoleUserSqlUtil;
import com.hh.frame.db_sql.pg.info.PgDataBaseBean;
import com.hh.frame.dbobj3.AllDbObjType;
import com.hh.frame.dbobj3.pg.PgInstanceObj;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextAreaInput;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class HhPgForm extends AbsForm {
    private final SelectBox userSelectBox;
    private final SelectBox spaceSelectBox;
    private final TextAreaInput descriptionInput;

    public HhPgForm(JdbcBean jdbcBean, Connection connection) throws Exception {
        super(jdbcBean,connection);
        userSelectBox = new SelectBox("user");
        userSelectBox.addOption("", "");
        spaceSelectBox = new SelectBox("tableSpace");
        spaceSelectBox.addOption("", "");
        descriptionInput = new TextAreaInput("description", "", 5);
    
        List<String> userLists = SqlQueryUtil.selectOneColumn(conn, PgRoleUserSqlUtil.getUserNamesSql());
        for (String str : userLists) {
            if (!str.startsWith(HHdbPgsqlPrefixEnum.pg.name())) {
                userSelectBox.addOption(str, str);
            }
        }
        // 查询所有表空间信息
        List<Map<String, String>> allTbspProp = new PgInstanceObj(version, conn).getAllObjsProp(AllDbObjType.TAB_SPACE);
        for (Map<String, String> spaceMap : allTbspProp) {
            String name = spaceMap.get(AttrHeardType.NAME.name());
            spaceSelectBox.addOption(name, name);
        }
    }

    @Override
    public HPanel getPanel() {
        hPanel.add(new LabelInput(LangMgr.lang(Word3.OWNER)+":"), userSelectBox);
        hPanel.add(new LabelInput(LangMgr.lang(Word3.TABLE_SPACE)+":"), spaceSelectBox);
        hPanel.add(new LabelInput(LangMgr.lang(Word2.NOTES)+":"), descriptionInput);
        return hPanel;
    }
    
    @Override
    public String getSql() {
        PgDataBaseBean dataBaseBean = new PgDataBaseBean(nameInput.getValue());
        dataBaseBean.setOwner(userSelectBox.getValue());
        dataBaseBean.setTabSpace(spaceSelectBox.getValue());
        List<String> list = DatabaseSqlUtil.getCreateDbSqls(dbTypeEnum,version,dataBaseBean,descriptionInput.getValue());
        return StringUtils.join(list, ";\n");
    }
}
