package com.hh.hhdb_admin.comp;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.lg.Lg;
import com.hh.frame.parser.ParserUtil;
import com.hh.frame.swingui.view.container.HBorderPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.HBorderLayout.PositionType;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
//import com.hh.frame.swingui.view.util.font.FontUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;

public class SqlExeDlgTool {
	private HDialog dlg;
	private Statement stmt;
	private LabelInput label=new LabelInput("");
	private Timer timer=new Timer();
	private TimerTask task;
	private long begin=0;
	private boolean exeSuc=false;
	
	public SqlExeDlgTool() {
//		label.setFont(FontUtil.genFont(20));
		dlg=new HDialog(CsVarUtil.parentFrame,300,150,true) {
			protected void closeEvent() {
				cancel();
			}
			protected void onCancel() {
				cancel();
			}
		};
		HBorderPanel panel=new HBorderPanel();
		panel.set(PositionType.CENTER, label);
		dlg.setRootPanel(panel);
		dlg.removeOkBtn();
		task = new TimerTask() {
			public void run() {
				if(begin!=0) {
					long take=System.currentTimeMillis()-begin;
					label.setValue("运行"+take/1000+"秒");
					label.setAlign(AlignEnum.CENTER);
				}
            }
		};
		timer.schedule(task, 0,100);
	}
	
	public boolean exeSql(JdbcBean jdbc,Connection conn,String sql) {
		exeSuc=false;
		Lg.debug(getClass(), "running:"+sql);
		DBTypeEnum dbtype=DriverUtil.getDbType(jdbc);
		sql=sql.trim();
		if(!sql.endsWith(";")) 
			sql=sql+";";
		final String  trimedSQL=sql;
		new Thread() {
			public void run() {
				begin=System.currentTimeMillis();
				try {
					List<String> sqls=ParserUtil.split(dbtype,jdbc.getCurrentVersion(), trimedSQL);
					stmt = conn.createStatement();
					for(String s:sqls) {
						s=SqlStrUtil.trim(dbtype, s);
						stmt.execute(s);
					}
					stmt.close();
					if(!dlg.isVisible()) {
						Thread.sleep(100);
					}
					exeSuc=true;
				}catch(Exception e){
					PopDlgUtil.error(trimedSQL+"\n"+e.getMessage());
					Lg.error(getClass(), e);
				}finally {
					stmt=null;
					//解决偶发性界面无故卡死
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							dlg.hide();
						}
					});
					
					begin=0;
				}
			}
			
		}.start();
		dlg.show();
		return exeSuc;
	}
	
	public void cancel()  {
		if(stmt!=null)
			try {
				stmt.cancel();
			} catch (SQLException e) {
				PopDlgUtil.error(e);
				Lg.error(getClass(), e);
			}finally {
				stmt=null;
				dlg.hide();
				begin=0;
				exeSuc=false;
			}
	}
	public void dispose()  {
		dlg.dispose();
	}
}
