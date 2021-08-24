package com.hh.hhdb_admin.mgr.table.comp;

import com.hh.frame.lang.LangMgr;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.textEditor.base.ConstantsEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author oyx
 * @date 2020-10-29  0029 15:30:25
 */
public class SqlViewDialog {
	private final JDialog dialog;
	private final HTextArea textArea;
	private final static String LK_PREVIEW_SQL = "PREVIEW_SQL";
	private static final String DOMAIN_NAME = SqlViewDialog.class.getName();
	private final Component p;

	static {
		LangMgr.merge(DOMAIN_NAME, com.hh.frame.lang.LangUtil.loadLangRes(SqlViewDialog.class));
	}

	public SqlViewDialog(Component p) {
		this.p = p;
		textArea = new HTextArea(false, false);
		dialog = new JDialog(new JFrame(), true);
	}

	public SqlViewDialog() {
		this(null);
	}

	public void setSql(String sql) {
		textArea.setText(sql);
		textArea.setConstants(ConstantsEnum.SYNTAX_STYLE_SQL);
	}

	public void show() {
		dialog.setTitle(LangMgr.getValue(DOMAIN_NAME, LK_PREVIEW_SQL));
		dialog.setIconImage(IconFileUtil.getLogo().getImage());
		dialog.add(textArea.getComp());
		dialog.setSize(700, 500);
		dialog.setLocationRelativeTo(p);
		dialog.setVisible(true);
	}
}
