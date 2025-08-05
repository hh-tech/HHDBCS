package com.hh.hhdb_admin.mgr.function;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.function2.FunctionBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.function.ui.deBug_from.OrDebugForm;
import com.hh.hhdb_admin.mgr.function.util.DebugUtil;
import com.hh.hhdb_admin.mgr.function.util.FunUtil;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;
@Deprecated
public class FunctionMgr extends AbsGuiMgr {

    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.FUNCTION;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        JdbcBean jdbcBean = CsVarUtil.jdbcCloneWithSsh();
        FunctionBean csGuiBean = new FunctionBean(bj.getJobj());
        FunctionBean.OprType opEnum = csGuiBean.getOprType();
        
        FunctionComp fun = null;
        if(opEnum != FunctionBean.OprType.DEBUG_FUNCTION && opEnum != FunctionBean.OprType.DEBUG){
            TreeMrType type = csGuiBean.getNodeType();
            String schemaName = csGuiBean.getSchema();
            String objName = csGuiBean.getName();
            String objId = csGuiBean.getId();
            fun = new FunctionComp(CsVarUtil.jdbc,CsVarUtil.conn,schemaName, objName, objId, type == TreeMrType.FUNCTION) {
                @Override
                protected void refresh() {
                    CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(csGuiBean.getRefreshNodeId()));
                }
            };
        }
        switch (opEnum) {
            case ADD:
                fun.addFun();
                break;
            case UPDATE:
                fun.updFun();
                break;
//            case DELETE:
//                fun.delete();
//                break;
            case RUN:
                fun.runFunction(csGuiBean.getPackName());
                break;
            case EXAMINE:
                fun.examine();
                break;
            case ADD_DEBUG_INFO:
                fun.addDebugInfo();
                break;
            case DEBUG_FUNCTION:
                DebugUtil.examine(csGuiBean.getSchema(),jdbcBean);
                FunUtil.getDebugBaseForm(csGuiBean,jdbcBean);
                break;
            case DEBUG:
                DebugUtil.examine(jdbcBean.getSchema(),jdbcBean);
                new OrDebugForm(jdbcBean);
                break;
            default:
                break;
        }
        return null;
    }
    
    public static HeaderConfig newHeaderConfig(String name){
        HeaderConfig headerConfig = new HeaderConfig(name);
        headerConfig.setTitleEditable(false);
        headerConfig.setFixTab(true);
        headerConfig.setDetachEnabled(false);
        return headerConfig;
    }
}
