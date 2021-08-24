package com.hh.hhdb_admin.mgr.usr.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.userMr.base.UsrFormType;
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
import com.hh.hhdb_admin.mgr.usr.comp.form.UsrBaseForm;
import com.hh.hhdb_admin.mgr.usr.util.FormUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.Arrays;

/**
 * 创建或修改用户 /角色/登录名
 */
public abstract class AddUpdUsrComp {
    private DBTypeEnum typeEnum;
    private UsrFormType compType;
    private UsrBaseForm form;
    private HPanel panel;
    private HDialog dialog;
    private Connection conn;
    private final SqlViewDialog sqlViewComp = new SqlViewDialog();
    private static final String domainName = AddUpdUsrComp.class.getName();
    private final static String LK_ADD_TITLE = "ADD_TITLE";
    private final static String LK_UPDATE_TITLE = "UPDATE_TITLE";
    private final static String LK_ADD_ROLE_TITLE = "ADD_ROLE_TITLE";
    private final static String LK_ADD_LOGIN_TITLE = "ADD_LOGIN_TITLE";
    private final static String LK_UPDATE_ROLE_TITLE = "UPDATE_ROLE_TITLE";
    private final static String LK_PREVIEW_SQL = "PREVIEW_SQL";
    private final static String LK_SAVE = "SAVE";
    private final static String LK_ERROR = "ERROR";
    private final static String LK_CONN_ERROR = "CONN_ERROR";
    private final static String LK_SAVE_SUCCESS = "SAVE_SUCCESS";

    static {
        LangMgr.merge(domainName, com.hh.frame.lang.LangUtil.loadLangRes(AddUpdUsrComp.class));
    }

    public AddUpdUsrComp(Connection conn, UsrFormType compType) {
        try {
            this.conn = conn;
            this.compType = compType;
            this.typeEnum = DriverUtil.getDbType(conn);
            panel = new HPanel();
            LastPanel lastPanel = new LastPanel(false);
            lastPanel.setHead(getToolBar().getComp());
            this.form = FormUtil.getUsrBaseForm(this.typeEnum, this.conn, this.compType);
            lastPanel.setWithScroll(form.getComp());
            panel.setLastPanel(lastPanel);
        } catch (Exception e) {
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
    }


    /**
     * 新增用户 显示
     */
    public void show(HDialog dialog) {
        try {
            if (form == null) {
                throw new Exception("当前数据库未实现该功能");
            }
            dialog.setSize(form.getSize().width, form.getSize().height);
            form.setUpdate(false);
            form.clearForm();
            dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
            if (compType == UsrFormType.usr) {
                dialog.setWindowTitle(LangMgr.getValue(domainName, LK_ADD_TITLE));
            } else if (compType == UsrFormType.role) {
                dialog.setWindowTitle(LangMgr.getValue(domainName, LK_ADD_ROLE_TITLE));
            } else if (compType == UsrFormType.login) {
                dialog.setWindowTitle(LangMgr.getValue(domainName, LK_ADD_LOGIN_TITLE));
            }

            dialog.setRootPanel(panel);
            this.dialog = dialog;
            dialog.show();

        } catch (Exception e) {
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
    }

    /**
     * 修改用户 显示
     */
    public void show(HDialog dialog, String usrName) {
        try {
            if (form == null) {
                throw new Exception("当前数据库未实现该功能");
            }
            dialog.setSize(form.getSize().width, form.getSize().height);
            form.setUpdate(true);
            form.clearForm();
            form.initForm(usrName);
            if (compType == UsrFormType.usr) {
                dialog.setWindowTitle(LangMgr.getValue(domainName, LK_UPDATE_TITLE) + "(" + usrName + ")");
            } else if (compType == UsrFormType.role) {
                dialog.setWindowTitle(LangMgr.getValue(domainName, LK_UPDATE_ROLE_TITLE) + "(" + usrName + ")");
            } else if (compType == UsrFormType.login) {
                dialog.setWindowTitle(LangMgr.getValue(domainName, LK_ADD_LOGIN_TITLE) + "(" + usrName + ")");
            }

            dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
            dialog.setRootPanel(panel);
            this.dialog = dialog;
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
    }


    /**
     * 刷新左侧的用户集合
     */
    protected abstract void informRefreshUsr();


    /**
     * 工具栏
     */
    private HBarPanel getToolBar() {
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        l.setxGap(2);
        HBarPanel toolBar = new HBarPanel(l);

        HButton button = new HButton() {
            @Override
            protected void onClick() {
                clickSaveBtn();
            }
        };
        button.setText(LangMgr.getValue(domainName, LK_SAVE));
        button.setIcon(IconFileUtil.getIcon(new IconBean(CsMgrEnum.USR.name(), "save", IconSizeEnum.SIZE_16)));

        HButton sqlBtn = new HButton() {
            @Override
            protected void onClick() {
                clickSqlViewBtn();
            }
        };
        sqlBtn.setText(LangMgr.getValue(domainName, LK_PREVIEW_SQL));
        sqlBtn.setIcon(IconFileUtil.getIcon(new IconBean(CsMgrEnum.USR.name(), "viewsql", IconSizeEnum.SIZE_16)));
        toolBar.add(button);
        toolBar.add(sqlBtn);
        return toolBar;
    }

    /**
     * 点击sql预览按钮
     */
    private void clickSqlViewBtn() {
        try {
            sqlViewComp.setSql(form.getSql());
            if (form.checkForm().getCode() == 0) {
                sqlViewComp.show();
            } else {
                PopPaneUtil.error(dialog.getWindow(), form.checkForm().getMsg());
            }
        } catch (Exception e) {
            PopPaneUtil.error(dialog.getWindow(), e);
        }
    }

    /**
     * 点击保存按钮
     */
    private void clickSaveBtn() {
        try {
            if (form.checkForm().getCode() == 0) {
                String sql = form.getSql();
                try {
                    if (conn == null) {
                        PopPaneUtil.error(dialog.getWindow(), LangMgr.getValue(domainName, LK_CONN_ERROR));
                        return;
                    }
                    if (StringUtils.isBlank(sql)) {
                        return;
                    }
                    String[] sqlList = sql.split(System.lineSeparator());
                    SqlExeUtil.batchExecute(conn, Arrays.asList(sqlList));
                    form.clearForm();
                    this.dialog.hide();
                    informRefreshUsr();
                    PopPaneUtil.info(dialog.getWindow(), LangMgr.getValue(domainName, LK_SAVE_SUCCESS));
                } catch (Exception e) {
                    PopPaneUtil.error(dialog.getWindow(), e.getMessage());
                }
            } else {
                PopPaneUtil.error(dialog.getWindow(), form.checkForm().getMsg());
            }
        } catch (Exception e) {
            PopPaneUtil.error(dialog.getWindow(), e);
        }
    }


}
