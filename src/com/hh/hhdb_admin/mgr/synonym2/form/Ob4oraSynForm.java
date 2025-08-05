package com.hh.hhdb_admin.mgr.synonym2.form;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.view.input.SearchSelectBox;

import java.sql.Connection;
import java.util.List;


public class Ob4oraSynForm extends OracleSynForm {
    
    public Ob4oraSynForm(Connection conn, JdbcBean jdbcBean, String schema) throws Exception {
        super(conn,jdbcBean,schema);
    }
    
    protected void init() throws Exception {
        objectOwnerBox = new SearchSelectBox();
        List<String> users = SqlWinUtil.getSchemaNameList(dbTypeEnum,version,conn);
        users.forEach(user -> objectOwnerBox.addOption(user, user));
        objectOwnerBox.setValue(schema);
    }
    
}
