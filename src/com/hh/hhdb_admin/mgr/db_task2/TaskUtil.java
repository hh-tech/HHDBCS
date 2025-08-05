package com.hh.hhdb_admin.mgr.db_task2;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.dbtask2.TaskType;
import com.hh.frame.dbtask2.bean.BaseTaskBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.db_task2.dig.*;

import java.util.*;

public class TaskUtil {
    public static List<String> getDbTypes() {
        List<String> dbTypes = new ArrayList<>();
        if (CsVarUtil.getDbtype() == DBTypeEnum.redis) {
            dbTypes.add(DBTypeEnum.redis.name());
        } else {
            dbTypes.add(DBTypeEnum.hhdb.name());
            dbTypes.add(DBTypeEnum.pgsql.name());
            dbTypes.add(DBTypeEnum.oracle.name());
            dbTypes.add(DBTypeEnum.mysql.name());
            dbTypes.add(DBTypeEnum.mariadb.name());
            dbTypes.add(DBTypeEnum.db2.name());
            dbTypes.add(DBTypeEnum.sqlserver.name());
            dbTypes.add(DBTypeEnum.dm.name());
            dbTypes.add(DBTypeEnum.kingbase.name());
            dbTypes.add(DBTypeEnum.tdsql4mysql.name());
            dbTypes.add(DBTypeEnum.tdsql4pg.name());
            dbTypes.add(DBTypeEnum.gaussdb.name());
            dbTypes.add(DBTypeEnum.oceanbase.name());
            dbTypes.add(DBTypeEnum.hive.name());
            dbTypes.add(DBTypeEnum.informix.name());
            dbTypes.add(DBTypeEnum.gbase.name());
            dbTypes.add(DBTypeEnum.gbase8a.name());
        }
        return dbTypes;
    }
    
    public static Map<String, String> getTaskMap() {
        Map<String, String> taskMap = new LinkedHashMap<>();
        taskMap.put(TaskType.SQL_FILE.name(), LangMgr.sLang(CsL.ADD_SQL_FILE));
        if (CsVarUtil.getDbtype() == DBTypeEnum.redis) {
            taskMap.put(TaskType.REDIS_EXP.name(), LangMgr.sLang(CsL.OBJECT_EXPORT));
        } else {
            taskMap.put(TaskType.EXCEL_LOB_IMP.name(), LangMgr.sLang(CsL.IMPORT_LOB_DATA));
            taskMap.put(TaskType.LOB_EXP.name(), LangMgr.sLang(CsL.EXPORT_LOB_DATA));
            taskMap.put(TaskType.SCHEMA_CLEAN.name(), LangMgr.sLang(CsL.DB_SCHEMA_CLEAR));
            taskMap.put(TaskType.SCHEMA_EXP.name(), LangMgr.sLang(CsL.DB_SCHEMA_BACKUP));
            taskMap.put(TaskType.SCHEMA_IMP.name(), LangMgr.sLang(CsL.DB_SCHEMA_RESTORE));
            taskMap.put(TaskType.QUERY_EXP.name(), LangMgr.sLang(CsL.QUERY_DATA_EXPORT));
            taskMap.put(TaskType.DDL_EXP.name(), LangMgr.sLang(CsL.OBJECT_EXPORT));
            taskMap.put(TaskType.GEN_TAB_DATA.name(), LangMgr.sLang(CsL.TABLE_DATA_GENERATE));
        }
        return taskMap;
    }
    
    
    public static String generateTaskName(TaskType type) {
        return type.name() + "_" + new Random().nextInt(1000);
    }
    
    /**
     * 根据任务类型获取任务配置面板
     *
     * @param taskType 任务类型
     * @param bean     任务配置对象
     * @param editable jdbc是否可修改
     * @return 任务配置面板
     */
    public static AbsCfgDig getConfigDig(TaskType taskType, BaseTaskBean bean, boolean editable) {
        AbsCfgDig absCfgDig;
        switch (taskType) {
            case SQL_FILE:
                absCfgDig = new SqlFileCfgDig(bean);
                break;
            case EXCEL_LOB_IMP:
                absCfgDig = new ExcelImpLobCfgDig(bean);
                break;
            case LOB_EXP:
                absCfgDig = new ExpLobCfgDig(bean);
                break;
            case SCHEMA_CLEAN:
                absCfgDig = new CleanCfgDig(bean);
                break;
            case SCHEMA_EXP:
            case SCHEMA_IMP:
                absCfgDig = new BackupRestoreCfgDig(bean,taskType == TaskType.SCHEMA_EXP);
                break;
            case QUERY_EXP:
                absCfgDig = new ExpQueryCfgDig(bean);
                break;
            case DDL_EXP:
                absCfgDig = new DdlExpCfgDig(bean);
                editable = false;
                break;
            case REDIS_EXP:
                absCfgDig = new ExpRedisObjCfgDig(bean);
                break;
            case GEN_TAB_DATA:
                absCfgDig = new GenTabDataCfgDig(bean);
                break;
            default:
                return null;
        }
        absCfgDig.setJdbcEditable(editable);
        return absCfgDig;
    }
}
