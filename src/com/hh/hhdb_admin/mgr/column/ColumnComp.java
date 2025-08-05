package com.hh.hhdb_admin.mgr.column;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.db_sql.ColSqlUtil;
import com.hh.frame.db_sql.conn.ColConnUtil;
import com.hh.frame.db_sql.info.SqlColPropBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public abstract class ColumnComp {
    private final Connection conn;
    private final DBTypeEnum dbType;
    private final String schema;
    private final String tableName;

    private final HDialog dialog;
    private final DBTypeEnum.Version ver = CsVarUtil.jdbc.getCurrentVersion();
    private boolean isUpdate;
    private TextInput colNameInput;
    private TypeComp typeComp;
    private CheckBoxInput notNullCheckInput;
    private TextAreaInput defaultValueTextArea;
    private TextAreaInput commentInput;

    private SqlColPropBean oldBean;
    private AlignEnum labelAlign = AlignEnum.LEFT;
    private HTextArea previewTextArea;


    public ColumnComp(Connection conn, DBTypeEnum dbTypeEnum, String schema, String tableName) {
        this.conn = conn;
        this.dbType = dbTypeEnum;
        this.schema = schema;
        this.tableName = tableName;
        this.dialog = new HDialog(CsVarUtil.parentFrame, 600, 550);
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setSize(600, 550);
        dialog.setStatusBar(new HBarPanel());
    }

    public HPanel getPanel(String colName) {
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        HPanel panel = new HPanel(divLayout);

        colNameInput = new TextInput("col_name");
        typeComp = new TypeComp(labelAlign);
        notNullCheckInput = new CheckBoxInput("not_null");
        defaultValueTextArea = new TextAreaInput("default_value", "", 3);
        commentInput = new TextAreaInput("annotation", "", 3);

        panel.add(getWithLabelInput(LangMgr.lang(Word2.NAME), getBarPanel()));
        panel.add(getWithLabelInput(LangMgr.lang(Word2.TYPE), typeComp));
        if (dbType != DBTypeEnum.hive) {
            if (dbType != DBTypeEnum.clickhouse) panel.add(getWithLabelInput(LangMgr.lang(Word2.NOT_NULL), notNullCheckInput));
            panel.add(getWithLabelInput(LangMgr.lang(Word3.DEFAULT_VALUE), defaultValueTextArea));
        }
        if (dbType != DBTypeEnum.informix && dbType != DBTypeEnum.derby && dbType != DBTypeEnum.sqlite) panel.add(getWithLabelInput(LangMgr.lang(Word2.NOTES), commentInput));

        previewTextArea = new HTextArea();
        HTAreaConf conf = new HTAreaConf();
        conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
        previewTextArea.setConf(conf);
        LastPanel previewPanel = new LastPanel();
        previewPanel.set(previewTextArea.getComp());
        previewPanel.setTitle(LangMgr.sLang(CsL.SQL_AREA));
        panel.setLastPanel(previewPanel);

        if (isUpdate) {
            initValues(colName);
        }

        return panel;
    }

    public void initValues(String colName) {
        try {
            oldBean = ColConnUtil.selectColProp(dbType, ver, conn, schema, tableName, colName);
            colNameInput.setValue(colName);
            colNameInput.setEnabled(false);
            if (oldBean.getNotNull() != null)
                notNullCheckInput.setValue(oldBean.getNotNull());
            if (oldBean.getDefaultValue() != null && !StringUtils.equals(oldBean.getDefaultValue(), "null"))
                defaultValueTextArea.setValue(StringUtils.strip(oldBean.getDefaultValue(), "'"));
            commentInput.setValue(oldBean.getComment());
            typeComp.setByFullType(oldBean.getFullType(dbType, ver));
            if (oldBean.getIsAutoIn() != null)
                typeComp.setAuto(oldBean.getIsAutoIn());
            if (dbType == DBTypeEnum.databricks) typeComp.setEnabled(false);
        } catch (SQLException e) {
            Lg.error(ColumnComp.class, e);
            PopDlgUtil.error(CsVarUtil.parentFrame, e);
        }
    }

    private SqlColPropBean toModifyBean() {
        SqlColPropBean bean = new SqlColPropBean(null, tableName, colNameInput.getValue(), typeComp.getType());
        bean.setDataNumOne(typeComp.getLength());
        bean.setDataNumTwo(typeComp.getScale());
        bean.setComment(commentInput.getValue());
        bean.setNotNull(notNullCheckInput.isChecked());
        bean.setSchemaName(schema);
        bean.setIsAutoIn(typeComp.isAutoIncrease());
        if (StringUtils.isNotBlank(defaultValueTextArea.getValue()))
            bean.setDefaultValue(defaultValueTextArea.getValue());
        return bean;
    }

    private HBarPanel getBarPanel() {
        HBarLayout barLayout = new HBarLayout();
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton button = new HButton(LangMgr.lang(Word2.SAVE)) {
            @Override
            protected void onClick() {
                save();
            }
        };
        button.setIcon(FaSolidEnum.FLOPPY_DISK, 16);
        HButton sqlBtn = new HButton(LangMgr.lang(Word2.PREVIEW)) {
            @Override
            protected void onClick() {
                previewSql();
            }
        };
        sqlBtn.setIcon(FaSolidEnum.MAGNIFYING_GLASS, 16);
        barPanel.add(button);
        barPanel.add(sqlBtn);
        barPanel.setLastHComp(colNameInput);
        return barPanel;
    }

    public void save() {
        if (checkForm()) {
            boolean suc = true;

            String[] sqls = StringUtils.isBlank(previewTextArea.getText()) ? getSqlList().toArray(new String[0]) : StringUtils.split(previewTextArea.getText(), ";");
            for (String s : sqls) {
                if (!GlobalWorkUtil.exeSql(CsVarUtil.jdbc, conn, SqlStrUtil.trim(dbType, s))) {
                    suc = false;
                    break;
                }
            }
            if (suc) {
                dialog.dispose();
                PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.SAVED_SUCCESSFULLY));
            }
            refreshTree();
        }
    }

    public void previewSql() {
        if (checkForm()) {
            String sql = StringUtils.join(getSqlList(), ";\n");
            previewTextArea.setText(StringUtils.isNotBlank(sql) ? sql + ";" : "");
        }
    }

    /**
     * 拼接sql
     */
    private List<String> getSqlList() {
        SqlColPropBean newBean = toModifyBean();
        List<String> ls = null;
        if (isUpdate) {
            ls = ColSqlUtil.getModifyColSql(dbType, ver, oldBean, newBean);
        } else {
            ls = ColSqlUtil.getColAllRelatedSqls(dbType, ver, newBean);
        }
        return ls;
    }

    /**
     * 检查form
     */
    private boolean checkForm() {
        if (StringUtils.isBlank(colNameInput.getValue())) {
            PopDlgUtil.info(dialog, LangMgr.sLang(CsL.INPUT_NAME));
            return false;
        }
        return true;
    }


    HPanel getWithLabelInput(String label, AbsHComp hComp) {
        HPanel hPanel = new HPanel(new HDivLayout(GridSplitEnum.C2));
        hPanel.add(new LabelInput(label + ":", labelAlign));
        hPanel.add(hComp);
        return hPanel;
    }

    public void show(boolean isUpdate, String colName) {
        this.isUpdate = isUpdate;
        dialog.setRootPanel(getPanel(colName));
        dialog.setWindowTitle(LangMgr.lang(isUpdate ? Word2.DESIGN : Word2.ADD));
        dialog.show();
    }

    public void renameColumn(String tabName, String colName) {
        TextInput nameInput = new TextInput("newName", colName);
        LastPanel lastPanel = new LastPanel();
        lastPanel.set(getWithLabelInput(LangMgr.lang(Word2.NAME) + " :", nameInput).getComp());
        HDivLayout divLayout = new HDivLayout();
        divLayout.setTopHeight(10);
        HPanel panel = new HPanel(divLayout);
        panel.setLastPanel(lastPanel);
        HDialog dialog = new HDialog(CsVarUtil.parentFrame, 400, 130) {
            protected void onConfirm() {
                String newName = nameInput.getValue();
                if (StringUtils.isBlank(newName)) {
                    PopDlgUtil.info(this, LangMgr.sLang(CsL.INPUT_NEW_NAME));
                    return;
                }
                if (newName.trim().equals(colName)) {
                    PopDlgUtil.info(this, LangMgr.sLang(CsL.NAME_EQUAL_ERROR));
                    return;
                }
                try {
                    String colType = null;
                    if (DBTypeEnum.isHiveSeries(dbType, ver)) {

                        //TODO获取Hive原始的列数据
                    }
                    //获取Hive的Column
                    String sql = ColSqlUtil.getRenameColSql(dbType, ver, new SqlColPropBean(schema, tabName, colName, colType), newName);
                    if (GlobalWorkUtil.exeSql(CsVarUtil.jdbc, conn, sql)) {
                        dispose();
                        refreshTree();
                        PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.UPDATE_SUCCESS));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Lg.error(ColumnComp.class, e);
                    PopDlgUtil.error(CsVarUtil.parentFrame, e);
                }
            }

            protected void onCancel() {
                dispose();
            }
        };
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setWindowTitle(LangMgr.lang(Word3.RENAME));
        dialog.setRootPanel(panel);
        dialog.show();
    }

    public abstract void refreshTree();
}
