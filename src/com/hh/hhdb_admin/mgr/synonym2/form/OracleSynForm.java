package com.hh.hhdb_admin.mgr.synonym2.form;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.SynonymSqlUtil;
import com.hh.frame.db_sql.ora.OraSchemaSqlUtil2;
import com.hh.frame.db_sql.ora.info.OraSynonymBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SearchSelectBox;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.List;


public class OracleSynForm extends SynBaseForm {
    private SelectBox dblinkBox;
    
    public OracleSynForm(Connection conn, JdbcBean jdbcBean, String schema) throws Exception {
        super(jdbcBean,conn,schema);
        init();
    }

    protected void init() throws Exception {
        publicInput = new CheckBoxInput("public");
    
        objectOwnerBox = new SearchSelectBox();
        List<String> users = SqlWinUtil.getSchemaNameList(dbTypeEnum,version,conn);
        users.forEach(user -> objectOwnerBox.addOption(user, user));
        objectOwnerBox.setValue(schema);
    
        dblinkBox = new SelectBox("dblink");
        dblinkBox.addOption("", "");
        List<String> lis = SqlQueryUtil.selectOneColumn(conn, OraSchemaSqlUtil2.getSelDbLinkNamesSql(version, schema,false));
        lis.forEach(a -> dblinkBox.addOption(a, a));
    }
    
    @Override
    public LastPanel getLastPanel() {
        HButton searchBtn = new HButton(LangMgr.lang(Word2.QUERY),FaSolidEnum.MAGNIFYING_GLASS_CHART) {
            @Override
            protected void onClick() {
                showTable();
            }
        };
        HPanel loginSetPanel = new HPanel(new HDivLayout(GridSplitEnum.C4,GridSplitEnum.C6));
        loginSetPanel.add(new LabelInput(LangMgr.lang(Word4.OBJECT_NAME)+" ："),objectNameInput,searchBtn);
    
        HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
        layout.setRightWidth(100);
        HPanel panel = new HPanel(layout);
        panel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
        panel.add(new HeightComp(5));
        panel.add(getLabelInput(LangMgr.sLang(CsL.OBJECT_OWNER)+" ：", objectOwnerBox));
        panel.add(getLabelInput(LangMgr.lang(Word2.TYPE)+" ：", typeBox));
        panel.add(loginSetPanel);
        if (null != dblinkBox) panel.add(getLabelInput(LangMgr.sLang(CsL.DB_LINK)+" ：", dblinkBox));
        panel.add(new HeightComp(5));
        
        LastPanel areaLast = new LastPanel();
        areaLast.setTitle(LangMgr.lang(Word2.CODE));
        areaLast.set(area.getComp());
    
        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(panel.getComp());
        lastPanel.set(areaLast.getComp());
        return lastPanel;
    }
    
    @Override
    public String getSql() {
        String sql = SynonymSqlUtil.getCreateSql(dbTypeEnum,version,getUserBean());
        return StringUtils.isNotBlank(sql) ? sql : "";
    }
    
    @Override
    public String checkForm() {
        if (StringUtils.isBlank(nameInput.getValue())) {
            return LangMgr.sLang(CsL.PLEASE_FILL_NAME);
        }
        return null;
    }
    
    protected OraSynonymBean getUserBean() {
        OraSynonymBean synBean = new OraSynonymBean(nameInput.getValue());
        synBean.setOwner(schema);
        synBean.setObjectName(objectNameInput.getValue());
        synBean.setObjectOwner(objectOwnerBox.getValue());
        if (null != dblinkBox) {
            String str = dblinkBox.getValue();
            if (str.startsWith("PUBLIC.") || str.startsWith(schema+".")) {
                synBean.setDblink(str.split("\\.")[1]);
            } else {
                synBean.setDblink(str);
            }
        }
        if (null != publicInput) synBean.setIsPublic(publicInput.isChecked());
        return synBean;
    }
}
