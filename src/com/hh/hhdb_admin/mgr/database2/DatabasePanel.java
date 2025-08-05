package com.hh.hhdb_admin.mgr.database2;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.mongo.obj.MongoDb;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.mgr.database2.form.*;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;


public class DatabasePanel extends LastPanel {
    private DBTypeEnum dbTypeEnum = CsVarUtil.getDbtype();
    private AbsForm databaseForm;

    private HDialog dialog;
    private HTextArea area;


    public DatabasePanel(JdbcBean jdbc, Connection conn) throws Exception {
        dialog = new HDialog(CsVarUtil.parentFrame, 600, 500);
        if (DBTypeEnum.isPgSeries(dbTypeEnum, jdbc.getCurrentVersion())) {
            databaseForm = new HhPgForm(jdbc, conn);
        } else if (DBTypeEnum.isMysqlSeries(dbTypeEnum, jdbc.getCurrentVersion())) {
            databaseForm = new MysqlForm(jdbc, conn);
        } else if (dbTypeEnum == DBTypeEnum.sqlserver) {
            databaseForm = new SQLServerForm(jdbc, conn);
        } else if (dbTypeEnum == DBTypeEnum.clickhouse) {
            databaseForm = new ClickHouseForm(jdbc, conn);
        } else if (dbTypeEnum == DBTypeEnum.mongodb) {
            databaseForm = new MongodbForm(jdbc, conn);
            dialog.setSize(400, 200);
        } else {
            throw new Exception("暂不支持" + dbTypeEnum);
        }

        HBarPanel barPanel = new HBarPanel(getLayout(AlignEnum.LEFT));
        barPanel.add(new LabelInput(LangMgr.lang(Word2.NAME) + ":"));
        barPanel.add(databaseForm.nameInput);
        if (dbTypeEnum != DBTypeEnum.mongodb) {
            barPanel.add(new HButton(LangMgr.lang(Word2.CODE), FaSolidEnum.MAGNIFYING_GLASS) {
                @Override
                protected void onClick() {
                    if (check()) {
                        area.setText(databaseForm.getSql());
                    }
                }
            });
        }
        barPanel.add(new HButton(LangMgr.lang(Word2.SAVE), FaSolidEnum.FLOPPY_DISK) {
            @Override
            protected void onClick() {
                try {
                    if (check()) {
                        if (DBTypeEnum.mongodb == dbTypeEnum) {
                            MongoDb mongoDb = new MongoDb(conn);
                            mongoDb.newDb(databaseForm.nameInput.getValue(), databaseForm.getSql());
                            inform();
                            PopDlgUtil.info(getComp(), LangMgr.sLang(FrameL.TIP_SAVE_SUCCESS));
                            dialog.dispose();
                        } else {
                            String sql = area.getText();
                            if (StringUtils.isBlank(sql)) {
                                sql = databaseForm.getSql();
                                if (StringUtils.isBlank(sql)) return;
                                area.setText(sql);
                            }
                            if (GlobalWorkUtil.exeSql(jdbc, CsVarUtil.conn, sql)) {
                                inform();
                                PopDlgUtil.info(getComp(), LangMgr.sLang(FrameL.TIP_SAVE_SUCCESS));
                                dialog.dispose();
                            }
                        }
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
        areaLast.setTitle(LangMgr.lang(Word2.CODE));

        HPanel areaPanel = new HPanel();
        areaPanel.add(databaseForm.getPanel());
        if (DBTypeEnum.mongodb != dbTypeEnum) areaPanel.setLastPanel(areaLast);
        setHead(barPanel.getComp());
        set(areaPanel.getComp());

        dialog.setWindowTitle(LangMgr.lang(Word2.ADD));
        dialog.setRootPanel(this);
        dialog.setStatusBar(new HBarPanel());
        dialog.show();
    }

    private HBarLayout getLayout(AlignEnum align) {
        HBarLayout barLayout = HBarLayout.newLayout(align);
        barLayout.setTopHeight(5);
        barLayout.setBottomHeight(5);
        return barLayout;
    }

    private boolean check() {
        databaseForm.inform();
        if (StringUtils.isEmpty(databaseForm.nameInput.getValue())) {
            PopDlgUtil.info(getComp(), LangMgr.sLang(CsL.ENTER_DB_NAME));
            return false;
        }
        if (DBTypeEnum.mongodb == dbTypeEnum) {
            if (StringUtils.isEmpty(databaseForm.getSql())) {
                PopDlgUtil.info(getComp(), LangMgr.sLang(CsL.COLLECTION_NAME_NOT_NULL));
                return false;
            }
        }
        return true;
    }

    protected void inform() {
    }
}
