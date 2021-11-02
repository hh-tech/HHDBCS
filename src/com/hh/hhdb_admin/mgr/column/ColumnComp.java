package com.hh.hhdb_admin.mgr.column;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.column.AbsColumn;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YuSai
 */
public abstract class ColumnComp {

    private static final String LOG_NAME = ColumnComp.class.getSimpleName();

    private static final String DOMAIN_NAME = ColumnComp.class.getName();

    static {
        try {
            LangMgr2.loadMerge(ColumnComp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final Connection conn;
    private final DBTypeEnum dbTypeEnum;
    private final String schema;
    private final String tableName;
    private final AbsColumn absColumn;

    private final HDialog dialog;
    private boolean isUpdate;
    private TextInput colNameInput;
    private TypeComp typeComp;
    private CheckBoxInput checkBoxInput;
    private DefaultValueComp defaultValueComp;
    private TextAreaInput annotationInput;

    private final Map<String, String> oldValues = new HashMap<>();
    private Map<String, String> newValues;

    public ColumnComp(Connection conn, DBTypeEnum dbTypeEnum, String schema, String tableName) {
        this.conn = conn;
        this.dbTypeEnum = dbTypeEnum;
        this.schema = schema;
        this.tableName = tableName;
        this.absColumn = AbsColumn.getColumn(conn, dbTypeEnum);
        this.dialog = new HDialog(StartUtil.parentFrame, 600, 550);
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setSize(600, 550);
    }

    public HPanel getPanel(String colName) {
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(10);
        divLayout.setxBorderWidth(30);
        divLayout.setTopHeight(20);
        HPanel panel = new HPanel(divLayout);
        colNameInput = new TextInput("col_name");
        typeComp = new TypeComp(dialog, absColumn) {
            @Override
            protected void validate() {
                panel.getComp().validate();
            }
        };
        checkBoxInput = new CheckBoxInput("not_null");
        defaultValueComp = new DefaultValueComp();
        annotationInput = new TextAreaInput("annotation", "", 3);
        panel.add(getBarPanel());
        panel.add(getWithLabelInput(getLang("COL_NAME"), colNameInput));
        HPanel typePanel = new HPanel(new HDivLayout(GridSplitEnum.C2));
        typePanel.add(new LabelInput(getLang("COL_TYPE")));
        typePanel.add(typeComp);
        panel.add(typePanel);
        panel.add(getWithLabelInput(getLang("COL_NOT_NULL"), checkBoxInput));
        HPanel defaultPanel = new HPanel(new HDivLayout(GridSplitEnum.C2));
        defaultPanel.add(new LabelInput(getLang("COL_DEFAULT")));
        defaultPanel.add(defaultValueComp);
        panel.add(defaultPanel);
        panel.add(getWithLabelInput(getLang("COL_ANNOTATION"), annotationInput));
        if (isUpdate) {
            initValues(colName);
        }
        panel.setTitle(getLang("COLUMN_INFO"));
        return panel;
    }

    public void initValues(String colName) {
        try {
            Map<String, String> propMaps = absColumn.getColumnProp(conn, schema, tableName, colName);
            colNameInput.setValue(colName);
            colNameInput.setEnabled(false);
            checkBoxInput.setValue(Boolean.parseBoolean(propMaps.get("not_null")) + "");
            defaultValueComp.setDefaultValue(propMaps.get("default"));
            annotationInput.setValue(propMaps.get("annotation"));
            typeComp.setType(propMaps.get("col_type"));
            typeComp.setAuto(Boolean.parseBoolean(propMaps.get("auto")));
            oldValues.put("schema", schema);
            oldValues.put("tableName", tableName);
            oldValues.put("colName", colNameInput.getValue());
            oldValues.put("annotation", annotationInput.getValue());
            oldValues.put("default", defaultValueComp.getValue());
            oldValues.put("not_null", checkBoxInput.getValue());
            oldValues.put("col_type", typeComp.getType());
            oldValues.put("auto", typeComp.isAutoIncrease(dbTypeEnum) + "");
        } catch (SQLException e) {
            e.printStackTrace();
            logUtil.error(LOG_NAME, e);
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
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

    public void save() {
        if (checkForm()) {
            try {
                String sql = getSql();
                if (StringUtils.isBlank(sql)) {
                    return;
                }
                String[] sqlArr = sql.split(System.lineSeparator());
                for (String str : sqlArr) {
                    SqlExeUtil.executeUpdate(conn, str);
                }
                dialog.dispose();
                PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("SAVE_SUCCESS"));
                refreshTree();
            } catch (SQLException e) {
                e.printStackTrace();
                logUtil.error(LOG_NAME, e);
                PopPaneUtil.error(dialog.getWindow(), e);
            }
        }
    }

    public void previewSql() {
        if (checkForm()) {
            SqlViewDialog dialog = new SqlViewDialog();
            dialog.setSql(getSql());
            dialog.show();
        }
    }

    /**
     * 拼接sql
     */
    private String getSql() {
        initNewValues();
        if (isUpdate) {
            return absColumn.getUpdateSql(oldValues, newValues, typeComp.getType(), typeComp.isAutoIncrease(dbTypeEnum));
        }
        String colName = colNameInput.getValue();
        String annotation = annotationInput.getValue();
        return absColumn.getAddColSql(schema, tableName, colName, typeComp.getType(), defaultValueComp.getValue(), checkBoxInput.getValue(), annotation, typeComp.isAutoIncrease(dbTypeEnum));
    }

    /**
     * 检查form
     */
    private boolean checkForm() {
        if (StringUtils.isBlank(colNameInput.getValue())) {
            PopPaneUtil.error(dialog.getWindow(), getLang("INPUT_NAME"));
            return false;
        }
        return true;
    }

    private void initNewValues() {
        newValues = new HashMap<>();
        newValues.put("schema", schema);
        newValues.put("tableName", tableName);
        newValues.put("colName", colNameInput.getValue());
        newValues.put("annotation", annotationInput.getValue());
        newValues.put("default", defaultValueComp.getValue());
        newValues.put("not_null", checkBoxInput.getValue());
        newValues.put("col_type", typeComp.getType());
        newValues.put("auto", typeComp.isAutoIncrease(dbTypeEnum) + "");
    }

    HGridPanel getWithLabelInput(String label, AbsInput input) {
        HGridLayout gridLayout = new HGridLayout(GridSplitEnum.C2);
        HGridPanel gridPanel = new HGridPanel(gridLayout);
        LabelInput labelInput = new LabelInput(label);
        gridPanel.setComp(1, labelInput);
        gridPanel.setComp(2, input);
        return gridPanel;
    }

    public void show(boolean isUpdate, String colName) {
        this.isUpdate = isUpdate;
        dialog.setRootPanel(getPanel(colName));
        dialog.setWindowTitle(getLang(isUpdate ? "UPDATE_TITLE" : "ADD_TITLE"));
        dialog.show();
    }

    public void renameColumn(String tabName, String colName) {
        HDialog dialog = new HDialog(StartUtil.parentFrame, 400, 120);
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setWindowTitle(getLang("RENAME_TITLE"));
        TextInput nameInput = new TextInput("newName", colName);
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.CENTER);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton subitBtn = new HButton(getLang("BTN_QD")) {
            @Override
            protected void onClick() {
                String newName = nameInput.getValue();
                if (StringUtils.isBlank(newName)) {
                    PopPaneUtil.error(dialog.getWindow(), getLang("INPUT_NEW_NAME"));
                    return;
                }
                if (newName.trim().equals(colName)) {
                    PopPaneUtil.error(dialog.getWindow(), getLang("NAME_EQUAL_ERROR"));
                    return;
                }
                try {
                    if (absColumn != null) {
                        absColumn.renameColumn(schema, tabName, colName, newName);
                        dialog.dispose();
                        refreshTree();
                        PopPaneUtil.info(StartUtil.parentFrame.getWindow(), getLang("UPD_SUCCESS"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logUtil.error(LOG_NAME, e);
                    PopPaneUtil.error(dialog.getWindow(), e);
                }
            }
        };
        subitBtn.setIcon(getIcon("submit"));
        HButton cancelBtn = new HButton(getLang("BTN_QX")) {
            @Override
            protected void onClick() {
                dialog.dispose();
            }
        };
        cancelBtn.setIcon(getIcon("cancel"));
        barPanel.add(subitBtn, cancelBtn);
        LastPanel lastPanel = new LastPanel();
        lastPanel.set(getWithLabelInput(getLang("COL_NAME"), nameInput).getComp());
        lastPanel.setFoot(barPanel.getComp());
        HPanel panel = new HPanel();
        panel.add(new HeightComp(5));
        panel.setLastPanel(lastPanel);
        dialog.setRootPanel(panel);
        dialog.show();
    }

    public static String getLang(String key) {
        LangMgr2.setDefaultLang(StartUtil.default_language);
        return LangMgr2.getValue(DOMAIN_NAME, key);
    }

    public static ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.COLUMN.name(), name, IconSizeEnum.SIZE_16));
    }

    public abstract void refreshTree();
}
