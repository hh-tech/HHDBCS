package com.hh.hhdb_admin.mgr.table_open.comp;

import com.alee.extended.layout.FormLayout;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.managers.style.StyleId;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.mgr.login.base.ExpandTextInput;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabTool;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

/**
 * @author ouyangxu
 * @date 2021-12-01 0001 14:28:16
 * @description 表格 where条件 sql编辑框
 */
public class WhereTextInput extends ExpandTextInput {
	protected WebButton whereButton;
	protected WebButton sqlButton;
	protected static final String WHERE = "WHERE";
	protected String tips = "输入SQL语句筛选结果(Enter)";

	public WhereTextInput() {
		super();
		setTips(tips);
	}

	@Override
	protected void initLeadingComponent() {
		leadingPanel = new WebPanel(new FormLayout());

		sqlButton = new WebButton(StyleId.buttonIconHover, ModifyTabTool.getIcon("sql"));
		sqlButton.setCursor(Cursor.getDefaultCursor());
		sqlButton.setToolTip("查看SQL");

		whereButton = new WebButton(StyleId.buttonUndecorated, WHERE);
		whereButton.setMargin(0, 5, 0, 5);
//		whereButton.setIcon(ModifyTabDataUtil.getIcon(ModifyTabDataUtil.FILTER_ICON));
		setWhereBtnColor();

		leadingPanel.add(sqlButton, whereButton);
		leadingPanel.setPadding(0, 5, 0, 5);
	}

	@Override
	protected void initTrailingComponent() {
		super.initTrailingComponent();
	}

	public WebButton getWhereButton() {
		return whereButton;
	}

	public WebButton getSqlButton() {
		return sqlButton;
	}

	protected void setWhereBtnColor() {
		boolean isDarkSkin = HHSwingUi.isDarkSkin();
		Color foreground = isDarkSkin ? new Color(245, 245, 245) : Color.BLACK;
		webTextField.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
			String fieldText = webTextField.getText();
			boolean isChange = StringUtils.isNoneBlank(fieldText) && fieldText.length() > 0;
			if (isChange) {
				Color lightForeground = new Color(0, 51, 179);
				Color darkForeground = new Color(69, 177, 255);
				whereButton.setForeground(isDarkSkin ? darkForeground : lightForeground);
			} else {
				whereButton.setForeground(foreground);
			}
		});
	}


}
