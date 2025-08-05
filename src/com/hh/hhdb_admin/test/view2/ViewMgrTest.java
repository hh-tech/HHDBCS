package com.hh.hhdb_admin.test.view2;

import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.test.MgrTestUtil;

/**
 *
 */
public class ViewMgrTest {
    public static void main(String[] args) throws Exception {
        MgrTestUtil.initCsVarJdbc();
        MgrTestUtil.doTestMgr(ViewMgrTest.class);
        CsVarUtil.parentFrame.show();
    }
}
