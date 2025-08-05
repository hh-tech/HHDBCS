package com.hh.hhdb_admin.mgr.vm_editor;

import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.VmBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;

/**
 * 模板编辑器
 */
public class VmMgr extends AbsGuiMgr {
	@Override
	public void init(JsonObject jObj) {
	}

	@Override
	public CsMgrEnum getType() {
		return CsMgrEnum.VM;
	}
	
	@Override
	public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
		BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
		switch (csGuiBean.getOpType()) {
			case SHOW:
				SharePaneObj obj = new SharePaneObj();
				obj.setId(RandomUtil.getUUID());
				obj.setTitle(LangMgr.lang(Word4.VM_WINDOW));
				obj.setCsMgrEnum(CsMgrEnum.VM);
				obj.setFixTab(false);
				obj.setObj(new VmComp(CsVarUtil.getDbtype(),new VmBean(bj.getJobj()).getText()));
				CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);
				break;
			default:
				break;
		}
		return null;
	}

}
