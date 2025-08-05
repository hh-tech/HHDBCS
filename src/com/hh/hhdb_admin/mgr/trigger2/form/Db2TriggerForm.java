package com.hh.hhdb_admin.mgr.trigger2.form;

import java.awt.event.ItemEvent;
import java.sql.Connection;

import com.hh.frame.swingui.view.input.SearchSelectBox;
import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.db_sql.TriggerSqlUtil;
import com.hh.frame.db_sql.db2.info.Db2TriggerBean;
import com.hh.frame.db_sql.info.SqlTriggerBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.RadioGroupInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;


public class Db2TriggerForm extends TriggerForm {
    private SelectBox typeBox;
    private SearchSelectBox schemaBox;
    private SelectBox foreachBox;
    private RadioGroupInput eventInput;

    public Db2TriggerForm(Connection conn, JdbcBean jdbc, String schemaName) throws Exception {
        super(conn, jdbc, schemaName);
    
        schemaBox = new SearchSelectBox();
        for (String s : SqlWinUtil.getSchemaNameList(dbType, version, conn)) {
            schemaBox.addOption(s, s);
        }
        schemaBox.setValue(schemaName);
    
        foreachBox = new SelectBox("foreach");
        foreachBox.addOption(SqlTriggerBean.TriggerFor.ROW.name(), SqlTriggerBean.TriggerFor.ROW.name());
        foreachBox.addOption(SqlTriggerBean.TriggerFor.STATEMENT.name(), SqlTriggerBean.TriggerFor.STATEMENT.name());
    
        triggerBox = new SelectBox("trigger");
        triggerBox.addOption(SqlTriggerBean.TriggerFire.BEFORE.fireKey, SqlTriggerBean.TriggerFire.BEFORE.name());
        triggerBox.addOption(SqlTriggerBean.TriggerFire.AFTER.fireKey, SqlTriggerBean.TriggerFire.AFTER.name());
    
        typeBox = new SelectBox("type") {
            @Override
            protected void onItemChange(ItemEvent e) {
//                foreachBox.setEnabled(typeBox.getValue().equalsIgnoreCase(TriggerType.TABLE.name()));
                triggerBox.setEnabled(typeBox.getValue().equalsIgnoreCase(TreeMrType.TABLE.name()));
            }
        };
        typeBox.addOption(TreeMrType.TABLE.name(), TreeMrType.TABLE.name());
        typeBox.addOption(TreeMrType.VIEW.name(), TreeMrType.VIEW.name());
    
        eventInput = new RadioGroupInput("event", new HPanel(new HDivLayout(GridSplitEnum.C4, GridSplitEnum.C4, GridSplitEnum.C4)));
        eventInput.add(SqlTriggerBean.TriggerEvent.INSERT.name(), SqlTriggerBean.TriggerEvent.INSERT.name());
        eventInput.add(SqlTriggerBean.TriggerEvent.UPDATE.name(), SqlTriggerBean.TriggerEvent.UPDATE.name());
        eventInput.add(SqlTriggerBean.TriggerEvent.DELETE.name(), SqlTriggerBean.TriggerEvent.DELETE.name());
        eventInput.setSelected(SqlTriggerBean.TriggerEvent.INSERT.name());
    }

    @Override
    public LastPanel getLastPanel() {
        HButton searchBtn = new HButton(LangMgr.lang(Word2.QUERY),FaSolidEnum.MAGNIFYING_GLASS_CHART) {
            @Override
            protected void onClick() {
                showTable(schemaBox.getValue(),typeBox.getValue());
            }
        };
        HPanel searchPanel = new HPanel(new HDivLayout(GridSplitEnum.C4,GridSplitEnum.C6));
        searchPanel.add(new LabelInput(LangMgr.lang(Word4.OBJECT_NAME)+" ："), objectNameInput,searchBtn);
    
        HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
        layout.setRightWidth(100);
        HPanel panel = new HPanel(layout);
        panel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
        panel.add(new HeightComp(5));
        panel.add(getLabelInput(LangMgr.lang(Word2.TYPE)+" ：", typeBox));
        panel.add(getLabelInput(LangMgr.lang(Word2.SCHEMA)+" ：", schemaBox));
        panel.add(searchPanel);
        panel.add(getLabelInput(LangMgr.sLang(CsL.TRIGGER)+" ：", triggerBox));
        panel.add(getLabelInput(LangMgr.sLang(CsL.FOR_EACH)+" ：", foreachBox));
        panel.add(getLabelInput(LangMgr.lang(Word2.INCIDENT)+" ：", eventInput));
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
        String sql = TriggerSqlUtil.getCreateSql(dbType,jdbc.getCurrentVersion(),getTrigBean());
        return StringUtils.isNotBlank(sql) ? sql : "";
    }
    
    protected Db2TriggerBean getTrigBean() {
        Db2TriggerBean trigger = new Db2TriggerBean(nameInput.getValue());
        trigger.setOwner(schemaName);
        trigger.setObjectOwner(schemaBox.getValue());
        trigger.setObjName(objectNameInput.getValue());
        trigger.setTriggerFire(triggerBox.isEnabled() ? SqlTriggerBean.TriggerFire.valueOf(triggerBox.getValue()) : SqlTriggerBean.TriggerFire.INSTEAD_OF);
        trigger.setTriggerFor(SqlTriggerBean.TriggerFor.valueOf(foreachBox.getValue()));
        trigger.setEvent(SqlTriggerBean.TriggerEvent.valueOf(eventInput.getValue()));
        return trigger;
    }
}
