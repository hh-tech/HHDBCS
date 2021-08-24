package com.hh.hhdb_admin.mgr.sql_book;

import com.hh.frame.dbtask.TaskType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.engine.GuiMsgType;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.db_task.TaskMgr;
import com.hh.hhdb_admin.mgr.db_task.dig.SqlFileCfgDig;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.query.QueryMgr;
import com.hh.hhdb_admin.mgr.vm_editor.VmMgr;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class SqlBookMgr extends AbsGuiMgr {
    public static final String CMD_SHOW_SQL_BOOK = "show_book";
    private static final String BOOK_PATH = "sql_book";


    public enum ObjType {
    	SHARE_PATH
    }

    private SqlBookComp sqlBookComp;

    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.SQL_BOOK;
    }

    @Override
    public String getHelp() {
        return GuiJsonUtil.genCmdHelp(CMD_SHOW_SQL_BOOK, "打开SQL宝典", GuiMsgType.RECE);
    }


    @Override
    public void doPush(JsonObject msg) throws Exception {
        String cmd = GuiJsonUtil.toStrCmd(msg);
        if (CMD_SHOW_SQL_BOOK.equals(cmd)) {
            if (sqlBookComp == null || LangMgr.getDefaultLang() != sqlBookComp.getCurLanguage()) {
                sqlBookComp = getSqlBookComp();
            }
            sqlBookComp.show();
        }else {
            unknowMsg(msg.toPrettyString());
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) {
        try {
            if (GuiJsonUtil.isSharedId(msg)) {

            	if(sqlBookComp==null) {
            		return GuiJsonUtil.toJsonSharedId(getSqlBookPath());
            	}else {
            		return GuiJsonUtil.toJsonSharedId(sqlBookComp.getCurrentDir());
            	}

            }
        } catch (Exception e) {
            return GuiJsonUtil.toError(e);
        }
        return GuiJsonUtil.toError("未知命令:" + msg);
    }
    /**
     * 获取连接
     */
    private LoginBean getLoginBean() throws Exception {
        JsonObject sharedIdObj = StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(LoginMgr.ObjType.LOGIN_BEAN));
        return (LoginBean) StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(sharedIdObj));
    }


    /**
     * sqlbookcomp
     *
     */
    private SqlBookComp getSqlBookComp() throws Exception {
        return new SqlBookComp(getSqlBookPath(), StartUtil.getMainDialog()) {
            @Override
            protected void openTask(String path) throws Exception {
                LoginBean bean = getLoginBean();
                StartUtil.eng.doPush(CsMgrEnum.DB_TASK, GuiJsonUtil.toJsonCmd(TaskMgr.CMD_ADD_TASK)
                        .add("schema", bean.getJdbc().getSchema())
                        .add(TaskMgr.PARAM_TASK_TYPE, TaskType.SQL_FILE.name())
                        .add(SqlFileCfgDig.SQL_FILE_TEXT_ID, path)
                        .add(TaskMgr.PARAM_AUTO_START, true)
                );

            }

            @Override
            protected void openQuery(String text) {
            	try {
            		StartUtil.eng.doPush(CsMgrEnum.QUERY, GuiJsonUtil.toJsonCmd(QueryMgr.CMD_SHOW_QUERY).add("text", text));
				} catch (Exception e) {
            	    e.printStackTrace();
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
				}

            }

            @Override
            protected void openVM(String text) {
                try {
                    StartUtil.eng.doPush(CsMgrEnum.VM, GuiJsonUtil.toJsonCmd(VmMgr.CMD_SHOW_VM).add("text", text));
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
                }

            }
        };
    }

    /**
     * 获取sql_book路径
     */
    private String getSqlBookPath() throws IOException {
        File f = new File(StartUtil.getEtcFile().getAbsolutePath(), BOOK_PATH);
        if (!f.exists()) {
            FileUtils.forceMkdir(f);
        }
        return f.getAbsolutePath();
    }

}
