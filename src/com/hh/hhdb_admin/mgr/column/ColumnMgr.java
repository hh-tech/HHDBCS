package com.hh.hhdb_admin.mgr.column;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.modifyinfo.ModifyColumnPanel;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;

public class ColumnMgr extends AbsGuiMgr {
    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.COLUMN;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        ColumnBean csGuiBean =(ColumnBean) bj;
        String schemaName = csGuiBean.getSchema();
        String tableName = csGuiBean.getTableName();
        String columnName = csGuiBean.getColumnName();
        final String refreshNodeId=csGuiBean.getRefreshNodeId();
        ColumnComp columnComp = new ColumnComp(CsVarUtil.conn, CsVarUtil.getDbtype(), schemaName, tableName) {
            @Override
            public void refreshTree() {
//                TreeBean bean = new TreeBean(BaseOpEnum.REFRESH);
//                bean.setNodeType(TreeMrType.COLUMN_GROUP);
//                bean.setSchema(schemaName);
//                bean.setTableName(tableName);
                CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(refreshNodeId));
            }
        };
        switch (csGuiBean.getColOprType()) {
            case ADD:
                columnComp.show(false, "");
                break;
            case UPDATE: {
                columnComp.show(true, csGuiBean.getColumnName());
                break;
            }
            case RENAME: {
                columnComp.renameColumn(tableName, csGuiBean.getColumnName());
                break;
            }
            case MODIFY:
                ModifyColumnPanel modifyColumnPanel = new ModifyColumnPanel(schemaName, tableName, columnName);
                HDialog hDialog = modifyColumnPanel.createDialog(LangMgr.lang(Word2.MODIFY));
                modifyColumnPanel.setAfterSave(() -> {
                    hDialog.dispose();
                    CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(refreshNodeId));
                });
                hDialog.show();
                break;
            default:
                break;
        }
        return null;
    }
}
