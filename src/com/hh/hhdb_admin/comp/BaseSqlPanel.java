package com.hh.hhdb_admin.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseSqlPanel extends LastPanel {
    protected final Connection conn = CsVarUtil.conn;
    protected final JdbcBean jdbc = CsVarUtil.jdbc;
    protected final DBTypeEnum dbtype = CsVarUtil.getDbtype();
    protected final DBTypeEnum.Version ver = jdbc.getCurrentVersion();
    protected HTextArea sqlTextArea;
    protected HButton saveButton;
    protected HButton codeButton;
    public static final String NEW_LINE = ";\n";
    private Runnable afterSave = () -> {
    };

    public BaseSqlPanel() {
    }

    /**
     * 需要手动调用
     */
    protected void init() {
        try {
            initPanel();
            initValue();
        } catch (Exception e) {
            PopDlgUtil.error(getComp(), e);
            Lg.error(getClass(), e);
        }
    }

    protected void initPanel() throws SQLException {
        HTAreaConf conf = new HTAreaConf();
        conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
        sqlTextArea = new HTextArea(conf);

        saveButton = new HButton(LangMgr.lang(Word2.SAVE), FaSolidEnum.FLOPPY_DISK) {
            @Override
            protected void onClick() {
                try {
                    save();
                } catch (Exception e) {
                    PopDlgUtil.info(getComp(), e.getMessage());
                    Lg.error(getClass(), e);
                }
            }
        };
        codeButton = new HButton(LangMgr.lang(Word2.CODE), FaSolidEnum.MAGNIFYING_GLASS) {
            @Override
            protected void onClick() {
                try {
                    code();
                } catch (Exception e) {
                    PopDlgUtil.info(getComp(), e.getMessage());
                    Lg.error(getClass(), e);
                }
            }
        };
    }

    protected void initValue() throws SQLException {
    }

    protected HBarPanel createBarPanel() {
        HBarPanel hBarPanel = new HBarPanel(HBarLayout.newLayout(AlignEnum.RIGHT));
        hBarPanel.add(codeButton, saveButton);
        return hBarPanel;
    }

    protected LastPanel createSqlPanel() {
        LastPanel sqlPanel = new LastPanel();
        sqlPanel.setTitle(LangMgr.lang(Word2.CODE));
        sqlPanel.set(sqlTextArea.getComp());
        return sqlPanel;
    }

    /**
     * 生成代码到sql域
     */
    protected void code() throws Exception {
        check();
        String sql = genSql();
        if (sql == null)
            throw new Exception(LangMgr.lang(Word4.NOT_SUPPORT));
        sqlTextArea.setText(sql);
    }

    /**
     * 执行sql域的代码，没有时调用{@code code()}
     */
    public void save() throws Exception {
        String sql = sqlTextArea.getText();
        if (StringUtils.isBlank(sql))
            code();
        sql = sqlTextArea.getText();
        if (StringUtils.isBlank(sql) || GlobalWorkUtil.exeSql(CsVarUtil.jdbc, CsVarUtil.conn, sql)) {
            PopDlgUtil.info(getComp(), LangMgr.sLang(FrameL.TIP_SAVE_SUCCESS));
            afterSave.run();
        }
    }

    public void setAfterSave(Runnable afterSave) {
        this.afterSave = afterSave;
    }

    /**
     * 生成代码前的检查，抛出的异常以对话框形式弹出
     */
    public void check() throws Exception {
    }

    /**
     * 生成代码
     */
    public abstract String genSql() throws Exception;

    public HDialog createDialog(String title) {
        HDialog hDialog = new HDialog();
        hDialog.setStatusBar(new HBarPanel());
        hDialog.setWindowTitle(title);
        hDialog.setIconImage(IconFileUtil.getLogo());
        hDialog.setRootPanel(this);
        return hDialog;
    }
}
