package com.hh.hhdb_admin.mgr.trigger.comp.form;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.triggerMr.base.*;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.mgr.trigger.TriggerUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

/**
 * @author YuSai
 */
public class HhPgForm extends TriggerForm {

    private CheckBoxInput enableInput;
    private TextInput whenInput;
    private TextInput paramInput;
    private TextInput commentInput;
    private SelectBox funSchemaBox;
    private SelectBox functionBox;
    private HPanel eventsPanel;
    private LastPanel eventPanel;
    private HTable table;
    private List<String> linkedColumns;

    public HhPgForm(HDialog dialog, Connection conn, JdbcBean jdbc, String schemaName, String tableName) {
        super(dialog, conn, jdbc, schemaName, tableName);
        this.linkedColumns = new ArrayList<>();
        initPanel();
    }

    private void initPanel() {
        typeBox.setEnabled(false);
        schemaBox.setEnabled(false);
        tableInput.setValue(tableName);
        tableInput.setEnabled(false);
        enableInput = new CheckBoxInput("enable");
        enableInput.setValue("true");
        Arrays.stream(TriggerFor.values()).forEach(t -> foreachBox.addOption(t.name(), t.name()));
        whenInput = new TextInput("when");
        paramInput = new TextInput("param");
        commentInput = new TextInput("comment");
        functionBox = new SelectBox("function");
        funSchemaBox = new SelectBox("funSchema") {
            @Override
            protected void onItemChange(ItemEvent event) {
                functionBox.removeAllItems();
                TriggerUtil.getFunctions(conn, funSchemaBox.getValue(),
                        HHdbPgsqlPrefixEnum.valueOf(dbType.name().substring(0, 2))).
                        forEach(m -> functionBox.addOption(m, m));
            }
        };
        TriggerUtil.getAllSchema(conn, TriggerUtil.getDb(conn)).forEach(m -> funSchemaBox.addOption(m, m));
        funSchemaBox.setValue(schemaName);
        HGridPanel funcGrid = new HGridPanel(new HGridLayout(GridSplitEnum.C2, GridSplitEnum.C5, GridSplitEnum.C5));
        funcGrid.setComp(1, new LabelInput(getLang("fun") + " : "));
        funcGrid.setComp(2, funSchemaBox);
        funcGrid.setComp(3, functionBox);
        eventsPanel = new HPanel(new HDivLayout(GridSplitEnum.C2, GridSplitEnum.C3, GridSplitEnum.C3, GridSplitEnum.C3));
        eventsPanel.add(new LabelInput(getLang("action") + " : "));
        CheckBoxInput checkBoxInput;
        for (TriggerEvent triggerEvent : TriggerEvent.values()) {
            checkBoxInput = new CheckBoxInput(triggerEvent.name());
            checkBoxInput.setId(triggerEvent.name());
            CheckBoxInput finalCheckBoxInput = checkBoxInput;
            checkBoxInput.addListen(e -> {
                if (Arrays.asList(DBTypeEnum.hhdb, DBTypeEnum.pgsql).contains(dbType)) {
                    if (finalCheckBoxInput.getId().equals(TriggerEvent.UPDATE.name())) {
                        if (finalCheckBoxInput.isChecked()) {
                            eventPanel.set(getTablePanel().getComp());
                        } else {
                            eventPanel.set(new LabelInput().getComp());
                        }
                    }
                }
            });
            checkBoxInput.setText(triggerEvent.name());
            eventsPanel.add(checkBoxInput);
        }
        eventPanel = new LastPanel();

        panel.add(getGridPanel(getLang("enable"), enableInput));
        panel.add(getGridPanel(getLang("name"), nameInput));
        panel.add(getGridPanel(getLang("type"), typeBox));
        panel.add(getGridPanel(getLang("schema"), schemaBox));
        panel.add(getGridPanel(getLang("table"), tableInput));
        panel.add(getGridPanel(getLang("foreach"), foreachBox));
        panel.add(getGridPanel(getLang("trigger"), triggerBox));
        panel.add(eventsPanel);
        panel.add(funcGrid);
        panel.add(getGridPanel(getLang("param"), paramInput));
        panel.add(getGridPanel(getLang("when"), whenInput));
        panel.add(getGridPanel(getLang("comment"), commentInput));
        panel.setLastPanel(eventPanel);
    }

