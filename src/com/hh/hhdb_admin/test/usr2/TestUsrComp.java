package com.hh.hhdb_admin.test.usr2;

import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.usr2.UsrBean;
import com.hh.hhdb_admin.mgr.usr2.UsrPanel;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

public class TestUsrComp {
    public static void main(String[] args) throws Exception {
		MgrTestUtil.initConfLang();
		MgrTestUtil.initCsVarJdbc();
		HBarPanel bar=new HBarPanel();
		HButton createUsrBtn = getCreateUsrButton();
        createUsrBtn.setText("创建用户panel");
        
        HButton updateUsrPanel = getUpdateUsrButton();
        updateUsrPanel.setText("修改用户panel");
        
        HButton createRoleBtn = getCreateRoleBtn();
        createRoleBtn.setText("创建角色panel（oracle）");

        HButton updateRoleBtn = getUpdateRoleButton();
        updateRoleBtn.setText("修改角色panel（oracle）");
        bar.add(createUsrBtn,updateUsrPanel,createRoleBtn,updateRoleBtn);
        CsVarUtil.parentFrame.setToolBar(bar);
        CsVarUtil.parentFrame.show();
    }


    /**
     * 创建角色
     */
    private static HButton getCreateRoleBtn(){
        return new HButton() {
            @Override
            protected void onClick() {
                Connection conn = null;
                try {
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean());
                    UsrPanel comp = new UsrPanel(new UsrBean(UsrBean.OprType.ADD_ROLE),CsVarUtil.jdbc,conn);
                    comp.show();
                } catch (Exception e) {
                    PopDlgUtil.error(e);
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
                try {
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean());
                    UsrPanel addUpdUsrComp = new UsrPanel(new UsrBean(UsrBean.OprType.ADD_USER),CsVarUtil.jdbc,conn);
                    addUpdUsrComp.show();
                } catch (Exception e) {
                    PopDlgUtil.error(e);
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
                try {
                    String newName = PopDlgUtil.nameInput("请输入要修改的用户名：", "test");
                    if (StringUtils.isBlank(newName)) {
                        return;
                    }
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean());
                    UsrPanel addUpdUsrComp = new UsrPanel(new UsrBean(UsrBean.OprType.UPDATE_USER),CsVarUtil.jdbc,conn);
                    addUpdUsrComp.show(newName);
                } catch (Exception e) {
                    PopDlgUtil.error(e);
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
                Connection conn = null;
                try {
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean());
                    String newName = PopDlgUtil.nameInput("请输入要修改角色：", "test");
                    if (StringUtils.isBlank(newName)) {
                        return;
                    }
                    UsrPanel comp = new UsrPanel(new UsrBean(UsrBean.OprType.UPDATE_ROLE),CsVarUtil.jdbc,conn);
                    comp.show(newName);
                } catch (Exception e) {
                    PopDlgUtil.error(e);
                }finally {
                    ConnUtil.close(conn);
                }
            }
        };
    }
}
