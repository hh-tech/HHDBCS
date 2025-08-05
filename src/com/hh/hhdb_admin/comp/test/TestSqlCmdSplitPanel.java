package com.hh.hhdb_admin.comp.test;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.laf.LafIdEnum;
import com.hh.frame.swingui.laf.LafMgr;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.textarea2.sql.SqlAreaConf;
import com.hh.frame.test.FrameTestUtil;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.comp.cmd.SqlCmdSplitPanel;
import com.hh.hhdb_admin.conf.CsConf;

public class TestSqlCmdSplitPanel {

	private static HFrame hFrame = new HFrame(HFrame.MIDDLE_WIDTH);

	public static void main(String[] args) throws Exception {
		LafMgr.setLaf(LafIdEnum.FAST);
		CsVarUtil.parentFrame=hFrame;
		LangMgr.addLang(CsL.class);
		CsVarUtil.CONF = new CsConf();
		HBarPanel bar=new HBarPanel();
		bar.add(LafMgr.getSizeSelectBox(),LafMgr.getSkinSelectBox());
		
		JdbcBean jdbc=FrameTestUtil.getJdbcBean(DBTypeEnum.mysql);
		SqlCmdSplitPanel sqlPanel=new SqlCmdSplitPanel(new SqlAreaConf(),jdbc);

		hFrame.setToolBar(bar);
		hFrame.setRootPanel(sqlPanel);
		hFrame.show();
		sqlPanel.requestFocusInWindow();
		hFrame.maximize();
		hFrame.setCloseType(true);
	}

}
