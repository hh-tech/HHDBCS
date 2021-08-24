package com.hh.hhdb_admin.test;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HFrame;

public abstract class AbsMainTestComp {
	protected HFrame tFrame=new HFrame();
	private final HDialog dialog=new HDialog(tFrame,HDialog.MIDDLE_WIDTH,false);
	public abstract void init();

	public void show() {
		tFrame.show();
	}

	public HDialog getDialog() {
		return this.dialog;
	}
}
