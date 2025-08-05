package com.hh.hhdb_admin.mgr.delete;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.ProgressBarInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class DeleteComp {

    private final HDialog dialog;
    private final HTable resTable = new HTable();
    protected final ProgressBarInput progressBarInput;
    protected final HButton stopBtn;
    protected final List<Map<String, String>> data = new ArrayList<>();
    protected boolean isStop = false;
    protected Connection conn;
    protected Statement stat;

    public DeleteComp(DeleteBean deleteBean, JdbcBean jdbc, HWindow parent) {
        dialog = new HDialog(parent, 600, 400) {
            @Override
            protected void closeEvent() {
                super.closeEvent();
                doStop();
            }
        };
        dialog.setWindowTitle(LangMgr.lang(Word2.DELETE));
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setStatusBar(new HBarPanel());

        stopBtn = new HButton(LangMgr.lang(Word2.STOP)) {
            @Override
            protected void onClick() {
                doStop();
            }
        };
        HPanel root = new HPanel();
        HPanel lineOne = new HPanel(new HDivLayout(GridSplitEnum.C10));
        progressBarInput = new ProgressBarInput();
        int max=1;
        if(deleteBean.getObjNames().size()>1)
        	max=deleteBean.getObjNames().size();
      
        progressBarInput.setMaximum(max);
        progressBarInput.setValue(0);
        lineOne.add(progressBarInput);
        lineOne.add(stopBtn);

        resTable.addCols(new DataCol("name", LangMgr.lang(Word2.NAME)), new DataCol("res", LangMgr.lang(Word2.RESULT)));
        data.clear();
        for (String name : deleteBean.getObjNames()) {
            Map<String, String> lineData = new HashMap<>();
            lineData.put("name", name);
            lineData.put("res", LangMgr.sLang(CsL.TO_BE_RUN));
            data.add(lineData);
        }
        loadData();
        root.add(new HeightComp(5));
        root.add(lineOne);

        LastPanel lastPanel = new LastPanel();
        lastPanel.setTitle(LangMgr.lang(Word2.RESULT));
        lastPanel.setWithScroll(resTable.getComp());
        root.setLastPanel(lastPanel);
        dialog.setRootPanel(root);
        try {
        	conn=CsVarUtil.newConn(CsVarUtil.jdbc);
			stat = conn.createStatement();
		} catch (Exception e) {
            Lg.error(DeleteComp.class, e);
            PopDlgUtil.error(CsVarUtil.parentFrame,e);
			return;
		}
        new Thread(new DelThread(deleteBean, jdbc, this)).start();
        dialog.show();
    }

    private void doStop() {
        isStop = true;
        try {
        	//mongodb的stat可能为null;
        	if(stat!=null)
        		stat.cancel();
        } catch (SQLException e) {
            Lg.error(getClass(), e);
        }
    }

    protected void loadData() {
        resTable.load(data, 0);
    }

    
}
