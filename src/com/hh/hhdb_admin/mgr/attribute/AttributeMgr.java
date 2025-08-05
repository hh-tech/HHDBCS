package com.hh.hhdb_admin.mgr.attribute;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;

/**
 * @author: Jiang
 * @date: 2020/10/14
 */

public class AttributeMgr extends AbsGuiMgr {

	@Override
	public void init(JsonObject jObj) {
	}

	@Override
	public Enum<?> getType() {
		return CsMgrEnum.ATTRIBUTE;
	}
	
	@Override
	public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
		AttributeBean csGuiBean = new AttributeBean(bj.getJobj());
		switch (csGuiBean.getOpType()) {
			case SHOW:
				if (CsVarUtil.jdbc == null) {
					return null;
				}
				AttributeComp comp = new AttributeComp();
				comp.showAttr(csGuiBean, CsVarUtil.jdbc, CsVarUtil.conn);
				break;
			default:
				break;
		}
		return null;
	}
}
