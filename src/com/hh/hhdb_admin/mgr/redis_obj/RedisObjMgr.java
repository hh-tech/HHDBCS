package com.hh.hhdb_admin.mgr.redis_obj;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;

/**
 * redis数据库对象操作
 */
public class RedisObjMgr extends AbsGuiMgr {

	@Override
	public void init(JsonObject jObj) {
	}

	@Override
	public CsMgrEnum getType() {
		return CsMgrEnum.REDIS_OBJ;
	}
	
	@Override
	public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
		RedisObjBean csGuiBean = new RedisObjBean(bj.getJobj());
		String schema = csGuiBean.getSchema();
		String nodeId = csGuiBean.getRefreshNodeId();
		switch (csGuiBean.getOprType()) {
			case ADD:
				new AddUpdRedisObjComp(schema,nodeId).showDialog();
				break;
			case UPDATE:
				String key = csGuiBean.getName();
				new AddUpdRedisObjComp(schema,nodeId).showDialog(key);
				break;
			default:
				break;
		}
		return null;
	}
}
