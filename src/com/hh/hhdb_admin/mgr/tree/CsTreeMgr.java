package com.hh.hhdb_admin.mgr.tree;

import com.hh.frame.common.base.PageBean;
import com.hh.frame.create_dbobj.treeMr.base.MongoDocGroupNode;
import com.hh.frame.create_dbobj.treeMr.base.RedisObjGroupNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.view.tree2.LeafBean;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;
import com.hh.hhdb_admin.mgr.page_filter.PageFilterBean;
import org.apache.commons.lang3.StringUtils;


public class CsTreeMgr extends AbsGuiMgr  {
    //筛选集合.（模式，节点类型，筛选关键词）
//    public static Map<String, Map<String, String>> filterData = new HashMap<>();
    //存储redisDB最大查询数量
//    public static Map<String, Integer> dataSize = new HashMap<>();

    private CsTreeComp treeComp;

    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.TREE;
    }

    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
    	if(doMongoAddDoc(bj))return null;
    	//对新的redis对象进行getPreLoadKeys预处理，然后进行刷新
    	if(bj instanceof RedisRefreshBean) {
    		RedisRefreshBean rBean=(RedisRefreshBean)bj;
    		RedisObjGroupNode redisNode=getRedisGroupNode(rBean);
    		redisNode.addPreloadKey(rBean.getKeyName());
    	}
    	
    	if(bj instanceof RedisRmPreLoadKeysBean) {
    		RedisRmPreLoadKeysBean rBean=(RedisRmPreLoadKeysBean)bj;
    		RedisObjGroupNode redisNode=getRedisGroupNode(rBean);
    		for(String key:rBean.getKeys()) {
    			redisNode.getPreLoadKeys().remove(key);
    		}
    	}
    	
    	if(bj instanceof RefreshNodeBean) {
    		RefreshNodeBean rBean=(RefreshNodeBean)bj;
    		if(rBean.isOnlyGui()) {
    			treeComp.getTree().reload(rBean.getRefreshNodeId());
        		return null;
    		}
    	}
    	
    	if(bj instanceof PageFilterBean) {
    		PageFilterBean pBean=(PageFilterBean)bj;
    		
    		PageBean page=new PageBean(pBean.getTotalNum(),pBean.getNumPerPage());
    		//先设置total page然后才能配置current page
    		page.setCurrentPage(pBean.getCurrentPage());
    		String nodeId=pBean.getRefreshNodeId();
    		CsFolderBean folder=(CsFolderBean)treeComp.getTree().get(nodeId);
    		folder.getMrNode().setFilter(pBean.getFilter());
    		folder.getMrNode().setPageBean(page);
    		treeComp.refreshNode(nodeId);
    		return null;
    	}
    	
    	if(bj instanceof BaseRefreshBean) {
    		BaseRefreshBean rBean=(BaseRefreshBean)bj;
    		treeComp.refreshNode(rBean.getRefreshNodeId());
    		return null;
    	}

    	
        BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
        switch (csGuiBean.getOpType()) {
            case INIT:
                SharePaneObj obj = new SharePaneObj();
                obj.setCsMgrEnum(CsMgrEnum.TREE);
                obj.setObj(treeComp = CsTreeComp.newTreeInstance());
                CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);
                break;
            case ADD:
                if (StringUtils.isNotBlank(csGuiBean.getNodeId())) {
                    CsFolderBean newNode = new CsFolderBean(csGuiBean.getName());
                    newNode.setFontType(CsTreeUtil.getFontType(new TreeMrNode(newNode.getName(),newNode.getType(),"")));
                    newNode.setMrNode(new TreeMrNode(csGuiBean.getName(),csGuiBean.getNodeType()));
                    treeComp.getTree().add(csGuiBean.getNodeId(), newNode);
                    treeComp.getTree().reload(csGuiBean.getNodeId());
                    treeComp.getTree().expandFolder(csGuiBean.getNodeId());
                }
                break;
            case DELETE:
                if (StringUtils.isNotBlank(csGuiBean.getNodeId())) {
                    LeafBean folder = treeComp.getTree().get(csGuiBean.getNodeId()).getFolder();
                    treeComp.getTree().remove(csGuiBean.getNodeId());
                    if (null != folder) {
                        treeComp.getTree().reload(folder.getId());
                        treeComp.getTree().expandFolder(folder.getId());
                    }
                }
                break;
            default:
                break;
        }
        return null;
    }
    
    private RedisObjGroupNode getRedisGroupNode(BaseRefreshBean b){
        CsFolderBean redisObjs=(CsFolderBean)treeComp.getTree().get(b.getRefreshNodeId());
		RedisObjGroupNode redisNode= (RedisObjGroupNode)redisObjs.getMrNode();
		return redisNode;
    }
    
    private boolean doMongoAddDoc(BaseJsonBean bj) {
    	if(bj instanceof AddMongoDocBean) {
    		AddMongoDocBean mBean=(AddMongoDocBean)bj;
    		CsFolderBean mgDocGroup=(CsFolderBean)treeComp.getTree().get(mBean.getRefreshNodeId());
    		MongoDocGroupNode mgNode=(MongoDocGroupNode)mgDocGroup.getMrNode();
    		mgNode.add(mBean.getDocIdObj());
    		return true;
    	}
    	return false;
    }
    
}
