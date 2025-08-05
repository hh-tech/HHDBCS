package com.hh.hhdb_admin.mgr.view2;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.db_sql.ViewSqlUtil;
import com.hh.frame.db_sql.conn.ViewConnUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.parser.AbsStmt;
import com.hh.frame.parser.ParserUtil;
import com.hh.frame.sqlwin.SqlType;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
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
import com.hh.hhdb_admin.comp.colsel.ColSelPanel;
import com.hh.hhdb_admin.mgr.table_open3.gui.OpenTableComp;


public class ViewPanel extends LastPanel {
    private HTextArea area;
    private TextInput nameInput, tableInput;
    private ColSelPanel selPanel;
    public OpenTableComp mcomp;

    private final JdbcBean jdbcBean;
    private String schema;
    private String viewName;
    private File tmpFile;
    private boolean isMaterialized;
    private DBTypeEnum.Version ver;
    private DBTypeEnum dbtype;


    public ViewPanel(JdbcBean jdbcBean, String schema, boolean isMaterialized) {
        this.jdbcBean = jdbcBean;
        this.schema = schema;
        this.isMaterialized = isMaterialized;
        this.tmpFile = new File(CsVarUtil.workspace + File.separator + "view_tmp");
        this.ver = jdbcBean.getCurrentVersion();
        this.dbtype = DriverUtil.getDbType(jdbcBean);
        HTabPanel hTabPanel = new HTabPanel();
        HBarLayout barLayout = HBarLayout.newLayout(AlignEnum.LEFT);
        barLayout.setTopHeight(5);
        barLayout.setBottomHeight(5);

        HBarPanel barPanel = new HBarPanel(barLayout);
        nameInput = new TextInput();
        nameInput.getComp().setColumns(20);
        barPanel.add(new LabelInput(LangMgr.lang(Word2.NAME) + ":"), nameInput);
        barPanel.add(new HButton(LangMgr.lang(Word2.SAVE), FaSolidEnum.FLOPPY_DISK) {
            @Override
            protected void onClick() {
                try {
                    if (StringUtils.isBlank(nameInput.getValue())) {
                        PopDlgUtil.info(getComp(), LangMgr.sLang(CsL.FILL_VIEW_NAME));
                        return;
                    }

                    String sql = area.getText();
                    if (StringUtils.isBlank(sql)) {
                        sql = getSql();
                        if (StringUtils.isBlank(sql)) return;
                        area.setText(sql);
                    }
                    if (GlobalWorkUtil.exeSql(jdbcBean, CsVarUtil.conn, sql)) {
                        PopDlgUtil.info(getComp(), LangMgr.sLang(FrameL.TIP_SAVE_SUCCESS));
                        inform(schema, isMaterialized);
                    }
                } catch (Exception e) {
                    PopDlgUtil.logErr(getClass(), getComp(), e);
                }
            }
        });
        tableInput = new TextInput();
        tableInput.getComp().setColumns(20);
        barPanel.add(new LabelInput(LangMgr.lang(Word2.TABLE_NAME) + ":"), tableInput);
        barPanel.add(new HButton(LangMgr.sLang(CsL.COLUMN_SELECTION), FaSolidEnum.ALIGN_RIGHT) {
            @Override
            protected void onClick() {
                loadTabData();
                hTabPanel.selectPanel(LangMgr.sLang(CsL.COLUMN_SELECTION));
            }
        });
        barPanel.add(new HButton(LangMgr.lang(Word2.CODE), FaSolidEnum.MAGNIFYING_GLASS) {
            @Override
            protected void onClick() {
                area.setText(getSql());
                hTabPanel.selectPanel(LangMgr.lang(Word2.CODE));
            }
        });

        barPanel.add(new HButton(LangMgr.lang(Word2.RUN), FaSolidEnum.PLAY) {
            @Override
            protected void onClick() {
                try {
                    String sql = area.getText();
                    if (StringUtils.isBlank(sql)) {
                        sql = getSql();
                        if (StringUtils.isBlank(sql)) return;
                        area.setText(sql);
                    }
                    if (dbtype == DBTypeEnum.db2 && isMaterialized) {
                        sql = StringUtils.substringBetween(sql.toLowerCase(), "as", " data initial");
                        sql = sql.substring(sql.indexOf("(")+1,sql.lastIndexOf(")"));
                    } else {
                        sql = sql.substring(sql.toLowerCase().indexOf("as")+2);
                    }

                    AbsStmt stmt = ParserUtil.getSplitStmts(dbtype, ver, SqlStrUtil.prepare4Split(sql)).get(0);
                    if (SqlWinUtil.getType(ver, stmt) == SqlType.QUERY) {
                        if (null == mcomp) {
                            mcomp = new OpenTableComp(jdbcBean, null, tmpFile, true);
                            hTabPanel.setPanel(LangMgr.lang(Word2.RUN), mcomp, newHeaderConfig(LangMgr.lang(Word2.RUN)));
                        }
                        mcomp.loadSql (SqlStrUtil.removeSemi(sql));
                        hTabPanel.selectPanel(LangMgr.lang(Word2.RUN));
                    } else {
                        PopDlgUtil.info(getComp(), LangMgr.sLang(CsL.SQL_STATEMENT_ERROR));
                    }
                } catch (Exception e) {
                    PopDlgUtil.logErr(getClass(), getComp(), e);
                }
            }
        });

        HTAreaConf conf = new HTAreaConf();
        conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
        conf.setLineWrap(true);
        area = new HTextArea(conf);
        LastPanel areaLast = new LastPanel();
        areaLast.set(area.getComp());
        hTabPanel.setPanel(LangMgr.lang(Word2.RUN), new HPanel(), newHeaderConfig(LangMgr.lang(Word2.RUN)));
        hTabPanel.setPanel(LangMgr.lang(Word2.CODE), areaLast, newHeaderConfig(LangMgr.lang(Word2.CODE)));
        hTabPanel.setPanel(LangMgr.sLang(CsL.COLUMN_SELECTION), selPanel = new ColSelPanel(), newHeaderConfig(LangMgr.sLang(CsL.COLUMN_SELECTION)));
        hTabPanel.selectPanel(LangMgr.lang(Word2.CODE));

        setHead(barPanel.getComp());
        set(hTabPanel.getComp());
    }

