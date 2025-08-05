package com.hh.hhdb_admin.mgr.cmd2;

import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.CloseBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.comp.cmd.SqlCmdSplitPanel;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;

import java.util.HashMap;
import java.util.Map;

public class CmdMgr extends AbsGuiMgr {
	private Map<String, SqlCmdSplitPanel> map = new HashMap<>();
	
	@Override
	public void init(JsonObject jObj) {
	
	}

	@Override
	public CsMgrEnum getType() {
		return CsMgrEnum.CMD;
	}

	@Override
	public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
		BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
		switch (csGuiBean.getOpType()) {
			case CLOSE:
				CloseBean closeBean = new CloseBean(bj.getJobj());
				SqlCmdSplitPanel sqlCmdSplitPanel = map.get(closeBean.getId());
				sqlCmdSplitPanel.close();
				map.remove(closeBean.getId());
				break;
			case SHOW:
				CmdComp cmd = new CmdComp(CsVarUtil.jdbcCloneWithSsh(), CsVarUtil.parentFrame);
				String tabId = RandomUtil.getUUID();
				map.put(tabId,cmd.getPanel());
				SharePaneObj obj = new SharePaneObj();
				obj.setId(tabId);
				obj.setTitle(LangMgr.lang(Word4.COMMAND_WINDOW));
				obj.setCsMgrEnum(CsMgrEnum.CMD);
				obj.setFixTab(false);
				obj.setObj(cmd.getPanel());
				CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);
				break;
			default:
				break;
		}
		return null;
	}
}
