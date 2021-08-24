package com.hh.hhdb_admin.mgr.monitor;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.engine.GuiMsgType;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;
import com.hh.hhdb_admin.mgr.monitor.comp.DbChartMonitorComp;
import com.hh.hhdb_admin.mgr.monitor.comp.DbLockMonitorComp;
import com.hh.hhdb_admin.mgr.monitor.comp.DbMonitorComp;
import com.hh.hhdb_admin.mgr.monitor.comp.SystemMonitorComp;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

/**
 * @author YuSai
 */
public class MonitorMgr extends AbsGuiMgr {

    public static final String CMD_SHOW_DB_MONITOR = "SHOW_DB_MONITOR";
    public static final String CMD_SHOW_DB_LOCK_MONITOR = "SHOW_DB_LOCK_MONITOR";
    public static final String CMD_SHOW_DB_CHART_MONITOR = "SHOW_DB_CHART_MONITOR";
    public static final String CMD_SHOW_SYSTEM_MONITOR = "SHOW_SYSTEM_MONITOR";
    public static final String IS_TEST = "IS_TEST";

    private  DbMonitorComp dbMonitorComp;
    private DbChartMonitorComp chartComp;
    private SystemMonitorComp sysComp;

    private String dbMonitorTabId;
    private String dbLockMonitorTabId;
    private String chartMonitorTabId;
    private String systemMonitorTabId;

    @Override
    public void init(JsonObject jObj) {}

    @Override
    public String getHelp() {
        return GuiJsonUtil.genCmdHelp(CMD_SHOW_DB_MONITOR, "数据库监控(表)", GuiMsgType.RECE) +
                GuiJsonUtil.genCmdHelp(CMD_SHOW_DB_LOCK_MONITOR, "数据库锁监控", GuiMsgType.RECE) +
                GuiJsonUtil.genCmdHelp(CMD_SHOW_DB_CHART_MONITOR, "数据库监控(图)", GuiMsgType.RECE) +
                GuiJsonUtil.genCmdHelp(CMD_SHOW_SYSTEM_MONITOR, "系统监控", GuiMsgType.RECE);
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.MONITOR;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        String cmd = GuiJsonUtil.toStrCmd(msg);
        LoginBean loginBean = getLoginBean();
        Connection conn = loginBean.getConn();
        JdbcBean jdbcBean = getLoginBean().getJdbc();
        DBTypeEnum dbTypeEnum = DriverUtil.getDbType(jdbcBean);
        switch (cmd) {
            case MonitorMgr.CMD_SHOW_DB_MONITOR:
                dbMonitorComp = new DbMonitorComp(conn, jdbcBean, dbTypeEnum);
                if (msg.getBoolean(IS_TEST)) {
                    HDialog dialog = new HDialog(1500, 800);
                    dbMonitorComp.show(dialog);
                } else {
                    if (StringUtils.isBlank(dbMonitorTabId)) {
                        dbMonitorTabId = StartUtil.eng.push2SharedMap(dbMonitorComp.getPanel());
                    }
                    openTab(dbMonitorTabId, dbMonitorComp.getTitle());
                }
                break;
            case MonitorMgr.CMD_SHOW_DB_LOCK_MONITOR:
                DbLockMonitorComp dbLockMonitorComp = new DbLockMonitorComp(conn, dbTypeEnum);
                if (StringUtils.isBlank(dbLockMonitorTabId)) {
                    dbLockMonitorTabId = StartUtil.eng.push2SharedMap(dbLockMonitorComp.getPanel());
                }
                openTab(dbLockMonitorTabId, dbLockMonitorComp.getTitle());
                break;
            case MonitorMgr.CMD_SHOW_DB_CHART_MONITOR:
                chartComp = new DbChartMonitorComp(conn, dbTypeEnum);
                if (StringUtils.isBlank(chartMonitorTabId)) {
                    chartMonitorTabId = StartUtil.eng.push2SharedMap(chartComp.getPanel());
                }
                openTab(chartMonitorTabId, chartComp.getTitle());
                break;
            case MonitorMgr.CMD_SHOW_SYSTEM_MONITOR:
                if (StringUtils.isBlank(systemMonitorTabId) ) {
                    sysComp = new SystemMonitorComp(conn, dbTypeEnum) {
                        @Override
                        public void repaint() {
                            systemMonitorTabId = StartUtil.eng.push2SharedMap(sysComp.getPanel());
                            openTab(systemMonitorTabId, sysComp.getTitle());
                        }
                    };
                    if (msg.getBoolean(IS_TEST)) {
                        HDialog dialog = new HDialog(1500, 800);
                        sysComp.show(dialog);
                    }
                } else {
                    openTab(systemMonitorTabId, sysComp.getTitle());
                }
                break;
            case StartUtil.CMD_CLOSE:
                String closeId = msg.getString(StartUtil.CMD_ID);
                if (closeId.equals(dbMonitorTabId)) {
                    if (null != dbMonitorComp) {
                        dbMonitorTabId = null;
                        dbMonitorComp.closeRunnable();
                    }
                } else if (closeId.equals(dbLockMonitorTabId)) {
                    dbLockMonitorTabId = null;
                } else if (closeId.equals(chartMonitorTabId)) {
                    if (null != chartComp) {
                        chartMonitorTabId = null;
                        chartComp.closeRunnable();
                    }
                } else {
                    if (null != sysComp) {
                        systemMonitorTabId = null;
                        sysComp.closeRunnable();
                    }
                }
                break;
            default:
                unknowMsg(msg.toPrettyString());
                break;
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) {
        return null;
    }

    private LoginBean getLoginBean() throws Exception {
        JsonObject sharedIdObj = StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(LoginMgr.ObjType.LOGIN_BEAN));
        return (LoginBean) StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(sharedIdObj));
    }

    private static void openTab(String id, String title) {
        StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
                .add("id", id)
                .add("title", title)
                .add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.MONITOR.name()));
    }

}
