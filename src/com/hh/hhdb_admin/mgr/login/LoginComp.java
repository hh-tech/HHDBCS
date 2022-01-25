package com.hh.hhdb_admin.mgr.login;

import com.alee.extended.button.SplitButtonAdapter;
import com.alee.extended.language.LanguageChooser;
import com.alee.managers.language.LanguageManager;
import com.alee.managers.style.Skin;
import com.alee.managers.style.StyleManager;
import com.alee.painter.PainterSupport;
import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SshSessionTool;
import com.hh.frame.create_dbobj.treeMr.base.ViewType;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangEnum;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.connMgr.base.ConnMgrUtil;
import com.hh.frame.swingui.view.connMgr.base.ConnTreeNode;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.ui.skin.AbstractHhSkin;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.login.base.LoginConnMsg;
import com.hh.hhdb_admin.mgr.login.base.LoginToolBar;
import com.hh.hhdb_admin.mgr.login.comp.CommonComp;
import com.hh.hhdb_admin.mgr.login.comp.ConnMgrComp;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;
import com.hh.hhdb_admin.mgr.table_open.TableOpenMgr;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Date;
import java.util.Locale;

public abstract class LoginComp extends CommonComp {
    private final int width = 850;
    private final int height = 635;
    private HDialog dialog;
    private final HFrame frame;

    private HButton loginBtn;
    private HButton testConnBtn;
    private HButton cancelBtn;
    public static LoginBean loginBean = new LoginBean();
    public static final String LOG_NAME = LoginComp.class.getSimpleName();
    private SshSessionTool sessionTool;
    private ConnMgrComp connMgrComp;
    private final String filePath;
    private HSplitPanel splitPanel;

    private LoginToolBar loginToolBar;

    public LoginComp() {
        File file = new File(StartUtil.getEtcFile(), "login");
        if (!file.exists()) {
            file.mkdirs();
        }
        this.filePath = file.getAbsolutePath();
        frame = new HFrame(width, height);
        frame.setIconImage(IconFileUtil.getLogo());
        frame.setCloseType(true);
        frame.getWindow().setLocationRelativeTo(null);
    }

    public void showLogin() {
        loginToolBar = initMenuBar(true);
        frame.setWindowTitle(getLang("login"));
        frame.setRootPanel(getRootPanel());
        ((JFrame) frame.getWindow()).setJMenuBar(loginToolBar.getComp());
        frame.show();
        nameInputFocus();
    }

    public void switchLogin() {
        dialog = new HDialog(StartUtil.parentFrame, width, height, true) {
            @Override
            protected void closeEvent() {
                StartUtil.default_language = connMgrComp.oldLang;
                LangMgr2.setDefaultLang(connMgrComp.oldLang);
                StartUtil.setLocale(connMgrComp.oldLang);
            }
        };
        loginToolBar = initMenuBar(false);
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setRootPanel(getRootPanel());
        dialog.setWindowTitle(getLang("switch"));
        ((JDialog) dialog.getWindow()).setJMenuBar(loginToolBar.getComp());
        ((JDialog) dialog.getWindow()).setResizable(true);
        LoginConnMsg connMsg = new LoginConnMsg();
        connMsg.setJson(LoginUtil.currConnInfo);
        ConnMgrUtil.PATH = LoginUtil.currConnInfo.getString("path");
        ConnMgrUtil.NAME = LoginUtil.currConnInfo.getString("name");
        connMgrComp.setValues(connMsg);
        dialog.show();
        nameInputFocus();
    }

