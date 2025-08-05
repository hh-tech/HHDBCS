package com.hh.hhdb_admin.mgr.db_task2;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.dbtask2.TaskType;
import com.hh.frame.dbtask2.bean.BaseTaskBean;
import com.hh.frame.dbtask2.gen_tab_data.GenTabDataBean;
import com.hh.frame.dbtask2.query_exp.QueryExpBean;
import com.hh.frame.dbtask2.redis_exp.RedisExpBean;
import com.hh.frame.dbtask2.sql_files_imp.FileImpBean;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.TaskAddBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.db_task2.dig.AbsCfgDig;
import com.hh.hhdb_admin.mgr.db_task2.dig.BackupRestoreCfgDig;
import com.hh.hhdb_admin.mgr.db_task2.dig.CleanCfgDig;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;

import java.io.File;

public class TaskMgr extends AbsGuiMgr {
    public static SharePaneObj sharePaneObj;

    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.DB_TASK;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
        switch (csGuiBean.getOpType()) {
            case INIT:
                showTaskPanel();
                break;
            case ADD:
                addTask(new TaskAddBean(bj.getJobj()));
                break;
            default:
                break;
        }
        return null;
    }
    
    private void addTask(TaskAddBean taskBean) throws Exception {
    	JdbcBean jdbcBean= CsVarUtil.jdbcCloneWithSsh();
        jdbcBean.setSchema(taskBean.getSchema());
        BaseTaskBean bean = null;
        TaskType type = taskBean.getTaskType();
        if (type == TaskType.SQL_FILE) {
            bean = new FileImpBean(0,TaskUtil.generateTaskName(type),jdbcBean);
            ((FileImpBean)bean).setFilePathList(taskBean.getFilePath());
        } else if (type == TaskType.SCHEMA_CLEAN) {
            bean = new CleanCfgDig.CleanBean(0,TaskUtil.generateTaskName(type),jdbcBean);
            ((CleanCfgDig.CleanBean)bean).setisSchema(taskBean.getIsSchema());
            ((CleanCfgDig.CleanBean)bean).setSchema(taskBean.getSchema());
        } else if (type == TaskType.SCHEMA_EXP || type == TaskType.SCHEMA_IMP) {
            bean = new BackupRestoreCfgDig.BackupRestoreBean(0,TaskUtil.generateTaskName(type),jdbcBean);
            ((BackupRestoreCfgDig.BackupRestoreBean)bean).setisDb(taskBean.getIsDb());
            ((BackupRestoreCfgDig.BackupRestoreBean)bean).setSchema(taskBean.getSchema());
        } else if (type == TaskType.QUERY_EXP) {
            if (!jdbcBean.getSchema().equals(taskBean.getSchema())) jdbcBean.setSchema(taskBean.getSchema());
            bean = new QueryExpBean(0,TaskUtil.generateTaskName(type),jdbcBean);
            ((QueryExpBean)bean).setQuerySql(taskBean.getQuerySql());
        } else if (type == TaskType.REDIS_EXP) {
            bean = new RedisExpBean(0,TaskUtil.generateTaskName(type),jdbcBean,new File(""));
        } else if (type == TaskType.GEN_TAB_DATA) {
            bean = new GenTabDataBean(0,TaskUtil.generateTaskName(type),jdbcBean);
            ((GenTabDataBean)bean).setSCHEMA_NAME(taskBean.getSchema());
            ((GenTabDataBean)bean).setTABLE_NAME(taskBean.getName());
        }
        
        AbsCfgDig dig = TaskUtil.getConfigDig(type,bean, false);
        if (dig == null) {
            return;
        }
        dig.show();
    }

    private void showTaskPanel() throws Exception {
        JdbcBean jdbcBean = CsVarUtil.jdbcCloneWithSsh();
        if (sharePaneObj == null) {
            sharePaneObj = new SharePaneObj();
            sharePaneObj.setId(RandomUtil.getUUID());
            sharePaneObj.setTitle(LangMgr.lang(Word4.TASK_MANAGE));
            sharePaneObj.setCsMgrEnum(CsMgrEnum.DB_TASK);
            sharePaneObj.setFixTab(false);
            sharePaneObj.setObj(TaskComp.getLastPanel(jdbcBean, CsVarUtil.parentFrame));
        }
        CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, sharePaneObj);
    }
}
