package com.hh.hhdb_admin.mgr.table_open;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.db_sql.dm.DmObjectsSqlUtil;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.CloseBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;

import com.hh.hhdb_admin.mgr.view2.ViewBean;
import com.hh.hhdb_admin.mgr.view2.ViewBean.OprType;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
@Deprecated
public class TableOpenMgr extends AbsGuiMgr {
	private Map<String, ModifyTabDataComp> map = new HashMap<>();
	public static final String OPEN_TMP = "open_tmp";
	public static File tmpFile;
	public static int maxPoolSize;

	public static ExecutorService threadPool;

	@Override
	public void init(JsonObject jObj) {
		initPool();
	}

	@Override
	public CsMgrEnum getType() {
		return CsMgrEnum.TABLE_OPEN;
	}
	
	@Override
	public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
		String openId = "";
		if (!tmpFile.exists()) {
			tmpFile.mkdirs();
		}
		
		if (bj instanceof CloseBean) {
			CloseBean closeBean = new CloseBean(bj.getJobj());
			ModifyTabDataComp tabDataComp = map.get(closeBean.getId());
			tabDataComp.close();
			map.remove(closeBean.getId());
		} else {
			TableOpenBean csGuiBean = new TableOpenBean(bj.getJobj());
			JdbcBean jdbcBean = CsVarUtil.jdbcCloneWithSsh();
			switch (csGuiBean.getOprType()) {
				case SHOW:
					String schemaName = csGuiBean.getSchema();
					jdbcBean.setSchema(schemaName);
					String table = csGuiBean.getTableName();
					DBTypeEnum dbtype=CsVarUtil.getDbtype();
//					if (dbtype == DBTypeEnum.mongodb) {
//						MongodbDocAddUpdateComp comp = new MongodbDocAddUpdateComp(CsVarUtil.newConn(jdbcBean),schemaName,table,csGuiBean.getName()) {
//							@Override
//							protected void refresh() {
//								CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(csGuiBean.getRefreshNodeId()));
//							}
//						};
//						comp.showDialog();
//					} else {
//						if (dbtype == DBTypeEnum.dm) {
//							boolean hasrowid= DmObjectsSqlUtil.hasRowIdTab(CsVarUtil.conn, schemaName, table);
//							if(!hasrowid) {
//								ViewBean viewBean=new ViewBean(OprType.SHOW,null);
//								viewBean.setSchema(schemaName);
//								viewBean.setName(table);
//								 return CsVarUtil.eng2.doCall(CsMgrEnum.VIEW, viewBean);
//								
//							}
//							
//						}
//						ModifyTabDataComp tablePanel = null;
//						//判断是否已打开
//						boolean bool = true;
//						for (String string : map.keySet()) {
//							tablePanel = map.get(string);
//							if (table.equals(tablePanel.getTabName()) && schemaName.equals(tablePanel.getSchemaName())) {
//								openId = string;
//								bool = false;
//								break;
//							}
//						}
//						if (bool) { //打开新的
//							tablePanel = new ModifyTabDataComp(jdbcBean, table, tmpFile);
////							tablePanel.init(jdbcBean, table, tmpFile);
//							tablePanel.refreshTab();
//							map.put(openId = RandomUtil.getUUID(), tablePanel);
//						}
//						SharePaneObj obj = new SharePaneObj();
//						obj.setId(openId);
//						obj.setTitle(CsVarUtil.getDbtype() == DBTypeEnum.sqlite ? table : (schemaName + "." + table));
//						obj.setCsMgrEnum(CsMgrEnum.TABLE_OPEN);
//						obj.setFixTab(false);
//						obj.setObj(tablePanel);
//						CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);
//					}
					if (dbtype == DBTypeEnum.dm) {
						boolean hasrowid= DmObjectsSqlUtil.hasRowIdTab(CsVarUtil.conn, schemaName, table);
						if(!hasrowid) {
							ViewBean viewBean=new ViewBean(OprType.SHOW,null);
							viewBean.setSchema(schemaName);
							viewBean.setName(table);
							 return CsVarUtil.eng2.doCall(CsMgrEnum.VIEW, viewBean);
							
						}
						
					}
					ModifyTabDataComp tablePanel = null;
					//判断是否已打开
					boolean bool = true;
					for (String string : map.keySet()) {
						tablePanel = map.get(string);
						if (table.equals(tablePanel.getTabName()) && schemaName.equals(tablePanel.getSchemaName())) {
							openId = string;
							bool = false;
							break;
						}
					}
					if (bool) { //打开新的
						tablePanel = new ModifyTabDataComp(jdbcBean, table, tmpFile);
//						tablePanel.init(jdbcBean, table, tmpFile);
						tablePanel.refreshTab();
						map.put(openId = RandomUtil.getUUID(), tablePanel);
					}
					SharePaneObj obj = new SharePaneObj();
					obj.setId(openId);
					obj.setTitle(CsVarUtil.getDbtype() == DBTypeEnum.sqlite ? table : (schemaName + "." + table));
					obj.setCsMgrEnum(CsMgrEnum.TABLE_OPEN);
					obj.setFixTab(false);
					obj.setObj(tablePanel);
					CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);
					break;
				default:
					break;
			}
		}
		return null;
	}

	public static void initPool() {
		int n = Runtime.getRuntime().availableProcessors();
		maxPoolSize = (int) Math.ceil(n * 2 * ((1 - 0.9) * 100));
		ThreadFactory namedThreadFactory = new BasicThreadFactory.Builder().namingPattern("table-open-pool-%d").daemon(true).build();
		threadPool = new ThreadPoolExecutor(n, maxPoolSize,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>(1024), namedThreadFactory,
				new ThreadPoolExecutor.DiscardOldestPolicy());
	}
}
