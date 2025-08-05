package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbobj3.AllDbObjType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.mongo.obj.MongoColl;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.column.ColumnBean;
import com.hh.hhdb_admin.mgr.constraint3.ConstraintBean;
import com.hh.hhdb_admin.mgr.database2.DataBaseBean;
import com.hh.hhdb_admin.mgr.dblink2.DbLinkBean;
import com.hh.hhdb_admin.mgr.function2.FunctionBean;
import com.hh.hhdb_admin.mgr.index2.IndexBean;
import com.hh.hhdb_admin.mgr.mongo.MongoDocAddUpdateBean;
import com.hh.hhdb_admin.mgr.pack.PackageBean;
import com.hh.hhdb_admin.mgr.partition.PartitionBean;
import com.hh.hhdb_admin.mgr.redis_obj.RedisObjBean;
import com.hh.hhdb_admin.mgr.rule.RuleBean;
import com.hh.hhdb_admin.mgr.schema2.SchemaBean;
import com.hh.hhdb_admin.mgr.sequence2.SeqBean;
import com.hh.hhdb_admin.mgr.synonym2.SynonymBean;
import com.hh.hhdb_admin.mgr.table2.TableBean;
import com.hh.hhdb_admin.mgr.table_space2.TableSpaceBean;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;
import com.hh.hhdb_admin.mgr.trigger2.TriggerBean;
import com.hh.hhdb_admin.mgr.type.TypeBean;
import com.hh.hhdb_admin.mgr.usr2.UsrBean;
import com.hh.hhdb_admin.mgr.view2.ViewBean;
import org.apache.commons.lang3.StringUtils;

/**
 * 处理新增事件
 *
 * @author: Jiang
 * @date: 2020/9/15
 */

public class AddHandler extends AbsHandler {

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        String schemaName = getSchemaName();
        String tableName = getTableName();
        TreeMrType nodeType = treeNode.getType();
        DBTypeEnum dbtype=DriverUtil.getDbType(jdbc);
        
