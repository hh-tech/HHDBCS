package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.TableOpenBean;
import com.hh.hhdb_admin.common.bean.TableOpenBean.OpenObjType;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.mongo.MongoDocOpenBean;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;

/**
 * 处理打开事件
 *
 * @author: Jiang
 * @date: 2020/9/15
 */

public class OpenHandler extends AbsHandler {

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        String name = treeNode.getName();
        String schemaName = getSchemaName();
        TableOpenBean tableOpenBean;
        switch (treeNode.getType()) {
            case VIEW:
            case M_VIEW:
                TableOpenBean  viewBean = new TableOpenBean(TableOpenBean.OprType.SHOW,null);
                viewBean.setSchema(schemaName);
                viewBean.setTableName(name);
                viewBean.setOpenObjType(OpenObjType.VIEW);
                sendMsg(CsMgrEnum.TABLE_OPEN, viewBean);
                break;
            case PARTITION:
//                PartitionBean partitionBean = new PartitionBean(PartitionBean.OprType.OPEN, null);
//                partitionBean.setSchema(schemaName);
//                partitionBean.setTableName(tableName);
//                partitionBean.setName(name);
//                sendMsg(CsMgrEnum.PARTITION, partitionBean);
                TableOpenBean  partionBean = new TableOpenBean(TableOpenBean.OprType.SHOW,null);
                partionBean.setSchema(schemaName);
                partionBean.setTableName(tableName);
                partionBean.setPartitionTableName(name);
                partionBean.setOpenObjType(OpenObjType.PATITION);
                sendMsg(CsMgrEnum.TABLE_OPEN, partionBean);
                
                
                
                break;
            case TABLE:
            	if(CsVarUtil.getDbtype() == DBTypeEnum.mongodb) {
            		MongoDocOpenBean mongoCollection = new MongoDocOpenBean(treeNode.getFolder().getId());
            		mongoCollection.setCollName(name);
            		mongoCollection.setDbName(schemaName);
            		sendMsg(CsMgrEnum.MONGO_MGR, mongoCollection);
            		break;
            	}
            default:
//                boolean isNew2 = PopDlgUtil.confirm("新的/旧的");
//                if (isNew2) {
//                    com.hh.hhdb_admin.mgr.table_open2.TableOpenBean tableOpenBean1;
//                    tableOpenBean1 = new com.hh.hhdb_admin.mgr.table_open2.TableOpenBean(com.hh.hhdb_admin.mgr.table_open2.TableOpenBean.OprType.SHOW,null);
//                    tableOpenBean1.setTableName(name);
//                    tableOpenBean1.setSchema(schemaName);
//                    sendMsg(CsMgrEnum.TABLE_OPEN2, tableOpenBean1);
//                } else {
                    tableOpenBean = new TableOpenBean(TableOpenBean.OprType.SHOW,null);
                    tableOpenBean.setTableName(name);
                    tableOpenBean.setSchema(schemaName);
                    sendMsg(CsMgrEnum.TABLE_OPEN, tableOpenBean);
//                }
        }
    }

}
