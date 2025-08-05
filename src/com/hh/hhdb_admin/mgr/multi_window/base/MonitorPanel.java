package com.hh.hhdb_admin.mgr.multi_window.base;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HBasePanel;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.grid_split.LoadInf;
import com.hh.frame.swingui.view.container.grid_split.param.ParamData;
import com.hh.hhdb_admin.mgr.monitor.comp.DbMonitorComp;

public class MonitorPanel implements LoadInf {

    private DbMonitorComp dbMonitorComp;

    @Override
    public HBasePanel load(JsonObject jobj) {
        try {
        	JdbcBean jdbc=PkgPanelUtil.getJdbc();
            dbMonitorComp = new DbMonitorComp(DriverUtil.getDbType(jdbc),jdbc.getCurrentVersion(),PkgPanelUtil.getConn(),  new HFrame());
            return dbMonitorComp.getPanel();
        } catch (Exception e) {
            return PkgPanelUtil.getErrorPanel(e.getMessage());
        }
    }

    @Override
    public void close() {
        if (null != dbMonitorComp) {
            dbMonitorComp.closeRunnable();
        }
    }

   

	@Override
	public ParamData getPData() {
		// TODO Auto-generated method stub
		return null;
	}
}
