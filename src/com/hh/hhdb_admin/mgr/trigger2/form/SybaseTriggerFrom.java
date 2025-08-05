package com.hh.hhdb_admin.mgr.trigger2.form;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.db_sql.TriggerSqlUtil;
import com.hh.frame.db_sql.info.SqlTriggerBean;
import com.hh.frame.db_sql.sybase.info.SybaseTriggerBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.CheckGroupInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SearchSelectBox;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;


public class SybaseTriggerFrom extends TriggerForm {
    private SelectBox typeBox;
    protected SearchSelectBox schemaBox;
    protected SelectBox foreachBox;
    protected CheckGroupInput checkGroupInput;

    public SybaseTriggerFrom(Connection conn, JdbcBean jdbc, String schemaName) throws Exception {
        super(conn, jdbc, schemaName);
    
        schemaBox = new SearchSelectBox();
        for (String s : SqlWinUtil.getSchemaNameList(dbType, version, conn)) {
            schemaBox.addOption(s, s);
        }
        schemaBox.setValue(schemaName);
    
        foreachBox = new SelectBox("foreach");
    
        triggerBox = new SelectBox("trigger") {
            @Override
            protected void onItemChange(ItemEvent e) {
            	if (getValue().equalsIgnoreCase(SqlTriggerBean.TriggerFire.BEFORE.name())) {
            		foreachBox.addOption(SqlTriggerBean.TriggerFor.ROW.name(), SqlTriggerBean.TriggerFor.ROW.name());
				} else {
					foreachBox.addOption(SqlTriggerBean.TriggerFor.ROW.name(), SqlTriggerBean.TriggerFor.ROW.name());
			        foreachBox.addOption(SqlTriggerBean.TriggerFor.STATEMENT.name(), SqlTriggerBean.TriggerFor.STATEMENT.name());
				}
            }
        };
        triggerBox.addOption(SqlTriggerBean.TriggerFire.BEFORE.fireKey, SqlTriggerBean.TriggerFire.BEFORE.name());
        triggerBox.addOption(SqlTriggerBean.TriggerFire.AFTER.fireKey, SqlTriggerBean.TriggerFire.AFTER.name());
        
        typeBox = new SelectBox("type");
        typeBox.addOption(TreeMrType.TABLE.name(), TreeMrType.TABLE.name());
      
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
    
    protected SybaseTriggerBean getTrigBean() {
    	SybaseTriggerBean trigger = new SybaseTriggerBean(nameInput.getValue());
        trigger.setOwner(schemaName);
        trigger.setObjectOwner(schemaBox.getValue());
        trigger.setObjName(objectNameInput.getValue());
        trigger.setTriggerFire(triggerBox.isEnabled() ? SqlTriggerBean.TriggerFire.valueOf(triggerBox.getValue()) : SqlTriggerBean.TriggerFire.INSTEAD_OF);
        if (foreachBox.isEnabled()) trigger.setTriggerFor(SqlTriggerBean.TriggerFor.valueOf(foreachBox.getValue()));
        List<SqlTriggerBean.TriggerEvent> list = new LinkedList<>();
        for (String value : checkGroupInput.getValues()) {
            list.add(SqlTriggerBean.TriggerEvent.valueOf(value));
        }
        trigger.setEvent(list);
        return trigger;
    }
}
