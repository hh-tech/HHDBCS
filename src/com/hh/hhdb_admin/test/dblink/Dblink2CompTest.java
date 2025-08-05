package com.hh.hhdb_admin.test.dblink;

import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;

import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.dblink2.Dblink2Comp;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class Dblink2CompTest {
    public static void main(String[] args) throws Exception {
        MgrTestUtil.initConfLang();
        MgrTestUtil.initCsVarJdbc();
        String schema= CsVarUtil.jdbc.getSchema();
        HBarPanel bar=new HBarPanel();
        HButton addBtn=new HButton("新增") {
            @Override
            public void onClick() {
                try {
                    Dblink2Comp dblink2Comp = new Dblink2Comp(CsVarUtil.conn,schema,null) {
                        @Override
                        public void refreshTree() {

                        }
                    };
                    dblink2Comp.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        bar.add(addBtn);
        CsVarUtil.parentFrame.setToolBar( bar);
        CsVarUtil.parentFrame.show();
    }
}
