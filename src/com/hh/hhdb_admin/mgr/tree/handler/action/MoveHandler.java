package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.redis.base.RedisAbsKeyObj;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.redis_obj.RedisObjUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.CsTreeUtil;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;
import org.apache.commons.lang3.StringUtils;

/**
 * 移动到
 */
public class MoveHandler extends AbsHandler {
    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        String schemaName = treeNode.getMrNode().getSchemaName();
        TreeMrType nodeType = treeNode.getType();
        switch (nodeType) {
            case STRING:
            case HASH:
            case LIST:
            case SET:
            case ZSET:
            case JSON:
            case UNSUPPORT:
                String newDbName = PopDlgUtil.nameInput(LangMgr.sLang(CsL.MOVE_DATABASE),"");
                if (StringUtils.isNotBlank(newDbName) && !newDbName.equals(schemaName)) {
                	String key=treeNode.getMrNode().getName();
                    RedisAbsKeyObj obj = RedisObjUtil.getRedisObj(CsVarUtil.conn,key);
                    if (obj.move(newDbName)) {
                    	String refreshId=CsTreeUtil.getTargetParentNode(treeNode, TreeMrType.ROOT_SCHEMA_OR_DB).getId();
                        CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(refreshId));
                        PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.OPERATE_SUCCESSFULLY));
                    } else {
                        PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.sLang(CsL.MOVE_FAILED));
                    }
                }
                break;
            default:
        }
    }
    
    
}
