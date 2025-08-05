package com.hh.hhdb_admin.mgr.gis;

import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;

/**
 * GIS插件
 */
public class GisMgr extends AbsGuiMgr {
    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.GIS;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
        switch (csGuiBean.getOpType()) {
            case SHOW:
                SharePaneObj obj = new SharePaneObj();
                obj.setId(RandomUtil.getUUID());
                obj.setTitle("GIS");
                obj.setCsMgrEnum(CsMgrEnum.GIS);
                obj.setFixTab(false);
                obj.setObj(new GisComp(CsVarUtil.jdbcCloneWithSsh()));
                CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);
                break;
            default:
                break;
        }
        return null;
    }
}
