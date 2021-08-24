package com.hh.hhdb_admin.test.function;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.lang.LangUtil;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.function.FunctionComp;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import com.hh.hhdb_admin.test.MgrTestUtil;

import javax.swing.*;
import java.io.File;

public class FunCompTest {

	public static void main(String[] args) throws Exception {
		IconFileUtil.setIconBaseDir(new File("etc/icon/"));
		LangMgr.merge(FunctionMgr.class.getName(), LangUtil.loadLangRes(FunctionMgr.class));

		HHSwingUi.init();

		HFrame frame = new HFrame();
		HDivLayout layout = new HDivLayout(20, 30, GridSplitEnum.C12);
		layout.setxBorderWidth(20);


		HPanel panel = new HPanel(layout);
		HButton createPanel = new HButton() {
			@Override
			protected void onClick() {
				try {
					FunctionComp fun = new FunctionComp(MgrTestUtil.getJdbcBean(),MgrTestUtil.getJdbcBean().getSchema());
					fun.show(TreeMrType.FUNCTION.name());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		createPanel.setText("添加函数");
		panel.add(createPanel);
		HButton update = new HButton() {
			@Override
			protected void onClick() {
				try {
					String sql = null;
					FunctionComp fun = new FunctionComp(MgrTestUtil.getJdbcBean(),MgrTestUtil.getJdbcBean().getSchema());
					if (DriverUtil.getDbType(MgrTestUtil.getJdbcBean()) == DBTypeEnum.hhdb || DriverUtil.getDbType(MgrTestUtil.getJdbcBean()) == DBTypeEnum.pgsql
							|| DriverUtil.getDbType(MgrTestUtil.getJdbcBean()) == DBTypeEnum.db2) {
						sql = JOptionPane.showInputDialog(null,"请输入函数名称与id或别名（,分隔）：","");
					}else {
						sql = JOptionPane.showInputDialog(null,"请输入函数名称：","");
					}
					if(sql==null) return;
					if (sql.contains(",")) {
						fun.show(sql.split(",")[0],sql.split(",")[1],TreeMrType.FUNCTION.name());
					}else {
						fun.show(sql,"",TreeMrType.FUNCTION.name());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		update.setText("修改函数");
		panel.add(update);
		frame.setRootPanel(panel);
		frame.show();

	}
}
