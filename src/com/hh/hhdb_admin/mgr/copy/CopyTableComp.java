package com.hh.hhdb_admin.mgr.copy;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.db_sql.TableSqlUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.comp.BaseSqlPanel;
import com.hh.hhdb_admin.comp.colsel.ColSelPanel;

import org.apache.commons.lang3.StringUtils;



public abstract class CopyTableComp extends BaseSqlPanel {
    private final HTextArea area;
    private final TextInput nameInput;
    private final TextInput tableInput;
    private final ColSelPanel selPanel;
    private final JdbcBean jdbcBean;
    private final String schemaName;
    private final String tableName;
    private final HTabPanel hTabPanel;


    public CopyTableComp(JdbcBean jdbcBean, String schemaName, String tableName) {
        this.jdbcBean = jdbcBean;
        this.schemaName = schemaName;
        this.tableName = tableName;

        hTabPanel = new HTabPanel();
        HBarLayout barLayout = HBarLayout.newLayout(AlignEnum.LEFT);
        barLayout.setTopHeight(5);
        barLayout.setBottomHeight(5);

        HBarPanel barPanel = new HBarPanel(barLayout);
        String name = "new_table";
        nameInput = new TextInput("new_table", name);
        nameInput.getComp().setColumns(20);
        barPanel.add(new LabelInput(LangMgr.lang(Word2.NAME) + ":"), nameInput);
        barPanel.add(new HButton(LangMgr.lang(Word2.SAVE), FaSolidEnum.FLOPPY_DISK) {
            @Override
            protected void onClick() {
                try {
                    if (StringUtils.isBlank(nameInput.getValue())) {
                        PopDlgUtil.info(getComp(), LangMgr.sLang(CsL.FILL_TABLE_NAME));
                        return;
                    }

                    String sql = area.getText();
                    if (StringUtils.isBlank(sql)) {
                        sql = genSql();
                        if (StringUtils.isBlank(sql)) return;
                        area.setText(sql);
                    }
                    if (GlobalWorkUtil.exeSql(jdbcBean, CsVarUtil.conn, sql)) {
                        PopDlgUtil.info(getComp(), LangMgr.sLang(FrameL.TIP_SAVE_SUCCESS));
                        refreshTree();
                    }
                } catch (Exception e) {
                    PopDlgUtil.logErr(getClass(), getComp(), e);
                }
            }
        });
        tableInput = new TextInput("tablename",tableName);
        tableInput.getComp().setColumns(20);
        tableInput.setEnabled(false);
        barPanel.add(new LabelInput(LangMgr.lang(Word2.TABLE_NAME) + ":"), tableInput);
        barPanel.add(new HButton(LangMgr.sLang(CsL.COLUMN_SELECTION), FaSolidEnum.ALIGN_RIGHT) {
            @Override
            protected void onClick() {
                columnSelect();
            }
        });
        barPanel.add(new HButton(LangMgr.lang(Word2.CODE), FaSolidEnum.MAGNIFYING_GLASS) {
            @Override
            protected void onClick() {
                area.setText(genSql());
                hTabPanel.selectPanel(LangMgr.lang(Word2.CODE));
            }
        });


        HTAreaConf conf = new HTAreaConf();
        conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
        conf.setLineWrap(true);
        area = new HTextArea(conf);
        LastPanel areaLast = new LastPanel();
        areaLast.set(area.getComp());
        hTabPanel.setPanel(LangMgr.lang(Word2.CODE), areaLast, newHeaderConfig(LangMgr.lang(Word2.CODE)));
        hTabPanel.setPanel(LangMgr.sLang(CsL.COLUMN_SELECTION), selPanel = new ColSelPanel(), newHeaderConfig(LangMgr.sLang(CsL.COLUMN_SELECTION)));
        columnSelect();

        setHead(barPanel.getComp());
        set(hTabPanel.getComp());
    }

    private void columnSelect() {
        try {
            selPanel.loadData(jdbcBean, CsVarUtil.conn, schemaName, tableName, null);
            hTabPanel.selectPanel(LangMgr.sLang(CsL.COLUMN_SELECTION));
            if (selPanel.getTable().getRowCount() < 1) {
                PopDlgUtil.info(getComp(), LangMgr.sLang(CsL.NO_FORM_FOUND));
            }
        } catch (Exception e) {
            PopDlgUtil.logErr(getClass(), getComp(), e);
        }
    }

    private HeaderConfig newHeaderConfig(String name) {
        HeaderConfig headerConfig = new HeaderConfig(name);
        headerConfig.setTitleEditable(false);
        headerConfig.setFixTab(true);
        headerConfig.setDetachEnabled(false);
        return headerConfig;
    }

    @Override
    public String genSql() {
        if (StringUtils.isBlank(nameInput.getValue())) {
            PopDlgUtil.info(getComp(), LangMgr.sLang(CsL.FILL_TABLE_NAME));
            return area.getText();
        }
        if (StringUtils.isBlank(tableInput.getValue())) {
            PopDlgUtil.info(getComp(), LangMgr.sLang(CsL.WRITE_TABLE_NAME));
            return area.getText();
        }
        String sql = TableSqlUtil.getCopyTabSql(dbtype, ver, nameInput.getValue(), schemaName, tableName, selPanel.getSelected());
        
        return sql;
    }

    public abstract void refreshTree();
}
