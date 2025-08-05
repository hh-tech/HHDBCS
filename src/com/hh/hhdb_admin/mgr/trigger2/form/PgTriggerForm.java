package com.hh.hhdb_admin.mgr.trigger2.form;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.TriggerSqlUtil;
import com.hh.frame.db_sql.info.SqlTriggerBean;
import com.hh.frame.db_sql.pg.PgSchemaUtil2;
import com.hh.frame.db_sql.pg.info.PgFunProcInfo;
import com.hh.frame.db_sql.pg.info.PgTriggerBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.*;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class PgTriggerForm extends TriggerForm {
    private String tableName;
    
    private SelectBox foreachBox;
    private CheckGroupInput checkGroupInput;
    private TextInput whenInput;
    protected SearchSelectBox schemaBox;
    protected SelectBox functionBox;
    private TextInput paramInput;
    

    public PgTriggerForm(Connection conn, JdbcBean jdbc, String schemaName, String tableName) throws Exception {
        super(conn, jdbc, schemaName);
        this.tableName = tableName;
    
        enableInput = new CheckBoxInput("enable");
        enableInput.setValue("true");
    
        objectNameInput.setValue(this.tableName);
        objectNameInput.setEnabled(false);
    
        triggerBox = new SelectBox("trigger");
        triggerBox.addOption(SqlTriggerBean.TriggerFire.BEFORE.fireKey, SqlTriggerBean.TriggerFire.BEFORE.name());
        triggerBox.addOption(SqlTriggerBean.TriggerFire.AFTER.fireKey, SqlTriggerBean.TriggerFire.AFTER.name());
    
        foreachBox = new SelectBox("foreach");
        foreachBox.addOption(SqlTriggerBean.TriggerFor.ROW.name(), SqlTriggerBean.TriggerFor.ROW.name());
        foreachBox.addOption(SqlTriggerBean.TriggerFor.STATEMENT.name(), SqlTriggerBean.TriggerFor.STATEMENT.name());
    
        checkGroupInput = new CheckGroupInput("event", new HPanel(new HDivLayout(GridSplitEnum.C4, GridSplitEnum.C4, GridSplitEnum.C4)));
        checkGroupInput.add(SqlTriggerBean.TriggerEvent.INSERT.name(), SqlTriggerBean.TriggerEvent.INSERT.name());
        checkGroupInput.add(SqlTriggerBean.TriggerEvent.UPDATE.name(), SqlTriggerBean.TriggerEvent.UPDATE.name());
        checkGroupInput.add(SqlTriggerBean.TriggerEvent.DELETE.name(), SqlTriggerBean.TriggerEvent.DELETE.name());
        checkGroupInput.setValue(SqlTriggerBean.TriggerEvent.INSERT.name());
        whenInput = new TextInput("when");
        functionBox = new SelectBox("function");
        schemaBox = new SearchSelectBox(){
            @Override
            protected void onValueChanged(String value) {
                functionBox.removeAllItems();
                setTriggerFunction();
            }
        };
        List<String> list = SqlWinUtil.getSchemaNameList(dbType, version, conn);
        for (String s : list) {
            schemaBox.addOption(s, s);
        }
        schemaBox.setValue(schemaName);
        paramInput = new TextInput("param");
    }
    
    @Override
    public LastPanel getLastPanel() {
        HPanel hp = new HPanel(new HDivLayout(GridSplitEnum.C4));
        hp.add(new LabelInput(LangMgr.lang(Word3.FUNCTION_NAME2)+" ："), functionBox);
        HPanel searchPanel = new HPanel(new HDivLayout(GridSplitEnum.C4,GridSplitEnum.C4));
        searchPanel.add(new LabelInput(LangMgr.lang(Word2.SCHEMA)+" ："), schemaBox,hp);
    
        HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
        layout.setRightWidth(100);
        HPanel panel = new HPanel(layout);
        panel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
        panel.add(new HeightComp(5));
        panel.add(getLabelInput(LangMgr.lang(Word2.TABLE_NAME)+" ：", objectNameInput));
        panel.add(getLabelInput(LangMgr.sLang(CsL.TRIGGER)+" ：", triggerBox));
        panel.add(getLabelInput(LangMgr.sLang(CsL.FOR_EACH)+" ：", foreachBox));
        panel.add(getLabelInput(LangMgr.lang(Word2.INCIDENT)+" ：", checkGroupInput));
        panel.add(getLabelInput(LangMgr.lang(Word1.WHEN)+" ：", whenInput));
        panel.add(searchPanel);
        panel.add(getLabelInput(LangMgr.lang(Word2.PARAMETER)+" ：", paramInput));
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
        String sql = TriggerSqlUtil.getCreateSql(dbType,version,getTrigBean());
        return StringUtils.isNotBlank(sql) ? sql : "";
    }

    protected PgTriggerBean getTrigBean() {
        PgTriggerBean trigger = new PgTriggerBean(nameInput.getValue());
        trigger.setEnable(enableInput.isChecked());
        trigger.setOwner(schemaName);
        trigger.setObjName(objectNameInput.getValue());
        trigger.setTriggerFor(SqlTriggerBean.TriggerFor.valueOf(foreachBox.getValue()));
        trigger.setTriggerFire(SqlTriggerBean.TriggerFire.valueOf(triggerBox.getValue()));
        trigger.setWhen(whenInput.getValue());
        trigger.setFunOwner(schemaBox.getValue());
        trigger.setFunName(functionBox.getValue());
        trigger.setParam(paramInput.getValue());
        List<SqlTriggerBean.TriggerEvent> list = new LinkedList<>();
        for (String value : checkGroupInput.getValues()) {
            list.add(SqlTriggerBean.TriggerEvent.valueOf(value));
        }
        trigger.setEvent(list);
        return trigger;
    }
    
    protected void setTriggerFunction() {
        try {
            String sql = PgSchemaUtil2.getAllFunInfo(null,schemaBox.getValue(), PgFunProcInfo.class);
            SqlQueryUtil.selectStrMapList(conn, sql, PgFunProcInfo.class);
            List<Map<PgFunProcInfo, String>> list = SqlQueryUtil.selectStrMapList(conn, sql, PgFunProcInfo.class);
            for (Map<PgFunProcInfo, String> map : list) {
                if ("trigger".equalsIgnoreCase(map.get(PgFunProcInfo.RETURN))) functionBox.addOption(map.get(PgFunProcInfo.NAME), map.get(PgFunProcInfo.NAME));
            }
        } catch (SQLException e) {
            PopDlgUtil.logErr(getClass(),schemaBox.getComp(), e);
        }
    }
}
