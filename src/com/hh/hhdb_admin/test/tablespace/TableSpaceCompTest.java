package com.hh.hhdb_admin.test.tablespace;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.tablespace.TableSpaceComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.io.File;
import java.sql.Connection;

/**
 * @author YuSai
 */
public class TableSpaceCompTest {

    public static void main(String[] args) throws Exception {
        HHSwingUi.init();
        IconFileUtil.setIconBaseDir(new File("etc/icon/"));
        HFrame frame = new HFrame(HFrame.SMALL_WIDTH);
        Connection conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean());
        DBTypeEnum dbTypeEnum = DriverUtil.getDbType(conn);
        frame.setWindowTitle(dbTypeEnum.name() + "--表空间新增测试");
        TableSpaceComp spaceComp = new TableSpaceComp(conn, dbTypeEnum) {
            @Override
            public void refreshTree() {
                System.out.println("保存成功");
            }
        };
        if (DBTypeEnum.mysql.equals(dbTypeEnum) || DBTypeEnum.sqlserver.equals(dbTypeEnum)) {
            PopPaneUtil.info(dbTypeEnum.name() + "--不支持该功能");
            return;
        }
        LastPanel lastPanel = new LastPanel();
        lastPanel.setWithScroll(spaceComp.getPanel().getComp());
        HPanel rootPanel = new HPanel();
        rootPanel.setLastPanel(lastPanel);
        frame.setRootPanel(rootPanel);
        frame.show();
    }
}
