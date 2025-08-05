package com.hh.hhdb_admin.mgr.table2;

import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.comp.BaseSqlPanel;
import com.hh.hhdb_admin.mgr.copy.CopyTableComp;
import com.hh.hhdb_admin.mgr.modifyinfo.ModifyTablePanel;
import com.hh.hhdb_admin.mgr.table2.comp.TableComp;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;

import java.sql.Connection;

public class TableMgr extends AbsGuiMgr {
    @Override
    public Enum<?> getType() {
        return CsMgrEnum.TABLE;
    }

    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        TableBean treeBean = new TableBean(bj.getJobj());
        String tableName = treeBean.getTableName();
        String schemaName = treeBean.getSchema();
        switch (treeBean.getOprType()) {
            case ADD:
                JdbcInfo jdbc = CsVarUtil.jdbcCloneWithSsh();
                jdbc.setSchema(schemaName);
                Connection conn = CsVarUtil.conn;
//            	if (CsVarUtil.getDbtype() == DBTypeEnum.mongodb) {
//                    MongodbDocAddUpdateComp comp = new MongodbDocAddUpdateComp(conn, schemaName, tableName, null) {
//                        @Override
//                        protected void refresh() {
//                            CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(treeBean.getRefreshNodeId()));
//                        }
//                    };
//                    comp.showDialog();
//                } else {
//                    TableComp panelCreate = new TableComp(conn, jdbc,schemaName){
//                        @Override
//                        protected void refresh() {
//                            CsVarUtil.eng2.doCall(CsMgrEnum.TREE, new RefreshNodeBean(treeBean.getRefreshNodeId()));
//                        }
//                    };
//                    panelCreate.showDialog();
//                }
                TableComp panelCreate = new TableComp(conn, jdbc,schemaName){
                    @Override
                    protected void refresh() {
                        CsVarUtil.eng2.doCall(CsMgrEnum.TREE, new RefreshNodeBean(treeBean.getRefreshNodeId()));
                    }
                };
                panelCreate.showDialog();
                break;
            case MODIFY:
                HDialog hDialog = new HDialog();
                tableName= SqlStrUtil.addQuote4DDL(tableName, CsVarUtil.getDbtype(), CsVarUtil.jdbc.getCurrentVersion());
                BaseSqlPanel sqlPanel = new ModifyTablePanel(schemaName, tableName);
                sqlPanel.setAfterSave(() -> {
                    hDialog.dispose();
                    CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(treeBean.getRefreshNodeId()));
                });
    
                hDialog.setStatusBar(new HBarPanel());
                hDialog.setWindowTitle(LangMgr.lang(Word2.MODIFY));
                hDialog.setIconImage(IconFileUtil.getLogo());
                hDialog.setRootPanel(sqlPanel);
                hDialog.show();
                break;
            case COPY:
                HDialog hDialog1 = new HDialog(650, 500);
                CopyTableComp copyTableComp = new CopyTableComp(CsVarUtil.jdbc,schemaName,tableName) {
                    @Override
                    public void refreshTree() {
                        hDialog1.dispose();
                        CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(treeBean.getRefreshNodeId()));
                    }
                };
    
                hDialog1.setStatusBar(new HBarPanel());
                hDialog1.setWindowTitle(LangMgr.lang(Word2.COPY));
                hDialog1.setIconImage(IconFileUtil.getLogo());
                hDialog1.setRootPanel(copyTableComp);
                hDialog1.show();
                break;
            default:
            	throw new Exception("unknown error");
        }
        return null;
    }
}
