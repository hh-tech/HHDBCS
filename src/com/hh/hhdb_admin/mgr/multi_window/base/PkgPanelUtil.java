package com.hh.hhdb_admin.mgr.multi_window.base;

import java.sql.Connection;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.hhdb_admin.common.util.CsVarUtil;

class PkgPanelUtil {
	protected static HPanel getErrorPanel(String errorMsg) {
        HPanel panel = new HPanel();
        panel.add(new LabelInput(errorMsg, AlignEnum.LEFT));
        return panel;
    }

    protected static HPanel getPanel(LastPanel lastPanel) {
        HPanel panel = new HPanel();
        panel.setLastPanel(lastPanel);
        return panel;
    }
    
    protected static JdbcBean getJdbc() throws Exception{
    	return CsVarUtil.jdbcCloneWithSsh();
    }
    
    protected static Connection getConn() throws Exception{
    	return CsVarUtil.conn;
    }
}
