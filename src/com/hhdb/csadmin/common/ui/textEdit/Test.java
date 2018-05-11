package com.hhdb.csadmin.common.ui.textEdit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import com.hh.frame.swingui.swingcontrol.textEdit.QueryEditorTextArea;

public class Test {

	public static void showQueryEditorScrollPanel() {
		JFrame frame = new JFrame();
		QueryEditorUi qui = new QueryEditorUi();
		qui.setText("This is a demonstration of...\n...line numbering using a JText area within...\n...a JScrollPane\n");
		frame.add(qui.getContentPane());
		frame.pack();
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static void test(){
		JFrame frame = new JFrame();
		QueryEditorTextArea textArea = new QueryEditorTextArea();
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
	      textArea.setCodeFoldingEnabled(true);
	      textArea.setText("This is a demonstration of...\n...line numbering using a JText area within...\n...a JScrollPane\n");
		frame.add(textArea);
		frame.pack();
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static void main(String[] args) throws Exception {

		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				test();
				//showQueryEditorScrollPanel();
			}

		});

	}
}
