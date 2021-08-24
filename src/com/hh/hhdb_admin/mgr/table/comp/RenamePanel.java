package com.hh.hhdb_admin.mgr.table.comp;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.table.TableComp;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.sql.Connection;

/**
 * @author YuSai
 */
public class RenamePanel {

	public RenamePanel(String schema, String tableName, Connection conn) {
		Object o = JOptionPane.showInputDialog(StartUtil.parentFrame.getWindow(),
				TableComp.getLang("enterTableName"), TableComp.getLang("rename"),
				JOptionPane.PLAIN_MESSAGE, null, null, tableName);
		if (o == null) {
			return;
		}
		String newName = o.toString();
		try {
			if (StringUtils.isNotBlank(newName)) {
				String sql = "";
				DBTypeEnum dbTypeEnum = DriverUtil.getDbType(conn);
				switch (dbTypeEnum) {
					case hhdb:
					case pgsql:
					case oracle:
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
				PopPaneUtil.info(StartUtil.parentFrame.getWindow(), TableComp.getLang("updateSuccess"));
				//刷新树节点
				StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
						.add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.TABLE_GROUP.name())
						.add(StartUtil.PARAM_SCHEMA, schema));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			PopPaneUtil.error(StartUtil.parentFrame.getWindow(), ex.getMessage());
		}
	}

}
