package com.hh.hhdb_admin.comp.sql;

import com.hh.frame.common.base.BaseJob;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.base.JobStatus;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.parser.PosBean;
import com.hh.frame.sqlwin.rs.WinRs;
import com.hh.frame.sqlwin.rs.WinRsBean;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import javax.swing.text.Element;

import java.util.HashMap;
import java.util.Map;

class PkgSqlRunJob extends BaseJob implements Runnable{
	private PkgSqlTopPanel panel;
	private PkgSqlRsPanel rsPanel;
	private JdbcBean jdbc;
	
	public PkgSqlRunJob(JdbcBean jdbc,PkgSqlTopPanel panel,PkgSqlRsPanel rsPanel) {
		this.panel=panel;
		this.rsPanel=rsPanel;
		this.jdbc=jdbc;
	}
	@Override
	public void run() {
		this.jobStatus=JobStatus.RUNNING;
		Map<PosBean, WinRsBean> posMap = new HashMap<>();
		panel.setBtn4runing(true);
		try {
			String sql = "";
			String selectedSql = panel.harea.getSelectedText();
			sql = StringUtils.isNotEmpty(selectedSql) ? selectedSql : panel.harea.getText();
			
			PkgSqlTopPanel.ExeType exeType=PkgSqlTopPanel.ExeType.valueOf(panel.execType.getValue());
			WinRs rs=null;
			PosBean pos=panel.getSelectPos();			
			if(exeType==PkgSqlTopPanel.ExeType.auto_split) {
				rs=panel.sqlWin.runSql(sql, pos);
			}else {
				rs=panel.sqlWin.runSqlNoParse(sql, pos);
			}
			//TODO 错误信息位置应该加入到Sqlwin中
			getPrecisePositon(posMap = rs.getRsMap());  //处理结果集中的错误信息
			rsPanel.setSqlWin(panel.sqlWin);
			rsPanel.setJdbc(jdbc);
			rsPanel.setRs(posMap);
			if (null != panel.autoCommit && !panel.autoCommit.isChecked()) {
				//PG系列数据库才需要回滚
//				if(SqlWinUtil.isPgBaseDB(panel.dbtype))
				if(DBTypeEnum.isPgSeries(DriverUtil.getDbType(jdbc), jdbc.getCurrentVersion())) {
					for (PosBean posBean : posMap.keySet()) {
						if (null != posMap.get(posBean).getErr()) {
							boolean ok=PopDlgUtil.confirm(panel.getComp(), LangMgr.sLang(CsL.RUNNING_ERROR));
							if(ok)
								panel.sqlWin.rollback();
							break;
						}
					}
				}
				
			}
		} catch (Exception e) {
			Lg.error(PkgSqlRunJob.class, e);
			if (e.getMessage()!=null&&e.getMessage().contains("com.hh.frame.parser.ora.gen.ParseException")) {
				PopDlgUtil.error(panel.getComp(), LangMgr.sLang(CsL.SYNTAX_CHECK_FAILURE));
			} else {
				PopDlgUtil.error(panel.getComp(),e);
				try {
					if (panel.sqlWin.getConn().isClosed()) {
						if (PopDlgUtil.confirm(panel.getComp(), "重新连接数据库？")) {
							panel.reconnect();
						}
					}
				} catch (Exception e1) {
					PopDlgUtil.error(panel.getComp(), e1);
					Lg.error(PkgSqlRunJob.class, e1);
				}
			}
		}finally {
			panel.setBtn4runing(false);
			panel.setBtn4Commit();
			this.jobStatus=JobStatus.DONE;
		}
	}
	

	
	/**
	 * 根据错误信息获得精准错误位置信息
	 */
	private void getPrecisePositon(Map<PosBean, WinRsBean> map) {
		try {
			for (PosBean posBean : map.keySet()) {
				WinRsBean rsBean = map.get(posBean);
				if (null != rsBean.getErr()) {
					String m2 = "";
					String message = rsBean.getErr().getMessage();
					int i = message.indexOf("Position:");
					if (i < 0) {
						i = message.indexOf(LangMgr.lang(Word2.POSITION));
						if (i > 0) {
							m2 = message.substring(i + 3);
						}
					} else {
						m2 = message.substring(i + 9);
					}
					if (i > 0) {
						String m1 = message.substring(0, i) + "Line:";
						int o = panel.harea.getRSyntaxTextArea().getLineStartOffset(posBean.getBeginLine() - 1);
						int p = 0;
						int col = 0;
						Element root = panel.harea.getRSyntaxTextArea().getDocument().getDefaultRootElement();
						p = root.getElementIndex(Integer.parseInt(m2.trim()) + o) + 1;
						col = Integer.parseInt(m2.trim()) + o - panel.harea.getRSyntaxTextArea().getLineStartOffset(p - 1);
						message = m1 + p + "  Position:" + col;
					}
					rsBean.setErr(new Exception(message));
				}
			}
		} catch (Exception e) {
			Lg.error(PkgSqlRunJob.class, e);
		}
	}
	public void setJdbc(JdbcBean jdbc) {
		this.jdbc = jdbc;
	}
}
