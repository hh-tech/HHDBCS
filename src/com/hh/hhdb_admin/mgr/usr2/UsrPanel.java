package com.hh.hhdb_admin.mgr.usr2;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.attributeMr.AttrHeardType;
import com.hh.frame.dbobj3.AllDbObjType;
import com.hh.frame.dbobj3.pg.PgInstanceObj;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.mgr.usr2.form.*;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.Map;


public class UsrPanel extends LastPanel {
    private UsrBean usrBean;
    private UsrBaseForm form;
    private HDialog dialog;
    
    
    public UsrPanel(UsrBean usrBean,JdbcBean jdbc, Connection conn) throws Exception {
        try {
            this.usrBean = usrBean;
            dialog = new HDialog(CsVarUtil.parentFrame, 800, 600);
            DBTypeEnum typeEnum = usrBean.getDbType();
            if (typeEnum == DBTypeEnum.oracle) {
                form = new OrUsrForm(usrBean,conn);
            } else  if (typeEnum == DBTypeEnum.dm) {
                form = new DmUsrForm(usrBean,conn);
            } else if (DBTypeEnum.isPgSeries(typeEnum,usrBean.getVersion())) {
                form = new PgUsrForm(usrBean,conn);
            } else if (DBTypeEnum.isMysqlSeries(typeEnum,usrBean.getVersion())) {
                form = new MysqlUsrForm(usrBean, conn);
            } else if (typeEnum == DBTypeEnum.sqlserver) {
                form = new SqlServerUsrForm(usrBean, conn);
            } else if (typeEnum == DBTypeEnum.clickhouse) {
                form = new ClickHouseUsrForm(usrBean, conn);
            } else if (typeEnum == DBTypeEnum.hana) {
                form = new HanaUsrForm(usrBean, conn);
            }else {
                throw new Exception(LangMgr.sLang(CsL.DB_NOT_SUPPORTED));
            }
    
            HBarPanel barPanel = new HBarPanel(getLayout(AlignEnum.LEFT));
            if (usrBean.getOprType() != UsrBean.OprType.PERM) {
                barPanel.add(new LabelInput(LangMgr.lang(Word2.NAME) + ":"),form.nameInput);
                if (usrBean.isUser()) barPanel.add(new LabelInput(LangMgr.lang(Word2.PASSWORD) + ":"),form.pwInput);
            }
            barPanel.add(new HButton(LangMgr.lang(Word2.CODE),FaSolidEnum.MAGNIFYING_GLASS) {
                @Override
                protected void onClick() {
                    if (usrBean.getOprType() != UsrBean.OprType.PERM) {
                        if (check()) {
                            form.area.setText(form.getSql());
                            form.inform();
                        }
                    } else {
                        form.area.setText(form.getSql());
                        form.inform();
                    }
                }
            });
            barPanel.add(new HButton(LangMgr.lang(Word2.SAVE),FaSolidEnum.FLOPPY_DISK) {
                @Override
                protected void onClick() {
                    try {
                        if (usrBean.getOprType() != UsrBean.OprType.PERM) {
                            if (check()) {
                                String sql = form.area.getText();
                                if (StringUtils.isBlank(sql)) {
                                    sql = form.getSql();
                                    if (StringUtils.isBlank(sql)) return;
                                    form.area.setText(sql);
                                }
                                if (GlobalWorkUtil.exeSql(jdbc,CsVarUtil.conn, sql)) {
                                    PopDlgUtil.info(dialog, LangMgr.lang(Word4.SAVED_SUCCESSFULLY));
                                }
                                inform();
                            }
                        } else {
                            String sql = form.area.getText();
                            if (StringUtils.isBlank(sql)) {
                                sql = form.getSql();
                                if (StringUtils.isBlank(sql)) return;
                                form.area.setText(sql);
                            }

                            DBTypeEnum.Version ver = jdbc.getCurrentVersion();
                            if (DBTypeEnum.isPgSeries(typeEnum, ver)) {
                                PgInstanceObj pgInstanceObj = new PgInstanceObj(ver, conn);
                                Map<String, String> usrProp = pgInstanceObj.getObjProp(AllDbObjType.USER, usrBean.getName());
                                if ("t".equals(usrProp.get(AttrHeardType.IS_SUPERUSER.name()))) {
                                    PopDlgUtil.info(dialog, LangMgr.sLang(CsL.SUPER_ERROR));
                                    return;
                                }
                            }
                            if (GlobalWorkUtil.exeSql(jdbc,CsVarUtil.conn, sql)) {
                                PopDlgUtil.info(dialog, LangMgr.lang(Word4.SAVED_SUCCESSFULLY));
                            }
                        }
                    } catch (Exception e) {
                        PopDlgUtil.logErr(getClass(),dialog, e);
                    }
                }
            });
    
            setHead(barPanel.getComp());
            set(form.getLastPanel().getComp());
        } catch (Exception e) {
            PopDlgUtil.logErr(UsrPanel.class,CsVarUtil.parentFrame, e);
        }
    }

    public void show() {
        if (usrBean.isUser()) {
            dialog.setWindowTitle(LangMgr.lang(Word4.ADD_USER));
        } else if (usrBean.isRole()) {
            dialog.setWindowTitle(LangMgr.lang(Word4.CREATE_ROLE));
        }
        dialog.setRootPanel(this);
        dialog.setStatusBar(new HBarPanel());
        dialog.show();
    }

    public void show(String usrName) {
        try {
            if (usrBean.isUser()) {
                form.upInitForm(usrName);
                dialog.setWindowTitle(LangMgr.lang(Word4.MODIFY_USER) + "(" + usrName + ")");
            } else if (usrBean.isRole()) {
                form.upInitForm(usrName);
                dialog.setWindowTitle(LangMgr.lang(Word4.UPDATE_ROLE) + "(" + usrName + ")");
            } else {
                dialog.setWindowTitle(LangMgr.lang(Word4.PERMISSIONS_MANAGEMENT) + "(" + usrName + ")");
            }
            dialog.setRootPanel(this);
            dialog.setStatusBar(new HBarPanel());
            dialog.show();
        } catch (Exception e) {
            Lg.error(UsrPanel.class, e);
            PopDlgUtil.error(CsVarUtil.parentFrame, e);
        }
    }
    
    private boolean check() {
        String str = form.checkForm();
        if (StringUtils.isNotBlank(str)) {
            PopDlgUtil.info(dialog, str);
            return false;
        }
        return true;
    }
    
    private HBarLayout getLayout(AlignEnum align) {
        HBarLayout barLayout = HBarLayout.newLayout(align);
        barLayout.setTopHeight(5);
        barLayout.setBottomHeight(5);
        return barLayout;
    }
    
    protected void inform() {}
}
