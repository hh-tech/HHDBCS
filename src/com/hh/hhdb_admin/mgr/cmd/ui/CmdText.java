package com.hh.hhdb_admin.mgr.cmd.ui;

import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.dbobj2.kword.KeyWordUtil;
import com.hh.frame.json.JsonArray;
import com.hh.frame.swingui.view.textEditor.base.BaseTextArea;
import com.hh.frame.swingui.view.textEditor.base.ThemesEnum;
import com.hh.frame.swingui.view.textEditor.rSyntaxTextArea.ui.rsyntaxtextarea.RSyntaxTextArea;
import com.hh.frame.swingui.view.textEditor.rSyntaxTextArea.ui.rtextarea.RTextScrollPane;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditUtil;
import com.hh.hhdb_admin.common.util.textEditor.tooltip.Tooltip;
import com.hh.hhdb_admin.mgr.cmd.CmdComp;
import com.hh.hhdb_admin.mgr.cmd.CmdMgr;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.sql.Connection;

public abstract class CmdText {
	private boolean fromInputMethod = true;
	private CmdComp cmdcomp;
	private RTextScrollPane scrollPane;
	private RSyntaxTextArea textArea;
	private Document doc = null;
	public Tooltip tip;		//提示工具
	
	private int caretOffset = 0;
	private int startOffset = 0;
	
	private JsonArray jsonValues;
	private Connection conns; //查询提示词连接

	public CmdText(CmdComp cmd){
		this.cmdcomp = cmd;
		BaseTextArea bta = new BaseTextArea(false);
		textArea = bta.getTextArea();
		textArea.setSyntaxEditingStyle("text/plain");
		textArea.getPopupMenu().removeAll();
		if(HHSwingUi.isDarkSkin()) {
			bta.setTheme(ThemesEnum.monokai.toString() + ".xml");
        }
		
		JMenuItem copyItem = new JMenuItem(CmdMgr.getLang("copy"));
		copyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copy();
			}
		});
		JMenuItem pasteItem = new JMenuItem(CmdMgr.getLang("paste"));
		pasteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				paste();
			}
		});
		textArea.getPopupMenu().add(copyItem);
		textArea.getPopupMenu().add(pasteItem);
		textArea.addKeyListener(new CmdKeyListener(this));
		
		doc = textArea.getDocument();//
		scrollPane = new RTextScrollPane(textArea);
		//添加提示框
		try {
			tip = QueryEditUtil.getQueryTooltip(textArea,"q");
			tip.setAutomatic(false);
			tip.setJdbc(cmdcomp.getJdbc());
			setKeyWord();    //置常关键字
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		doc.addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				caretOffset = textArea.getCaretPosition();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (fromInputMethod) 
				{
					caretOffset += e.getLength();
				}
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
//				System.out.println("changedUpdate");
			}
		});
		// 输入法监控事件
		textArea.addInputMethodListener(new InputMethodListener() {
			@Override
			public void inputMethodTextChanged(InputMethodEvent event) {
				if (null != event.getText()) {
					fromInputMethod = false;
				}
				if (event.getCommittedCharacterCount() > 0) {
					if (startOffset > textArea.getCaretPosition()) {
						event.consume();
					}
					fromInputMethod = true;
				}
			}
			@Override
			public void caretPositionChanged(InputMethodEvent event) {
			}
		});
	}

	public void replaceSql(String newSql) {
		try {
			doc.remove(startOffset, doc.getLength() - startOffset);
			textArea.append(newSql);
			textArea.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
			PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
		}
	}

	/**
	 * 接收消息
	 * 
	 * @param str
	 */
	public void recv(String str) {		
		try {
//			if (enter) {
//				textArea.append("\n");
//			}
			textArea.append(str);
			textArea.setCaretPosition(doc.getLength());
		} catch (Throwable t) {
			t.printStackTrace();
			PopPaneUtil.error(StartUtil.parentFrame.getWindow(), t.getMessage());
		}
	}

	/**
	 * 接收头部显示的信息。
	 * 
	 * @param top
	 */
	public void recvTop(String top) {		
		try {
//			if (enter) {
//				textArea.append("\n");
//			}
			textArea.append(top + " ");
			textArea.setCaretPosition(doc.getLength());
		} catch (Throwable t) {
			t.printStackTrace();
			PopPaneUtil.error(StartUtil.parentFrame.getWindow(), t.getMessage());
		}
		this.startOffset = doc.getLength();
	}

	/**
	 * 获取当前输入的SQL
	 * 
	 * @return
	 */
	public String getCurCmd() {
		try {
			return doc.getText(startOffset, doc.getLength() - startOffset);
		} catch (BadLocationException e) {
			e.printStackTrace();
			PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
			return "";
		}
	}
	
	/**
	 * 切换模式
	 * @throws Exception
	 */
	public void setKeyWord() throws Exception{
		//强制关闭之前查询的连接，防止表过多查询过久的情况
		ConnUtil.close(conns);
		conns = ConnUtil.getConn(cmdcomp.getJdbc());
		jsonValues = new JsonArray();
		//先设置常用的关键字
		jsonValues = KeyWordUtil.getKeyWordJson(conns);
		tip.setkeyword(jsonValues);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					KeyWordUtil.getDbObjectJson(jsonValues,conns, cmdcomp.getJdbc().getSchema(),"table"); //先设置表提示
					tip.setkeyword(jsonValues);
					KeyWordUtil.getDbObjectJson(jsonValues,conns, cmdcomp.getJdbc().getSchema(),"view");
					tip.setkeyword(jsonValues);
					KeyWordUtil.getDbObjectJson(jsonValues,conns, cmdcomp.getJdbc().getSchema(),"mview");
					tip.setkeyword(jsonValues);
					KeyWordUtil.getDbObjectJson(jsonValues,conns, cmdcomp.getJdbc().getSchema(),"function","procedure");
					tip.setkeyword(jsonValues);
					KeyWordUtil.getDbObjectJson(jsonValues,conns, cmdcomp.getJdbc().getSchema(),"synonym");
					tip.setkeyword(jsonValues);
					KeyWordUtil.getDbObjectJson(jsonValues,conns, cmdcomp.getJdbc().getSchema(),"pack");
					tip.setkeyword(jsonValues);
				} catch (Exception e) {
					e.printStackTrace();
					PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
				}finally {
					ConnUtil.close(conns);
					jsonValues = new JsonArray();
				}
			}
		}).start();
	}
	
	
	/**
	 * 上键获取历史SQL
	 */
	public abstract void up();
	/**
	 * 下键获取历史SQL
	 */
	public abstract void down();
	/**
	 * 复制
	 */
	public abstract void copy();
	/**
	 * 粘贴
	 */
	public abstract void paste();
	/**
	 * Ctrl+Z撤销
	 */
	public abstract void keyCancel();
	/**
	 * 发送消息
	 * @param sql
	 */
	public abstract void send(String sql);
	
	
	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public void setEditable(boolean edit) {
		textArea.setEditable(edit);
	}

	public int getCaretOffset() {
		return caretOffset;
	}

	public void setCaretOffset(int caretOffset) {
		this.caretOffset = caretOffset;
	}

	public Document getDoc() {
		return doc;
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public int getStartOffset() {
		return startOffset;
	}
}
