package com.hh.hhdb_admin.test.column;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.column.ColumnMgr;
import com.hh.hhdb_admin.mgr.login.LoginUtil;
import com.hh.hhdb_admin.test.AbsMainTestComp;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

/**
 * @author yangxianhui
 */
public class ColumnTestComp extends AbsMainTestComp {
	@Override
	public void init() {
		String tableName = (String) JOptionPane.showInputDialog(null, "", "输入表名",
				JOptionPane.PLAIN_MESSAGE, null, null, "");
		if (StringUtils.isBlank(tableName)) {
			System.exit(0);
		}
		JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
		if (jdbcBean != null) {
			DBTypeEnum dbTypeEnum = DriverUtil.getDbType(jdbcBean);
			if (dbTypeEnum != null) {
				String name = StringUtils.isEmpty(jdbcBean.getSchema()) ? jdbcBean.getUser() : jdbcBean.getSchema();
				String schema = LoginUtil.getRealName(name, dbTypeEnum.name());
				jdbcBean.setUser(LoginUtil.getRealName(jdbcBean.getUser(), dbTypeEnum.name()));
				jdbcBean.setSchema(schema);

				JsonObject json = new JsonObject();
				json.add("schema", schema);
				tableName = LoginUtil.getRealName(tableName, dbTypeEnum.name());
				json.add("table", tableName);
				HBarLayout barLayout = new HBarLayout();
				barLayout.setAlign(AlignEnum.LEFT);
				HBarPanel barPanel = new HBarPanel(barLayout);
				HButton addBtn = new HButton("创建列") {
					@Override
					public void onClick() {
						json.add("__CMD", ColumnMgr.CMD_SHOW_ADD_TABLE_COLUMN);
						StartUtil.eng.doPush(CsMgrEnum.COLUMN, json);
					}
				};
				barPanel.add(addBtn);
				HButton updBtn = new HButton("设计列") {
					@Override
					public void onClick() {
						String colName = (String) JOptionPane.showInputDialog(null, "", "输入列名",
								JOptionPane.PLAIN_MESSAGE, null, null, "");
						colName = LoginUtil.getRealName(colName, dbTypeEnum.name());
						json.add("__CMD", ColumnMgr.CMD_SHOW_UPDATE_TABLE_COLUMN);
						json.add(ColumnMgr.PARAM_COLUMN_NAME, colName);
						StartUtil.eng.doPush(CsMgrEnum.COLUMN, json);
					}
				};
				barPanel.add(updBtn);
				tFrame.setToolBar(barPanel);
			}
		}
	}
}
