package com.hh.hhdb_admin.common.util;

import com.alee.managers.style.Skin;
import com.alee.utils.ReflectUtils;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.lang.LangEnum;
import com.hh.frame.lic.VerifyLicTool;
import com.hh.frame.swingui.engine.GuiEngine;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.tab_files.TabFileRequires;
import com.hh.frame.swingui.view.ui.skin.AbstractHhSkin;
import com.hh.frame.swingui.view.ui.skin.light.LightSkin;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameComp;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
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
	public static JsonObject defaultJson;
	public static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzM8vX+P2jvRuCvMG6mf+KCqfYjjmLREBXQNsIIYIMqaLkVYSZzC/EXXHC6/x8fFIrRfXMzGA+iGZWDcA5B/CxcoGNsq9lFNurCqX36P9HBAVHEpZbvi8bC+g1kpH8pX700vqwUremy17F5K80Km8nGSeAr6RTz51ROuaY4UMPQwIDAQAB";
	public static GuiEngine eng = null;
	public static LangEnum default_language = LangEnum.ZH;
	public static boolean autoSave = true;
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
	public static final String CS_VERSION = "7.0";

	private static File etcFile = null;
	private static HDialog dialog = null;

	//	public static AbstractHhSkin defaultSkin = null;
	public static TabFileRequires requires;

	static {
		init();
	}

	public static void init() {
		try {
			IconFileUtil.setIconBaseDir(new File(StartUtil.getEtcFile(), "icon"));
			parentFrame = new MainFrameComp();
			requires = new TabFileRequires(parentFrame);
			defaultJson = Json.parse(FileUtils.readFileToString(defaultJsonFile, StandardCharsets.UTF_8))
					.asObject();
			// 从配置文件读取默认语言
			default_language = LangEnum.valueOf(defaultJson.get("language").asString());
			autoSave = defaultJson.getBoolean("autoSave");
			//读取皮肤风格设置

			JsonArray dbTypeArr = defaultJson.get("dbTypes").asArray();
			dbTypeArr.forEach(item -> supportDbTypeList.add(item.asString()));
		} catch (Exception e) {
			e.printStackTrace();
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
		Locale locale;
		switch (langEnum) {
			case EN:
				locale = Locale.ENGLISH;
				break;
			case JA:
				locale = Locale.JAPANESE;
				break;
			default:
				locale = Locale.SIMPLIFIED_CHINESE;
		}
		Locale.setDefault(locale);
		JComponent.setDefaultLocale(Locale.getDefault());
	}

	/**
	 * 读取json文件中的皮肤风格设置
	 *
	 * @return
	 * @throws Exception
	 */
	public static Class<?> getDefaultSkinClass() throws Exception {
		JsonValue uiValue = defaultJson.get("ui");
		if (uiValue != null) {
			JsonObject uiObject = uiValue.asObject();
			String skinClassStr = uiObject.get("skin_class").asString();
			if (StringUtils.isNoneBlank(skinClassStr)) {
				Class<? extends Skin> skinClass = ReflectUtils.getClass(skinClassStr);
				//Class<?> skinClass = Class.forName(skinClassStr);
				if (AbstractHhSkin.class.isAssignableFrom(skinClass)) {
					return skinClass;
				}
			}
		}
		return LightSkin.class;
	}

	/**
	 * 将皮肤设置写入到json文件中
	 *
	 * @param defaultSkin
	 * @throws IOException
	 */
	public synchronized static void writeSkinToFile(AbstractHhSkin defaultSkin) throws IOException {
		JsonValue uiValue = defaultJson.get("ui");
		if (uiValue == null) {
			uiValue = new JsonObject();
		}
		JsonObject uiObject = uiValue.asObject();
		uiObject.set("skin_class", defaultSkin.getClass().getName());
		defaultJson.set("ui", uiObject);
		FileUtils.writeStringToFile(defaultJsonFile, defaultJson.toPrettyString(), StandardCharsets.UTF_8);
	}

}
