package com.hh.hhdb_admin.mgr.usr.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.table.comp.SqlViewDialog;
import com.hh.hhdb_admin.mgr.usr.util.FormUtil;
import com.hh.hhdb_admin.mgr.usr.util.UsrUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限管理面板
 */
public class PermComp {
    private HPanel panel;
    private HDialog dialog;
    private final SqlViewDialog sqlViewComp = new SqlViewDialog();
    private GrantTableComp basePermComp;
    private String usr;
    private static final String domainName = PermComp.class.getName();

    private final static String LK_TITLE = "PERM_TITLE";
    private final static String LK_PREVIEW_SQL = "PREVIEW_SQL";
    private final static String LK_SAVE = "SAVE";
    private final static String LK_CONN_ERROR = "CONN_ERROR";
    private final static String LK_SAVE_SUCCESS = "SAVE_SUCCESS";
    private final static String LK_SUPER_ERROR = "SUPER_ERROR";

    static {
        LangMgr.merge(domainName, com.hh.frame.lang.LangUtil.loadLangRes(PermComp.class));
    }

    public PermComp(Connection conn) {
        try {
            panel = new HPanel();
            basePermComp = FormUtil.getGrantTableComp(conn, GrantTableComp.PrivsType.sys);
            LastPanel lastPanel = new LastPanel(false);
            HBarLayout l = new HBarLayout();
            l.setAlign(AlignEnum.LEFT);
            l.setxGap(2);
            HBarPanel toolBar = new HBarPanel(l);
            HButton button = new HButton() {
                @Override
                protected void onClick() {

                    try {
                        String sql = basePermComp.getSql();
                        if (conn == null) {
                            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), LangMgr.getValue(domainName, LK_CONN_ERROR));
                            return;
                        }
                        if (StringUtils.isBlank(sql)) {
                            return;
                        }
                        if (UsrUtil.isSuperUsrExtra(conn, usr)) {
                            throw new Exception(LangMgr.getValue(domainName, LK_SUPER_ERROR));
                        }
                        String[] sqlList = sql.split(System.lineSeparator());
                        List<String> newSqlList = new ArrayList<>();
                        for (String sqlCode : sqlList) {
                            newSqlList.add(sqlCode.replace(";", ""));
                        }
                        SqlExeUtil.batchExecute(conn, newSqlList);
                        basePermComp.refreshTable();
                        dialog.dispose();
                        PopPaneUtil.info(StartUtil.parentFrame.getWindow(), LangMgr.getValue(domainName, LK_SAVE_SUCCESS));
                    } catch (Exception e) {
                        PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
                    }

                }
            };
            button.setText(LangMgr.getValue(domainName, LK_SAVE));
            HButton sqlBtn = new HButton() {
                @Override
                protected void onClick() {
                    try {
                        sqlViewComp.setSql(basePermComp.getSql());
                        sqlViewComp.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        PopPaneUtil.error(dialog.getWindow(), e);
                    }

                }
            };
            sqlBtn.setIcon(IconFileUtil.getIcon(new IconBean(CsMgrEnum.USR.name(), "viewsql", IconSizeEnum.SIZE_16)));
            sqlBtn.setText(LangMgr.getValue(domainName, LK_PREVIEW_SQL));
            button.setIcon(IconFileUtil.getIcon(new IconBean(CsMgrEnum.USR.name(), "save", IconSizeEnum.SIZE_16)));
            toolBar.add(button);
            toolBar.add(sqlBtn);
            lastPanel.setHead(toolBar.getComp());
            lastPanel.set(this.basePermComp.getComp());
            panel.setLastPanel(lastPanel);
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(dialog.getWindow(), e);
        }
    }


    /**
     * 显示面板
     *
     * @param dialog  窗口
     * @param usrName 用户名称
     */
    public void show(HDialog dialog, String usrName) {
        try {
            this.usr = usrName;
            this.dialog = dialog;
            dialog.setSize(750, 590);
            dialog.setWindowTitle(LangMgr.getValue(domainName, LK_TITLE) + "(" + usrName + ")");
            dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
            dialog.setRootPanel(panel);

            this.basePermComp.setPermUpdate(true);
            this.basePermComp.setPermUsr(usrName);
            this.basePermComp.refreshTable();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }

    }


}
