package com.hh.hhdb_admin.mgr.constraint;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.lang.LangUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.constraint.foreign.AbsForeTable;
import com.hh.hhdb_admin.mgr.constraint.otherConst.AbsTable;
import com.hh.hhdb_admin.mgr.table.TableComp;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author YuSai
 */
public abstract class ConstraintComp {

    private static final String LOG_NAME = ConstraintComp.class.getSimpleName();
    private static final String DOMAIN_NAME = ConstraintComp.class.getName();

    static {
        LangMgr.merge(DOMAIN_NAME, LangUtil.loadLangRes(ConstraintComp.class));
    }

    private final AtomicInteger atomicId = new AtomicInteger();
    private final Connection conn;
    private final DBTypeEnum dbTypeEnum;
    private final TreeMrType treeMrType;
    private final String schema;
    private final String tableName;
    private final HDialog dialog;
    private boolean bool;

    public ConstraintComp(TreeMrType treeMrType, Connection conn, DBTypeEnum dbTypeEnum, String schema, String tableName) {
        this.conn = conn;
        this.dbTypeEnum = dbTypeEnum;
        this.treeMrType = treeMrType;
        this.schema = schema;
        this.tableName = tableName;
        this.dialog = new HDialog(StartUtil.parentFrame, 800, 600);
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setWindowTitle(ConstraintComp.getLang("addConstraint"));
        ConstraintUtil.initConst(conn, dbTypeEnum);
        TableComp panelCreate = new TableComp(conn, dbTypeEnum);
        panelCreate.genTableData();
    }

    private HBarPanel getBarPanel(HTable table) {
        HButton addButton = new HButton(ConstraintComp.getLang("add")) {
            @Override
            protected void onClick() {
                table.add(getData().get(0));
            }
        };
        addButton.setIcon(getIcon("add"));
        HButton delButton = new HButton(ConstraintComp.getLang("delete")) {
            @Override
            protected void onClick() {
                List<HTabRowBean> rows = table.getSelectedRowBeans();
                if (rows.size() > 0) {
                    int result = JOptionPane.showConfirmDialog(null, ConstraintComp.getLang("sureDelete"),
                            ConstraintComp.getLang("hint"), JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        table.deleteSelectRow();
                    }
                }
            }
        };
        delButton.setIcon(getIcon("delete"));
        HButton savaBtn = new HButton(ConstraintComp.getLang("save")) {
            @Override
            protected void onClick() {
                save(table);
            }
        };
        savaBtn.setIcon(getIcon("save"));
        HButton preViewBtn = new HButton(ConstraintComp.getLang("previewSql")) {
            @Override
            protected void onClick() {
                previewSql(table);
            }
        };
        preViewBtn.setIcon(getIcon("sql_view"));
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        barPanel.add(addButton);
        barPanel.add(delButton);
        barPanel.add(savaBtn);
        barPanel.add(preViewBtn);
        return barPanel;
    }

    private void save(HTable table) {
        try {
            ConstraintUtil.save(treeMrType, schema, tableName, table, bool);
            dialog.dispose();
            refreshTree();
            PopPaneUtil.info(StartUtil.parentFrame.getWindow(), ConstraintComp.getLang("saveSuccess"));
        } catch (SQLException e) {
            e.printStackTrace();
            logUtil.error(LOG_NAME, e);
            PopPaneUtil.error(dialog.getWindow(), e.getMessage());
        }
    }

    private void previewSql(HTable table) {
        ConstraintUtil.previewSql(treeMrType, schema, tableName, table, bool);
    }

    private List<Map<String, String>> getData() {
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(atomicId.incrementAndGet()));
        map.put("foreignName", "");
        map.put("foreignOnDelete", "");
        data.add(map);
        return data;
    }

    public HPanel getForePanel() {
        this.bool = true;
        LastPanel lastPanel = new LastPanel();
        AbsForeTable foreTable = AbsForeTable.getForeTable(dbTypeEnum);
        if (null != foreTable) {
            HTable table = foreTable.getTable(conn, schema, tableName);
            lastPanel.setHead(getBarPanel(table).getComp());
            lastPanel.setWithScroll(table.getComp());
            table.load(new ArrayList<>(), 1);
        }
        HPanel panel = new HPanel();
        panel.setLastPanel(lastPanel);
        return panel;
    }

    void showFore() {
        dialog.setRootPanel(getForePanel());
        dialog.show();
    }

    public HPanel getOtherPanel() {
        this.bool = false;
        LastPanel lastPanel = new LastPanel();
        AbsTable absOtherTable = AbsTable.getOtherTable(treeMrType);
        if (null != absOtherTable) {
            HTable table = absOtherTable.getTable(conn, schema, tableName);
            lastPanel.setHead(getBarPanel(table).getComp());
            lastPanel.setWithScroll(table.getComp());
            table.load(new ArrayList<>(), 1);
        }
        HPanel panel = new HPanel();
        panel.setLastPanel(lastPanel);
        return panel;
    }

    void showOtherConst() {
        dialog.setRootPanel(getOtherPanel());
        dialog.show();
    }

    void delConst(String constType, String schema, String table, String constName) throws Exception {
        ConstraintUtil.delete(constType, schema, table, constName);
        refreshTree();
    }

    public static String getLang(String key) {
        LangMgr.setDefaultLang(StartUtil.default_language);
        return LangMgr.getValue(DOMAIN_NAME, key);
    }

    private ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.CONSTRAINT.name(), name, IconSizeEnum.SIZE_16));
    }

    public abstract void refreshTree();

}
