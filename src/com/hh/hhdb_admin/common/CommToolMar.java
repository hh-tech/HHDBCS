package com.hh.hhdb_admin.common;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.ViewType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.cmd.CmdMgr;
import com.hh.hhdb_admin.mgr.db_task.TaskMgr;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import com.hh.hhdb_admin.mgr.gis.GisMgr;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.menubar.MenubarMgr;
import com.hh.hhdb_admin.mgr.monitor.MonitorMgr;
import com.hh.hhdb_admin.mgr.obj_query.ObjQueryMgr;
import com.hh.hhdb_admin.mgr.query.QueryMgr;
import com.hh.hhdb_admin.mgr.quick_query.QuickQueryMgr;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookMgr;
import com.hh.hhdb_admin.mgr.vm_editor.VmMgr;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 工具栏共用方法
 *
 * @author hyz
 */
public class CommToolMar {
    private static final String SWITCH_CONN = "switch_conn";
    private static final String QUERY = "query";
    private static final String SQL_BOOK = "sql_book";
    private static final String GIS = "gis";
    private static final String DB_MONITOR = "db_monitor";
    private static final String SYS_MONITOR = "sys_monitor";
    //    public static final String SYS_CONFIG = "sys_config";
    private static final String DB_TASK = "db_task";
    private static final String QUICK_CMD = "quick_cmd";
    private static final String VM = "vm";
    private static final String DEBUG = "debug";
    private static final String SQL_CONVERSION = "sql_conversion";
    private static final String OBJ_QUERY = "obj_query";
    private static final String CMD = "cmd";

    static {
        try {
            LangMgr2.loadMerge(CommToolMar.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> genToolInfo() {
        List<String> toolInfolist = new ArrayList<>();
        LoginBean lb;
        DBTypeEnum dbtype = DBTypeEnum.hhdb;
        try {
            lb = StartUtil.getLoginBean();
            dbtype = DriverUtil.getDbType(lb.getJdbc());
        } catch (Exception e) {
            lb = new LoginBean();
            e.printStackTrace();
        }
        toolInfolist.add(SWITCH_CONN);
        toolInfolist.add(QUERY);
        toolInfolist.add(CMD);
        toolInfolist.add(QUICK_CMD);
        toolInfolist.add(VM);
        toolInfolist.add(SQL_BOOK);
        toolInfolist.add(DB_TASK);
        toolInfolist.add(DB_MONITOR);
        if (lb.getViewType() == ViewType.DBA) {
            toolInfolist.add(SYS_MONITOR);
        }
        if (lb.getViewType() == ViewType.USER && dbtype == DBTypeEnum.oracle) {
            toolInfolist.add(DEBUG);
        }
        if (lb.getViewType() == ViewType.USER &&
                (dbtype == DBTypeEnum.hhdb || dbtype == DBTypeEnum.pgsql || dbtype == DBTypeEnum.mysql) ) {
            toolInfolist.add(SQL_CONVERSION);
            toolInfolist.add(GIS);
        }
        toolInfolist.add(OBJ_QUERY);
        return toolInfolist;
    }

    public String genTitle(String key) {
        return LangMgr2.getValue(CommToolMar.class.getName(), key);
    }

    public void onBtnClick(String item) {
        switch (item) {
            case SWITCH_CONN:
                sendMsg(CsMgrEnum.LOGIN, GuiJsonUtil.toJsonCmd(LoginMgr.CMD_SHOW_SWITCH));
                break;
            case QUERY:
                sendMsg(CsMgrEnum.QUERY, GuiJsonUtil.toJsonCmd(QueryMgr.CMD_SHOW_QUERY));
                break;
            case SQL_BOOK:
                sendMsg(CsMgrEnum.SQL_BOOK, GuiJsonUtil.toJsonCmd(SqlBookMgr.CMD_SHOW_SQL_BOOK));
                break;
//            case SYS_CONFIG:
//                break;
            case DB_MONITOR:
                sendMsg(CsMgrEnum.MONITOR,
                        GuiJsonUtil.toJsonCmd(MonitorMgr.CMD_SHOW_DB_MONITOR).add(MonitorMgr.IS_TEST, false));
                break;
            case SYS_MONITOR:
                sendMsg(CsMgrEnum.MONITOR,
                        GuiJsonUtil.toJsonCmd(MonitorMgr.CMD_SHOW_SYSTEM_MONITOR).add(MonitorMgr.IS_TEST, false));
                break;
            case DB_TASK:
                sendMsg(CsMgrEnum.DB_TASK, GuiJsonUtil.toJsonCmd(TaskMgr.CMD_SHOW_TASK_PANEL));
                break;
            case QUICK_CMD:
                sendMsg(CsMgrEnum.QUICK_CMD, GuiJsonUtil.toJsonCmd(QuickQueryMgr.CMD_SHOW_QUICK_QUERY));
                break;
            case VM:
                sendMsg(CsMgrEnum.VM, GuiJsonUtil.toJsonCmd(VmMgr.CMD_SHOW_VM));
                break;
            case GIS:
                sendMsg(CsMgrEnum.GIS, GuiJsonUtil.toJsonCmd(GisMgr.CMD_SHOW_GIS));
                break;
            case DEBUG:
                sendMsg(CsMgrEnum.FUNCTION, GuiJsonUtil.toJsonCmd(FunctionMgr.DEBUG));
                break;
            case SQL_CONVERSION:
                sendMsg(CsMgrEnum.MENUBAR, GuiJsonUtil.toJsonCmd(MenubarMgr.CMD_SHOW_CONVERSION));
                break;
            case OBJ_QUERY:
                sendMsg(CsMgrEnum.OBJ_QUERY, GuiJsonUtil.toJsonCmd(ObjQueryMgr.SHOW_QUERY));
                break;
            case CMD:
                sendMsg(CsMgrEnum.CMD, GuiJsonUtil.toJsonCmd(CmdMgr.CMD_SHOW_CMD));
                break;
            default:
        }
    }

    private void sendMsg(CsMgrEnum target, JsonObject msg) {
        if (StartUtil.eng != null) {
            StartUtil.eng.doPush(target, msg);
        }
    }

    public ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.TOOLBAR.name(), name, IconSizeEnum.SIZE_16));
    }
}
