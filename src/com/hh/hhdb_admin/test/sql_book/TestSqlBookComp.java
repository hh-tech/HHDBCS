package com.hh.hhdb_admin.test.sql_book;


import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookComp;
import com.hh.hhdb_admin.test.MgrTestUtil;


public class TestSqlBookComp {
	private static SqlBookComp bookComp;
    public static void main(String[] args) throws Exception {
    	MgrTestUtil.initConfLang();
    	HBarPanel bar=new HBarPanel();
    	bar.add(new HButton("打开") {
    		@Override
    		public void onClick()  {
    			try {
					bookComp.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}
    	});
    	
    	bookComp = new SqlBookComp(CsVarUtil.getEtcFile().getAbsolutePath()) {
            @Override
            protected void openTask(String filePath) {
                PopDlgUtil.info("打开任务框");
            }
        };
        CsVarUtil.parentFrame.setToolBar(bar);
    	CsVarUtil.parentFrame.show();
    }

}
