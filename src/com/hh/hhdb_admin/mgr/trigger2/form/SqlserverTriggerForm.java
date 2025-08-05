package com.hh.hhdb_admin.mgr.trigger2.form;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.db_sql.TriggerSqlUtil;
import com.hh.frame.db_sql.info.SqlTriggerBean;
import com.hh.frame.db_sql.sqlserver.info.SqlServerTriggerBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.CheckGroupInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;


public class SqlserverTriggerForm extends TriggerForm {
    private SelectBox typeBox;
    private CheckGroupInput checkGroupInput;

    public SqlserverTriggerForm(Connection conn, JdbcBean jdbc, String schemaName) throws Exception {
        super(conn, jdbc, schemaName);
    
        enableInput = new CheckBoxInput("enable");
        enableInput.setValue("true");
        
        triggerBox = new SelectBox("trigger");
        triggerBox.addOption(SqlTriggerBean.TriggerFire.AFTER.fireKey, SqlTriggerBean.TriggerFire.AFTER.name());
        triggerBox.addOption(SqlTriggerBean.TriggerFire.INSTEAD_OF.fireKey, SqlTriggerBean.TriggerFire.INSTEAD_OF.name());
    
        typeBox = new SelectBox("type") {
            @Override
            protected void onItemChange(ItemEvent e) {
                triggerBox.setEnabled(typeBox.getValue().equalsIgnoreCase(TreeMrType.TABLE.name()));
            }
        };
        typeBox.addOption(TreeMrType.TABLE.name(), TreeMrType.TABLE.name());
        typeBox.addOption(TreeMrType.VIEW.name(), TreeMrType.VIEW.name());
    
        checkGroupInput = new CheckGroupInput("event", new HPanel(new HDivLayout(GridSplitEnum.C4, GridSplitEnum.C4, GridSplitEnum.C4)));
        checkGroupInput.add(SqlTriggerBean.TriggerEvent.INSERT.name(), SqlTriggerBean.TriggerEvent.INSERT.name());
        checkGroupInput.add(SqlTriggerBean.TriggerEvent.UPDATE.name(), SqlTriggerBean.TriggerEvent.UPDATE.name());
        checkGroupInput.add(SqlTriggerBean.TriggerEvent.DELETE.name(), SqlTriggerBean.TriggerEvent.DELETE.name());
    }
    
    @Override
    public LastPanel getLastPanel() {
        HButton searchBtn = new HButton(LangMgr.lang(Word2.QUERY),FaSolidEnum.MAGNIFYING_GLASS_CHART) {
            @Override
            protected void onClick() {
                showTable(schemaName,typeBox.getValue());
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
        panel.add(searchPanel);
        panel.add(getLabelInput(LangMgr.sLang(CsL.TRIGGER)+" ：", triggerBox));
        panel.add(getLabelInput(LangMgr.lang(Word2.INCIDENT)+" ：", checkGroupInput));
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
    
    protected SqlServerTriggerBean getTrigBean() {
        SqlServerTriggerBean trigger = new SqlServerTriggerBean(nameInput.getValue());
        trigger.setOwner(schemaName);
        trigger.setObjName(objectNameInput.getValue());
        trigger.setTriggerFire(triggerBox.isEnabled() ? SqlTriggerBean.TriggerFire.valueOf(triggerBox.getValue()) : SqlTriggerBean.TriggerFire.INSTEAD_OF);
        List<SqlTriggerBean.TriggerEvent> list = new LinkedList<>();
        for (String value : checkGroupInput.getValues()) {
            list.add(SqlTriggerBean.TriggerEvent.valueOf(value));
        }
        trigger.setEvent(list);
        trigger.setEnable(enableInput.isChecked());
        return trigger;
    }
}
