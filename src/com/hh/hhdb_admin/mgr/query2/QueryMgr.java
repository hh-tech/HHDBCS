package com.hh.hhdb_admin.mgr.query2;

import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.CloseBean;
import com.hh.hhdb_admin.common.bean.QueryBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.comp.sql.SqlSplitPanel;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;

import java.util.HashMap;
import java.util.Map;

public class QueryMgr extends AbsGuiMgr {
	public static final String WSQLTMP = "wsqltmp";		//查询器临时文件目录
	private Map<String, SqlSplitPanel> map = new HashMap<>();
	
	@Override
	public void init(JsonObject jObj) {
	}

	@Override
	public CsMgrEnum getType() {
		return CsMgrEnum.QUERY;
	}

	@Override
	public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
		BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
		switch (csGuiBean.getOpType()) {
			case CLOSE:
				CloseBean closeBean = new CloseBean(bj.getJobj());
				SqlSplitPanel query = map.get(closeBean.getId());
				query.close();
				map.remove(closeBean.getId());
				break;
			case SHOW:
				String tabId = RandomUtil.getUUID();
				QueryComp queryPanel = new QueryComp(CsVarUtil.jdbcCloneWithSsh(),new QueryBean(bj.getJobj()).getText(), CsVarUtil.parentFrame);
				map.put(tabId,queryPanel.getPanel());
				SharePaneObj obj = new SharePaneObj();
				obj.setId(tabId);
				obj.setTitle(LangMgr.lang(Word4.QUERY_WINDOW));
				obj.setCsMgrEnum(CsMgrEnum.QUERY);
				obj.setFixTab(false);
				obj.setObj(queryPanel.getPanel());
				CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);
				break;
			default:
				break;
		}
		return null;
	}
}
