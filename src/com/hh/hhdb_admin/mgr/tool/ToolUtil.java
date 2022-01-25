package com.hh.hhdb_admin.mgr.tool;

import com.hh.frame.common.util.db.SelectTableSqlUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.menubar.SettingsComp;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author ouyangxu
 * @date 2021-12-16 0016 16:43:11
 */
public class ToolUtil {
	public static final String DOMAIN_NAME = ToolUtil.class.getName();
	public static final String TOOL_JSON = "Tool.json";
	public static final String SQL_CONVERSION = "sql_conversion";
	public static final String CONVERSION = "conversion";
	public static final String CLEAR = "clear";
	public static final String CONVERSION_SUCCESS = "conversion_success";
	public static final String CONVERSION_FAIL = "conversion_fail";
	public static final String SOURCE = "source";
	public static final String DEST = "dest";
	public static final String COPY_VALUE = "copy_value";
	public static final String CLOSE_TIPS = "close_tips";

	public static final String SQL_FORMAT = "sql_format";
	public static final String SQL_FORMAT_BREAK_WIDTH = "sql_format_break_width";

	public static String sqlFormatBreakWidth = "50";

	public static List<HFrame> hFrames;

	static {
		try {
			LangMgr2.loadMerge(ToolUtil.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static String getLang(String key) {
		LangMgr2.setDefaultLang(StartUtil.default_language);
		return LangMgr2.getValue(DOMAIN_NAME, key);
	}

	public static ImageIcon getIcon(String name) {
		return IconFileUtil.getIcon(new IconBean("Tool", name, IconSizeEnum.SIZE_16));
	}

	public static ImageIcon getIcon(String name, IconSizeEnum sizeEnum) {
		return IconFileUtil.getIcon(new IconBean("Tool", name, sizeEnum));
	}


	public static HFrame initFrame(String name) {
		HFrame frame = new HFrame(HFrame.LARGE_WIDTH);
		frame.setWindowTitle(ToolUtil.getLang(name));
		frame.setIconImage(Objects.requireNonNull(ToolUtil.getIcon(name, IconSizeEnum.SIZE_32)));
		((JFrame) frame.getWindow()).setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		if (hFrames == null) {
			hFrames = new ArrayList<>();
		}
		hFrames.add(frame);
		return frame;
	}

	public static void closeFrame(HFrame frame) {
		int res = JOptionPane.showConfirmDialog(frame.getWindow(), ToolUtil.getLang(ToolUtil.CLOSE_TIPS), null, JOptionPane.YES_NO_OPTION);
		if (res == 0) {
			frame.dispose();
			if (hFrames != null) {
				hFrames.remove(frame);
			}
		}
	}

	public static String getSqlFormatBreakWidth() {
		try {
			JsonObject json = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();
			JsonValue sqlFormatJson = json.get(SettingsComp.SQL_FORMAT_SETTING);
			if (sqlFormatJson != null) {
				JsonValue breakWidthJson = sqlFormatJson.asObject().get(SettingsComp.SQL_FORMAT_BREAK_WIDTH);
				if (breakWidthJson != null) {
					return breakWidthJson.asString();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sqlFormatBreakWidth;
	}

	public static boolean verifyBreakWidth(String value) {
		if (SelectTableSqlUtil.isNumeric(value)) {
			if (Integer.parseInt(value) <= 0) {
				PopPaneUtil.error("格式化宽度不能为0!");
			} else {
				return true;
			}
		} else {
			PopPaneUtil.error("格式化宽度请输入正整数!");
		}
		return false;
	}

}
