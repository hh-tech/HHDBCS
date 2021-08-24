package com.hh.hhdb_admin.test.view;

import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.view.comp.AddUpdViewComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

import javax.swing.*;
import java.io.File;
import java.sql.Connection;

/**
 *
 */
public class ViewCompTest {
    public static void main(String[] args) {
        try {
            HHSwingUi.init();
            IconFileUtil.setIconBaseDir(new File("etc/icon/"));

            HFrame frame = new HFrame();
            HDivLayout layout = new HDivLayout(20, 30, GridSplitEnum.C12);
            layout.setxBorderWidth(20);

            HPanel panel = new HPanel(layout);
            HButton createViewButton = getCreateViewButton();
            createViewButton.setText("创建视图panel");

            HButton updateViewButton = getUpdateViewButton();
            updateViewButton.setText("修改视图panel");

            HButton createMViewButton = getCreateMViewButton();
            createMViewButton.setText("创建物化视图panel");

            HButton updateMViewButton = getUpdateMViewButton();
            updateMViewButton.setText("修改物化视图panel");

            panel.add(createViewButton);
            panel.add(updateViewButton);
            panel.add(createMViewButton);
            panel.add(updateMViewButton);

            frame.setRootPanel(panel);
            frame.show();
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(e);
        }
    }

    /**
     * 创建视图
     */
    private static HButton getCreateViewButton() {
        return new HButton() {
            @Override
            protected void onClick() {
                Connection conn = null;
                try {
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean());
                    LoginBean loginBean = new LoginBean();
                    loginBean.setConn(conn);
                    loginBean.setJdbc(MgrTestUtil.getJdbcBean());
                    AddUpdViewComp addUpdViewComp = new AddUpdViewComp(loginBean) {
                        @Override
                        protected void informRefreshView(String schemaName, boolean isMaterialized) {
                            System.out.println("刷新左侧视图树节点");
                        }
                    };
                    addUpdViewComp.show(loginBean.getJdbc().getSchema(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(e);
                } finally {
                    ConnUtil.close(conn);
                }
            }
        };
    }

    /**
     * 创建视图
     */
    private static HButton getCreateMViewButton() {
        return new HButton() {
            @Override
            protected void onClick() {
                Connection conn = null;
                try {
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean());
                    LoginBean loginBean = new LoginBean();
                    loginBean.setConn(conn);
                    loginBean.setJdbc(MgrTestUtil.getJdbcBean());
                    AddUpdViewComp addUpdViewComp = new AddUpdViewComp(loginBean) {
                        @Override
                        protected void informRefreshView(String schemaName, boolean isMaterialized) {
                            System.out.println("刷新左侧物化视图树节点");
                        }
                    };
                    addUpdViewComp.show(loginBean.getJdbc().getSchema(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(e);
                } finally {
                    ConnUtil.close(conn);
                }
            }
        };
    }

    /**
     * 修改物化视图
     */
    private static HButton getUpdateMViewButton() {
        return new HButton() {
            @Override
            protected void onClick() {
                Connection conn = null;
                try {
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean());
                    LoginBean loginBean = new LoginBean();
                    loginBean.setConn(conn);
                    loginBean.setJdbc(MgrTestUtil.getJdbcBean());
                    String newName = JOptionPane.showInputDialog(null, "请输入要修改的视图名：", "");
                    if (newName == null) {
                        return;
                    }
                    AddUpdViewComp addUpdViewComp = new AddUpdViewComp(loginBean) {
                        @Override
                        protected void informRefreshView(String schemaName, boolean isMaterialized) {
                            System.out.println("刷新左侧物化视图树节点");
                        }
                    };
                    addUpdViewComp.show(loginBean.getJdbc().getSchema(), newName, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(e);
                } finally {
                    ConnUtil.close(conn);
                }
            }
        };
    }

    /**
     * 修改视图
     */
    private static HButton getUpdateViewButton() {
        return new HButton() {
            @Override
            protected void onClick() {
                Connection conn = null;
                try {
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean());
                    LoginBean loginBean = new LoginBean();
                    loginBean.setConn(conn);
                    loginBean.setJdbc(MgrTestUtil.getJdbcBean());
                    String newName = JOptionPane.showInputDialog(null, "请输入要修改的视图名：", "");
                    if (newName == null) {
                        return;
                    }
                    AddUpdViewComp addUpdViewComp = new AddUpdViewComp(loginBean) {
                        @Override
                        protected void informRefreshView(String schemaName, boolean isMaterialized) {
                            System.out.println("刷新左侧视图树节点");
                        }
                    };
                    addUpdViewComp.show(loginBean.getJdbc().getSchema(), newName, false);
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(e);
                } finally {
                    ConnUtil.close(conn);
                }
            }
        };
    }

}
