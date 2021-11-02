package com.hh.hhdb_admin.mgr.table.comp;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JDialog;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.textEditor.base.ConstantsEnum;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.query.QueryMgr;
import com.hh.hhdb_admin.mgr.table.TableComp;

/**
 * @author oyx
 * @date 2020-10-29  0029 15:30:25
 */
public class SqlViewDialog {
	private HDialog dialog;
	private HTextArea textArea;
	private final static String LK_PREVIEW_SQL = "PREVIEW_SQL";
	private final static String LK_TO_QUERY = "TO_QUERY";
	private final static String LK_CANCEL = "CANCEL";
	private static final String DOMAIN_NAME = SqlViewDialog.class.getName();
//	private final Component p;
	private HBarPanel barPanel;

	static {
		try {
            LangMgr2.loadMerge(SqlViewDialog.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public SqlViewDialog(Component p) {
//		this.p = p;
		init();
	}

	private void init() {
		textArea = new HTextArea(false, true);
		dialog = new HDialog(TableComp.dialog, 700, 580);
		HBarLayout barLayout = new HBarLayout();
		barLayout.setAlign(AlignEnum.RIGHT);
		barLayout.setTopHeight(10);
		barLayout.setBottomHeight(10);
		barPanel = new HBarPanel(barLayout);
		HButton toQuery = new HButton(LangMgr2.getValue(DOMAIN_NAME, LK_TO_QUERY)) {
			@Override
			protected void onClick() {
				StartUtil.eng.doPush(CsMgrEnum.QUERY, GuiJsonUtil.toJsonCmd(QueryMgr.CMD_SHOW_QUERY).add("text", textArea.getArea().getTextArea().getText()));
				dialog.hide();
			}
		};
		HButton cancel = new HButton(LangMgr2.getValue(DOMAIN_NAME, LK_CANCEL)) {
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
		textArea.setConstants(ConstantsEnum.SYNTAX_STYLE_SQL);
	}

	public void show() {
		dialog.setIconImage(IconFileUtil.getLogo().getImage());
		((JDialog) dialog.getWindow()).setTitle(LangMgr2.getValue(DOMAIN_NAME, LK_PREVIEW_SQL));
		LastPanel lastPanel = new LastPanel();
		lastPanel.set(textArea.getComp());
		lastPanel.setFoot(barPanel.getComp());

		HPanel rootPanel = new HPanel();
		rootPanel.setLastPanel(lastPanel);
		dialog.setRootPanel(rootPanel);
		((JDialog) dialog.getWindow()).setResizable(true);
		dialog.show();
	}
}
