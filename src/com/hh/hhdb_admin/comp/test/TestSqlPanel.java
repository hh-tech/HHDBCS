package com.hh.hhdb_admin.comp.test;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.laf.LafIdEnum;
import com.hh.frame.swingui.laf.LafMgr;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.dlg.HTAreaFRDlg;
import com.hh.frame.swingui.view.textarea2.dlg.HTAreaGotoDlg;
import com.hh.frame.swingui.view.textarea2.sql.SqlAreaConf;
import com.hh.frame.swingui.view.textarea2.sql.SqlConfDlg;
import com.hh.frame.test.FrameTestUtil;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.comp.sql.SqlSplitPanel;
import com.hh.hhdb_admin.conf.CsConf;

public class TestSqlPanel {

	private static HFrame hFrame = new HFrame(HFrame.LARGE_WIDTH);
	private static SqlConfDlg confDlg=null;
	private static HTAreaFRDlg findDlg=null;
	private static HTAreaGotoDlg gotoDlg=null;
	public static void main(String[] args) throws Exception {
		LafMgr.setLaf(LafIdEnum.FAST);
		LangMgr.addLang(CsL.class);
		CsVarUtil.CONF = new CsConf();
		
		
		confDlg=new SqlConfDlg(hFrame.getWindow()) {
			@Override
			protected void onSaveTo(HTAreaConf conf) {
				PopDlgUtil.info(conf.toString());
			}
		};
		findDlg=new HTAreaFRDlg(hFrame.getWindow());
		gotoDlg=new HTAreaGotoDlg(hFrame.getWindow());
		HBarPanel bar=new HBarPanel();
		bar.add(LafMgr.getSizeSelectBox(),LafMgr.getSkinSelectBox());
		
	
		
		JdbcBean jdbc=FrameTestUtil.getJdbcBean(DBTypeEnum.oracle);
		SqlSplitPanel sqlPanel=new SqlSplitPanel(new SqlAreaConf(),jdbc){
			@Override
			protected void openOtherDialog() {
				PopDlgUtil.info("打开其它窗口");
			}
		};

		sqlPanel.setCfgDlg(confDlg);
		sqlPanel.setFRDlg(findDlg);
		sqlPanel.setGotoDlg(gotoDlg);
		hFrame.setToolBar(bar);
		hFrame.setRootPanel(sqlPanel);
		hFrame.show();
	}

}
