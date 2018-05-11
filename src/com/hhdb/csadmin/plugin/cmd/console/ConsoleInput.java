package com.hhdb.csadmin.plugin.cmd.console;

import java.util.LinkedList;

import javax.swing.JTextPane;

/**
 * 
 * <p>
 * Description: 字符sql窗口输入组件对象
 * </p>
 * <p>
 * Company: 恒辉
 * </p>
 * 
 * @author 张涛
 * @version 创建时间：2017年10月30日 上午11:08:00
 */
public class ConsoleInput extends JTextPane {
	private static final long serialVersionUID = 1L;
	private static final String MORE_PROMPT = "-- More --";
	//命令历史游标
	private int historyIndex;
	//命令历史集合
	private LinkedList<String> historyLines = new LinkedList<String>();

	ConsoleInput() {
	}
	
	void setHistoryLines(int index) {
		setText(historyLines.get(historyIndex));
	}

	int getHLineSize() {
		return historyLines.size();
	}
	
	//存入命令历史集合，游标设置为最新命令
	void addHLine(String line) {
		historyLines.add(line);
		historyIndex = historyLines.size();
	}
	
	// 未打印完
	void setMoreToText() {
		setText(MORE_PROMPT);
	}
	
	// 通过输入文本判断是否打印完
	boolean beMore() {
		String input = getText();
		if (input != null) {
			input = input.replace("\n", "");
		}
		return MORE_PROMPT.equals(input);
	}
	
	//上翻命令历史
	void upPressed() {
		if (beMore()) {
			return;
		}
		if (historyIndex > 0) {
			historyIndex--;
			setHistoryLines(historyIndex);
		}
	}

	//下翻命令历史
	void downPressed() {
		// 没有打印完毕时，不能使用
		if (beMore()) {
			return;
		}
		int size = getHLineSize();
		if (historyIndex < size)
			historyIndex++;
		if (historyIndex == size)
			setText(null);
		else {
			setHistoryLines(historyIndex);
		}
	}

}
