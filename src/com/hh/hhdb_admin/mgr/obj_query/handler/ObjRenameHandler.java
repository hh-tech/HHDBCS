package com.hh.hhdb_admin.mgr.obj_query.handler;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
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
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.obj_query.ObjQueryComp;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;
import com.hh.hhdb_admin.mgr.tree.handler.action.RenameHandler;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

public class ObjRenameHandler extends RenameHandler {

    private final HDialog parent;

    private final ObjQueryComp queryComp;

    private HDialog dialog;

    public ObjRenameHandler(HDialog parent, ObjQueryComp queryComp) {
       this.parent = parent;
       this.queryComp = queryComp;
    }

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        dialog = new HDialog(parent, 400, 130);
        dialog.setIconImage(IconFileUtil.getLogo());
        TextInput nameInput = new TextInput("newName", treeNode.getName());
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.CENTER);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton subitBtn = new HButton(ObjQueryComp.getLang("BTN_QD")) {
            @Override
            protected void onClick() {
                String newName = nameInput.getValue();
                if (StringUtils.isBlank(newName)) {
                    PopPaneUtil.error(dialog.getWindow(), ObjQueryComp.getLang("INPUT_NEW_NAME"));
                    return;
                }
                if (newName.trim().equals(tableName)) {
                    PopPaneUtil.error(dialog.getWindow(), ObjQueryComp.getLang("NAME_EQUAL_ERROR"));
                    return;
                }
                Connection conn = null;
                try {
                    conn = ConnUtil.getConn(loginBean.getJdbc());
                    String schema = getSchemaName();
                    DBTypeEnum dbTypeEnum = DriverUtil.getDbType(conn);
                    if (TreeMrType.TABLE.name().equals(treeNode.getType())) {
                        renameTable(conn, dbTypeEnum, schema, treeNode.getName(), nameInput.getValue());
                    } else {
                        renameSeq(conn, dbTypeEnum, schema, treeNode.getName(), nameInput.getValue());
                    }
                    dialog.dispose();
                    queryComp.search();
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(dialog.getWindow(), e);
                } finally {
                    if (conn != null) {
                        ConnUtil.close(conn);
                    }
                }
            }
        };
        subitBtn.setIcon(ObjQueryComp.getIcon("submit"));
        HButton cancelBtn = new HButton(ObjQueryComp.getLang("BTN_QX")) {
            @Override
            protected void onClick() {
                dialog.dispose();
            }
        };
        cancelBtn.setIcon(ObjQueryComp.getIcon("cancel"));
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
        dialog.setWindowTitle(ObjQueryComp.getLang("rename"));
        dialog.show();
    }

    private void renameTable(Connection conn, DBTypeEnum dbTypeEnum, String schema, String oldName, String newName) throws Exception {
        String sql = "";
        switch (dbTypeEnum) {
            case hhdb:
            case pgsql:
            case oracle:
            case dm:
                sql = "alter table \"" + schema + "\".\"" + oldName + "\" rename to " + newName;
                break;
            case mysql:
                sql = "alter table `" + schema + "`.`" + oldName + "` rename to " + newName;
                break;
            case db2:
                sql = String.format("RENAME TABLE \"%s\".\"%s\" TO %s", schema, oldName, newName);
                break;
            case sqlserver:
                sql = String.format("EXEC sys.sp_rename N'%s.%s' , N'%s', 'OBJECT'", schema, tableName, newName);
                break;
            default:
        }
        SqlExeUtil.executeUpdate(conn, sql);
        StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
                .add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.TABLE_GROUP.name())
                .add(StartUtil.PARAM_SCHEMA, schema));
    }

    private void renameSeq(Connection conn, DBTypeEnum dbTypeEnum, String schema, String oldName, String newName) throws Exception {
        String sql = "";
        switch (dbTypeEnum) {
            case hhdb:
            case pgsql:
                sql = String.format("alter sequence \"" + schema + "\".\"" + oldName + "\" rename to \"%s\"", newName);
                break;
            case oracle:
                sql = String.format("RENAME \"" + oldName + "\"  TO %s", newName);
                break;
            case sqlserver:
                sql = String.format("exec sp_rename '\"" + schema + "\".\"" + oldName + "\"','%s'", newName);
                break;
            default:
        }
        SqlExeUtil.executeUpdate(conn, sql);
        StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
                .add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.SEQUENCE_GROUP.name())
                .add(StartUtil.PARAM_SCHEMA, schema));
    }

}
