package com.hh.hhdb_admin.mgr.tree;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.fa.FaBrandsEnum;
import com.hh.frame.swingui.view.fa.FaRegularEnum;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.fa.GoogleCharEnum;


/**
 * @author: Jiang
 * @date: 2020/9/17
 */

public class CsTreeUtil {


    /**
     * 获取当前节点的上层节点，直到匹配目标节点类型
     *
     * @param treeNode   当前节点
     * @param targetType 目标节点类型
     * @return 目标节点
     */
    public static CsFolderBean getTargetParentNode(CsFolderBean treeNode, TreeMrType targetType) {
        if (treeNode.getType() == targetType) {
            return treeNode;
        }
        CsFolderBean parentNode = (CsFolderBean) treeNode.getFolder();
        if (parentNode == null) {
            return null;
        }
        if (parentNode.getType() == targetType) {
            return parentNode;
        } else {
            return getTargetParentNode(parentNode, targetType);
        }
    }

    /**
     * 获取指定节点所在的模式，当不存在的模式时返回空串，避免json赋值错误
     *
     * @param treeNode 树结点
     * @return 模式名
     */
    public static String getSchemaName(CsFolderBean treeNode, DBTypeEnum typeEnum) {
        TreeMrType type;
        switch (typeEnum) {
            case hhdb:
            case pgsql:
            case kingbase:
            case sqlserver:
            case dm:
            case tdsql4pg:
            case gaussdb:
            case cache:
            case derby:
            case greenplum:
            case databricks:
            case hana:
                type = TreeMrType.SCHEMA;
                break;
            default:
                type = TreeMrType.ROOT_SCHEMA_OR_DB;
        }
        CsFolderBean ownerNode = getTargetParentNode(treeNode, type);
        return ownerNode == null ? "" : ownerNode.getName();
    }

    /**
     * 获取指定节点所在的表，当不存在时返回空串
     *
     * @param treeNode 树结点
     * @return 表名
     */
    public static String getTableName(CsFolderBean treeNode) {
        CsFolderBean tableNode = getTargetParentNode(treeNode, TreeMrType.TABLE);
        if (tableNode == null)
            tableNode = getTargetParentNode(treeNode, TreeMrType.PAR_TABLE);
        if (tableNode == null)
            tableNode = getTargetParentNode(treeNode, TreeMrType.SYS_TABLE);
        return tableNode == null ? "" : tableNode.getName();
    }

    /**
     * 获取指定节点所在的包，当不存在时返回空串
     *
     * @param treeNode 树结点
     * @return 包名
     */
    public static String getPackageName(CsFolderBean treeNode) {
        CsFolderBean tableNode = getTargetParentNode(treeNode, TreeMrType.PACKAGE);
        return tableNode == null ? "" : tableNode.getName();
    }

    /**
     * 获取指定节点所在的分区表子表或主表，当不存在时返回空串
     *
     * @param treeNode 树结点
     * @param isChild  是子表还是主表 true-子表；false-主表
     * @return 表名
     */
    public static String getPartitionTableName(CsFolderBean treeNode, boolean isChild) {
        TreeMrType nodeType = isChild ? TreeMrType.PARTITION_TABLE_CHILD : TreeMrType.PARTITION;
        CsFolderBean tableNode = getTargetParentNode(treeNode, nodeType);
        return tableNode == null ? "" : tableNode.getName();
    }

