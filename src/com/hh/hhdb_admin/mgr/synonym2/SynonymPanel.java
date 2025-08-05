package com.hh.hhdb_admin.mgr.synonym2;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
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
import com.hh.hhdb_admin.mgr.synonym2.form.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;

public class SynonymPanel extends LastPanel {
    private HDialog dialog;
    private SynBaseForm absForm;
    
    public SynonymPanel(Connection conn,JdbcBean jdbcBean, String schema) {
        try {
            dialog = new HDialog(CsVarUtil.parentFrame, 800, 600);
            DBTypeEnum typeEnum = DriverUtil.getDbType(jdbcBean);
            if (DBTypeEnum.isOraSeries(typeEnum, jdbcBean.getCurrentVersion())) {
                if (typeEnum == DBTypeEnum.dm || jdbcBean.getCurrentVersion() == DBTypeEnum.Version.ob4ora) {
                    absForm = new Ob4oraSynForm(conn, jdbcBean, schema);
                } else {
                    absForm = new OracleSynForm(conn, jdbcBean, schema);
                }
            } else if (typeEnum == DBTypeEnum.sqlserver || typeEnum == DBTypeEnum.gaussdb || typeEnum == DBTypeEnum.kingbase || typeEnum == DBTypeEnum.derby) {
                absForm = new SqlServerSynForm(conn, jdbcBean, schema);
            } else if (typeEnum == DBTypeEnum.gbase || typeEnum == DBTypeEnum.informix) {
                absForm = new GBaseSynForm(conn, jdbcBean, schema);
            } else if (typeEnum == DBTypeEnum.hana) {
                absForm = new HanaSynForm(conn, jdbcBean, schema);
            }
    
            HBarPanel barPanel = new HBarPanel(getLayout(AlignEnum.LEFT));
            barPanel.add(new LabelInput(LangMgr.lang(Word2.NAME) + ":"), absForm.nameInput);
            if (null != absForm.publicInput)
                barPanel.add(new LabelInput(LangMgr.lang(Word2.PUBLIC) + ":"), absForm.publicInput);
            barPanel.add(new HButton(LangMgr.lang(Word2.CODE), FaSolidEnum.MAGNIFYING_GLASS) {
                @Override
                protected void onClick() {
                    if (check()) {
                        absForm.area.setText(absForm.getSql());
                    }
                }
            });
            barPanel.add(new HButton(LangMgr.lang(Word2.SAVE), FaSolidEnum.FLOPPY_DISK) {
                @Override
                protected void onClick() {
                    try {
                        if (check()) {
                            String sql = absForm.area.getText();
                            if (StringUtils.isBlank(sql)) {
                                sql = absForm.getSql();
                                if (StringUtils.isBlank(sql)) return;
                                absForm.area.setText(sql);
                            }
                            if (GlobalWorkUtil.exeSql(jdbcBean,conn, sql)) {
                                inform();
                                PopDlgUtil.info(dialog, LangMgr.lang(Word4.SAVED_SUCCESSFULLY));
                                dialog.dispose();
                            }
                        }
                    } catch (Exception e) {
                        PopDlgUtil.logErr(getClass(), dialog, e);
                    }
                }
            });
            
            setHead(barPanel.getComp());
            set(absForm.getLastPanel().getComp());
    
            dialog.setWindowTitle(LangMgr.lang(Word2.ADD));
            dialog.setRootPanel(this);
            dialog.setStatusBar(new HBarPanel());
            dialog.show();
        } catch (Exception e) {
            PopDlgUtil.logErr(SynonymPanel.class, CsVarUtil.parentFrame, e);
        }
    }
    
    private HBarLayout getLayout(AlignEnum align) {
        HBarLayout barLayout = HBarLayout.newLayout(align);
        barLayout.setTopHeight(5);
        barLayout.setBottomHeight(5);
        return barLayout;
    }
    
    private boolean check() {
        String str = absForm.checkForm();
        if (StringUtils.isNotBlank(str)) {
            PopDlgUtil.info(dialog, str);
            return false;
        }
        return true;
    }
    
    protected void inform() {}
    
    public static void main(String[] args) throws Exception {
        try {
            LangMgr.addLang(CsL.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        JdbcInfo bean = new JdbcInfo();
        bean.setClazz(DriverUtil.ORA_DRIVER);
        bean.setUser("TSP");
        bean.setPassword("123456");
        bean.setDbUrl("jdbc:oracle:thin:@192.168.2.253:1521/orcl");
        
        CsVarUtil.conn = ConnUtil.getConn(bean);
        CsVarUtil.jdbc = bean;
//        SynonymPanel p = new SynonymPanel(CsVarUtil.conn,CsVarUtil.jdbc,  "TSP");
    }
}
