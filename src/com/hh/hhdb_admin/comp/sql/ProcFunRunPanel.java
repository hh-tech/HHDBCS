package com.hh.hhdb_admin.comp.sql;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.parser.PosBean;
import com.hh.frame.sqlwin.rs.WinRsBean;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.sql.SqlAreaConf;

public class ProcFunRunPanel extends HSplitPanel implements Closeable{
	
	PkgSqlRsPanel rsPanel=null;
	PkgSqlTopPanel topPanel=null;
	
	protected void callDbTask(String schemaName, String sql) {
		PopDlgUtil.info(comp, "此处不支持导出");
	}
	
	public ProcFunRunPanel(SqlAreaConf conf, JdbcBean jdbc) throws Exception{
		super(false);
		rsPanel=new PkgSqlRsPanel(jdbc) {
			@Override
			void doExport(String schemaName, String sql) {
				callDbTask(schemaName,sql);
			}
			@Override
			public void setRs(Map<PosBean, WinRsBean> posMap) throws Exception {
				super.setRs(posMap);
				
				if(!getSelectedId().equals(ERRORPANE)&&!getSelectedId().matches("\\d")) {
					selectPanel(ERRORPANE);
				}
			}
			
            
        };
        
       
		topPanel=new ProFunTopPanel(conf,jdbc,rsPanel) ;
		
		super.setLastComp4One(topPanel);
		super.setPanelTwo(rsPanel);
		super.setSplitWeight(0.7);
	}
	
	
	
	
	public HTabPanel getRsPanel() {
		return rsPanel;
	}

	@Override
	public void close() throws IOException {
		topPanel.close();
	}
	
	public String getText(){
		return topPanel.harea.getText();
	}
	
	public void setText(String text) {
		topPanel.harea.setText(text);
	}
	
	
	class ProFunTopPanel extends PkgSqlTopPanel{

		public ProFunTopPanel(SqlAreaConf conf, JdbcBean jdbc, PkgSqlRsPanel rsPanel) throws Exception {
			super(conf, jdbc, rsPanel);
			HBarPanel barPanel = new HBarPanel( HBarLayout.newLayout(AlignEnum.LEFT));
			barPanel.add(runBtn);
			setHead(barPanel.getComp());
			setFoot(new HPanel().getComp());
		}
		
	}
	
}
