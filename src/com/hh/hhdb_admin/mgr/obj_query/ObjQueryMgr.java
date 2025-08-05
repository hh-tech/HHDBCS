package com.hh.hhdb_admin.mgr.obj_query;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.obj_query.comp.ObjQueryComp;
import com.hh.hhdb_admin.mgr.obj_query.comp.RedisObjQueryComp;

/**
 * 对象搜索
 * @Author: Jiang
 * @Date: 2021/7/9 10:33
 */
public class ObjQueryMgr extends AbsGuiMgr {
    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.OBJ_QUERY;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        JdbcBean jdbc = CsVarUtil.jdbcCloneWithSsh();
        BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
        switch (csGuiBean.getOpType()) {
            case SHOW:
                if (CsVarUtil.getDbtype() == DBTypeEnum.redis) {
                    new RedisObjQueryComp(jdbc, CsVarUtil.parentFrame).show();
                } else {
                    new ObjQueryComp(jdbc, CsVarUtil.parentFrame).show();
                }
                break;
            default:
                break;
        }
        return null;
    }
}
