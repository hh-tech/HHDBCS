package com.hh.hhdb_admin.mgr.login;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.JdbcUrlIpUtil;
import com.hh.frame.common.util.db.SshSessionTool;
import com.hh.frame.create_dbobj.treeMr.base.ViewType;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangEnum;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.ui.button.ButtonUI;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;
import com.hh.hhdb_admin.mgr.table_open.TableOpenMgr;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Date;
import java.util.regex.Pattern;

public abstract class LoginComp {
    private final int width = 700;
    private final int height = 630;
    private final HFrame frame;
    private HDialog dialog;
    private HButton loginBtn;
    private HButton cancelBtn;
    private HButton testConnBtn;
    private HButton saveBtn;
    private HButton resetBtn;
    private HButton chooseBtn;
    public static LoginBean loginBean = new LoginBean();
    private final LoginForm loginForm;
    private final SshLoginComp sshLoginComp;
    private SshSessionTool sessionTool;
    private final SelectConnComp connComp;
    private HTabPane tabPane;
    public static final String DOMAIN_NAME = LoginComp.class.getName();
    public static final String LOG_NAME = LoginComp.class.getSimpleName();
    public static final Pattern pattern = Pattern.compile("[0-9]*");

    static {
        try {
            LangMgr2.loadMerge(LoginComp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HPanel loginFormPanel;

    protected LoginComp() {
        frame = new HFrame(width, height);
        frame.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
        frame.setCloseType(true);
        frame.getWindow().setLocationRelativeTo(null);

        EnterEventAction enterEventAction = new EnterEventAction();
        sshLoginComp = new SshLoginComp(LoginUtil.sshLoginBen);
        sshLoginComp.setAction(enterEventAction);
        loginForm = new LoginForm() {
            @Override
            public void changeBtnStatus() {
                boolean enabled = StringUtils.isNotEmpty(nameLabelInput.getValue()) && StringUtils.isNotEmpty(connLabelInput.getValue())
                        && StringUtils.isNotEmpty(userLabelInput.getValue()) && StringUtils.isNotEmpty(passLabelInput.getValue());
                loginBtn.setEnabled(enabled);
                loginBtn.setIcon(getIcon(enabled ? "login2" : "login"));
                saveBtn.setEnabled(enabled);
                testConnBtn.setEnabled(enabled);
                resetBtn.setEnabled(StringUtils.isNotEmpty(nameLabelInput.getValue()) || StringUtils.isNotEmpty(connLabelInput.getValue())
                        || StringUtils.isNotEmpty(userLabelInput.getValue()) || StringUtils.isNotEmpty(passLabelInput.getValue()));

            }

            @Override
            public void setLang() {
                loginBtn.setText(getLang("login"));
                cancelBtn.setText(getLang("cancel"));
                testConnBtn.setText(getLang("test_conn"));
                saveBtn.setText(getLang("save"));
                resetBtn.setText(getLang("reset"));
                chooseBtn.setText(getLang("choose"));
                loginFormPanel.setTitle(getLang("general") + getLang(SshLoginComp.SSH_CONFIG));
                sshLoginComp.resLang();
                frame.setWindowTitle(getLang("login"));
                tabPane.updateTabName("general", getLang("general"));
                resetLang();
                if (dialog != null) {
                    dialog.setWindowTitle(getLang("switch"));
                }
            }
        };
        loginForm.addInputsEvents(enterEventAction);
        connComp = new SelectConnComp(loginForm, sshLoginComp) {
            @Override
            protected void selectCallback() {
                loginForm.getConnNameInput().getComp().requestFocus();
            }
        };
    }

    public static String getLang(String key) {
        LangMgr2.setDefaultLang(StartUtil.default_language);
        return LangMgr2.getValue(DOMAIN_NAME, key);
    }

    public void showLogin() {
        frame.setWindowTitle(getLang("login"));
        frame.setRootPanel(getRootPanel(frame, null, false));
        frame.show();
        sshLoginComp.addInputsEvents(new EnterEventAction());
        loginForm.getConnNameInput().getComp().requestFocus();
    }

    private HPanel getRootPanel(HFrame frame, HDialog dialog, boolean isSwitch) {
        loginFormPanel = loginForm.getFormPanel();
        loginFormPanel.setTitle(getLang("general") + getLang(SshLoginComp.SSH_CONFIG));
        LastPanel lastPanel = new LastPanel();
        LastPanel formLastPanel = new LastPanel();
        formLastPanel.setHead(initBtnPanel(frame, dialog, isSwitch).getComp());
        formLastPanel.set(loginFormPanel.getComp());
        HPanel formPanel = new HPanel();
        formPanel.setLastPanel(formLastPanel);

        HPanel rootPanel = new HPanel();
        tabPane = new HTabPane();
        tabPane.setCloseBtn(false);
        tabPane.addPanel("general", getLang("general"), formPanel);
        tabPane.addPanel("ssh", "SSH", sshLoginComp);
        HPanel footBtnPanel = initFootBtnPanel();
        lastPanel.set(tabPane.getComp());
        lastPanel.setFoot(footBtnPanel.getComp());
        rootPanel.setLastPanel(lastPanel);
        return rootPanel;
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
        testConnBtn = new HButton(getLang("test_conn"));
        testConnBtn.setEnabled(false);
        testConnBtn.setIcon(getIcon("test_conn"));
        testConnBtn.addActionListener(new TestConnListener());
        leftPanel.add(testConnBtn);

        HBarPanel rightPanel = new HBarPanel(rightLayout);
        loginBtn = new HButton(getLang("login"));
        loginBtn.addActionListener(e -> login());
        loginBtn.getComp().setUI(new ButtonUI().setNormalColor(ButtonUI.NormalColor.blue));
        loginBtn.getComp().setForeground(Color.white);
        loginBtn.setEnabled(false);
        loginBtn.setIcon(getIcon("login"));

        cancelBtn = new HButton(getLang("cancel")) {
            @Override
            protected void onClick() {
                if (dialog != null) {
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
        return footPanel;
    }

    private HBarPanel initBtnPanel(HFrame frame, HDialog dialog, boolean isSwitch) {
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        barLayout.setTopHeight(10);
        barLayout.setBottomHeight(15);
        HBarPanel barPanel = new HBarPanel(barLayout);
        chooseBtn = new HButton(getLang("choose")) {
            @Override
            protected void onClick() {
                connComp.show(frame, dialog, isSwitch);
            }
        };
        chooseBtn.setIcon(getIcon("list"));
        resetBtn = new HButton(getLang("reset")) {
            @Override
            protected void onClick() {
                loginForm.reset();
                sshLoginComp.reset();
            }
        };
        resetBtn.setEnabled(false);
        resetBtn.setIcon(getIcon("reset"));
        saveBtn = new HButton(getLang("save")) {
            @Override
            protected void onClick() {
                save(true);
            }
        };
        saveBtn.setEnabled(false);
        saveBtn.setIcon(getIcon("save"));
        barPanel.add(loginBtn);
        barPanel.add(chooseBtn);
        barPanel.add(resetBtn);
        barPanel.add(saveBtn);
        return barPanel;
    }

    void switchLogin() {
        dialog = new HDialog(StartUtil.parentFrame, width, height) {
            @Override
            protected void closeEvent() {
                StartUtil.default_language = loginForm.oldLang;
                LangMgr2.setDefaultLang(loginForm.oldLang);
                StartUtil.setLocale(loginForm.oldLang);
            }
        };
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setRootPanel(getRootPanel(null, dialog, true));
        dialog.setWindowTitle(getLang("switch"));
        dialog.getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                loginForm.getConnNameInput().getComp().requestFocus();
            }
        });
        loginForm.updateFormData(LoginUtil.currConnInfo);
        dialog.show();
    }

    private void save(boolean isPopInfo) {
        JsonObject connInfo = loginForm.getData(true);
        JsonObject sshJsonData = sshLoginComp.getJsonData();
        if (sshJsonData != null) {
            connInfo.set("ssh", sshJsonData);
        }
        connComp.loginData.set(connInfo.getString("conn_name"), connInfo);
        LoginUtil.saveConnFile(connComp.loginData);
        if (isPopInfo) {
            if (dialog != null) {
                PopPaneUtil.info(dialog.getWindow(), getLang("saveSuccess"));
            } else {
                PopPaneUtil.info(frame.getWindow(), getLang("saveSuccess"));
            }
        }
    }

    private void login() {
        loginBean.initFilterData();
        JsonObject connInfo = loginForm.getData(false);
        JdbcBean jdbcBean = LoginUtil.json2JdbcBean(connInfo);
        try {
            reConn(jdbcBean);
            loginBean.setConnName(connInfo.getString("conn_name"));
            ViewType viewType = ViewType.valueOf(connInfo.getString("db_view"));
            loginBean.setViewType(viewType);
            loginBean.setLoginDate(new Date());
            if (viewType == ViewType.DBA && !LoginUtil.isDba(loginBean.getConn(), jdbcBean.getUser())) {
                throw new Exception(getLang("not_dba"));
            }
            LoginUtil.currConnInfo = loginForm.getData(true);
            if (dialog != null) {
                dialog.dispose();
                StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.CMD_CLOSE_ALL_TAB));
            } else {
                frame.hide();
            }
            try {
                if (Boolean.parseBoolean(connInfo.getString("is_save"))) save(false);
                JsonObject json = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();
                json.set("language", loginForm.langLabelInput.getValue());
                FileUtils.writeStringToFile(StartUtil.defaultJsonFile, json.toPrettyString(), StandardCharsets.UTF_8);
                StartUtil.default_language = LangEnum.valueOf(loginForm.langLabelInput.getValue());
                LangMgr2.setDefaultLang(StartUtil.default_language);
                StartUtil.setLocale(StartUtil.default_language);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            inform(loginBean);
        } catch (Exception e) {
            e.printStackTrace();
            logUtil.error(LOG_NAME, e);
            if (dialog != null) {
                PopPaneUtil.error(dialog.getWindow(), e);
            } else {
                PopPaneUtil.error(frame.getWindow(), e);
            }
        }
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
        SshLoginBen sshLoginBen = sshLoginComp.getSshLoginBen();
        Connection conn;
        ConnUtil.close(loginBean.getConn());
        if (sessionTool != null) {
            sessionTool.close();
        }
        sessionTool = new SshSessionTool();
        loginBean.setOriginalJdbc(jdbcBean);
        jdbcBean = buildJdbcBean(sessionTool, jdbcBean, sshLoginBen);
        LoginUtil.sshLoginBen = sshLoginBen;
        conn = ConnUtil.getConn(jdbcBean);
        LoginUtil.setLockTimeOut(conn);
        loginBean.setJdbc(jdbcBean);
        loginBean.setConn(conn);
    }

    private JdbcBean buildJdbcBean(SshSessionTool sessionTool, JdbcBean jdbcBean, SshLoginBen sshLoginBen) throws Exception {
        if (sshLoginBen != null && sshLoginBen.isEnabled()) {
            String dbUrl = jdbcBean.getDbUrl();
            sessionTool.setRemoteDbHost(JdbcUrlIpUtil.getHostFromUrl(dbUrl));
            sessionTool.setRemoteDbPort(JdbcUrlIpUtil.getPortFromUrl(dbUrl));
            boolean isPassword = sshLoginBen.getType() == SshLoginBen.SshType.PASSWORD;
            try {
                int port = sshLoginBen.getPort();
                String host = sshLoginBen.getHost();
                if (!pattern.matcher(String.valueOf(port)).matches() || port > 65535) {
                    throw new NumberFormatException(port + " Port Incorrect");
                }
                if (StringUtils.isBlank(host)) {
                    throw new NullPointerException("Host Not Be Null");
                }
                if (isPassword) {
                    sessionTool.connect(sshLoginBen.getHost(), sshLoginBen.getPort(), sshLoginBen.getUser(), sshLoginBen.getPassword());
                } else {
                    File keyFile = new File(sshLoginBen.getPrivateKey());
                    if (!keyFile.exists()) {
                        throw new FileNotFoundException(sshLoginBen.getPrivateKey() + " PrivateKey File Not Found");
                    }
                    sessionTool.connect(sshLoginBen.getHost(), sshLoginBen.getPort(), sshLoginBen.getUser(), keyFile, sshLoginBen.getKeyPassphrase());
                }
            } catch (Exception e) {
                loginBean.setSshAuth(false);
                throw e;
            }
            jdbcBean = sessionTool.getSshJdbcBean(jdbcBean);
            loginBean.setSshAuth(true);
        }
        return jdbcBean;
    }

    protected static ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.LOGIN.name(), name, IconSizeEnum.SIZE_16));
    }

    /**
     * 登录成功之后做的操作
     *
     * @param loginBean 连接bean
     */
    public abstract void inform(LoginBean loginBean);

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

    /**
     * 测试连接
     */
    class TestConnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            frame.setWindowTitle(getLang("testing"));
            if (dialog != null) {
                dialog.setWindowTitle(getLang("testing"));
            }
            SwingUtilities.invokeLater(() -> {
                Connection conn = null;
                SshSessionTool testSessionTool = null;
                try {
                    testSessionTool = new SshSessionTool();
                    JsonObject connInfo = loginForm.getData(false);
                    SshLoginBen sshLoginBen = sshLoginComp.getSshLoginBen();
                    JdbcBean jdbcBean = LoginUtil.json2JdbcBean(connInfo);
                    jdbcBean = buildJdbcBean(testSessionTool, jdbcBean, sshLoginBen);
                    conn = ConnUtil.getConn(jdbcBean);
                    LoginUtil.setLockTimeOut(conn);
                    if (ConnUtil.isConnected(conn)) {
                        if (dialog != null) {
                            PopPaneUtil.info(dialog.getWindow(), getLang("conn_successful"));
                        } else {
                            PopPaneUtil.info(frame.getWindow(), getLang("conn_successful"));
                        }
                    } else {
                        if (dialog != null) {
                            PopPaneUtil.error(dialog.getWindow(), getLang("conn_fail"));
                        } else {
                            PopPaneUtil.error(frame.getWindow(), getLang("conn_fail"));
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                    if (dialog != null) {
                        PopPaneUtil.error(dialog.getWindow(), getLang("conn_fail") + " : " + exception.getMessage());
                    } else {
                        PopPaneUtil.error(frame.getWindow(), getLang("conn_fail") + " : " + exception.getMessage());
                    }
                } finally {
                    frame.setWindowTitle(getLang("login"));
                    if (dialog != null) {
                        dialog.setWindowTitle(getLang("switch"));
                    }
                    ConnUtil.close(conn);
                    if (testSessionTool != null) {
                        testSessionTool.close();
                    }
                }
            });
        }
    }

    /**
     * 回车按钮监听
     */
    class EnterEventAction extends AbstractAction {

        private static final long serialVersionUID = -5578066054062128882L;

        @Override
        public void actionPerformed(ActionEvent e) {
            login();
        }
    }
}
