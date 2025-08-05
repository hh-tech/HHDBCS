package com.hh.hhdb_admin.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.bean.QueryBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author oyx
 * @date 2020-10-29  0029 15:30:25
 */
public class SqlViewDialog {
	private HDialog dialog;
	private HTextArea textArea;
	//	private final Component p;
	private HBarPanel barPanel;


	public SqlViewDialog(Component p) {
//		this.p = p;
		init();
	}

	private void init() {
		textArea = new HTextArea();
		HTAreaConf conf = new HTAreaConf();
		conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
		textArea.setConf(conf);
		
		dialog = new HDialog(new HDialog(CsVarUtil.parentFrame, HFrame.LARGE_WIDTH, HFrame.LARGE_WIDTH / 4 * 3), 700, 580);
		HBarLayout barLayout = new HBarLayout();
		barLayout.setAlign(AlignEnum.RIGHT);
		barLayout.setTopHeight(10);
		barLayout.setBottomHeight(10);
		barPanel = new HBarPanel(barLayout);
		HButton toQuery = new HButton(LangMgr.sLang(CsL.TO_QUERY)) {
			@Override
			protected void onClick() {
				CsVarUtil.eng2.doCall(CsMgrEnum.QUERY, new QueryBean(textArea.getText()));
				dialog.hide();
			}
		};
		HButton cancel = new HButton(LangMgr.lang(Word2.CANCEL)) {
			@Override
			protected void onClick() {
				dialog.hide();
			}
		};
		barPanel.add(toQuery, cancel);
	}

	public SqlViewDialog() {
		this(null);
	}

	public void setSql(String sql) {
		textArea.setText(sql);
	}

	public void show() {
		show(false);
	}

	public void show(boolean formatSql) {
		if (formatSql)
			GlobalWorkUtil.formatSql(textArea);

		dialog.setIconImage(IconFileUtil.getLogo());
		((JDialog) dialog.getWindow()).setTitle(LangMgr.lang(Word2.PREVIEW));
		LastPanel lastPanel = new LastPanel();
		lastPanel.set(textArea.getComp());
		lastPanel.setFoot(barPanel.getComp());

		HPanel rootPanel = new HPanel();
		rootPanel.setLastPanel(lastPanel);
		dialog.setRootPanel(rootPanel);
		((JDialog) dialog.getWindow()).setResizable(true);
		dialog.setStatusBar(new HBarPanel());
		dialog.show();
	}
}
