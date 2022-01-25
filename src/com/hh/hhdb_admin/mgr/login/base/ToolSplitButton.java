package com.hh.hhdb_admin.mgr.login.base;

import com.alee.extended.button.WebSplitButton;
import com.alee.managers.style.StyleId;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.hmenu.HPopMenu;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.comp.CommonComp;
import com.hh.hhdb_admin.mgr.tool.ToolMgr;
import com.hh.hhdb_admin.mgr.tool.ToolUtil;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author ouyangxu
 * @date 2021-12-17 0017 15:26:01
 */
public class ToolSplitButton extends AbsHComp {
	private JButton toolSplitButton;

	private HMenuItem formatSqlItem;
	private HMenuItem sqlConversionItem;
	private HPopMenu toolPopupMenu;

	public ToolSplitButton() {
		this(null);
	}

	public ToolSplitButton(String id) {
		super(id);
		initToolButton();
	}

	protected void initToolButton() {
		comp = toolSplitButton = new WebSplitButton(StyleId.splitbuttonIconHover, CommonComp.getIcon("tool"));
		toolSplitButton.setText(CommonComp.getLang("tool"));
		formatSqlItem = new HMenuItem(ToolUtil.getLang(ToolUtil.SQL_FORMAT), ToolUtil.getIcon(ToolUtil.SQL_FORMAT));
		sqlConversionItem = new HMenuItem(ToolUtil.getLang(ToolUtil.SQL_CONVERSION), ToolUtil.getIcon(ToolUtil.SQL_CONVERSION));

		toolPopupMenu = new HPopMenu();
		toolPopupMenu.addItem(formatSqlItem, sqlConversionItem);

		((WebSplitButton) toolSplitButton).setPopupMenu(toolPopupMenu.getComp());
		((WebSplitButton) toolSplitButton).setAlwaysShowMenu(true);

		formatSqlItem.getComp().addActionListener(e -> StartUtil.eng.doPush(CsMgrEnum.TOOL, GuiJsonUtil.toJsonCmd(ToolMgr.CMD_SHOW_SQL_FORMAT)));
		sqlConversionItem.getComp().addActionListener(e -> StartUtil.eng.doPush(CsMgrEnum.TOOL, GuiJsonUtil.toJsonCmd(ToolMgr.CMD_SHOW_SQL_CONVERSION)));

	}

	public JButton getToolSplitButton() {
		return toolSplitButton;
	}

	public HPopMenu getToolPopupMenu() {
		return toolPopupMenu;
	}

	public HMenuItem getFormatSqlItem() {
		return formatSqlItem;
	}

	public HMenuItem getSqlConversionItem() {
		return sqlConversionItem;
	}

	public List<HMenuItem> getMenuItems() {
		return Arrays.asList(formatSqlItem, sqlConversionItem);
	}

	@Override
	public JButton getComp() {
		return toolSplitButton;
	}

	public void setLocal() {
		toolSplitButton.setText(CommonComp.getLang("tool"));
		formatSqlItem.setText(ToolUtil.getLang("sql_format"));
		sqlConversionItem.setText(ToolUtil.getLang("sql_conversion"));
	}
}
