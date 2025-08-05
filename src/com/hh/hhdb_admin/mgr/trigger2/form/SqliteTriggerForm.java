package com.hh.hhdb_admin.mgr.trigger2.form;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.db_sql.TriggerSqlUtil;
import com.hh.frame.db_sql.info.SqlTriggerBean;
import com.hh.frame.db_sql.sqlite.info.SqliteTriggerBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.RadioGroupInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.ItemEvent;
import java.sql.Connection;

public class SqliteTriggerForm extends TriggerForm {
    private SelectBox typeBox;
    private RadioGroupInput checkGroupInput;
    private TextInput whenInput;
    
    
    public SqliteTriggerForm(Connection conn, JdbcBean jdbc, String schemaName) throws Exception {
        super(conn, jdbc, schemaName);
        
        triggerBox = new SelectBox("trigger");
        
        checkGroupInput = new RadioGroupInput("event", new HPanel(new HDivLayout(GridSplitEnum.C4, GridSplitEnum.C4)));
        checkGroupInput.add(SqlTriggerBean.TriggerEvent.INSERT.name(), SqlTriggerBean.TriggerEvent.INSERT.name());
        checkGroupInput.add(SqlTriggerBean.TriggerEvent.UPDATE.name(), SqlTriggerBean.TriggerEvent.UPDATE.name());
        checkGroupInput.add(SqlTriggerBean.TriggerEvent.DELETE.name(), SqlTriggerBean.TriggerEvent.DELETE.name());
        checkGroupInput.setSelected(SqlTriggerBean.TriggerEvent.INSERT.name());
        
        whenInput = new TextInput("when");
        
        typeBox = new SelectBox("type") {
            @Override
            protected void onItemChange(ItemEvent e) {
                triggerBox.removeAllItems();
                if (getValue().equals(TreeMrType.VIEW.name())) {
                    triggerBox.addOption(SqlTriggerBean.TriggerFire.INSTEAD_OF.fireKey, SqlTriggerBean.TriggerFire.INSTEAD_OF.name());
                } else {
                    triggerBox.addOption(SqlTriggerBean.TriggerFire.BEFORE.fireKey, SqlTriggerBean.TriggerFire.BEFORE.name());
                    triggerBox.addOption(SqlTriggerBean.TriggerFire.AFTER.fireKey, SqlTriggerBean.TriggerFire.AFTER.name());
                }
            }
        };
        typeBox.addOption(TreeMrType.TABLE.name(), TreeMrType.TABLE.name());
        typeBox.addOption(TreeMrType.VIEW.name(), TreeMrType.VIEW.name());
    }
    
    @Override
    public LastPanel getLastPanel() {
        HButton searchBtn = new HButton(LangMgr.lang(Word2.QUERY), FaSolidEnum.MAGNIFYING_GLASS_CHART) {
            @Override
            protected void onClick() {
                showTable(schemaName, typeBox.getValue());
            }
        };
        HPanel searchPanel = new HPanel(new HDivLayout(GridSplitEnum.C4, GridSplitEnum.C6));
        searchPanel.add(new LabelInput(LangMgr.lang(Word4.OBJECT_NAME) + " ："), objectNameInput, searchBtn);
        
        HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
        layout.setRightWidth(100);
        HPanel panel = new HPanel(layout);
        panel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
        panel.add(new HeightComp(5));
        panel.add(getLabelInput(LangMgr.lang(Word2.TYPE) + " ：", typeBox));
        panel.add(searchPanel);
        panel.add(getLabelInput(LangMgr.sLang(CsL.TRIGGER) + " ：", triggerBox));
        panel.add(getLabelInput(LangMgr.lang(Word1.WHEN) + " ：", whenInput));
        panel.add(getLabelInput(LangMgr.lang(Word2.INCIDENT) + " ：", checkGroupInput));
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
        String sql = TriggerSqlUtil.getCreateSql(dbType, version, getTrigBean());
        return StringUtils.isNotBlank(sql) ? sql : "";
    }
    
    protected SqliteTriggerBean getTrigBean() {
        SqliteTriggerBean trigger = new SqliteTriggerBean(nameInput.getValue());
        trigger.setOwner(schemaName);
        trigger.setObjName(objectNameInput.getValue());
        trigger.setTriggerFire(SqlTriggerBean.TriggerFire.valueOf(triggerBox.getValue()));
        trigger.setWhen(whenInput.getValue());
        trigger.setEvent(SqlTriggerBean.TriggerEvent.valueOf(checkGroupInput.getValue()));
        return trigger;
    }
    
}
