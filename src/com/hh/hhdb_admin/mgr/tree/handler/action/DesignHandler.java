package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbobj3.AllDbObjType;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.column.ColumnBean;
import com.hh.hhdb_admin.mgr.dblink2.DbLinkBean;
import com.hh.hhdb_admin.mgr.function2.FunctionBean;
import com.hh.hhdb_admin.mgr.mongo.MongoDocAddUpdateBean;
import com.hh.hhdb_admin.mgr.pack.PackageBean;
import com.hh.hhdb_admin.mgr.redis_obj.RedisObjBean;
import com.hh.hhdb_admin.mgr.schema2.SchemaBean;
import com.hh.hhdb_admin.mgr.sequence2.SeqBean;
import com.hh.hhdb_admin.mgr.synonym2.SynonymBean;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.CsTreeUtil;
import com.hh.hhdb_admin.mgr.trigger2.TriggerBean;
import com.hh.hhdb_admin.mgr.type.TypeBean;
import com.hh.hhdb_admin.mgr.usr2.UsrBean;
import com.hh.hhdb_admin.mgr.view2.ViewBean;
import org.apache.commons.lang3.StringUtils;


public class DesignHandler extends AbsHandler {

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
    	
        String name = treeNode.getName();
        String schemaName = getSchemaName();
        String tableName = getTableName();
        TreeMrType nodeType = treeNode.getType();
        switch (nodeType) {
            case USER:
                UsrBean us = new UsrBean(UsrBean.OprType.UPDATE_USER,treeNode.getFolder().getId());
                us.setName(name);
                sendMsg(CsMgrEnum.USR, us);
                return;
            case ROLE:
                UsrBean ro = new UsrBean(UsrBean.OprType.UPDATE_ROLE,treeNode.getFolder().getId());
                ro.setName(name);
                sendMsg(CsMgrEnum.USR, ro);
                return;
            case VIEW:
                ViewBean viewBean = new ViewBean(ViewBean.OprType.UPDATE,treeNode.getFolder().getId());
                viewBean.setSchema(schemaName);
                viewBean.setName(name);
                sendMsg(CsMgrEnum.VIEW, viewBean);
                break;
            case FUNCTION:
            case TRIGGER_FUNCTION:
            case PROCEDURE:
                FunctionBean functionBean = new FunctionBean(FunctionBean.OprType.UPDATE,treeNode.getFolder().getId());
                functionBean.setSchema(schemaName);
                functionBean.setName(name);
                functionBean.setId(StringUtils.isBlank(treeNode.getObjectId()) ? "" : treeNode.getObjectId());
                functionBean.setNodeType(nodeType == TreeMrType.PROCEDURE ? TreeMrType.PROCEDURE : TreeMrType.FUNCTION);
                sendMsg(CsMgrEnum.FUNCTION, functionBean);
                break;
            case SEQUENCE:
                SeqBean sequenceBean = new SeqBean(SeqBean.OprType.UPDATE,treeNode.getFolder().getId());
                sequenceBean.setSchema(schemaName);
                sequenceBean.setName(name);
                sendMsg(CsMgrEnum.SEQUENCE, sequenceBean);
                break;
            case TRIGGER:
                TriggerBean triggerBean = new TriggerBean(TriggerBean.OprType.UPDATE,treeNode.getFolder().getId());
                triggerBean.setSchema(schemaName);
                triggerBean.setTableName(tableName);
                triggerBean.setName(name);
                sendMsg(CsMgrEnum.TRIGGER, triggerBean);
                break;
            case SCHEMA:
                SchemaBean schemaBean = new SchemaBean(SchemaBean.OprType.UPDATE,treeNode.getFolder().getId());
                schemaBean.setSchema(name);
                sendMsg(CsMgrEnum.SCHEMA, schemaBean);
                break;
            case COLUMN:
            	if(DriverUtil.getDbType(jdbc)==DBTypeEnum.mongodb) {
            		MongoDocAddUpdateBean b=new MongoDocAddUpdateBean(treeNode.getFolder().getId());
            		b.setDbName(schemaName);
            		b.setCollName(tableName);
            		b.setDocId(name);
            		sendMsg(CsMgrEnum.MONGO_MGR, b);
            		return;
            	}
                ColumnBean bean = new ColumnBean(ColumnBean.ColOprType.UPDATE,treeNode.getFolder().getId());
                bean.setSchema(schemaName);
                bean.setTableName(tableName);
                bean.setColumnName(name);
                sendMsg(CsMgrEnum.COLUMN, bean);
                break;
            case PACKAGE_HEAD:
            case PACKAGE_BODY:
                AllDbObjType type = nodeType == TreeMrType.PACKAGE_HEAD ? AllDbObjType.PACK : AllDbObjType.PACKBODY;
                String packageName = treeNode.getFolder().getName();
                if (packageName.contains(".")) {
                    schemaName = packageName.split("\\.")[0];
                    packageName = packageName.split("\\.")[1];
                }
                PackageBean packageBean = new PackageBean(PackageBean.OprType.UPDATE,treeNode.getFolder().getId());
                packageBean.setName(packageName);
                packageBean.setSchema(schemaName);
                packageBean.setType(type);
                sendMsg(CsMgrEnum.PACKAGE, packageBean);
                break;
            case DBLINK:
                DbLinkBean dblink = new DbLinkBean(treeNode.getFolder().getId());
                dblink.setName(name);
                sendMsg(CsMgrEnum.DBLINK, dblink);
                break;
            case SYNONYM:
                SynonymBean synonymBean = new SynonymBean(SynonymBean.OprType.SHOW,treeNode.getFolder().getId());
                synonymBean.setSchema(schemaName);
                synonymBean.setName(name);
                synonymBean.setId(StringUtils.isBlank(treeNode.getObjectId()) ? "" : treeNode.getObjectId());
                sendMsg(CsMgrEnum.SYNONYM, synonymBean);
                break;
            case TYPE:
            case TYPE_BODY:
                AllDbObjType sessionEnum = TreeMrType.TYPE == nodeType ? AllDbObjType.TYPE : AllDbObjType.TYPEBODY;
                TypeBean typeBean = new TypeBean(TypeBean.OprType.UPDATE,treeNode.getFolder().getId());
                typeBean.setSchema(schemaName);
                typeBean.setName(name);
                typeBean.setType(sessionEnum.name());
                sendMsg(CsMgrEnum.TYPE, typeBean);
                break;
            case STRING:
            case HASH:
            case LIST:
            case SET:
            case ZSET:
            case JSON:
            	CsFolderBean root=CsTreeUtil.getTargetParentNode(treeNode, TreeMrType.ROOT_SCHEMA_OR_DB);
                RedisObjBean redisObjBean = new RedisObjBean(RedisObjBean.OprType.UPDATE,root.getId());
                redisObjBean.setSchema(root.getMrNode().getSchemaName());
                redisObjBean.setName(treeNode.getMrNode().getName());
                sendMsg(CsMgrEnum.REDIS_OBJ, redisObjBean);
                break;
            default:
        }
    }
}
