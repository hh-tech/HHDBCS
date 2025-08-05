package com.hh.hhdb_admin.mgr.multi_view;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.mongo.obj.MongoColl;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;
import com.hh.hhdb_admin.mgr.multi_view.comp.MongodbViewComp;

import java.sql.Connection;
import java.util.List;


public class MultiViewMgr extends AbsGuiMgr {
    @Override
    public void init(JsonObject conf) {
        super.init(conf);
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.MULTI_VIEW;
    }

    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        MultiViewBean multiViewBean = new MultiViewBean(bj.getJobj());
        String schemaName = multiViewBean.getSchema();
        String tableName = multiViewBean.getTableName();
        List<String> idList = multiViewBean.getIdList();
        Connection conn = CsVarUtil.conn;
        //查看mongodb文档
        MongoColl mongoColl = new MongoColl(conn, schemaName);
        MongodbViewComp mongodbViewComp = new MongodbViewComp(mongoColl, tableName, idList);
        
		SharePaneObj obj = new SharePaneObj();
		String id = RandomUtil.getUUID();
		obj.setId(id); //TODO 如果要保证每个集合只显示一个tab，则需要记录id-tab映射关系
		obj.setTitle(schemaName + "." + tableName);
		obj.setCsMgrEnum(CsMgrEnum.MONGO_MGR);
		obj.setFixTab(false);
		obj.setObj(mongodbViewComp);
		CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);

        return null;
    }

}
