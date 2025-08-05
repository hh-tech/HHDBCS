package com.hh.hhdb_admin.mgr.sql_book;

import com.hh.frame.dbtask2.TaskType;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.SqlBookBean;
import com.hh.hhdb_admin.common.bean.TaskAddBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 文件管理
 */
public class SqlBookMgr extends AbsGuiMgr {
    public static final int maxM = 10;
    public static final int maxFileSize = maxM * 1024 * 1024;
    
    private SqlBookComp sqlBookComp;

    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.SQL_BOOK;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
        if (sqlBookComp == null || CsVarUtil.CONF.getLang() != sqlBookComp.getCurLanguage()) {
            sqlBookComp = getSqlBookComp();
        }
        switch (csGuiBean.getOpType()) {
            case SHOW:
                sqlBookComp.show();
                break;
            case GET_PATH:
                return new SqlBookBean(sqlBookComp.getCurrentDir());
            default:
                break;
        }
        return null;
    }

    /**
     * sqlbookcomp
     *
     */
    private SqlBookComp getSqlBookComp() throws Exception {
        return new SqlBookComp(getSqlBookPath()) {
            @Override
            protected void openTask(String path) throws Exception {
                TaskAddBean bean = new TaskAddBean();
                bean.setTaskType(TaskType.SQL_FILE);
                bean.setFilePath(path);
                CsVarUtil.eng2.doCall(CsMgrEnum.DB_TASK, bean);
            }
        };
    }

    /**
     * 获取sql_book路径
     */
    private String getSqlBookPath() throws IOException {
        File f = new File(CsVarUtil.getEtcFile().getAbsolutePath(), "sql_book");
        if (!f.exists()) {
            FileUtils.forceMkdir(f);
        }
        return f.getAbsolutePath();
    }

}
