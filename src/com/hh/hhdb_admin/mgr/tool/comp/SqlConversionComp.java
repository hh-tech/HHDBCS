package com.hh.hhdb_admin.mgr.tool.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.parser.sql_fmt2.SqlFmtUtil;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HFlowPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.textEditor.base.ConstantsEnum;
import com.hh.frame.swingui.view.textEditor.base.ThemesEnum;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.ClipboardUtil;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.mgr.login.base.DatabaseTypeComboBox;
import com.hh.hhdb_admin.mgr.menubar.SqlConversionUtil;
import com.hh.hhdb_admin.mgr.tool.ToolUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * SQL转换Comp
 *
 * @author ouyangxu
 * @date 2021-12-09 14:33:02
 */
public class SqlConversionComp extends LastPanel {
	private HTextArea srcArea;
	private HTextArea destArea;
	private DatabaseTypeComboBox srcBox;
	private SelectBox destBox;
	private CommonButtons commonButtons;


	public SqlConversionComp() {
		init();
	}

	protected void init() {
		srcArea = new HTextArea(false, true);
		srcArea.showBookMask(false);
		srcArea.setConstants(ConstantsEnum.SYNTAX_STYLE_SQL);

		destArea = new HTextArea(false, true);
		destArea.showBookMask(false);
		destArea.setConstants(ConstantsEnum.SYNTAX_STYLE_SQL);

		initHeadPanel();

		setHead(getBarPanel().getComp());
		set(getSplitPanel().getComp());
	}

	private void initHeadPanel() {
		List<String> srcTypeList = SqlConversionUtil.getEnumToStrList(SqlConversionUtil.getSrcTypeEnumList());
		srcBox = new DatabaseTypeComboBox(e -> {
			destBox.getComp().removeAllItems();
			List<String> destList = SqlConversionUtil.getEnumToStrList(SqlConversionUtil.getDestTypeList(DBTypeEnum.valueOf(srcBox.getValue())));
			for (String type : destList) {
				destBox.getComp().addItem(type);
			}
		}, srcTypeList);
		List<String> destList = SqlConversionUtil.getEnumToStrList(SqlConversionUtil.getDestTypeList(DBTypeEnum.valueOf(srcBox.getValue())));
		destBox = new DatabaseTypeComboBox(e -> {
		}, destList);
	}


	private HSplitPanel getSplitPanel() {
		HSplitPanel splitPane = new HSplitPanel(false);
		splitPane.setSplitWeight(0.5);

		LastPanel srcLastPanel = new LastPanel(false);
		HFlowPanel panel = new HFlowPanel();
		LabelInput srcLabel = new LabelInput(ToolUtil.getLang(ToolUtil.SOURCE));
		panel.add(srcLabel, srcBox);
		srcLastPanel.setHead(panel.getComp());
		srcLastPanel.set(srcArea.getComp());
		splitPane.setLastComp4One(srcLastPanel);


		LastPanel destLastPanel = new LastPanel(false);
		panel = new HFlowPanel();
		LabelInput destLabel = new LabelInput(ToolUtil.getLang(ToolUtil.DEST));
		panel.add(destLabel, destBox);
		destLastPanel.setHead(panel.getComp());
		destLastPanel.set(destArea.getComp());
		splitPane.setLastComp4Two(destLastPanel);

		if (HHSwingUi.isDarkSkin()) {
			srcArea.setTheme(ThemesEnum.monokai);
			destArea.setTheme(ThemesEnum.monokai);
		}
		return splitPane;
	}

	/**
	 * 初始化顶部按钮栏
	 *
	 * @return HBarPanel
	 */
	private HBarPanel getBarPanel() {
		HBarLayout barLayout = new HBarLayout();
		barLayout.setAlign(AlignEnum.LEFT);
		barLayout.setTopHeight(10);
		HBarPanel barPanel = new HBarPanel(barLayout);
		HButton conversionBtn = new HButton(ToolUtil.getLang(ToolUtil.CONVERSION));
		conversionBtn.addActionListener(e -> conversionAction());
		conversionBtn.setIcon(ToolUtil.getIcon(ToolUtil.SQL_CONVERSION));

		commonButtons = new CommonButtons();
		commonButtons.getFormatBtn().addActionListener(e -> formatSql());
		commonButtons.getCopyBtn().addActionListener(e -> {
			String destStr = destArea.getArea().getTextArea().getText();
			if (StringUtils.isNoneBlank(destStr)) {
				ClipboardUtil.putText(destStr);
				PopPaneUtil.info(ToolUtil.getLang("copy_success"));
			}
		});
		commonButtons.getClearBtn().addActionListener(e -> {
			destArea.setText("");
			srcArea.setText("");
		});
		barPanel.add(conversionBtn);
		for (AbsHComp button : commonButtons.getButtons()) {
			barPanel.add(button);
		}
		return barPanel;
	}

	/**
	 * 格式化sql
	 */
	private void formatSql() {
		try {
			String srcStr = srcArea.getArea().getTextArea().getText();
			if (StringUtils.isNoneBlank(srcStr)) {
				String srcSql = SqlFmtUtil.fmt2Str(srcStr, Integer.parseInt(commonButtons.getFormatBreakWidth()), 0);
				srcArea.setText(srcSql);
			}
			String destStr = destArea.getArea().getTextArea().getText();
			if (StringUtils.isNoneBlank(destStr)) {
				String destSql = SqlFmtUtil.fmt2Str(destStr, Integer.parseInt(commonButtons.getFormatBreakWidth()), 0);
				destArea.setText(destSql);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			PopPaneUtil.error(ex);
		}
	}

	/**
	 * 转换sql事件
	 */
	private void conversionAction() {
		try {
			if (srcBox.getValue().equals(destBox.getValue())) {
				return;
			}
			String str = srcArea.getArea().getTextArea().getText();
			if (StringUtils.isBlank(str)) {
				return;
			}
			destArea.setText("");
			if (!StringUtils.endsWith(str, SqlConversionUtil.SUFFIX)) {
				str += SqlConversionUtil.SUFFIX;
			}
			DBTypeEnum srcDbType = DBTypeEnum.valueOf(srcBox.getValue());
			DBTypeEnum destDbType = DBTypeEnum.valueOf(destBox.getValue());
			String convertSql = SqlConversionUtil.convert(srcDbType, destDbType, str);
			try {
				if (StringUtils.isNoneBlank(convertSql)) {
					convertSql = SqlFmtUtil.fmt2Str(convertSql);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			destArea.setText(convertSql);
			PopPaneUtil.info(ToolUtil.getLang(StringUtils.isBlank(convertSql) ? ToolUtil.CONVERSION_FAIL : ToolUtil.CONVERSION_SUCCESS));
		} catch (Exception exception) {
			exception.printStackTrace();
			PopPaneUtil.error(exception.getMessage());
		}
	}

	public HTextArea getSrcArea() {
		return srcArea;
	}

	public HTextArea getDestArea() {
		return destArea;
	}

}
