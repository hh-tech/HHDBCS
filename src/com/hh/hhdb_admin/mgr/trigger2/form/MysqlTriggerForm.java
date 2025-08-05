package com.hh.hhdb_admin.mgr.trigger2.form;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.db_sql.TriggerSqlUtil;
import com.hh.frame.db_sql.info.SqlTriggerBean;
import com.hh.frame.db_sql.mysql.info.MysqlTriggerBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.RadioGroupInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;


public class MysqlTriggerForm extends TriggerForm {
    private String tableName;

    private RadioGroupInput eventInput;

    public MysqlTriggerForm(Connection conn, JdbcBean jdbc, String schemaName, String tableName) throws Exception {
        super(conn, jdbc, schemaName);
        this.tableName = tableName;
    
        objectNameInput.setValue(this.tableName);
        objectNameInput.setEnabled(false);
        
        triggerBox = new SelectBox("trigger");
        triggerBox.addOption(SqlTriggerBean.TriggerFire.BEFORE.fireKey, SqlTriggerBean.TriggerFire.BEFORE.name());
        triggerBox.addOption(SqlTriggerBean.TriggerFire.AFTER.fireKey, SqlTriggerBean.TriggerFire.AFTER.name());
    
        eventInput = new RadioGroupInput("event", new HPanel(new HDivLayout(GridSplitEnum.C4, GridSplitEnum.C4, GridSplitEnum.C4)));
        eventInput.add(SqlTriggerBean.TriggerEvent.INSERT.name(), SqlTriggerBean.TriggerEvent.INSERT.name());
        eventInput.add(SqlTriggerBean.TriggerEvent.UPDATE.name(), SqlTriggerBean.TriggerEvent.UPDATE.name());
        eventInput.add(SqlTriggerBean.TriggerEvent.DELETE.name(), SqlTriggerBean.TriggerEvent.DELETE.name());
        eventInput.setSelected(SqlTriggerBean.TriggerEvent.INSERT.name());
    }

    
    @Override
    public LastPanel getLastPanel() {
        HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
        layout.setRightWidth(100);
        HPanel panel = new HPanel(layout);
        panel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
        panel.add(new HeightComp(5));
        panel.add(getLabelInput(LangMgr.lang(Word2.TABLE_NAME)+" ：", objectNameInput));
        panel.add(getLabelInput(LangMgr.sLang(CsL.TRIGGER)+" ：", triggerBox));
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
        String sql = TriggerSqlUtil.getCreateSql(dbType,version,getTrigBean());
        return StringUtils.isNotBlank(sql) ? sql : "";
    }
    
    protected MysqlTriggerBean getTrigBean() {
        MysqlTriggerBean trigger = new MysqlTriggerBean(nameInput.getValue());
        trigger.setOwner(schemaName);
        trigger.setObjName(objectNameInput.getValue());
        trigger.setTriggerFire(SqlTriggerBean.TriggerFire.valueOf(triggerBox.getValue()));
        trigger.setEvent(SqlTriggerBean.TriggerEvent.valueOf(eventInput.getValue()));
        return trigger;
    }
}
