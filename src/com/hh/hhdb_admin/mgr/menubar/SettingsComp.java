package com.hh.hhdb_admin.mgr.menubar;

import com.alee.painter.PainterSupport;
import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.RadioGroupInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.frame.swingui.view.util.VerifyUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditUtil;
import com.hh.hhdb_admin.mgr.tool.ToolUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 查询器基础信息设置
 *
 * @author hexu
 */
public class SettingsComp {
	private static final String KEYS = "qkeys";
	private static final String INVALID = "invalid";
	private static final String ALT = "Alt";
	private static final String CTRL = "Ctrl";
	private static final String NULL = "null";
	private static final String QUERY_DISPLAY_SETTING = "query_display_setting";
	public static final String SQL_FORMAT_SETTING = "sql_format_setting";
	public static final String SQL_FORMAT_BREAK_WIDTH = "sql_format_break_width";


	private HDialog dialog;
	private TextInput rowInput;
	private TextInput nullInput;
	private TextInput breakWidthInput;

	private HPanel keyPanel;
	private HPanel vmKeyPanel;
	private HPanel queryDisplayPanel;
	private HPanel sqlFormatPanel;

	private JsonObject fileJsonArr;

	public SettingsComp() {
		try {
			fileJsonArr = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();

			dialog = new HDialog(StartUtil.parentFrame, HWindow.SMALL_WIDTH, true);
			dialog.setIconImage(MenubarComp.getIcon("setting"));
			dialog.setWindowTitle(MenubarComp.getLang("setting"));
			LastPanel rootPanel = initRootPanel();
			dialog.setRootPanel(rootPanel);
			((JDialog) dialog.getWindow()).setResizable(false);
			dialog.setSize(HWindow.SMALL_WIDTH, 780);
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
			PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
		}
	}

	/**
	 * 查询器快捷键设置面板
	 *
	 * @return HPanel
	 */
	private HPanel initKeyPanel() {
		//快捷键设置面板
		HPanel keyPanel = new HPanel(new HDivLayout(15, 10, GridSplitEnum.C12));
		keyPanel.setTitle(MenubarComp.getLang("quer_settings"));
		keyPanel.add(getLabelInput("  ", new LabelInput(MenubarComp.getLang("frontkey")), new LabelInput(MenubarComp.getLang("backkey")), new LabelInput(MenubarComp.getLang("combination"))));
		keyPanel.add(getKeyWordPanel(MenubarComp.getLang("key"), QueryEditUtil.KEYWORD));
		keyPanel.add(getKeyWordPanel(MenubarComp.getLang("table_name"), QueryEditUtil.TABLE_KEYWORD));
		keyPanel.add(getKeyWordPanel(MenubarComp.getLang("view_name"), QueryEditUtil.VIEW_KEYWORD));
		keyPanel.add(getKeyWordPanel(MenubarComp.getLang("fun_name"), QueryEditUtil.FUN_KEYWORD));
		keyPanel.add(getKeyWordPanel(MenubarComp.getLang("synonyms-for"), QueryEditUtil.SYNONYM_KEYWORD));
		keyPanel.add(getKeyWordPanel(MenubarComp.getLang("package"), QueryEditUtil.PACKAGE_KEYWORD));
		keyPanel.add(new HeightComp(5));
		return keyPanel;
	}

	/**
	 * 查询器显示设置面板
	 *
	 * @return HPanel
	 */
	private HPanel initQueryDisplayPanel() {
		HPanel queryDisplayPanel = new HPanel(new HDivLayout(15, 10, GridSplitEnum.C12));
		//一般设置面板
		String str = fileJsonArr.get(KEYS).asObject().asObject().get(QueryEditUtil.AUTOMATIC).asString();
		str = StringUtils.isNotBlank(str) ? str : "true";
		queryDisplayPanel.setTitle(MenubarComp.getLang(QUERY_DISPLAY_SETTING));
		rowInput = new TextInput("varPageSize", fileJsonArr.get("varPageSize").asInt() + "");
		rowInput.setInputVerifier(VerifyUtil.getTextIntVerifier(MenubarComp.getLang("rowsnumber"), 1, Integer.MAX_VALUE));

		HGridPanel rowsNumber = getLabelInput(MenubarComp.getLang("rowsnumber") + ":", rowInput);
		rowsNumber.setId("varPageSize");
		queryDisplayPanel.add(rowsNumber);
		nullInput = new TextInput(NULL);
		nullInput.setValue(fileJsonArr.get(NULL).asString());
		HGridPanel nulGrid = getLabelInput(MenubarComp.getLang("null_view"), nullInput);
		nulGrid.setId(NULL);
		queryDisplayPanel.add(nulGrid);
		RadioGroupInput rInput = new RadioGroupInput(QueryEditUtil.AUTOMATIC, new HPanel(new HDivLayout(GridSplitEnum.C6)));
		rInput.setId(QueryEditUtil.AUTOMATIC);
		rInput.add("true", MenubarComp.getLang("automatic"));
		rInput.add("false", MenubarComp.getLang("manual"));
		rInput.setSelected(str);
		HGridPanel gridPanel = getLabelInput(MenubarComp.getLang("prompt_mode") + ":", rInput);
		gridPanel.setId(QueryEditUtil.AUTOMATIC);
		queryDisplayPanel.add(gridPanel);
		return queryDisplayPanel;
	}

