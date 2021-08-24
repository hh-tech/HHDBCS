package com.hh.hhdb_admin.mgr.trigger.comp.form;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.triggerMr.base.*;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.RadioGroupInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author YuSai
 */
public class Db2Form extends TriggerForm {

    private RadioGroupInput eventInput;

    public Db2Form(HDialog dialog, Connection conn, JdbcBean jdbc, String schemaName, String tableName) {
        super(dialog, conn, jdbc, schemaName, tableName);
        initPanel();
    }

    private void initPanel() {
        HGridPanel tableGrid = new HGridPanel(new HGridLayout(GridSplitEnum.C2, GridSplitEnum.C8, GridSplitEnum.C2));
        tableGrid.setComp(1, new LabelInput(getLang("table") + " : "));
        tableGrid.setComp(2, tableInput);
        HButton searchBtn = new HButton(getLang("search")) {
            @Override
            protected void onClick() {
                showTable(typeBox.getValue(), schemaBox.getValue(), tableInput);
            }
        };
        searchBtn.setIcon(IconFileUtil.getIcon(new IconBean(CsMgrEnum.TRIGGER.name(), "query", IconSizeEnum.SIZE_16)));
        tableGrid.setComp(3, searchBtn);
        Arrays.stream(TriggerFor.values()).forEach(t -> foreachBox.addOption(t.name(), t.name()));
        eventInput = new RadioGroupInput("event", new HPanel(new HDivLayout(GridSplitEnum.C4, GridSplitEnum.C4, GridSplitEnum.C4)));
        Arrays.stream(TriggerEvent.values()).forEach(triggerEvent -> eventInput.add(triggerEvent.name(), triggerEvent.name()));
        panel.add(getGridPanel(getLang("name"), nameInput));
        panel.add(getGridPanel(getLang("type"), typeBox));
        panel.add(getGridPanel(getLang("schema"), schemaBox));
        panel.add(tableGrid);
        panel.add(getGridPanel(getLang("foreach"), foreachBox));
        panel.add(getGridPanel(getLang("trigger"), triggerBox));
        panel.add(getGridPanel(getLang("action"), eventInput));
    }

    @Override
    public String getSql() {
        Trigger trigger = new Trigger();
        trigger.setName(nameInput.getValue());
        trigger.setType(TriggerType.valueOf(typeBox.getValue()));
        trigger.setSchemaName(DbCmdStrUtil.toDbCmdStr(schemaBox.getValue(), dbType));
        trigger.setTableName(DbCmdStrUtil.toDbCmdStr(tableInput.getValue(), dbType));
        trigger.setFire(TriggerFire.valueOf(triggerBox.getValue()));
        trigger.setForEach(TriggerFor.valueOf(foreachBox.getValue()));
        if (eventInput.getValue() != null) {
            trigger.setEvent(Collections.singletonList(TriggerEvent.valueOf(eventInput.getValue())));
        } else {
            trigger.setEvent(new ArrayList<>());
        }
        return triggerMr.getTriggerCreateSql(trigger);
    }

}
