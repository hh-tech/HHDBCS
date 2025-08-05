package com.hh.hhdb_admin.mgr.tree.handler.action;

//import com.hh.frame.common.util.db.SelectTableSqlUtil;
//import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
//import com.hh.frame.lang2.LangMgr;
//import com.hh.frame.redis.db.ResdiDb;
//import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
//import com.hh.hhdb_admin.CsMgrEnum;
//import com.hh.hhdb_admin.common.CsL;
//import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
//import com.hh.hhdb_admin.mgr.tree.CsTreeMgr;
//import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.Map;

/**
 * 设置
 */
public class SettingsHandler extends AbsHandler {
    
    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
//        TreeMrType nodeType = treeNode.getType();
//        switch (nodeType) {
//            case ROOT_DATA_MODEL:
//                Map<String, Integer> dataSize = CsTreeMgr.dataSize;
//                Integer max = dataSize.get(schemaName) != null ? dataSize.get(schemaName) : ResdiDb.DEFAULT_MAX;
//                String str = PopDlgUtil.nameInput(LangMgr.sLang(CsL.OBJECTS_MAXIMUM_LOAD_QUANTITY),max.toString());
//                if (StringUtils.isNotBlank(str)) {
//                    if (SelectTableSqlUtil.isNumeric(str) && Integer.parseInt(str) > 0) {
//                        dataSize.put(schemaName, Integer.valueOf(str));
//                        CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(treeNode.getId()));
//                    } else {
//                        PopDlgUtil.info(CsVarUtil.parentFrame,LangMgr.sLang(CsL.POSITIVE_NTEGER));
//                    }
//                }
//                break;
//            default:
//        }
    }
    
}
