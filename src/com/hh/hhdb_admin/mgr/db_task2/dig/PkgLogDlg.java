package com.hh.hhdb_admin.mgr.db_task2.dig;

import com.hh.frame.common.base.LogBean;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaRegularEnum;
import com.hh.frame.swingui.view.textarea2.HTextArea;

import java.awt.*;

public class PkgLogDlg extends HDialog {
	private HTextArea area=new HTextArea();
	protected LogBean log;
	public PkgLogDlg(Component parent, LogBean log) {
		super(parent,800,600,false);
		this.log=log;
		this.setOkBtnText("关闭");
		this.removeCancleBtn();
		HButton refreshBtn=new HButton("刷新") {
			@Override
			public void onClick() {
				refreshInform();
				setLog();
			}
		};
		refreshBtn.setIcon(FaRegularEnum.CIRCLE_PLAY,16);
		stBar.add(refreshBtn);
		LastPanel p=new LastPanel();
		p.set(area.getComp());
		this.setRootPanel(p);
	}
	@Override
	public void show() {
		super.show();
		setLog();
	}
	
	private void setLog() {
		area.setText(log.toString());
	}
	
	protected void refreshInform() {
	
	}
}
