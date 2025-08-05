package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.redis.base.RedisAbsKeyObj;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.column.ColumnBean;
import com.hh.hhdb_admin.mgr.redis_obj.RedisObjUtil;
import com.hh.hhdb_admin.mgr.sequence2.SeqBean;
import com.hh.hhdb_admin.mgr.table2.TableBean;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.CsTreeUtil;
import com.hh.hhdb_admin.mgr.tree.RedisRefreshBean;
import com.hh.hhdb_admin.mgr.usr2.UsrBean;
import org.apache.commons.lang3.StringUtils;

/**
 * 重命名
 */

public class RenameHandler extends AbsHandler {

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        String name = treeNode.getName();
        String schemaName = getSchemaName();
        String tableName = getTableName();

        switch (treeNode.getType()) {
            case ROLE:
            case USER:
                UsrBean us = new UsrBean(UsrBean.OprType.RENAME,treeNode.getFolder().getId());
                us.setName(name);
                sendMsg(CsMgrEnum.USR, us);
                break;
            case COLUMN:
                ColumnBean bean = new ColumnBean(ColumnBean.ColOprType.RENAME,treeNode.getFolder().getId());
                bean.setSchema(schemaName);
                bean.setTableName(tableName);
                bean.setColumnName(name);
                sendMsg(CsMgrEnum.COLUMN, bean);
                break;
            case SEQUENCE:
                SeqBean sequenceBean = new SeqBean(SeqBean.OprType.RENAME,treeNode.getFolder().getId());
                sequenceBean.setSchema(schemaName);
                sequenceBean.setName(name);
                sendMsg(CsMgrEnum.SEQUENCE, sequenceBean);
                break;
            case TABLE:
                TableBean tableBean = new TableBean(TableBean.OprType.RENAME,treeNode.getFolder().getId());
                tableBean.setSchema(schemaName);
                tableBean.setTableName(name);
                sendMsg(CsMgrEnum.TABLE, tableBean);
                break;
            case STRING:
            case HASH:
            case LIST:
            case SET:
            case ZSET:
            case JSON:
            case UNSUPPORT:
            	name = treeNode.getMrNode().getName();
                String newName = PopDlgUtil.nameInput("请输入新的名称",name);
                if (StringUtils.isNotBlank(newName) && !newName.equals(name)) {
                    RedisAbsKeyObj obj = RedisObjUtil.getRedisObj(CsVarUtil.conn,name);
                    CsFolderBean folder=CsTreeUtil.getTargetParentNode(treeNode, TreeMrType.ROOT_SCHEMA_OR_DB);
                    if (obj.rename(newName)) {
                    	if(folder!=null)
                    		CsVarUtil.eng2.doCall(CsMgrEnum.TREE, new RedisRefreshBean(folder.getId(),newName));
                        PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.OPERATE_SUCCESSFULLY));
                    } else {
                        PopDlgUtil.info(CsVarUtil.parentFrame, "重命名失败!名称重复。");
                    }
                }
                break;
            default:
        }
    }
}
