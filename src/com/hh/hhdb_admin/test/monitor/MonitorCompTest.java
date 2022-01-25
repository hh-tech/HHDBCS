package com.hh.hhdb_admin.test.monitor;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.monitor.comp.DbMonitorComp;
import com.hh.hhdb_admin.mgr.monitor.comp.SystemMonitorComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.io.File;
import java.sql.Connection;

/**
 * @author YuSai
 */
public class MonitorCompTest {

    private static SystemMonitorComp sysComp;

    public static void main(String[] args) throws Exception {
        HHSwingUi.init();
        IconFileUtil.setIconBaseDir(new File("etc/icon/"));
        HFrame frame = new HFrame(HFrame.LARGE_WIDTH);
        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        Connection conn = ConnUtil.getConn(jdbcBean);
        DBTypeEnum dbTypeEnum = DriverUtil.getDbType(conn);
        HTabPanel tabPane = new HTabPanel() {
            @Override
            protected void onClose(String id) {
                if ("systemMonitor".equals(id)) {
                    sysComp.closeRunnable();
                }
            }
        };
        DbMonitorComp dbMonitorComp = new DbMonitorComp(conn, jdbcBean, dbTypeEnum);
        tabPane.addPanel("dbMonitor", dbMonitorComp.getPanel(),new HeaderConfig("数据库监控"));
        tabPane.selectPanel("dbMonitor");

        if (DBTypeEnum.hhdb.equals(dbTypeEnum)) {
            sysComp = new SystemMonitorComp(conn, dbTypeEnum) {
                @Override
                public void repaint() {
                    tabPane.addPanel("systemMonitor", sysComp.getPanel(),new HeaderConfig("系统监控"));
                    tabPane.selectPanel("systemMonitor");
                }
            };
        }
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.set(tabPane.getComp());
        frame.setWindowTitle(dbTypeEnum + "--监控测试");
        HPanel rootPanel = new HPanel();
        rootPanel.setLastPanel(lastPanel);
        frame.setRootPanel(rootPanel);
        frame.show();
    }

}
