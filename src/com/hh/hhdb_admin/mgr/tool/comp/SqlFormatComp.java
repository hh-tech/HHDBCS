package com.hh.hhdb_admin.mgr.tool.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.parser.sql_fmt2.SqlFmtUtil;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.textEditor.base.ConstantsEnum;
import com.hh.frame.swingui.view.util.ClipboardUtil;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.mgr.tool.ToolUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author ouyangxu
 * @date 2021-12-15 0015 9:39:53
 */
public class SqlFormatComp extends LastPanel {

	private HBarPanel barPanel;
	private HTextArea sqlArea;
	private CommonButtons commonButtons;


	public SqlFormatComp() {
		super(false);
		initComp();
	}

	protected void initComp() {
		HBarLayout barLayout = new HBarLayout();
		barLayout.setAlign(AlignEnum.LEFT);
		barLayout.setTopHeight(10);
		barLayout.setBottomHeight(10);
		barPanel = new HBarPanel(barLayout);
		List<AbsHComp> absComps = initToolBarBtn();
		barPanel.add(absComps.toArray(new AbsHComp[0]));
		setHead(barPanel.getComp());

		sqlArea = new HTextArea();
		sqlArea.setConstants(ConstantsEnum.SYNTAX_STYLE_SQL);

		set(sqlArea.getComp());
	}

	protected List<AbsHComp> initToolBarBtn() {
		commonButtons = new CommonButtons();
		commonButtons.getFormatBtn().addActionListener(e -> formatSql());
		commonButtons.getCopyBtn().addActionListener(e -> copyValue());
		commonButtons.getClearBtn().addActionListener(e -> sqlArea.setText(""));
		return commonButtons.getButtons();
	}

	private void copyValue() {
		try {
			String srcStr = sqlArea.getArea().getTextArea().getText();
			if (StringUtils.isNoneBlank(srcStr)) {
				ClipboardUtil.putText(srcStr);
				PopPaneUtil.info(ToolUtil.getLang("copy_success"));
			}
		} catch (Exception ex) {
			PopPaneUtil.error(ex);
			ex.printStackTrace();
		}
	}


	/**
	 * 格式化sql
	 */
	private void formatSql() {
		try {
			String srcStr = sqlArea.getArea().getTextArea().getText();
			if (StringUtils.isNoneBlank(srcStr)) {
				String formatBreakWidth = commonButtons.getFormatBreakWidth();
				String srcSql = SqlFmtUtil.fmt2Str(srcStr, Integer.parseInt(formatBreakWidth == null ? ToolUtil.sqlFormatBreakWidth : formatBreakWidth), 0);
				sqlArea.setText(srcSql);
			}
		} catch (Exception ex) {
			PopPaneUtil.error(ex);
			ex.printStackTrace();
		}
	}

	public HTextArea getSqlArea() {
		return sqlArea;
	}


}
