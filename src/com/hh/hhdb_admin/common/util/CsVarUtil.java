package com.hh.hhdb_admin.common.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.LangType;
import com.hh.frame.lg.Lg;
import com.hh.frame.lg.LgBaseConf;
import com.hh.frame.lg.LgFileConf;
import com.hh.frame.lic2.AbsGuiLicBean;
import com.hh.frame.lic2.DbcsLicBean;
import com.hh.frame.ssh.JdbcSshTunnelTool;
import com.hh.frame.swingui.cs_desk.GlobalVarUtil;
import com.hh.frame.swingui.cs_desk.license2.LicCheckResult;
import com.hh.frame.swingui.cs_desk.license2.LicCheckTask;
import com.hh.frame.swingui.cs_desk.license2.LicMgr;
import com.hh.frame.swingui.engine2.GuiEngine;
import com.hh.frame.swingui.engine2.GuiMgrArrBean;
import com.hh.frame.swingui.laf.LafMgr;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.conf.CsConf;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameComp;
import com.hh.hhdb_admin.mgr.workspace.WorkSpaceUtil;

/**
 * 管理工具基础类
 *
 */
public class CsVarUtil extends GlobalVarUtil {
	public static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzM8vX+P2jvRuCvMG6mf+KCqfYjjmLREBXQNsIIYIMqaLkVYSZzC/EXXHC6/x8fFIrRfXMzGA+iGZWDcA5B/CxcoGNsq9lFNurCqX36P9HBAVHEpZbvi8bC+g1kpH8pX700vqwUremy17F5K80Km8nGSeAr6RTz51ROuaY4UMPQwIDAQAB";
	public static String exportConfigName = "DBCS文件备份"; // 备份配置导出的名称
	public static final String CONFIG_FILE_NAME = "config.json";
//	public static final String BTN_SQL = "sql_btn";
	public static CsConf CONF;
	public static File workspace = new File(getEtcFile(), "csWorkspase");
	public static File openDataFile = new File(workspace,"open_tmp");
	// 许可检查,60分钟检测一次
	public static LicMgr licMgr;
	public static LicCheckResult result;

	public static LicCheckTask task;
	public static String website; // 官方网站
	public static GuiEngine eng2 = null;
	// 和登录相关变量
	public static JdbcInfo jdbc;
	public static Connection conn;
	private static JdbcSshTunnelTool sshTunnel;

	public static final String[] nessaryFile = { // 恢复配置必须的文件
	};
	public static final String[] backUpNames = { // 需要备份的文件的名称
			"login.dat", "config.json", "file_open_config.json", "sqlbook.path.json", "multiWin.dat",
			"url_setting.json", "dbTypes.json", "ext" };
	public static List<DBTypeEnum> allSupportDbTypes = new ArrayList<>();
	static {

		allSupportDbTypes.add(DBTypeEnum.oracle);
		allSupportDbTypes.add(DBTypeEnum.mysql);
		allSupportDbTypes.add(DBTypeEnum.mariadb);
		allSupportDbTypes.add(DBTypeEnum.db2);
		allSupportDbTypes.add(DBTypeEnum.sqlserver);

		allSupportDbTypes.add(DBTypeEnum.pgsql);

		allSupportDbTypes.add(DBTypeEnum.dm);
		allSupportDbTypes.add(DBTypeEnum.kingbase);
		allSupportDbTypes.add(DBTypeEnum.gbase);
		allSupportDbTypes.add(DBTypeEnum.gbase8a);
		allSupportDbTypes.add(DBTypeEnum.tdsql4mysql);
		allSupportDbTypes.add(DBTypeEnum.tdsql4pg);

		allSupportDbTypes.add(DBTypeEnum.hive);
		allSupportDbTypes.add(DBTypeEnum.redis);
		allSupportDbTypes.add(DBTypeEnum.gaussdb);
		allSupportDbTypes.add(DBTypeEnum.cache);
		allSupportDbTypes.add(DBTypeEnum.derby);
		allSupportDbTypes.add(DBTypeEnum.sqlite);
		allSupportDbTypes.add(DBTypeEnum.hana);
		allSupportDbTypes.add(DBTypeEnum.oceanbase);
		allSupportDbTypes.add(DBTypeEnum.mongodb);
		allSupportDbTypes.add(DBTypeEnum.clickhouse);
		allSupportDbTypes.add(DBTypeEnum.informix);
		allSupportDbTypes.add(DBTypeEnum.sybase);
		allSupportDbTypes.add(DBTypeEnum.greenplum);
		allSupportDbTypes.add(DBTypeEnum.databricks);
	}

	private static HDialog dialog = null;
	private static boolean reload = false;

	public static void init() {
		init(CsVarUtil.class, "mgr_conf.json");
	}

