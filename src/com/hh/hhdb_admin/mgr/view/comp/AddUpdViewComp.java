package com.hh.hhdb_admin.mgr.view.comp;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.create_dbobj.viewMr.mr.AbsViewMr;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.textEditor.base.ConstantsEnum;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.table_open.ModifyTabDataComp;

/**
 * 添加和修改视图的组件
 *
 * @author yangxianhui
 * @date 2020/10/13
 */
public class AddUpdViewComp {
    private final HTextArea textArea;
    private final HSplitPanel splitPanel;

    private final HPanel panel;
    private final File tmpFile;
    private String viewName;
    private boolean isUpdate;
    private HDialog dialog;
    private HButton saveBtn;
    private HButton saveAsBtn;
    private HButton viewBtn;
    private HButton selTableBtn;
    private boolean isMaterialized;
    private String schemaName;
    private final HBarPanel toolBar;
    private final JdbcBean bean;
    private final Connection conn;
    private ModifyTabDataComp mcomp = null;
    private static ChooseTableComp chooseTableComp;
    private final AbsViewMr absViewMr;
    private final HPanel bottomPanel;
    private static final String domainName = AddUpdViewComp.class.getName();

    private final static String LK_ADD_VIEW_TITLE = "ADD_VIEW_TITLE";
    private final static String LK_UPDATE_VIEW_TITLE = "UPDATE_VIEW_TITLE";
    private final static String LK_ADD_MVIEW_TITLE = "ADD_MVIEW_TITLE";
    private final static String LK_UPDATE_MVIEW_TITLE = "UPDATE_MVIEW_TITLE";
    private final static String LK_PREVIEW = "PREVIEW";
    private final static String LK_SAVE = "SAVE";
    private final static String LK_SAVE_AS = "SAVE_AS";
    private final static String LK_SELECT_TABLE_COLUMN = "SELECT_TABLE_COLUMN";
    private final static String LK_SAVE_SUCCESS = "SAVE_SUCCESS";
    private final static String LK_PLEASE_ENTER_VIEW_NAME = "PLEASE_ENTER_VIEW_NAME";
    private final static String LK_PLEASE_ENTER_SELECT_SQL = "PLEASE_ENTER_SELECT_SQL";
    private final static String LK_PLEASE_ENTER_DEFINE = "PLEASE_ENTER_DEFINE";
    private static final String VIEW_TMP = "view_tmp";

    static {
        try {
			LangMgr2.loadMerge(AddUpdViewComp.class);
		} catch (IOException e) {
			PopPaneUtil.error(e);
		}
    }


    public AddUpdViewComp(LoginBean loginBean) {
        this.tmpFile = new File(StartUtil.workspace + File.separator + VIEW_TMP);
        this.bean = loginBean.getJdbc();
        this.conn = loginBean.getConn();
        this.absViewMr = AbsViewMr.genViewMr(bean);
        initBtn();
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        l.setxGap(2);
        toolBar = new HBarPanel(l);
        //预览表格
        //sql文本框
        textArea = new HTextArea(false, true);
        textArea.setConstants(ConstantsEnum.SYNTAX_STYLE_SQL);
        textArea.getArea().setWordWrap(true);
        //splitPanel
        splitPanel = new HSplitPanel(false);
        LastPanel last = new LastPanel();
        last.set(textArea.getComp());
        splitPanel.setLastComp4One(last);
        splitPanel.setSplitWeight(1);
        //底部预览面板
        bottomPanel = new HPanel();
        splitPanel.setPanelTwo(bottomPanel);

        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setHead(toolBar.getComp());
        lastPanel.set(splitPanel.getComp());
        //主面板
        panel = new HPanel();
        panel.setLastPanel(lastPanel);
        chooseTableComp = new ChooseTableComp(loginBean) {
            @Override
            protected void setTextEditorValue(String sql) {
                AddUpdViewComp.this.setTextEditorValue(sql);
            }
        };
    }


