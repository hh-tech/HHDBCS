package com.hhdb.csadmin.common.ui.textEdit;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.text.Element;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.hh.frame.swingui.swingcontrol.textEdit.QueryEditorTextArea;
import com.hhdb.csadmin.common.bean.DefaultSet;
import com.hhdb.csadmin.common.bean.DefaultSetting;

public class QueryEditorUi2 {
	//编辑面板
	private QueryEditorTextArea textArea;
	//带行号的有滚动条的面板
	private RTextScrollPane scrollpane;
	//基础面板
	private JPanel contentPane;

	/**
	 * 获得编辑面板
	 * @return
	 */
	public QueryEditorTextArea getTextArea() {
		return textArea;
	}
	/**
	 * 获得基础面板
	 * @return
	 */
	public JPanel getContentPane() {
		return contentPane;
	}

	public QueryEditorUi2() {
		textArea = new QueryEditorTextArea(20, 60);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
		textArea.setCodeFoldingEnabled(true);
		scrollpane = new RTextScrollPane(textArea);
		contentPane = new JPanel(new BorderLayout());
		contentPane.add(scrollpane);
		setTextPanelSet(DefaultSetting.loadFontSettings());
	}
	
	public void setTextPanelSet(DefaultSet textpaneSet){
		textArea.keylist = textpaneSet.getKeylist();
		Font font = new Font("宋体", Font.PLAIN, Integer.parseInt(textpaneSet
				.getFontSize()));
		textArea.setFont(font);
		textArea.setBackground(DefaultSetting.strToColor(textpaneSet.getBackground()));
		textArea.qkeyguanjian = textpaneSet.getQkeyguanjian();
		textArea.qkeytablename = textpaneSet.getQkeytablename();
		textArea.qkeyviewname = textpaneSet.getQkeyviewname();
	}

	/**
	 * 设置表名提示
	 * @param tablenames
	 */
	public void setTableCompletionProvider(List<String> tablenames) {
		textArea.setTablelist(tablenames);
	}
 
	/**
	 * 设置视图名提示
	 * @param viewnames
	 */
	public void setViewCompletionProvider(List<String> viewnames) {
		textArea.setViewlist(viewnames);
	}

	/**
	 * 获取编辑面板里的内容
	 * @return
	 */
	public String getText() {
		return textArea.getText();
	}
	/**
	 * 获取编辑面板选中的内容
	 * @return
	 */
	public String getSelectedText() {
		return textArea.getSelectedText();
	}

	/**
	 * 设置编辑面板的内容
	 * @param text
	 */
	public void setText(String text) {
		textArea.setText(text);
	}

	/**
	 * 根据位置获取行号
	 * @param position
	 * @return
	 */
	public int getLineByPosition(int position) {
		Element root = textArea.getDocument().getDefaultRootElement();
		int line = root.getElementIndex(position) + 1;
		return line;
	}
}
