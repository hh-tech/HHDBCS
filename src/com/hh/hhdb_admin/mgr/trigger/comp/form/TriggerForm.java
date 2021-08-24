package com.hh.hhdb_admin.mgr.trigger.comp.form;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.SleepUtil;
import com.hh.frame.create_dbobj.obj_query.AbsObjQuery;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.create_dbobj.triggerMr.base.Trigger;
import com.hh.frame.create_dbobj.triggerMr.base.TriggerFire;
import com.hh.frame.create_dbobj.triggerMr.base.TriggerType;
import com.hh.frame.create_dbobj.triggerMr.mr.AbsTriggerMr;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginComp;
import com.hh.hhdb_admin.mgr.trigger.TriggerUtil;
import com.hh.hhdb_admin.mgr.trigger.comp.TriggerComp;

import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author YuSai
 */
public class TriggerForm {

    private HDialog dialog;
    protected Connection conn;
    private JdbcBean jdbc;
    protected String schemaName;
    protected String tableName;
    protected DBTypeEnum dbType;
    protected AbsTriggerMr triggerMr;
    protected HPanel panel;
    protected boolean isUpdate;
    protected Trigger oldTrigger;
    protected TextInput nameInput;
    protected SelectBox typeBox;
    protected SelectBox schemaBox;
    protected TextInput tableInput;
    protected SelectBox foreachBox;
    protected SelectBox triggerBox;

    public TriggerForm(HDialog dialog, Connection conn, JdbcBean jdbc, String schemaName, String tableName) {
        try {
            this.dialog = dialog;
            this.conn = conn;
            this.jdbc = jdbc;
            this.schemaName = schemaName;
            this.tableName = tableName;
            this.dbType = DriverUtil.getDbType(jdbc);
            if (dbType != null) {
                this.triggerMr = AbsTriggerMr.genTriggerMr(dbType);
            }
            init();
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
    }

    public boolean checkForm() {
        return true;
    }

    public String getSql() {
        return "";
    }

    public void initFormData(String triggerName) {

    }

    public HPanel getPanel() {
        return panel;
    }

    protected String getLang(String key) {
        return TriggerComp.getLang(key);
    }

    protected HGridPanel getGridPanel(String label, AbsInput absInput) {
        HGridPanel gridPanel = new HGridPanel(new HGridLayout(GridSplitEnum.C2));
        gridPanel.setComp(1, new LabelInput(label + " : "));
        gridPanel.setComp(2, absInput);
        return gridPanel;
    }

    protected void showTable(String type, String schema, TextInput tableInput) {
        HDialog tableDialog = new HDialog(this.dialog, 500, 500);
        tableDialog.setWindowTitle(getLang("select"));
        tableDialog.setIconImage(IconFileUtil.getLogo());
        HPanel panel = new HPanel();
        HTable table = new HTable();
        table.setCellEditable(false);
        table.getComp().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    List<HTabRowBean> rowBeans = table.getSelectedRowBeans();
                    if (rowBeans.size() == 0) {
                        PopPaneUtil.info(dialog.getWindow(), LoginComp.getLang("pleaseSelectOneData"));
                        return;
                    }
                    if (rowBeans.size() > 1) {
                        PopPaneUtil.info(dialog.getWindow(), LoginComp.getLang("onlySelectOne"));
                        return;
                    }
                    Map<String, String> map = rowBeans.get(0).getOldRow();
                    if (null != map) {
                        tableInput.setValue(map.get("name"));
                        tableDialog.dispose();
                    }
                }
            }
        });
        table.addCols(new DataCol("name", getLang("name")));
        SearchToolBar toolBar = new SearchToolBar(table);
        loadData(table, type, schema, tableInput.getValue());
        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(toolBar.getComp());
        lastPanel.setWithScroll(table.getComp());
        panel.setLastPanel(lastPanel);
        tableDialog.setRootPanel(panel);
        tableDialog.show();
    }

    private void init() {
        HDivLayout divLayout = new HDivLayout();
        divLayout.setTopHeight(5);
        divLayout.setyGap(5);
        panel = new HPanel(divLayout);
        panel.setTitle(getLang("triggerInfo"));
        nameInput = new TextInput("name");
        triggerBox = new SelectBox("trigger");
        typeBox = new SelectBox("type") {
            @Override
            protected void onItemChange(ItemEvent e) {
                triggerBox.removeAllItems();
                if (typeBox.getValue().equalsIgnoreCase(TriggerType.TABLE.name()) || dbType == DBTypeEnum.mysql) {
                    if (dbType == DBTypeEnum.sqlserver) {
                        triggerBox.addOption(TriggerFire.AFTER.fireKey, TriggerFire.AFTER.name());
                        triggerBox.addOption(TriggerFire.INSTEAD_OF.fireKey, TriggerFire.INSTEAD_OF.name());
                    } else {
                        triggerBox.addOption(TriggerFire.BEFORE.fireKey, TriggerFire.BEFORE.name());
                        triggerBox.addOption(TriggerFire.AFTER.fireKey, TriggerFire.AFTER.name());
                    }
                } else {
                    triggerBox.addOption(TriggerFire.INSTEAD_OF.fireKey, TriggerFire.INSTEAD_OF.name());
                }
            }
        };
        typeBox.addOption(TriggerType.TABLE.name(), TriggerType.TABLE.name());
        typeBox.addOption(TriggerType.VIEW.name(), TriggerType.VIEW.name());
        schemaBox = new SelectBox("schema");
        TriggerUtil.getAllSchema(conn, "").forEach(item -> schemaBox.addOption(item, item));
        schemaBox.setValue(schemaName);
        tableInput = new TextInput("table");
        foreachBox = new SelectBox("foreach");
    }

    private void loadData(HTable table, String type, String schema, String value) {
        try {
            List<TreeMrType> treeMrTypes = new ArrayList<>();
            if (TreeMrType.TABLE.name().equals(type)) {
                treeMrTypes.add(TreeMrType.TABLE);
            } else {
                treeMrTypes.add(TreeMrType.VIEW);
            }
            AbsObjQuery query = AbsObjQuery.getInstance(dbType, jdbc, schema, treeMrTypes, value);
            query.doQuery();
            while (!query.getDone()) {
                SleepUtil.sleep100();
            }
            table.load(query.getResList(), 1);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
