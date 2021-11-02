package com.hh.hhdb_admin.mgr.dblink;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.table.comp.SqlViewDialog;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class DblinkComp {

    private static final String DOMAIN_NAME = DblinkComp.class.getName();

    private final String schema;
    private final String dblinkName;
    private final Connection conn;
    private final HDialog dialog;
    private TextInput ownerInput;
    private TextInput nameInput;
    private CheckBoxInput shareInput;
    private CheckBoxInput publicInput;
    private TextInput usernameInput;
    private TextInput userPassInput;
    private TextInput databaseInput;
    private TextInput authNameInput;
    private TextInput authPassInput;
    private Map<String, String> map = new HashMap<>();

    static {
        try {
            LangMgr2.loadMerge(DblinkComp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DblinkComp(Connection conn, String schema, String dblinkName) {
        this.conn = conn;
        this.schema = schema;
        this.dblinkName = dblinkName;
        dialog = new HDialog(StartUtil.parentFrame, 550, 500);
        dialog.setWindowTitle(getLang(StringUtils.isEmpty(dblinkName) ? "add" : "edit"));
        dialog.setIconImage(IconFileUtil.getLogo());
    }

    public abstract void refreshTree();

    protected void show() {
        dialog.setRootPanel(getPanel());
        if (StringUtils.isNotEmpty(dblinkName)) {
            Map<String, String> map = getDblink();
            ownerInput.setValue(map.get("owner"));
            nameInput.setValue(map.get("db_link"));
            publicInput.setValue(String.valueOf("PUBLIC".equals(map.get("owner"))));
            usernameInput.setValue(map.get("username"));
            databaseInput.setValue(map.get("host"));
        }
        dialog.show();
    }

    public HPanel getPanel() {
        HPanel rootPanel = new HPanel();
        HPanel dbPanel = new HPanel(new HDivLayout(0, 10));
        dbPanel.setTitle(getLang("dblink"));
        ownerInput = new TextInput("owner", schema);
        ownerInput.setEnabled(false);
        nameInput = new TextInput("name");
        publicInput = new CheckBoxInput("public");
        publicInput.getComp().addItemListener(e -> ownerInput.setValue(publicInput.isChecked() ? "PUBLIC" : schema));
        shareInput = new CheckBoxInput("share");
        dbPanel.add(getWithPanelInput(getLang("owner"), ownerInput));
        dbPanel.add(getWithPanelInput(getLang("name"), nameInput));
        dbPanel.add(getWithPanelInput(getLang("public"), publicInput));
        dbPanel.add(getWithPanelInput(getLang("shared"), shareInput));

        HPanel connPanel = new HPanel(new HDivLayout(0, 10));
        connPanel.setTitle(getLang("connection_to"));
        usernameInput = new TextInput("username");
        userPassInput = new TextInput("userPass");
        databaseInput = new TextInput("database");
        connPanel.add(getWithPanelInput(getLang("username"), usernameInput));
        connPanel.add(getWithPanelInput(getLang("password"), userPassInput));
        connPanel.add(getWithPanelInput(getLang("database"), databaseInput));

        HPanel authPanel = new HPanel(new HDivLayout(0, 10));
        authPanel.setTitle(getLang("certifier"));
        authNameInput = new TextInput("authName");
        authPassInput = new TextInput("authPass");
        authPanel.add(getWithPanelInput(getLang("username"), authNameInput));
        authPanel.add(getWithPanelInput(getLang("password"), authPassInput));

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

        rootPanel.add(dbPanel);
        rootPanel.add(connPanel);
        rootPanel.add(authPanel);
        rootPanel.add(barPanel);
        return rootPanel;
    }

    private Map<String, String> getDblink() {
        try {
            String sql = "select * from all_db_links where owner = '%s' and db_link = '%s'";
            String[] params = dblinkName.split("\\.");
            map = SqlQueryUtil.selectOneStrMap(conn, String.format(sql, params[0], params[1]));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    private void save() {
        try {
            String sql = getSql();
            String[] sqlLists = sql.split(";");
            if (sqlLists.length > 1) {
                SqlExeUtil.batchExecute(conn, Arrays.asList(sqlLists));
            } else {
                SqlExeUtil.executeUpdate(conn, sqlLists[0]);
            }
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
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotEmpty(dblinkName)) {
            builder.append("drop ").append("PUBLIC".equals(map.get("owner")) ? "public " : "");
            builder.append("database link ").append(dblinkName.split("\\.")[1]).append(";\n\n");
        }
        builder.append("create ");
        if (shareInput.isChecked()) {
            builder.append("shared ");
        }
        if (publicInput.isChecked()) {
            builder.append("public ");
        }
        builder.append("database link ");
        if (StringUtils.isNotBlank(nameInput.getValue())) {
            builder.append(nameInput.getValue()).append(" ");
        }
        builder.append("\n");
        if (StringUtils.isNotBlank(usernameInput.getValue())) {
            builder.append(" connect to ").append(usernameInput.getValue());
        }
        if (StringUtils.isNotBlank(userPassInput.getValue())) {
            builder.append(" identified by ").append("\"").append(userPassInput.getValue()).append("\"");
        }
        if (StringUtils.isNotBlank(authNameInput.getValue())) {
            builder.append("\n authenticated by ").append(authNameInput.getValue());
        }
        if (StringUtils.isNotBlank(authPassInput.getValue())) {
            builder.append(" identified by ").append("\"").append(authPassInput.getValue()).append("\"");
        }
        builder.append("\n using '");
        if (StringUtils.isNotBlank(databaseInput.getValue())) {
            builder.append(databaseInput.getValue());
        }
        builder.append("'");
        return builder.toString();
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
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.DBLINK.name(), name, IconSizeEnum.SIZE_16));
    }

}
