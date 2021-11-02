package com.hh.hhdb_admin.mgr.synonym;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.SleepUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.create_dbobj.obj_query.AbsObjQuery;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.CheckBoxInput;
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
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.table.comp.SqlViewDialog;

public abstract class SynonymComp {

    private static final String DOMAIN_NAME = SynonymComp.class.getName();

    private final String schema;
    private final String synonymName;
    private final Connection conn;
    private final JdbcBean jdbcBean;
    private final HDialog dialog;

    private SelectBox ownerBox;
    private CheckBoxInput publicInput;
    private TextInput nameInput;
    private SelectBox objectOwnerBox;
    private TextInput objectNameInput;
    private SelectBox dblinkBox;
    private Map<String, String> map = new HashMap<>();

    static {
        try {
            LangMgr2.loadMerge(SynonymComp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SynonymComp(Connection conn, JdbcBean jdbcBean, String synonymName) {
        this.conn = conn;
        this.jdbcBean = jdbcBean;
        this.schema = jdbcBean.getSchema();
        this.synonymName = synonymName;
        dialog = new HDialog(StartUtil.parentFrame, 550, 350);
        dialog.setWindowTitle(getLang(StringUtils.isEmpty(synonymName) ? "add" : "edit"));
        dialog.setIconImage(IconFileUtil.getLogo());
    }

    public abstract void refreshTree();

    protected void show() {
        dialog.setRootPanel(getPanel());
        if (StringUtils.isNotEmpty(synonymName)) {
            Map<String, String> map = getSynonym();
            ownerBox.setValue(map.get("owner"));
            nameInput.setValue(map.get("synonym_name"));
            publicInput.setValue(String.valueOf("PUBLIC".equals(map.get("owner"))));
            objectOwnerBox.setValue(map.get("table_owner"));
            objectNameInput.setValue(map.get("table_name"));
            dblinkBox.setValue(map.get("db_link"));
        } else {
            ownerBox.setValue(schema);
            objectOwnerBox.setValue(schema);
        }
        dialog.show();
    }

    public HPanel getPanel() {
        HPanel rootPanel = new HPanel();
        HPanel synonymPanel = new HPanel(new HDivLayout(0, 10));
        synonymPanel.setTitle(getLang("synonym"));
        ownerBox = new SelectBox("owner");
        publicInput = new CheckBoxInput("public");
        publicInput.getComp().addItemListener(e -> ownerBox.setValue(publicInput.isChecked() ? "PUBLIC" : schema));
        nameInput = new TextInput("name");
        synonymPanel.add(getWithPanelInput(getLang("owner"), ownerBox));
        synonymPanel.add(getWithPanelInput(getLang("public"), publicInput));
        synonymPanel.add(getWithPanelInput(getLang("name"), nameInput));

        HPanel tranPanel = new HPanel(new HDivLayout(0, 10));
        tranPanel.setTitle(getLang("transformation"));
        objectOwnerBox = new SelectBox("objectOwner");
        objectNameInput = new TextInput("objectName");
        dblinkBox = new SelectBox("dblink");
        tranPanel.add(getWithPanelInput(getLang("object_owner"), objectOwnerBox));

        HGridPanel tableGrid = new HGridPanel(new HGridLayout(GridSplitEnum.C3, GridSplitEnum.C6));
        tableGrid.setComp(1, new LabelInput(getLang("name")));
        tableGrid.setComp(2, objectNameInput);
        HButton searchBtn = new HButton(getLang("search")) {
            @Override
            protected void onClick() {
                showTable();
            }
        };
        searchBtn.setIcon(IconFileUtil.getIcon(new IconBean(CsMgrEnum.TRIGGER.name(), "query", IconSizeEnum.SIZE_16)));
        tableGrid.setComp(3, searchBtn);

        tranPanel.add(tableGrid);
        tranPanel.add(getWithPanelInput(getLang("dblink"), dblinkBox));

        initSelectBox();

        HBarPanel barPanel = new HBarPanel();
        HButton saveBtn = new HButton(getLang("save")) {
            @Override
            protected void onClick() {
                save();
            }
        };
        saveBtn.setIcon(getIcon("save"));
        HButton sqlPreview = new HButton(getLang("sql_preview")) {
            @Override
            protected void onClick() {
                sqlPreview();
            }
        };
        sqlPreview.setIcon(getIcon("sql_view"));
        barPanel.add(saveBtn, sqlPreview);

        rootPanel.add(synonymPanel);
        rootPanel.add(tranPanel);
        rootPanel.add(barPanel);
        return rootPanel;
    }

    private void initSelectBox() {
        try {
            List<String> users = SqlQueryUtil.selectOneColumn(conn, "select username from all_users");
            users.add("PUBLIC");
            Collections.sort(users);
            users.forEach(user -> {
                ownerBox.addOption(user, user);
                objectOwnerBox.addOption(user, user);
            });
            String sql = String.format("select * from all_db_links where owner = '%s' order by owner, db_link", schema);
            List<Map<String, String>> dblinks = SqlQueryUtil.selectStrMapList(conn, sql);
            dblinkBox.addOption("", "");
            dblinks.forEach(dblink -> dblinkBox.addOption(dblink.get("db_link"), dblink.get("db_link")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try {
            SqlExeUtil.executeUpdate(conn, getSql());
            dialog.dispose();
            PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("savedSuccess"));
            refreshTree();
        } catch (SQLException e) {
            e.printStackTrace();
            PopPaneUtil.error(dialog.getWindow(), e);
        }
    }

    private void sqlPreview() {
        SqlViewDialog dialog = new SqlViewDialog();
        dialog.setSql(getSql());
        dialog.show();
    }

    private String getSql() {
        StringBuilder builder = new StringBuilder("create or replace ");
        if (publicInput.isChecked()) {
            builder.append("public synonym ");
        } else {
            builder.append("synonym ");
            if (StringUtils.isNotBlank(ownerBox.getValue())) {
                builder.append(ownerBox.getValue()).append(".");
            }
        }
        if (StringUtils.isNotBlank(nameInput.getValue())) {
            builder.append(nameInput.getValue());
        }
        builder.append("\n for ");
        if (StringUtils.isNotBlank(objectOwnerBox.getValue())) {
            builder.append(objectOwnerBox.getValue()).append(".");
        }
        if (StringUtils.isNotBlank(objectNameInput.getValue())) {
            builder.append(objectNameInput.getValue());
        }
        if (StringUtils.isNotBlank(dblinkBox.getValue())) {
            builder.append("@").append(dblinkBox.getValue());
        }
        return builder.toString();
    }

    protected void showTable() {
        HDialog tDialog = new HDialog(this.dialog, 500, 500);
        tDialog.setWindowTitle(getLang("select"));
        tDialog.setIconImage(IconFileUtil.getLogo());
        HPanel panel = new HPanel();
        HTable table = new HTable();
        table.setCellEditable(false);
        table.getComp().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    List<HTabRowBean> rowBeans = table.getSelectedRowBeans();
                    if (rowBeans.size() == 0) {
                        PopPaneUtil.info(dialog.getWindow(), getLang("pleaseSelectOneData"));
                        return;
                    }
                    if (rowBeans.size() > 1) {
                        PopPaneUtil.info(dialog.getWindow(), getLang("onlySelectOne"));
                        return;
                    }
                    Map<String, String> map = rowBeans.get(0).getOldRow();
                    if (null != map) {
                        objectNameInput.setValue(map.get("name"));
                        tDialog.dispose();
                    }
                }
            }
        });
        table.addCols(new DataCol("name", getLang("object_name")),
                new DataCol("type", getLang("object_type")));
        SearchToolBar toolBar = new SearchToolBar(table);
        loadData(table);
        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(toolBar.getComp());
        lastPanel.setWithScroll(table.getComp());
        panel.setLastPanel(lastPanel);
        tDialog.setRootPanel(panel);
        tDialog.show();
    }

    private void loadData(HTable table) {
        try {
            List<TreeMrType> treeMrTypes = Arrays.asList(TreeMrType.TABLE, TreeMrType.VIEW, TreeMrType.M_VIEW, TreeMrType.SEQUENCE,
                    TreeMrType.TYPE, TreeMrType.PACKAGE, TreeMrType.SYNONYM, TreeMrType.PROCEDURE, TreeMrType.FUNCTION);
            AbsObjQuery query = AbsObjQuery.getInstance(DBTypeEnum.oracle, jdbcBean, schema, treeMrTypes, objectNameInput.getValue());
            query.doQuery();
            while (!query.getDone()) {
                SleepUtil.sleep100();
            }
            table.load(query.getResList(), 1);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private Map<String, String> getSynonym() {
        try {
            String sql = "select * from all_synonyms where owner = '%s' and synonym_name = '%s'";
            map = SqlQueryUtil.selectOneStrMap(conn, String.format(sql, schema, synonymName));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }


    private HGridPanel getWithPanelInput(String label, AbsInput input) {
        HGridPanel gridPanel = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
        gridPanel.setComp(1, new LabelInput(label));
        gridPanel.setComp(2, input);
        return gridPanel;
    }

    private String getLang(String key) {
        LangMgr2.setDefaultLang(StartUtil.default_language);
        return LangMgr2.getValue(DOMAIN_NAME, key);
    }

    private ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.SYNONYM.name(), name, IconSizeEnum.SIZE_16));
    }

}
