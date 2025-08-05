package com.hh.hhdb_admin.mgr.redisPubSub;

import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.bean.CloseBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * redis发布订阅
 */
public class RedisPubSubMgr extends AbsGuiMgr {
	public static final String PUB_TMP = "pub_tmp";
	public static final String SUB_TMP = "sub_tmp";
	public static final String PUBSUB_TMP = "pubsub_tmp";
	public static File tmpFile;		//临时文件存储主目录
	
	private Map<String, PubSubComp> map = new HashMap<>();
	
	@Override
	public void init(JsonObject jObj) {
	}

	@Override
	public CsMgrEnum getType() {
		return CsMgrEnum.REDISPUBSUB;
	}
	
	@Override
	public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
		String tabId;
		BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
		switch (csGuiBean.getOpType()) {
			case CLOSE:
				CloseBean closeBean = new CloseBean(bj.getJobj());
				PubSubComp comp = map.get(closeBean.getId());
				comp.close();
				map.remove(closeBean.getId());
				break;
			case PUBLISHER:
			case SUBSCRIBER:
				tabId = RandomUtil.getUUID();
				map.put(tabId,new PubSubComp(CsVarUtil.jdbcCloneWithSsh(),csGuiBean.getOpType() == BaseOpEnum.PUBLISHER));
				SharePaneObj obj = new SharePaneObj();
				obj.setId(tabId);
				obj.setTitle(LangMgr.lang(csGuiBean.getOpType() == BaseOpEnum.PUBLISHER ? Word4.PUBLISH_WINDOW : Word4.SUBSCRIBE_WINDOW));
				obj.setCsMgrEnum(CsMgrEnum.REDISPUBSUB);
				obj.setFixTab(false);
				obj.setObj(map.get(tabId));
				CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);
				break;
			default:
				break;
		}
		return null;
	}
	
	public static synchronized File prepareDir(String name) {
		File f = new File(tmpFile,name+"_"+System.currentTimeMillis());
		FileUtils.deleteQuietly(f);
		return f;
	}
}