    private boolean loadTabData() {
        try {
            Map<String, String> map = strToSchemaTable();
            selPanel.loadData(jdbcBean, CsVarUtil.conn, map.get("schema"), map.get("tabName"), null);
            if (selPanel.getTable().getRowCount() < 1) {
                PopDlgUtil.info(getComp(), LangMgr.sLang(CsL.NO_FORM_FOUND));
                return false;
            }
        } catch (Exception e) {
            PopDlgUtil.logErr(getClass(), getComp(), e);
            return false;
        }
        return true;
    }

    /**
     * 根据view名称加载修改view的sql
     *
     * @param viewName 视图名称，当设计修改视图的必须设置视图名称
     */
    public void loadView(String viewName) throws Exception {
        this.viewName = viewName;
        String sql = ViewConnUtil.selectUpdateViewSql(dbtype, ver, CsVarUtil.conn, schema, viewName);
        area.setText(sql);
        nameInput.setValue(viewName);
        nameInput.setEnabled(false);
    }

    private HeaderConfig newHeaderConfig(String name) {
        HeaderConfig headerConfig = new HeaderConfig(name);
        headerConfig.setTitleEditable(false);
        headerConfig.setFixTab(true);
        headerConfig.setDetachEnabled(false);
        return headerConfig;
    }

    private String getSql() {
        if (!StringUtils.isNotBlank(tableInput.getValue())) {
            PopDlgUtil.info(getComp(), LangMgr.sLang(CsL.WRITE_TABLE_NAME));
            return area.getText();
        }
        if (!StringUtils.isNotBlank(nameInput.getValue())) {
            PopDlgUtil.info(getComp(), LangMgr.sLang(CsL.FILL_VIEW_NAME));
            return area.getText();
        }
        if (selPanel.getSelected().isEmpty() && (null == selPanel.getColNames() || selPanel.getColNames().isEmpty())) {
            if (!loadTabData()) return area.getText();
        }

        String selectColNames;
        if (selPanel.getSelected().isEmpty()) {
            selectColNames = StringUtils.join(selPanel.getColNames(), ",");
        } else {
            selectColNames = StringUtils.join(selPanel.getSelected(), ",");
        }
        String sql = "SELECT " + selectColNames + " FROM " + strToSchemaTable().get("val");
        String codeViewName = SqlStrUtil.rmQuote4Sel(nameInput.getValue(), dbtype, ver);
        if (StringUtils.isNotBlank(viewName)) {
            return ViewSqlUtil.getUpdateViewSql(dbtype, ver, schema, codeViewName, sql);
        } else {
            if (isMaterialized) {
                return ViewSqlUtil.getCreateMViewSql(dbtype, ver, schema, codeViewName, sql);
            } else {
                return ViewSqlUtil.getCreateViewSql(dbtype, ver, schema, codeViewName, sql);
            }
        }
    }

    private Map<String, String> strToSchemaTable() {
        Map<String, String> map = new HashMap<>();
        String v = DBTypeEnum.isInformixSeries(dbtype, ver) ? ":" : ".";
        //由于表名是用户自己输入的，需要从数据库中查询列，所以使用rmQuote4Sel进行字符转换
        String tabName = SqlStrUtil.rmQuote4Sel(tableInput.getValue(), dbtype, ver);
        if (tabName.indexOf(v) != -1) {//处理用户查询其他模式的表情况：test.tab
            map.put("schema", tabName.substring(0, tabName.indexOf(v)));
            map.put("tabName", tabName.substring(tabName.indexOf(v) + 1));
            map.put("val", tabName);
        } else {
            map.put("schema", schema);
            map.put("tabName", tabName);
            map.put("val", map.get("schema") + v + map.get("tabName"));
        }
        return map;
    }

    protected void inform(String schemaName, boolean isMaterialized) {
    }
}
