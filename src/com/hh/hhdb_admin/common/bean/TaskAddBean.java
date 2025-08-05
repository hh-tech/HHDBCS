package com.hh.hhdb_admin.common.bean;

import com.hh.frame.dbtask2.TaskType;
import com.hh.frame.json.JsonObject;

public class TaskAddBean extends BaseOpBean {
    private static final String TASKTYPE = "taskType";
    private static final String ISSCHEMA = "isSchema";
    private static final String ISDB = "isDb";
    private static final String FILEPATH = "filePath";
    private static final String QUERYSQL = "querySql";
    
    public TaskAddBean() {
        super(BaseOpEnum.ADD);
    }
    
    public TaskAddBean(JsonObject obj) {
        super(obj);
    }
    
    public TaskType getTaskType() {
        return TaskType.valueOf(jobj.getString(TASKTYPE));
    }
    
    public void setTaskType(TaskType taskType) {
        jobj.set(TASKTYPE, taskType.name());
    }
    
    public boolean getIsSchema() {
        return jobj.getBoolean(ISSCHEMA);
    }
    
    public void setIsSchema(boolean isSchema) {
        jobj.set(ISSCHEMA, isSchema);
    }
    
    public boolean getIsDb() {
        return jobj.getBoolean(ISDB);
    }
    
    public void setIsDb(boolean isDb) {
        jobj.set(ISDB, isDb);
    }
    
    public String getFilePath() {
        return jobj.getString(FILEPATH);
    }
    
    public void setFilePath(String filePath) {
        jobj.set(FILEPATH, filePath);
    }
    
    public String getQuerySql() {
        return jobj.getString(QUERYSQL);
    }
    
    public void setQuerySql(String querySql) {
        jobj.set(QUERYSQL, querySql);
    }
}