    @Override
    public void initFormData(String triggerName) {
        try {
            if (Arrays.asList(DBTypeEnum.hhdb, DBTypeEnum.pgsql).contains(dbType)) {
                isUpdate = true;
                nameInput.setEnabled(false);
                oldTrigger = triggerMr.getTriggerInfo(conn, triggerName, this.tableName, this.schemaName);
                nameInput.setValue(oldTrigger.getName());
                typeBox.setValue(oldTrigger.getType().name());
                schemaBox.setValue(oldTrigger.getSchemaName());
                tableInput.setValue(oldTrigger.getTableName());
                foreachBox.setValue(oldTrigger.getForEach().name());
                triggerBox.setValue(oldTrigger.getFire().name());
                for (TriggerEvent event : oldTrigger.getEvent()) {
                    ((CheckBoxInput) eventsPanel.getHComp(event.name())).setValue(Boolean.TRUE.toString());
                    if (event == TriggerEvent.UPDATE) {
                        eventPanel.set(getTablePanel().getComp());
                    }
                }
                funSchemaBox.setValue(oldTrigger.getFunSchema());
                functionBox.setValue(oldTrigger.getFunName());
                paramInput.setValue(oldTrigger.getFunParam());
                whenInput.setValue(oldTrigger.getFunWhen());
                if (commentInput != null) {
                    commentInput.setValue(oldTrigger.getComment());
                }
                enableInput.setValue(String.valueOf(oldTrigger.isEnable()));
                linkedColumns = oldTrigger.getColumns();
                if (!linkedColumns.isEmpty()) {
                    table.load(getColumnData(), 1);
                }
                oldTrigger.setName(DbCmdStrUtil.toDbCmdStr(oldTrigger.getName(), dbType));
                oldTrigger.setSchemaName(DbCmdStrUtil.toDbCmdStr(oldTrigger.getSchemaName(), dbType));
                oldTrigger.setTableName(DbCmdStrUtil.toDbCmdStr(oldTrigger.getTableName(), dbType));
                oldTrigger.setFunName(DbCmdStrUtil.toDbCmdStr(oldTrigger.getFunName(), dbType));
                oldTrigger.setFunSchema(DbCmdStrUtil.toDbCmdStr(oldTrigger.getFunSchema(), dbType));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
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
        List<String> newColumns = new ArrayList<>();
        if (table != null) {
            List<HTabRowBean> beans = table.getRowBeans(RowStatus.UPDATE);
            if (isUpdate) {
                newColumns.addAll(linkedColumns);
                for (HTabRowBean bean : beans) {
                    if (bean.getCurrRow().get("select").equals(Boolean.TRUE.toString())) {
                        if (!newColumns.contains(bean.getOldRow().get("column"))) {
                            newColumns.add(bean.getOldRow().get("column"));
                        }
                    } else if (bean.getCurrRow().get("select").equals(Boolean.FALSE.toString())) {
                        newColumns.remove(bean.getOldRow().get("column"));
                    }
                }
            } else {
                for (HTabRowBean bean : beans) {
                    if (bean.getCurrRow().get("select").equals(Boolean.TRUE.toString())) {
                        newColumns.add(bean.getOldRow().get("column"));
                    }
                }
            }
        }
        trigger.setColumns(newColumns);
        trigger.setFunSchema(DbCmdStrUtil.toDbCmdStr(funSchemaBox.getValue(), dbType));
        trigger.setFunName(DbCmdStrUtil.toDbCmdStr(functionBox.getValue(), dbType));
        trigger.setFunWhen(whenInput.getValue());
        trigger.setFunParam(paramInput.getValue());
        trigger.setComment(commentInput.getValue());
        trigger.setEnable(Boolean.parseBoolean(enableInput.getValue()));
        trigger.setEvent(getEvents());
        if (!isUpdate) {
            return triggerMr.getTriggerCreateSql(trigger);
        } else {
            trigger.setName(DbCmdStrUtil.toDbCmdStr(trigger.getName(), dbType));
            return triggerMr.getTriggerUpdateSql(trigger, oldTrigger);
        }
    }

    private HGridPanel getTablePanel() {
        table = new HTable();
        DataCol colCol = new DataCol("column", getLang("COLUMN_NAME"));
        BoolCol selCol = new BoolCol("select", getLang("select"));
        selCol.setWidth(100);
        table.addCols(colCol, selCol);
        table.setRowHeight(30);
        try {
            table.load(getColumnData(), 1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        JScrollPane scrollPane = new JScrollPane(table.getComp());
        scrollPane.setPreferredSize(new Dimension(scrollPane.getWidth(), 220));
        AbsHComp tableComp = new AbsHComp() {
            @Override
            public Component getComp() {
                return scrollPane;
            }
        };
        HPanel panel = new HPanel();
        panel.add(tableComp);
        HGridPanel gridPanel = new HGridPanel(new HGridLayout(GridSplitEnum.C2));
        gridPanel.setComp(1, new LabelInput(getLang("column") + " : "));
        gridPanel.setComp(2, panel);
        return gridPanel;
    }

    private List<Map<String, String>> getColumnData() throws SQLException {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (String column : TriggerUtil.getColumns(conn, schemaName, tableName)) {
            Map<String, String> map = new HashMap<>();
            map.put("column", column);
            if (linkedColumns.contains(column)) {
                map.put("select", Boolean.TRUE.toString());
            } else {
                map.put("select", Boolean.FALSE.toString());
            }
            mapList.add(map);
        }
        return mapList;
    }

    private List<TriggerEvent> getEvents() {
        List<TriggerEvent> events = new ArrayList<>();
        List<AbsHComp> comps = eventsPanel.getSubCompList();
        for (AbsHComp comp : comps) {
            if (comp instanceof CheckBoxInput) {
                String value = ((CheckBoxInput) comp).getValue();
                if (value.equalsIgnoreCase(Boolean.TRUE.toString())) {
                    events.add(TriggerEvent.valueOf(comp.getId()));
                }
            }
        }
        return events;
    }

}
