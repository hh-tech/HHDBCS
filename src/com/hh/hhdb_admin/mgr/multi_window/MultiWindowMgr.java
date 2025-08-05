package com.hh.hhdb_admin.mgr.multi_window;

import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;

import java.io.File;

/**
 * 平铺窗口
 */
public class MultiWindowMgr extends AbsGuiMgr {

    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public Enum<?> getType() {
        return null;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
        switch (csGuiBean.getOpType()) {
            case SHOW:
                String dataFile = System.getProperty("user.dir") + File.separator + "etc" + File.separator + "multiWin.dat";
                MultiWindowComp comp = new MultiWindowComp(CsVarUtil.parentFrame,new File(dataFile));
                SharePaneObj obj = new SharePaneObj();
                obj.setId(RandomUtil.getUUID());
                obj.setTitle(comp.getTitle());
                obj.setCsMgrEnum(CsMgrEnum.MULTI_WINDOW);
                obj.setFixTab(false);
                obj.setObj(comp.getPanel());
                CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);
                break;
            default:
                break;
        }
        return null;
    }
}