	public static void init(Class<?> c, String confName) {
		try {
			JsonObject config = getConf(CONFIG_FILE_NAME);
			if (!reload) {
				// lic,日志配置和插件加载不在重新加载的范围内
//                website = Json.parse(ClassLoadUtil.loadTextRes(CSProduct.class,"CSProduct.json")).asObject().get("website").asObject().get("k").asString();
				licMgr = new LicMgr(new File(getEtcFile(), GlobalVarUtil.LICENSE_DIR_NAME), publicKey) {
					@Override
					protected void onUpdate() {
						result = licMgr.check();
					}
				};
				task = new LicCheckTask(licMgr, 60);

				if (config == null) {
					CONF = new CsConf();
				} else {
					CONF = new CsConf(config);
				}
				LgBaseConf lgConf = CONF.getLogConf();
				if (lgConf instanceof LgFileConf) {
					((LgFileConf) lgConf).setLogFilePrefix(System.currentTimeMillis() + "_");
				}
				saveConfig();

				LangMgr.addLang(CsL.class);
				LangMgr.langType = CONF.getLang();
				Lg.setConf(CONF.getLogConf());

				JsonObject jObj = Json.parse(ClassLoadUtil.loadTextRes(c, confName)).asObject();
				CsVarUtil.eng2 = new GuiEngine(CsMgrEnum.class, new GuiMgrArrBean(jObj));
				reload = true;
			}

			ExtDbTypeUtil.loadSelfDefineDbTypes();
			if (CONF.isSysWindow())
				parentFrame = new MainFrameComp();
			LafMgr.setLaf(CONF.getSkin());
			LafMgr.setFontSize(CONF.getFontSize());
			if (!CONF.isSysWindow())
				parentFrame = new MainFrameComp();
			WorkSpaceUtil.initWorkSpaceData();

		} catch (Exception e) {
			PopDlgUtil.logErr(CsVarUtil.class, parentFrame, e);
		}
	}

	public static void saveConfig() {
		File f = new File(CsVarUtil.getEtcFile(), CONFIG_FILE_NAME);
		try {
			FileUtils.writeStringToFile(f, CONF.toString(), "UTF-8");
		} catch (IOException e) {
			PopDlgUtil.logErr(CsVarUtil.class, parentFrame, e);
		}
	}

	/**
	 * 获取mainFrame插件创建的公共弹窗
	 *
	 * @return 公共弹窗
	 */
	public static HDialog getMainDialog() {
		if (dialog != null) {
			return dialog;
		}
		dialog = new HDialog(parentFrame, 400);
		dialog.setIconImage(IconFileUtil.getLogo());
		return dialog;
	}

	public static boolean doConn() {
		try {
			closeConn();
			conn = newConn(jdbc);
			return true;
		} catch (Exception e) {
			PopDlgUtil.error(e);
			Lg.error(CsVarUtil.class, e);
			closeConn();
		}
		return false;
	}

	public static Connection newConn(JdbcBean jdbc) throws Exception {
		JdbcBean tmp = jdbc;
		if (jdbc.isUseSshTunnel()) {
			if (sshTunnel == null)
				sshTunnel = new JdbcSshTunnelTool(jdbc);
			if (!sshTunnel.isConnected()) {
				sshTunnel.close();
				sshTunnel = new JdbcSshTunnelTool(jdbc);
			}
			JdbcBean localJdbc = sshTunnel.getLocalJdbc();
			Lg.debug(CsVarUtil.class, "SSH隧道本地jdbc信息:%s\n", localJdbc.toString());
			tmp = localJdbc;
		}
		return ConnUtil.getConn(tmp);
	}

	public static void closeConn() {
		ConnUtil.close(conn);
		if (sshTunnel != null) {
			sshTunnel.close();
			sshTunnel = null;
		}
	}

	public static boolean isConnected() {
		if (conn == null)
			return false;
		return ConnUtil.isConnected(getDbtype(), jdbc.getCurrentVersion(), conn);
	}

	public static DBTypeEnum getDbtype() {
		return DriverUtil.getDbType(jdbc);
	}

	public static JdbcInfo jdbcCloneWithSsh() {
		if (jdbc == null)
			return null;
		JdbcInfo newJdbc = new JdbcInfo(jdbc.clone().getJobj());
		if (jdbc.isUseSshTunnel()) {
			JdbcBean local = sshTunnel.getLocalJdbc();
			newJdbc.setDbUrl(local.getDbUrl());
		}
		return newJdbc;
	}

	public static File getSqlBtnDir() {
		return new File(getEtcFile(), "sql_btn");
	}

	
	
	
	public static boolean canLogin(JdbcBean jdbcBean) {
		try {
			if (result == null) {
				result = CsVarUtil.licMgr.check();
			}
			AbsGuiLicBean b = result.getBean();
			if (b != null) {
				DbcsLicBean licBean = (DbcsLicBean) b;
				List<DBTypeEnum> types = licBean.getValidDBTypes();
				if (types.isEmpty()) {
					return true;
				}
				DBTypeEnum te = DriverUtil.getDbType(jdbcBean);
				if (types.contains(te)) {
					return true;
				}
			}
		} catch (Exception e) {
			Lg.error(CsVarUtil.class, e);
		}
		return false;
	}

	/**
	 * 保存语言
	 * 
	 * @param language
	 */
	public static void saveLang(String language) {
		LangMgr.langType = LangType.valueOf(language);
		CONF.setLang(LangMgr.langType);
		saveConfig();
	}

}
