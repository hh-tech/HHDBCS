package com.hh.hhdb_admin.test.usr;

import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.userMr.base.UsrFormType;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.usr.comp.AddUpdUsrComp;
import com.hh.hhdb_admin.mgr.usr.comp.PermComp;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;
import java.sql.Connection;

public class UsrCompTest {
    public static void main(String[] args) {
        try {
            HHSwingUi.init();


            IconFileUtil.setIconBaseDir(new File("etc/icon/"));
            HFrame frame = new HFrame();
            HDivLayout layout = new HDivLayout(20, 30, GridSplitEnum.C12);
            layout.setxBorderWidth(20);


            HPanel panel = new HPanel(layout);
            HButton createUsrBtn = getCreateUsrButton();
            createUsrBtn.setText("创建用户panel");


            HButton updateUsrPanel = getUpdateUsrButton();
            updateUsrPanel.setText("修改用户panel");


            HButton updateUsrPermissionPanel = getUpdatePermBtn();
            updateUsrPermissionPanel.setText("修改用户权限panel");



            HButton createRoleBtn = getCreateRoleBtn();
            createRoleBtn.setText("创建角色panel（oracle）");


            HButton updateRoleBtn = getUpdateRoleButton();
            updateRoleBtn.setText("修改角色panel（oracle）");




            panel.add(createUsrBtn);
            panel.add(updateUsrPanel);
            panel.add(updateUsrPermissionPanel);
            panel.add(createRoleBtn);
            panel.add(updateRoleBtn);
            frame.setRootPanel(panel);
            frame.show();
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(e);
        }
    }

    /**
     * 修改权限
     */
    private static HButton getUpdatePermBtn(){
        return new HButton() {
            @Override
            protected void onClick() {
                Connection conn = null;
                try {
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean());
                    String newName = JOptionPane.showInputDialog(null, "请输入要修改权限的用户名：", "test");
                    if (StringUtils.isBlank(newName)) {
                        return;
                    }
                    HDialog dialog = StartUtil.getMainDialog();
                    dialog.setSize(800, 600);
                    PermComp permissionComp = new PermComp(conn);
                    permissionComp.show(dialog, newName);
                }catch (Exception e){
                    e.printStackTrace();
                    PopPaneUtil.error(e);
                }finally {
                    ConnUtil.close(conn);
                }
            }
        };
    }

    /**
     * 创建角色
     */
    private static HButton getCreateRoleBtn(){
        return new HButton() {
            @Override
            protected void onClick() {
                Connection conn = null;
                HDialog dialog = StartUtil.getMainDialog();
                dialog.setSize(800, 600);
                try {
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean());
                    AddUpdUsrComp comp = new AddUpdUsrComp(conn, UsrFormType.role) {
                        @Override
                        protected void informRefreshUsr() {
                            System.out.println("通知刷新角色树节点");
                        }
                    };
                    comp.show(dialog);
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(e);
                }finally {
                    ConnUtil.close(conn);
                }
            }
        };
    }

    /**
     * 创建用户
     */
    private static HButton getCreateUsrButton(){
        return new HButton() {
            @Override
            protected void onClick() {
                Connection conn = null;
                HDialog dialog = StartUtil.getMainDialog();
                dialog.setSize(800, 600);
                try {
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean());
                    AddUpdUsrComp addUpdUsrComp = new AddUpdUsrComp(conn, UsrFormType.usr) {
                        @Override
                        protected void informRefreshUsr() {
                            System.out.println("通知刷新用户树节点");
                        }
                    };
                    addUpdUsrComp.show(dialog);
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(e);
                }finally {
                    ConnUtil.close(conn);
                }
            }
        };
    }

    /**
     * 修改用户
     */
    private static HButton getUpdateUsrButton(){
        return new HButton() {
            @Override
            protected void onClick() {
                Connection conn = null;
                HDialog dialog = StartUtil.getMainDialog();
                dialog.setSize(800, 600);
                try {
                    String newName = JOptionPane.showInputDialog(null, "请输入要修改的用户名：", "test");
                    if (StringUtils.isBlank(newName)) {
                        return;
                    }
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean());
                    AddUpdUsrComp addUpdUsrComp = new AddUpdUsrComp(conn, UsrFormType.usr) {
                        @Override
                        protected void informRefreshUsr() {
                            System.out.println("通知刷新用户树节点");
                        }
                    };
                    addUpdUsrComp.show(dialog,newName);
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(e);
                }finally {
                    ConnUtil.close(conn);
                }
            }
        };
    }

    /**
     * 修改角色
     */
    private static HButton getUpdateRoleButton(){
        return new HButton() {
            @Override
            protected void onClick() {
                HDialog dialog = StartUtil.getMainDialog();
                dialog.setSize(800, 600);
                Connection conn = null;
                try {
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean());
                    String newName = JOptionPane.showInputDialog(null, "请输入要修改角色：", "test");
                    if (StringUtils.isBlank(newName)) {
                        return;
                    }
                    AddUpdUsrComp comp = new AddUpdUsrComp(conn, UsrFormType.role) {
                        @Override
                        protected void informRefreshUsr() {
                            System.out.println("通知刷新角色树节点");
                        }
                    };
                    comp.show(dialog,newName);
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(e);
                }finally {
                    ConnUtil.close(conn);
                }
            }
        };
    }




}
