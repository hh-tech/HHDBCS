package com.hh.hhdb_admin.mgr.index;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.index.Index;
import com.hh.frame.create_dbobj.index.IndexEnum;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.table.comp.SqlViewDialog;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author YuSai
 */
public abstract class IndexComp {

    private static final String LOG_NAME = IndexComp.class.getSimpleName();
    private static final String DOMAIN_NAME = IndexComp.class.getName();

    static {
        LangMgr.merge(DOMAIN_NAME, com.hh.frame.lang.LangUtil.loadLangRes(IndexComp.class));
    }

    private HDialog dialog;
    private final DBTypeEnum dbTypeEnum;
    private final String schema;
    private final String tableName;
    private final Index index;
    private final HTable table;
    private TextInput nameInput;
    private SelectBox typeBox;

    public IndexComp(Connection conn, DBTypeEnum dbTypeEnum, String schema, String tableName) {
        this.dbTypeEnum = dbTypeEnum;
        this.schema = schema;
        this.tableName = tableName;
        this.index = new Index(conn, dbTypeEnum);
        this.dialog = new HDialog(StartUtil.parentFrame, 600, 550);
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setWindowTitle(getLang("ADD_TITLE"));
        table = new HTable();
        BoolCol selected = new BoolCol("selected", IndexComp.getLang("SELECT"));
        selected.setWidth(50);
        table.addCols(new DataCol("column_name", IndexComp.getLang("COLUMN")), selected);
        table.setRowHeight(25);
        table.load(IndexUtil.getColumns(conn, schema, tableName), 1);
    }

    private HBarPanel getBarPanel() {
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton button = new HButton(getLang("SAVE")) {
            @Override
            protected void onClick() {
                save();
            }
        };
        button.setIcon(getIcon("save"));
        HButton sqlBtn = new HButton(getLang("PREVIEW_SQL")) {
            @Override
            protected void onClick() {
                previewSql();
            }
        };
        sqlBtn.setIcon(getIcon("sql_view"));
        barPanel.add(button);
        barPanel.add(sqlBtn);
        return barPanel;
    }

    private void save() {
        if (checkForm()) {
            String indexName = nameInput.getValue();
            String type = typeBox.getValue();
            if (null != index) {
                try {
                    String columns = IndexUtil.getColumnsName(table, dbTypeEnum);
                    index.addIndex(schema, indexName, tableName, columns, IndexEnum.valueOf(type));
                    refreshTree();
                    dialog.dispose();
                    PopPaneUtil.info(dialog.getWindow(), getLang("SAVE_SUCCESS"));
                } catch (SQLException e) {
                    e.printStackTrace();
                    logUtil.error(LOG_NAME, e);
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
                }
            }
        }
    }

    private void previewSql() {
        String indexName = nameInput.getValue();
        String type = typeBox.getValue();
        if (checkForm()) {
            String sql = "";
            if (null != index) {
                String columns = IndexUtil.getColumnsName(table, dbTypeEnum);
                sql = index.getSql(schema, indexName, tableName, columns, IndexEnum.valueOf(type));
            }
            SqlViewDialog dialog = new SqlViewDialog();
            dialog.setSql(sql);
            dialog.show();
        }
    }

    private boolean checkForm() {
        if (StringUtils.isBlank(nameInput.getValue())) {
            PopPaneUtil.error(dialog.getWindow(), getLang("INPUT_NAME"));
            return false;
        }
        String columns = IndexUtil.getColumnsName(table, dbTypeEnum);
        if (StringUtils.isEmpty(columns)) {
            PopPaneUtil.error(dialog.getWindow(), getLang("SELECT_COL"));
            return false;
        }
        return true;
    }

    public HPanel getPanel() {
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(10);
        divLayout.setxBorderWidth(30);
        divLayout.setTopHeight(20);
        HPanel panel = new HPanel(divLayout);
        panel.add(getBarPanel());
        nameInput = new TextInput("index_name");
        panel.add(getWithLabelInput(getLang("INDEX_NAME"), nameInput));
        typeBox = new SelectBox("type");
        JsonObject json = IndexUtil.getIndexType(dbTypeEnum);
        for (String name : json.names()) {
            typeBox.addOption(name, json.getString(name));
        }
        panel.add(getWithLabelInput(getLang("INDEX_TYPE"), typeBox));
        HPanel tablePanel = new HPanel(new HDivLayout(GridSplitEnum.C3));
        tablePanel.add(new LabelInput(getLang("INDEX_COLUMN")));
        JScrollPane scrollPane = new JScrollPane(table.getComp());
        scrollPane.setPreferredSize(new Dimension(scrollPane.getWidth(), 250));
        AbsHComp tableComp = new AbsHComp() {
            @Override
            public Component getComp() {
                return scrollPane;
            }
        };
        tablePanel.add(tableComp);
        panel.add(tablePanel);
        return panel;
    }

    private HGridPanel getWithLabelInput(String label, AbsInput input) {
        HGridLayout gridLayout = new HGridLayout(GridSplitEnum.C3);
        HGridPanel gridPanel = new HGridPanel(gridLayout);
        LabelInput labelInput = new LabelInput(label);
        gridPanel.setComp(1, labelInput);
        gridPanel.setComp(2, input);
        return gridPanel;
    }

    public void show() {
        dialog.setRootPanel(getPanel());
        dialog.show();
    }

    void delIndex(String indexName) throws Exception {
        index.delIndex(schema, tableName, indexName);
        refreshTree();
    }

    public static String getLang(String key) {
        LangMgr.setDefaultLang(StartUtil.default_language);
        return LangMgr.getValue(DOMAIN_NAME, key);
    }

    private ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.INDEX.name(), name, IconSizeEnum.SIZE_16));
    }

    public abstract void refreshTree();

}
