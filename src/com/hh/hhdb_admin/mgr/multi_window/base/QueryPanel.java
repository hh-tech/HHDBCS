package com.hh.hhdb_admin.mgr.multi_window.base;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HBasePanel;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.grid_split.LoadInf;
import com.hh.frame.swingui.view.container.grid_split.param.ParamData;
import com.hh.hhdb_admin.comp.sql.SqlSplitPanel;
import com.hh.hhdb_admin.mgr.query2.QueryComp;

public class QueryPanel  implements LoadInf {

    private QueryComp queryComp;

    @Override
    public HBasePanel load(JsonObject jobj) {
        try {
            queryComp = new QueryComp(PkgPanelUtil.getJdbc(), "", new HFrame());
            LastPanel lastPanel = new LastPanel(false);
            lastPanel.set(queryComp.getPanel().getComp());
            return PkgPanelUtil.getPanel(lastPanel);
        } catch (Exception e) {
            return PkgPanelUtil.getErrorPanel(e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            SqlSplitPanel query = queryComp.getPanel();
            query.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

   

	@Override
	public ParamData getPData() {
		// TODO Auto-generated method stub
		return null;
	}

}
