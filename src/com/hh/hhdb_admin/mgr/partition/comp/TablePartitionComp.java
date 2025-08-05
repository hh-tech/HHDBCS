package com.hh.hhdb_admin.mgr.partition.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
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
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

/**
 * 表分区添加页面
 */
public class TablePartitionComp extends LastPanel {
    private AbsParPanel absPanel;
    
    private HDialog dialog;
    
    public TablePartitionComp(Connection conn, JdbcBean jdbcBean, String schema, String tableName) {
        try {
            dialog = new HDialog(CsVarUtil.parentFrame, 600, 400);
            DBTypeEnum typeEnum = DriverUtil.getDbType(jdbcBean);
            if (DBTypeEnum.isPgSeries(typeEnum, jdbcBean.getCurrentVersion())) {
                switch (typeEnum) {
                    case gaussdb:
                        absPanel = new GaussParPanel(conn, jdbcBean, schema, tableName);
                        break;
                    case greenplum:
                        absPanel = new GreenPlumParPanel(conn, jdbcBean, schema, tableName);
                        break;
                    default:
                        absPanel = new PgPartPanel(conn, jdbcBean, schema, tableName);
                        break;
                }
            } else if (typeEnum == DBTypeEnum.db2) {
                absPanel = new Db2PartPanel(conn, jdbcBean, schema, tableName);
            } else {
                throw new Exception("暂不支持" + typeEnum);
            }
            
            HBarPanel barPanel = new HBarPanel(getLayout(AlignEnum.LEFT));
            barPanel.add(new LabelInput(LangMgr.lang(Word4.PARTITION_NAME) + ":"), absPanel.nameInput);
            barPanel.add(new HButton(LangMgr.lang(Word2.CODE), FaSolidEnum.MAGNIFYING_GLASS) {
                @Override
                protected void onClick() {
                    if (check()) {
                        absPanel.area.setText(absPanel.getSql());
                    }
                }
            });
            barPanel.add(new HButton(LangMgr.lang(Word2.SAVE), FaSolidEnum.FLOPPY_DISK) {
                @Override
                protected void onClick() {
                    try {
                        if (check()) {
                            String sql = absPanel.area.getText();
                            if (StringUtils.isBlank(sql)) {
                                sql = absPanel.getSql();
                                if (StringUtils.isBlank(sql)) return;
                                absPanel.area.setText(sql);
                            }
                            if (GlobalWorkUtil.exeSql(jdbcBean, conn, sql)) {
                                refresh();
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
            set(absPanel.getLastPanel().getComp());
        } catch (Exception e) {
            PopDlgUtil.logErr(TablePartitionComp.class, CsVarUtil.parentFrame, e);
        }
    }
    
    /**
     * 新增用户 显示
     */
    public void show() {
        dialog.setWindowTitle(LangMgr.lang(Word4.ADD_PARTITION) + "("+ absPanel.parType +")");
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
        String str = absPanel.checkForm();
        if (StringUtils.isNotBlank(str)) {
            PopDlgUtil.info(dialog, str);
            return false;
        }
        return true;
    }
    
    protected void refresh() {
    }
    
}
