package com.hh.hhdb_admin.mgr.function2;

import java.awt.Component;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.hhdb_admin.mgr.function2.debug.base.DebugBean;
import com.hh.hhdb_admin.mgr.function2.debug.panel.DebugPanel;

public abstract class DebugComp extends HDialog {
	
	private DebugPanel debugPanel;
	
	public DebugComp(Component parent,DebugBean bean) throws Exception {
		super(parent, 1000,680, false);
		
		
		debugPanel = new DebugPanel(bean) {
			@Override
			protected String getDebugSql() {
				return DebugComp.this.getDebugSql();
			}
		};
		
		 
		if(debugPanel == null) {
			throw new Exception("暂不支持");
		}
		
		setRootPanel(debugPanel);
		setStatusBar(null);
		setWindowTitle(LangMgr.lang(Word2.DEBUG));
	}
	
	

	@Override
	protected void closeEvent() {
		debugPanel.close();
	}
	
	protected abstract String getDebugSql();
	

	
	
}
