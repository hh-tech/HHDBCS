package com.hh.hhdb_admin.mgr.cmd2;

import java.io.IOException;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HWindow;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;

import com.hh.frame.swingui.view.textarea2.sql.SqlAreaConf;
import com.hh.frame.swingui.view.textarea2.sql.SqlConfDlg;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.comp.cmd.SqlCmdSplitPanel;
import com.hh.hhdb_admin.conf.CmdPanelConf;

public class CmdComp {
	
	private SqlCmdSplitPanel sqlArea;
	private static SqlConfDlg confDlg=null;

	
	public CmdComp(JdbcBean jdbc, HWindow window) {
		try {
			SqlAreaConf sqlconf = new SqlAreaConf();
			sqlconf.setNullValue(CsVarUtil.CONF.getNullValue());
			sqlconf.setPageSize(CsVarUtil.CONF.getCmdPanelConf().getRowPerPage());
			sqlconf.setErrBreak(CsVarUtil.CONF.getCmdPanelConf().isErrorBreak());
			sqlconf.setAutoPop(CsVarUtil.CONF.getCmdPanelConf().isAutoPop());
			sqlconf.setFontSize(CsVarUtil.CONF.getCmdPanelConf().getFontSize());
			sqlconf.setTheme(CsVarUtil.CONF.getCmdPanelConf().getTareaTheme());
			if(confDlg==null) {
				confDlg=new SqlConfDlg(window.getWindow()) {
					@Override
					protected void onSaveTo(HTAreaConf conf) {
						if(null != conf) {
							SqlAreaConf sqlconf = (SqlAreaConf) conf;
							CmdPanelConf cmdPanelConf = CsVarUtil.CONF.getCmdPanelConf();
							cmdPanelConf.setRowPerPage(sqlconf.getPageSize());
							cmdPanelConf.setAutoPop(sqlconf.isAutoPop());
							cmdPanelConf.setFontSize(sqlconf.getFontSize());
							cmdPanelConf.setTAreaThemeType(sqlconf.getTheme());
							cmdPanelConf.setErrorBreak(sqlconf.isErrBreak());
							CsVarUtil.CONF.setNullValue(sqlconf.getNullValue());
							onConfirm();
							CsVarUtil.saveConfig();
							confDlg.hide();
						}
					}
				};
				confDlg.setWindowTitle(LangMgr.sLang(CsL.CONFIGURATION_COMMAND_WINDOW));
			}
			

			sqlArea=new SqlCmdSplitPanel(sqlconf,jdbc);
			sqlArea.setCfgDlg(confDlg);
			sqlArea.setFRDlg(GlobalWorkUtil.getFrDlg());
			sqlArea.setGotoDlg(GlobalWorkUtil.getAreaGotoDlg());
			sqlArea.requestFocusInWindow();
		}catch (Exception e){
			Lg.error(CmdComp.class, e);
			PopDlgUtil.error(window,e);
		}
	}
	
	public void close() throws IOException {
		sqlArea.close();	
	}
	
	public SqlCmdSplitPanel getPanel(){
		return sqlArea;
	}
}
