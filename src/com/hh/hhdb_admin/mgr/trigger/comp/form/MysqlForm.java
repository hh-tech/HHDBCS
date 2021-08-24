package com.hh.hhdb_admin.mgr.trigger.comp.form;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.triggerMr.base.*;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.RadioGroupInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author YuSai
 */
public class MysqlForm extends TriggerForm {

    private RadioGroupInput eventInput;
    private TextAreaInput definitionInput;

    public MysqlForm(HDialog dialog, Connection conn, JdbcBean jdbc, String schemaName, String tableName) {
        super(dialog, conn, jdbc, schemaName, tableName);
        initPanel();
    }

    private void initPanel() {
        definitionInput = new TextAreaInput("definition");
        typeBox.setEnabled(false);
        schemaBox.setEnabled(false);
        tableInput.setValue(tableName);
        tableInput.setEnabled(false);
        eventInput = new RadioGroupInput("event", new HPanel(new HDivLayout(GridSplitEnum.C4, GridSplitEnum.C4, GridSplitEnum.C4)));
        Arrays.stream(TriggerEvent.values()).forEach(triggerEvent -> eventInput.add(triggerEvent.name(), triggerEvent.name()));
        foreachBox.addOption(TriggerFor.ROW.name(), TriggerFor.ROW.name());
        panel.add(getGridPanel(getLang("name"), nameInput));
        panel.add(getGridPanel(getLang("type"), typeBox));
        panel.add(getGridPanel(getLang("schema"), schemaBox));
        panel.add(getGridPanel(getLang("table"), tableInput));
        panel.add(getGridPanel(getLang("foreach"), foreachBox));
        panel.add(getGridPanel(getLang("trigger"), triggerBox));
        panel.add(getGridPanel(getLang("action"), eventInput));
        panel.add(getGridPanel(getLang("define"), definitionInput));
    }

    @Override
    public String getSql() {
        Trigger trigger = new Trigger();
        trigger.setName(nameInput.getValue());
        trigger.setType(TriggerType.valueOf(typeBox.getValue()));
        trigger.setSchemaName(DbCmdStrUtil.toDbCmdStr(schemaBox.getValue(), dbType));
        trigger.setTableName(DbCmdStrUtil.toDbCmdStr(tableInput.getValue(), dbType));
        trigger.setFire(TriggerFire.valueOf(triggerBox.getValue()));
        trigger.setDefine(definitionInput.getValue());
        trigger.setForEach(TriggerFor.valueOf(foreachBox.getValue()));
        if (eventInput.getValue() != null) {
            trigger.setEvent(Collections.singletonList(TriggerEvent.valueOf(eventInput.getValue())));
        } else {
            trigger.setEvent(new ArrayList<>());
        }
        return triggerMr.getTriggerCreateSql(trigger);
    }
}
