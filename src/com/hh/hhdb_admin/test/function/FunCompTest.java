package com.hh.hhdb_admin.test.function;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lang.LangMgr2;
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

public class FunCompTest {

	public static void main(String[] args) throws Exception {
		IconFileUtil.setIconBaseDir(new File("etc/icon/"));
		try {
            LangMgr2.loadMerge(FunctionMgr.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
