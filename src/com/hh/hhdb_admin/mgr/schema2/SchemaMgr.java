package com.hh.hhdb_admin.mgr.schema2;

import com.hh.frame.db_sql.conn.SchemaConnUtil;
import com.hh.frame.db_sql.info.SqlSchemaInfo;
import com.hh.frame.db_sql.info.SqlSchemaUpdateBean;
import com.hh.frame.db_sql.info.SqlSelSchemaInfoBean;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;



public class SchemaMgr extends AbsGuiMgr {
    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.SCHEMA;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        SchemaBean bean = new SchemaBean(bj.getJobj());
        switch (bean.getOprType()) {
            case ADD:
            	SchemaPanel schemaPanel = getSchemaPanel(bean.getRefreshNodeId());
            	schemaPanel.init(CsVarUtil.jdbc.getUser());
            	showDialog(LangMgr.lang( Word2.CREATE) + LangMgr.lang(Word2.SCHEMA),schemaPanel);
                break;
            case UPDATE:
            	schemaPanel = getSchemaPanel(bean.getRefreshNodeId());
            	SqlSelSchemaInfoBean b = new SqlSelSchemaInfoBean(SqlSchemaInfo.class);
            	b.setSchemaName(bean.getSchema());
            	SqlSchemaUpdateBean up = SchemaConnUtil.selectSchema(CsVarUtil.getDbtype(),CsVarUtil.jdbc.getCurrentVersion(), CsVarUtil.conn,b);
            	schemaPanel.init(up);
            	showDialog(LangMgr.lang( Word2.UPDATE) + LangMgr.lang(Word2.SCHEMA),schemaPanel);
                break;
            default:
        }
        return null;
    }
    
    
    private void showDialog(String title,SchemaPanel panel) {
    	HDialog dialog = new HDialog(CsVarUtil.parentFrame,600,400,false);
    	dialog.setRootPanel(panel);
    	dialog.setStatusBar(new HBarPanel());
    	dialog.setWindowTitle(title);
    	dialog.show();
    }
    
    private SchemaPanel getSchemaPanel(String id) {
    	return new SchemaPanel() {
    		@Override
    		protected void inform() {
                CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(id));
    		}
    	};
    	
    }
}
