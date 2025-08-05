package com.hh.hhdb_admin.test.synonym2;


import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.synonym2.SynonymPanel;
import com.hh.hhdb_admin.test.MgrTestUtil;


public class TestSynonymComp {
    public static void main(String[] args) throws Exception {
    	MgrTestUtil.initConfLang();
    	MgrTestUtil.initCsVarJdbc();
    	HBarPanel bar=new HBarPanel();
    	bar.add(new HButton("打开") {
    		@Override
    		public void onClick() {
                new SynonymPanel(CsVarUtil.conn, CsVarUtil.jdbc,"TSP") {
                    @Override
                    public void inform() {
                        System.out.println("保存成功");
                    }
                };
    		}
    	});

        CsVarUtil.parentFrame.setToolBar(bar);
        CsVarUtil.parentFrame.show();
    }
}
