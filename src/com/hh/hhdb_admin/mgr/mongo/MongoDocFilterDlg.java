package com.hh.hhdb_admin.mgr.mongo;

import com.hh.frame.create_dbobj.treeMr.base.MongoDocGroupNode;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HWindow;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.IntSpinner;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;


public class MongoDocFilterDlg extends HDialog {
	private IntSpinner maxNum=new IntSpinner();
	private TextAreaInput area=new TextAreaInput();
	private MongoDocGroupNode objNode;
	
	public MongoDocFilterDlg() {
		super(CsVarUtil.parentFrame,HWindow.SMALL_WIDTH);
		HBarPanel bar=new HBarPanel();
		bar.add(new LabelInput("最多加载"));
		bar.add(maxNum,100);
		setToolBar(bar);
		LastPanel panel=new LastPanel();
		panel.setTitle("查询条件(Mongo数据库语法json格式)");
		panel.setWithScroll(area.getComp());
		setRootPanel(panel);
	}
	protected void onConfirm() {
		objNode.setQueryText(area.getValue());
		objNode.setMaxKeyLoad(maxNum.getInt());
		CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(objNode.getRefreshId()));
	}
	protected void onCancel() {
		this.hide();
	}
	
	public void show(MongoDocGroupNode objNode) {
		this.objNode=objNode;
		maxNum.setValue(objNode.getMaxKeyLoad());
		area.setValue(objNode.getQueryText());
		super.show();
	}
}
