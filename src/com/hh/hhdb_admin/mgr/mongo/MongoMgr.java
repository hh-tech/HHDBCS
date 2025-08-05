package com.hh.hhdb_admin.mgr.mongo;


import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.create_dbobj.treeMr.base.MongoDocGroupNode;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.mongo.obj.MongoColl;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.engine2.GuiShareObjInf;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;

public class MongoMgr extends AbsGuiMgr implements GuiShareObjInf{
	private MongoDocFilterDlg filterDlg=null;
	@Override
	public CsMgrEnum getType() {
		return CsMgrEnum.MONGO_MGR;
	}

	@Override
	public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
		if(bj instanceof MongoDocAddUpdateBean) {
			MongoDocAddUpdateBean b=(MongoDocAddUpdateBean)bj;
            MongodbDocAddUpdateComp comp = new MongodbDocAddUpdateComp(CsVarUtil.conn, b.getDbName(), b.getCollName(), b.getDocId(),b.getRefreshNodeId()) {
                @Override
                protected void refresh(boolean isAdded) {
                	if(isAdded) {
                		CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(b.getRefreshNodeId()));
                	}
                }
            };
            comp.showDialog();
		}else if(bj instanceof MongoDocOpenBean) {
			MongoDocOpenBean b = (MongoDocOpenBean)bj;
	        MongoColl mongoColl = new MongoColl(CsVarUtil.conn, b.getDbName());

	        MongodbOpenCollComp mongodbOpenCollComp = new MongodbOpenCollComp(mongoColl, b.getCollName());
			SharePaneObj obj = new SharePaneObj();
			String id = RandomUtil.getUUID();
			obj.setId(id);
			obj.setTitle(b.getDbName() + "." + b.getCollName());
			obj.setCsMgrEnum(CsMgrEnum.MONGO_MGR);
			obj.setFixTab(false);
			obj.setObj(mongodbOpenCollComp);
			CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);
		}
		return null;
	}

	@Override
	public BaseJsonBean shareObj(Object obj) throws Exception {
		MongoDocGroupNode objNode=(MongoDocGroupNode)obj;
		if(filterDlg==null) {
			filterDlg=new MongoDocFilterDlg();
		}
		filterDlg.show(objNode);
		return null;
	}

}
