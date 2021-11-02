package com.hh.hhdb_admin.common.util;

import com.hh.frame.json.Json;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangEnum;
import com.hh.frame.lic.VerifyLicTool;
import com.hh.frame.swingui.engine.GuiEngine;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameComp;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//import java.text.DecimalFormat;

/**
 * 管理工具基础类
 *
 * @author hexu
 */
public class StartUtil {
	/**
	 * 管理工具配置文件
	 */
	public static File defaultJsonFile = new File(getEtcFile(), "default.json");
	public static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCrX5+JAw51CSoctcslNWDvunqQQqWwB4UOb4RLvvHMeTYi4BYgrA7BsjFf9N0jWGd7n9zDYhkr+UTPzfU6F2OVzio0SGnXoy2wh+VxHUdT3KAv9xVENqYoLC/s6ifKFL0dEnXGLYXSbaHk9+4cQYJRkZdOelmcnjrhpiPTp+jFdQIDAQAB";
	public static GuiEngine eng = null;
	public static LangEnum default_language = LangEnum.ZH;
	public static File workspace = new File(getEtcFile(), "csWorkspase");
	/**
	 * 支持的数据库类型
	 */
	public static List<String> supportDbTypeList = new ArrayList<>();
	/**
	 * CMD命令常量
	 */
	public static final String CMD_CLOSE = "close"; // 分页面板关闭标识
	public static final String CMD_ID = "id"; // 分页面板id
	public static final String PARAM_SCHEMA = "schema"; // 模式名
	public static final String PARAM_TABLE = "table"; // 表名
	public static HFrame parentFrame;
	/**
	 * default.json文件常量
	 */
	public static final String DEFAULT_LANGUAGE = "language";
	public static final String CS_VERSION = "6.4";

	private static File etcFile = null;
	private static HDialog dialog = null;

	static {
		try {
			IconFileUtil.setIconBaseDir(new File(StartUtil.getEtcFile(),"icon"));
			parentFrame = new MainFrameComp();
			JsonObject fileJsonArr = Json.parse(FileUtils.readFileToString(defaultJsonFile, StandardCharsets.UTF_8))
					.asObject();
			// 从配置文件读取默认语言
			default_language = LangEnum.valueOf(fileJsonArr.get("language").asString());

			JsonArray dbTypeArr = fileJsonArr.get("dbTypes").asArray();
			dbTypeArr.forEach(item -> supportDbTypeList.add(item.asString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取mainFrame插件创建的公共弹窗
	 *
	 * @return 公共弹窗
	 * @throws Exception e
	 */
	public static HDialog getMainDialog() {
		if (dialog != null) {
			return dialog;
		}
		dialog = new HDialog(parentFrame, 400);
		dialog.setIconImage(IconFileUtil.getLogo());
		return dialog;
	}

	public static LoginBean getLoginBean() throws Exception {
		JsonObject resObj = eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(LoginMgr.ObjType.LOGIN_BEAN));
		String loginBeanId = GuiJsonUtil.toStrSharedId(resObj);
		return (LoginBean) eng.getSharedObj(loginBeanId);
	}

	public static File getEtcFile() {
		if (etcFile == null) {
			etcFile = new File(System.getProperty("user.dir"), "etc");
		}
		return etcFile;
	}

	public static VerifyLicTool getVt() throws Exception {
		File file = new File(getEtcFile(), "lic");
		File[] licFiles = file.listFiles();
		VerifyLicTool vt = null;
		if (licFiles == null) {
			return null;
		}
		for (File licFile : licFiles) {
			if (licFile.getName().endsWith(".dat")) {
				vt = new VerifyLicTool(licFile, publicKey);
				break;
			}
		}
		return vt;
	}

	public static void setLocale(LangEnum langEnum) {
		switch (langEnum) {
		case ZH:
			Locale.setDefault(Locale.CHINA);
			break;
		case EN:
			Locale.setDefault(Locale.ENGLISH);
			break;
		case JA:
			Locale.setDefault(Locale.JAPANESE);
			break;
		default:
			throw new IllegalStateException("Unexpected value: " + langEnum);
		}
		JComponent.setDefaultLocale(Locale.getDefault());
		// 初始化自定义UI
		try {
			HHSwingUi.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
