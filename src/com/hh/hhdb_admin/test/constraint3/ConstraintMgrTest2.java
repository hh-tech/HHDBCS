package com.hh.hhdb_admin.test.constraint3;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.hhdb_admin.mgr.constraint3.ConstraintBean;
import com.hh.hhdb_admin.mgr.constraint3.ConstraintMgr;
import com.hh.hhdb_admin.test.MgrTestUtil;

import javax.swing.*;

class ConstraintMgrTest2 {

    public static void main(String[] args) throws Exception {


        ConstraintMgr constraintMgr3 = new ConstraintMgr();

        MgrTestUtil.initConfLang();
        MgrTestUtil.initCsVarJdbc();

        JComboBox<TreeMrType> box = new JComboBox<>();
        box.addItem(TreeMrType.PRIMARY_KEY_GROUP);
        box.addItem(TreeMrType.UNIQUE_KEY_GROUP);
        box.addItem(TreeMrType.CHECK_KEY_GROUP);
        box.addItem(TreeMrType.FOREIGN_KEY_GROUP);

        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(box);

        HButton hButton = new HButton("new") {
            @Override
            protected void onClick() {
                ConstraintBean.OprType baseOpEnum = ConstraintBean.OprType.PK;
                switch (TreeMrType.valueOf(box.getSelectedItem().toString())) {
                    case UNIQUE_KEY_GROUP:
                        baseOpEnum = ConstraintBean.OprType.UK;
                        break;
                    case CHECK_KEY_GROUP:
                        baseOpEnum = ConstraintBean.OprType.CK;
                        break;
                    case FOREIGN_KEY_GROUP:
                        baseOpEnum = ConstraintBean.OprType.FK;
                        break;
                    default:
                }
                ConstraintBean baseJsonBean = new ConstraintBean(baseOpEnum,null);
                baseJsonBean.setSchema("U1");
                baseJsonBean.setTableName("PERSON");
                baseJsonBean.setConstType(TreeMrType.valueOf(box.getSelectedItem().toString()));
                try {
                    constraintMgr3.doCall(baseJsonBean);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        lastPanel.setFoot(hButton.getComp());

        HFrame hFrame = new HFrame();
        hFrame.setRootPanel(lastPanel);
        hFrame.show();


    }
}