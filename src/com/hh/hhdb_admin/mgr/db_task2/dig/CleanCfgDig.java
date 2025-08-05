package com.hh.hhdb_admin.mgr.db_task2.dig;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.dbtask2.bean.BaseTaskBean;
import com.hh.frame.dbtask2.schema.AbsSchemaClean;
import com.hh.frame.dbtask2.schema.SchemaBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.hhdb_admin.common.CsL;


/**
 * 数据库/模式清空
 */
public class CleanCfgDig extends AbsCfgDig {
    
    public CleanCfgDig(BaseTaskBean bean) {
        super(bean);
        setJdbcPanel();
        boolean isSchema = false;
        if (bean instanceof CleanBean) {
            isSchema = ((CleanBean) bean).isSchema;
            setSchema(((CleanBean) bean).schema);
        }
        setTaskName(bean.getName());
        dialog.setWindowTitle(isSchema ? LangMgr.sLang(CsL.CLEAN_SCHEMA) : LangMgr.sLang(CsL.CLEAN_DB));
        rootPanel.add(getToolBar());
        dialog.setRootPanel(rootPanel);
        setSize(rootPanel.getHeight());
    }
    
    @Override
    protected void setTask() throws Exception {
        CleanBean bean = new CleanBean(0, getTaskName(), getJdbc());
        task = new AbsSchemaClean(bean) {
            @Override
            protected void taskDone() {
                System.out.println("任务结束");
            }
        };
    }
    
    
    public static class CleanBean extends SchemaBean {
        private boolean isSchema = false;
        private String schema;
        
        public CleanBean(int id, String name, JdbcBean jdbc) {
            super(id, name, jdbc);
        }
    
        public void setisSchema(boolean isSchema) {
            this.isSchema = isSchema;
        }
    
    
        public void setSchema(String schema) {
            this.schema = schema;
        }
    }
}
