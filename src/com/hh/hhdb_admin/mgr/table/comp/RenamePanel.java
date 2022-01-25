package com.hh.hhdb_admin.mgr.table.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.table.TableComp;
import com.hh.hhdb_admin.mgr.table.common.TableUtil;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

/**
 * @author YuSai
 */
public class RenamePanel {

	public RenamePanel(String schema, String tableName, Connection conn) {
		HDialog dialog = new HDialog(StartUtil.parentFrame, 400, 130);
		dialog.setIconImage(IconFileUtil.getLogo());
		TextInput nameInput = new TextInput("newName", tableName);
		HBarLayout barLayout = new HBarLayout();
		barLayout.setAlign(AlignEnum.CENTER);
		HBarPanel barPanel = new HBarPanel(barLayout);
		HButton subitBtn = new HButton(TableComp.getLang("BTN_QD")) {
			@Override
			protected void onClick() {
				String newName = nameInput.getValue();
				if (StringUtils.isBlank(newName)) {
					PopPaneUtil.error(dialog.getWindow(), TableComp.getLang("INPUT_NEW_NAME"));
					return;
				}
				if (newName.trim().equals(tableName)) {
					PopPaneUtil.error(dialog.getWindow(), TableComp.getLang("NAME_EQUAL_ERROR"));
					return;
				}
				try {
					String sql = "";
					DBTypeEnum dbTypeEnum = DriverUtil.getDbType(conn);
					switch (dbTypeEnum) {
						case hhdb:
						case pgsql:
						case oracle:
						case dm:
							sql = "alter table \"" + schema + "\".\"" + tableName + "\" rename to " + newName;
							break;
						case mysql:
							sql = "alter table `" + schema + "`.`" + tableName + "` rename to " + newName;
							break;
						case db2:
							sql = String.format("RENAME TABLE \"%s\".\"%s\" TO %s", schema, tableName, newName);
							break;
						case sqlserver:
							sql = String.format("EXEC sys.sp_rename N'%s.%s' , N'%s', 'OBJECT'", schema, tableName, newName);
							break;
						default:
					}
					SqlExeUtil.executeUpdate(conn, sql);
					dialog.dispose();
					StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
							.add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.TABLE_GROUP.name())
							.add(StartUtil.PARAM_SCHEMA, schema));
					PopPaneUtil.info(StartUtil.parentFrame.getWindow(), TableComp.getLang("updateSuccess"));
					//刷新树节点
				} catch (Exception e) {
					e.printStackTrace();
					PopPaneUtil.error(dialog.getWindow(), e);
				}
			}
		};
		subitBtn.setIcon(TableUtil.getIcon("submit"));
		HButton cancelBtn = new HButton(TableComp.getLang("BTN_QX")) {
			@Override
			protected void onClick() {
				dialog.dispose();
			}
		};
		cancelBtn.setIcon(TableUtil.getIcon("cancel"));
		barPanel.add(subitBtn, cancelBtn);
		HGridPanel gridPanel = new HGridPanel(new HGridLayout(GridSplitEnum.C12));
		gridPanel.setComp(1, nameInput);

		LastPanel lastPanel = new LastPanel();
		lastPanel.set(gridPanel.getComp());
		lastPanel.setFoot(barPanel.getComp());
		HPanel panel = new HPanel();
		panel.add(new HeightComp(5));
		panel.setLastPanel(lastPanel);
		dialog.setRootPanel(panel);
		dialog.setWindowTitle(TableComp.getLang("rename"));
		dialog.show();
	}

}
