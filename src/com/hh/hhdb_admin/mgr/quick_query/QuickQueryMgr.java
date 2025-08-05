package com.hh.hhdb_admin.mgr.quick_query;

import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;

/**
 * 快捷查询
 */
public class QuickQueryMgr extends AbsGuiMgr {

	@Override
	public void init(JsonObject jObj) {
	}

	@Override
	public CsMgrEnum getType() {
		return CsMgrEnum.QUICK_CMD;
	}
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
        switch (csGuiBean.getOpType()) {
            case SHOW:
                QuickQueryComp queryPanel = new QuickQueryComp(CsVarUtil.jdbcCloneWithSsh(), CsVarUtil.parentFrame);
                SharePaneObj obj = new SharePaneObj();
                obj.setId(RandomUtil.getUUID());
                obj.setTitle(queryPanel.getTitle());
                obj.setCsMgrEnum(CsMgrEnum.QUICK_CMD);
                obj.setFixTab(false);
                obj.setObj(queryPanel);
                CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);
                break;
            default:
                break;
        }
        return null;
    }
}
