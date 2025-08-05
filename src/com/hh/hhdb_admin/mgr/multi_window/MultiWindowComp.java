package com.hh.hhdb_admin.mgr.multi_window;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.HWindow;
import com.hh.frame.swingui.view.container.grid_split.GridSplitPanel;
import com.hh.hhdb_admin.mgr.multi_window.base.CmdPanel;
import com.hh.hhdb_admin.mgr.multi_window.base.MonitorPanel;
import com.hh.hhdb_admin.mgr.multi_window.base.QueryPanel;

import java.io.File;

public class MultiWindowComp {
    private final HSplitPanel splitPanel;

    public MultiWindowComp(HWindow win,File multiFile) throws Exception {
        GridSplitPanel gridSplitPanel = new GridSplitPanel(multiFile);
        gridSplitPanel.putClass(MonitorPanel.class.getName(), LangMgr.lang(Word4.MONITORING_MANAGEMENT));
        gridSplitPanel.putClass(QueryPanel.class.getName(), LangMgr.lang(Word4.QUERY_WINDOW));
        gridSplitPanel.putClass(CmdPanel.class.getName(), LangMgr.lang(Word4.COMMAND_WINDOW));
        splitPanel = gridSplitPanel.getSplitPanel();
    }

    

    protected HSplitPanel getPanel() {
        return splitPanel;
    }

    public String getTitle() {
        return LangMgr.lang(Word4.TILE_WINDOWS);
    }
}
