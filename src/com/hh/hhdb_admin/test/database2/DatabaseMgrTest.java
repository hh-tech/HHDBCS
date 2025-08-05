package com.hh.hhdb_admin.test.database2;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.database2.DataBaseBean;
import com.hh.hhdb_admin.test.MgrTestUtil;


public class DatabaseMgrTest {
    public static void main(String[] args) throws Exception {
        CsVarUtil.jdbc=new JdbcInfo(MgrTestUtil.getJdbcBean(DBTypeEnum.pgsql).getJobj());
        CsVarUtil.conn= ConnUtil.getConn(CsVarUtil.jdbc);
        CsVarUtil.init(DatabaseMgrTest.class,"conf.json");
        CsVarUtil.eng2.doCall(CsMgrEnum.MAIN_FRAME, new BaseOpBean(BaseOpEnum.INIT));
    
        HBarPanel barPanel2 = new HBarPanel(HBarLayout.newLayout(AlignEnum.LEFT));
    
        barPanel2.add(new HButton("ADD") {
            @Override
            protected void onClick() {
                CsVarUtil.eng2.doCall(CsMgrEnum.DATABASE, new DataBaseBean(DataBaseBean.OprType.ADD,null));
            }
        });
        HPanel p = new HPanel();
        p.add(barPanel2);
        MgrTestUtil.showComp(p);
        CsVarUtil.parentFrame.setWindowTitle("添加测试用例");
    }
}
