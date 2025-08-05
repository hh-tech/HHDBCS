package com.hh.hhdb_admin.mgr.constraint3;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.db_sql.TabKeySqlUtil;
import com.hh.frame.db_sql.TableSqlUtil;
import com.hh.frame.db_sql.info.SqlTabBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class ForeignKeyPanel extends PkgAbsBasePanel {
    private final HPanel associationPanel;
    LinkedHashMap<SelectBox, SelectBox> associationMap = new LinkedHashMap<>();
    private final TextInput associationTableInput = new TextInput();
    private DBTypeEnum dbtype=CsVarUtil.getDbtype();
    private DBTypeEnum.Version ver=CsVarUtil.jdbc.getCurrentVersion();
    private Connection conn=CsVarUtil.conn;
    private List<String> cols = getAllCols(schema, tabName, false);
    private List<String> refCols = new ArrayList<>();
    private HButton addButton;
    private HButton viewSqlButton;
    private HButton saveButton;
    private final StringBuilder refSchema = new StringBuilder();
    private final StringBuilder refTabName = new StringBuilder();

    public ForeignKeyPanel(String schema, String tabName) {
        super(schema, tabName);

        setHead(getBarPanel().getComp());

        AlignEnum align = AlignEnum.CENTER;
        HGridPanel gridTitlePanel = new HGridPanel(new HGridLayout(GridSplitEnum.C4, GridSplitEnum.C4, GridSplitEnum.C4));
        gridTitlePanel.setComp(1, new LabelInput(LangMgr.sLang(FrameL.ORIGINAL_TABLE_COLUMN_NAMES), align));
        gridTitlePanel.setComp(2, new LabelInput(LangMgr.sLang(FrameL.ASSOCIATE_TABLE_COLUMN_NAMES), align));
        gridTitlePanel.setComp(3, new LabelInput(LangMgr.lang(Word2.OPERATION), align));

        associationPanel = new HPanel();
        associationPanel.add(gridTitlePanel);

        LastPanel associationLastPanel = new LastPanel();
        associationLastPanel.setTitle(LangMgr.lang(Word2.REFLEVANCE));
        associationLastPanel.setWithScroll(associationPanel.getComp());

        HSplitPanel split = new HSplitPanel(false);
        split.setLastComp4One(associationLastPanel);
        split.setLastComp4Two(getSqlPanel(sqlTextArea));
        split.setDividerLocation(200);

        set(split.getComp());
    }

    @Override
    protected void inform() {

    }

    @Override
    protected HBarPanel getBarPanel() {
        HPanel hPanel = new HPanel(new HDivLayout(GridSplitEnum.C2));
        hPanel.add(new LabelInput(LangMgr.lang(Word3.ASSOCIATE_TABLE) + ":", AlignEnum.LEFT));
        hPanel.add(associationTableInput);
        HButton loadButton = new HButton(LangMgr.lang(Word2.LOAD), FaSolidEnum.DOWNLOAD) {
            @Override
            protected void onClick() {
                if (StringUtils.isBlank(associationTableInput.getValue())) {
                    PopDlgUtil.info(getComp(), LangMgr.sLang(CsL.ASSOCIATE_TABLE_NOT_NULL));
                    return;
                }
                String[] ref = StringUtils.split(associationTableInput.getValue(), ".");
                String refSchema;
                String refTabName;
                if (ref.length == 1) {
                    refSchema = schema;
                    refTabName = ref[0];
                } else {
                    refSchema = ref[0];
                    refTabName = ref[1];
                }
                //由于表格是用户输入的，所以需要根据数据库的规则进行大小写转换
                refSchema=SqlStrUtil.rmQuote4Sel(refSchema, dbtype, ver);
                refTabName=SqlStrUtil.rmQuote4Sel(refTabName, dbtype, ver);
                ForeignKeyPanel.this.refSchema.setLength(0);
                ForeignKeyPanel.this.refTabName.setLength(0);
                ForeignKeyPanel.this.refSchema.append(refSchema);
                ForeignKeyPanel.this.refTabName.append(refTabName);
                refCols = getAllCols(refSchema, refTabName);
                associationMap.forEach((a, b) -> {
                    b.removeAllItems();
                    refCols.forEach(s -> b.addOption(s, s));
                });
            }
        };
        addButton = new HButton(LangMgr.lang(Word2.ADD2), FaSolidEnum.PLUS) {

            @Override
            protected void onClick() {
                HGridPanel gridPanel = new HGridPanel(new HGridLayout(GridSplitEnum.C4, GridSplitEnum.C4, GridSplitEnum.C4));
                SelectBox selectBox1 = new SelectBox();
                SelectBox selectBox2 = new SelectBox();
                cols.forEach(s -> selectBox1.addOption(s, s));
                refCols.forEach(s -> selectBox2.addOption(s, s));
                gridPanel.setComp(1, selectBox1);
                gridPanel.setComp(2, selectBox2);
                associationMap.put(selectBox1, selectBox2);
                gridPanel.setComp(3, new HButton(LangMgr.lang(Word2.DELETE), FaSolidEnum.MINUS) {
                    @Override
                    protected void onClick() {
                        associationPanel.remove(gridPanel);
                        associationMap.remove(selectBox1);
                    }
                });
                associationPanel.add(gridPanel);
                updateUI();
            }
        };
        viewSqlButton = createViewSqlButton();
        saveButton = createSaveButton();
        setButtonsEnable(false);

        HBarPanel barPanel = new HBarPanel(getLayout(AlignEnum.RIGHT));
        barPanel.setLastHComp(hPanel);
        barPanel.add(loadButton);
        barPanel.add(addButton);
        barPanel.add(viewSqlButton);
        barPanel.add(saveButton);
        return barPanel;
    }

    @Override
    protected void check() throws Exception {
        if (associationMap.isEmpty()) {
            throw new Exception(LangMgr.lang(Word4.NOT_NULL));
        }
    }

    @Override
    protected String genSql() {
        SqlTabBean tab = new SqlTabBean(tabName);
        tab.setSchemaName(schema);
        SqlTabBean refTab = new SqlTabBean(refTabName.toString());
        refTab.setSchemaName(refSchema.toString());
        List<String> colList = new ArrayList<>();
        List<String> refColList = new ArrayList<>();
        associationMap.forEach((a, b) -> {
            colList.add(a.getValue());
            refColList.add(b.getValue());
        });
        return TabKeySqlUtil.getAddForKeySql(dbtype, ver, tab, colList, refTab, refColList);
    }

    private List<String> getAllCols(String schema, String tabName) {
        return getAllCols(schema, tabName, true);
    }

    private List<String> getAllCols(String schema, String tabName, boolean showSuccessMessage) {
        String colsSql = TableSqlUtil.getSelColNamesSql(dbtype, ver, schema, tabName);
        try {
            List<String> allCols = SqlQueryUtil.selectOneColumn(conn, colsSql);
            if (allCols.isEmpty()) {
                setButtonsEnable(false);
                PopDlgUtil.info(getComp(), LangMgr.lang(Word2.FAILED));
            } else {
                setButtonsEnable(true);
                if (showSuccessMessage) {
                    PopDlgUtil.info(getComp(), LangMgr.lang(Word2.SUCCESSFULLY));
                }
            }
            return allCols;
        } catch (SQLException e) {
            PopDlgUtil.error(getComp(), e);
            Lg.error(getClass(), e);
            return new ArrayList<>();
        }
    }

    private void setButtonsEnable(boolean isEnable) {
        for (HButton hButton : Arrays.asList(addButton, viewSqlButton, saveButton)) {
            if (hButton != null) {
                hButton.setEnabled(isEnable);
            }
        }
    }
}
