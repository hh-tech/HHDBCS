package com.hh.hhdb_admin.mgr.tool.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.parser.sql_fmt2.SqlFmtUtil;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.frame.swingui.view.util.ClipboardUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.ActionListener;
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
	private SelectBox srcBox,destBox;
	private CommonButtons commonButtons;
	private HFrame frame;


	public SqlConversionComp(HFrame frame) {
		this.frame = frame;
		init();
	}

	protected void init() {
		srcArea = new HTextArea();
		HTAreaConf conf = new HTAreaConf();
		conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
		srcArea.setConf(conf);

		destArea = new HTextArea();
		destArea.setConf(conf);

		initHeadPanel();

		setHead(getBarPanel().getComp());
		set(getSplitPanel().getComp());
	}

	private void initHeadPanel() {
		srcBox = new SelectBox(){
			@Override
			public void addListener(ActionListener l) {
				destBox.getComp().removeAllItems();
				List<String> destList = SqlConversionUtil.getEnumToStrList(SqlConversionUtil.getDestTypeList(DBTypeEnum.valueOf(srcBox.getValue())));
				for (String type : destList) {
					destBox.getComp().addItem(type);
				}
			}
		};
		List<String> srcTypeList = SqlConversionUtil.getEnumToStrList(SqlConversionUtil.getSrcTypeEnumList());
		srcTypeList.forEach(e -> srcBox.addOption(e, e, IconFileUtil.getDbIcon(DBTypeEnum.valueOf(e), IconSizeEnum.SIZE_16)));
		
		destBox = new SelectBox();
		List<String> destList = SqlConversionUtil.getEnumToStrList(SqlConversionUtil.getDestTypeList(DBTypeEnum.valueOf(srcBox.getValue())));
		destList.forEach(e -> destBox.addOption(e, e, IconFileUtil.getDbIcon(DBTypeEnum.valueOf(e), IconSizeEnum.SIZE_16)));
		
	}


	private HSplitPanel getSplitPanel() {
		HSplitPanel splitPane = new HSplitPanel(false);
		splitPane.setSplitWeight(0.5);

		LastPanel srcLastPanel = new LastPanel(false);
		HFlowPanel panel = new HFlowPanel();
		LabelInput srcLabel = new LabelInput(LangMgr.lang(Word1.SOURCE));
		panel.add(srcLabel, srcBox);
		srcLastPanel.setHead(panel.getComp());
		srcLastPanel.set(srcArea.getComp());
		splitPane.setLastComp4One(srcLastPanel);


		LastPanel destLastPanel = new LastPanel(false);
		panel = new HFlowPanel();
		LabelInput destLabel = new LabelInput(LangMgr.lang(Word2.TARGET));
		panel.add(destLabel, destBox);
		destLastPanel.setHead(panel.getComp());
		destLastPanel.set(destArea.getComp());
		splitPane.setLastComp4Two(destLastPanel);
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
		HButton conversionBtn = new HButton(LangMgr.lang(Word2.CONVERSION));
		conversionBtn.addActionListener(e -> conversionAction());
		conversionBtn.setIcon(FaSolidEnum.RETWEET,16);

		commonButtons = new CommonButtons(frame);
		commonButtons.getFormatBtn().addActionListener(e -> formatSql());
		commonButtons.getCopyBtn().addActionListener(e -> {
			String destStr = destArea.getText();
			if (StringUtils.isNoneBlank(destStr)) {
				ClipboardUtil.putText(destStr);
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
			String srcStr = srcArea.getText();
			if (StringUtils.isNoneBlank(srcStr)) {
				String srcSql = SqlFmtUtil.fmt2Str(srcStr, CsVarUtil.CONF.getSqlFormatWith(), 0);
				srcArea.setText(srcSql);
			}
			String destStr = destArea.getText();
			if (StringUtils.isNoneBlank(destStr)) {
				String destSql = SqlFmtUtil.fmt2Str(destStr, CsVarUtil.CONF.getSqlFormatWith(), 0);
				destArea.setText(destSql);
			}
		} catch (Exception e) {
			Lg.error(SqlConversionComp.class, e);
			PopDlgUtil.error(frame,e);
		}
	}

	/**
	 * 转换sql事件
	 */
	private void conversionAction() {
		try {
			String str = srcArea.getText();
			if (StringUtils.isBlank(str)) {
				return;
			}
			if (srcBox.getValue().equals(destBox.getValue())) {
				destArea.setText(str);
				return;
			}
			
			destArea.setText("");
			if (!StringUtils.endsWith(str, SqlConversionUtil.SUFFIX)) {
				str += SqlConversionUtil.SUFFIX;
			}
			DBTypeEnum srcDbType = DBTypeEnum.valueOf(srcBox.getValue());
			DBTypeEnum destDbType = DBTypeEnum.valueOf(destBox.getValue());
			StringBuilder errorSql = new StringBuilder();
			String convertSql = SqlConversionUtil.convert(srcDbType, destDbType, str,errorSql);
			try {
				if (StringUtils.isNoneBlank(convertSql)) {
					convertSql = SqlFmtUtil.fmt2Str(convertSql);
				}
			} catch (Exception e) {
				Lg.error(SqlConversionComp.class, e);
			}
			destArea.setText(convertSql);
			if (errorSql.length() > 0) PopDlgUtil.info(frame, errorSql.toString());
		} catch (Exception e) {
			Lg.error(SqlConversionComp.class, e);
			PopDlgUtil.error(frame,e);
		}
	}
}
