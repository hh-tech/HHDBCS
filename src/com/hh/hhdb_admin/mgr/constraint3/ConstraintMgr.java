package com.hh.hhdb_admin.mgr.constraint3;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;

public class ConstraintMgr extends AbsGuiMgr {
	HDialog dialog;
    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.CONSTRAINT;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
		dialog = new HDialog(CsVarUtil.parentFrame,800,600,false);
		dialog.setStatusBar(new HBarPanel());
        ConstraintBean bean = (ConstraintBean)bj;
//        TreeMrType constType = bean.getConstType();
        String tableName = bean.getTableName();
        String schema = bean.getSchema();
        
        switch (bean.getOprType()) {
		case PK:
			PrimaryKeyPanel panel = new PrimaryKeyPanel(schema,tableName) {
				@Override
				protected void inform() {
					refreshTree(bean.getRefreshNodeId());
					dialog.dispose();
				}
			};
			showDialog(LangMgr.lang(Word2.CREATE) + LangMgr.lang(Word2.PRIMARY_KEY),panel);
			break;
		case UK:
			UniqueKeyPanel unink = new UniqueKeyPanel(schema,tableName) {
				@Override
				protected void inform() {
					refreshTree(bean.getRefreshNodeId());
					dialog.dispose();
				}
			};
			showDialog(LangMgr.lang(Word2.CREATE) + LangMgr.lang(Word3.UNIQUE_KEY),unink);
			break;
		case CK:
			CheckKeyPanel checkKeyPanel = new CheckKeyPanel(schema, tableName) {
				@Override
				protected void inform() {
					refreshTree(bean.getRefreshNodeId());
					dialog.dispose();
				}
			};
			showDialog(LangMgr.lang(Word2.CREATE) + LangMgr.lang(Word3.CHECK_KEY), checkKeyPanel);
			break;
		case FK:
			ForeignKeyPanel foreignKeyPanel = new ForeignKeyPanel(schema, tableName) {
				@Override
				protected void inform() {
					refreshTree(bean.getRefreshNodeId());
					dialog.dispose();
				}
			};
			showDialog(LangMgr.lang(Word2.CREATE) + LangMgr.lang(Word2.FOREIGN_KEY), foreignKeyPanel);
			break;
		default:
			break;
		}


        return null;
    }
    
    private void showDialog(String title,LastPanel panel) {
    	dialog.setRootPanel(panel);
    	dialog.setWindowTitle(title);
    	dialog.show();
    }
    
    private void refreshTree(String id) {
		CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(id));
    }
}