    public void switchSchema(String schemaName) {
        LoginUtil.switchSchema(loginBean.getJdbc(), schemaName);
        try {
            reConn(loginBean.getJdbc());
            StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.SWITCH_SCHEMA));
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
    }

    private HSplitPanel getRootPanel() {
        LastPanel lastPanel = new LastPanel();
        HPanel footBtnPanel = initFootBtnPanel();
        connMgrComp = new ConnMgrComp(dialog != null ? dialog : frame, filePath, LoginConnMsg.class.getName(), true) {

            @Override
            public void updateBtnStatus(boolean enabled) {
                loginToolBar.getSaveItem().setEnabled(enabled);
                testConnBtn.setEnabled(enabled);
                loginBtn.setEnabled(enabled);
            }

            @Override
            public void select(ConnTreeNode node) {
                super.select(node);
                tabComp.nameInput.getInput().getComp().requestFocus();
            }

            @Override
            public void dbLogin(ConnTreeNode node) {
                LoginConnMsg connMsg = (LoginConnMsg) node.getConnMsg();
                connMsg.setUsername(LoginUtil.getRealName(connMsg.getUsername(), connMsg.getType()));
                connMsg.setSchema(LoginUtil.getRealName(connMsg.getSchema(), connMsg.getType()));
                login(connMsg);
            }
        };

        lastPanel.set(connMgrComp.getTabPanel().getComp());
        lastPanel.setFoot(footBtnPanel.getComp());
        splitPanel = new HSplitPanel();
        splitPanel.setSplitWeight(0.27);
        splitPanel.setPanelOne(connMgrComp.getTreePanel());
        splitPanel.setLastComp4Two(lastPanel);
        setSplitUi();
        connMgrComp.addUndoableEvent();
        connMgrComp.addEnterEvent(e -> {
            if (connMgrComp.tabComp.isBtnEnabled()) {
                login(connMgrComp.getLoginConnMsg(false));
            }
        });
        return splitPanel;
    }

    private void setSplitUi() {
        BasicSplitPaneUI basicSplitPaneUi = new BasicSplitPaneUI();
        splitPanel.getComp().setUI(basicSplitPaneUi);
        splitPanel.getComp().setBorder(BorderFactory.createEmptyBorder());
        basicSplitPaneUi.getDivider().setBorder(BorderFactory.createLineBorder(HHSwingUi.isDarkSkin() ? Color.gray : Color.lightGray));
    }

    /**
     * 初始化顶部状态栏
     *
     * @return ToolBar
     */
    private LoginToolBar initMenuBar(boolean showTool) {
        LoginToolBar loginToolBar = new LoginToolBar(this::switchSkinActionEvent);
        loginToolBar.getLanguageChooser().addActionListener(this::localeActionEvent);
        loginToolBar.getSaveItem().addActionListener(e -> save(true));
        loginToolBar.getAutoSaveItem().addItemListener(event -> {
            try {
                JMenuItem item = (JMenuItem) event.getItem();
                JsonObject json = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();
                json.set("autoSave", item.isSelected());
                StartUtil.autoSave = item.isSelected();
                FileUtils.writeStringToFile(StartUtil.defaultJsonFile, json.toPrettyString(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
                PopPaneUtil.error(dialog != null ? dialog.getWindow() : frame.getWindow(), e);
            }
        });
        loginToolBar.getResetButton().addSplitButtonListener(new SplitButtonAdapter() {
            @Override
            public void buttonClicked(final ActionEvent e) {
                connMgrComp.reset();
                loginBtn.setEnabled(false);
            }
        });
        loginToolBar.setShowToolSplitButton(showTool);
        return loginToolBar;
    }

    private void switchSkinActionEvent(ActionEvent e) {
        JComboBox<?> comboBox = (JComboBox<?>) e.getSource();
        final Skin skin = (Skin) comboBox.getSelectedItem();
        Skin oldSkin = StyleManager.getSkin();
        if (skin != null && oldSkin != skin) {
            HHSwingUi.switchSkin((AbstractHhSkin) skin);
            setSplitUi();
            nameInputFocus();
            try {
                StartUtil.writeSkinToFile((AbstractHhSkin) skin);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void localeActionEvent(ActionEvent e) {
        Locale locale = (Locale) ((LanguageChooser) e.getSource()).getSelectedItem();
        if (locale != null && LanguageManager.getLocale() != locale) {
            boolean validEnum = EnumUtils.isValidEnum(LangEnum.class, locale.getLanguage().toUpperCase());
            if (!validEnum) {
                return;
            }
            LangEnum langEnum = LangEnum.valueOf(locale.getLanguage().toUpperCase());
            if (StartUtil.default_language == langEnum) {
                return;
            }
            LanguageManager.setLocale(locale);
            System.out.println("当前语言" + langEnum);
            StartUtil.default_language = langEnum;
            LangMgr2.setDefaultLang(langEnum);
            StartUtil.setLocale(langEnum);
            setLocal();
        }
    }

    private void save(boolean isPop) {
        LoginConnMsg connMsg = connMgrComp.getLoginConnMsg(true);
        if (StringUtils.isNotEmpty(connMsg.getPort())) {
            if (!LoginUtil.pattern.matcher(connMsg.getPort()).matches() || Integer.parseInt(connMsg.getPort()) > 65535) {
                PopPaneUtil.error(dialog == null ? frame.getWindow() : dialog.getWindow(), connMsg.getPort() + " " + getLang("portIncorrect"));
                return;
            }
        }
        connMgrComp.save(connMsg);
        if (isPop) {
            ConnMgrUtil.NAME = connMsg.getConnName();
            PopPaneUtil.info(dialog == null ? frame.getWindow() : dialog.getWindow(), getLang("saveSuccessful"));
        }
    }

    protected void nameInputFocus() {
        connMgrComp.tabComp.nameInput.getInput().getComp().requestFocus();
    }

    private HPanel initFootBtnPanel() {
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C6);
        divLayout.setBottomHeight(10);
        divLayout.setTopHeight(10);
        HPanel footPanel = new HPanel(divLayout);
        HBarLayout rightLayout = new HBarLayout();
        rightLayout.setAlign(AlignEnum.RIGHT);
        rightLayout.setxGap(10);
        HBarLayout leftLayout = new HBarLayout();
        leftLayout.setAlign(AlignEnum.LEFT);
        HBarPanel leftPanel = new HBarPanel(leftLayout);
        testConnBtn = new HButton(getLang("test_conn")) {
            @Override
            protected void onClick() {
                super.onClick();
            }
        };
        testConnBtn.setEnabled(false);
        testConnBtn.setIcon(getIcon("test_conn"));
        testConnBtn.addActionListener(new TestConnListener());
        leftPanel.add(testConnBtn);
        HBarPanel rightPanel = new HBarPanel(rightLayout);
        loginBtn = new HButton(getLang("login"));
        loginBtn.addActionListener(e -> {
            loginBean.initFilterData();
            login(connMgrComp.getLoginConnMsg(false));
        });
        loginBtn.setEnabled(false);
        loginBtn.setIcon(getIcon("login"));
        cancelBtn = new HButton(getLang("cancel")) {
            @Override
            protected void onClick() {
                if (dialog != null) {
                    StartUtil.default_language = connMgrComp.oldLang;
                    LangMgr2.setDefaultLang(connMgrComp.oldLang);
                    StartUtil.setLocale(connMgrComp.oldLang);
                    dialog.hide();
                } else {
                    frame.dispose();
                    System.exit(0);
                }
            }
        };
        cancelBtn.setIcon(getIcon("cancel"));
        rightPanel.add(loginBtn, cancelBtn);
        footPanel.add(leftPanel, rightPanel);
        PainterSupport.setPadding(loginBtn.getComp(), 5);
        PainterSupport.setPadding(cancelBtn.getComp(), 5);
        PainterSupport.setPadding(testConnBtn.getComp(), 5);
        return footPanel;
    }

    private void setLocal() {
        HHSwingUi.setFileChooserText();
        loginBtn.setText(getLang("login"));
        cancelBtn.setText(getLang("cancel"));
        testConnBtn.setText(getLang("test_conn"));
        frame.setWindowTitle(getLang("login"));
        if (dialog != null) {
            dialog.setWindowTitle(getLang("switch"));
        }
        loginToolBar.setLocal();
        if (connMgrComp != null) {
            connMgrComp.resetLabel();
        }
    }


    /**
     * 测试连接
     */
    class TestConnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            testConnBtn.setEnabled(false);
            if (dialog != null) {
                dialog.setWindowTitle(getLang("testing"));
            } else {
                frame.setWindowTitle(getLang("testing"));
            }
            new Thread(() -> {
                Connection conn = null;
                SshSessionTool testSessionTool = null;
                try {
                    testSessionTool = new SshSessionTool();
                    LoginConnMsg connMsg = connMgrComp.getLoginConnMsg(false);
                    JdbcBean jdbcBean = LoginUtil.json2JdbcBean(connMsg);
                    jdbcBean = LoginUtil.buildJdbcBean(jdbcBean, connMsg, testSessionTool);
                    conn = ConnUtil.getConn(jdbcBean);
                    LoginUtil.setLockTimeOut(conn);
                    if (ConnUtil.isConnected(conn)) {
                        PopPaneUtil.info(getLang("conn_successful"));
                    } else {
                        PopPaneUtil.error(getLang("conn_fail"));
                    }
                    nameInputFocus();
                } catch (Exception exception) {
                    exception.printStackTrace();
                    PopPaneUtil.error(getLang("conn_fail") + " : " + exception.getMessage());
                } finally {
                    frame.setWindowTitle(getLang("login"));
                    if (dialog != null) {
                        dialog.setWindowTitle(getLang("switch"));
                    }
                    ConnUtil.close(conn);
                    if (testSessionTool != null) {
                        testSessionTool.close();
                    }
                    testConnBtn.setEnabled(true);
                }
            }).start();
        }
    }

    private void login(LoginConnMsg connMsg) {
        loginBtn.setEnabled(false);
        if (dialog != null) {
            dialog.setWindowTitle(getLang("connecting"));
        } else {
            frame.setWindowTitle(getLang("connecting"));
        }
        new Thread(() -> {
            try {
                JdbcBean jdbcBean = LoginUtil.json2JdbcBean(connMsg);
                reConn(jdbcBean);
                loginBean.setConnName(connMsg.getConnName());
                ViewType viewType = ViewType.valueOf(connMsg.getView());
                loginBean.setViewType(viewType);
                loginBean.setLoginDate(new Date());
                if (viewType == ViewType.DBA && !LoginUtil.isDba(loginBean.getConn(), jdbcBean.getUser())) {
                    throw new Exception(getLang("not_dba"));
                }
                LoginUtil.currConnInfo = connMgrComp.getLoginConnMsg(true).toJson();
                if (StringUtils.isEmpty(LoginUtil.PATH)) {
                    LoginUtil.currConnInfo.set("path", new File(StartUtil.getEtcFile(), "login").getAbsolutePath());
                } else {
                    LoginUtil.currConnInfo.set("path", LoginUtil.PATH);
                }
                if (dialog != null) {
                    dialog.dispose();
                    StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.CMD_CLOSE_ALL_TAB));
                } else {
                    frame.hide();
                }
                try {
                    if (loginToolBar.getAutoSaveItem().isSelected()) {
                        save(false);
                    }
                    JsonObject json = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();
                    json.set("language", StartUtil.default_language.name());
                    FileUtils.writeStringToFile(StartUtil.defaultJsonFile, json.toPrettyString(), StandardCharsets.UTF_8);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                inform(loginBean);
            } catch (Exception e) {
                e.printStackTrace();
                logUtil.error(LOG_NAME, e);
                PopPaneUtil.error(e);
            } finally {
                frame.setWindowTitle(getLang("login"));
                if (dialog != null) {
                    dialog.setWindowTitle(getLang("switch"));
                }
                loginBtn.setEnabled(true);
            }
        }).start();

        File tmpFile = new File(StartUtil.workspace, TableOpenMgr.OPEN_TMP);
        try {
            if (tmpFile.exists()) {
                FileUtils.cleanDirectory(tmpFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reConn(JdbcBean jdbcBean) throws Exception {
        Connection conn;
        ConnUtil.close(loginBean.getConn());
        if (sessionTool != null) {
            sessionTool.close();
        }
        sessionTool = new SshSessionTool();
        loginBean.setOriginalJdbc(jdbcBean);
        LoginConnMsg connMsg = new LoginConnMsg();
        ;
        if (connMgrComp == null) {
            connMsg.setJson(LoginUtil.currConnInfo);
        } else {
            connMsg = connMgrComp.getLoginConnMsg(false);
        }
        jdbcBean = LoginUtil.buildJdbcBean(jdbcBean, connMsg, sessionTool);
        conn = ConnUtil.getConn(jdbcBean);
        LoginUtil.setLockTimeOut(conn);
        loginBean.setJdbc(jdbcBean);
        loginBean.setConn(conn);
    }

    /**
     * 登录成功之后做的操作
     *
     * @param loginBean 连接bean
     */
    public abstract void inform(LoginBean loginBean);

}
