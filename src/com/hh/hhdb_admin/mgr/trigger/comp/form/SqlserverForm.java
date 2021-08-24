package com.hh.hhdb_admin.mgr.trigger.comp.form;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.triggerMr.base.Trigger;
import com.hh.frame.create_dbobj.triggerMr.base.TriggerEvent;
import com.hh.frame.create_dbobj.triggerMr.base.TriggerFire;
import com.hh.frame.create_dbobj.triggerMr.base.TriggerType;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.CheckGroupInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;

import java.sql.Connection;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author YuSai
 */
public class SqlserverForm extends TriggerForm {

    private CheckGroupInput checkGroupInput;

    public SqlserverForm(HDialog dialog, Connection conn, JdbcBean jdbc, String schemaName, String tableName) {
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
        HPanel eventPanel = new HPanel(new HDivLayout(GridSplitEnum.C4, GridSplitEnum.C4, GridSplitEnum.C4));
        checkGroupInput = new CheckGroupInput("event", eventPanel);
        Arrays.stream(TriggerEvent.values()).forEach(triggerEvent -> checkGroupInput.add(triggerEvent.name(), triggerEvent.name()));

        panel.add(getGridPanel(getLang("name"), nameInput));
        panel.add(getGridPanel(getLang("type"), typeBox));
        panel.add(getGridPanel(getLang("schema"), schemaBox));
        panel.add(tableGrid);
        panel.add(getGridPanel(getLang("trigger"), triggerBox));
        panel.add(getGridPanel(getLang("action"), checkGroupInput));
    }

    @Override
    public String getSql() {
        Trigger trigger = new Trigger();
        trigger.setName(nameInput.getValue());
        trigger.setType(TriggerType.valueOf(typeBox.getValue()));
        trigger.setSchemaName(DbCmdStrUtil.toDbCmdStr(schemaBox.getValue(), dbType));
        trigger.setTableName(DbCmdStrUtil.toDbCmdStr(tableInput.getValue(), dbType));
        trigger.setFire(TriggerFire.valueOf(triggerBox.getValue()));
        trigger.setEvent(checkGroupInput.getValues().stream().map(TriggerEvent::valueOf).collect(Collectors.toList()));
        return triggerMr.getTriggerCreateSql(trigger);
    }

}
