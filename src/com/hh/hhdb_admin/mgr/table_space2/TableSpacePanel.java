package com.hh.hhdb_admin.mgr.table_space2;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.laf.LafIdEnum;
import com.hh.frame.swingui.laf.LafMgr;
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
import com.hh.hhdb_admin.mgr.table_space2.form.AbsTableSpaceForm;
import com.hh.hhdb_admin.mgr.table_space2.form.Db2TableSpaceForm;
import com.hh.hhdb_admin.mgr.table_space2.form.OracleTableSpaceForm;
import com.hh.hhdb_admin.mgr.table_space2.form.PgTableSpaceForm;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;


public class TableSpacePanel extends LastPanel {
    private AbsTableSpaceForm absForm;

    private HDialog dialog;


    public TableSpacePanel(Connection conn, JdbcInfo jdbc) throws Exception {
        DBTypeEnum dbType = DriverUtil.getDbType(jdbc);
        dialog = new HDialog(CsVarUtil.parentFrame, 700, 500);
        switch (dbType) {
            case hhdb:
            case pgsql:
            case tdsql4pg:
            case kingbase:
            case gaussdb:
            case greenplum:
                absForm = new PgTableSpaceForm(conn, jdbc);
                break;
            case oracle:
                absForm = new OracleTableSpaceForm(conn, jdbc);
                dialog.setSize(800, 700);
                break;
            case db2:
                absForm = new Db2TableSpaceForm(conn, jdbc);
                break;
            default:
                throw new Exception(LangMgr.lang(Word4.NOT_SUPPORT));
        }

        HBarPanel barPanel = new HBarPanel(getLayout(AlignEnum.LEFT));
        barPanel.add(new LabelInput(LangMgr.lang(Word2.NAME) + ":"), absForm.nameInput);
        if (dbType == DBTypeEnum.oracle) barPanel.add(absForm.addBtn, absForm.delBtn);
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
                        if (GlobalWorkUtil.exeSql(jdbc, conn, sql)) {
                            PopDlgUtil.info(dialog, LangMgr.lang(Word4.SAVED_SUCCESSFULLY));
                            dialog.dispose();
                        }
                        inform();
                    }
                } catch (Exception e) {
                    PopDlgUtil.logErr(getClass(), dialog, e);
                }
            }
        });

        setHead(barPanel.getComp());
        set(absForm.getLastPanel().getComp());
    }

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

    protected void inform() {
    }


    public static void main(String[] args) throws Exception {
        try {
            LangMgr.addLang(CsL.class);
            LafMgr.setLaf(LafIdEnum.FAST);

            JdbcInfo bean = new JdbcInfo();
            bean.setClazz(DriverUtil.ORA_DRIVER);
            bean.setUser("HU");
            bean.setPassword("123456");
            bean.setDbUrl("jdbc:oracle:thin:@192.168.2.253:1521/orcl");
//            bean.setSys(false);
//            bean.setUseSshTunnel(true);

//            SshBean sshBean = new SshBean();
//            sshBean.setIp("69.230.214.210");
//            sshBean.setPort(22);
//            sshBean.setUser("ec2-user");
//            sshBean.setAuthMode(SshAuthType.KEY);
//            sshBean.setKey("'-----BEGIN OPENSSH PRIVATE KEY-----\n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\nLmNvbXB1dGUuaW50ZXJuYWwBAgM=\n-----END OPENSSH PRIVATE KEY-----");
//            bean.setSshBean(sshBean);

            CsVarUtil.jdbc = bean;
            CsVarUtil.conn = CsVarUtil.newConn(bean);
            TableSpacePanel p = new TableSpacePanel(CsVarUtil.conn, CsVarUtil.jdbc);
            p.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
