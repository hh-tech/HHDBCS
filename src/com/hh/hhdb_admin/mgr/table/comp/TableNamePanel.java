package com.hh.hhdb_admin.mgr.table.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.create_dbobj.table.CreateTableTool;
import com.hh.frame.lang.LangEnum;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.WithLabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.mgr.table.TableComp;
import com.hh.hhdb_admin.mgr.table.common.TableCreatePanel;
import com.hh.hhdb_admin.mgr.table.common.TableUtil;

/**
 * @author oyx
 * @Description: 表格名及注解
 * @date 2020-9-24  0024 16:51:05
 */
public class TableNamePanel extends TableCreatePanel {
	private TextInput tableNameInput;
	private TextAreaInput annotateInput;

	private final static String LK_SAVE = "SAVE";
	private final static String LK_SQL_VIEW = "SQL_VIEW";
	private final static String LK_TABLE_NAME = "TABLE_NAME";
	private final static String LK_TABLE_COMMENT = "TABLE_COMMENT";

	public TableNamePanel(HDivLayout hDivLayout) {
		super(hDivLayout);
		lastPanel = new LastPanel(false);
		tableNameInput = new TextInput("table_name", "new_table");
		annotateInput = new TextAreaInput("annotateArea", "", 2);
		GridSplitEnum splitEnum = LangMgr.getDefaultLang() == LangEnum.ZH ? GridSplitEnum.C1 : GridSplitEnum.C2;

		add(getLabelInput(TableComp.getLang(LK_TABLE_NAME), tableNameInput, splitEnum));
		add(getLabelInput(TableComp.getLang(LK_TABLE_COMMENT), annotateInput, splitEnum));
		lastPanel.set(this.getComp());
		lastPanel.setHead(getSaveBar().getComp());
	}

	@Override
	public void build(CreateTableTool createTableTool) throws Exception {

	}

	public TextInput getTableNameInput() {
		return tableNameInput;
	}

	public void setTableNameInput(TextInput tableNameInput) {
		this.tableNameInput = tableNameInput;
	}

	public TextAreaInput getAnnotateInput() {
		return annotateInput;
	}

	public void setAnnotateInput(TextAreaInput annotateInput) {
		this.annotateInput = annotateInput;
	}

	public static WithLabelInput getLabelInput(String label, AbsInput input, GridSplitEnum splitEnum) {
		HPanel hPanel = new HPanel(new HDivLayout(splitEnum));
		LabelInput labelInput = new LabelInput(label);
		labelInput.setAlign(AlignEnum.LEFT);
		return new WithLabelInput(hPanel, labelInput, input);
	}

	public HBarPanel getSaveBar() {
		HBarPanel toolBar = new HBarPanel(TableUtil.newBarPan());
		toolBar.add(getSaveButton());
		toolBar.add(getViewSqlButton());
		return toolBar;
	}

	public HButton getSaveButton() {
		HButton button = new HButton("saveButton", TableComp.getLang(LK_SAVE)) {
			@Override
			protected void onClick() {
				saveOnclick();
			}
		};
		button.setIcon(TableUtil.getIcon(TableUtil.save_icon));
		return button;
	}

	public HButton getViewSqlButton() {
		HButton button = new HButton("saveButton", TableComp.getLang(LK_SQL_VIEW)) {
			@Override
			protected void onClick() {
				viewSqlOnclick();
			}
		};
		button.setIcon(TableUtil.getIcon(TableUtil.sql_view_icon));
		return button;
	}

	public LastPanel getLastPanel() {
		return lastPanel;
	}

	protected void viewSqlOnclick() {
	}

	protected void saveOnclick() {
	}


}
