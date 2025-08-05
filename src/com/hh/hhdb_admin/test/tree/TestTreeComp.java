package com.hh.hhdb_admin.test.tree;

import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.create_dbobj.treeMr.base.ViewType;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.CsTreeComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TestTreeComp {
	private static SelectBox viewType ;
	public static void main(String[] args) throws Exception {
		MgrTestUtil.initConfLang();
		MgrTestUtil.initCsVarJdbc();
		HBarPanel bar = new HBarPanel();
		bar.add(new LabelInput("视图类型"));
		viewType = new SelectBox();
		viewType.addOption("普通用户视图", ViewType.USER.name());
		viewType.addOption("管理员视图", ViewType.DBA.name());
		viewType.addListener(e -> {
			try {
				load();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		bar.add(viewType);
		CsVarUtil.parentFrame.setToolBar(bar);
		load() ;
		CsVarUtil.parentFrame.show();

	}
	private static void load() throws Exception {
		CsVarUtil.jdbc.setView(JdbcInfo.View.valueOf(viewType.getValue()));
		CsTreeComp treeComp = CsTreeComp.newTreeInstance();
		CsVarUtil.parentFrame.setRootPanel(treeComp);	
	}
}