	/**
	 * 模板编辑器快捷键设置面板
	 *
	 * @return HPanel
	 */
	private HPanel initVmKeyPanel() {
		HPanel vmKeyPanel = new HPanel(new HDivLayout(15, 10, GridSplitEnum.C12));
		vmKeyPanel.setTitle(MenubarComp.getLang("vm_setting"));
		vmKeyPanel.add(new HeightComp(5));
		vmKeyPanel.add(getKeyWordPanel(MenubarComp.getLang("key"), QueryEditUtil.VMKEYWORD));
		vmKeyPanel.add(new HeightComp(5));
		return vmKeyPanel;
	}


	/**
	 * sql格式化宽度设置
	 *
	 * @return
	 */
	private HPanel initSqlFormatPanel() {
		breakWidthInput = new TextInput();
		String sqlFormatBreakWidth = ToolUtil.getSqlFormatBreakWidth();
		breakWidthInput.setValue(sqlFormatBreakWidth);
		HGridPanel gridPanel = getLabelInput(MenubarComp.getLang(SQL_FORMAT_BREAK_WIDTH) + ":", breakWidthInput);
		gridPanel.setId(SQL_FORMAT_BREAK_WIDTH);

		HPanel sqlFormatPanel = new HPanel(new HDivLayout(15, 10, GridSplitEnum.C12));
		sqlFormatPanel.setTitle(MenubarComp.getLang(SQL_FORMAT_SETTING));
		sqlFormatPanel.add(new HeightComp(5));
		sqlFormatPanel.add(gridPanel);
		sqlFormatPanel.add(new HeightComp(5));
		return sqlFormatPanel;
	}

	/**
	 * 初始化中间面板
	 *
	 * @return LastPanel
	 */
	private LastPanel initRootPanel() {
		keyPanel = initKeyPanel();
		vmKeyPanel = initVmKeyPanel();
		queryDisplayPanel = initQueryDisplayPanel();
		sqlFormatPanel = initSqlFormatPanel();
		LastPanel lastPanel = new LastPanel(true);

		HPanel rootPanel = new HPanel(new HDivLayout(15, 10, GridSplitEnum.C12));
		rootPanel.add(keyPanel, vmKeyPanel, queryDisplayPanel, sqlFormatPanel);
		rootPanel.getComp().setBorder(BorderFactory.createEmptyBorder());
		PainterSupport.setPadding(rootPanel.getComp(), 5);
		((JComponent) lastPanel.getComp()).setBorder(BorderFactory.createEmptyBorder());
		lastPanel.set(rootPanel.getComp());
		lastPanel.setFoot(initButtons().getComp());
		return lastPanel;
	}

