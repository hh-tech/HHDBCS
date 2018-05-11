package com.hhdb.csadmin.common.ui.textEdit;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.text.Element;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.hhdb.csadmin.common.bean.DefaultSet;
import com.hhdb.csadmin.common.bean.DefaultSetting;

public class QueryEditorUi {
	//编辑面板
	private RSyntaxTextArea textArea;
	//带行号的有滚动条的面板
	private RTextScrollPane scrollpane;
	//基础面板
	private JPanel contentPane;
	
	//
	private List<String> keylist;
	private List<String> tablelist = new ArrayList<String>();
	private List<String> viewlist = new ArrayList<String>();

	/**
	 * 获得编辑面板
	 * @return
	 */
	public RSyntaxTextArea getTextArea() {
		return textArea;
	}
	/**
	 * 获得基础面板
	 * @return
	 */
	public JPanel getContentPane() {
		return contentPane;
	}

	public QueryEditorUi() {
		textArea = new RSyntaxTextArea(20, 60);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
		textArea.setCodeFoldingEnabled(true);
		
		DefaultSet textpaneSet = DefaultSetting.loadFontSettings();
		keylist = textpaneSet.getKeylist();
		Font font = new Font("宋体", Font.PLAIN, Integer.parseInt(textpaneSet
				.getFontSize()));
		textArea.setFont(font);
		textArea.setBackground(DefaultSetting.strToColor(textpaneSet.getBackground()));
		
		scrollpane = new RTextScrollPane(textArea);
		contentPane = new JPanel(new BorderLayout());
		contentPane.add(scrollpane);
		setKeyCompletionProvider(keylist);
	}

	/**
	 * 设置关键字提示
	 * @param keylist
	 */
	private void setKeyCompletionProvider(List<String> keylist) {
		DefaultCompletionProvider provider = new DefaultCompletionProvider();
		for (String key : keylist) {
			provider.addCompletion(new BasicCompletion(provider, key));
		}
		AutoCompletion ac = new AutoCompletion(provider);
		ac.install(textArea);
		KeyStroke ks = KeyStroke.getKeyStroke('K', InputEvent.ALT_DOWN_MASK);
		ac.setTriggerKey(ks);
	}

	/**
	 * 设置表名提示
	 * @param tablenames
	 */
	public void setTableCompletionProvider(List<String> tablenames) {
		tablelist.clear();
		tablelist.addAll(tablenames);
		DefaultCompletionProvider provider = new DefaultCompletionProvider();
		for (String key : keylist) {
			provider.addCompletion(new BasicCompletion(provider, key));
		}
		for (String key : tablenames) {
			provider.addCompletion(new BasicCompletion(provider, key));
		}
		for (String key : viewlist) {
			provider.addCompletion(new BasicCompletion(provider, key));
		}
		AutoCompletion ac = new AutoCompletion(provider);
		ac.install(textArea);
		KeyStroke ks = KeyStroke.getKeyStroke('T', InputEvent.ALT_DOWN_MASK);
		ac.setTriggerKey(ks);
	}
 
	/**
	 * 设置视图名提示
	 * @param viewnames
	 */
	public void setViewCompletionProvider(List<String> viewnames) {
		viewlist.clear();
		viewlist.addAll(viewnames);
		DefaultCompletionProvider provider = new DefaultCompletionProvider();
		for (String key : viewnames) {
			provider.addCompletion(new BasicCompletion(provider, key));
		}
		for (String key : keylist) {
			provider.addCompletion(new BasicCompletion(provider, key));
		}
		for (String key : tablelist) {
			provider.addCompletion(new BasicCompletion(provider, key));
		}
		AutoCompletion ac = new AutoCompletion(provider);
		ac.install(textArea);
		KeyStroke ks = KeyStroke.getKeyStroke('W', InputEvent.ALT_DOWN_MASK);
		ac.setTriggerKey(ks);
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