    /**
     * 组件显示-新增
     */
    public void show(String schemaName, boolean isMaterialized) {
        try {
            if (this.absViewMr == null || (isMaterialized && !this.absViewMr.isSupportMView())) {
                throw new Exception("暂不支持数据库类型");
            }
            setUpdate(false);
            this.bean.setCurSessionSchema(DbCmdStrUtil.toDbCmdStr(schemaName, DriverUtil.getDbType(this.bean)));
            mcomp = new ModifyTabDataComp(this.bean, null, tmpFile);
            bottomPanel.getComp().removeAll();
            bottomPanel.setLastPanel(mcomp);
            this.dialog = new HDialog(StartUtil.parentFrame, 800, 550);
            this.dialog.getWindow().addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    mcomp.close();
                }
            });
            this.schemaName = schemaName;
            this.isMaterialized = isMaterialized;
            String title = isMaterialized ? LK_ADD_MVIEW_TITLE : LK_ADD_VIEW_TITLE;
            dialog.setWindowTitle(LangMgr2.getValue(domainName, title));
            dialog.setIconImage(IconFileUtil.getLogo().getImage());
            dialog.setRootPanel(panel);
            setTextEditorValue(LangMgr2.getValue(domainName, LK_PLEASE_ENTER_DEFINE) + "select * from usr");
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }

    }

    /**
     * 组件显示-修改
     */
    public void show(String schemaName, String viewName, boolean isMaterialized) {
        try {
            if (this.absViewMr == null || (isMaterialized && !this.absViewMr.isSupportMView())) {
                throw new Exception("暂不支持数据库类型");
            }
            setUpdate(true);
            this.schemaName = schemaName;
            this.isMaterialized = isMaterialized;
            this.bean.setCurSessionSchema(DbCmdStrUtil.toDbCmdStr(schemaName, DriverUtil.getDbType(this.bean)));
            loadView(viewName);
            mcomp = new ModifyTabDataComp(this.bean, null, tmpFile);
            bottomPanel.setLastPanel(mcomp);
            this.dialog = new HDialog(StartUtil.parentFrame, 750, 550);
            this.dialog.getWindow().addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    mcomp.close();
                }
            });
            String title = isMaterialized ? LK_UPDATE_MVIEW_TITLE : LK_UPDATE_VIEW_TITLE;
            dialog.setWindowTitle(LangMgr2.getValue(domainName, title) + "(" + viewName + ")");
            dialog.setIconImage(IconFileUtil.getLogo().getImage());
            dialog.setSize(750, 550);
            dialog.setRootPanel(panel);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);

        }
    }


    /**
     * 设置sql文本框值
     */
    private void setTextEditorValue(String sql) {
        this.textArea.setText(sql);
    }

    /**
     * 设置是否更新设计视图 true:设计视图 false:新增视图
     */
    private void setUpdate(boolean update) {
        this.isUpdate = update;
        resetToolBar();

    }

    /**
     * 根据view名称加载view的sql
     *
     * @param viewName 视图名称，当设计修改视图的必须设置视图名称
     */
    private void loadView(String viewName) throws Exception {
        if (isUpdate) {
            this.viewName = viewName;
            String sql = Objects.requireNonNull(absViewMr).getViewDefineSql(this.conn, schemaName, viewName, isMaterialized);
            if (StringUtils.isBlank(sql)) {
                throw new Exception("视图不存在:" + viewName);
            }
            
            setTextEditorValue(sql);
        }
    }

    /**
     * 点击选择表按钮
     */
    private void clickSelTableBtn() {
        chooseTableComp.show(dialog);
    }

    /**
     * 点击另存为按钮
     */
    private void clickSaveAsBtn() {
        popViewNameInput();

    }

    protected void informRefreshView(String schemaName, boolean isMaterialized) {

    }

    /**
     * 弹出视图名称输入框
     */
    private void popViewNameInput() {
        String name = PopPaneUtil.input(LangMgr2.getValue(domainName, LK_PLEASE_ENTER_VIEW_NAME));
        if (!StringUtils.isBlank(name)) {
            createOrReplaceView(name, getTextSql());
        }
    }

    /**
     * 点击预览按钮
     */
    private void clickViewBtn() {
        try {
            String sql = this.textArea.getArea().getTextArea().getSelectedText();
            if (StringUtils.isBlank(sql)) {
                sql = this.textArea.getArea().getTextArea().getText();
            }
            if (StringUtils.isBlank(sql)) {
                return;
            }

            sql = sql.replaceAll("--.*", "");

            if (!sql.toLowerCase().trim().startsWith("select")) {
                throw new Exception(LangMgr2.getValue(domainName, LK_PLEASE_ENTER_SELECT_SQL));
            }
            mcomp.loadReadOnlyTable(SqlStrUtil.removeSemi(sql));
            int maxLocation = splitPanel.getComp().getMaximumDividerLocation();
            if (splitPanel.getDividerLocation() >= maxLocation - 20) {
                splitPanel.getComp().setDividerLocation(0.5);
            }
            bottomPanel.getComp().validate();
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(dialog.getWindow(), e);
        }
    }


    /**
     * 点击保存按钮
     */
    private void clickSaveBtn() {
        try {
            if (StringUtils.isBlank(getTextSql())) {
                return;
            }
            if (isUpdate) {
                createOrReplaceView("", getTextSql());
            } else {
                popViewNameInput();
            }
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(dialog.getWindow(), e);
        }

    }


    private String getTextSql() {
        String sql = this.textArea.getArea().getTextArea().getSelectedText();
        if (StringUtils.isBlank(sql)) {
            sql = this.textArea.getArea().getTextArea().getText();
        }
        sql = sql.replaceAll("--.*", "");
        return sql;
    }


    /**
     * 创建or替换视图
     *
     * @param viewName 弹出框输入新的视图名称
     */
    private void createOrReplaceView(String viewName, String sql) {
        try {
            if (sql.endsWith(";")) {
                sql = sql.substring(0, sql.length() - 1);
            }
            if (StringUtils.isBlank(viewName) && isUpdate) {
                absViewMr.updateView(this.conn, DbCmdStrUtil.toDbCmdStr(this.schemaName, DriverUtil.getDbType(this.bean)),
                        DbCmdStrUtil.toDbCmdStr(this.viewName, DriverUtil.getDbType(this.bean)), sql, isMaterialized);
            } else {
                absViewMr.createView(this.conn, DbCmdStrUtil.toDbCmdStr(this.schemaName, DriverUtil.getDbType(this.bean)),
                        viewName, sql, isMaterialized);
            }
            informRefreshView(this.schemaName, isMaterialized);
            mcomp.close();
            this.dialog.dispose();
            PopPaneUtil.info(StartUtil.parentFrame.getWindow(), LangMgr2.getValue(domainName, LK_SAVE_SUCCESS));
        } catch (SQLException e) {
            e.printStackTrace();
            PopPaneUtil.error(dialog.getWindow(), e);
        }
    }

    /**
     * 重置工具栏
     */
    private void resetToolBar() {
        toolBar.add(saveBtn);
        if (this.isUpdate) {
            toolBar.add(saveAsBtn);
        }
        toolBar.add(viewBtn);
        toolBar.add(selTableBtn);
    }

    /**
     * 初始化工具栏按钮
     */
    private void initBtn() {
        saveBtn = new HButton(LangMgr2.getValue(domainName, LK_SAVE)) {
            @Override
            protected void onClick() {
                clickSaveBtn();
            }
        };
        saveBtn.setIcon(getIcon("save"));
        saveAsBtn = new HButton(LangMgr2.getValue(domainName, LK_SAVE_AS)) {
            @Override
            protected void onClick() {
                clickSaveAsBtn();
            }
        };
        saveAsBtn.setIcon(getIcon("saveas"));
        viewBtn = new HButton(LangMgr2.getValue(domainName, LK_PREVIEW)) {
            @Override
            protected void onClick() {
                clickViewBtn();
            }
        };
        viewBtn.setIcon(getIcon("runview"));
        selTableBtn = new HButton(LangMgr2.getValue(domainName, LK_SELECT_TABLE_COLUMN)) {
            @Override
            protected void onClick() {
                clickSelTableBtn();
            }
        };
        selTableBtn.setIcon(getIcon("column"));
    }

    public static ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.VIEW.name(), name, IconSizeEnum.SIZE_16));
    }

}


