package com.hh.hhdb_admin.mgr.view.comp;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.viewMr.mr.AbsViewMr;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.table_open.ModifyTabDataComp;

/**
 * 打开视图的组件
 * author:yangxianhui
 * date:2020-10-20
 */
public class OpenViewComp {
    private final JdbcBean jdbcBean;
    private static final String domainName = OpenViewComp.class.getName();
    private static final String LK_OPEN_VIEW = "OPEN_VIEW";
    private static final String LK_OPEN_MVIEW = "OPEN_MVIEW";
    private static final String OPEN_TMP = "open_tmp";
    private final AbsViewMr absViewMr;
    private final File tmpFile;

    static {
        try {
			LangMgr2.loadMerge(OpenViewComp.class);
		} catch (IOException e) {
			PopPaneUtil.error(e);
		}
    }

    public OpenViewComp(JdbcBean bean) {
        this.jdbcBean = bean;
        this.absViewMr = AbsViewMr.genViewMr(bean);
        this.tmpFile = new File(StartUtil.workspace + File.separator + OPEN_TMP);
    }


    /**
     * 组件显示
     */
    public void show(String schemaName, String viewName, boolean isMaterialized) {

        try {
            if (this.absViewMr == null || (isMaterialized && !this.absViewMr.isSupportMView())) {
                throw new Exception("暂不支持数据库类型");
            }
            HPanel panel = new HPanel();
            jdbcBean.setCurSessionSchema(DbCmdStrUtil.toDbCmdStr(schemaName, DriverUtil.getDbType(this.jdbcBean)));
            ModifyTabDataComp mcomp = new ModifyTabDataComp(jdbcBean, null, tmpFile);
            String sql = String.format("select * from %s.%s",
                    jdbcBean.getCurSessionSchema(),
                    DbCmdStrUtil.toDbCmdStr(viewName, DriverUtil.getDbType(this.jdbcBean)));
            mcomp.loadReadOnlyTable(sql);
            panel.setLastPanel(mcomp);
            String title = isMaterialized ? LK_OPEN_MVIEW : LK_OPEN_VIEW;
            HDialog dialog = new HDialog(StartUtil.parentFrame, 800, 550);
            dialog.getWindow().addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    mcomp.close();
                }
            });

            dialog.setWindowTitle(LangMgr2.getValue(domainName, title) + "(" + viewName + ")");
            dialog.setIconImage(IconFileUtil.getLogo().getImage());
            dialog.setRootPanel(panel);
            dialog.setSize(750, 550);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
    }

}
