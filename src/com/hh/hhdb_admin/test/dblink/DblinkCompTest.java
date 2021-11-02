package com.hh.hhdb_admin.test.dblink;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.dblink.DblinkComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.io.File;
import java.sql.Connection;

/**
 * @author YuSai
 */
public class DblinkCompTest {

    public static void main(String[] args) throws Exception {
        HHSwingUi.init();
        IconFileUtil.setIconBaseDir(new File("etc/icon/"));
        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        if (jdbcBean != null) {
            HFrame frame = new HFrame(HFrame.SMALL_WIDTH);
            HPanel panel = new HPanel();
            Connection conn = ConnUtil.getConn(jdbcBean);
            DBTypeEnum dbTypeEnum = DriverUtil.getDbType(conn);
            frame.setWindowTitle(dbTypeEnum.name() + "--Dblink新增测试");
            DblinkComp dblinkComp = new DblinkComp(conn, jdbcBean.getUser(), "") {
                @Override
                public void refreshTree() {
                    System.out.println("保存成功");
                }
            };
            if (!DBTypeEnum.oracle.equals(dbTypeEnum)) {
                PopPaneUtil.info(dbTypeEnum.name() + "--不支持该功能");
                return;
            }
            panel.add(dblinkComp.getPanel());
            frame.setRootPanel(panel);
            frame.show();
        }

    }
}
