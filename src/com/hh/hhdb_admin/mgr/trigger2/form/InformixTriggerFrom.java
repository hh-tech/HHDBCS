package com.hh.hhdb_admin.mgr.trigger2.form;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.db_sql.TriggerSqlUtil;
import com.hh.frame.db_sql.info.SqlTriggerBean;
import com.hh.frame.db_sql.informix.info.InformixTriggerBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.*;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;


public class InformixTriggerFrom extends TriggerForm {
    private SelectBox typeBox;
    protected SearchSelectBox schemaBox;
    protected CheckGroupInput triggerInput;
    protected SelectBox triggerBox;

    public InformixTriggerFrom(Connection conn, JdbcBean jdbc, String schemaName) throws Exception {
        super(conn, jdbc, schemaName);
    
        enableInput = new CheckBoxInput("enable");
        enableInput.setValue("true");
        
        schemaBox = new SearchSelectBox();
        for (String s : SqlWinUtil.getSchemaNameList(dbType, version, conn)) {
            schemaBox.addOption(s, s);
        }
        schemaBox.setValue(schemaName);
    
        triggerInput = new CheckGroupInput("triggerInput", new HPanel(new HDivLayout(GridSplitEnum.C4, GridSplitEnum.C4, GridSplitEnum.C4)));
        triggerInput.add(SqlTriggerBean.TriggerFire.BEFORE.fireKey, SqlTriggerBean.TriggerFire.BEFORE.name());
        triggerInput.add(SqlTriggerBean.TriggerFire.FOR_EACH_ROW.name(), SqlTriggerBean.TriggerFire.FOR_EACH_ROW.fireKey);
        triggerInput.add(SqlTriggerBean.TriggerFire.AFTER.fireKey, SqlTriggerBean.TriggerFire.AFTER.name());
        
        typeBox = new SelectBox("type");
        typeBox.addOption(TreeMrType.TABLE.name(), TreeMrType.TABLE.name());
        typeBox.addOption(TreeMrType.VIEW.name(), TreeMrType.VIEW.name());
      
        triggerBox = new SelectBox("trigger");
        triggerBox.addOption(SqlTriggerBean.TriggerEvent.INSERT.name(), SqlTriggerBean.TriggerEvent.INSERT.name());
        triggerBox.addOption(SqlTriggerBean.TriggerEvent.UPDATE.name(), SqlTriggerBean.TriggerEvent.UPDATE.name());
        triggerBox.addOption(SqlTriggerBean.TriggerEvent.DELETE.name(), SqlTriggerBean.TriggerEvent.DELETE.name());
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
        panel.add(getLabelInput(LangMgr.lang(Word2.INCIDENT)+" ：", triggerBox));
        panel.add(getLabelInput(LangMgr.sLang(CsL.TRIGGER)+" ：", triggerInput));
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
    
    protected InformixTriggerBean getTrigBean() {
        InformixTriggerBean trigger = new InformixTriggerBean(nameInput.getValue());
        trigger.setOwner(schemaName);
        trigger.setObjectOwner(schemaBox.getValue());
        trigger.setObjName(objectNameInput.getValue());
        List<SqlTriggerBean.TriggerFire> list = new LinkedList<>();
        for (String value : triggerInput.getValues()) {
            list.add(SqlTriggerBean.TriggerFire.valueOf(value));
        }
        trigger.setTriggerFire(list);
        trigger.setEvent(SqlTriggerBean.TriggerEvent.valueOf(triggerBox.getValue()));
        trigger.setEnable(enableInput.isChecked());
        return trigger;
    }
}
