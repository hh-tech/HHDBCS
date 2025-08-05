package com.hh.hhdb_admin.mgr.login;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;


public class LoginMgr extends AbsGuiMgr {
//    private static LoginComp loginComp;

    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.LOGIN;
    }

    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        try {
//            if(loginComp == null) {
            LoginComp loginComp = new LoginComp() {
                    @Override
                    public void inform() {
                        CsVarUtil.eng2.doCall(CsMgrEnum.MAIN_FRAME, new BaseOpBean(BaseOpEnum.INIT));
                    }
                };
//            }
            if(bj instanceof SwitchBean) {
              loginComp.switchSchema(((SwitchBean)bj).getSwitchName());
              return null;
            }
            
            BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
            switch (csGuiBean.getOpType()) {
                case INIT:
                    loginComp.showLogin();
                    break;
                case LOGIN_SWITCH:
                    loginComp.switchLogin();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            PopDlgUtil.error(CsVarUtil.parentFrame, e);
        }
        return null;
    }
}
