package com.hh.hhdb_admin.mgr.multi_window.base;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HBasePanel;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.grid_split.LoadInf;
import com.hh.frame.swingui.view.container.grid_split.param.ParamData;
import com.hh.hhdb_admin.comp.cmd.SqlCmdSplitPanel;
import com.hh.hhdb_admin.mgr.cmd2.CmdComp;

public class CmdPanel  implements LoadInf {



	private CmdComp cmdComp;

    @Override
    public HBasePanel load(JsonObject jobj) {
        try {
			cmdComp = new CmdComp(PkgPanelUtil.getJdbc(), new HFrame());
			LastPanel lastPanel = new LastPanel();
	        lastPanel.set(cmdComp.getPanel().getComp());
	        return PkgPanelUtil.getPanel(lastPanel);
		} catch (Exception e) {
			return PkgPanelUtil.getErrorPanel(e.getMessage());
		}
        
    }

    @Override
    public void close() {
    	try {
			SqlCmdSplitPanel cmd = cmdComp.getPanel();
			cmd.close();
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