    /**
     * 获取树节点图标
     *
     * @return
     */
    public static Enum<?> getFontType(TreeMrNode node) {
        if ("error".equals(node.getIcon()))
            return FaSolidEnum.TRIANGLE_EXCLAMATION;
        if ("temp".equals(node.getIcon()))
            return FaSolidEnum.WINDOW_MAXIMIZE;
        if ("install".equals(node.getIcon()) && node.getType() == TreeMrType.EXTENSION)
            return FaSolidEnum.VIRUS_SLASH;
        
        Enum<?> fontType;
        switch (node.getType()) {
            case ROOT_SCHEMA_OR_DB:
            case DATABASE_GROUP:
            case DATABASE:
                fontType = FaSolidEnum.DATABASE;
                break;
            case SCHEMA_GROUP:
            case SCHEMA:
                fontType = FaSolidEnum.FOLDER;
                break;
            case EXTENSION_GROUP:
            case EXTENSION:
                fontType = FaSolidEnum.VIRUS;
                break;
            case USER_GROUP:
            case USER:
                fontType = FaSolidEnum.USERS;
                break;
            case ROLE_GROUP:
            case ROLE:
            case SUPER_ROLE_GROUP:
            case DATABASE_ROLE_GROUP:
            case AUTHORIZER_ROLE_GROUP:
            case DISABLE_ROLE_GROUP:
            case SUPER_ROLE:
            case DATABASE_ROLE:
            case AUTHORIZER_ROLE:
            case DISABLE_ROLE:
                fontType = FaSolidEnum.USER_GROUP;
                break;
            case TABLE_SPACE_GROUP:
            case TABLE_SPACE:
                fontType = FaSolidEnum.KAABA;
                break;
            case TABLE_GROUP:
            case TABLE:
            case RECYCLE_BIN_TABLE:
                fontType = FaSolidEnum.TABLE;
                break;
            case PAR_TABLE_GROUP:
            case PAR_TABLE:
                fontType = FaSolidEnum.CALENDAR_DAYS;
                break;
            case PARTITION_GROUP:
            case PARTITION:
                fontType = FaSolidEnum.CALENDAR_DAY;
                break;
            case VIEW_GROUP:
            case VIEW:
                fontType = FaSolidEnum.MONEY_BILL;
                break;
            case M_VIEW_GROUP:
            case M_VIEW:
                fontType = FaSolidEnum.MONEY_BILLS;
                break;
            case M_VIEW_LOG_GROUP:
            case M_VIEW_LOG:
                fontType = FaSolidEnum.RECEIPT;
                break;
            case FUNCTION_GROUP:
            case FUNCTION:
            case PACKAGE_FUNCTION_GROUP:
            case PACKAGE_FUNCTION:
                fontType = FaSolidEnum.F;
                break;
            case PROCEDURE_GROUP:
            case PROCEDURE:
            case PACKAGE_PROCEDURE_GROUP:
            case PACKAGE_PROCEDURE:
                fontType = FaSolidEnum.P;
                break;
            case SEQUENCE_GROUP:
            case SEQUENCE:
                fontType = FaSolidEnum.ARROW_DOWN_UP_ACROSS_LINE;
                break;
            case TYPE_GROUP:
            case TYPE:
            case PACKAGE_TYPE_GROUP:
            case PACKAGE_TYPE:
                fontType = FaSolidEnum.LAYER_GROUP;
                break;
            case TYPE_BODY_GROUP:
            case TYPE_BODY:
                fontType = FaSolidEnum.CIRCLE_NODES;
                break;
            case TRIGGER_GROUP:
            case TRIGGER:
                fontType = FaSolidEnum.STERLING_SIGN;
                break;
            case RULE_GROUP:
            case RULE:
                fontType = FaSolidEnum.RULER_VERTICAL;
                break;
            case PACKAGE_GROUP:
            case PACKAGE:
                fontType = FaSolidEnum.CUBE;
                break;
            case PACKAGE_HEAD:
                fontType = FaSolidEnum.COPY;
                break;
            case PACKAGE_BODY:
                fontType = FaSolidEnum.CLONE;
                break;
            case SYNONYM_GROUP:
            case SYNONYM:
                fontType = FaSolidEnum.AUDIO_DESCRIPTION;
                break;
            case DBLINK_GROUP:
            case DBLINK:
            case PACKAGE_HEAD_REFERENCES_GROUP:
            case PACKAGE_BODY_REFERENCES_GROUP:
                fontType = FaSolidEnum.LINK;
                break;
            case CLUSTER_GROUP:
            case CLUSTER:
                fontType = FaSolidEnum.CROSSHAIRS;
                break;
            case WINDOWS_GROUP:
            case WINDOW_GROUP:
                fontType = FaSolidEnum.WINDOW_RESTORE;
                break;
            case WINDOWS:
            case WINDOW:
                fontType = FaBrandsEnum.WINDOWS;
                break;
            case SCHEDULE_GROUP:
            case SCHEDULE:
                fontType = FaSolidEnum.WEIGHT_SCALE;
                break;
            case PROGRAM_GROUP:
            case PROGRAM:
                fontType = FaSolidEnum.CUBES;
                break;
            case CHAIN_GROUP:
            case CHAIN:
                fontType = FaSolidEnum.CODE_COMMIT;
                break;
            case JOB_GROUP:
            case JOB:
                fontType = FaRegularEnum.CLOCK;
                break;
            case JOB_CLASS_GROUP:
            case JOB_CLASS:
                fontType = FaSolidEnum.CALENDAR_CHECK;
                break;
            case DBMS_JOB_GROUP:
            case DBMS_JOB:
                fontType = FaSolidEnum.CALENDAR_WEEK;
                break;
            case RECYCLE_BIN_GROUP:
            case RECYCLE_BIN:
            case RECYCLE_BIN_INDEX:
                fontType = FaSolidEnum.TRASH_CAN;
                break;
            case COLUMN_GROUP:
            case COLUMN:
                fontType = FaSolidEnum.BARS;
                break;
            case INDEX_GROUP:
            case INDEX:
                fontType = FaSolidEnum.ARROWS_UP_DOWN;
                break;
            case CONSTRAINT_GROUP:
            case CHECK_KEY_GROUP:
            case PRIMARY_KEY_GROUP:
            case UNIQUE_KEY_GROUP:
            case FOREIGN_KEY_GROUP:
            case CHECK_KEY:
            case PRIMARY_KEY:
            case UNIQUE_KEY:
            case FOREIGN_KEY:
                fontType = FaSolidEnum.ARROWS_LEFT_RIGHT_TO_LINE;
                break;
            case STRING:
                fontType = FaSolidEnum.CLOSED_CAPTIONING;
                break;
            case HASH:
                fontType = FaSolidEnum.CIRCLE_H;
                break;
            case LIST:
                fontType = FaSolidEnum.ELLIPSIS_VERTICAL;
                break;
            case SET:
                fontType = FaSolidEnum.CENT_SIGN;
                break;
            case ZSET:
                fontType = FaSolidEnum.CEDI_SIGN;
                break;
            case JSON:
                fontType = FaRegularEnum.FILE_CODE;
                break;
            case UNSUPPORT:
                fontType = FaRegularEnum.CIRCLE_QUESTION;
                break;
            case MORE:
                fontType = FaSolidEnum.ARROWS_DOWN_TO_LINE;
                break;
            case SYS_TABLE_GROUP:
            case SYS_TABLE:
                fontType = GoogleCharEnum.CALL_TO_ACTION;
                break;
            default:
                fontType = FaRegularEnum.FOLDER;
        }
        return fontType;
    }

}
