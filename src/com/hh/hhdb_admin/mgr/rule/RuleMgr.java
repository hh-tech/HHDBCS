package com.hh.hhdb_admin.mgr.rule;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;

/**
 * @author: Jiang
 * @date: 2020/7/24
 */

public class RuleMgr extends AbsGuiMgr {

    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public Enum<?> getType() {
        return null;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        RuleBean csGuiBean = new RuleBean(bj.getJobj());
        switch (csGuiBean.getOprType()) {
            case ADD:
                RuleComp ruleComp = new RuleComp();
                ruleComp.add(csGuiBean);
                break;
            default:
                break;
        }
        return null;
    }
}
