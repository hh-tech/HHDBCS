package com.hhdb.csadmin.common.ui.textEdit;

import java.awt.Font;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import com.hhdb.csadmin.common.bean.DefaultSet;
import com.hhdb.csadmin.common.bean.DefaultSetting;


public class QueryTextPane extends RSyntaxTextArea{
	private static final long serialVersionUID = 1L;
	public QueryTextPane() {
		// 加载sql关键字
		DefaultSet textpaneSet = DefaultSetting.loadFontSettings();
		Font font = new Font("宋体", Font.PLAIN, Integer.parseInt(textpaneSet
				.getFontSize()));
		setFont(font);
		setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
		setEditable(false);
	}
}