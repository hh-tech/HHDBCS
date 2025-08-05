package com.hh.hhdb_admin.mgr.index2;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;


/**
 * 索引的组件MGR
 *
 * @author yangxianhui
 */
public class IndexMgr extends AbsGuiMgr {
    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.INDEX;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        IndexBean csGuiBean = new IndexBean(bj.getJobj());
        String schemaName = csGuiBean.getSchema();
        String tableName = csGuiBean.getTableName();
        
        switch (csGuiBean.getOprType()) {
            case SHOW:
            	LastPanel indexPanel = getPanel(schemaName,tableName,csGuiBean.getRefreshNodeId());
            	HDialog dialog = new HDialog(CsVarUtil.parentFrame,800,indexPanel.getHeight()+40,false);
            	dialog.setRootPanel(indexPanel);
            	dialog.setStatusBar(new HBarPanel());
            	dialog.setWindowTitle(LangMgr.lang(Word2.CREATE) + LangMgr.lang(Word2.INDEX));
            	dialog.show();
                break;
            default:
                break;
        }
        return null;
    }
    
    private LastPanel getPanel(String schemaName,String tableName,String id) throws Exception {
    	DBTypeEnum type = CsVarUtil.getDbtype();
    	if(type == DBTypeEnum.mongodb) {
    		return new MongodbIndexPanel(schemaName,tableName) {
    			@Override
    			protected void inform() {
                    CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(id));
    			}
    		};
    	}else {
    		IndexPanel p= new IndexPanel() {
	    		@Override
	    		protected void inform() {
                    CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(id));
	    		}
	    	};
	    	p.init(tableName, schemaName);
	    	return p;
    	}
    	
    	
    }
}
