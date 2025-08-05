package com.hh.hhdb_admin.comp.cmd;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.script.ScriptLoadBar;
import com.hh.frame.swingui.view.textarea2.dlg.HTAreaFRDlg;
import com.hh.frame.swingui.view.textarea2.dlg.HTAreaGotoDlg;
import com.hh.frame.swingui.view.textarea2.sql.HSqlArea;
import com.hh.frame.swingui.view.textarea2.sql.SqlAreaConf;
import com.hh.frame.swingui.view.textarea2.sql.SqlConfDlg;

public class SqlCmdSplitPanel extends AbsHComp {
	private HSplitPanel split = new HSplitPanel(false);
	private PkgSqlCmdPanel cmdPanel;
	private PkgCmdScriptPanel scriptPanel;
	
	public SqlCmdSplitPanel(SqlAreaConf conf, JdbcBean jdbc) throws Exception {
		cmdPanel = new PkgSqlCmdPanel(conf, jdbc);
		split.setSplitWeight(0.8);
		LastPanel panleOne = new LastPanel();
		panleOne.set(cmdPanel.getComp());
		split.setLastComp4One(panleOne);
		scriptPanel = new PkgCmdScriptPanel(cmdPanel,jdbc);
		
		JdbcInfo info = new JdbcInfo(jdbc.getJobj());
		ScriptLoadBar sBar = new ScriptLoadBar() {

			@Override
			public void sendMessage(String string) {
				try {
					cmdPanel.harea.doCmd(string);
					requestFocusInWindow();
				} catch (Throwable e) {
					PopDlgUtil.logErr(SqlCmdSplitPanel.class, split.getComp(), e);
				}
			}
		};
		
		String path = info.getScriptDirPath();
		if(!StringUtils.isBlank(path)) {
			DBTypeEnum type = DriverUtil.getDbType(jdbc);
			

			
			
			
			
		
			File f = new File(path,type.name().toLowerCase());
			File dir = new File(path);
			if(dir.exists()&&dir.isDirectory()) {
				sBar.loadDir(dir);
				if(f.exists()&&f.isDirectory()) {
					sBar.loadDir(f);
				}
			}
			
		}
		
		scriptPanel.setScriptBar(sBar.getHComp());
		split.setLastComp4Two(scriptPanel);
		this.comp = split.getComp();
	}

	public void setCfgDlg(SqlConfDlg dlg) {
		cmdPanel.harea.setCfgDlg(dlg);
	}

	public void setFRDlg(HTAreaFRDlg dlg) {
		cmdPanel.harea.setFRDlg(dlg);
	}

	public void setGotoDlg(HTAreaGotoDlg dlg) {
		cmdPanel.harea.setGotoDlg(dlg);
	}

	/**
	 * 聚焦
	 */
	public void requestFocusInWindow() {
		scriptPanel.getCodeArea().requestFocusInWindow();
	}

	public HSplitPanel getSplit() {
		return split;
	}

	public void close() throws IOException {
		cmdPanel.close();
		scriptPanel.close();
	}
	
	public HSqlArea getHarea() {
		return cmdPanel.harea;
	}
}
