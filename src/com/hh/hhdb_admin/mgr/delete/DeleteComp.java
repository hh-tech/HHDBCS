package com.hh.hhdb_admin.mgr.delete;

import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HWindow;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.ProgressBarInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Jiang
 * @Date: 2021/9/7 14:21
 */
public class DeleteComp {

    private final HDialog dialog;
    private final HTable resTable = new HTable();
    protected final ProgressBarInput progressBarInput;
    protected final HButton stopBtn;
    protected final List<Map<String, String>> data = new ArrayList<>();
    protected boolean isStop = false;
    protected Connection conn;
    protected Statement stat;

    public DeleteComp(List<NodeInfo> nodeInfoList, LoginBean loginBean, HWindow parent) {
        dialog = new HDialog(parent, 600, 400) {
            @Override
            protected void closeEvent() {
                super.closeEvent();
                isStop = true;
                try {
					stat.cancel();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
            }
        };
        dialog.setWindowTitle("执行中");
        dialog.setIconImage(IconFileUtil.getLogo());

        stopBtn = new HButton("中止运行") {
            @Override
            protected void onClick() {
                isStop = true;
                try {
					stat.cancel();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                dialog.dispose();
            }
        };
        if (nodeInfoList.get(0).getOperType() == OperateType.TRUN) stopBtn.setEnabled(false);
        
        HPanel root = new HPanel();
        HPanel lineOne = new HPanel(new HDivLayout(GridSplitEnum.C10));
        progressBarInput = new ProgressBarInput();
        progressBarInput.setMaximum(nodeInfoList.size());
        progressBarInput.setValue(0);
        lineOne.add(progressBarInput);
        lineOne.add(stopBtn);

        resTable.addCols(new DataCol("name", "名称"), new DataCol("res", "结果"));
        data.clear();
        for (NodeInfo nodeInfo : nodeInfoList) {
            Map<String, String> lineData = new HashMap<>();
            lineData.put("name", nodeInfo.getName());
            lineData.put("res", "待运行");
            data.add(lineData);
        }
        loadData();
        root.add(new HeightComp(5));
        root.add(lineOne);

        LastPanel lastPanel = new LastPanel();
        lastPanel.setTitle("操作结果");
        lastPanel.setWithScroll(resTable.getComp());
        root.setLastPanel(lastPanel);
        dialog.setRootPanel(root);
        try {
			conn = ConnUtil.getConn(loginBean.getJdbc());
			stat = conn.createStatement();
		} catch (Exception e) {
			PopPaneUtil.error(StartUtil.parentFrame.getWindow(),e);
			return;
		}
        
        new Thread(new DelThread(nodeInfoList, loginBean, this) {
            @Override
            public void afterDelete() {
            	close();
                refresh();
            }
        }).start();
        dialog.show();
    }

    protected void loadData() {
        resTable.load(data, 0);
    }

    public void refresh() {

    }
    
    private void close() {
    	if(stat!=null) {
    		try {
				stat.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
    	if(conn!=null) {
    		try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
    }
}
