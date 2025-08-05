package com.hh.hhdb_admin.mgr.type;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.mgr.type.form.OrTypeForm;
import com.hh.hhdb_admin.mgr.type.form.PgTypeForm;
import com.hh.hhdb_admin.mgr.type.form.TypeBaseForm;

import java.sql.Connection;


public class TypePanel extends LastPanel {
    private TypeBaseForm form;
    private HDialog dialog;
    
    
    public TypePanel(TypeBean typeBean, Connection conn) throws Exception {
        try {
            dialog = new HDialog(CsVarUtil.parentFrame, 800, 600);
            if (CsVarUtil.getDbtype() == DBTypeEnum.oracle) {
                form = new OrTypeForm(conn, typeBean);
            } else  if (CsVarUtil.getDbtype() == DBTypeEnum.pgsql) {
                form = new PgTypeForm(conn, typeBean);
            } else {
                throw new Exception(LangMgr.sLang(CsL.DB_NOT_SUPPORTED));
            }
            
            HBarPanel barPanel = new HBarPanel(getLayout(AlignEnum.LEFT));
            barPanel.add(new HButton(LangMgr.lang(Word2.SAVE), FaSolidEnum.FLOPPY_DISK) {
                @Override
                protected void onClick() {
                    form.save();
                    inform();
                }
            });
            barPanel.add(new HButton(LangMgr.lang(Word3.FORMATTING), FaSolidEnum.WAND_MAGIC_SPARKLES) {
                @Override
                protected void onClick() {
                    GlobalWorkUtil.formatSql(form.area);
                }
            });
            
            setHead(barPanel.getComp());
            set(form.getLastPanel().getComp());
        } catch (Exception e) {
            PopDlgUtil.logErr(TypePanel.class, CsVarUtil.parentFrame, e);
        }
    }
    
    public void show() {
        dialog.setWindowTitle(LangMgr.lang(Word2.ADD));
        dialog.setRootPanel(this);
        dialog.setStatusBar(new HBarPanel());
        dialog.show();
    }

    public void show(String usrName) {
        try {
            form.upInitForm();
            dialog.setWindowTitle(LangMgr.lang(Word4.MODIFY_USER) + "(" + usrName + ")");
            dialog.setRootPanel(this);
            dialog.setStatusBar(new HBarPanel());
            dialog.show();
        } catch (Exception e) {
            Lg.error(TypePanel.class, e);
            PopDlgUtil.error(CsVarUtil.parentFrame, e);
        }
    }
    
    
    private HBarLayout getLayout(AlignEnum align) {
        HBarLayout barLayout = HBarLayout.newLayout(align);
        barLayout.setTopHeight(5);
        barLayout.setBottomHeight(5);
        return barLayout;
    }
    
    protected void inform() {
    }
}
