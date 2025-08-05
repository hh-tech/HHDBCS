package com.hh.hhdb_admin.mgr.toolbar;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;

import javax.swing.*;
import java.awt.*;

public class ToolbarMgr extends AbsGuiMgr {
	
	@Override
	public void init(JsonObject jObj) {

	}

	@Override
	public Enum<?> getType() {
		return CsMgrEnum.TOOLBAR;
	}

	@Override
	public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
		BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
		switch (csGuiBean.getOpType()) {
			case INIT:
				HBarLayout layout = new HBarLayout();
				layout.setAlign(AlignEnum.LEFT);
				layout.setBottomHeight(10);
				ToolbarComp toolbarComp = new ToolbarComp(layout);
				toolbarComp.getComp().setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.lightGray));
				
				SharePaneObj obj = new SharePaneObj();
				obj.setCsMgrEnum(CsMgrEnum.TOOLBAR);
				obj.setObj(toolbarComp);
				CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);
				break;
			default:
				break;
		}
		return null;
	}
}
