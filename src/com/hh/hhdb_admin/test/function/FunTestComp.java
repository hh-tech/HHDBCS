package com.hh.hhdb_admin.test.function;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

import javax.swing.*;

public class FunTestComp extends AbsMainTestComp{

	@Override
	public void init() {
		HBarLayout l = new HBarLayout();
		l.setAlign(AlignEnum.LEFT);
		HBarPanel toolBar = new HBarPanel(l);
		JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
		DBTypeEnum dbTypeEnum = DriverUtil.getDbType(jdbcBean);
		
		HButton showBtn=new HButton("添加函数") {
			@Override
			public void onClick() {
				try {
					StartUtil.eng.doPush(CsMgrEnum.FUNCTION, GuiJsonUtil.toJsonCmd(FunctionMgr.CMD_ADD_FUNCTION)
							.add(StartUtil.PARAM_SCHEMA, jdbcBean.getSchema()).add(FunctionMgr.TYPE, TreeMrType.FUNCTION.name()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		toolBar.add(showBtn);

		HButton editBtn=new HButton("修改函数") {
			@Override
			public void onClick() {
				try {
					String sql = null;
					if (dbTypeEnum == DBTypeEnum.hhdb || dbTypeEnum == DBTypeEnum.pgsql) {
						sql = JOptionPane.showInputDialog(null,"请输入函数名称与id（,分隔）：","");
					}else {
						sql = JOptionPane.showInputDialog(null,"请输入函数名称：","");
					}
					if(sql==null) return;
					if (sql.contains(",")) {
						StartUtil.eng.doPush(CsMgrEnum.FUNCTION, GuiJsonUtil.toJsonCmd(FunctionMgr.CMD_EDIT_FUNCTION)
								.add(StartUtil.PARAM_SCHEMA, jdbcBean.getSchema()).add(FunctionMgr.PARAM_FUNC_NAME, sql.split(",")[0])
								.add(FunctionMgr.PARAM_FUNC_ID, sql.split(",")[1]).add(FunctionMgr.TYPE, TreeMrType.FUNCTION.name()));
					}else {
						StartUtil.eng.doPush(CsMgrEnum.FUNCTION, GuiJsonUtil.toJsonCmd(FunctionMgr.CMD_EDIT_FUNCTION)
								.add(StartUtil.PARAM_SCHEMA, jdbcBean.getSchema()).add(FunctionMgr.PARAM_FUNC_NAME, sql.split(",")[0])
								.add(FunctionMgr.PARAM_FUNC_ID, "").add(FunctionMgr.TYPE, TreeMrType.FUNCTION.name()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		toolBar.add(editBtn);

		HButton delBtn=new HButton("运行函数") {
			@Override
			public void onClick() {
				try {
					StartUtil.eng.doPush(CsMgrEnum.FUNCTION, GuiJsonUtil.toJsonCmd(FunctionMgr.RUN_FUNCTION)
							.add(StartUtil.PARAM_SCHEMA, "QWE").add(FunctionMgr.PARAM_FUNC_NAME, "TST_1").add(FunctionMgr.PARAM_FUNC_ID, "")
							.add(FunctionMgr.PARAM_PACKNAME, "PKG_DBGD").add(FunctionMgr.TYPE, TreeMrType.FUNCTION.name()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		toolBar.add(delBtn);

		HButton debugBtn=new HButton("函数调试") {
			@Override
			public void onClick() {
				try {
					StartUtil.eng.doPush(CsMgrEnum.FUNCTION, GuiJsonUtil.toJsonCmd(FunctionMgr.CMD_DEBUG_FUNCTION)
							.add(StartUtil.PARAM_SCHEMA, "QWE").add(FunctionMgr.PARAM_FUNC_NAME, "hstest2").add(FunctionMgr.PARAM_FUNC_ID, "")
							.add(FunctionMgr.TYPE, TreeMrType.FUNCTION.name()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		toolBar.add(debugBtn);
		
		HButton debugSqlBtn=new HButton("oracle代码块调试") {
			@Override
			public void onClick() {
				try {
					StartUtil.eng.doPush(CsMgrEnum.FUNCTION, GuiJsonUtil.toJsonCmd(FunctionMgr.DEBUG));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		toolBar.add(debugSqlBtn);

		tFrame.setToolBar(toolBar);
	}
}
