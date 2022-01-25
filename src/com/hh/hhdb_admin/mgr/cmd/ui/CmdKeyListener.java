package com.hh.hhdb_admin.mgr.cmd.ui;

import javax.swing.text.BadLocationException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
public class CmdKeyListener extends KeyAdapter {

	CmdText cmdtext;
	
	public CmdKeyListener(CmdText cmdtext) {
		this.cmdtext = cmdtext;
	}
	@Override
	public void keyPressed(KeyEvent e) {
		if (!cmdtext.tip.isVisible()) {
			if (e.getKeyCode() == KeyEvent.VK_CONTROL || (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C)) {
			} else {
				if (cmdtext.getCaretOffset() != cmdtext.getTextArea().getCaretPosition()) {
					if (cmdtext.getCaretOffset() > cmdtext.getDoc().getLength()) {
						cmdtext.setCaretOffset(cmdtext.getDoc().getLength());
					}
					cmdtext.getTextArea().setCaretPosition(cmdtext.getCaretOffset());
				}
				if (e.getKeyCode() == KeyEvent.VK_CONTROL || (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V)) {
					e.consume();
					cmdtext.paste();
				}
				if (e.getKeyCode() == KeyEvent.VK_CONTROL || (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z)) {
					e.consume();
					cmdtext.keyCancel();
				}
				if (e.getKeyCode() == KeyEvent.VK_CONTROL || (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_D)) {
					e.consume();
				}
			}
		}
		
		switch (e.getKeyCode()) {
			case KeyEvent.VK_DOWN:
				if (!cmdtext.tip.isVisible()) {
					e.consume();
					cmdtext.down();
				}
				break;
			case KeyEvent.VK_UP:
				if (!cmdtext.tip.isVisible()) {
					e.consume();//
					cmdtext.up();
				}
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_BACK_SPACE:
				if(cmdtext.getStartOffset()>=cmdtext.getTextArea().getCaretPosition()) {
					e.consume();
				}
				break;
			case KeyEvent.VK_ENTER:
				if (!cmdtext.tip.isVisible()) {
					cmdtext.setCaretOffset(cmdtext.getDoc().getLength());
					try {
						cmdtext.send(cmdtext.getDoc().getText(cmdtext.getStartOffset(), cmdtext.getDoc().getLength()-cmdtext.getStartOffset()));
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
					e.consume();
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
			
			case KeyEvent.VK_RIGHT:

//		case KeyEvent.VK_BACK_SPACE:
				cmdtext.setCaretOffset(cmdtext.getTextArea().getCaretPosition());
				break;
			case KeyEvent.VK_BACK_SPACE:
				cmdtext.setCaretOffset(cmdtext.getTextArea().getCaretPosition());
				break;
			default:
				break;
		}
	}

}
