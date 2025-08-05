package com.hh.hhdb_admin.test.monitor;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.monitor.comp.DbMonitorComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TestMonitorComp {
    public static void main(String[] args) throws Exception {
    	MgrTestUtil.initConfLang();
    	MgrTestUtil.initCsVarJdbc();
        HTabPanel tabPane = new HTabPanel();
        DbMonitorComp dbMonitorComp = new DbMonitorComp(CsVarUtil.getDbtype(), CsVarUtil.jdbc.getCurrentVersion(),CsVarUtil.conn,  CsVarUtil.parentFrame);
        tabPane.setPanel("dbMonitor", dbMonitorComp.getPanel(),new HeaderConfig("数据库监控"));
        tabPane.selectPanel("dbMonitor");
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.set(tabPane.getComp());
        CsVarUtil.parentFrame.setWindowTitle("监控测试");
        MgrTestUtil.showComp(lastPanel);
        CsVarUtil.parentFrame.show();
    }

}
