package com.hh.hhdb_admin.mgr.trigger2;

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
import com.hh.hhdb_admin.mgr.trigger2.form.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;

public class TriggerPanel extends LastPanel {
    private TriggerForm absForm;
    
    private HDialog dialog;
    
    public TriggerPanel(Connection conn, JdbcBean jdbcBean, String schema,String tableName) {
        try {
            dialog = new HDialog(CsVarUtil.parentFrame, 800, 600);
            DBTypeEnum typeEnum = DriverUtil.getDbType(jdbcBean);
            if (DBTypeEnum.isOraSeries(typeEnum,jdbcBean.getCurrentVersion())) {
                absForm = new OracleTriggerFrom(conn, jdbcBean, schema);
            } else if (typeEnum == DBTypeEnum.sqlserver) {
                absForm = new SqlserverTriggerForm(conn, jdbcBean, schema);
            } else if (DBTypeEnum.isMysqlSeries(typeEnum,jdbcBean.getCurrentVersion())) {
                absForm = new MysqlTriggerForm(conn, jdbcBean, schema,tableName);
            } else if (DBTypeEnum.isPgSeries(typeEnum,jdbcBean.getCurrentVersion())) {
                absForm = new PgTriggerForm(conn, jdbcBean, schema,tableName);
            } else if (typeEnum == DBTypeEnum.db2) {
                absForm = new Db2TriggerForm(conn, jdbcBean, schema);
            } else if (typeEnum == DBTypeEnum.sybase) {
                absForm = new SybaseTriggerFrom(conn, jdbcBean, schema);
            }else if (typeEnum==DBTypeEnum.derby){
                absForm = new DerbyTriggerFrom(conn, jdbcBean, schema);
            } else if (DBTypeEnum.isInformixSeries(typeEnum,jdbcBean.getCurrentVersion())) {
                absForm = new InformixTriggerFrom(conn, jdbcBean, schema);
            }else if (typeEnum==DBTypeEnum.sqlite){
                absForm = new SqliteTriggerForm(conn, jdbcBean, schema);
            }else if (typeEnum==DBTypeEnum.hana){
                absForm = new HanaTriggerFrom(conn, jdbcBean, schema);
            }
            
            HBarPanel barPanel = new HBarPanel(getLayout(AlignEnum.LEFT));
            barPanel.add(new LabelInput(LangMgr.lang(Word2.NAME) + ":"), absForm.nameInput);
            if (null != absForm.enableInput) barPanel.add(new LabelInput(LangMgr.lang(Word2.ENABLED) + ":"), absForm.enableInput);
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
        } catch (Exception e) {
            PopDlgUtil.logErr(TriggerPanel.class, CsVarUtil.parentFrame, e);
        }
    }
    
    /**
     * 新增用户 显示
     */
    public void show() {
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
        bean.setClazz(DriverUtil.PG_DRIVER);
        bean.setUser("postgres");
        bean.setPassword("123123");
        bean.setDbUrl("jdbc:postgresql://192.168.2.250:5432/postgres");
        
        CsVarUtil.conn = ConnUtil.getConn(bean);
        CsVarUtil.jdbc = bean;
        TriggerPanel p = new TriggerPanel(CsVarUtil.conn,CsVarUtil.jdbc, "test","company");
        p.show();
    }
}
