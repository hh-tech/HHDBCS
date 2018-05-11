package com.hhdb.csadmin.plugin.backup_recovery;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.JDialog;

import com.hhdb.csadmin.common.ui.BaseFrame;
import com.hhdb.csadmin.plugin.backup_recovery.ui.BackUpAndRestore;

/**
 * 对话框面板
 */
public class BackupRecoveryPanel extends JDialog implements WindowListener {
	
	private static final long serialVersionUID = 1L;
	private JComponent component;
	
	public BackupRecoveryPanel(String title,int width, int height,JComponent comp,BaseFrame bf){
		super(bf,title);
		component=comp;
		add(comp);
		setSize(width, height);
		setModal(true);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		if(component instanceof BackUpAndRestore){
			((BackUpAndRestore) component).closeTask();
		}
	}

}