	private HGridPanel getKeyWordPanel(String type, String name) {
		TextInput key = new TextInput(name);
		key.setEnabled(false);
		TextInput keyInput = new TextInput();
		SelectBox keyBox = new SelectBox();
		keyBox.getComp().addItemListener(e -> {
			if (StringUtils.isNoneBlank(keyInput.getValue().trim())) {
				key.setValue(keyBox.getValue() + "+" + keyInput.getValue().trim());
			}
		});
		keyBox.addOption(ALT, ALT);
		keyBox.addOption(CTRL, CTRL);
//        keyBox.addOption("command ", "command");
		keyBox.setValue(ALT);

		keyInput.getComp().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == 16) {
					return;
				}
				char keyChar = e.getKeyChar();
				if (!"".equals(("" + keyChar + "").trim()) && keyChar < 127) {
					keyInput.setValue("" + keyChar + "");
					key.setValue(keyBox.getValue() + "+" + keyChar);
				} else {
					key.setValue(MenubarComp.getLang(INVALID));
					keyInput.setValue("");
				}
			}
		});
		String keyStr = fileJsonArr.get(KEYS).asObject().asObject().get(name).asString();
		if (StringUtils.isNotBlank(keyStr) && !keyStr.equals(MenubarComp.getLang(INVALID))) {
			keyBox.setValue(keyStr.substring(0, keyStr.indexOf("+")));
			keyInput.setValue(keyStr.charAt(keyStr.length() - 1) + "");
			key.setValue(keyStr);
		} else {
			key.setValue(MenubarComp.getLang(INVALID));
		}
		HGridPanel hPanel = getLabelInput(type + ":", keyBox, keyInput, key);
		hPanel.setId(name);
		return hPanel;
	}

	private JsonObject getSqlFormatJsonObject() {
		JsonObject sqlFormatJson = new JsonObject();
		sqlFormatJson.add(SQL_FORMAT_BREAK_WIDTH, breakWidthInput.getValue());
		return sqlFormatJson;
	}

	private JsonObject getKeyJsonObject() {
		String keyText = ((HGridPanel) keyPanel.getHComp(QueryEditUtil.KEYWORD)).getInputValue(QueryEditUtil.KEYWORD);
		String tabText = ((HGridPanel) keyPanel.getHComp(QueryEditUtil.TABLE_KEYWORD)).getInputValue(QueryEditUtil.TABLE_KEYWORD);
		String viewText = ((HGridPanel) keyPanel.getHComp(QueryEditUtil.VIEW_KEYWORD)).getInputValue(QueryEditUtil.VIEW_KEYWORD);
		String funText = ((HGridPanel) keyPanel.getHComp(QueryEditUtil.FUN_KEYWORD)).getInputValue(QueryEditUtil.FUN_KEYWORD);
		String synonymText = ((HGridPanel) keyPanel.getHComp(QueryEditUtil.SYNONYM_KEYWORD)).getInputValue(QueryEditUtil.SYNONYM_KEYWORD);
		String packageText = ((HGridPanel) keyPanel.getHComp(QueryEditUtil.PACKAGE_KEYWORD)).getInputValue(QueryEditUtil.PACKAGE_KEYWORD);
		String vmkeyText = ((HGridPanel) vmKeyPanel.getHComp(QueryEditUtil.VMKEYWORD)).getInputValue(QueryEditUtil.VMKEYWORD);
		String automatic = ((HGridPanel) queryDisplayPanel.getHComp(QueryEditUtil.AUTOMATIC)).getInputValue(QueryEditUtil.AUTOMATIC);

		if (checkQueryKey(keyText, tabText, viewText, funText, synonymText, packageText, vmkeyText)) {
			return null;
		}
		JsonObject newObj = new JsonObject();
		newObj.add(QueryEditUtil.AUTOMATIC, automatic);
		newObj.add(QueryEditUtil.KEYWORD, keyText);
		newObj.add(QueryEditUtil.TABLE_KEYWORD, tabText);
		newObj.add(QueryEditUtil.VIEW_KEYWORD, viewText);
		newObj.add(QueryEditUtil.FUN_KEYWORD, funText);
		newObj.add(QueryEditUtil.SYNONYM_KEYWORD, synonymText);
		newObj.add(QueryEditUtil.PACKAGE_KEYWORD, packageText);
		newObj.add(QueryEditUtil.VMKEYWORD, vmkeyText);
		return newObj;
	}

	private HBarPanel initButtons() {
		HButton saveBtn = new HButton(MenubarComp.getLang("Ok"));
		PainterSupport.setPadding(saveBtn.getComp(), 5);
		saveBtn.addActionListener(e -> save());
		saveBtn.setIcon(MenubarComp.getIcon("submit"));
		HButton cancelBtn = new HButton(MenubarComp.getLang("cancel"));
		PainterSupport.setPadding(cancelBtn.getComp(), 5);
		cancelBtn.addActionListener(e -> dialog.dispose());
		cancelBtn.setIcon(MenubarComp.getIcon("cancel"));
		HBarLayout layout = new HBarLayout();
		layout.setxGap(10);
		layout.setBottomHeight(10);
		layout.setAlign(AlignEnum.CENTER);
		HBarPanel barPanel = new HBarPanel(layout);
		barPanel.add(saveBtn, cancelBtn);
		return barPanel;
	}

	private void save() {
		if (!ToolUtil.verifyBreakWidth(breakWidthInput.getValue())) {
			return;
		}

		BufferedWriter writer = null;
		JsonObject jsonObject = new JsonObject();
		fileJsonArr.forEach(a -> {
			JsonValue obj = a.getValue();
			switch (a.getName()) {
				case KEYS:
					//保存快捷方式到json
					JsonObject newObj = getKeyJsonObject();
					if (newObj == null) {
						return;
					}
					jsonObject.add(a.getName(), newObj);
					break;
				case NULL:
					String nullStr = ((HGridPanel) queryDisplayPanel.getHComp(NULL)).getInputValue(NULL);
					jsonObject.add(a.getName(), nullStr);
					break;
				case "varPageSize":
					String varPageSize = ((HGridPanel) queryDisplayPanel.getHComp("varPageSize")).getInputValue("varPageSize");
					jsonObject.add(a.getName(), Integer.parseInt(varPageSize));
					break;
				case SQL_FORMAT_SETTING:
					jsonObject.add(a.getName(), getSqlFormatJsonObject());
					break;
				default:
					jsonObject.add(a.getName(), obj);
					break;
			}
		});
		if (!fileJsonArr.names().contains(SQL_FORMAT_SETTING)) {
			jsonObject.add(SQL_FORMAT_SETTING, getSqlFormatJsonObject());
		}

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(StartUtil.defaultJsonFile), StandardCharsets.UTF_8));
			jsonObject.writeTo(writer);
			PopPaneUtil.info(StartUtil.parentFrame.getWindow(), MenubarComp.getLang("success"));
			dialog.dispose();
		} catch (IOException e) {
			e.printStackTrace();
			PopPaneUtil.error(dialog.getWindow(), e.getMessage());
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 快捷键合法验证
	 */
	private boolean checkQueryKey(String keyText, String tabText, String viewText, String funText, String synonymText, String packageText, String vmkeyText) {
		for (String s : Arrays.asList(keyText, tabText, viewText, funText, synonymText, vmkeyText)) {
			if (s.equals(MenubarComp.getLang(INVALID)) || "Ctrl+f".equals(s)) {
				JOptionPane.showMessageDialog(null, MenubarComp.getLang("settingError") + s, MenubarComp.getLang("error"), JOptionPane.ERROR_MESSAGE);
				return true;
			}
		}
		List<String> keyTextVerify = Arrays.asList(tabText, viewText, funText, synonymText, packageText);
		for (String key : keyTextVerify) {
			if (verify(keyText, key)) {
				return true;
			}
		}

		keyTextVerify = Arrays.asList(viewText, funText, funText, synonymText, packageText);
		for (String key : keyTextVerify) {
			if (verify(tabText, key)) {
				return true;
			}
		}
		keyTextVerify = Arrays.asList(funText, synonymText, packageText);
		for (String key : keyTextVerify) {
			if (verify(viewText, key)) {
				return true;
			}
		}

		keyTextVerify = Arrays.asList(synonymText, packageText);
		for (String key : keyTextVerify) {
			if (verify(funText, key)) {
				return true;
			}
		}
		return verify(synonymText, packageText);
	}

	private boolean verify(String str, String str1) {
		if (str.equals(str1)) {
			JOptionPane.showMessageDialog(null, MenubarComp.getLang("settingError") + str, MenubarComp.getLang("error"), JOptionPane.ERROR_MESSAGE);
			return true;
		}
		return false;
	}

	private HGridPanel getLabelInput(String name, AbsInput... intputs) {
		HGridPanel gridPanel;
		if (intputs.length > 1) {
			gridPanel = new HGridPanel(new HGridLayout(GridSplitEnum.C3, GridSplitEnum.C3, GridSplitEnum.C3));
		} else {
			gridPanel = new HGridPanel(new HGridLayout(GridSplitEnum.C3));
		}
		gridPanel.setComp(1, new LabelInput(name, AlignEnum.RIGHT));
		int i = 2;
		for (AbsInput absInput : intputs) {
			if (absInput instanceof LabelInput) {
				((LabelInput) absInput).setAlign(AlignEnum.CENTER);
			}
			gridPanel.setComp(i, absInput);
			i++;
		}
		return gridPanel;
	}

}
