package com.hh.hhdb_admin.mgr.tool;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.engine.GuiMsgType;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.tool.comp.SqlConversionComp;
import com.hh.hhdb_admin.mgr.tool.comp.SqlFormatComp;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author ouyangxu
 * @date 2021-12-17 0017 17:21:53
 */
public class ToolMgr extends AbsGuiMgr {
	public static final String CMD_SHOW_SQL_FORMAT = "CMD_SHOW_SQL_FORMAT";
	public static final String CMD_CLOSE_SQL_FORMAT = "CMD_CLOSE_SQL_FORMAT";

	public static final String CMD_SHOW_SQL_CONVERSION = "CMD_SHOW_SQL_CONVERSION";
	public static final String CMD_CLOSE_SQL_CONVERSION = "CMD_CLOSE_SQL_CONVERSION";

	@Override
	public void init(JsonObject jObj) {

	}

	@Override
	public String getHelp() {
		return GuiJsonUtil.genCmdHelp(CMD_SHOW_SQL_FORMAT, "工具", GuiMsgType.RECE);
	}

	@Override
	public Enum<?> getType() {
		return CsMgrEnum.TOOL;
	}

	@Override
	public void doPush(JsonObject msg) throws Exception {

		String cmd = msg.getString("cmd");
		if (StringUtils.isBlank(cmd)) {
			cmd = GuiJsonUtil.toStrCmd(msg);
		}
		LastPanel comp = null;
		String name = null;
		switch (cmd) {
			case CMD_SHOW_SQL_CONVERSION:
				comp = new SqlConversionComp();
				name = ToolUtil.SQL_CONVERSION;
				break;
			case CMD_SHOW_SQL_FORMAT:
				comp = new SqlFormatComp();
				name = ToolUtil.SQL_FORMAT;
			default:
		}
		showComp(name, comp);
	}

	protected void showComp(String name, LastPanel lastPanel) {
		if (name == null || lastPanel == null) {
			return;
		}
		HFrame frame = ToolUtil.initFrame(name);
		frame.getWindow().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ToolUtil.closeFrame(frame);
			}
		});
		frame.setRootPanel(lastPanel);
		frame.show();
		frame.getWindow().setLocation(100 + (ToolUtil.hFrames.size() * 50), 100 + (ToolUtil.hFrames.size() * 50));
	}

	@Override
	public JsonObject doCall(JsonObject msg) throws Exception {
		return null;
	}

}
