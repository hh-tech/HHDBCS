package com.hh.hhdb_admin.mgr.table.comp;

import com.hh.frame.create_dbobj.table.CreateTableTool;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.mgr.table.TableComp;
import com.hh.hhdb_admin.mgr.table.common.TableCreatePanel;
import org.apache.commons.lang3.StringUtils;

/**
 * @author oyx
 * @date 2020-11-6  10:37:31
 */
public class PartitionPanel extends TableCreatePanel {
	public final static String RANGE = "RANGE";
	public final static String LIST = "LIST";
	public final static String HASH = "HASH";

	private final static String TXT_AREA = "txtArea";
	private final static String SELECT_BOX = "selectBox";

	private TextAreaInput areaInput;
	private SelectBox selectBox;

	public PartitionPanel(HDivLayout hDivLayout ) {
		super(hDivLayout);
//		super.baseComp = baseComp;
		init();
	}

	private void init() {
		selectBox = new SelectBox(SELECT_BOX);
		selectBox.addOption("", "");
		selectBox.addOption(RANGE, RANGE);
		selectBox.addOption(LIST, LIST);
		selectBox.addOption(HASH, HASH);

		areaInput = new TextAreaInput(TXT_AREA, "", 2);
		add(TableNamePanel.getLabelInput(TableComp.getLang("tableType"), selectBox, GridSplitEnum.C2));
		add(TableNamePanel.getLabelInput(TableComp.getLang("condition"), areaInput, GridSplitEnum.C2));
	}


	@Override
	public void build(CreateTableTool createTableTool) throws Exception {
		String selectBoxValue = getSelectBoxValue();
		StringBuilder sql = new StringBuilder();
		if (StringUtils.isNoneBlank(selectBoxValue)) {
			sql.append(SPACE).append("PARTITION BY").append(SPACE).append(selectBoxValue);
			String textAreaValue = getTextAreaValue();
			if (StringUtils.isNoneBlank(textAreaValue)) {
				sql.append(B_OPEN).append(textAreaValue.trim()).append(B_CLOSE);
			}

		}
		createTableTool.setOther(sql.toString());
	}

	public String getTextAreaValue() {
		return areaInput.getValue();
	}

	public String getSelectBoxValue() {
		return selectBox.getValue();
	}
}

