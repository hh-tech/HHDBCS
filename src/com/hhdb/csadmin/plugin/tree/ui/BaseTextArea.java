package com.hhdb.csadmin.plugin.tree.ui;

import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class BaseTextArea extends JTextArea {

	private static final long serialVersionUID = 1L;

	public BaseTextArea(int length) {
		setDocument(new LimitNumDocument(length));
		init();
	}

	public void setRowAsColumn(int rows, int columns) {
		setRows(rows);
		setColumns(columns);
	}

	private void init() {
		setLineWrap(true);// 激活自动换行功能
		setWrapStyleWord(true);// 激活断行不断字功能
	}

	private class LimitNumDocument extends PlainDocument {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int fLength = -1; // 可任意输入

		public LimitNumDocument(int length) {
			fLength = length;
		}

		public void insertString(int offs, String str, AttributeSet attr) throws BadLocationException {
			int originalLength = getLength();
			if (originalLength <= 0) {
				super.insertString(offs, str, attr);
				return;
			}
			char[] input = str.toCharArray();
			int inputLength = 0;
			for (int i = 0; i < input.length; i++) {
				if (originalLength + inputLength >= fLength) {
					break;
				}
				inputLength++;
			}
			super.insertString(offs, new String(input, 0, inputLength), attr);
		}
	}
}
