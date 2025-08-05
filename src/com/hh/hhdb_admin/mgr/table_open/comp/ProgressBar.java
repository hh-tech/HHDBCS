package com.hh.hhdb_admin.mgr.table_open.comp;

import com.hh.frame.swingui.view.abs.AbsHComp;

import javax.swing.*;

/**
 * @author ouyangxu
 * @date 2021-11-30 0030 14:21:01
 * @description 进度条
 */
public class ProgressBar extends AbsHComp {
	protected JProgressBar progressBar;

	public ProgressBar() {
		this(null);
	}

	public ProgressBar(String id) {
		super(id);
		init();
	}

	public void setValue(int n) {
		progressBar.setValue(n);
	}

	public void setIndeterminate(boolean b) {
		progressBar.setIndeterminate(b);
	}

	public void setString(String s) {
		progressBar.setString(s);
	}

	protected void init() {
		comp = progressBar = new JProgressBar();
		progressBar.setBorder(BorderFactory.createEmptyBorder());
	}

	@Override
	public JProgressBar getComp() {
		return progressBar;
	}
}
