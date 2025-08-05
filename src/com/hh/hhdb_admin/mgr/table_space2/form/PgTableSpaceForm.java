package com.hh.hhdb_admin.mgr.table_space2.form;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.db_sql.TabSpaceSqlUtil;
import com.hh.frame.db_sql.pg.PgRoleUserSqlUtil;
import com.hh.frame.db_sql.pg.info.PgTableSpaceBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;


public class PgTableSpaceForm extends AbsTableSpaceForm {
    private final TextInput locationInput;
    private final SelectBox userSelectBox;
    private final TextAreaInput descriptionInput;
    

    public PgTableSpaceForm(Connection conn, JdbcInfo jdbc) throws Exception{
        super(conn,jdbc);
        locationInput = new TextInput("location");
        userSelectBox = new SelectBox("user");
        userSelectBox.addOption("", "");
        descriptionInput = new TextAreaInput("description", "", 5);
    
        String sql = PgRoleUserSqlUtil.getUserNamesSql();
        SqlQueryUtil.selectOneColumn(conn, sql);
        for (String name : SqlQueryUtil.selectOneColumn(conn, sql)) {
            userSelectBox.addOption(name, name);
        }
    }
    
    @Override
    public LastPanel getLastPanel() {
        HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
        layout.setRightWidth(50);
        HPanel panel = new HPanel(layout);
        panel.add(new HeightComp(5));
        panel.add(getLabelInput(LangMgr.lang(Word4.STORAGE_LOCATION)+" ：", locationInput));
        panel.add(getLabelInput(LangMgr.lang(Word2.USER)+" ：", userSelectBox));
        panel.add(getLabelInput(LangMgr.lang(Word2.NOTES)+" ：", descriptionInput));
        panel.add(new HeightComp(5));
        panel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
    
        LastPanel areaLast = new LastPanel();
        areaLast.setTitle(LangMgr.lang(Word2.CODE));
        areaLast.set(area.getComp());
        
        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(panel.getComp());
        lastPanel.set(areaLast.getComp());
        return lastPanel;
    }
    
    @Override
    public String checkForm() {
        if (StringUtils.isBlank(nameInput.getValue())) {
            return LangMgr.sLang(CsL.PLEASE_FILL_NAME);
        }
        if (StringUtils.isBlank(locationInput.getValue())) {
            return LangMgr.sLang(CsL.PLEASE_ENTER_LOCATION);
        }
        return null;
    }
    
    @Override
    public String getSql() {
        String name = SqlStrUtil.rmQuote4Sel(nameInput.getValue(),dbType, vers);
        PgTableSpaceBean bean = new PgTableSpaceBean(name,userSelectBox.getValue(),locationInput.getValue());
        List<String> list = TabSpaceSqlUtil.getCreateSql(dbType,vers,bean,descriptionInput.getValue());
        return StringUtils.join(list, ";\n");
    }
    
}
