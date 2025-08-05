package com.hh.hhdb_admin.test.db_task2;

import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.db_task2.TaskComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TaskCompTest {

    private static final HPanel rootPanel = new HPanel();

    public static void main(String[] args) throws Exception {
    	MgrTestUtil.initConfLang();
    	MgrTestUtil.initCsVarJdbc();
        HFrame f = new HFrame(1024, HFrame.fromW2H(1024));
        HBarPanel toolBar = new HBarPanel();
        HButton btn1 = new HButton("面板添加任务") {
            public void onClick() {
                LastPanel lastPanel = TaskComp.getLastPanel(CsVarUtil.jdbc, f);
                rootPanel.setLastPanel(lastPanel);
            }
        };

        toolBar.add(btn1);
        f.setToolBar(toolBar);
        f.setRootPanel(rootPanel);
        f.show();
    }
}