        if (dbtype == DBTypeEnum.mongodb &&  nodeType==TreeMrType.TABLE_GROUP) {
            String newName = PopDlgUtil.nameInput(LangMgr.lang(Word4.COLLECTION_NAME), "");
            if (!StringUtils.isBlank(newName)) {
                MongoColl mongoColl = new MongoColl(CsVarUtil.conn,schemaName);
                mongoColl.newCollection(newName);
                PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.SUCCESSFULLY_ADDED));
                CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(treeNode.getId()));
            }
            return;
        }
        
        switch (nodeType) {
            case ROOT:
            case DATABASE_GROUP:
                sendMsg(CsMgrEnum.DATABASE, new DataBaseBean(DataBaseBean.OprType.ADD,treeNode.getId()));
                break;
            case ROOT_SCHEMA_OR_DB:
                if (dbtype == DBTypeEnum.redis) {
                    RedisObjBean redisObjBean = new RedisObjBean(RedisObjBean.OprType.ADD,treeNode.getId());
                    redisObjBean.setSchema(treeNode.getMrNode().getSchemaName());
                    sendMsg(CsMgrEnum.REDIS_OBJ, redisObjBean);
                } 
                break;
            case TABLE_GROUP:
            case TABLE:
            case PAR_TABLE_GROUP:
                TableBean tableBean = new TableBean(TableBean.OprType.ADD,treeNode.getId());
                tableBean.setSchema(schemaName);
                sendMsg(CsMgrEnum.TABLE, tableBean);
                break;
            case SEQUENCE_GROUP:
            case SEQUENCE:
                SeqBean sequenceBean = new SeqBean(SeqBean.OprType.ADD,treeNode.getId());
                sequenceBean.setSchema(schemaName);
                sendMsg(CsMgrEnum.SEQUENCE, sequenceBean);
                break;
            case VIEW_GROUP:
            case VIEW:
                ViewBean viewBean = new ViewBean(ViewBean.OprType.ADD,treeNode.getId());
                viewBean.setSchema(schemaName);
                sendMsg(CsMgrEnum.VIEW, viewBean);
                break;
            case FUNCTION_GROUP:
            case PROCEDURE_GROUP:
                FunctionBean functionBean = new FunctionBean(FunctionBean.OprType.ADD,treeNode.getId());
                functionBean.setSchema(schemaName);
                functionBean.setNodeType(nodeType == TreeMrType.FUNCTION_GROUP ? TreeMrType.FUNCTION : TreeMrType.PROCEDURE);
                sendMsg(CsMgrEnum.FUNCTION, functionBean);
                break;
            case M_VIEW_GROUP:
            case M_VIEW:
                ViewBean mviewBean = new ViewBean(ViewBean.OprType.ADD_MVIEW,treeNode.getId());
                mviewBean.setSchema(schemaName);
                sendMsg(CsMgrEnum.VIEW, mviewBean);
                break;
            case SCHEMA_GROUP:
                sendMsg(CsMgrEnum.SCHEMA, new SchemaBean(SchemaBean.OprType.ADD,treeNode.getId()));
                break;
            case TABLE_SPACE_GROUP:
                sendMsg(CsMgrEnum.TABLE_SPACE, new TableSpaceBean(TableSpaceBean.OprType.SHOW,treeNode.getId()));
                break;
            case TABLE_SPACE:
                break;
            case USER_GROUP:
                sendMsg(CsMgrEnum.USR, new UsrBean(UsrBean.OprType.ADD_USER,treeNode.getId()));
                break;
            case ROLE_GROUP:
                sendMsg(CsMgrEnum.USR, new UsrBean(UsrBean.OprType.ADD_ROLE,treeNode.getId()));
                break;
            case RULE_GROUP:
                RuleBean ruleBean = new RuleBean(RuleBean.OprType.ADD,treeNode.getId());
                ruleBean.setSchema(schemaName);
                ruleBean.setTable(tableName);
                sendMsg(CsMgrEnum.RULE, ruleBean);
                break;
            case COLUMN_GROUP:
                if (CsVarUtil.getDbtype() == DBTypeEnum.mongodb) {
            		MongoDocAddUpdateBean b=new MongoDocAddUpdateBean(treeNode.getId());
            		b.setDbName(schemaName);
            		b.setCollName(tableName);
            		sendMsg(CsMgrEnum.MONGO_MGR, b);
                } else {
                    ColumnBean bean = new ColumnBean(ColumnBean.ColOprType.ADD,treeNode.getId());
                    bean.setSchema(schemaName);
                    bean.setTableName(tableName);
                    sendMsg(CsMgrEnum.COLUMN, bean);
                }
                break;
            case INDEX_GROUP:
                IndexBean indexBean = new IndexBean(IndexBean.OprType.SHOW,treeNode.getId());
                indexBean.setSchema(schemaName);
                indexBean.setTableName(tableName);
                sendMsg(CsMgrEnum.INDEX, indexBean);
                break;
            case TRIGGER_GROUP:
                TriggerBean triggerBean = new TriggerBean(TriggerBean.OprType.ADD,treeNode.getId());
                triggerBean.setSchema(schemaName);
                triggerBean.setTableName(tableName);
                sendMsg(CsMgrEnum.TRIGGER, triggerBean);
                break;
            case FOREIGN_KEY_GROUP:
                ConstraintBean fkbean = new ConstraintBean(ConstraintBean.OprType.FK,treeNode.getId());
                fkbean.setSchema(schemaName);
                fkbean.setTableName(tableName);
                fkbean.setConstType(nodeType);
                sendMsg(CsMgrEnum.CONSTRAINT, fkbean);
                break;
            case UNIQUE_KEY_GROUP:
                ConstraintBean ukbean = new ConstraintBean(ConstraintBean.OprType.UK,treeNode.getId());
                ukbean.setSchema(schemaName);
                ukbean.setTableName(tableName);
                ukbean.setConstType(nodeType);
                sendMsg(CsMgrEnum.CONSTRAINT, ukbean);
                break;
            case CHECK_KEY_GROUP:
                ConstraintBean ckbean = new ConstraintBean(ConstraintBean.OprType.CK,treeNode.getId());
                ckbean.setSchema(schemaName);
                ckbean.setTableName(tableName);
                ckbean.setConstType(nodeType);
                sendMsg(CsMgrEnum.CONSTRAINT, ckbean);
                break;
            case PRIMARY_KEY_GROUP:
                ConstraintBean pkbean = new ConstraintBean(ConstraintBean.OprType.PK,treeNode.getId());
                pkbean.setSchema(schemaName);
                pkbean.setTableName(tableName);
                pkbean.setConstType(nodeType);
                sendMsg(CsMgrEnum.CONSTRAINT, pkbean);
                break;
            case PACKAGE_GROUP:
                PackageBean packageBean = new PackageBean(PackageBean.OprType.ADD,treeNode.getId());
                packageBean.setName(tableName);
                packageBean.setSchema(schemaName);
                sendMsg(CsMgrEnum.PACKAGE, packageBean);
                break;
            case DBLINK_GROUP:
                sendMsg(CsMgrEnum.DBLINK, new DbLinkBean(treeNode.getId()));
                break;
            case SYNONYM_GROUP:
                SynonymBean synonymBean = new SynonymBean(SynonymBean.OprType.SHOW,treeNode.getId());
                synonymBean.setSchema(schemaName);
                sendMsg(CsMgrEnum.SYNONYM, synonymBean);
                break;
            case TYPE_GROUP:
            case TYPE_BODY_GROUP:
                AllDbObjType sessionEnum = TreeMrType.TYPE_GROUP == nodeType
                        ? AllDbObjType.TYPE : AllDbObjType.TYPEBODY;
                TypeBean typeBean = new TypeBean(TypeBean.OprType.ADD,treeNode.getId());
                typeBean.setSchema(schemaName);
                typeBean.setType(sessionEnum.name());
                sendMsg(CsMgrEnum.TYPE, typeBean);
                break;
            case PARTITION_GROUP:
                PartitionBean partitionBean = new PartitionBean(PartitionBean.OprType.ADD, treeNode.getId());
                partitionBean.setSchema(schemaName);
                partitionBean.setTableName(tableName);
                partitionBean.setName(treeNode.getName());
                sendMsg(CsMgrEnum.PARTITION, partitionBean);
                break;
            default:
        }
    }

}
