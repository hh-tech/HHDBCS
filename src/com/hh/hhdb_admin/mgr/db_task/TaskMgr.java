package com.hh.hhdb_admin.mgr.db_task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.dbtask.TaskType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.db_task.dig.AbsCfgDig;
import com.hh.hhdb_admin.mgr.db_task.dig.ExpQueryCfgDig;
import com.hh.hhdb_admin.mgr.db_task.dig.SqlFileCfgDig;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;

/**
 * @author: Jiang
 * @date: 2021/1/21
 */

public class TaskMgr extends AbsGuiMgr {

    public static final String CMD_SHOW_TASK_PANEL = "showTaskPanel";
    public static final String CMD_ADD_TASK = "addTask";
    public static final String PARAM_TASK_TYPE = "taskType";
    public static final String PARAM_AUTO_START = "autoStart";
    private String objId;

    @Override
    public void init(JsonObject jObj) {
    	 try {
             LangMgr2.loadMerge(TaskMgr.class);
         }catch (Exception e){
             e.printStackTrace();
         }
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.DB_TASK;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        String cmd = GuiJsonUtil.toStrCmd(msg);
        if (cmd.equals(CMD_SHOW_TASK_PANEL)) {
            showTaskPanel();
        } else if (cmd.equals(CMD_ADD_TASK)) {
            String typeStr = msg.getString(PARAM_TASK_TYPE);
            if (StringUtils.isBlank(typeStr)) {
                return;
            }
            TaskType type = TaskType.valueOf(typeStr);
            addTask(type, msg);
        }
    }

    private void addTask(TaskType type, JsonObject msg) throws Exception {
        LoginBean loginBean = StartUtil.getLoginBean();
        AbsCfgDig dig = TaskComp.getConfigDig(loginBean.getJdbc(), type, getConfig(type, msg), false);
        if (dig == null) {
            return;
        }
        dig.show();
    }

    private void showTaskPanel() throws Exception {
        if (objId == null) {
            LoginBean loginBean = StartUtil.getLoginBean();
            LastPanel taskPanel = TaskComp.getLastPanel(loginBean.getJdbc());
            objId = StartUtil.eng.push2SharedMap(taskPanel);
        }
        TaskComp.setJdbc(StartUtil.getLoginBean().getJdbc());
        StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
                .add(MainFrameMgr.PARAM_ID, objId)
                .add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.DB_TASK.name())
                .add(MainFrameMgr.PARAM_TITLE, TaskMgr.getLang("taskMana"))
        );
        new TaskRefreshThread().start();
    }

    private Map<String, String> getConfig(TaskType type, JsonObject msg) {
        switch (type) {
            case BACKUP:
            case RESTORE:
                return new HashMap<String, String>() {
                    private static final long serialVersionUID = 3236644850951371497L;

                    {
                        put("isDb", msg.getString("isDb"));
                        put("isBackup", String.valueOf(type == TaskType.BACKUP));
                        put("schema", msg.getString("schema"));
                    }
                };
            case CLEAN:
                return new HashMap<String, String>() {
                    private static final long serialVersionUID = -3388444236757241425L;

                    {
                        put("nodeName", msg.getString("nodeName"));
                        put("isSchema", msg.getString("isSchema"));
                        put("schema", msg.getString("schema"));
                    }
                };
            case GEN_TAB_DATA:
                return new HashMap<String, String>() {
                    private static final long serialVersionUID = -3862643788271906143L;

                    {
                        put(StartUtil.PARAM_SCHEMA, msg.getString(StartUtil.PARAM_SCHEMA));
                        put(StartUtil.PARAM_TABLE, msg.getString(StartUtil.PARAM_TABLE));
                    }
                };
            case EXP_QUERY_AS_INSERT:
            case EXP_QUERY_AS_XLS:
                return new HashMap<String, String>() {
                    private static final long serialVersionUID = -3862643788271906143L;

                    {
                        put(StartUtil.PARAM_SCHEMA, msg.getString(StartUtil.PARAM_SCHEMA));
                        put(ExpQueryCfgDig.SQL, msg.getString(ExpQueryCfgDig.SQL));
                    }
                };
            case SQL_FILE:
                return new HashMap<String, String>() {
                    private static final long serialVersionUID = -3862643788271906143L;

                    {
                        put(StartUtil.PARAM_SCHEMA, msg.getString(StartUtil.PARAM_SCHEMA));
                        put(SqlFileCfgDig.ENCODING_TEXT_ID, msg.getString(SqlFileCfgDig.ENCODING_TEXT_ID));
                        put(SqlFileCfgDig.SQL_FILE_TEXT_ID, msg.getString(SqlFileCfgDig.SQL_FILE_TEXT_ID));
                    }
                };
            default:
                return new HashMap<>();
        }
    }

    protected static String getLang(String key) {
        return LangMgr2.getValue(TaskMgr.class.getName(), key);
    }

    @Override
    public JsonObject doCall(JsonObject msg) throws Exception {
        return null;
    }
}
