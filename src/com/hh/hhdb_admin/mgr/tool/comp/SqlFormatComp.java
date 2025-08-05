package com.hh.hhdb_admin.mgr.tool.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.frame.swingui.view.util.ClipboardUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
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
	private HFrame frame;

	public SqlFormatComp(HFrame frame) {
		super(false);
		this.frame = frame;
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
		HTAreaConf conf = new HTAreaConf();
		conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
		sqlArea.setConf(conf);
		set(sqlArea.getComp());
	}

	protected List<AbsHComp> initToolBarBtn() {
		commonButtons = new CommonButtons(frame);
		commonButtons.getFormatBtn().addActionListener(e -> GlobalWorkUtil.formatSql(sqlArea));
		commonButtons.getCopyBtn().addActionListener(e -> copyValue());
		commonButtons.getClearBtn().addActionListener(e -> sqlArea.setText(""));
		return commonButtons.getButtons();
	}

	private void copyValue() {
		try {
			String srcStr = sqlArea.getText();
			if (StringUtils.isNoneBlank(srcStr)) {
				ClipboardUtil.putText(srcStr);
			}
		} catch (Exception e) {
			Lg.error(SqlFormatComp.class, e);
			PopDlgUtil.error(frame,e);
		}
	}


	public HTextArea getSqlArea() {
		return sqlArea;
	}


}
