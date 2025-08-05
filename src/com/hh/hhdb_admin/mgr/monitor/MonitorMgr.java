package com.hh.hhdb_admin.mgr.monitor;

import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.CloseBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;
import com.hh.hhdb_admin.mgr.monitor.comp.DbMonitorComp;
import org.apache.commons.lang3.StringUtils;


public class MonitorMgr extends AbsGuiMgr {
    private  DbMonitorComp dbMonitorComp;
    private String tabId;

    @Override
    public void init(JsonObject jObj) {}

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.MONITOR;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
        switch (csGuiBean.getOpType()) {
            case CLOSE:
                CloseBean closeBean = new CloseBean(bj.getJobj());
                if (closeBean.getId().equals(tabId) && null != dbMonitorComp) {
                    tabId = null;
                    dbMonitorComp.closeRunnable();
                }
                break;
            case SHOW:
                dbMonitorComp = new DbMonitorComp(CsVarUtil.getDbtype(),CsVarUtil.jdbc.getCurrentVersion(), CsVarUtil.newConn(CsVarUtil.jdbc),  CsVarUtil.parentFrame);
                if (StringUtils.isBlank(tabId)) tabId = RandomUtil.getUUID();
                SharePaneObj obj = new SharePaneObj();
                obj.setId(tabId);
                obj.setTitle(dbMonitorComp.getTitle());
                obj.setCsMgrEnum(CsMgrEnum.MONITOR);
                obj.setFixTab(false);
                obj.setObj(dbMonitorComp.getPanel());
                CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);
                break;
            default:
                break;
        }
        return null;
    }
}
