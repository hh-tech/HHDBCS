package com.hh.hhdb_admin.comp.column.test;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.comp.column.*;

/*
* Author:Zhang Dagang
* Date:2024年2月20日
*/
public class TestDataTypePanel {

	public static void main(String[] args) {
		HFrame frame=new HFrame();
		DataTypePanel panel=new DataTypePanel(DBTypeEnum.oracle,null);

		HBarPanel bar=new HBarPanel();
		HButton hbtn=new HButton("取值") {
			@Override
			public void onClick() {
				PopDlgUtil.info(panel.getFullTypeDefine());
			}
		};
		bar.add(hbtn);
		frame.setToolBar(bar);
		
		frame.setRootPanel(panel);
		frame.show();

	}

}
